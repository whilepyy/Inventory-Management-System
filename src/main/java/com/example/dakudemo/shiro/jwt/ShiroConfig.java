//package com.example.dakudemo.shiro.jwt;
//
//import com.example.dakudemo.entity.UrlFilter;
//import com.example.dakudemo.service.UserService;
//import com.example.dakudemo.shiro.cache.RedisCacheManager;
//import com.example.dakudemo.util.JwtTokenUtils;
//import lombok.extern.slf4j.Slf4j;
//import org.apache.shiro.SecurityUtils;
//import org.apache.shiro.cache.CacheManager;
//import org.apache.shiro.cache.MemoryConstrainedCacheManager;
//import org.apache.shiro.mgt.DefaultSessionStorageEvaluator;
//import org.apache.shiro.mgt.DefaultSubjectDAO;
//import org.apache.shiro.mgt.DefaultSubjectFactory;
//import org.apache.shiro.session.mgt.DefaultSessionManager;
//import org.apache.shiro.spring.security.interceptor.AuthorizationAttributeSourceAdvisor;
//import org.apache.shiro.spring.web.ShiroFilterFactoryBean;
//import org.apache.shiro.web.filter.authc.LogoutFilter;
//import org.apache.shiro.web.mgt.DefaultWebSecurityManager;
//import org.apache.shiro.web.mgt.DefaultWebSubjectFactory;
//import org.springframework.aop.framework.autoproxy.DefaultAdvisorAutoProxyCreator;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.beans.factory.annotation.Value;
//import org.springframework.boot.web.servlet.FilterRegistrationBean;
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
//import org.springframework.util.CollectionUtils;
//import org.springframework.web.filter.DelegatingFilterProxy;
//
//import javax.servlet.Filter;
//import java.util.LinkedHashMap;
//import java.util.List;
//import java.util.Map;
//
//@Slf4j
//@Configuration
//public class ShiroConfig {
//
//        /** CasServerUrlPrefix */
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
//    private String casCallback;
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
//    @Autowired
//    private JwtTokenUtils jwtTokenUtil;
//
//    @Autowired
//    private UserService userService;
//
//
//
//    @Bean(name = "userRealm")
//    public jwtUserRealm userRealm() {
//        jwtUserRealm userRealm = new jwtUserRealm();
//        userRealm.setCachingEnabled(true);
//        //userRealm.setCacheManager(new RedisCacheManager());
//        userRealm.setCachingEnabled(true);
//        userRealm.setAuthenticationCachingEnabled(false);
//        userRealm.setAuthorizationCachingEnabled(true);
//        //userRealm.setAuthenticationCacheName("CasAuthenticationCache");
//        userRealm.setAuthorizationCacheName("CasAuthorizationCache");
//        return userRealm;
//    }
//
//    @Bean
//    public FilterRegistrationBean delegatingFilterProxy() {
//        FilterRegistrationBean filterRegistrationBean = new FilterRegistrationBean();
//        DelegatingFilterProxy proxy = new DelegatingFilterProxy();
//        proxy.setTargetFilterLifecycle(true);
//        proxy.setTargetBeanName("shiroFilterFactoryBean");
//        filterRegistrationBean.setFilter(proxy);
//        return filterRegistrationBean;
//    }
//
//    @Bean(name = "shiroFilterFactoryBean")
//    public ShiroFilterFactoryBean shiroFilter(DefaultWebSecurityManager securityManager) {
//        ShiroFilterFactoryBean shiroFilter = new ShiroFilterFactoryBean();
//        //Shiro的核心安全接口,这个属性是必须的
//        shiroFilter.setLoginUrl("/login");
//        shiroFilter.setSecurityManager(securityManager);
//        Map<String, Filter> filters = new LinkedHashMap<>();
//        CasCallbackFilter casCallbackFilter = new CasCallbackFilter();
//        shiroFilter.setUnauthorizedUrl(unauthorizedUrl);
//
//        //casCallbackFilter.setCasServerUrlPrefix("https://nsrloa.ustc.edu.cn/cas");
//        //casCallbackFilter.setCasService("http://localhost:8989/cas/callback");
//        //casCallbackFilter.setCasService("http://202.38.77.133:8989/cas/callback");
//        //casJwtFilter.setRedirectUrl("https://nsrloa.ustc.edu.cn/cas/login?service=http://localhost:8989/cas/callback");
//
//
//        casCallbackFilter.setCasServerUrlPrefix(casServerUrlPrefix);
//        casCallbackFilter.setCasService(shiroServerUrlPrefix+casCallback);
//        casCallbackFilter.setJwtTokenUtil(jwtTokenUtil);
//        casCallbackFilter.setSuccessUrl(shiroServerUrlPrefix+successUrl);
//        filters.put("casCallback", casCallbackFilter);
//
//
//        CasJwtFilter casJwtFilter = new CasJwtFilter();
////        JwtFilter casJwtFilter = new JwtFilter();
//        casJwtFilter.setJwtTokenUtil(jwtTokenUtil);
//        casJwtFilter.setRedirectUrl(loginUrl);
//        filters.put("authc", casJwtFilter);
//
//        //设置登出拦截器
//        LogoutFilter logout = new LogoutFilter();
//        logout.setRedirectUrl(casLogoutUrl);
//        filters.put("logout", logout);
//
//
//        shiroFilter.setFilters(filters);
//        Map<String, String> filterMap = new LinkedHashMap<>();
//         /* 过滤链定义，从上向下顺序执行，一般将 / ** 放在最为下边;
//          authc:所有url都必须认证通过才可以访问; anon:所有url都都可以匿名访问 */
//
//
//        log.info("##################从数据库读取权限规则，加载到shiroFilter中##################");
//        List<UrlFilter> urlFilterList = userService.getAllUrlFilters();
//
//        System.out.println(urlFilterList);
//        if(!CollectionUtils.isEmpty(urlFilterList)){
//            urlFilterList.forEach(urlFilter -> {
//                if(urlFilter.getRoleName().equals("anon")) {
//                    //匿名访问单独使用角色控制
//                   filterMap.put(urlFilter.getUrl(), "anon");
////                }else if(urlFilter.getRoleName().startsWith("logout")){
////                    logoutFilter.set(urlFilter);
////                    System.out.println("设置默认退出");
////                    //filterChainDefinitionMap.put(urlFilter.getUrl(), "logout");
//                }else{
//                    //使用权限控制
//                    filterMap.put(urlFilter.getUrl(), "authc,perms["+urlFilter.getPermName()+"]");
//                }
//            });
//
//            log.info("========完成权限加载!=========");
//            //System.out.println("完成权限加载!");
//        }
//
//
//        //filterMap.put("/", "anon");
//
//        filterMap.put("/daku/**", "authc");
//
//        //filterMap.put("/error", "anon");
//        filterMap.put("/cas/callback", "casCallback");
//        filterMap.put("/logout", "logout,authc");
//        filterMap.put("/public/logout", "logout,authc");
//        filterMap.put("/public/**", "anon");
//        filterMap.put("/login", "authc");
//        filterMap.put("/**", "authc");
//
//
//        shiroFilter.setFilterChainDefinitionMap(filterMap);
//        return shiroFilter;
//    }
//
//    @Bean
//    public DefaultWebSubjectFactory subjectFactory(){
//        DefaultWebSubjectFactory subjectFactory = new StatelessDefaultSubjectFactory();
//        return subjectFactory;
//    }
//
//    @Bean
//    public DefaultWebSecurityManager securityManager(DefaultWebSubjectFactory subjectFactory, DefaultSessionManager sessionManager, jwtUserRealm userRealm) {
//        DefaultWebSecurityManager securityManager = new DefaultWebSecurityManager();
//        securityManager.setRealm(userRealm);
//        securityManager.setCacheManager(new RedisCacheManager());
//        // 替换默认的DefaultSubjectFactory，用于关闭session功能
//        securityManager.setSubjectFactory(subjectFactory);
//        securityManager.setSessionManager(sessionManager);
//        // 关闭session存储，禁用Session作为存储策略的实现，但它没有完全地禁用Session所以需要配合SubjectFactory中的context.setSessionCreationEnabled(false)
//        ((DefaultSessionStorageEvaluator) ((DefaultSubjectDAO)securityManager.getSubjectDAO()).getSessionStorageEvaluator()).setSessionStorageEnabled(false);
//        SecurityUtils.setSecurityManager(securityManager);
//        return securityManager;
//    }
//
//    @Bean
//    public DefaultSessionManager sessionManager(){
//        DefaultSessionManager sessionManager =new DefaultSessionManager();
//        // 关闭session定时检查，通过setSessionValidationSchedulerEnabled禁用掉会话调度器
//        sessionManager.setSessionValidationSchedulerEnabled(false);
//        return  sessionManager;
//    }
//
//    @Bean
//    public DefaultAdvisorAutoProxyCreator advisorAutoProxyCreator() {
//        DefaultAdvisorAutoProxyCreator advisorAutoProxyCreator = new DefaultAdvisorAutoProxyCreator();
//        advisorAutoProxyCreator.setProxyTargetClass(true);
//        return advisorAutoProxyCreator;
//    }
//
//    @Bean
//    public AuthorizationAttributeSourceAdvisor authorizationAttributeSourceAdvisor(DefaultWebSecurityManager securityManager) {
//        AuthorizationAttributeSourceAdvisor authorizationAttributeSourceAdvisor = new AuthorizationAttributeSourceAdvisor();
//        authorizationAttributeSourceAdvisor.setSecurityManager(securityManager);
//        return authorizationAttributeSourceAdvisor;
//    }
//
//}
