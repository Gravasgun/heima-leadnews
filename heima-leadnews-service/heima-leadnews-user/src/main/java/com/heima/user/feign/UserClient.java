package com.heima.user.feign;

import com.heima.apis.user.IUserClient;
import com.heima.model.admin.dtos.AuthDto;
import com.heima.model.common.dtos.ResponseResult;
import com.heima.model.user.dtos.UserRelationDto;
import com.heima.user.service.ApUserRealNameService;
import com.heima.user.service.ApUserRelationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class UserClient implements IUserClient {
    @Autowired
    private ApUserRealNameService userRealNameService;

    @Autowired
    private ApUserRelationService userRelationService;

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

    /**
     * 审核失败
     *
     * @param dto
     * @return
     */
    @Override
    @PostMapping("/api/v1/auth/authFail")
    public ResponseResult authFail(@RequestBody AuthDto dto) {
        return userRealNameService.authFail(dto);
    }

    /**
     * 通过审核
     *
     * @param dto
     * @return
     */
    @Override
    @PostMapping("/api/v1/auth/authPass")
    public ResponseResult authPass(@RequestBody AuthDto dto) {
        return userRealNameService.authPass(dto);
    }

    /**
     * 用户关注/取消关注
     *
     * @param dto
     * @return
     */
    @Override
    public ResponseResult follow(@RequestBody UserRelationDto dto) {
        return userRelationService.follow(dto);
    }
}
