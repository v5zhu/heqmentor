package com.qingsb.dao.repository.mybatis;

import com.qingsb.dao.repository.mybatis.common.MyBatisRepository;
import com.qingsb.po.entity.Auth;
import com.qingsb.po.entity.User;

/*
 * ============================================================================		
 * = COPYRIGHT		
 *               PAX TECHNOLOGY, Inc. PROPRIETARY INFORMATION		
 *   This software is supplied under the terms of a license agreement or		
 *   nondisclosure agreement with PAX  Technology, Inc. and may not be copied		
 *   or disclosed except in accordance with the terms in that agreement.		
 *      Copyright (C) 2017-? PAX Technology, Inc. All rights reserved.		
 * Description: // Detail description about the function of this module,		
 *             // interfaces with the other modules, and dependencies. 		
 * Revision History:		
 * Date	                 Author	                  Action
 * 2017/2/23  	         zhuxl@paxsz.com        Create/Add/Modify/Delete
 * ============================================================================		
 */
@MyBatisRepository
public interface AuthMybatisDao {
    int insertAuth(Auth auth);

    Auth findAuthByLoginName(String loginName);

    int checkIsRegisteredByLoginName(String loginName);
}
