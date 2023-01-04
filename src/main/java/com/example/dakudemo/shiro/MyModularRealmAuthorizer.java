package com.example.dakudemo.shiro;

import com.example.dakudemo.shiro.realm.MyShiroCasRealm;
import com.example.dakudemo.shiro.realm.UserRealm;
import org.apache.shiro.authc.AuthenticationException;
import org.apache.shiro.authc.AuthenticationInfo;
import org.apache.shiro.authc.AuthenticationToken;
import com.example.dakudemo.shiro.jwt1.CasJwtUserRealm;
import org.apache.shiro.authz.Authorizer;
import org.apache.shiro.authz.ModularRealmAuthorizer;
import org.apache.shiro.authz.Permission;
import org.apache.shiro.realm.Realm;
import org.apache.shiro.subject.PrincipalCollection;

import java.util.Set;

/**
 * @author chh
 * @date 2022/2/27 10:00
 */
public class MyModularRealmAuthorizer extends ModularRealmAuthorizer {
    @Override
    public boolean isPermitted(PrincipalCollection principals, String permission) {
        assertRealmsConfigured();
        Set<String> realmNames = principals.getRealmNames();
        //获取realm的名字
        String realmName = realmNames.iterator().next();
        for (Realm realm : getRealms()) {
            if (!(realm instanceof Authorizer)) continue;
            //匹配名字
            if(realmName.equals("admin")) {
                if (realm instanceof UserRealm) {
                    return ((UserRealm) realm).isPermitted(principals, permission);
                }
            }
            if(realmName.equals("user")) {
//                if (realm instanceof MyShiroCasRealm) {
//                    return ((MyShiroCasRealm) realm).isPermitted(principals, permission);
//                }
                if (realm instanceof CasJwtUserRealm) {
                    return ((CasJwtUserRealm) realm).isPermitted(principals, permission);
                }
            }
        }
        return false;
    }

    @Override
    public boolean isPermitted(PrincipalCollection principals, Permission permission) {
        assertRealmsConfigured();
        Set<String> realmNames = principals.getRealmNames();
        //获取realm的名字
        String realmName = realmNames.iterator().next();
        for (Realm realm : getRealms()) {
            if (!(realm instanceof Authorizer)) continue;
            //匹配名字
            if(realmName.equals("admin")) {
                if (realm instanceof UserRealm) {
                    return ((UserRealm) realm).isPermitted(principals, permission);
                }
            }
            //匹配名字
            if(realmName.equals("user")) {
//                if (realm instanceof MyShiroCasRealm) {
//                    return ((MyShiroCasRealm) realm).isPermitted(principals, permission);
//                }
                if (realm instanceof CasJwtUserRealm) {
                    return ((CasJwtUserRealm) realm).isPermitted(principals, permission);
                }
            }
        }
        return false;    }

    @Override
    public boolean hasRole(PrincipalCollection principals, String roleIdentifier) {
        assertRealmsConfigured();
        Set<String> realmNames = principals.getRealmNames();
        //获取realm的名字
        String realmName = realmNames.iterator().next();
        for (Realm realm : getRealms()) {
            if (!(realm instanceof Authorizer)) continue;
            //匹配名字
            if(realmName.equals("admin")) {
                if (realm instanceof UserRealm) {
                    return ((UserRealm) realm).hasRole(principals, roleIdentifier);
                }
            }
            //匹配名字
            if(realmName.equals("user")) {
//                if (realm instanceof MyShiroCasRealm) {
//                    return ((MyShiroCasRealm) realm).hasRole(principals, roleIdentifier);
//                }
                if (realm instanceof CasJwtUserRealm) {
                    return ((CasJwtUserRealm) realm).hasRole(principals, roleIdentifier);
                }
            }
        }
        return false;
    }


}
