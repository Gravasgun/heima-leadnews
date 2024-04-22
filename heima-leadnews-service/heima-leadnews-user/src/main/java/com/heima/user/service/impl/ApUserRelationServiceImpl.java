package com.heima.user.service.impl;

import com.heima.common.constans.BehaviorConstants;
import com.heima.common.redis.CacheService;
import com.heima.model.common.dtos.ResponseResult;
import com.heima.model.common.enums.AppHttpCodeEnum;
import com.heima.model.user.beans.ApUser;
import com.heima.model.user.dtos.UserRelationDto;
import com.heima.user.service.ApUserRelationService;
import com.heima.utils.thread.AppThreadLocalUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ApUserRelationServiceImpl implements ApUserRelationService {

    @Autowired
    private CacheService cacheService;


    /**
     * 用户关注/取消关注
     *
     * @param dto
     * @return
     */
    @Override
    public ResponseResult follow(UserRelationDto dto) {
        //1 参数校验
        if (dto.getOperation() == null || dto.getOperation() < 0 || dto.getOperation() > 1) {
            return ResponseResult.errorResult(AppHttpCodeEnum.PARAM_INVALID);
        }
        //2 判断是否登录
        ApUser user = AppThreadLocalUtil.getUser();
        if (user == null) {
            return ResponseResult.errorResult(AppHttpCodeEnum.NEED_LOGIN);
        }
        //当前登录的用户(用户端的用户)
        Integer apUserId = user.getId();
        //3 关注 apuser:follow:  apuser:fans:
        //被关注的用户的id(自媒体端的用户)
        Integer followedUserId = dto.getAuthorId();
        if (dto.getOperation() == 0) {
            // 当前登录用户关注列表
            cacheService.zAdd(BehaviorConstants.APUSER_FOLLOW_RELATION + apUserId, followedUserId.toString(), System.currentTimeMillis());
            // 被关注用户粉丝列表
            cacheService.zAdd(BehaviorConstants.APUSER_FANS_RELATION+ followedUserId, apUserId.toString(), System.currentTimeMillis());
        } else {
            // 取消关注
            cacheService.zRemove(BehaviorConstants.APUSER_FOLLOW_RELATION + apUserId, followedUserId.toString());
            cacheService.zRemove(BehaviorConstants.APUSER_FANS_RELATION + followedUserId, apUserId.toString());
        }
        return ResponseResult.okResult(AppHttpCodeEnum.SUCCESS);
    }
}
