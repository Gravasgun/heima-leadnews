package com.heima.model.admin.dtos;

import lombok.Data;

@Data
public class AuthDto{
    private Integer id;
    private String msg;
    private Integer page;
    private Integer size;
    private Integer status;
}