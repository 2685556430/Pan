package com.clouddisk.service;

import com.clouddisk.utils.CookieUtils;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * TODO 类描述
 *
 * @author ddwl.
 * @date 2022/5/29 23:22
 */
public interface LogoutService {

    // 用户登出
    void Logout(HttpServletRequest request, HttpServletResponse response);
}
