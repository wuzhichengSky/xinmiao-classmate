package com.yupi.springbootinit.handler;


import com.yupi.springbootinit.common.ErrorCode;
import com.yupi.springbootinit.exception.BusinessException;
import com.yupi.springbootinit.model.entity.User;
import com.yupi.springbootinit.service.UserService;
import com.yupi.springbootinit.utils.JwtUtil;
import com.yupi.springbootinit.utils.UserThreadLocal;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import static com.yupi.springbootinit.constant.UserConstant.USER_LOGIN_STATE;


/**
 * @author wzc
 * 2023/8/2
 */
@Component
@Slf4j
@NoArgsConstructor
public class UserLoginInterceptor implements HandlerInterceptor {
    private UserService userService;

    @Autowired
    private void setUserService(UserService userService){
        this.userService=userService;
    }

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        // 如果不是映射到方法直接通过
        if (!(handler instanceof HandlerMethod)){
            return true;
        }

        String token= request.getHeader("token");
        // 执行认证
        if (token == null) {
            throw new BusinessException(ErrorCode.NOT_LOGIN_ERROR);
        }

        // 验证 token
        if (!JwtUtil.verify(token)) {
            throw new BusinessException(ErrorCode.TOKEN_ERROR);
        }

        // 获取 token 中的 user id
        Long userId = Long.valueOf(JwtUtil.getUserIdByToken(token));

        //查询数据库，看看是否存在此用户
        User user = userService.getById(userId);
        if(user==null){
            throw new BusinessException(ErrorCode.TOKEN_ERROR,"用户不存在");
        }
        //将获取到的user存入UserThreadLocal中备用
        UserThreadLocal.put(user);

        return true;
    }

    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) throws Exception {
        HandlerInterceptor.super.postHandle(request, response, handler, modelAndView);
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {
        HandlerInterceptor.super.afterCompletion(request, response, handler, ex);
        // 移除登录态
        //request.getSession().removeAttribute(USER_LOGIN_STATE);
    }
}
