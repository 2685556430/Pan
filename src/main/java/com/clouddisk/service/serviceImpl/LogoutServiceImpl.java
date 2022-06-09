package com.clouddisk.service.serviceImpl;

import com.clouddisk.constants.Constant;
import com.clouddisk.service.LogoutService;
import com.clouddisk.utils.CookieUtils;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * TODO 类描述
 *
 * @author ddwl.
 * @date 2022/5/29 23:25
 */
@Service
public class LogoutServiceImpl implements LogoutService {
    @Override
    public void Logout(HttpServletRequest request, HttpServletResponse response) {

        // TODO 现在只是单纯退出 清除cookie  后期需要修改表中用户在线状态
        CookieUtils.clearCookie(request, response);

        request.setAttribute(Constant.status, 1);
    }
}
