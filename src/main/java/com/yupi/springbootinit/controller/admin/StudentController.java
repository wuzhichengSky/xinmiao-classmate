package com.yupi.springbootinit.controller.admin;

import com.alibaba.fastjson.JSONArray;
import com.yupi.springbootinit.common.BaseResponse;
import com.yupi.springbootinit.common.ErrorCode;
import com.yupi.springbootinit.common.ResultUtils;
import com.yupi.springbootinit.exception.BusinessException;
import com.yupi.springbootinit.exception.ThrowUtils;
import com.yupi.springbootinit.model.dto.admin.AdminLoginRequest;
import com.yupi.springbootinit.model.entity.ExcelUser;
import com.yupi.springbootinit.service.UserService;
import com.yupi.springbootinit.utils.FaceUtils;
import com.yupi.springbootinit.utils.PictureUtils;
import com.yupi.springbootinit.utils.excel.ExcelUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

import java.util.List;

import static com.yupi.springbootinit.constant.UserConstant.*;

/**
 * @author wzc
 * 2024/1/31
 */

@RestController
@RequestMapping("/student")
@Slf4j
public class StudentController {

    @Resource
    private UserService userService;

    /**
     * 导入学生数据
     *
     * @return
     */
    @PostMapping("/import")
    public BaseResponse<Boolean> importUser(@RequestPart("file") MultipartFile file) throws Exception {
        if(file.isEmpty()){
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        Boolean result=userService.importUser(file);
        ThrowUtils.throwIf(!result,ErrorCode.SYSTEM_ERROR);

        return  ResultUtils.success(result,"导入成功");
    }

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

}
