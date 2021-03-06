package com.touch6.business.api.service.impl;


import com.touch6.business.api.service.UserService;
import com.touch6.business.entity.AuthCenter;
import com.touch6.business.entity.PhoneCode;
import com.touch6.business.mybatis.*;
import com.touch6.core.exception.CoreException;
import com.touch6.core.exception.ECodeUtil;
import com.touch6.core.exception.Error;
import com.touch6.core.exception.error.constant.*;
import com.touch6.business.dto.UserDto;
import com.touch6.business.enums.UserInfo;
import com.touch6.business.params.LoginParam;
import com.touch6.business.params.PerfectInfoParam;
import com.touch6.business.params.RegisterParam;
import com.touch6.business.entity.User;
import com.touch6.utils.T6PasswordEncryptionUtil;
import com.touch6.utils.T6StringUtils;
import com.touch6.utils.T6ValidatorUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springside.modules.mapper.BeanMapper;

import javax.validation.Validator;
import java.util.HashMap;
import java.util.Map;


/**
 * Created by zhuxl@paxsz.com on 2016/7/25.
 */
@SuppressWarnings("ALL")
@Service
public class UserServiceImpl implements UserService {
    private static final Logger logger = LoggerFactory.getLogger(UserServiceImpl.class);

    @Autowired
    UserMybatisDao userMybatisDao;
    @Autowired
    CertificateMybatisDao certificateMybatisDao;
    @Autowired
    ImageMybatisDao imageMybatisDao;
    @Autowired
    AuthCenterMybatisDao authCenterMybatisDao;
    @Autowired
    PhoneCodeMybatisDao phoneCodeMybatisDao;
    @Autowired
    private Validator validator;

    @Override
    @Transactional
    public void register(RegisterParam registerParam) throws CoreException {
        String error = T6ValidatorUtil.validate(validator, registerParam);
        if (error != null) {
            Error err = ECodeUtil.getCommError(CommonErrorConstant.COMMON_PARAMS_ERROR);
            err.setDes(error);
            throw new CoreException(err);
        }
        //判定密码和确认密码是否一样
        if (!registerParam.getPassword().equals(registerParam.getConfirmPassword())) {
            throw new CoreException(ECodeUtil.getCommError(UserInfoErrorConstant.USER_INFO_PASSWORD_CONFIRM_ERROR));
        }
        //判定手机号是否已注册
        int count1 = userMybatisDao.checkIsRegisteredByPhone(registerParam.getPhone());
        if (count1 > 0) {
            throw new CoreException(ECodeUtil.getCommError(PhoneErrorConstant.PHONE_ALREADY_REGISTERED));
        }
        //判定验证码是否正确
        PhoneCode phoneCode = phoneCodeMybatisDao.findByPhone(registerParam.getPhone());
        if (phoneCode == null) {
            throw new CoreException(ECodeUtil.getCommError(PhoneErrorConstant.PHONE_INCORRECT));
        } else {
            if (!registerParam.getCode().equals(phoneCode.getPresCode())) {
                //验证码不同
                throw new CoreException(ECodeUtil.getCommError(PhoneErrorConstant.PHONE_CODE_INCORRECT));
            }
        }

        User user = new User();
        String uid = T6StringUtils.generate32uuid();
        user.setToken(uid);
        user.setPhone(registerParam.getPhone());
        //insert user
        try {
            userMybatisDao.register(user);
        } catch (Exception e) {
            logger.info("插入用户信息异常，堆栈:", e);
            throw new CoreException(ECodeUtil.getCommError(SystemErrorConstant.SYSTEM_EXCEPTION));
        }
        AuthCenter authCenter = new AuthCenter();
        String authId = T6StringUtils.generate32uuid();
        authCenter.setId(authId);
        authCenter.setUserId(uid);
        authCenter.setLoginName(registerParam.getPhone());
        String salt = T6StringUtils.generate32uuid();
        authCenter.setSalt(salt);
        authCenter.setPassword(T6PasswordEncryptionUtil.getEncryptedPassword(registerParam.getPassword(), salt));
        //insert authCenter
        try {
            authCenterMybatisDao.insertAuth(authCenter);
        } catch (Exception e) {
            logger.info("插入登录信息异常，堆栈:", e);
            throw new CoreException(ECodeUtil.getCommError(SystemErrorConstant.SYSTEM_EXCEPTION));
        }
    }

