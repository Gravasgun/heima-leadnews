package com.heima.common.aliyun.util;

import com.alibaba.fastjson.JSON;
import com.aliyun.green20220302.Client;
import com.aliyun.green20220302.models.ImageModerationRequest;
import com.aliyun.green20220302.models.ImageModerationResponse;
import com.aliyun.green20220302.models.ImageModerationResponseBody;
import com.aliyun.teaopenapi.models.Config;
import com.aliyun.teautil.models.RuntimeOptions;
import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.*;

@Getter
@Setter
@Component
@ConfigurationProperties(prefix = "aliyun")
public class ImageScanUtil {

    private String accessKeyId;
    private String secret;
    private String scenes;

    public Map imageScan(String url) throws Exception {
        Map<String, String> map = new HashMap<>();
        // 接入区域和地址请根据实际情况修改。
        ImageModerationResponse response = invokeFunction(accessKeyId, secret, "green-cip.cn-chengdu.aliyuncs.com", url);
        try {
            // 自动路由。
            if (response != null) {
                //区域切换到cn-beijing。
                if (500 == response.getStatusCode() || (response.getBody() != null && 500 == (response.getBody().getCode()))) {
                    // 接入区域和地址请根据实际情况修改。
                    response = invokeFunction(accessKeyId, secret, "green-cip.cn-beijing.aliyuncs.com", url);
                }
            }
            // 打印检测结果。
            if (response != null) {
                if (response.getStatusCode() == 200) {
                    ImageModerationResponseBody body = response.getBody();
                    System.out.println("requestId=" + body.getRequestId());
                    System.out.println("code=" + body.getCode());
                    System.out.println("msg=" + body.getMsg());
                    if (body.getCode() == 200) {
                        ImageModerationResponseBody.ImageModerationResponseBodyData data = body.getData();
                        System.out.println("dataId=" + data.getDataId());
                        List<ImageModerationResponseBody.ImageModerationResponseBodyDataResult> results = data.getResult();
                        if (results.get(0).getLabel().equals("nonLabel") && results.get(0).getConfidence() == null) {
                            map.put("suggestion", "pass");
                        } else {
                            map.put("suggestion", "block");
                        }
                        for (ImageModerationResponseBody.ImageModerationResponseBodyDataResult result : results) {
                            System.out.println("label=" + result.getLabel());
                            System.out.println("confidence=" + result.getConfidence());
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
        if (map.isEmpty()) {
            map.put("result", "");
            return map;
        }
        return map;
    }

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
        config.setEndpoint(endpoint);
        return new Client(config);
    }

    public static ImageModerationResponse invokeFunction(String accessKeyId, String accessKeySecret, String endpoint, String url) throws Exception {
        //注意，此处实例化的client请尽可能重复使用，避免重复建立连接，提升检测性能。
        Client client = createClient(accessKeyId, accessKeySecret, endpoint);
        // 创建RuntimeObject实例并设置运行参数
        RuntimeOptions runtime = new RuntimeOptions();
        // 检测参数构造。
        Map<String, String> serviceParameters = new HashMap<>();
        //公网可访问的URL。
        serviceParameters.put("imageUrl", url);
        //待检测数据唯一标识
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
}