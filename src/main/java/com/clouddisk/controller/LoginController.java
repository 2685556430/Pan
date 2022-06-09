package com.clouddisk.controller;

import com.clouddisk.constants.Constant;
import com.clouddisk.pojo.File;
import com.clouddisk.pojo.User;
import com.clouddisk.service.FileService;
import com.clouddisk.service.LoginService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@Controller
public class LoginController {

    @Autowired
    private LoginService loginService;
    @Autowired
    private FileService fileService;


    /**
     * TODO 实现登录控制
     *
     * @param request request
     * @param response response
     * @param modelAndView modelAndView
     * @param vuser vuser
     *
     * @return springframework.web.servlet.ModelAndView
     * @Author ddwl.
     * @Date 2022/5/29 13:01
    **/
    @RequestMapping("/login")
    public ModelAndView login(HttpServletRequest request,
                        HttpServletResponse response,
                        ModelAndView modelAndView,
                        User vuser) {
        // service 来进行登录判断 登陆成功后设置会话中所需要的初始化参数
        if(loginService.Login(vuser, request, response)){
            modelAndView = new ModelAndView(Constant.uploadView);
            // 登录时默认路径为用户根目录 ‘/’
            String path = Constant.sourcePath + vuser.getUsername() + Constant.userRootPath;
            File[] fileArray = fileService.getUserFileArray(Constant.All, path, vuser.getUsername());
            // 设置用户默认路径和默认文件筛选
            request.getSession().setAttribute(Constant.userPath, Constant.userRootPath);
            request.getSession().setAttribute(Constant.fileType, Constant.All);

            if(fileArray != null) {
                modelAndView.addObject(Constant.UserFileList, fileArray);
            }
            return modelAndView;
        }

        modelAndView = new ModelAndView(Constant.loginView);

        return modelAndView;
    }

}
