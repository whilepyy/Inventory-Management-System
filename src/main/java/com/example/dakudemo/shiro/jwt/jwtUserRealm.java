//package com.example.dakudemo.shiro.jwt;
//
//import com.example.dakudemo.entity.Permission;
//import com.example.dakudemo.entity.User;
//import com.example.dakudemo.service.UserService;
//import com.example.dakudemo.shiro.jwt1.Constant;
//import com.example.dakudemo.util.EncryptUtils;
//import com.example.dakudemo.util.JwtTokenUtils;
//import lombok.extern.slf4j.Slf4j;
//import org.apache.shiro.authc.AuthenticationException;
//import org.apache.shiro.authc.AuthenticationInfo;
//import org.apache.shiro.authc.AuthenticationToken;
//import org.apache.shiro.authc.SimpleAuthenticationInfo;
//import org.apache.shiro.authz.AuthorizationInfo;
//import org.apache.shiro.authz.SimpleAuthorizationInfo;
//import org.apache.shiro.realm.AuthorizingRealm;
//import org.apache.shiro.subject.PrincipalCollection;
//import org.apache.shiro.subject.SimplePrincipalCollection;
//import org.apache.shiro.util.CollectionUtils;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.util.ObjectUtils;
//
//import java.util.HashMap;
//import java.util.List;
//import java.util.Map;
//
//@Slf4j
//public class jwtUserRealm extends AuthorizingRealm {
//
//    @Autowired
//    private JwtTokenUtils jwtTokenUtil;
//
//    @Autowired
//    private UserService userService;
//
//    @Override
//    public boolean supports(AuthenticationToken token) {
//        //表示此Realm只支持JwtToken类型
//        return token instanceof JwtToken;
//    }
//
//    {
//        super.setName("user");//设置realm的名字，非常重要
//    }
//
//
//    @Override
//    protected AuthorizationInfo doGetAuthorizationInfo(PrincipalCollection principalCollection) {
//        //获取当前登录输入的用户名，等价于(String) principalCollection.fromRealm(getName()).iterator().next();
//        log.info("==========进入授权领域, Realm:"+ this.getName() + "============");
//        String loginName = (String)super.getAvailablePrincipal(principalCollection);
//        System.out.println(loginName);
//        //到数据库查是否有此对象
//        User user=userService.getRolesByUserName(loginName);// 实际项目中，这里可以根据实际情况做缓存，如果不做，Shiro自己也是有时间间隔机制，2分钟内不会重复执行该方法
//        System.out.println(user.getRoles());
//        if(!CollectionUtils.isEmpty(user.getRoles())){
//            SimpleAuthorizationInfo simpleAuthorizationInfo = new SimpleAuthorizationInfo();
//            user.getRoles().forEach(role -> {
//                simpleAuthorizationInfo.addRole(role.getName());
//                //角色对应权限
//                List<Permission> permissionList = userService.getPermsByRoleId(role.getId());
//                if(!CollectionUtils.isEmpty(permissionList)){
//                    permissionList.forEach(permission -> {
//                        simpleAuthorizationInfo.addStringPermission(permission.getName());
//                    });
//                }
//            });
//            return simpleAuthorizationInfo;
//        }
//        // 返回null的话，就会导致任何用户访问被拦截的请求时，都会自动跳转到unauthorizedUrl指定的地址
//        return null;
//    }
//
//    @Override
//    protected AuthenticationInfo doGetAuthenticationInfo(AuthenticationToken authenticationToken) throws AuthenticationException {
//        log.info("==========进入认证领域, Realm:"+ this.getName() + "============");
//        JwtToken jwtToken = (JwtToken) authenticationToken;
//        String token = jwtToken.getToken();
//
//        if(ObjectUtils.isEmpty(token)){
//            return null;
//        }
//        String username = jwtTokenUtil.getUsernameFromToken(token);
//        String department = jwtTokenUtil.getDepartmentFromToken(token);
//        System.out.println(token);
//
//
//        User user = userService.getUserByUserName(username);
//
//        if(ObjectUtils.isEmpty(user)){
//            User newUser = new User();
//            newUser.setDisplayName(username);
//            newUser.setUser_name(username);//使用Cas的唯一身份标识作为用户名
//            String randPassword = EncryptUtils.getRandPassword();
//            System.out.println("========生成的随机密码:" + randPassword+"=========");
//            log.info("id:" + newUser.getUser_name() + "  生成的随机密码:" + randPassword);
//            /*此处可以告知用户随机密码，否则以后不可能知晓这个密码*/
//            newUser.setPassword(EncryptUtils.encrypt(username, randPassword));
//            userService.addDepartment(department);
//            Integer department_id = userService.getDepartmentIdByDepartmentName(department);
//            newUser.setDepartment_id(department_id);
//            newUser.setDescription("CAS统一认证接入");
//            if(!userService.addUser(newUser)){
//                log.error("******数据库读写异常:新用户注入失败！*******");
//                throw new AuthenticationException("数据库读写异常:新用户注入失败！");
//            }
//            //为用户添加默认身份:user
//            userService.addRoleForUserByUserName(newUser.getUser_name(), "user");
//        }
//        user.setPassword(null);
//        user.setRoles(userService.getRolesByUserName(username).getRoles());
//        //SecurityUtils.getSubject().getSession().setAttribute("user", user.getUser_name());
////        try {
////            SecurityUtils.getSubject().getSession().setAttribute(Constant.SESSION_USER_INFO, JsonCovertUtils.objToJson(user));
////        } catch (JsonProcessingException e) {
////            e.printStackTrace();
////        }
//        Map<String, Object> attributes = new HashMap<>();//此处暂时置空
//        attributes.put(Constant.DEPARTMENT, department);
//        List<Object> principals = CollectionUtils.asList(username, attributes);
//        PrincipalCollection principalCollection = new SimplePrincipalCollection(principals, this.getName());
//        return new SimpleAuthenticationInfo(principalCollection, token);
//    }
//}
