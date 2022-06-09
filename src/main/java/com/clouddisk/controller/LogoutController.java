package com.clouddisk.controller;

import com.clouddisk.constants.Constant;
import com.clouddisk.service.LogoutService;
import com.clouddisk.utils.CookieUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * TODO 用户登出控制
 *
 * @author ddwl.
 * @date 2022/5/29 23:04
 */
@Controller
public class LogoutController {
    @Autowired
    private LogoutService logoutService;


    @RequestMapping("/logout")
    public ModelAndView logout(HttpServletRequest request,
                               HttpServletResponse response,
                               ModelAndView modelAndView){
        // 当前用户退出
        logoutService.Logout(request, response);
        modelAndView = new ModelAndView(Constant.loginView);

        return modelAndView;
    }

}
