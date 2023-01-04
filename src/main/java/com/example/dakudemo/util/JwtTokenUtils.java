package com.example.dakudemo.util;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTCreationException;
import com.auth0.jwt.exceptions.JWTDecodeException;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.exceptions.TokenExpiredException;
import com.auth0.jwt.interfaces.DecodedJWT;
import org.apache.commons.lang3.time.DateUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.util.ObjectUtils;


import java.util.Date;

@Component
public class JwtTokenUtils {
    @Value("${jwt.token.secret}")
    private String secret;

    @Value("${jwt.token.expiration}")
    private Long expiration;

    @Value("${jwt.token.issuer}")
    private String issuer;

    public String getSecret() {
        return secret;
    }

    public void setSecret(String secret) {
        this.secret = secret;
    }

    public Long getExpiration() {
        return expiration;
    }

    public void setExpiration(Long expiration) {
        this.expiration = expiration;
    }

    public String getIssuer() {
        return issuer;
    }

    public void setIssuer(String issuer) {
        this.issuer = issuer;
    }



    /**
     * 生成token
     * @param username 用户名
     * @param
     * @return
     */
    public String generateToken(String username, String IPAddress, String department) {
        String token = null;
        try {
            Algorithm algorithm = Algorithm.HMAC256(secret);
            Date date = new Date();
            token = JWT.create()
                    .withIssuer(issuer)
                    .withIssuedAt(date)
                    .withExpiresAt(DateUtils.addMilliseconds(date, expiration.intValue()))
                    .withClaim("username",username)
                    .withClaim("IPAddress",IPAddress)
                    .withClaim("department",department)
                    .sign(algorithm);
        }catch (JWTCreationException e){
            e.printStackTrace();
        }
        return token;
    }



//    /**
//     * 通过spring-mobile-device的device检测访问主体
//     * @param device
//     * @return
//     */
//    private String generateAudience(Device device) {
//        String audience = AUDIENCE_UNKNOWN;
//        if (device.isNormal()) {
//            audience = AUDIENCE_WEB;//PC端
//        } else if (device.isTablet()) {
//            audience = AUDIENCE_TABLET;//平板
//        } else if (device.isMobile()) {
//            audience = AUDIENCE_MOBILE;//手机
//        }
//        return audience;
//    }

    /**
     * 根据token获取用户名
     * @param token
     * @return
     */
    public String getUsernameFromToken(String token) {
        String username = null;
        DecodedJWT jwt = verifyToken(token);
        if(ObjectUtils.isEmpty(jwt)){
            return null;
        }
        username = jwt.getClaim("username").asString();
        return username;
    }

    /**
     * 根据token获取用户名
     * @param token
     * @return
     */
    public String getIPAddressToken(String token) {
        String IPAddress = null;
        DecodedJWT jwt = verifyToken(token);
        if(ObjectUtils.isEmpty(jwt)){
            return null;
        }
        IPAddress = jwt.getClaim("IPAddress").asString();
        return IPAddress;
    }

    public String getDepartmentFromToken(String token) {
        String department = null;
        DecodedJWT jwt = verifyToken(token);
        if(ObjectUtils.isEmpty(jwt)){
            return null;
        }
        department  = jwt.getClaim("department").asString();
        return department ;
    }

    /**
     * verify token返回一个verifier
     * @param token
     * @return
     */
    public DecodedJWT verifyToken(String token){
        DecodedJWT jwt = null;
        try {
            Algorithm algorithm = Algorithm.HMAC256(secret);
            JWTVerifier verifier = JWT.require(algorithm)
                    .withIssuer(issuer)
                    .build();
            jwt = verifier.verify(token);
        } catch (TokenExpiredException e) {
            throw new TokenExpiredException("JWT过期");
        } catch (JWTDecodeException e){
            throw new JWTDecodeException("JWT不合法");
        }
        return jwt;
    }

    /**
     * 判断token失效时间是否到了
     * @param token
     * @return
     */
    public Boolean isTokenExpired(String token) {
        try {
            DecodedJWT jwt = verifyToken(token);
        } catch (TokenExpiredException e) {
            return true;
        }
        return false;
    }

    /**
     * 获取设置的token失效时间
     * @param token
     * @return
     */
    public Date getExpirationDateFromToken(String token) {
        Date expiration;
        try {
            if(isTokenExpired(token)){
                return null;
            }
            DecodedJWT jwt = verifyToken(token);
            expiration = jwt.getExpiresAt();
        } catch (TokenExpiredException e) {
            return null;
        }
        return expiration;
    }

    // /**
    //  * Token失效校验
    //  * @param token token字符串
    //  * @param loginInfo 用户信息
    //  * @return
    //  */
    // public Boolean validateToken(String token, LoginInfo loginInfo) {
    //     final String username = getUsernameFromToken(token);
    //     return (
    //             username.equals(loginInfo.getUsername())
    //                     && !isTokenExpired(token));
    // }

    public String refreshToken(String token) {
        DecodedJWT jwt = verifyToken(token);
        String username = jwt.getClaim("username").asString();
        String IPAddress = jwt.getClaim("IPAddress").asString();
        String department = jwt.getClaim("department").asString();
        return generateToken(username, IPAddress, department);
    }
}
