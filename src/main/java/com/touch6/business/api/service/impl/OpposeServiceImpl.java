package com.touch6.business.api.service.impl;

import com.touch6.business.api.service.ApprovalService;
import com.touch6.business.api.service.OpposeService;
import com.touch6.business.dto.ToutiaoDto;
import com.touch6.business.dto.article.ArticleCommentDto;
import com.touch6.business.dto.article.ArticleCommentReplyDto;
import com.touch6.business.dto.article.ArticleDto;
import com.touch6.business.dto.common.ApprovalDto;
import com.touch6.business.dto.common.OpposeDto;
import com.touch6.business.entity.common.Approval;
import com.touch6.business.entity.common.Oppose;
import com.touch6.business.mybatis.ToutiaoMybatisDao;
import com.touch6.business.mybatis.article.ArticleCommentMybatisDao;
import com.touch6.business.mybatis.article.ArticleCommentReplyMybatisDao;
import com.touch6.business.mybatis.article.ArticleMybatisDao;
import com.touch6.business.mybatis.common.ApprovalMybatisDao;
import com.touch6.business.mybatis.common.OpposeMybatisDao;
import com.touch6.core.exception.CoreException;
import com.touch6.core.exception.ECodeUtil;
import com.touch6.core.exception.Error;
import com.touch6.core.exception.error.constant.CommonErrorConstant;
import com.touch6.core.exception.error.constant.SystemErrorConstant;
import com.touch6.utils.T6ValidatorUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springside.modules.mapper.BeanMapper;

import javax.validation.Validator;
import java.util.Date;

/**
 * Created by xuan.touch6@qq.com on 2017/4/14.
 */
@SuppressWarnings("ALL")
@Service
public class OpposeServiceImpl implements OpposeService {
    private static final Logger logger = LoggerFactory.getLogger(OpposeServiceImpl.class);

    @Autowired
    private ApprovalMybatisDao approvalMybatisDao;
    @Autowired
    private OpposeMybatisDao opposeMybatisDao;
    @Autowired
    private ArticleMybatisDao articleMybatisDao;
    @Autowired
    private ArticleCommentMybatisDao articleCommentMybatisDao;
    @Autowired
    private ArticleCommentReplyMybatisDao articleCommentReplyMybatisDao;
    @Autowired
    private ToutiaoMybatisDao toutiaoMybatisDao;
    @Autowired
    private Validator validator;


    @Override
    @Transactional
    public int makeOppose(OpposeDto opposeDto) {
        String error = T6ValidatorUtil.validate(validator, opposeDto);
        if (error != null) {
            Error err = ECodeUtil.getCommError(CommonErrorConstant.COMMON_PARAMS_ERROR);
            err.setDes(error);
            throw new CoreException(err);
        }

        int opposeAmount = 0;
        //反对
        Oppose oppose = BeanMapper.map(opposeDto, Oppose.class);
        //插入点赞
        Date time = new Date();
        oppose.setCreateTime(time);
        oppose.setUpdateTime(time);
        int inserted = opposeMybatisDao.addOppose(oppose);
        if (inserted == 1) {
            //插入返回1对象点赞数增加
            switch (oppose.getTargetObject()) {
                case ARTICLE:
                    articleMybatisDao.increaseOpposeAmount(oppose.getObjectId());
                    opposeAmount = articleMybatisDao.findOpposeAmountById(oppose.getObjectId());
                    break;
                case ARTICLE_COMMENT:
                    articleCommentMybatisDao.increaseOpposeAmount(oppose.getObjectId());
                    opposeAmount = articleCommentMybatisDao.findOpposeAmountById(oppose.getObjectId());
                    break;
                case ARTICLE_COMMENT_REPLY:
                    articleCommentReplyMybatisDao.increaseOpposeAmount(oppose.getObjectId());
                    opposeAmount = articleCommentReplyMybatisDao.findOpposeAmountById(oppose.getObjectId());
                    break;
                case NEWS:
                    toutiaoMybatisDao.increaseOpposeAmount(oppose.getObjectId());
                    opposeAmount = toutiaoMybatisDao.findOpposeAmountById(oppose.getObjectId());
                    break;
            }
            return opposeAmount;
        } else {
            throw new CoreException(ECodeUtil.getCommError(CommonErrorConstant.COMMON_OPER_REPEAT));
        }
    }
}
