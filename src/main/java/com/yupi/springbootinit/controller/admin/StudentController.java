package com.yupi.springbootinit.controller.admin;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.yupi.springbootinit.common.BaseResponse;
import com.yupi.springbootinit.common.ErrorCode;
import com.yupi.springbootinit.common.ResultUtils;
import com.yupi.springbootinit.exception.BusinessException;
import com.yupi.springbootinit.exception.ThrowUtils;
import com.yupi.springbootinit.model.dto.user.UserQueryRequest;
import com.yupi.springbootinit.model.entity.User;
import com.yupi.springbootinit.model.vo.UserVO;
import com.yupi.springbootinit.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;

import static com.yupi.springbootinit.constant.UserConstant.ADMIN_LOGIN_STATE;


/**
 * @author wzc
 * 2024/1/31
 */

@RestController
@RequestMapping("/admin/student")
@CrossOrigin
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
        if(file.isEmpty()){
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        Boolean result=userService.importUser(file);
        ThrowUtils.throwIf(!result,ErrorCode.SYSTEM_ERROR);

        return  ResultUtils.success(result,"导入成功");
    }

    /**
     * 单条导入学生数据
     *
     * @return
     */
    @PostMapping("/import/one")
    public BaseResponse<Boolean> importOneUser(@RequestBody User user,HttpServletRequest request) throws Exception {
        if(user == null){
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        Boolean result=userService.importOneUser(user);
        ThrowUtils.throwIf(!result,ErrorCode.SYSTEM_ERROR);

        return  ResultUtils.success(result,"导入成功");
    }

    /**
     * 获取学生总数
     *
     * @return
     */
    @GetMapping("/total")
    public BaseResponse<Integer> getTotal(HttpServletRequest request) throws Exception {
        System.out.println(request.getSession().getAttribute(ADMIN_LOGIN_STATE));
        Integer result=userService.getTotal();
        return  ResultUtils.success(result,"查询成功");
    }

    /**
     * 获取已认证人数
     *
     * @return
     */
    @GetMapping("/identify_total")
    public BaseResponse<Integer> getIdentifyTotal(HttpServletRequest request) throws Exception {
        Integer result=userService.getIdentifyTotal();
        return  ResultUtils.success(result,"查询成功");
    }

    /**
     * 批量删除学生
     *
     * @return
     */
    @DeleteMapping("/batches")
    public BaseResponse<Boolean> deleteStudentInBatches( Long[]  idList, HttpServletRequest request) throws Exception {
        userService.deleteStudentInBatches(idList);
        return  ResultUtils.success(true,"批量删除成功");
    }

    /**
     * 根据 id 获取包装类
     *
     * @param id
     * @param request
     * @return
     */
    @GetMapping("/info/{id}")
    public BaseResponse<UserVO> getUserVOById(@PathVariable("id") Long id, HttpServletRequest request) {
        User user = userService.getById(id);
        return ResultUtils.success(userService.getUserVO(user));
    }

    /**
     * 学生列表
     *
     * @return
     */
    @GetMapping("/get")
    public BaseResponse<Page<UserVO>> listUserByPage(@RequestBody UserQueryRequest userQueryRequest,
                                                     HttpServletRequest request) {
        long current = userQueryRequest.getCurrent();
        long size = userQueryRequest.getPageSize();
        // 限制爬虫
        ThrowUtils.throwIf(size > 50, ErrorCode.PARAMS_ERROR);

        Page<User> taskPage = userService.page(new Page<>(current, size),
                userService.getQueryWrapper(userQueryRequest));
        return ResultUtils.success(userService.getUserVOPage(taskPage,request),"查找成功");
    }

}
