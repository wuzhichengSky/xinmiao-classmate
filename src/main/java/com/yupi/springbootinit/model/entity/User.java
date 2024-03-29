package com.yupi.springbootinit.model.entity;

import com.baomidou.mybatisplus.annotation.*;

import java.io.Serializable;
import java.util.Date;

import com.yupi.springbootinit.utils.excel.ExcelImport;
import lombok.Data;

/**
 * 用户
 * @TableName user
 */
@TableName(value ="user")
@Data
public class User implements Serializable {
    /**
     * id
     */
    @TableId(type = IdType.ASSIGN_ID)
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
     * 真实姓名
     */
    private String name;

    /**
     * 学号
     */
    private String userAccount;

    /**
     * 身份证号
     */
    private String idNumber;

    /**
     * 密码
     */
    private String userPassword;

    /**
     * 学院
     */
    private String academy;

    /**
     * 班级
     */
    private String classes;

    /**
     * 寝室楼栋
     */
    private String building;

    /**
     * 房间号
     */
    private String room;

    /**
     * 0-女  1-男
     */
    private Integer gender;

    /**
     * 积分
     */
    private Integer points;

    /**
     * 是否认证
     */
    private Integer isIdentify;

    /**
     * 微信开放平台id
     */
    private String unionId;

    /**
     * 公众号openId
     */
    private String mpOpenId;

    /**
     * 创建时间
     */
    private Date createTime;

    /**
     * 更新时间
     */
    private Date updateTime;

    /**
     * 是否删除
     */
    @TableLogic
    private Integer isDelete;


    @TableField(exist = false)
    private static final long serialVersionUID = 1L;
}