package com.example.dakudemo.shiro.jwt;

import org.apache.shiro.authc.AuthenticationToken;

import java.util.Map;

public class JwtToken implements AuthenticationToken {

    private String principal;
    private String token;

    @Override
    public String getPrincipal() {
        return principal;
    }

    @Override
    public Object getCredentials() {
        return token;
    }



    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public void setPrincipal(String principal) {
        this.principal = principal;
    }


    public JwtToken() {
    }

    public JwtToken(String principal, String token) {
        this.principal = principal;
        this.token = token;
    }

    public JwtToken(String principal, String ticket, String token) {
        this.principal = principal;
        this.token = token;
    }

    public JwtToken(String token) {
        this.token = token;
    }
}
