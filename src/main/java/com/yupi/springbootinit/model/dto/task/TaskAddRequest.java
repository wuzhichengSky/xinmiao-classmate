package com.yupi.springbootinit.model.dto.task;

import com.baomidou.mybatisplus.annotation.*;

import java.io.Serializable;
import java.util.Date;
import lombok.Data;


@Data
public class TaskAddRequest implements Serializable {

    /**
     * 任务名称
     */
    private String taskName;

    /**
     * 任务内容
     */
    private String taskContent;

    /**
     * 任务分数
     */
    private Integer points;

    /**
     * 任务类别(0-开学前期  2-开学后)
     */
    private Integer stage;


    /**
     * 任务开始时间
     */
    private Date startTime;


    @TableField(exist = false)
    private static final long serialVersionUID = 1L;
}