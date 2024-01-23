package com.yupi.springbootinit.model.dto.user;

import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

import java.io.Serializable;

/**
 * 用户更新密码请求
 *
 * @author <a href="https://github.com/liyupi">程序员鱼皮</a>
 * @from <a href="https://yupi.icu">编程导航知识星球</a>
 */
@Data
public class UserPasswordRequest implements Serializable {

    /**
     * 旧密码
     */
    private String oldPassword;


    /**
     * 新密码
     */
    private String newPassword1;

    /**
     * 确认密码
     */
    private String newPassword2;



    private static final long serialVersionUID = 1L;
}