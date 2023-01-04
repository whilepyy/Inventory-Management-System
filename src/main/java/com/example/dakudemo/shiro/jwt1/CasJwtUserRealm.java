package com.example.dakudemo.shiro.jwt1;

import com.auth0.jwt.exceptions.JWTDecodeException;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.exceptions.TokenExpiredException;
import com.example.dakudemo.entity.Permission;
import com.example.dakudemo.entity.Role;
import com.example.dakudemo.entity.User;
import com.example.dakudemo.service.ApproveService;
import com.example.dakudemo.service.UserService;
import com.example.dakudemo.util.EncryptUtils;
import com.example.dakudemo.util.JwtTokenUtils;
import com.example.dakudemo.util.StringUtils;
import org.apache.shiro.authc.AuthenticationException;
import org.apache.shiro.authc.AuthenticationInfo;
import org.apache.shiro.authc.AuthenticationToken;
import org.apache.shiro.authc.SimpleAuthenticationInfo;
import org.apache.shiro.authz.AuthorizationInfo;
import org.apache.shiro.authz.SimpleAuthorizationInfo;

import org.apache.shiro.realm.AuthorizingRealm;
import org.apache.shiro.subject.PrincipalCollection;
import org.apache.shiro.subject.SimplePrincipalCollection;
import org.apache.shiro.util.CollectionUtils;
import org.jasig.cas.client.authentication.AttributePrincipal;
import org.jasig.cas.client.validation.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.ObjectUtils;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

// JWT模式专属的realm,改写自CasRealm
public class CasJwtUserRealm extends AuthorizingRealm{
    public static final String DEFAULT_REMEMBER_ME_ATTRIBUTE_NAME = "longTermAuthenticationRequestTokenUsed";
    public static final String DEFAULT_VALIDATION_PROTOCOL = "CAS";
    private static final Logger logger = LoggerFactory.getLogger(CasJwtUserRealm.class);
    private String casServerUrlPrefix;
    private String casService;
    private String validationProtocol = "CAS";
    private TicketValidator ticketValidator;
    private String defaultRoles;
    private String defaultPermissions;

    @Autowired
    private UserService userService;

    @Autowired
    private ApproveService approveService;

    @Autowired
    private JwtTokenUtils jwtTokenUtil;

    @Override
    public boolean supports(AuthenticationToken token) {
        //必须重写该方法才能支持我们自定义的CasJwtToken
        return token instanceof CasJwtToken;
    }

    {
        super.setName("user");//设置realm的名字，非常重要
    }




    protected void onInit() {
        super.onInit();
        this.ensureTicketValidator();
    }

    protected TicketValidator ensureTicketValidator() {
        if (this.ticketValidator == null) {
            this.ticketValidator = this.createTicketValidator();
        }

        return this.ticketValidator;
    }

    protected TicketValidator createTicketValidator() {
        String urlPrefix = this.getCasServerUrlPrefix();
        return (TicketValidator)("saml".equalsIgnoreCase(this.getValidationProtocol()) ? new Saml11TicketValidator(urlPrefix) : new Cas20ServiceTicketValidator(urlPrefix));
    }

    public String getCasServerUrlPrefix() {
        return this.casServerUrlPrefix;
    }

    public void setCasServerUrlPrefix(String casServerUrlPrefix) {
        this.casServerUrlPrefix = casServerUrlPrefix;
    }

    public String getCasService() {
        return this.casService;
    }

    public void setCasService(String casService) {
        this.casService = casService;
    }

    public String getValidationProtocol() {
        return this.validationProtocol;
    }

    public void setValidationProtocol(String validationProtocol) {
        this.validationProtocol = validationProtocol;
    }

    public String getDefaultRoles() {
        return this.defaultRoles;
    }

    public void setDefaultRoles(String defaultRoles) {
        this.defaultRoles = defaultRoles;
    }

    public String getDefaultPermissions() {
        return this.defaultPermissions;
    }

    public void setDefaultPermissions(String defaultPermissions) {
        this.defaultPermissions = defaultPermissions;
    }


