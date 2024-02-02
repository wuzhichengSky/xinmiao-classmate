package com.yupi.springbootinit.controller.user;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.yupi.springbootinit.common.BaseResponse;
import com.yupi.springbootinit.common.DeleteRequest;
import com.yupi.springbootinit.common.ErrorCode;
import com.yupi.springbootinit.common.ResultUtils;
import com.yupi.springbootinit.exception.BusinessException;
import com.yupi.springbootinit.exception.ThrowUtils;
import com.yupi.springbootinit.model.dto.post.PostQueryRequest;
import com.yupi.springbootinit.model.dto.postcomment.PostCommentAddRequest;
import com.yupi.springbootinit.model.dto.postcomment.PostCommentListRequest;
import com.yupi.springbootinit.model.dto.postfavour.PostFavourAddRequest;
import com.yupi.springbootinit.model.entity.Post;
import com.yupi.springbootinit.model.entity.PostComment;
import com.yupi.springbootinit.model.entity.User;
import com.yupi.springbootinit.model.vo.PostVO;
import com.yupi.springbootinit.service.PostCommentService;
import com.yupi.springbootinit.service.PostFavourService;
import com.yupi.springbootinit.service.PostService;
import com.yupi.springbootinit.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

/**
 * 帖子评论接口
 *
 */
@RestController
@RequestMapping("/post_comment")
@Slf4j
public class PostCommentController {

    @Resource
    private PostCommentService postCommentService;

    @Resource
    private PostService postService;

    @Resource
    private UserService userService;

    /**
     * 评论帖子
     *
     * @param postCommentAddRequest
     * @param request
     */
    @PostMapping
    public BaseResponse<Long> addComment(@RequestBody PostCommentAddRequest postCommentAddRequest,
            HttpServletRequest request) {
        if (postCommentAddRequest == null || postCommentAddRequest.getPostId() <= 0 || postCommentAddRequest.getContent().isBlank()) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        // 登录才能操作
        final User loginUser = userService.getLoginUser(request);
        Long postId = postCommentAddRequest.getPostId();
        String content = postCommentAddRequest.getContent();
        Long commentId=postCommentService.addComment(postId,content,loginUser);

        return ResultUtils.success(commentId,"评论成功");
    }


    /**
     * 删除
     *
     * @param deleteRequest
     * @param request
     * @return
     */
    @DeleteMapping
    public BaseResponse<Boolean> deletePostComment(@RequestBody DeleteRequest deleteRequest, HttpServletRequest request) {
        if ((deleteRequest == null)) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        User user = userService.getLoginUser(request);
        String id = deleteRequest.getId();
        // 判断是否存在
        PostComment postComment = postCommentService.getById(id);
        ThrowUtils.throwIf(postComment == null, ErrorCode.NOT_FOUND_ERROR);
        // 仅本人可删除
        if (!postComment.getUserId().equals(user.getId())) {
            throw new BusinessException(ErrorCode.NO_AUTH_ERROR);
        }
        boolean b = postCommentService.removeById(id);
        return ResultUtils.success(b,"删除成功");
    }


    /**
     * 获取用户收藏的帖子列表
     *
     * @param postCommentListRequest
     * @param request
     */
    @GetMapping("/list/page")
    public BaseResponse<Page<PostComment>> listFavourPostByPage(@RequestBody PostCommentListRequest postCommentListRequest,
            HttpServletRequest request) {
        if (postCommentListRequest == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        long current = postCommentListRequest.getCurrent();
        long size = postCommentListRequest.getPageSize();
        Long postId = postCommentListRequest.getPostId();
        // 限制爬虫
        ThrowUtils.throwIf(size > 20 || postId == null, ErrorCode.PARAMS_ERROR);
        Page<PostComment> postCommentPagePage = postCommentService.listCommentByPage(postCommentListRequest);

        return ResultUtils.success(postCommentPagePage);
    }
}
