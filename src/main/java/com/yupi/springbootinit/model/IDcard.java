package com.yupi.springbootinit.model;

import lombok.Data;

/**
 * @author wzc
 * 2024/1/23
 */
@Data
public class IDcard {

    /**
     * 姓名
     */
    private String name;

    /**
     * 身份证号
     */
    private String number;

    /**
     * 地址
     */
    private String address;

    /**
     * 出生 例：19990417
     */
    private String birth;

    /**
     * 性别  男/女
     */
    private String gender;

    /**
     * 民族  汉
     */
    private String ethnic;

    /**
     * 头像  base64编码
     */
    private String photo;
}
