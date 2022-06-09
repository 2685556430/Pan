package com.clouddisk.config;

import com.clouddisk.constants.Constant;
import com.clouddisk.pojo.File;
import com.clouddisk.pojo.User;
import com.clouddisk.service.FileService;
import com.clouddisk.service.UserService;
import com.clouddisk.utils.CookieUtils;

import com.clouddisk.utils.FileUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * TODO 主拦截器
 *      实现用户登录拦截和文件列表信息传递
 *
 * @author ddwl.
 * @date 2022/5/27 10:42
 */
@Slf4j
@Order(1)
@Component
public class MainInterceptor implements HandlerInterceptor {
    @Autowired
    private FileService fileService;
    @Autowired
    private UserService userService;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        // 携带cookie信息 且cookie信息正确
        if (CookieUtils.getCookie(request) != null) {
            User user = userService.getUserByName(CookieUtils.getUserName(request));
            if(user != null) {
                // 如果为空 当前用户路径默认为根路径
                if(request.getSession().getAttribute(Constant.userPath) == null)
                    // 存在session中的都是以 ’/‘ 为开头和结尾的路径如 ’/newDir/a/‘ 就表示a这个文件夹的位置（路径）
                    request.getSession().setAttribute(Constant.userPath, Constant.userRootPath);

                // 如果为空即展示所有文件
                //if(request.getSession().getAttribute(Constant.fileType) == null)
                    request.getSession().setAttribute(Constant.fileType, Constant.All);
                // 验证通过 继续执行
                return true;
            }
        }
        // 验证未通过 再转发到登录界面
        request.setAttribute(Constant.status, 0);
        request.getRequestDispatcher(Constant.loginView).forward(request, response);
        return false;
    }

    // 加载用户文件列表
    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) throws Exception {
        String username = CookieUtils.getUserName(request);
        String userPath = (String)request.getSession().getAttribute(Constant.userPath);
        String fileType = (String)request.getSession().getAttribute(Constant.fileType);

        userPath = Constant.sourcePath + username + userPath;
        // 每次都更新一下文件夹大小
        fileService.computeDirSize(username);
        // 筛选得到指定类型文件
        File[] fileArray = fileService.getUserFileArray(fileType, userPath, username);

        if(fileArray != null && modelAndView != null) {
            // 返回文件列表
            modelAndView.addObject(Constant.UserFileList, fileArray);
        }

    }


    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {
        HandlerInterceptor.super.afterCompletion(request, response, handler, ex);
    }

}
