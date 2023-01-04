package com.example.dakudemo.shiro.session;

import com.example.dakudemo.shiro.jwt1.CasJwtToken;
import lombok.extern.slf4j.Slf4j;
import org.apache.shiro.authc.AuthenticationException;
import org.apache.shiro.authc.AuthenticationInfo;
import org.apache.shiro.authc.AuthenticationToken;
import org.apache.shiro.authc.UsernamePasswordToken;
import org.apache.shiro.authc.pam.ModularRealmAuthenticator;
import org.apache.shiro.cas.CasToken;
import org.apache.shiro.realm.Realm;

import java.util.ArrayList;
import java.util.Collection;

/**
 * @author chh
 * @date 2022/2/27 10:31
 */
@Slf4j
public class MyModularRealmAuthenticator extends ModularRealmAuthenticator {

    @Override
    protected AuthenticationInfo doAuthenticate(AuthenticationToken authenticationToken)
            throws AuthenticationException {
        // 判断getRealms()是否返回为空
        assertRealmsConfigured();
        String loginType = null;
        //判断token类型
//        if(authenticationToken instanceof CasToken) {
//            loginType = "user";
//        }
        if (authenticationToken instanceof CasJwtToken){
            loginType = "user";
        }else if(authenticationToken instanceof UsernamePasswordToken){
            loginType = "admin";
        }else{
            log.error(this.getClass().getName() + " does not support this class of authenticationToken:"+authenticationToken.getClass().toString());
            throw new ClassCastException(this.getClass().getName() + " does not support this class of authenticationToken:"+authenticationToken.getClass().toString());
        }

        // 所有Realm
        Collection<Realm> realms = getRealms();
        // 登录类型对应的所有Realm
        Collection<Realm> typeRealms = new ArrayList<>();
        for (Realm realm : realms) {
            if (realm.getName().equals(loginType))
                typeRealms.add(realm);
        }

        // 判断是单Realm还是多Realm
        if (typeRealms.size() == 1)
            return doSingleRealmAuthentication(typeRealms.iterator().next(), authenticationToken);
        else
            return doMultiRealmAuthentication(typeRealms, authenticationToken);
    }

}
