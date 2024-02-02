package com.yupi.springbootinit.constant;

/**
 * 用户常量
 *
 * @author <a href="https://github.com/liyupi">程序员鱼皮</a>
 * @from <a href="https://yupi.icu">编程导航知识星球</a>
 */
public interface UserConstant {

    /**
     * 用户登录态键
     */
    String USER_LOGIN_STATE = "user_login";

    /**
     * 管理员登录态键
     */
    String ADMIN_LOGIN_STATE = "admin_login";

    //  region 权限

    /**
     * 默认角色
     */
    String DEFAULT_ROLE = "user";

    /**
     * 管理员角色
     */
    String ADMIN_ROLE = "admin";

    /**
     * 管理员账号
     */
    String ADMIN_COUNT = "admin";

    /**
     * 管理员密码
     */
    String ADMIN_PASSWORD = "xinmiao666";

    /**
     * 被封号
     */
    String BAN_ROLE = "ban";

    // endregion
}
