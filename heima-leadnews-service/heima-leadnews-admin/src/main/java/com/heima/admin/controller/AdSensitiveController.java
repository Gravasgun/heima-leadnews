package com.heima.admin.controller;

import com.heima.apis.wemedia.IWemediaClient;
import com.heima.model.admin.dtos.SensitiveDto;
import com.heima.model.common.dtos.ResponseResult;
import com.heima.model.wemedia.pojos.WmSensitive;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/sensitive")
public class AdSensitiveController {
    @Autowired
    private IWemediaClient wemediaClient;

    @PostMapping("/api/v1/sensitive/list")
    public ResponseResult findListWithPage(@RequestBody SensitiveDto dto) {
        return wemediaClient.findSensitiveListPage(dto);
    }

    @PostMapping("/api/v1/sensitive/save")
    public ResponseResult saveSensitive(@RequestBody WmSensitive sensitive) {
        return wemediaClient.saveSensitive(sensitive);
    }

    @PostMapping("/api/v1/sensitive/update")
    public ResponseResult updateSensitive(@RequestBody WmSensitive sensitive) {
        return wemediaClient.updateSensitive(sensitive);
    }
}
