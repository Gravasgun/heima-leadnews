package com.heima.user.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.heima.model.admin.dtos.AuthDto;
import com.heima.model.common.dtos.ResponseResult;
import com.heima.model.user.beans.ApUserRealName;

public interface ApUserRealNameService extends IService<ApUserRealName> {
    /**
     * 查询用户列表
     *
     * @param dto
     * @return
     */
    ResponseResult findUserList(AuthDto dto);

    /**
     * 审核失败
     * @param dto
     * @return
     */
    ResponseResult authFail(AuthDto dto);


    /**
     * 通过审核
     * @param dto
     * @return
     */
    ResponseResult authPass(AuthDto dto);
}