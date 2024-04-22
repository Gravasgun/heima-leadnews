package com.heima.behavior.controller;

import com.heima.apis.user.IUserClient;
import com.heima.model.common.dtos.ResponseResult;
import com.heima.model.user.dtos.UserRelationDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/user")
public class UserRelationController {

    @Autowired
    private IUserClient userClient;

    @PostMapping("/user_follow")
    public ResponseResult follow(@RequestBody UserRelationDto dto) {
        return userClient.follow(dto);
    }
}