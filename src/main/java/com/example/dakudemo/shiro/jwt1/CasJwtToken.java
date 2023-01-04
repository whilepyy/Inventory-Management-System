package com.example.dakudemo.shiro.jwt1;

import org.apache.shiro.authc.AuthenticationToken;

public class CasJwtToken implements AuthenticationToken {
    private String username;
    private String ticket;
    private String token;
    private String IPAddress;
    private CasJwtTokenMode mode;

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getTicket() {
        return ticket;
    }

    public void setTicket(String ticket) {
        this.ticket = ticket;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getIPAddress() {
        return IPAddress;
    }

    public void setIPAddress(String IPAddress) {
        this.IPAddress = IPAddress;
    }

    public CasJwtToken() {
    }

    public CasJwtTokenMode getMode() {
        return mode;
    }

    public void setMode(CasJwtTokenMode mode) {
        this.mode = mode;
    }

    public CasJwtToken(String token) {
        this.token = token;
    }

    public CasJwtToken(String ticket, String token) {
        this.ticket = ticket;
        this.token = token;
    }

    public CasJwtToken(String ticket, String token, String IPAddress) {
        this.ticket = ticket;
        this.token = token;
        this.IPAddress = IPAddress;
    }

    public CasJwtToken(String username, String ticket, String token, String IPAddress) {
        this.username = username;
        this.ticket = ticket;
        this.token = token;
        this.IPAddress = IPAddress;
    }

    @Override
    public Object getPrincipal() {
        return this.username;
    }

    @Override
    public Object getCredentials() {
        if(this.mode.equals(CasJwtTokenMode.CasLogin)){
            return this.ticket;
        }else{
            return this.token;
        }

    }
}
