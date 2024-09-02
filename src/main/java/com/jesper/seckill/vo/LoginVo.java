package com.jesper.seckill.vo;

import com.jesper.seckill.validator.IsMobile;

import javax.validation.constraints.NotNull;

/**
 * Created by jiangyunxiong on 2018/5/22.
 */
public class LoginVo {

    @NotNull
    private String mobile;

    @NotNull
    private String password;

    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    @Override
    public String toString() {
        return "LoginVo{" +
                "mobile='" + mobile + '\'' +
                ", password='" + password + '\'' +
                '}';
    }
}
