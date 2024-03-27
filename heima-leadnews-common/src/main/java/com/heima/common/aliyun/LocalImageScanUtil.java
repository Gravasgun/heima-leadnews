package com.heima.common.aliyun;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.aliyun.green20220302.Client;
import com.aliyun.green20220302.models.*;
import com.aliyun.oss.OSS;
import com.aliyun.oss.OSSClientBuilder;
import com.aliyun.oss.model.PutObjectRequest;
import com.aliyun.teaopenapi.models.Config;
import com.aliyun.teautil.models.RuntimeOptions;
import com.heima.file.service.FileStorageService;
import lombok.Getter;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Getter
@Setter
@Component
@ConfigurationProperties(prefix = "aliyun")
public class LocalImageScanUtil {
    private String accessKeyId;
    private String secret;
    private String scenes;
    //服务是否部署在vpc上
    public static boolean isVPC = false;

    //文件上传token endpoint->token
    public static Map<String, DescribeUploadTokenResponseBody.DescribeUploadTokenResponseBodyData> tokenMap = new HashMap<>();

    //上传文件请求客户端
    public static OSS ossClient = null;
    @Autowired
    private FileStorageService fileStorageService;


    /**
     * 创建请求客户端
     *
     * @param accessKeyId
     * @param accessKeySecret
     * @param endpoint
     * @return
     * @throws Exception
     */
    public static Client createClient(String accessKeyId, String accessKeySecret, String endpoint) throws Exception {
        Config config = new Config();
        config.setAccessKeyId(accessKeyId);
        config.setAccessKeySecret(accessKeySecret);
        // 接入区域和地址请根据实际情况修改
        config.setEndpoint(endpoint);
        return new Client(config);
    }

    /**
     * 创建上传文件请求客户端
     *
     * @param tokenData
     * @param isVPC
     */
    public static void getOssClient(DescribeUploadTokenResponseBody.DescribeUploadTokenResponseBodyData tokenData, boolean isVPC) {
        //注意，此处实例化的client请尽可能重复使用，避免重复建立连接，提升检测性能。
        if (isVPC) {
            ossClient = new OSSClientBuilder().build(tokenData.ossInternalEndPoint, tokenData.getAccessKeyId(), tokenData.getAccessKeySecret(), tokenData.getSecurityToken());
        } else {
            ossClient = new OSSClientBuilder().build(tokenData.ossInternetEndPoint, tokenData.getAccessKeyId(), tokenData.getAccessKeySecret(), tokenData.getSecurityToken());
        }
    }

    /**
     * 上传文件
     *
     * @param filePath
     * @param tokenData
     * @return
     */
    public String uploadFile(String filePath, DescribeUploadTokenResponseBody.DescribeUploadTokenResponseBodyData tokenData, String imagePath) {
        //从minio下载文件
        byte[] bytes = fileStorageService.downLoadFile(imagePath);
        //转换为inputStream
        InputStream inputStream = new ByteArrayInputStream(bytes);
        String[] split = filePath.split("\\.");
        String objectName;
        if (split.length > 1) {
            objectName = tokenData.getFileNamePrefix() + UUID.randomUUID() + "." + split[split.length - 1];
        } else {
            objectName = tokenData.getFileNamePrefix() + UUID.randomUUID();
        }
        PutObjectRequest putObjectRequest = new PutObjectRequest(tokenData.getBucketName(), objectName, inputStream);
        ossClient.putObject(putObjectRequest);
        return objectName;
    }

    public ImageModerationResponse invokeFunction(String accessKeyId, String accessKeySecret, String endpoint, String imagePath) throws Exception {
        //注意，此处实例化的client请尽可能重复使用，避免重复建立连接，提升检测性能。
        Client client = createClient(accessKeyId, accessKeySecret, endpoint);
        RuntimeOptions runtime = new RuntimeOptions();
        //本地文件的完整路径，例如D:\localPath\exampleFile.png。
        String filePath = imagePath;
        String bucketName = "localImage";
        DescribeUploadTokenResponseBody.DescribeUploadTokenResponseBodyData uploadToken = tokenMap.get(endpoint);
        //获取文件上传token
        if (uploadToken == null || uploadToken.expiration <= System.currentTimeMillis() / 1000) {
            DescribeUploadTokenResponse tokenResponse = client.describeUploadToken();
            uploadToken = tokenResponse.getBody().getData();
            bucketName = uploadToken.getBucketName();
        }
        //上传文件请求客户端
        getOssClient(uploadToken, isVPC);
        //上传文件
        String objectName = uploadFile(filePath, uploadToken, imagePath);
        // 检测参数构造。
        Map<String, String> serviceParameters = new HashMap<>();
        //文件上传信息
        serviceParameters.put("ossBucketName", bucketName);
        serviceParameters.put("ossObjectName", objectName);
        serviceParameters.put("dataId", UUID.randomUUID().toString());
        ImageModerationRequest request = new ImageModerationRequest();
        // 图片检测service：内容安全控制台图片增强版规则配置的serviceCode，示例：baselineCheck
        // 支持service请参考：https://help.aliyun.com/document_detail/467826.html?0#p-23b-o19-gff
        request.setService("baselineCheck");
        request.setServiceParameters(JSON.toJSONString(serviceParameters));
        ImageModerationResponse response = null;
        try {
            response = client.imageModerationWithOptions(request, runtime);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return response;
    }

    public Map localImageScan(String imagePath) throws Exception {
        Map<String, String> map = new HashMap<>();
        // 接入区域和地址请根据实际情况修改。
        ImageModerationResponse response = invokeFunction(accessKeyId, secret, "green-cip.cn-chengdu.aliyuncs.com", imagePath);
        try {
            // 自动路由。
            if (response != null) {
                //区域切换到cn-beijing。
                if (500 == response.getStatusCode() || (response.getBody() != null && 500 == (response.getBody().getCode()))) {
                    // 接入区域和地址请根据实际情况修改。
                    response = invokeFunction(accessKeyId, secret, "green-cip.cn-beijing.aliyuncs.com", imagePath);
                }
            }
            // 打印检测结果。
            if (response != null) {
                if (response.getStatusCode() == 200) {
                    System.out.println("图片检测开始");
                    ImageModerationResponseBody body = response.getBody();
                    JSONObject.toJSONString(body);
//                    System.out.println("requestId=" + body.getRequestId());
//                    System.out.println("code=" + body.getCode());
//                    System.out.println("msg=" + body.getMsg());
                    if (body.getCode() == 200) {
                        ImageModerationResponseBody.ImageModerationResponseBodyData data = body.getData();
//                        System.out.println("dataId=" + data.getDataId());
                        List<ImageModerationResponseBody.ImageModerationResponseBodyDataResult> results = data.getResult();
                        if (results.get(0).getLabel().equals("nonLabel") && results.get(0).getConfidence() == null) {
                            map.put("suggestion", "pass");
                        } else {
                            map.put("suggestion", "block");
                        }
                        for (ImageModerationResponseBody.ImageModerationResponseBodyDataResult result : results) {
                            System.out.println("result = " + JSONObject.toJSONString(result));
                        }
                    } else {
                        System.out.println("image moderation not success. code:" + body.getCode());
                    }
                } else {
                    System.out.println("response not success. status:" + response.getStatusCode());
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        System.out.println(JSONObject.toJSONString(map));
        return map;
    }
}