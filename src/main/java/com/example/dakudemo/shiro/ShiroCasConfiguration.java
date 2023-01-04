//package com.example.dakudemo.shiro;
//
//import com.example.dakudemo.entity.UrlFilter;
//import com.example.dakudemo.service.UserService;
//import com.example.dakudemo.shiro.cache.RedisCacheManager;
//import com.example.dakudemo.shiro.realm.MyShiroCasRealm;
//import com.example.dakudemo.shiro.realm.UserRealm;
//import com.example.dakudemo.shiro.session.*;
//import lombok.extern.slf4j.Slf4j;
//import org.apache.shiro.authc.credential.HashedCredentialsMatcher;
//import org.apache.shiro.cas.CasFilter;
//import org.apache.shiro.cas.CasSubjectFactory;
//import org.apache.shiro.realm.Realm;
//import org.apache.shiro.session.SessionListener;
//import org.apache.shiro.session.mgt.SessionManager;
//import org.apache.shiro.spring.LifecycleBeanPostProcessor;
//import org.apache.shiro.spring.security.interceptor.AuthorizationAttributeSourceAdvisor;
//import org.apache.shiro.spring.web.ShiroFilterFactoryBean;
//import org.apache.shiro.util.CollectionUtils;
//import org.apache.shiro.web.filter.authc.LogoutFilter;
//import org.apache.shiro.web.mgt.CookieRememberMeManager;
//import org.apache.shiro.web.mgt.DefaultWebSecurityManager;
//import org.apache.shiro.web.servlet.SimpleCookie;
//import org.springframework.aop.framework.autoproxy.DefaultAdvisorAutoProxyCreator;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.beans.factory.annotation.Value;
//import org.springframework.boot.web.servlet.FilterRegistrationBean;
//import org.springframework.context.annotation.Bean;
//
//import org.springframework.context.annotation.Configuration;
//import org.springframework.context.annotation.DependsOn;
//import org.springframework.stereotype.Component;
//import org.springframework.web.filter.DelegatingFilterProxy;
//
//import javax.servlet.Filter;
//import java.util.*;
//
///**
// * @author chh
// * @date 2022/2/26 12:51
// */
//
//@Slf4j
//@Component
//@Configuration
//public class ShiroCasConfiguration {
//
//    @Autowired
//    private UserService userService;
//
//    //private static final Logger logger = LoggerFactory.getLogger(ShiroCasConfiguration.class);
//
//     //CasServerUrlPrefix
////    public static final String casServerUrlPrefix = "https://nsrloa.ustc.edu.cn/cas";
////     //Cas登录页面地址
////    public static final String casLoginUrl = casServerUrlPrefix + "/login";
////     //Cas登出页面地址
////    public static final String casLogoutUrl = "https://nsrloa.ustc.edu.cn/sso/logout";
////     //当前工程对外提供的服务地址
////    public static final String shiroServerUrlPrefix = "http://localhost:8989";
////     //casFilter UrlPattern
////    public static final String casFilterUrlPattern = "/shiro-cas";
////     //登录地址
////    public static final String loginUrl = casLoginUrl + "?service=" + shiroServerUrlPrefix + casFilterUrlPattern;
////     //成功地址
////    public static final String successUrl = "/user";
////     //未授权地址
////    public static final String unauthorizedUrl = "/public/403";
//
//    /** CasServerUrlPrefix */
//    @Value("${cas.casServer.urlPrefix}")
//    private String casServerUrlPrefix;
//
//    /** Cas登录页面地址 */
//    @Value("${cas.casServer.loginUrl}")
//    private String casLoginUrl;
//
//    /** Cas登出页面地址 */
//    @Value("${cas.casServer.logoutUrl}")
//    private String casLogoutUrl;
//
//    /** 当前工程对外提供的服务地址 */
//    @Value("${cas.client.urlPrefix}")
//    private String shiroServerUrlPrefix;
//    /** casFilter UrlPattern */
//    @Value("${cas.client.casCallback}")
//    private String casFilterUrlPattern;
//    /** 登录的具体地址 */
//    @Value("${cas.casServer.loginRequestUrl}")
//    private String loginUrl;
//    /** 成功地址 */
//    @Value("${cas.client.successUrl}")
//    private String successUrl;
//    /** 未授权地址 */
//    @Value("${cas.client.unauthorizedUrl}")
//    private String unauthorizedUrl;
//
//    @Value("${cas.client.needLogin}")
//    private String needLogin;
//
//
////    @Bean(name = "cacheManage")
////    public EhCacheManager cacheManager() {
////        EhCacheManager em = new EhCacheManager();
////        em.setCacheManagerConfigFile("classpath:ehcache-shiro.xml");
////        return em;
////    }
//
//    @Bean("redisCacheManager")
//    public RedisCacheManager redisCacheManager(){
//        return new RedisCacheManager();
//    }
//
//    @Bean(name = "myShiroCasRealm")
//    public MyShiroCasRealm myShiroCasRealm(RedisCacheManager redisCacheManager) {
//        MyShiroCasRealm realm = new MyShiroCasRealm();
//        realm.setCacheManager(redisCacheManager);
//        realm.setCachingEnabled(true);
//        realm.setAuthenticationCachingEnabled(false);
//        realm.setAuthorizationCachingEnabled(true);
//        //realm.setAuthenticationCacheName("CasAuthenticationCache");
//        realm.setAuthorizationCacheName("CasAuthorizationCache");
//        realm.setCasServerUrlPrefix(casServerUrlPrefix);
//        realm.setCasService(shiroServerUrlPrefix + casFilterUrlPattern);
//        //realm.setDefaultRoles("user");
//        realm.setRememberMeAttributeName("true");
//        return realm;
//    }
//
//    @Bean(name = "userRealm")
//    public UserRealm userRealm(RedisCacheManager redisCacheManager){
//        UserRealm userRealm = new UserRealm();
//        //创建hash校验匹配器
//        HashedCredentialsMatcher credentialsMatcher = new HashedCredentialsMatcher();
//        //设置散列算法
//        credentialsMatcher.setHashAlgorithmName("MD5");
//        //设置散列次数
//        credentialsMatcher.setHashIterations(64);
//        userRealm.setCredentialsMatcher(credentialsMatcher);
//
//
//        userRealm.setCacheManager(redisCacheManager);
//        userRealm.setCachingEnabled(true);
//        userRealm.setAuthenticationCachingEnabled(true);
//        userRealm.setAuthorizationCachingEnabled(true);
//        userRealm.setAuthenticationCacheName("authenticationCache");
//        userRealm.setAuthorizationCacheName("authorizationCache");
//        return userRealm;
//    }
//
//    /**
//     * 注册DelegatingFilterProxy（Shiro）
//     *
//     */
//    @Bean
//    public FilterRegistrationBean filterRegistrationBean() {
//        FilterRegistrationBean filterRegistration = new FilterRegistrationBean();
//        filterRegistration.setFilter(new DelegatingFilterProxy("shiroFilterFactoryBean"));
//        //  该值缺省为false,表示生命周期由SpringApplicationContext管理,设置为true则表示由ServletContainer管理
//        filterRegistration.addInitParameter("targetFilterLifecycle", "true");
//        filterRegistration.setEnabled(true);
//        filterRegistration.addUrlPatterns("/*");
//        return filterRegistration;
//    }
//
//
//    @Bean(name = "securityManager")
//    public DefaultWebSecurityManager getDefaultWebSecurityManager(MyShiroCasRealm myShiroCasRealm, UserRealm userRealm, RedisSessionDao redisSessionDao) {
//        DefaultWebSecurityManager dwsm = new DefaultWebSecurityManager();
//
////      <!-- 用户授权/认证信息Cache, 采用EhCache 缓存 -->
//        //dwsm.setCacheManager(cacheManager());
//        //dwsm.setCacheManager(redisCacheManager());
//        // 指定 SubjectFactory
//        dwsm.setSubjectFactory(new CasSubjectFactory());
//        //注入记住我管理器;
//        dwsm.setRememberMeManager(rememberMeManager());
//        // 自定义session管理 使用redis
//        dwsm.setSessionManager(sessionManager(sessionListener(),redisSessionDao));
//
//
//        //设置多realm
//        List<Realm> realms = new ArrayList<>();
//        realms.add(myShiroCasRealm);
//        realms.add(userRealm);
//
//        MyModularRealmAuthorizer authorizer = new MyModularRealmAuthorizer();
//        authorizer.setRealms(realms);
//        dwsm.setAuthorizer(authorizer);
//
//        MyModularRealmAuthenticator authenticator = new MyModularRealmAuthenticator();
//        authenticator.setRealms(realms);
//        dwsm.setAuthenticator(authenticator);
//        //dwsm.setRealm(myShiroCasRealm);
//        return dwsm;
//    }
//
//
//    /**
//     * 加载shiroFilter权限控制规则（从数据库读取然后配置）
//     *
//     */
//    private void loadShiroFilterChain(ShiroFilterFactoryBean shiroFilterFactoryBean){
//        /////////////////////// 下面这些规则配置最好配置到配置文件中 ///////////////////////
//        Map<String, String> filterChainDefinitionMap = new LinkedHashMap<String, String>();
//
//        filterChainDefinitionMap.put(casFilterUrlPattern, "casFilter");// shiro集成cas后，首先添加该规则
//
//        // authc：该过滤器下的页面必须验证后才能访问，它是Shiro内置的一个拦截器org.apache.shiro.web.filter.authc.FormAuthenticationFilter
//
//        // anon：它对应的过滤器里面是空的,什么都没做
//        log.info("##################从数据库读取权限规则，加载到shiroFilter中##################");
//        List<UrlFilter> urlFilterList = userService.getAllUrlFilters();
//
//        System.out.println(urlFilterList);
//        if(!CollectionUtils.isEmpty(urlFilterList)){
//            urlFilterList.forEach(urlFilter -> {
//                if(urlFilter.getRoleName().equals("anon")) {
//                    //匿名访问单独使用角色控制
//                    filterChainDefinitionMap.put(urlFilter.getUrl(), "anon");
////                }else if(urlFilter.getRoleName().startsWith("logout")){
////                    logoutFilter.set(urlFilter);
////                    System.out.println("设置默认退出");
////                    //filterChainDefinitionMap.put(urlFilter.getUrl(), "logout");
//                }else{
//                    //使用权限控制
//                    filterChainDefinitionMap.put(urlFilter.getUrl(), "authc,perms["+urlFilter.getPermName()+"]");
//                }
//            });
//
//            log.info("========完成权限加载!=========");
//            //System.out.println("完成权限加载!");
//        }
//        //filterChainDefinitionMap.put("/daku/**", "authc");
////        filterChainDefinitionMap.put("/daku/public/casLogout", "logout");
////        filterChainDefinitionMap.put("/daku/public/logout", "logout");
//        filterChainDefinitionMap.put("/public/**", "anon");
//        filterChainDefinitionMap.put("/user", "anon");
//
//
//
//        //filterChainDefinitionMap.put("/user/edit/**", "authc,perms[user:edit]");// 这里为了测试，固定写死的值，也可以从数据库或其他配置中读取
//
//        filterChainDefinitionMap.put("/login", "casFilter");
//        //filterChainDefinitionMap.put("/**", "anon");//anon 可以理解为不拦截
//
//        shiroFilterFactoryBean.setFilterChainDefinitionMap(filterChainDefinitionMap);
//    }
//
//    /**
//     * CAS过滤器
//     * @return
//     */
//    @Bean(name = "casFilter")
//    public CasFilter getCasFilter() {
//        CasFilter casFilter = new CasFilter();
//        casFilter.setName("casFilter");
//        casFilter.setEnabled(true);
//        casFilter.setSuccessUrl(successUrl);
//        // 登录失败后跳转的URL，也就是 Shiro 执行 CasRealm 的 doGetAuthenticationInfo 方法向CasServer验证tiket
//        casFilter.setFailureUrl(loginUrl);// 我们选择认证失败后再打开登录页面
//        return casFilter;
//    }
//
//    /**
//     * ShiroFilter<br/>
//     * 注意这里参数中的 StudentService 和 IScoreDao 只是一个例子，因为我们在这里可以用这样的方式获取到相关访问数据库的对象，
//     * 然后读取数据库相关配置，配置到 shiroFilterFactoryBean 的访问规则中。实际项目中，请使用自己的Service来处理业务逻辑。
//     *
//     */
//    @Bean(name = "shiroFilterFactoryBean")
//    public ShiroFilterFactoryBean getShiroFilterFactoryBean(DefaultWebSecurityManager securityManager, CasFilter casFilter) {
//        ShiroFilterFactoryBean shiroFilterFactoryBean = new ShiroFilterFactoryBean();
//        // 必须设置 SecurityManager
//        shiroFilterFactoryBean.setSecurityManager(securityManager);
//        // 如果不设置默认会自动寻找Web工程根目录下的"/login.jsp"页面
//        shiroFilterFactoryBean.setLoginUrl(needLogin);
//        // 登录成功后要跳转的连接
//        shiroFilterFactoryBean.setSuccessUrl(successUrl);
//        shiroFilterFactoryBean.setUnauthorizedUrl(unauthorizedUrl);
//        //设置登出拦截器
//        LogoutFilter logout = new LogoutFilter();
//        //logout.setRedirectUrl(casLogoutUrl);
//        // 添加casFilter到shiroFilter中
//        Map<String, Filter> filters = new HashMap<>();
//        filters.put("casFilter", casFilter);
//        filters.put("logout", logout);
//        shiroFilterFactoryBean.setFilters(filters);
//        loadShiroFilterChain(shiroFilterFactoryBean);
//        return shiroFilterFactoryBean;
//    }
//
//
//    /**
//     * cookie对象;
//     * @return
//     */
//    @Bean
//    public SimpleCookie rememberMeCookie(){
//        //这个参数是cookie的名称，对应前端的checkbox的name = rememberMe
//        SimpleCookie simpleCookie = new SimpleCookie("rememberMe");
//        //<!-- 记住我cookie生效时间30天 ,单位秒;-->
//        simpleCookie.setMaxAge(2592000);
//        return simpleCookie;
//    }
//
//    /**
//     * cookie管理对象;记住我功能
//     * @return
//     */
//    @Bean
//    public CookieRememberMeManager rememberMeManager(){
//        CookieRememberMeManager cookieRememberMeManager = new CookieRememberMeManager();
//        cookieRememberMeManager.setCookie(rememberMeCookie());
//        return cookieRememberMeManager;
//    }
//
//    @Bean
//    public SessionManager sessionManager(SessionListener sessionListener, RedisSessionDao redisSessionDao){
//        ShiroSessionManager sessionManager = new ShiroSessionManager();
//        sessionManager.setGlobalSessionTimeout(1800*1000);
//        sessionManager.setDeleteInvalidSessions(false);
//        sessionManager.setSessionValidationSchedulerEnabled(false);
//        sessionManager.setSessionValidationInterval(5000);
//        sessionManager.setSessionFactory(new ShiroSessonFactory());
//        sessionManager.setSessionDAO(redisSessionDao);
//        List<SessionListener> sessionListeners = new ArrayList<>();
//        sessionListeners.add(sessionListener);
//        sessionManager.setSessionListeners(sessionListeners);
//        return sessionManager;
//    }
//
//    @Bean("sessionLisener")
//    public SessionListener sessionListener(){
//        return new ShiroSessionListener();
//    }
//
//    @Bean("redisSessionDao")
//    public RedisSessionDao redisSessionDao(){
//        return new RedisSessionDao();
//    }
//
//    /**
//     * 下面的代码是添加注解支持
//     */
//    @Bean
//    @DependsOn("lifecycleBeanPostProcessor")
//    public DefaultAdvisorAutoProxyCreator defaultAdvisorAutoProxyCreator() {
//        DefaultAdvisorAutoProxyCreator defaultAdvisorAutoProxyCreator = new DefaultAdvisorAutoProxyCreator();
//        // 强制使用cglib，防止重复代理和可能引起代理出错的问题
//        // https://zhuanlan.zhihu.com/p/29161098
//        defaultAdvisorAutoProxyCreator.setProxyTargetClass(true);
//        return defaultAdvisorAutoProxyCreator;
//    }
//
//    @Bean
//    public static LifecycleBeanPostProcessor lifecycleBeanPostProcessor() {
//        return new LifecycleBeanPostProcessor();
//    }
//
//    @Bean
//    public AuthorizationAttributeSourceAdvisor authorizationAttributeSourceAdvisor(DefaultWebSecurityManager securityManager) {
//        AuthorizationAttributeSourceAdvisor advisor = new AuthorizationAttributeSourceAdvisor();
//        advisor.setSecurityManager(securityManager);
//        return advisor;
//    }
//
//}