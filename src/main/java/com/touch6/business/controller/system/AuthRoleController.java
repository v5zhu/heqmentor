package com.touch6.business.controller.system;

import com.alibaba.fastjson.JSONObject;
import com.touch6.business.api.service.system.AuthRoleService;
import com.touch6.business.entity.system.AuthRole;
import com.touch6.commons.PageObject;
import com.touch6.core.exception.CoreException;
import com.touch6.core.info.Success;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

/**
 * Created by zhuxl@paxsz.com on 2016/7/27.
 */
@SuppressWarnings("ALL")
@Controller
@RequestMapping(value = "/system/authrole")
public class AuthRoleController {
    private static final Logger logger = LoggerFactory.getLogger(AuthRoleController.class);

    @Autowired
    private AuthRoleService authRoleService;

    @RequestMapping(value = "/role/auth", method = RequestMethod.POST,
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public ResponseEntity assignRoleAuth(@RequestBody AuthRole authRole) {
        try {
            logger.info("接收到角色权限配置:[{}]", JSONObject.toJSONString(authRole));
            AuthRole ar = authRoleService.assignAuthRole(authRole.getAuthId(), authRole.getRoleId());
            Success ok = new Success(200, ar, "配置角色权限成功");
            return new ResponseEntity(ok, HttpStatus.OK);
        } catch (CoreException e) {
            return new ResponseEntity(e.getError(), HttpStatus.BAD_REQUEST);
        }
    }

    @RequestMapping(value = "/auth/role", method = RequestMethod.PUT,
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public ResponseEntity updateAuthRole(@RequestBody AuthRole authRole) {
        try {
            logger.info("接收到角色权限修改:[{}]", JSONObject.toJSONString(authRole));
            AuthRole ar = authRoleService.updateAuthRole(authRole.getAuthId(), authRole.getRoleId(), authRole.getNewAuthId());
            Success ok = new Success(200, ar, "修改角色权限信息成功");
            return new ResponseEntity(ok, HttpStatus.OK);
        } catch (CoreException e) {
            return new ResponseEntity(e.getError(), HttpStatus.BAD_REQUEST);
        }
    }

    @RequestMapping(value = "page/authroles", method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public ResponseEntity pageAuthRoles(@RequestParam(value = "page", defaultValue = "1") int page,
                                        @RequestParam(value = "pageSize", defaultValue = "10") int pageSize) {
        try {
            PageObject<AuthRole> pageObject = authRoleService.findAuthRoles(page, pageSize);
            Success ok = new Success(200, pageObject, "查询成功");
            return new ResponseEntity(ok, HttpStatus.OK);
        } catch (CoreException e) {
            return new ResponseEntity(e.getError(), HttpStatus.BAD_REQUEST);
        }
    }
}
