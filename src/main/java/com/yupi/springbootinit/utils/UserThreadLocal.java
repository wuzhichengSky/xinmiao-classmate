package com.yupi.springbootinit.utils;

import com.yupi.springbootinit.model.entity.User;
import org.springframework.stereotype.Component;

/**
 * @author guoyixing
 * 2023/1/24
 */
@Component
public class UserThreadLocal {
    private static final ThreadLocal<User> LOCAL=new ThreadLocal<>();

    public static void put(User user){
        LOCAL.set(user);
    }

    public static User get(){
        return LOCAL.get();
    }

    public static void remove(){
        LOCAL.remove();
    }
}
