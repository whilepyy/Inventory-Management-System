package com.example.dakudemo.controller;

import com.example.dakudemo.entity.Result;
import com.example.dakudemo.entity.Scrap;
import com.example.dakudemo.entity.User;
import com.example.dakudemo.mapper.ApproveMapper;
import com.example.dakudemo.service.ApproveService;
import com.example.dakudemo.service.UserService;
import com.example.dakudemo.shiro.jwt1.CasJwtToken;
import com.example.dakudemo.shiro.jwt1.CasJwtTokenMode;
import com.example.dakudemo.shiro.jwt1.Constant;
import com.example.dakudemo.util.JwtTokenUtils;
import com.example.dakudemo.util.StringUtils;
import io.swagger.annotations.ApiOperation;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.IncorrectCredentialsException;
import org.apache.shiro.authc.UnknownAccountException;
import org.apache.shiro.authc.UsernamePasswordToken;
import org.apache.shiro.subject.Subject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.util.ObjectUtils;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Map;

/**
 * @author chh
 * @date 2022/2/26 13:12
 */
@RestController
@RequestMapping("/daku/public")
public class PublicController {

    @Value("${cas.casServer.logoutUrl}")
    private String logoutUrl;

    @Autowired
    private UserService userService;

    @Autowired
    private JwtTokenUtils jwtTokenUtil;

    @Autowired
    private ApproveService approveService;

    @Value("${cas.client.successUrl}")
    private String successUrl;

    @ApiOperation("此处用于跳转未授权页面")
    @RequestMapping("/403")
    public Result gotoUnauthorized(){
        Result result = new Result();
        result.setMsg("对不起，你没有访问该资源的权限！");
        result.setCode(403);
        return result;
    }

    @ApiOperation("账号登录")
    @PostMapping("/login")
    public Result login(@RequestBody Map<String, String> params){
        String username = params.get("username");
        String password = params.get("password");
        Result result = new Result();
        //获取主体对象
        Subject subject = SecurityUtils.getSubject();
        try {
            subject.login(new UsernamePasswordToken(username, password));
            result.setCode(0);
            result.setMsg("登录成功!");
            User user = userService.getUserByUserName(username);
            user.setPassword(null);//抹除密码
            //user.setRoles(userService.getRolesByUserName(username).getRoles());
            user.setRoles(approveService.getRolesByUserId(user.getId()));
            result.setObject(user);

        }catch (UnknownAccountException e){
            e.printStackTrace();
            result.setCode(1);
            result.setMsg("用户名错误!");
            System.out.println("用户名错误!");
        }catch (IncorrectCredentialsException e){
            e.printStackTrace();
            result.setCode(2);
            result.setMsg("密码错误!");
            System.out.println("密码错误!");
        }catch (Exception e){
            e.printStackTrace();
            result.setCode(3);
            result.setMsg(e.getMessage());
        }
        //User user = (User) SecurityUtils.getSubject().getPrincipal(); // 获取当前登录用户
        //return user;  //也可以返回用户信息
        return result;
    }
	
	@ApiOperation("jwt账号登录")
    @PostMapping("/loginJWT")
    public Result loginJWT(@RequestBody Map<String, String> params, HttpServletRequest request, HttpServletResponse response){
        String username = params.get("username");
        String password = params.get("password");
        Result result = new Result();
        //获取主体对象
        Subject subject = SecurityUtils.getSubject();
        try {
            subject.login(new UsernamePasswordToken(username, password));
            // 验证完密码后
            User user = userService.getUserByUserName(username);
            user.setPassword(null);//抹除密码
            //user.setRoles(userService.getRolesByUserName(username).getRoles());
            user.setRoles(approveService.getRolesByUserId(user.getId()));
            String IPAddress  = getIPAddress(request);
            String department = userService.getDepartmentNameByDepartmentId(user.getDepartment_id());
            String token = jwtTokenUtil.generateToken(username, IPAddress, department);
            CasJwtToken casJwtToken = new CasJwtToken();
            casJwtToken.setUsername(username);
            casJwtToken.setIPAddress(IPAddress);
            casJwtToken.setMode(CasJwtTokenMode.UserAccess);
            casJwtToken.setToken(token);
            subject.login(casJwtToken);
            user.setToken(token);
            result.setCode(0);
            result.setMsg("登录成功!");
            result.setObject(user);
            response.setHeader("content-type", "text/html;charset=utf-8");
            response.setHeader(Constant.TOKEN, token);
            //response.sendRedirect(successUrl + "?token=" + token);

        }catch (UnknownAccountException e){
            e.printStackTrace();
            result.setCode(1);
            result.setMsg("用户名错误!");
            System.out.println("用户名错误!");
        }catch (IncorrectCredentialsException e){
            e.printStackTrace();
            result.setCode(2);
            result.setMsg("密码错误!");
            System.out.println("密码错误!");
        }catch (Exception e){
            e.printStackTrace();
            result.setCode(3);
            result.setMsg(e.getMessage());
        }
        //User user = (User) SecurityUtils.getSubject().getPrincipal(); // 获取当前登录用户
        //return user;  //也可以返回用户信息
        return result;
    }
	

