package com.yupi.springbootinit.model.vo;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;


@Data
public class TaskVO implements Serializable {
    /**
     * id
     */
    private Long id;

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
     * 任务状态(2-正常、1-过期、0-失效)
     */
    private Integer state;

    /**
     * 任务开始时间
     */
    private Date startTime;

    /**
     * 任务完成状态 1-已完成 0-未完成  -1-不合格
     */
    private Integer finishState;

}