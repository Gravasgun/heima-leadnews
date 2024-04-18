package com.heima.user.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.heima.model.admin.dtos.AuthDto;
import com.heima.model.common.dtos.PageResponseResult;
import com.heima.model.common.dtos.ResponseResult;
import com.heima.model.common.enums.AppHttpCodeEnum;
import com.heima.model.user.beans.ApUserRealName;
import com.heima.user.mapper.ApUserRealNameMapper;
import com.heima.user.service.ApUserRealNameService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@Slf4j
public class ApUserRealNameServiceImpl extends ServiceImpl<ApUserRealNameMapper, ApUserRealName> implements ApUserRealNameService {
    /**
     * 查询用户列表
     *
     * @param dto
     * @return
     */
    @Override
    public ResponseResult findUserList(AuthDto dto) {
        if (dto == null) {
            return ResponseResult.errorResult(AppHttpCodeEnum.PARAM_INVALID);
        }
        // 2 分页查询
        IPage pageCheck = new Page(dto.getPage(), dto.getSize());
        // 3 按照不同需求查询
        LambdaQueryWrapper<ApUserRealName> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        //3.1 状态
        if (dto.getStatus() != null) {
            lambdaQueryWrapper.eq(ApUserRealName::getStatus, dto.getStatus());
        }
        //3.2 排序
        lambdaQueryWrapper.orderByDesc(ApUserRealName::getCreatedTime);
        pageCheck = page(pageCheck, lambdaQueryWrapper);
        //4. 返回结果
        ResponseResult responseResult = new PageResponseResult(dto.getPage(), dto.getSize(), (int) pageCheck.getTotal());
        responseResult.setData(pageCheck.getRecords());
        return responseResult;
    }
}
