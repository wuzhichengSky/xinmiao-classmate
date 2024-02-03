package com.yupi.springbootinit.controller.admin;

import com.yupi.springbootinit.common.BaseResponse;
import com.yupi.springbootinit.common.ErrorCode;
import com.yupi.springbootinit.common.ResultUtils;
import com.yupi.springbootinit.exception.BusinessException;
import com.yupi.springbootinit.model.dto.task.TaskAddRequest;
import com.yupi.springbootinit.model.entity.Task;
import com.yupi.springbootinit.service.TaskService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import static com.yupi.springbootinit.constant.UserConstant.*;

/**
 * @author wzc
 * 2024/2/2
 */

@RestController
@RequestMapping("/task_admin")
@Slf4j
public class AdminTaskController {

    @Resource
    private TaskService taskService;

    /**
     * 发布任务
     *
     * @param request
     * @return
     */
    @PostMapping("/publish")
    public BaseResponse<Task> addTask(@RequestBody TaskAddRequest taskAddRequest, HttpServletRequest request) {
        if (taskAddRequest == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        // 先判断是否已登录
        Object adminObj = request.getSession().getAttribute(ADMIN_LOGIN_STATE);
        if (adminObj == null ) {
            throw new BusinessException(ErrorCode.NOT_LOGIN_ERROR);
        }

        Task task = taskService.addTask(taskAddRequest);

        return ResultUtils.success(task,"发布成功");
    }


}
