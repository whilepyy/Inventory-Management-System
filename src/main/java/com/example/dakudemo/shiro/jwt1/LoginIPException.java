package com.example.dakudemo.shiro.jwt1;
/*
 * @author:chh
 * @Date:2022-03-16-19:53
 * @Description:自定义异常，登录IP异常
 */
public class LoginIPException extends Exception{
    public LoginIPException() {
        super();
    }

    public LoginIPException(String message) {
        super(message);
    }
}
