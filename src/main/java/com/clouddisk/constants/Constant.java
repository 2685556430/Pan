package com.clouddisk.constants;

import java.util.*;

public class Constant {
    // 一些视图
    public static final String uploadView = "/main/upload";
    public static final String loginView = "/login";
    public static final String registryView = "/registry";
    public static final String []excludePath={
            "/login",                    //登录请求
            "/logout",                   //登出请求
            "/toLogin",                  //登录页面
            "/toRegistry",               //注册页面
            "/registry",                 //注册路径
            "/zipTemp/**",               //压缩临时路径
            "/**/*.js",                  //js静态资源
            "/**/*.css",                 //css静态资源
            "/**/*.jpg",                 //jpg图片资源
            "/**/*.png"                  //png图片资源
    };




    // 用户根目录
    public static final String userRootPath = "/";
    // 判别状况
    public static final String status = "status";
    // 用户资源根目录
    public static final String sourcePath = System.getProperty("user.dir").replace('\\','/') + "/src/main/resources/userSources/";
    // 压缩包临时目录
    public static final String zipTemp = System.getProperty("user.dir").replace('\\','/') + "/src/main/resources/zipTemp";

    /*一些键*/
    // session常量
    public static final String userSession = "userSession";
    // 用户文件列表
    public static final String UserFileList = "UserFileList";
    // 文件类型
    public static final String fileType = "fileType";
    // 用户当前路径
    public static final String userPath = "userPath";
    // cookieName
    public static final String cookieName = "cookie";
    // cookie 有效期 一小时
    public static final int cookieMaxAge = 60*60;
    // key
    public static final String Key = "201917325";
    // clipboard 粘贴板
    public static final String Clipboard = "clipboard";


    // 文件筛选
    public static final String All = "all";
    public static final String Wendang = "wendang";
    public static final List<String> wendang
            = Arrays.asList(".doc", ".docx", ".xls", ".xlsx", ".ppt", ".pptx", ".pdf",".pdfx", ".txt", ".md", ".xml",
                            ".sql", ".cpp", ".xml", ".md");

    public static final String Shipin = "shipin";
    public static final List<String> shipin
            = Arrays.asList(".mp4", ".avi", ".flv", ".mov", ".mpg", ".mpeg", ".mkv", ".wma");

    public static final String Tupian = "tupian";
    public static final List<String> tupian
            = Arrays.asList(".jpg", ".jpeg", ".png", ".bmp", ".gif");

    public static final String Yasuo = "yasuo";
    public static final List<String> yasuo
            = Arrays.asList(".zip", ".rar", ".7z", ".tar", ".iso");

}
