package com.clouddisk.service.serviceImpl;

import com.clouddisk.constants.Constant;
import com.clouddisk.pojo.User;
import com.clouddisk.service.FileService;
import com.clouddisk.service.LoginService;
import com.clouddisk.service.UserService;
import com.clouddisk.utils.CookieUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@Service
public class LoginServiceImpl implements LoginService {
    @Autowired
    private UserService userService;
    @Autowired
    private FileService fileService;

    // 登录判断
    @Override
    public boolean Login(User vuser, HttpServletRequest request, HttpServletResponse response) {
        User ruser = userService.getUserByName(vuser.getUsername());
        // 用户存在且密码正确
        if(ruser != null) {
            if(ruser.getPassword().equals(vuser.getPassword())){
                // 添加Cookie
                CookieUtils.setCookie(vuser.getUsername(), response);
                request.setAttribute(Constant.status, 1);
                return true;
            }
        }
        request.setAttribute(Constant.status, 0);
        return false;
    }
}
