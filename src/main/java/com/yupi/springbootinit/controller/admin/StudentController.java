package com.yupi.springbootinit.controller.admin;

import com.alibaba.fastjson.JSONArray;
import com.yupi.springbootinit.common.BaseResponse;
import com.yupi.springbootinit.common.ErrorCode;
import com.yupi.springbootinit.common.ResultUtils;
import com.yupi.springbootinit.exception.BusinessException;
import com.yupi.springbootinit.exception.ThrowUtils;
import com.yupi.springbootinit.model.dto.admin.AdminLoginRequest;
import com.yupi.springbootinit.model.entity.ExcelUser;
import com.yupi.springbootinit.model.entity.User;
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
    public BaseResponse<Boolean> importUser(@RequestPart("file") MultipartFile file,HttpServletRequest request) throws Exception {
        //判断是否登录
        // 先判断是否已登录
        Object adminObj = request.getSession().getAttribute(ADMIN_LOGIN_STATE);
        if (adminObj == null ) {
            throw new BusinessException(ErrorCode.NOT_LOGIN_ERROR);
        }

        if(file.isEmpty()){
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        Boolean result=userService.importUser(file);
        ThrowUtils.throwIf(!result,ErrorCode.SYSTEM_ERROR);

        return  ResultUtils.success(result,"导入成功");
    }

}
