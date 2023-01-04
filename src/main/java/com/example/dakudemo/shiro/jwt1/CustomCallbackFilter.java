package com.example.dakudemo.shiro.jwt1;

import com.auth0.jwt.exceptions.JWTDecodeException;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.exceptions.TokenExpiredException;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.shiro.authc.AuthenticationException;
import org.apache.shiro.authc.AuthenticationToken;
import org.apache.shiro.subject.Subject;
import org.apache.shiro.web.filter.authc.BasicHttpAuthenticationFilter;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.RequestMethod;


import javax.servlet.*;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.UnknownHostException;

@Slf4j
public class CustomCallbackFilter extends BasicHttpAuthenticationFilter {

    @Override
    protected boolean isAccessAllowed(ServletRequest request, ServletResponse response, Object mappedValue) {
        return false;

    }

    @Override
    protected boolean onAccessDenied(ServletRequest request, ServletResponse response) throws Exception{
        try {
            this.executeLogin(request, response);
            //((HttpServletResponse) response).setStatus(HttpStatus.OK.value());
            return true;
        } catch (Exception e) {
            String msg = e.getMessage();
            Throwable throwable = e.getCause();
            if(throwable instanceof JWTDecodeException){
                responseError(request, response, "JWT不合法或解码错误！", 403);
            }else if(throwable instanceof TokenExpiredException){
                responseError(request, response, "JWT过期失效！", 403);
            }else if(e instanceof LoginIPException) {
                responseError(request, response, "IP校验异常！", 403);
            }else if(e instanceof AuthenticationException){
                responseError(request, response, e.getMessage(), 403);
            }else {
                if(throwable != null){
                    responseError(request, response, e.getMessage(), 403);
                }
            }
            return false;
            //e.printStackTrace();
        }
    }

    @Override
    protected AuthenticationToken createToken(ServletRequest request, ServletResponse response) {
        HttpServletRequest httpRequest = (HttpServletRequest)request;
        String token = getRequestToken(httpRequest);
        String ticket = httpRequest.getParameter(Constant.TICKET_NAME);
        CasJwtToken casJwtToken = new CasJwtToken();
        casJwtToken.setTicket(ticket);
        casJwtToken.setToken(token);
        casJwtToken.setIPAddress(getIPAddress(httpRequest));
        if(isTicketRequest(httpRequest)){
            casJwtToken.setMode(CasJwtTokenMode.CasLogin);
        }else{
            casJwtToken.setMode(CasJwtTokenMode.UserAccess);
        }
        return casJwtToken;
    }


    @Override
    protected boolean executeLogin(ServletRequest request, ServletResponse response) throws Exception{
        CasJwtToken token = (CasJwtToken) this.createToken(request, response);
        Subject subject =  this.getSubject(request, response);
        subject.login(token);
        return true;
    }

    private boolean isTicketRequest(HttpServletRequest httpRequest) {
        return StringUtils.isNoneEmpty(httpRequest.getParameter(Constant.TICKET_NAME));
    }


    //该方法用于从header,params,cookie中搜索token
    protected String getRequestToken(HttpServletRequest request){
        String token = request.getHeader(Constant.TOKEN);
        if (com.example.dakudemo.util.StringUtils.isBlank(token)){
            token = request.getHeader(Constant.AUTHORIZATION_HEADER);
        }
        if(com.example.dakudemo.util.StringUtils.isBlank(token)){
            token = request.getParameter(Constant.TOKEN);
        }
        if (com.example.dakudemo.util.StringUtils.isBlank(token)) {
            token = request.getParameter("sx_sso_sessionid");
        }
        if(com.example.dakudemo.util.StringUtils.isBlank(token)){
            Cookie[] cookies = request.getCookies();
            if(cookies != null && cookies.length > 0){
                for(Cookie cookie : cookies){
                    if(Constant.TOKEN.equals(cookie.getName())){
                        token = cookie.getValue();
                        break;
                    }
                }
            }
        }
        return token;
    }

    //获取请求的ip地址
    public static String getIPAddress(HttpServletRequest request) {
        String ipAddress = request.getHeader("x-forwarded-for");
        if (ipAddress == null || ipAddress.length() == 0 || "unknown".equalsIgnoreCase(ipAddress)) {
            ipAddress = request.getHeader("Proxy-Client-IP");
        }
        if (ipAddress == null || ipAddress.length() == 0 || "unknown".equalsIgnoreCase(ipAddress)) {
            ipAddress = request.getHeader("WL-Proxy-Client-IP");
        }
        if (ipAddress == null || ipAddress.length() == 0 || "unknown".equalsIgnoreCase(ipAddress)) {
            ipAddress = request.getRemoteAddr();
            String localIp = "127.0.0.1";
            String localIpv6 = "0:0:0:0:0:0:0:1";
            if (ipAddress.equals(localIp) || ipAddress.equals(localIpv6)) {
                // 根据网卡取本机配置的IP
                InetAddress inet = null;
                try {
                    inet = InetAddress.getLocalHost();
                    ipAddress = inet.getHostAddress();
                } catch (UnknownHostException e) {
                    e.printStackTrace();
                }
            }
        }
        // 对于通过多个代理的情况，第一个IP为客户端真实IP,多个IP按照','分割
        String ipSeparate = ",";
        int ipLength = 15;
        if (ipAddress != null && ipAddress.length() > ipLength) {
            if (ipAddress.indexOf(ipSeparate) > 0) {
                ipAddress = ipAddress.substring(0, ipAddress.indexOf(ipSeparate));
            }
        }
        return ipAddress;
    }

    private void responseError(ServletRequest request, ServletResponse response, String msg, Integer status){
//        HttpServletRequest httpServletRequest = (HttpServletRequest) request;
        HttpServletResponse httpServletResponse = (HttpServletResponse) response;
        httpServletResponse.setStatus(status);
        httpServletResponse.setCharacterEncoding("utf-8");
        httpServletResponse.setContentType("application/text; charset=utf-8");
        try(PrintWriter out = httpServletResponse.getWriter()){
//            Result result = new Result();
//            result.setCode(status);
//            result.setMsg("无法访问："+ msg);
//            String data = JsonCovertUtils.objToJson(request);
            out.append("无法访问：").append(msg);
        }catch (IOException e){
            log.error("responseError返回信息错误"+e.getMessage());
        }
    }

    @Override
    protected boolean preHandle(ServletRequest request, ServletResponse response)throws Exception{
        HttpServletRequest httpServletRequest = (HttpServletRequest) request;
        HttpServletResponse httpServletResponse = (HttpServletResponse) response;
        httpServletResponse.setHeader("Access-control-Allow-Origin", httpServletRequest.getHeader("Origin"));
        httpServletResponse.setHeader("Access-Control-Allow-Methods", "GET,POST,OPTIONS,PUT,DELETE");
        httpServletResponse.setHeader("Access-Control-Allow-Headers", httpServletRequest.getHeader("Access-Control-Request-Headers"));
        // 跨域时会首先发送一个OPTIONS请求，这里我们给OPTIONS请求直接返回正常状态
        if (httpServletRequest.getMethod().equals(RequestMethod.OPTIONS.name())) {
            httpServletResponse.setStatus(HttpStatus.OK.value());
            return false;
        }
        return super.preHandle(request, response);
    }
}
