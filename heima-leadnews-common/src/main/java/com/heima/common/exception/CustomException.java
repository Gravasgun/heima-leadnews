package com.heima.common.exception;

import com.heima.model.common.enums.AppHttpCodeEnum;

/**
 * 自定义异常类
 */
public class CustomException extends RuntimeException {

    //异常枚举对象
    private AppHttpCodeEnum appHttpCodeEnum;

    public CustomException(AppHttpCodeEnum appHttpCodeEnum){
        this.appHttpCodeEnum = appHttpCodeEnum;
    }

    public AppHttpCodeEnum getAppHttpCodeEnum() {
        return appHttpCodeEnum;
    }
}
