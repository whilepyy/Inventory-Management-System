package com.example.dakudemo.shiro.jwt;


import com.auth0.jwt.exceptions.JWTDecodeException;
import com.auth0.jwt.exceptions.TokenExpiredException;
import com.example.dakudemo.shiro.jwt1.Constant;
import com.example.dakudemo.util.JwtTokenUtils;
import com.example.dakudemo.util.StringUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.shiro.authc.AuthenticationToken;

import org.apache.shiro.subject.Subject;
import org.apache.shiro.web.filter.authc.AuthenticatingFilter;
import org.springframework.http.HttpStatus;
import org.springframework.util.ObjectUtils;


import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.UnknownHostException;


@Slf4j
public class CasJwtFilter extends AuthenticatingFilter {
    //该地址应该是调用cas服务并重定向到本地回调地址的地址，例如：https://xxx/cas/login?service=http://localhost:8989/cas/callback
    private String redirectUrl;

    public void setRedirectUrl(String redirectUrl) {
        this.redirectUrl = redirectUrl;
    }

    public String getRedirectUrl() {
        return redirectUrl;
    }

    private JwtTokenUtils jwtTokenUtil;

    public JwtTokenUtils getJwtTokenUtil() {
        return jwtTokenUtil;
    }

    public void setJwtTokenUtil(JwtTokenUtils jwtTokenUtil) {
        this.jwtTokenUtil = jwtTokenUtil;
    }

    @Override
    protected AuthenticationToken createToken(ServletRequest request, ServletResponse response) throws Exception {
        HttpServletRequest httpRequest = (HttpServletRequest)request;
        String token = getRequestToken(httpRequest);
        JwtToken jwtToken = new JwtToken();
        jwtToken.setToken(token);
        return jwtToken;
    }

    @Override
    protected boolean onAccessDenied(ServletRequest request, ServletResponse response) throws Exception {
        // 获取请求token，如果token不存在，直接返回401
        String token = getRequestToken((HttpServletRequest) request);
        if (StringUtils.isBlank(token)) {
            return executeLogin(request, response);
        }
        try {
            return executeLogin(request, response);
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    @Override
    protected boolean isAccessAllowed(ServletRequest request, ServletResponse response, Object mappedValue) {
//        if (((HttpServletRequest) request).getMethod().equals(RequestMethod.OPTIONS.name())) {
//            return true;
//        }
//        return false;
        String token = getRequestToken((HttpServletRequest) request);
            //校验token
            String tokenIPAddress = null;
            try {
                tokenIPAddress = jwtTokenUtil.getIPAddressToken(token);
            }catch (JWTDecodeException e){
                log.error("token解码出现异常！ token:" +  token);
                ResponseMsg("token解码出现异常！ token:" +  token, response);
                return false;
            }catch (TokenExpiredException e){
                log.error("token已经过期！ token:" + token);
                ResponseMsg("token已经过期！ token:" +  token, response);
                return false;
            }catch (Exception e){
                log.error("token校验异常！ token:" + token);
                ResponseMsg("token校验异常！错误信息:"+e.getMessage()+ "   token:" +  token, response);
                return false;
            }
            if (ObjectUtils.isEmpty(tokenIPAddress)){
                return false;
            }
            String requestIPAddress = getIPAddress((HttpServletRequest) request);
            if(!tokenIPAddress.equals(requestIPAddress)){
                //ip地址异常
                ResponseMsg("IP地址校验异常!", response);
                log.error("IP地址校验异常! token生成的客户端IP:"+tokenIPAddress + " 请求客户端IP:" + requestIPAddress+"  token:"+ token);
                return false;
            }
        return false;
    }

    @Override
    protected boolean onLoginSuccess(AuthenticationToken token, Subject subject, ServletRequest request, ServletResponse response) throws Exception {
        return true;
    }

    @Override
    protected boolean executeLogin(ServletRequest request, ServletResponse response) throws Exception {
        if (isLoginRequest(request, response)) {
            String token = getRequestToken((HttpServletRequest) request);
            System.out.println(token);
            if(StringUtils.isBlank(token)){
                // TODO 跳转到认证服务 cas server带上回调地址  便于登录认证成功后回调到自己的服务
                ((HttpServletResponse) response).sendRedirect(redirectUrl);
            }else{
                ((HttpServletResponse) response).sendRedirect("http://localhost:8989/user?token=" + token);
            }

        }
        return super.executeLogin(request, response);
    }

    //该方法用于从header,params,cookie中搜索token
    protected String getRequestToken(HttpServletRequest request){
        String token = request.getHeader(Constant.TOKEN);
        if (StringUtils.isBlank(token)){
            token = request.getHeader(Constant.AUTHORIZATION_HEADER);
        }
        if(StringUtils.isBlank(token)){
            token = request.getParameter(Constant.TOKEN);
        }
        if (StringUtils.isBlank(token)) {
            token = request.getParameter("sx_sso_sessionid");
        }
        if(StringUtils.isBlank(token)){
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


    private void ResponseMsg(String msg, ServletResponse response){
        HttpServletResponse httpServletResponse = (HttpServletResponse) response;
        httpServletResponse.setStatus(HttpStatus.OK.value());
        httpServletResponse.setCharacterEncoding("utf-8");
        httpServletResponse.setContentType("text/html");
        try {
            PrintWriter out = httpServletResponse.getWriter();
            out.append(msg);
        }catch (Exception e){
            log.error("直接返回Response信息异常"+ e.getMessage());
            e.printStackTrace();
        }
    }
}

