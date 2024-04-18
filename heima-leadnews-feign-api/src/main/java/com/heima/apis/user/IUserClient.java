package com.heima.apis.user;

import com.heima.model.admin.dtos.AuthDto;
import com.heima.model.common.dtos.ResponseResult;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(value = "leadnews-user")
public interface IUserClient {
    /**
     * 分页查询用户列表
     * @param dto
     * @return
     */
    @PostMapping("/api/v1/auth/list")
    ResponseResult findUserList(@RequestBody AuthDto dto);
}