    @ApiOperation("账号退出")
    @GetMapping("/logout")
    public Result logout(){
        Result result = new Result();
        Subject subject = SecurityUtils.getSubject();
        subject.logout();
        result.setCode(0);
        result.setMsg("退出成功!");
        return result;
    }

    @ApiOperation("统一身份账号退出")
    @GetMapping("/casLogout")
    @ResponseBody
    public String casLogout(HttpServletResponse response){
        Subject subject = SecurityUtils.getSubject();
        subject.logout();
        response.setStatus(HttpServletResponse.SC_OK);
        return logoutUrl;
    }

    @ApiOperation("获取当前用户信息,若未登录则为null")
    @GetMapping("/getCurrentUserInfo")
    public User getCurrentUserInfo(){
        String username = (String) SecurityUtils.getSubject().getPrincipal();
        System.out.println(username);
        if(StringUtils.isBlank(username) || StringUtils.isEmpty(username)){
            return null;
        }
        User user = userService.getUserByUserName(username);
        if(ObjectUtils.isEmpty(user)){
            return null;
        }
        //user.setRoles(userService.getRolesByUserName(username).getRoles());
        user.setRoles(approveService.getRolesByUserId(user.getId()));
        user.setPassword(null);
        return user;
    }

    @ApiOperation("根据Token获取当前用户信息,若未登录则为null")
    @PostMapping("/getCurrentUserInfoByToken")
    public User getCurrentUserInfoByToken(@RequestBody Map<String, String> params){
        if(! params.containsKey("token")){
            return null;
        }
        String token = params.get("token");
        String username = null;
        try{
            username = jwtTokenUtil.getUsernameFromToken(token);
        }catch (Exception e){
            return null;
        }
        if(StringUtils.isBlank(username) || StringUtils.isEmpty(username)){
            return null;
        }
        User user = userService.getUserByUserName(username);
        if(ObjectUtils.isEmpty(user)){
            return null;
        }
        //user.setRoles(userService.getRolesByUserName(username).getRoles());
        user.setRoles(approveService.getRolesByUserId(user.getId()));
        user.setPassword(null);
        user.setToken(token);
        return user;
    }

    @ApiOperation(value = "没有登陆时触发的接口")
    @GetMapping("/needLogin")
    @CrossOrigin(allowCredentials = "true")
    public void needLogin(HttpServletRequest request, HttpServletResponse response) throws Exception {
        //302
        response.setStatus (HttpServletResponse.SC_MOVED_TEMPORARILY);
        response.setHeader ("Access-Control-Allow-Methods","POST,GET,PUT,OPTIONS,DELETE");
        response.setHeader ("Access-Control-Allow-Origin", "*");
        response.setHeader ("Access-Control-Allow-Credentials","true");
        response.setHeader ("Access-Control-Allow-Headers","Origin,content-Type,Authorization,xfilename,xfilecategory,xfilesize");
        if(org.apache.commons.lang3.StringUtils.equals(request.getMethod(),"OPTIONS")){
            response.setStatus(response.SC_OK);
            response.setHeader ("Access-Control-Allow-Headers","Origin,content-Type,Authorization,xfilename,xfilecategory,xfilesize");
        }
        response.setCharacterEncoding ("gbk");
        response.getWriter ().write ("You need login!");
        response.getWriter ().close ();
    }

    @ApiOperation(value = "没有权限时触发的接口")
    @GetMapping("/notPermit")
    @CrossOrigin(allowCredentials = "true")
    public void notPermit(HttpServletRequest request, HttpServletResponse response) throws Exception {
        //401
        response.setStatus (HttpServletResponse.SC_UNAUTHORIZED);
        response.setHeader ("Access-Control-Allow-Methods","*");
        response.setHeader ("Access-Control-Allow-Origin", "*");
        response.setHeader ("Access-Control-Allow-Credentials","true");
        response.setHeader ("Access-Control-Allow-Headers","Origin,content-Type,Authorization,xfilename,xfilecategory,xfilesize");
        if(org.apache.commons.lang3.StringUtils.equals(request.getMethod(),"OPTIONS")){
            response.setStatus(response.SC_OK);
            response.setHeader ("Access-Control-Allow-Headers","Origin,content-Type,Authorization,xfilename,xfilecategory,xfilesize");
        }
        response.setCharacterEncoding ("gbk");
        response.getWriter ().write ("You have no authorization");
        response.getWriter ().close ();
    }

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

}
