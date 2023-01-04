package com.example.dakudemo.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

/*
 * @author:chh
 * @Date:2022-05-28-13:35
 * @Description:将JWT的exception统一
 */
//@ResponseStatus(code = HttpStatus.FOUND, reason = "JWT过期或者不合法！")
public class JWTException extends Exception {
    public JWTException() {
        super();
    }
    public JWTException(String message) {
        super(message);
    }
}
