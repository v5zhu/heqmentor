package com.touch6.business.api.service;

import com.touch6.business.dto.article.ArticleDto;
import com.touch6.business.entity.init.article.ArticleCategory;
import com.touch6.business.entity.init.article.ArticleType;
import com.touch6.business.mybatis.init.article.ArticleCategoryMybatisDao;
import com.touch6.commons.PageObject;

import java.util.List;

/**
 * Created by PAX on 2017/4/7.
 */
public interface ArticleService {
    /**
     * 用户撰写文章
     *
     * @param articleDto
     * @return
     */
    ArticleDto writeArticle(ArticleDto articleDto);

    /**
     * 查看文章详细信息
     *
     * @param id
     * @return
     */
    ArticleDto articleDetail(String id);

    /**
     * 分页查询文章列表
     *
     * @param uid      用户唯一标识,若为空则获取所有的文章列表
     * @param page     页码
     * @param pageSize 每页显示条数
     * @return
     */
    PageObject<ArticleDto> articleList(String uid, int page, int pageSize);

    /**
     * 获取文章类型列表
     *
     * @return
     */
    List<ArticleType> typeList();


    /**
     * 文章分类
     *
     * @param parentCategory
     * @return
     */
    List<ArticleCategory> findCategoriesByParentCategory(String parentCategory);
}
