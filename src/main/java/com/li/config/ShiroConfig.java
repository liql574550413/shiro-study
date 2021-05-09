package com.li.config;


import at.pollux.thymeleaf.shiro.dialect.ShiroDialect;
import org.apache.shiro.spring.web.ShiroFilterFactoryBean;
import org.apache.shiro.web.mgt.DefaultWebSecurityManager;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * @author liql
 * @date 2021/4/13
 * Subject：主体，代表了当前 “用户”，这个用户不一定是一个具体的人，与当前应用交互的任何东西都是 Subject，如网络爬虫，机器人等；
 * 即一个抽象概念；所有 Subject 都绑定到 SecurityManager，与 Subject 的所有交互都会委托给 SecurityManager；
 * 可以把 Subject 认为是一个门面；SecurityManager 才是实际的执行者；
 *
 * SecurityManager：安全管理器；即所有与安全有关的操作都会与 SecurityManager 交互；且它管理着所有 Subject；
 * 可以看出它是 Shiro 的核心，它负责与后边介绍的其他组件进行交互，如果学习过 SpringMVC，你可以把它看成 DispatcherServlet 前端控制器；
 *
 * Realm：域，Shiro 从从 Realm 获取安全数据（如用户、角色、权限），就是说 SecurityManager 要验证用户身份，
 * 那么它需要从 Realm 获取相应的用户进行比较以确定用户身份是否合法；也需要从 Realm 得到用户相应的角色 / 权限进行验证用户是否能进行操作；
 * 可以把 Realm 看成 DataSource，即安全数据源。
 */
@Configuration
public class ShiroConfig {
    //围绕着三个写 分别是 Subject ,  SecurityManager ,Realm

    //获取自定义Realm，并将其存入spring容器
    @Bean(name = "getRealm")
    public UserRealm getRealm(){
        return new UserRealm();
    }

    /** 这部分代码是死的  不用强记
     * 获取 DefaultSecurityManager
     * 把 getRealm() 当成参数 传给该方法，这样就让这两个方法绑定起来了
     * @param realm
     * @return
     */
    @Bean(name = "securityManager")
    public DefaultWebSecurityManager getDefaultWebSecurityManager(@Qualifier("getRealm")UserRealm realm){
        DefaultWebSecurityManager securityManager = new DefaultWebSecurityManager();
        //关联Realm
        securityManager.setRealm(realm);
        return securityManager;
    }



    /**
     * 获取 ShiroFilterFactoryBean  需要与安全管理器的方法关联 getDefaultWebSecurityManager
     * 需要配置过滤器，配置安全管理器
     *
     * 配置好这个拦截规则的方法后 记得去配置认证。这个再 realm的相关类里进行配置
     * @param securityManager
     * @return
     */
    @Bean(name = "shiroFilterFactoryBean")
    public ShiroFilterFactoryBean getShiroFilterFactoryBean(@Qualifier("securityManager") DefaultWebSecurityManager securityManager){
        ShiroFilterFactoryBean bean = new ShiroFilterFactoryBean();

        //设置安全管理器  securityManager
        bean.setSecurityManager(securityManager);

        //添加shiro的内置过滤器    bean.setFilterChainDefinitionMap(map);
        /*
              anon : 无需认真就能访问
              authc： 必须认证才能访问
              user ： 必须拥有 记住我 的功能才能访问
              perms ：  拥有对某个资源的权限才能访问
              role  ：  拥有某个角色的权限才能访问

              注意： 当设置了访问需要认证时  需要设置跳转到认证界面  bean.setLoginUrl("/login");
         */
        //设置访问拦截链  filters
        Map<String, String> filtermap = new LinkedHashMap<>(); //用来存储拦截规则

        //设置拦截的路径，这个是controller层上写的那些东西  可以使用通配符  filtermap.put("/user/*", "anon");
      //  filtermap.put("/user/add", "anon");//让 http://localhost:9081/user/add 路径不需要认证就能访问
        filtermap.put("/user/update", "authc"); //让update 需要认证才能访问
//        map.put("/index", "authc");
        filtermap.put("/index", "authc");


        //配置某些页面需要授权才能访问  具体授权操作请在realm的授权方法里面进行操作
        filtermap.put("/user/add","perms[user:add]");

        //把设置的拦截规则交给shiro的拦截器
        bean.setFilterChainDefinitionMap(filtermap);

        //如果没有权限就跳转到登录界面
        bean.setLoginUrl("/login");

        //设置 当访问的页面未授权时 返回的页面
        bean.setUnauthorizedUrl("/unautho");
        return bean;
        //拦截规则配置完毕  ， 去配置认证规则 ，到 realm的相关类里
    }



    //shiro整合thymeleaf
    /*  需要这个依赖   shiro的方言  关于thymeleaf  的方言
    <!-- thymeleaf-extras-shiro -->
        <dependency>
            <groupId>com.github.theborakompanioni</groupId>
            <artifactId>thymeleaf-extras-shiro</artifactId>
            <version>2.0.0</version>
        </dependency>*/
    @Bean
    public ShiroDialect getShiroDialect(){
        return new ShiroDialect();
    }
}