package com.example.dakudemo.controller;

import com.example.dakudemo.service.UserService;
import com.example.dakudemo.shiro.jwt1.Constant;
import io.swagger.annotations.ApiOperation;
import org.apache.shiro.SecurityUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.HashMap;

@Controller
@RequestMapping("/daku/test")
public class TestController {
    @Value("${cas.client.successUrl}")
    private String successUrl;


    @Autowired
    private UserService userService;


    @ApiOperation(value = "回调")
    @GetMapping("/cas/callback")
    @CrossOrigin(allowCredentials = "true")
    public String callback(HttpServletRequest request, HttpServletResponse response) throws Exception {
        String username = (String) SecurityUtils.getSubject().getPrincipal();
        HashMap<String, String> map = (HashMap<String, String>) SecurityUtils.getSubject().getPrincipals().asList().get(1);
        //System.out.println(map.get(Constant.TOKEN));
        response.setHeader("content-type", "text/html;charset=utf-8");
        response.setHeader(Constant.TOKEN, map.get(Constant.TOKEN));
        response.sendRedirect(successUrl + "?token=" + map.get(Constant.TOKEN));

        return username;
}}
