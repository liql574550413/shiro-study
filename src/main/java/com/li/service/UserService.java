package com.li.service;

import com.li.entity.User;
import org.springframework.stereotype.Service;

import java.util.concurrent.ConcurrentHashMap;


/**
 * @author liql
 * @date 2021/5/9
 */

//模拟用户表
@Service
public class UserService {

  private static ConcurrentHashMap map=  new ConcurrentHashMap<String,String>();

    public UserService() {
        map.put("admin", "123456");
        map.put("root", "123456");
        map.put("lql", "1234");
        map.put("xiaowang", "1234567");
    }

    //模拟用户是否存在
    public synchronized User queryByName(String name){
        boolean o = map.containsKey(name);
        if (o){
            return new User(name,map.get(name).toString(),"");
        }
        return null;
    }

    //模拟查询用户密码
    public synchronized String  queryPwd(String name){
        Object o = map.get(name);
        return o.toString();
    }
}
