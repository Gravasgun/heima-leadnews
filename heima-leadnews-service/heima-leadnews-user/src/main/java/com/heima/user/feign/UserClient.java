package com.heima.user.feign;

import com.heima.apis.user.IUserClient;
import com.heima.model.admin.dtos.AuthDto;
import com.heima.model.common.dtos.ResponseResult;
import com.heima.user.service.ApUserRealNameService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class UserClient implements IUserClient {
    @Autowired
    private ApUserRealNameService userRealNameService;

    /**
     * 分页查询用户列表
     *
     * @param dto
     * @return
     */
    @Override
    @PostMapping("/api/v1/auth/list")
    public ResponseResult findUserList(@RequestBody AuthDto dto) {
        return userRealNameService.findUserList(dto);
    }
}
