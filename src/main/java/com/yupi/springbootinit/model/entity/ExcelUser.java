package com.yupi.springbootinit.model.entity;

import com.baomidou.mybatisplus.annotation.*;

import java.io.Serializable;
import java.util.Date;

import com.yupi.springbootinit.utils.excel.ExcelImport;
import lombok.Data;

/**
 * @author wzc
 * 2024/1/31
 */
@TableName(value ="user")
@Data
public class ExcelUser implements Serializable {

    /**
     * 行号(方便查找)
     */
    @TableField(exist = false)
    private Integer rowNum;

    /**
     * 真实姓名
     */
    @ExcelImport("姓名")
    private String name;

    /**
     * 0-女  1-男
     */
    @ExcelImport(value = "性别",kv = "1-男;0-女")
    private Integer gender;

    /**
     * 学号
     */
    @ExcelImport(value = "学号",unique = true)
    private String userAccount;

    /**
     * 身份证号
     */
    @ExcelImport(value = "身份证号",unique = true)
    private String idNumber;

    /**
     * 学院
     */
    @ExcelImport("学院")
    private String academy;

    /**
     * 班级
     */
    @ExcelImport("班级")
    private String classes;

    /**
     * 寝室楼栋
     */
    @ExcelImport("寝室楼栋")
    private String building;

    /**
     * 房间号
     */
    @ExcelImport("寝室房间号")
    private String room;

    /**
     * 原始json数据(方便纠错)
     */
    private String rowData;

    /**
     * 错误提示
     */
    private String rowTips;

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;
}
