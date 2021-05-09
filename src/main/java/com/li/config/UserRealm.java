package com.li.config;

import com.li.entity.User;
import com.li.service.UserService;


import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.*;
import org.apache.shiro.authz.AuthorizationInfo;
import org.apache.shiro.authz.SimpleAuthorizationInfo;
import org.apache.shiro.realm.AuthorizingRealm;
import org.apache.shiro.subject.PrincipalCollection;
import org.apache.shiro.subject.Subject;
import org.springframework.beans.factory.annotation.Autowired;


//这个部分是重点  自定义 realm 必须继承 AuthorizingRealm
class UserRealm extends AuthorizingRealm {
    @Autowired
    private UserService userService;

    //这个是授权 ，就是访问权限
    @Override
    protected AuthorizationInfo doGetAuthorizationInfo(PrincipalCollection principalCollection) {
        System.out.println("shiro 框架正在执行授权操作 AuthorizationInfo, 方法名是 doGetAuthorizationInfo");
        //SimpleAuthorizationInfo 这个是认证的  和授权的SimpleAuthorizationInfo 长得非常想 注意区分
        SimpleAuthorizationInfo info = new SimpleAuthorizationInfo();

        /* 下面这种写法是当所有用户进来都能获得 user:add 权限
         info.addStringPermission("user:add");
        * */

        //给指定用户授权
        //获取当前登录的对象
        Subject subject= SecurityUtils.getSubject();
        //把拿到的这个 "用户 subject” 转成我们可以使用的用户对象
        User currentUser =  (User)subject.getPrincipal();//当前登录的对象 这个要在认证的方法里返回当前对象，在这里才能拿到当前对象
        System.out.println("授权方法里的用户名:"+currentUser.getUsername());

        if (currentUser.getUsername().equals("admin")){//给admin 用户授权
            System.out.println("给 admin 用户授权");
            info.addStringPermission("user:add");
        }else {
            System.out.printf("当前用户%s 不配授权",currentUser.getUsername());
           // info.addStringPermission(null);
        }
//        //授权  perms是用户的权限 一半存在数据库
//        info.addStringPermission(currentUser.getPerms());
        System.out.println("当前用户授权完毕");

        return info;
    }


    //这个是认证 也就是登录
    /**
     * 这个是认证 也就是登录  ，当我们设置了拦截过滤器后( ShiroFilterFactoryBean )，需要配置认证
     * @param authenticationToken
     * @return
     * @throws AuthenticationException
     */
    @Override
    protected AuthenticationInfo doGetAuthenticationInfo(AuthenticationToken authenticationToken) throws AuthenticationException {
        System.out.println("shiro 框架正在执行认证登录操作 AuthenticationInfo, 方法名是 doGetAuthenticationInfo");
        /* 下面这部分我们写在了controller层里了
        //认证的步骤
        // 当请求被过滤器拦截后 ，当用户输入认证信息后 会进入到这个方法进行认证
        //认真过程也可以写在controller层

        //获取当前用户
        Subject subject = SecurityUtils.getSubject();
        //封装用户的登录数据
        UsernamePasswordToken token = new UsernamePasswordToken(username,password);
        try {
            subject.login(token);//shiro会在底层进行验证，如果没有异常 ，则表示认证成功

            //登录成功后返回到成功的页面
            return "index";
        } catch (UnknownAccountException uae){
            model.addAttribute("msg","用户名错误");
            return "login";
        }catch (IncorrectCredentialsException ice){
            model.addAttribute("msg", "密码错误");
            return "login";
        } catch (AuthenticationException e) {
            model.addAttribute("msg", "登录失败");
            return "login";
        }
        */


        //这个token是shiro框架自己的 就是controller层传过来的token subject.login(token);   我们也可以定义紫的的token
        UsernamePasswordToken token = (UsernamePasswordToken) authenticationToken;
        System.out.println("获取用户名："+token.getUsername());
        System.out.println("获取用户密码："+token.getPassword());

 /*
   //这个username 实际情况要从数据库中查询
        String name="admin";  // String name=查数据库
        String password="123456";//  String password=数据库查询

        //判断当前用户名是否正确，至于密码认证，由shiro来做，防止密码泄露
        if (!token.getUsername().equals(name)){
            System.out.println("判断当前者用户名是否为空等 初级判断");
            return null; // return null 会自动抛出相关的异常  详细异常在controller层里
        }*/

      //  当连接真实数据库时
        User user= userService.queryByName(token.getUsername());
        if(user==null){//没有这个人
            return null;  //自动抛出相关异常 UnknowenAccountException
        }
        //连接真实数据库获取密码







       /* 这个密码认证时多余的，清交给shiro自己做
        if (!token.getPassword().equals(password)){
            System.out.println("判断当前密是否正确 初级判断");
            return null;
        }*/


      //  User user = (User) subject.getPrincipal();
    //    System.out.println(user);
//        第一个参数：传入的都是com.java.entity包下的User类的user对象。
//        注意：此参数可以通过subject.getPrincipal()方法获取—获取当前记录的用户，从这个用户对象进而再获取一系列的所需要的属性。
        //第二参数，是从数据库中查询到的密码,会在底层中与token.password 做对比验证。
//        第三个参数，盐–用于加密密码对比。 若不需要，则可以设置为空 “ ”  这个参数可以不写
//
//        第四个参数(或者第三个)：当前realm的名字。
        String s = userService.queryPwd(token.getUsername());
        System.out.println("密码是："+s);
        //在这里可以对密码进行加密 md5加密  md5盐值加密
        //第一个参数非常重要，把从数据库查到的账户信息传进去，这样在授权的方法里的    User currentUser =  (User)subject.getPrincipal();//当前登录的对象
        //才能获取当前的对象
        return new SimpleAuthenticationInfo(user,user.getPassword(),"");
    }
}