package com.yupi.springbootinit.model.dto.postfavour;

import com.yupi.springbootinit.common.PageRequest;
import com.yupi.springbootinit.model.dto.post.PostQueryRequest;
import java.io.Serializable;
import java.util.List;

import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 帖子收藏查询请求
 *
 * @author <a href="https://github.com/liyupi">程序员鱼皮</a>
 * @from <a href="https://yupi.icu">编程导航知识星球</a>
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class PostFavourQueryRequest extends PageRequest implements Serializable {

    /**
     * 标题
     */
    private String title;

    /**
     * 内容
     */
    private String content;

    /**
     * 标签列表
     */
    private List<String> tags;

    /**
     * 至少有一个标签
     */
    private List<String> orTags;

    /**
     * 搜索词
     */
    private String searchText;


    /**
     * 帖子id
     */
    private Long id;

    /**
     * id
     */
    private Long notId;

    /**
     * 创建用户 id
     */
    private Long userId;

    /**
     * 收藏用户 id
     */
    private Long favourUserId;



    private static final long serialVersionUID = 1L;
}