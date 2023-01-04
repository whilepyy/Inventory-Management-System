package com.example.dakudemo.shiro.realm;


import com.example.dakudemo.entity.Permission;
import com.example.dakudemo.entity.User;
import com.example.dakudemo.service.UserService;
import com.example.dakudemo.shiro.salt.MyByteSource;
import org.apache.shiro.authc.AuthenticationException;
import org.apache.shiro.authc.AuthenticationInfo;
import org.apache.shiro.authc.AuthenticationToken;
import org.apache.shiro.authc.SimpleAuthenticationInfo;
import org.apache.shiro.authz.AuthorizationInfo;
import org.apache.shiro.authz.SimpleAuthorizationInfo;
import org.apache.shiro.realm.AuthorizingRealm;
import org.apache.shiro.subject.PrincipalCollection;
import org.apache.shiro.util.ByteSource;
import org.apache.shiro.util.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.ObjectUtils;

import java.util.List;

/**
 * @author chh
 * @date 2022/2/4 18:23
 * 此类用于管理用户权限
 */
public class UserRealm extends AuthorizingRealm {

    {
        super.setName("admin");//设置realm的名字，非常重要
    }

    @Autowired
    UserService userService;

    //授权
    @Override
    protected AuthorizationInfo doGetAuthorizationInfo(PrincipalCollection principalCollection) {
        //获取身份信息
        String primaryPrincipal = (String) principalCollection.getPrimaryPrincipal();
        //获取用户角色
        User user = userService.getRolesByUserName(primaryPrincipal);
        if(!CollectionUtils.isEmpty(user.getRoles())){
            SimpleAuthorizationInfo simpleAuthorizationInfo = new SimpleAuthorizationInfo();
            user.getRoles().forEach(role -> {
                simpleAuthorizationInfo.addRole(role.getName());
                //角色对应权限
                List<Permission> permissionList = userService.getPermsByRoleId(role.getId());
                if(!CollectionUtils.isEmpty(permissionList)){
                    permissionList.forEach(permission -> {
                        simpleAuthorizationInfo.addStringPermission(permission.getName());
                    });
                }
            });
            return simpleAuthorizationInfo;
        }
        return null;
    }

    //认证
    @Override
    protected AuthenticationInfo doGetAuthenticationInfo(AuthenticationToken token) throws AuthenticationException {
        //通过token拿到用户名
        String principal = (String)token.getPrincipal();
        //查询数据库
        User user = userService.getUserByUserName(principal);
        //
        if(!ObjectUtils.isEmpty(user)){
            //由于salt这里不能序列化下面语句暂时弃用
            //return new SimpleAuthenticationInfo(user.getUser_name(), user.getPassword(), ByteSource.Util.bytes(user.getUser_name()), this.getName());
            return new SimpleAuthenticationInfo(user.getUser_name(), user.getPassword(), new MyByteSource(user.getUser_name()), this.getName());
        }
        return null;
    }
}
