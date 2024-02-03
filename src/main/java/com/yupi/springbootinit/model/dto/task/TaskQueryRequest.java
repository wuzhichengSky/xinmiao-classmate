package com.yupi.springbootinit.model.dto.task;

import com.baomidou.mybatisplus.annotation.*;
import com.yupi.springbootinit.common.PageRequest;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;


@Data
public class TaskQueryRequest  extends PageRequest implements Serializable {

    /**
     * 任务名称
     */
    private String taskName;

    /**
     * 任务内容
     */
    private String taskContent;

    /**
     * 任务类别(0-开学前期  2-开学后)
     */
    private Integer stage;

    /**
     * 任务状态(2-正常、1-过期、0-失效)
     */
    private Integer state;

    /**
     * id
     */
    private Long id;

}