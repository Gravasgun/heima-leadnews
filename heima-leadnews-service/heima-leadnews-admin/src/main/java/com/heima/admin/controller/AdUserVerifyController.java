package com.heima.admin.controller;

import com.heima.apis.user.IUserClient;
import com.heima.model.admin.dtos.AuthDto;
import com.heima.model.common.dtos.ResponseResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/auth")
public class AdUserVerifyController {
    @Autowired
    private IUserClient userClient;

    /**
     * 分页查询用户列表
     *
     * @param dto
     * @return
     */
    @PostMapping("/list")
    public ResponseResult findUserList(@RequestBody AuthDto dto) {
        return userClient.findUserList(dto);
    }

    /**
     * 审核失败
     */
    @PostMapping("/authFail")
    public ResponseResult authFail(@RequestBody AuthDto dto) {
        return userClient.authFail(dto);
    }

    /**
     * 通过审核
     * @param dto
     * @return
     */
    @PostMapping("/api/v1/auth/authPass")
    public ResponseResult authPass(@RequestBody AuthDto dto) {
        return userClient.authPass(dto);
    }
}