    @Override
    protected AuthorizationInfo doGetAuthorizationInfo(PrincipalCollection principalCollection) {
        //获取当前登录输入的用户名，等价于(String) principalCollection.fromRealm(getName()).iterator().next();
        logger.info("xx==========进入授权领域, Realm:"+ this.getName() + "============xx");
        String loginName = (String)super.getAvailablePrincipal(principalCollection);
        System.out.println(loginName);
        //到数据库查是否有此对象
        //User user=userService.getRolesByUserName(loginName);// 实际项目中，这里可以根据实际情况做缓存，如果不做，Shiro自己也是有时间间隔机制，2分钟内不会重复执行该方法
        //System.out.println(user.getRoles());
        User user = userService.getUserByUserName(loginName);
        List<Role> roleList = approveService.getRolesByUserId(user.getId());
        user.setRoles(roleList);
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

    @Override
    protected AuthenticationInfo doGetAuthenticationInfo(AuthenticationToken token) throws AuthenticationException {
        if(ObjectUtils.isEmpty(token)){
            throw new AuthenticationException("Authentication token is null!");
//            return null;
        }

        CasJwtToken casJwtToken = (CasJwtToken) token;
        String ticket = casJwtToken.getTicket();
        String jwt = casJwtToken.getToken();
        String IPAddress = casJwtToken.getIPAddress();
        CasJwtTokenMode mode = casJwtToken.getMode();

        String username = null;
        String department = null;
        String displayName = null;
        Map<String, Object> attributes_last = new HashMap<>();//此处暂时置空
        // 注意：如果ticket为空,token不为空说明是访问资源;如果ticket不为空,而token为空那说明是CAS登录;否则出错
        if(ObjectUtils.isEmpty(mode)){
            throw new AuthenticationException("jwt mode is null!");
//            return null;
        }
        if(mode.equals(CasJwtTokenMode.CasLogin)){
            if(StringUtils.isEmpty(ticket) || StringUtils.isBlank(ticket)){
                throw new AuthenticationException("ticket is null or blank!");
//                return null;
            }
            // 进入Cas登录回调后的步骤:鉴别ticket、判断数据库是否存在接入用户、生成token
            TicketValidator ticketValidator = this.ensureTicketValidator();
            try{
                // 鉴别ticket
                Assertion casAssertion = ticketValidator.validate(ticket, this.getCasService());
                // 鉴别成功获取CAS返回的各种信息
                AttributePrincipal casPrincipal = casAssertion.getPrincipal();
                username = casPrincipal.getName();
                logger.debug("Validate ticket : {} in CAS server : {} to retrieve user : {}", ticket, this.getCasServerUrlPrefix(), username);
                Map<String, Object> attributes = casPrincipal.getAttributes();
                casJwtToken.setUsername(username);
                department = (String) attributes.get(Constant.DEPARTMENT);
                displayName = (String) attributes.get(Constant.DISPLAY_NAME);

                // 为用户创建一个jwt
                String clientIpAddress = (String) attributes.get(Constant.CLIENT_IP_ADDRESS);
                if(StringUtils.isEmpty(clientIpAddress) || StringUtils.isBlank(clientIpAddress)){
                    throw new AuthenticationException("CAS获取IP地址为null！");
                }
                String[] strArray = clientIpAddress.split(":");
                clientIpAddress = strArray[0];
                jwt = jwtTokenUtil.generateToken(username, clientIpAddress, department);
                //jwt = jwtTokenUtil.generateToken(username, IPAddress, department);
                casJwtToken.setToken(jwt);

            }catch (TicketValidationException e){
                // 鉴别异常
                throw new AuthenticationException(e.getMessage());
            }
        }else if(mode.equals(CasJwtTokenMode.UserAccess)){
            if(StringUtils.isEmpty(jwt) || StringUtils.isBlank(jwt)){
                throw new AuthenticationException("jwt is null!");
                //return null;
            }
            // 校验jw token
            try {
                System.out.println("-----------------------");
                System.out.println(jwt);
                String IPAddressJwt = jwtTokenUtil.getIPAddressToken(jwt);
                username = jwtTokenUtil.getUsernameFromToken(jwt);
                department = jwtTokenUtil.getDepartmentFromToken(jwt);
                //问题：这里由于前段给我发起请求，IPAddress并非用户IP
//                if(! IPAddressJwt.equals(IPAddress)){
////                    throw new LoginIPException("IP地址检查异常！");
//                    logger.error("JWT_IP:"+IPAddressJwt+ " IP:"+IPAddress);
//                    throw new LoginIPException("IP地址检查异常！");
//                }
            }catch (JWTDecodeException e){
                throw new JWTDecodeException("JWT解码错误！");
            }catch (TokenExpiredException e){
                throw new TokenExpiredException("JWT已经过期失效！");
            }catch (Exception e){
                throw new AuthenticationException(e.getMessage());
            }
//            catch (Exception e){
//                throw new AuthenticationException(e.getMessage());
//            }
        }else {
            // 运行错误
            throw new AuthenticationException("Authentication running error!");
//            return null;
        }
        // 判断数据库有无此用户
        User user = userService.getUserByUserName(username);
        if(ObjectUtils.isEmpty(user)){
            user = new User();
            user.setDisplayName(displayName);
            user.setUser_name(username);//使用Cas的唯一身份标识作为用户名
            String randPassword = EncryptUtils.getRandPassword();
            System.out.println("========生成的随机密码:" + randPassword+"=========");
            logger.info("id:" + user.getUser_name() + "  生成的随机密码:" + randPassword);
            /*此处可以告知用户随机密码，否则以后不可能知晓这个密码*/
            user.setPassword(EncryptUtils.encrypt(username, randPassword));
            userService.addDepartment(department);
            Integer department_id = userService.getDepartmentIdByDepartmentName(department);
            user.setDepartment_id(department_id);
            user.setDescription("CAS统一认证接入");
            if(!userService.addUser(user)){
                logger.error("******数据库读写异常:新用户注入失败！*******");
                throw new AuthenticationException("数据库读写异常:新用户注入失败！");
            }
            //为用户添加默认身份:user
            userService.addRoleForUserByUserName(user.getUser_name(), "user");
        }

        attributes_last.put(Constant.DEPARTMENT, department);
        attributes_last.put(Constant.TOKEN, jwt);
        user.setPassword(null);
        //user.setRoles(userService.getRolesByUserName(username).getRoles());
        user.setRoles(approveService.getRolesByUserId(user.getId()));
        List<Object> principals = CollectionUtils.asList(username, attributes_last);
        PrincipalCollection principalCollection = new SimplePrincipalCollection(principals, this.getName());
        String credentials = null;
        if(casJwtToken.getMode().equals(CasJwtTokenMode.CasLogin)){
            credentials = ticket;
        }else {
            credentials = jwt;
        }
//        SimpleAuthenticationInfo info = new SimpleAuthenticationInfo(principalCollection, )
        return new SimpleAuthenticationInfo(principalCollection, credentials);
    }
}
