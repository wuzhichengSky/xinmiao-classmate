package com.yupi.springbootinit.model.vo;

import java.io.Serializable;
import java.util.Date;
import lombok.Data;

/**
 * 用户视图（脱敏）
 *
 * @author <a href="https://github.com/liyupi">程序员鱼皮</a>
 * @from <a href="https://yupi.icu">编程导航知识星球</a>
 */
@Data
public class UserVO implements Serializable {

    /**
     * id
     */
    private Long id;

    /**
     * 用户昵称
     */
    private String userName;

    /**
     * 用户头像
     */
    private String userAvatar;

    /**
     * 用户简介
     */
    private String userProfile;

    /**
     * 学号
     */
    private String userAccount;


    /**
     * 学院
     */
    private String academy;

    /**
     * 班级
     */
    private String classes;

    /**
     * 积分
     */
    private Integer points;


    /**
     * 创建时间
     */
    private Date createTime;

    private static final long serialVersionUID = 1L;
}