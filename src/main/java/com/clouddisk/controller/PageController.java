package com.clouddisk.controller;

import com.clouddisk.constants.Constant;
import com.clouddisk.service.FileService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
/**
 * TODO 页面跳转控制
 * @author ddwl.
 * @date 2022/5/29 13:06
 */
@Controller
public class PageController {
    @Autowired
    private FileService fileService;

    // 首页默认跳转登录
    @RequestMapping("/")
    public ModelAndView toLogin(HttpServletRequest request,
                          ModelAndView modelAndView){

        modelAndView = new ModelAndView(Constant.uploadView);

        return modelAndView;
    }

    @RequestMapping("/toLogin")
    public ModelAndView toLogin(ModelAndView modelAndView){
        modelAndView = new ModelAndView(Constant.loginView);
        return modelAndView;
    }

    @RequestMapping("/toUpload")
    public ModelAndView toUpload(HttpServletRequest request,
                           ModelAndView modelAndView){


        modelAndView = new ModelAndView(Constant.uploadView);
        return modelAndView;
    }

    @RequestMapping("/toRegistry")
    public ModelAndView toRegister(ModelAndView modelAndView){
        modelAndView = new ModelAndView(Constant.registryView);

        return modelAndView;
    }

}
