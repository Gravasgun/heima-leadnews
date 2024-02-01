package com.heima.user.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.heima.model.common.dtos.ResponseResult;
import com.heima.model.common.enums.AppHttpCodeEnum;
import com.heima.model.user.dtos.LoginDto;
import com.heima.user.mapper.ApUserMapper;
import com.heima.model.user.beans.ApUser;
import com.heima.user.service.ApUserService;
import com.heima.utils.AppJwtUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.DigestUtils;

import java.util.HashMap;
import java.util.Map;

@Service
@Transactional
@Slf4j
public class ApUserServiceImpl extends ServiceImpl<ApUserMapper, ApUser> implements ApUserService {
    @Autowired
    private ApUserMapper userMapper;

    /**
     * app端登录功能
     *
     * @param loginDto
     * @return
     */
    @Override
    public ResponseResult login(LoginDto loginDto) {
        if (loginDto != null && StringUtils.isNotBlank(loginDto.getPassword()) && StringUtils.isNotBlank(loginDto.getPhone())) {
            //1.正常登录(前端传的数据不为空)
            //1.1根据手机号查询用户
            LambdaQueryWrapper<ApUser> queryWrapper = new LambdaQueryWrapper<>();
            queryWrapper.eq(ApUser::getPhone, loginDto.getPhone());
            ApUser dbUser = userMapper.selectOne(queryWrapper);
            if (dbUser==null){
                return ResponseResult.errorResult(AppHttpCodeEnum.DATA_NOT_EXIST, "用户不存在");
            }
            //1.2比对密码(前端传过来的密码+数据库中的salt与数据库的密码比对)
            String dbSalt = dbUser.getSalt();
            String password = loginDto.getPassword();
            String md5Password = DigestUtils.md5DigestAsHex((password + dbSalt).getBytes());
            //1.3密码比对失败
            if (!dbUser.getPassword().equals(md5Password)) {
                return ResponseResult.errorResult(AppHttpCodeEnum.LOGIN_PASSWORD_ERROR, "用户密码错误");
            }
            //1.4密码比对成功 返回jwt和user
            String token = AppJwtUtil.getToken(dbUser.getId().longValue());
            dbUser.setPassword("");
            dbUser.setSalt("");
            Map<String, Object> map = new HashMap<>();
            map.put("token", token);
            map.put("user", dbUser);
            return ResponseResult.okResult(map);
        } else {
            //2.游客登录
            Map<String, Object> map = new HashMap<>();
            map.put("token", AppJwtUtil.getToken(0L));
            return ResponseResult.okResult(map);
        }
    }

}
