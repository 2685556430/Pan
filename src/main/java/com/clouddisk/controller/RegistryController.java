package com.clouddisk.controller;

import com.clouddisk.constants.Constant;
import com.clouddisk.pojo.User;
import com.clouddisk.service.UserService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@Controller
public class RegistryController {
    @Autowired
    private UserService userService;
    // 注册

    /**
     * TODO 实现注册控制
     * @param request request
     * @param response response
     * @param modelAndView modelAndView
     * @param user user
     * @return springframework.web.servlet.ModelAndView
     * @Author ddwl.
     * @Date 2022/5/29 13:13
    **/
    @RequestMapping("/registry")
    public ModelAndView registry(HttpServletRequest request,
                                 HttpServletResponse response,
                                 ModelAndView modelAndView,
                                 User user){
        User theUser = userService.getUserByName(user.getUsername());
        if(theUser == null){
            userService.insertUser(user);
            modelAndView = new ModelAndView(Constant.loginView);
            request.setAttribute(Constant.status, 1);
            return modelAndView;
        }
        // 用户存在
        modelAndView = new ModelAndView(Constant.registryView);
        request.setAttribute(Constant.status, 0);
        return  modelAndView;
    }
}
