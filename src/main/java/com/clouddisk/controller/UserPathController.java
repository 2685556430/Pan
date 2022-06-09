package com.clouddisk.controller;

import com.clouddisk.constants.Constant;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * TODO 用户路径控制
 *
 * @author ddwl.
 * @date 2022/5/29 8:37
 */
@Controller
public class UserPathController {

    /**
     * TODO 实现用户所在目录层级控制
     * @param request request
     * @param response response
     * @param modelAndView modelAndView
     * @param goTo goTo 请求前往的路径
     * @return springframework.web.servlet.ModelAndView
     * @Author ddwl.
     * @Date 2022/5/29 13:15
    **/
    @RequestMapping("/toPath")
    public ModelAndView toPath(HttpServletRequest request,
                       HttpServletResponse response,
                       ModelAndView modelAndView,
                       @RequestParam(Constant.userPath) String goTo){
        // 更新用户当前路径 末尾统一带上‘/’
        if(!goTo.endsWith("/")) goTo += "/";

        request.getSession().setAttribute(Constant.userPath, goTo);

        modelAndView = new ModelAndView(Constant.uploadView);

        return modelAndView;
    }

}
