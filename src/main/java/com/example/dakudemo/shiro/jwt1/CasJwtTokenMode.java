package com.example.dakudemo.shiro.jwt1;

// 该枚举类是区别CasJwtToken的不同应用场景
public enum CasJwtTokenMode {
    // CAS登录回调
    CasLogin,

    // 用户访问资源
    UserAccess
 }
