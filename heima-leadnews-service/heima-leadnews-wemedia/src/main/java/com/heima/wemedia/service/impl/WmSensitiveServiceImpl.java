package com.heima.wemedia.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.heima.model.admin.dtos.SensitiveDto;
import com.heima.model.common.dtos.PageResponseResult;
import com.heima.model.common.dtos.ResponseResult;
import com.heima.model.common.enums.AppHttpCodeEnum;
import com.heima.model.wemedia.pojos.WmSensitive;
import com.heima.wemedia.mapper.WmSensitiveMapper;
import com.heima.wemedia.service.WmSensitiveService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
@Transactional
public class WmSensitiveServiceImpl extends ServiceImpl<WmSensitiveMapper, WmSensitive> implements WmSensitiveService {
    @Autowired
    private WmSensitiveMapper sensitiveMapper;

    /**
     * 敏感词分页查询
     *
     * @param dto
     * @return
     */
    @Override
    public ResponseResult list(SensitiveDto dto) {
        //参数校验
        if (dto == null) {
            return ResponseResult.errorResult(AppHttpCodeEnum.PARAM_INVALID);
        }
        IPage<WmSensitive> page = new Page<>(dto.getPage(), dto.getSize());
        LambdaQueryWrapper<WmSensitive> queryWrapper = new LambdaQueryWrapper<>();
        //按照创建时间倒序查询
        queryWrapper.orderByDesc(WmSensitive::getCreatedTime);
        //按照敏感词名称模糊查询
        if (StringUtils.isNotBlank(dto.getName())) {
            queryWrapper.like(WmSensitive::getSensitives, dto.getName());
        }
        page = page(page, queryWrapper);
        //返回结果
        ResponseResult responseResult = new PageResponseResult(dto.getPage(), dto.getSize(), (int) page.getTotal());
        responseResult.setData(page.getRecords());
        return responseResult;
    }

    /**
     * 新增敏感词
     *
     * @param sensitive
     * @return
     */
    @Override
    public ResponseResult saveSensitive(WmSensitive sensitive) {
        //参数校验
        if (sensitive == null) {
            return ResponseResult.errorResult(AppHttpCodeEnum.PARAM_INVALID);
        }
        List<WmSensitive> sensitiveList = sensitiveMapper.selectList(null);
        List<String> sensitiveNameList = sensitiveList.stream().map(WmSensitive::getSensitives).collect(Collectors.toList());
        //已存在的敏感词不能保存
        if (sensitiveNameList.contains(sensitive.getSensitives())) {
            return ResponseResult.errorResult(AppHttpCodeEnum.DATA_EXIST);
        }
        if (sensitive.getCreatedTime() == null) {
            sensitive.setCreatedTime(new Date());
        }
        save(sensitive);
        return ResponseResult.okResult(AppHttpCodeEnum.SUCCESS);
    }
}
