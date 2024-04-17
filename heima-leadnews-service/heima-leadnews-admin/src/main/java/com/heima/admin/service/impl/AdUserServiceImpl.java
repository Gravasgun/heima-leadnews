package com.heima.admin.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.heima.admin.mapper.AdUserMapper;
import com.heima.admin.service.AdUserService;
import com.heima.model.admin.beans.AdUser;
import com.heima.model.admin.dtos.AdUserDto;
import com.heima.model.common.dtos.ResponseResult;
import com.heima.model.common.enums.AppHttpCodeEnum;
import com.heima.utils.common.AppJwtUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.DigestUtils;

import java.util.HashMap;
import java.util.Map;

@Service
@Transactional
@Slf4j
public class AdUserServiceImpl extends ServiceImpl<AdUserMapper, AdUser> implements AdUserService {
    /**
     * 登录功能
     *
     * @param dto
     * @return
     */
    @Override
    public ResponseResult login(AdUserDto dto) {
        if (dto != null && StringUtils.isNotBlank(dto.getName()) && StringUtils.isNotBlank(dto.getPassword())) {
            //1.正常登录(前端传的数据不为空)
            //1.1根据用户名查询用户
            LambdaQueryWrapper<AdUser> queryWrapper = new LambdaQueryWrapper<>();
            queryWrapper.eq(AdUser::getName, dto.getName());
            AdUser dbUser = getOne(queryWrapper);
            if (dbUser == null) {
                return ResponseResult.errorResult(AppHttpCodeEnum.AP_USER_DATA_NOT_EXIST, "用户不存在");
            }
            //1.2比对密码(前端传过来的密码+数据库中的salt与数据库的密码比对)
            String salt = dbUser.getSalt();
            String password = dto.getPassword();
            String pwd = DigestUtils.md5DigestAsHex((password + salt).getBytes());
            //1.3密码比对失败
            if (!pwd.equals(dbUser.getPassword())) {
                return ResponseResult.errorResult(AppHttpCodeEnum.LOGIN_PASSWORD_ERROR, "密码错误");
            }
            //1.4密码比对成功 返回jwt和user
            String token = AppJwtUtil.getToken(dbUser.getId().longValue());
            //清空敏感信息，填入需要的信息
            dbUser.setPassword("");
            dbUser.setSalt("");
            Map<String, Object> map = new HashMap<>();
            map.put("token", token);
            map.put("user", dbUser);
            return ResponseResult.okResult(map);
        } else {
            return ResponseResult.errorResult(AppHttpCodeEnum.PARAM_INVALID, "用户名或密码为空");
        }
    }
}