    @Override
    public UserDto login(LoginParam loginParam) throws CoreException {
        //todo 加入登录日志
        String loginName = loginParam.getLoginName();
        String password = loginParam.getPassword();
        AuthCenter authCenter = authCenterMybatisDao.findAuthByLoginName(loginName);
        if (authCenter == null) {
            logger.info("通过登录名[{}]查询不到登录信息", loginName);
            throw new CoreException(ECodeUtil.getCommError(AuthErrorConstant.AUTH_NO_USER));
        }
        boolean success = T6PasswordEncryptionUtil.authenticate(password, authCenter.getPassword(), authCenter.getSalt());
        if (!success) {
            logger.info("登录账号[{}]密码[{}]错误", loginName, password);
            throw new CoreException(ECodeUtil.getCommError(AuthErrorConstant.AUTH_PASSWORD_ERROR));
        }
        User user = userMybatisDao.findByToken(authCenter.getUserId());
        return BeanMapper.map(user, UserDto.class);
    }

    @Override
    @Transactional
    public void perfectUserInfo(PerfectInfoParam perfectInfoParam) throws CoreException {
        try {
            String error = T6ValidatorUtil.validate(validator, perfectInfoParam);
            if (error != null) {
                Error err = ECodeUtil.getCommError(CommonErrorConstant.COMMON_PARAMS_ERROR);
                err.setDes(error);
                throw new CoreException(err);
            }
            UserInfo infoType = UserInfo.valueOf(perfectInfoParam.getType());
            Map params = new HashMap();
            String column = infoType.name().toLowerCase();
            params.put("uid", "'" + perfectInfoParam.getUid() + "'");
            params.put("column", column);
            if (org.apache.commons.lang.StringUtils.isBlank(perfectInfoParam.getValue())) {
                params.put("value", "null");
            } else {
                params.put("value", "'" + perfectInfoParam.getValue() + "'");
            }
            logger.info("即将完善的信息参数:[{}]", params.toString());
            userMybatisDao.perfectUserInfo(params);
        } catch (Exception e) {
            logger.info("完善信息异常，堆栈:", e);
            throw new CoreException(ECodeUtil.getCommError(CommonErrorConstant.COMMON_PARAMS_ERROR));
        }
    }

    @Override
    public UserDto getUserInfo(String uid) throws CoreException {
        User user = userMybatisDao.findByToken(uid);
        if (user == null) {
            throw new CoreException(ECodeUtil.getCommError(CommonErrorConstant.COMMON_PARAMS_ERROR));
        }
        UserDto userDto = BeanMapper.map(user, UserDto.class);
        return userDto;
    }

//    @Override
//    @Transactional
//    public void addUser(UserDto userDto) throws Exception {
//        User user = BeanMapper.map(userDto, User.class);
//        String uid = StringUtil.generate32uuid();
//        user.setToken(uid);
//        //加入用户信息
//        int res1 = userMybatisDao.updateUser(user);
//        if (res1 != 1) {
//            throw new Exception("添加用户失败");
//        }
//
//        if (user.getIdcard() != null) {
//            Certificate idcard = user.getIdcard();
//            String id = StringUtil.generate32uuid();
//            idcard.setId(id);
//            //加入身份证证件
//            int res2 = certificateMybatisDao.addCert(idcard);
//            if (res2 != 1) {
//                throw new Exception("添加身份证信息失败");
//            }
//            Image idcardImage = idcard.getCert();
//            String imageId = StringUtil.generate32uuid();
//            idcardImage.setImageId(imageId);
//            //加入证件图片
//            int res3 = imageMybatisDao.addImage(idcardImage);
//            if (res3 != 1) {
//                throw new Exception("添加身份证图片失败");
//            }
//        }
//
//    }
}
