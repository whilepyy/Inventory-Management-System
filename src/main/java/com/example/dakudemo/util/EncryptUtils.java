package com.example.dakudemo.util;

import org.apache.shiro.crypto.hash.Md5Hash;

import java.util.Random;

/**
 * @author chh
 * @date 2022/2/17 10:40
 */
public class EncryptUtils {
    final static private Integer hashIterations = 64;//哈希迭代次数
    final static private char[] chars = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789{}()[]!@#$%".toCharArray();
    public static String encrypt(String username, String password){
        //此处暂时把username作为salt
        Md5Hash md5Hash = new Md5Hash(password, username, hashIterations);
        return md5Hash.toHex();
    }
    //生成随机8~16位密码
    public static String getRandPassword(){
        int len = getRandomInt(8, 16);
        StringBuilder sb = new StringBuilder();
        for(int i=0;i<len;i++){
            char aChar = (char) chars[getRandomInt(0, chars.length - 1)];
            sb.append(aChar);
        }
        return sb.toString();
    }

    public static void main(String[] args) {
        System.out.println(EncryptUtils.getRandPassword());
        System.out.println(EncryptUtils.encrypt("jyy", "123456"));
    }

    public static Integer getRandomInt(Integer min, Integer max){
        return new Random().nextInt((max - min) + 1) + min;
    }
}
