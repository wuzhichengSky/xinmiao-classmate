package com.yupi.springbootinit.constant;

/**
 * 通用常量
 *
 * @author <a href="https://github.com/liyupi">程序员鱼皮</a>
 * @from <a href="https://yupi.icu">编程导航知识星球</a>
 */
public interface CommonConstant {
    /**
     * redis过期时间,默认为24小时
     */
    public static final Long expiration = (long) 60 * 60*24;

    /**
     * 缓存中用户标识
     */
    public static final String USER = "user";

    //图片大小为1MB
    Long maxAvatarSize = 1024*1024L;

    /**
     * 升序
     */
    String SORT_ORDER_ASC = "ascend";

    /**
     * 降序
     */
    String SORT_ORDER_DESC = " descend";



}
