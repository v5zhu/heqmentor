package com.touch6.business.mybatis;

import com.touch6.business.entity.AuthCenter;
import com.touch6.business.mybatis.common.MyBatisRepository;

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
public interface AuthCenterMybatisDao {
    int insertAuth(AuthCenter authCenter);

    AuthCenter findAuthByLoginName(String loginName);

    int checkIsRegisteredByLoginName(String loginName);
}
