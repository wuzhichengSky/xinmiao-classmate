package com.yupi.springbootinit.controller.user;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.yupi.springbootinit.common.BaseResponse;
import com.yupi.springbootinit.common.ErrorCode;
import com.yupi.springbootinit.common.ResultUtils;
import com.yupi.springbootinit.exception.BusinessException;
import com.yupi.springbootinit.exception.ThrowUtils;
import com.yupi.springbootinit.model.dto.task.TaskQueryRequest;
import com.yupi.springbootinit.model.entity.Task;
import com.yupi.springbootinit.model.vo.TaskVO;
import com.yupi.springbootinit.service.TaskService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

import static com.yupi.springbootinit.constant.UserConstant.ADMIN_LOGIN_STATE;
import static com.yupi.springbootinit.constant.UserConstant.USER_LOGIN_STATE;

/**
 * @author wzc
 * 2024/2/3
 */

@RestController
@RequestMapping("/task_user")
@Slf4j
public class UserTaskController {

    @Resource
    private TaskService taskService;


    /**
     * 任务列表
     *
     * @param taskQueryRequest
     * @param request
     * @return
     */
    @GetMapping("/get")
    public BaseResponse<Page<TaskVO>> listTaskByPage(@RequestBody TaskQueryRequest taskQueryRequest,
                                                     HttpServletRequest request) {
        long current = taskQueryRequest.getCurrent();
        long size = taskQueryRequest.getPageSize();
        // 限制爬虫
        ThrowUtils.throwIf(size > 20, ErrorCode.PARAMS_ERROR);
        if(request.getSession().getAttribute(ADMIN_LOGIN_STATE) == null && request.getSession().getAttribute(USER_LOGIN_STATE) == null){
            throw new BusinessException(ErrorCode.NOT_LOGIN_ERROR);
        }

        Page<Task> taskPage = taskService.page(new Page<>(current, size),
                taskService.getQueryWrapper(taskQueryRequest));

        return ResultUtils.success(taskService.getPostVOPage(taskPage,request),"查找成功");
    }
}
