package com.example.dakudemo.shiro.realm;

import com.example.dakudemo.entity.Permission;
import com.example.dakudemo.entity.User;
import com.example.dakudemo.service.ApproveService;
import com.example.dakudemo.service.UserService;

import com.example.dakudemo.shiro.jwt1.Constant;
import com.example.dakudemo.util.EncryptUtils;
import com.example.dakudemo.util.JsonCovertUtils;
import com.fasterxml.jackson.core.JsonProcessingException;
import lombok.extern.slf4j.Slf4j;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.AuthenticationException;
import org.apache.shiro.authc.AuthenticationInfo;
import org.apache.shiro.authc.AuthenticationToken;
import org.apache.shiro.authc.SimpleAuthenticationInfo;
import org.apache.shiro.authz.AuthorizationInfo;
import org.apache.shiro.authz.SimpleAuthorizationInfo;
import org.apache.shiro.cas.CasAuthenticationException;
import org.apache.shiro.cas.CasRealm;
import org.apache.shiro.cas.CasToken;
import org.apache.shiro.subject.PrincipalCollection;
import org.apache.shiro.subject.SimplePrincipalCollection;
import org.apache.shiro.util.CollectionUtils;
import org.apache.shiro.util.StringUtils;
import org.jasig.cas.client.authentication.AttributePrincipal;
import org.jasig.cas.client.validation.Assertion;
import org.jasig.cas.client.validation.TicketValidationException;
import org.jasig.cas.client.validation.TicketValidator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.ObjectUtils;

import javax.annotation.PostConstruct;
import java.util.List;
import java.util.Map;

/**
 * @author chh
 * @date 2022/2/26 12:53
 */
@Slf4j
public class MyShiroCasRealm extends CasRealm {

    private static final Logger logger = LoggerFactory.getLogger(MyShiroCasRealm.class);
    {
        super.setName("user");//设置realm的名字，非常重要
    }

    @Autowired
    private UserService userService;

    @Autowired
    private ApproveService approveService;

    @PostConstruct
    public void initProperty(){


//        setCasServerUrlPrefix(ShiroCasConfiguration.casServerUrlPrefix);
//        // 客户端回调地址
//        setCasService(ShiroCasConfiguration.shiroServerUrlPrefix + ShiroCasConfiguration.casFilterUrlPattern);


    }

    @Override
    protected AuthenticationInfo doGetAuthenticationInfo(AuthenticationToken token) throws AuthenticationException {
        CasToken casToken = (CasToken)token;
        if (token == null) {
            return null;
        } else {
            String ticket = (String)casToken.getCredentials();
            if (!StringUtils.hasText(ticket)) {
                return null;
            } else {
                TicketValidator ticketValidator = this.ensureTicketValidator();

                try {
                    Assertion casAssertion = ticketValidator.validate(ticket, this.getCasService());
                    AttributePrincipal casPrincipal = casAssertion.getPrincipal();
                    String userId = casPrincipal.getName();
                    logger.debug("Validate ticket : {} in CAS server : {} to retrieve user : {}", new Object[]{ticket, this.getCasServerUrlPrefix(), userId});
                    Map<String, Object> attributes = casPrincipal.getAttributes();
                    System.out.println(attributes);
                    casToken.setUserId(userId);
                    //=====================================
                    User user = userService.getUserByUserName(userId);
                    if(ObjectUtils.isEmpty(user)){
                        User newUser = new User();
                        newUser.setDisplayName((String) attributes.get(Constant.DISPLAY_NAME));
                        newUser.setUser_name(userId);//使用Cas的唯一身份标识作为用户名
                        String randPassword = EncryptUtils.getRandPassword();
                        System.out.println("========生成的随机密码:" + randPassword+"=========");
                        log.info("id:" + newUser.getUser_name() + "  生成的随机密码:" + randPassword);
                        /*此处可以告知用户随机密码，否则以后不可能知晓这个密码*/
                        newUser.setPassword(EncryptUtils.encrypt(userId, randPassword));
                        userService.addDepartment((String) attributes.get(Constant.DEPARTMENT));
                        Integer department_id = userService.getDepartmentIdByDepartmentName((String) attributes.get("department"));
                        newUser.setDepartment_id(department_id);
                        newUser.setDescription("CAS统一认证接入");
                        if(!userService.addUser(newUser)){
                            log.error("******数据库读写异常:新用户注入失败！*******");
                            throw new AuthenticationException("数据库读写异常:新用户注入失败！");
                        }
                        //为用户添加默认身份:user
                        userService.addRoleForUserByUserName(newUser.getUser_name(), "user");
                    }
                    //=====================================
                    String rememberMeAttributeName = this.getRememberMeAttributeName();
                    String rememberMeStringValue = (String)attributes.get(rememberMeAttributeName);
                    boolean isRemembered = rememberMeStringValue != null && Boolean.parseBoolean(rememberMeStringValue);
                    if (isRemembered) {
                        casToken.setRememberMe(true);
                    }
                    user.setPassword(null);
                    //user.setRoles(userService.getRolesByUserName(userId).getRoles());
                    user.setRoles(approveService.getRolesByUserId(user.getId()));
                    SecurityUtils.getSubject().getSession().setAttribute(Constant.SESSION_USER_INFO, JsonCovertUtils.objToJson(user));
                    List<Object> principals = CollectionUtils.asList(new Object[]{userId, attributes});
                    PrincipalCollection principalCollection = new SimplePrincipalCollection(principals, this.getName());
                    return new SimpleAuthenticationInfo(principalCollection, ticket);
                } catch (TicketValidationException | JsonProcessingException var14) {
                    throw new CasAuthenticationException("Unable to validate ticket [" + ticket + "]", var14);
                }
            }
        }
    }

    /**
     * 权限认证，为当前登录的Subject授予角色和权限
     * @see //经测试：本例中该方法的调用时机为需授权资源被访问时
     * @see //经测试：并且每次访问需授权资源时都会执行该方法中的逻辑，这表明本例中默认并未启用AuthorizationCache
     * @see //经测试：如果连续访问同一个URL（比如刷新），该方法不会被重复调用，Shiro有一个时间间隔（也就是cache时间，在ehcache-shiro.xml中配置），超过这个时间间隔再刷新页面，该方法会被执行
     */
    @Override
    protected AuthorizationInfo doGetAuthorizationInfo(PrincipalCollection principalCollection) {
        logger.info("##################执行Shiro权限认证##################");
        //获取当前登录输入的用户名，等价于(String) principalCollection.fromRealm(getName()).iterator().next();
        log.info("==========进入授权领域, Realm:"+ this.getName() + "============");
        String loginName = (String)super.getAvailablePrincipal(principalCollection);
        System.out.println(loginName);
        //到数据库查是否有此对象
        User user=userService.getUserByUserName(loginName);// 实际项目中，这里可以根据实际情况做缓存，如果不做，Shiro自己也是有时间间隔机制，2分钟内不会重复执行该方法
        user.setRoles(approveService.getRolesByUserId(user.getId()));
        System.out.println(user.getRoles());
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
        // 返回null的话，就会导致任何用户访问被拦截的请求时，都会自动跳转到unauthorizedUrl指定的地址
        return null;
    }

}