package com.heima.wemedia.test;

import com.heima.common.aliyun.OssImageScanUtil;
import com.heima.common.aliyun.LocalImageScanUtil;
import com.heima.common.aliyun.TextScanUtil;
import com.heima.file.service.FileStorageService;
import com.heima.wemedia.WemediaApplication;
import com.heima.wemedia.service.WmNewsAutoScanService;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Map;

@SpringBootTest(classes = WemediaApplication.class)
@RunWith(SpringRunner.class)

public class Test {
    @Autowired
    private TextScanUtil textScanUtil;
    @Autowired
    private FileStorageService fileStorageService;
    @Autowired
    private OssImageScanUtil ossImageScanUtil;
    @Autowired
    private LocalImageScanUtil localImageScanUtil;
    @Autowired
    private WmNewsAutoScanService wmNewsAutoScanService;

    /**
     * 测试文本
     */
    @org.junit.Test
    public void testScanText() throws Exception {
        //Map map = greenTextScan.greeTextScan("你妈的");
        //Map map = greenTextScan.greeTextScan("冰毒");
        Map map = textScanUtil.greeTextScan("我很优秀");
        System.out.println(map);
    }

    /**
     * 测试图片
     */
    @org.junit.Test
    public void testScanImage() throws Exception {
        //byte[] bytes = fileStorageService.downLoadFile("http://192.168.200.130:9000/leadnews/2024/03/18/8b61536524d74815a2304b69574c096f.jpg");
        //Map map = imageScanUtil.imageScan("https://leadnews-liuhangji.oss-cn-chengdu.aliyuncs.com/image/1.jpg?Expires=1711470190&OSSAccessKeyId=TMP.3Kfq75oPa4Xv8kXy6ec145rTD695Dd2SkRAP7rXtDLKk7A9Q34nwV9aw88n7SBEi1Hf2C4qUkeMnasUBbWfA2cS327tar5&Signature=430nFUxLSKWaqrT8vovjJ23UPi4%3D");
        //Map map = imageScanUtil.imageScan("https://leadnews-liuhangji.oss-cn-chengdu.aliyuncs.com/image/2.jpg?Expires=1711470574&OSSAccessKeyId=TMP.3Kfq75oPa4Xv8kXy6ec145rTD695Dd2SkRAP7rXtDLKk7A9Q34nwV9aw88n7SBEi1Hf2C4qUkeMnasUBbWfA2cS327tar5&Signature=5H8ZOfvSCqFU7DfzLcsrvnysY74%3D");
        Map map = localImageScanUtil.localImageScan("http://192.168.200.130:9000/leadnews/2024/03/18/8b61536524d74815a2304b69574c096f.jpg");
        System.out.println(map);
    }

    @org.junit.Test
    public void autoScanNews() {
        wmNewsAutoScanService.autoScanNews(6234);
    }
}
