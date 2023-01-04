package com.example.dakudemo.shiro.jwt;


import com.example.dakudemo.shiro.jwt1.Constant;
import com.example.dakudemo.util.JwtTokenUtils;
import org.apache.commons.lang3.StringUtils;
import org.jasig.cas.client.authentication.AttributePrincipal;
import org.jasig.cas.client.validation.Assertion;
import org.jasig.cas.client.validation.Cas20ServiceTicketValidator;
import org.jasig.cas.client.validation.TicketValidationException;
import org.jasig.cas.client.validation.TicketValidator;
import org.springframework.http.HttpMethod;

import javax.servlet.*;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Enumeration;
import java.util.Map;

public class CasCallbackFilter extends MyPathMatchingFilter {

    private JwtTokenUtils jwtTokenUtil;

    private TicketValidator ticketValidator;

    private String casService;

    private String casServerUrlPrefix;

    private String successUrl;

    public String getSuccessUrl() {
        return successUrl;
    }

    public void setSuccessUrl(String successUrl) {
        this.successUrl = successUrl;
    }

    public String getCasServerUrlPrefix() {
        return casServerUrlPrefix;
    }

    public void setCasServerUrlPrefix(String casServerUrlPrefix) {
        this.casServerUrlPrefix = casServerUrlPrefix;
    }

    public String getCasService() {
        return casService;
    }

    public void setCasService(String casService) {
        this.casService = casService;
    }

    public JwtTokenUtils getJwtTokenUtil() {
        return jwtTokenUtil;
    }

    public void setJwtTokenUtil(JwtTokenUtils jwtTokenUtil) {
        this.jwtTokenUtil = jwtTokenUtil;
    }

    public CasCallbackFilter() {
        setSuffix(Constant.DEFAULT_CALLBACK_SUFFIX);
    }

    protected TicketValidator ensureTicketValidator() {
        if (this.ticketValidator == null) {
            this.ticketValidator = createTicketValidator();
        }
        return this.ticketValidator;
    }

    protected TicketValidator createTicketValidator() {
        String urlPrefix = getCasServerUrlPrefix();
        return new Cas20ServiceTicketValidator(urlPrefix);
    }

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {

    }

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        if (mustApply(servletRequest)){
            HttpServletRequest httpRequest = (HttpServletRequest) servletRequest;
            String ticket = "";
            //判断ticket是否为空
            if (isTokenRequest(httpRequest)){
                ticket = httpRequest.getParameter(Constant.TICKET_NAME);
                Assertion casAssertion = null;
                try {
                    ticketValidator = ensureTicketValidator();
                    casAssertion = ticketValidator.validate(ticket, getCasService());
                    // get principal, user id and attributes
                    AttributePrincipal casPrincipal = casAssertion.getPrincipal();
                    String username = casPrincipal.getName();
                    //从attributes里面获取请求IP
                    Map<String, Object> attributes = casPrincipal.getAttributes();
                    String IPAddressPort = (String) attributes.get(Constant.USER_IP);
                    String IPAddress = IPAddressPort.split(":")[0];
                    //从attributes获取部门
                    String department = (String) attributes.get(Constant.DEPARTMENT);
                    // 验证用户名密码成功后生成token
                    String token = jwtTokenUtil.generateToken(username, IPAddress, department);
                    //TODO 保存ticket与token

                    //跳转到前端服务并携带token
                    //((HttpServletResponse) servletResponse).sendRedirect("http://localhost:8989/login?token=" + token);
                    //((HttpServletResponse) servletResponse).sendRedirect("http://202.38.77.133:8989/#?token=" + token);
                    ((HttpServletResponse) servletResponse).sendRedirect(successUrl + "?token=" + token);
                } catch (TicketValidationException e) {
                    e.printStackTrace();
                }
            } else if (isBackLogoutRequest(httpRequest)) {
                //得到登出标识
                final String logoutMessage = httpRequest.getParameter(Constant.LOGOUT_NAME);
                //解析得到ticket
                ticket = substring(logoutMessage,"SessionIndex>","</");
                //TODO 在数据库中删除ticket
            }
        }else {
            filterChain.doFilter(servletRequest, servletResponse);
        }
    }

    @Override
    public void destroy() {

    }

    private boolean isBackLogoutRequest(HttpServletRequest httpRequest) {
        if (StringUtils.equalsAnyIgnoreCase(HttpMethod.POST.name(), httpRequest.getMethod())){
            String contentType = "";
            Enumeration<String> headerNames = httpRequest.getHeaderNames();
            while ( headerNames.hasMoreElements()){
                String headerName = headerNames.nextElement();
                if (StringUtils.equalsAnyIgnoreCase(headerName,Constant.CONTENT_TYPE_NAME)) {
                    contentType = httpRequest.getHeader(headerName);
                    if ( !StringUtils.startsWithIgnoreCase(contentType,Constant.TICKET_MULTIPART)){
                        if (StringUtils.isNoneEmpty(httpRequest.getParameter(Constant.LOGOUT_NAME))){
                            return Boolean.TRUE;
                        }
                    }
                }
            }

        }
        return Boolean.FALSE;
    }

    public static String substring(final String str, final String open, final String close) {
        if (str == null || open == null || close == null) {
            return null;
        }
        int start = str.indexOf(open);
        if (start != Constant.INDEX_NOT_FOUND) {
            int end = str.indexOf(close, start + open.length());
            if (end != Constant.INDEX_NOT_FOUND) {
                return str.substring(start + open.length(), end);
            }
        }
        return null;
    }

    private boolean isTokenRequest(HttpServletRequest httpRequest) {
        return StringUtils.isNoneEmpty(httpRequest.getParameter(Constant.TICKET_NAME));
    }

    protected String getRequestToken(HttpServletRequest request){
        String token = request.getHeader(Constant.TOKEN);
        if(com.example.dakudemo.util.StringUtils.isBlank(token)){
            token = request.getParameter(Constant.TOKEN);
        }
        if(com.example.dakudemo.util.StringUtils.isBlank(token)){
            Cookie[] cookies = request.getCookies();
            if(cookies != null && cookies.length > 0){
                for(Cookie cookie : cookies){
                    if(Constant.TOKEN.equals(cookie.getName())){
                        token = cookie.getValue();
                    }
                }
            }
        }
        return token;
    }
}
