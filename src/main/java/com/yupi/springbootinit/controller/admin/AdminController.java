package com.yupi.springbootinit.controller.admin;

import com.yupi.springbootinit.common.BaseResponse;
import com.yupi.springbootinit.common.ErrorCode;
import com.yupi.springbootinit.common.ResultUtils;
import com.yupi.springbootinit.constant.CommonConstant;
import com.yupi.springbootinit.exception.BusinessException;
import com.yupi.springbootinit.model.dto.admin.AdminLoginRequest;
import com.yupi.springbootinit.model.dto.user.UserLoginRequest;
import com.yupi.springbootinit.model.vo.LoginUserVO;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;

import static com.yupi.springbootinit.constant.UserConstant.*;

/**
 * @author wzc
 * 2024/1/24
 */

@RestController
@RequestMapping("/admin")
@CrossOrigin
@Slf4j
public class AdminController {
    /**
     * 管理员登录
     *
     * @param adminLoginRequest
     * @param request
     * @return
     */
    @PostMapping("/login")
    public BaseResponse<AdminLoginRequest> userLogin(@RequestBody AdminLoginRequest adminLoginRequest, HttpServletRequest request) {
        if (adminLoginRequest == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        String count = adminLoginRequest.getAccount();
        String password = adminLoginRequest.getPassword();
        if (StringUtils.isAnyBlank(count, password)) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        if(!ADMIN_COUNT.equals(count) || !ADMIN_PASSWORD.equals(password)){
            throw new BusinessException(ErrorCode.PARAMS_ERROR,"账号或密码错误");
        }

        request.getSession().setAttribute(ADMIN_LOGIN_STATE, adminLoginRequest);
        return ResultUtils.success(adminLoginRequest,"登录成功");
    }

    /**
     * 管理员注销
     *
     * @param request
     * @return
     */
    @PostMapping("/logout")
    public BaseResponse<Boolean> userLogout(HttpServletRequest request) {
        if (request == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        // 移除登录态
        request.getSession().removeAttribute(ADMIN_LOGIN_STATE);
        return ResultUtils.success(true,"注销成功");
    }
}
