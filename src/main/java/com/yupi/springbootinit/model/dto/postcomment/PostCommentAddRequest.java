package com.yupi.springbootinit.model.dto.postcomment;

import lombok.Data;

/**
 * @author wzc
 * 2024/1/26
 */
@Data
public class PostCommentAddRequest {
    /**
     * 帖子id
     */
    Long postId;

    /**
     * 内容
     */
    String content;
}
