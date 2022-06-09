package com.clouddisk.service;

import com.clouddisk.pojo.User;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public interface LoginService {

    // 传入提交信息进行登录验证
    boolean Login(User vuser, HttpServletRequest request, HttpServletResponse response);

}
