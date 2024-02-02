package com.yupi.springbootinit.model.dto.postcomment;

import com.yupi.springbootinit.constant.CommonConstant;
import lombok.Data;

/**
 * @author wzc
 * 2024/1/26
 */
@Data
public class PostCommentListRequest {
    /**
     * 当前页号
     */
    private int current = 1;

    /**
     * 页面大小
     */
    private int pageSize = 10;

    /**
     * 排序字段
     */
    private String sortField;

    /**
     * 排序顺序（默认升序）
     */
    private String sortOrder = CommonConstant.SORT_ORDER_ASC;

    /**
     * 帖子id
     */
    Long postId;

}
