package com.clouddisk.controller;

import com.clouddisk.constants.Constant;

import com.clouddisk.service.FileService;
import com.clouddisk.utils.CookieUtils;
import com.clouddisk.utils.FileUtils;


import org.apache.tomcat.util.bcel.Const;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Controller
public class FileController {
    @Autowired
    private FileService fileService;


    /**
     * TODO 用户上传文件
     *
     * @param request request
     * @param response response
     * @param modelAndView modelAndView
     * @param files 要上传的文件列表
     *
     * @return springframework.web.servlet.ModelAndView
     * @Author ddwl.
     * @Date 2022/5/28 13:59
    **/
    @RequestMapping("/upload")
    public ModelAndView upload(HttpServletRequest request,
                         HttpServletResponse response,
                         ModelAndView modelAndView,
                         @RequestParam("file") MultipartFile []files) throws IOException {
        String username = CookieUtils.getUserName(request);
        // 拼接文件路径 即系统路径 + 用户根路径 + 用户当前路径
        String userPath = (String)request.getSession().getAttribute(Constant.userPath);
        String path = Constant.sourcePath + username +  userPath;
        // 如果没有就创建新的目录保证文件夹路径存在
        FileUtils.creatDir(path);
        for (MultipartFile file : files) {
            fileService.fileUpload(file,
                    path + FileUtils.makeName(file.getOriginalFilename()), request);
        }

        modelAndView = new ModelAndView(Constant.uploadView);

        request.setAttribute("status", 1);
        return modelAndView;
    }


    /**
     * TODO 删除指定的文件
     *
     * @param request request
     * @param response response
     * @param modelAndView modelAndView
     * @param fileNames 要删除的文件列表
     *
     * @return springframework.web.servlet.ModelAndView
     * @Author ddwl.
     * @Date 2022/5/28 16:31
    **/
    @RequestMapping("/delete")
    public ModelAndView delete(HttpServletRequest request,
                       HttpServletResponse response,
                       ModelAndView modelAndView,
                       @RequestParam("fileName") String [] fileNames) throws IOException{

        String username = CookieUtils.getUserName(request);
        // 拼接文件路径 即系统路径 + 用户根路径 + 用户当前路径
        String userPath = (String)request.getSession().getAttribute(Constant.userPath);
        String path = Constant.sourcePath + username +  userPath;

        // 遍历传来的文件名
        for (String fileName : fileNames) {
            // 传入的是绝对路径
            fileService.fileDelete(path + fileName, request);
        }
        modelAndView = new ModelAndView(Constant.uploadView);
        request.setAttribute("status", 1);
        return modelAndView;
    }

    /**
     * TODO 下载文件
     *
     * @param request request
     * @param response response
     * @param modelAndView modelAndView
     * @param fileName 待下载文件列表
     *
     * @Author ddwl.
     * @Date 2022/5/28 16:31
    **/
    @RequestMapping("/download")
    public void download(HttpServletRequest request,
                         HttpServletResponse response,
                         ModelAndView modelAndView,
                         @RequestParam("fileName") String fileName) throws Exception{
        String username = CookieUtils.getUserName(request);

        // 拼接文件路径 即系统路径 + 用户根路径 + 用户当前路径
        String userPath = (String)request.getSession().getAttribute(Constant.userPath);
        String path = Constant.sourcePath + username +  userPath;

        // 调用service层接口 如果单个下载的是文件夹需要打包成压缩包
        if(fileName.lastIndexOf('.') != -1)
            fileService.fileDownload(path + fileName, request, response);
        else
            fileService.batchFileDownload(path, new String[]{fileName},request, response);
        request.setAttribute("status", 1);
    }


    @RequestMapping("/batchDownload")
    public void batchDownload(HttpServletRequest request,
                              HttpServletResponse response,
                              ModelAndView modelAndView,
                              @RequestParam("fileNames") String []fileNames) throws Exception{
        String username = CookieUtils.getUserName(request);
        // 拼接文件路径 即系统路径 + 用户根路径 + 用户当前路径
        String userPath = (String)request.getSession().getAttribute(Constant.userPath);
        String path = Constant.sourcePath + username +  userPath;

        fileService.batchFileDownload(path, fileNames, request, response);
    }

    /**
     * TODO 新建文件夹
     **
     * @param request request
     * @param response response
     * @param modelAndView modelAndView
     * @param dirName dirName 用户新建文件夹的名字
     *
     * @return springframework.web.servlet.ModelAndView
     * @Author ddwl.
     * @Date 2022/5/28 23:24
    **/
    @RequestMapping("/makeDir")
    public ModelAndView makeDir( HttpServletRequest request,
                                HttpServletResponse response,
                                ModelAndView modelAndView,
                                @RequestParam("dirName") String dirName) throws IOException{
        String username = CookieUtils.getUserName(request);

        // 拼接文件上传路径 即系统路径 + 用户根路径 + 用户当前路径
        String userPath = (String)request.getSession().getAttribute(Constant.userPath);
        String dirPath = Constant.sourcePath + username +  userPath;
        modelAndView = new ModelAndView(Constant.uploadView);


        if(fileService.makeDir(dirPath, dirName, username))
            request.setAttribute(Constant.status, 1);
        else
            request.setAttribute(Constant.status, 0);

        return modelAndView;
    }


    /**
     * TODO 文件类型过滤 此处仅用来设置session中的trigger
     *      实际功能调用在过滤器中实现
     *
	 * @param request request
	 * @param response response
	 * @param modelAndView modelAndView
	 * @param fileType fileType
     * @return springframework.web.servlet.ModelAndView
     * @Author ddwl.
     * @Date 2022/6/2 9:25
    **/
    @RequestMapping("/fileTypeFilter")
    public ModelAndView fileTypeFilter(HttpServletRequest request,
                                       HttpServletResponse response,
                                       ModelAndView modelAndView,
                                       @RequestParam("fileType") String fileType){
        // 此处设置session中的文件过滤类型 剩下的处理在过滤器中进行
        request.getSession().setAttribute(Constant.fileType, fileType);

        modelAndView = new ModelAndView(Constant.uploadView);
        return modelAndView;
    }

    /**
     * TODO 复制指定文件到粘贴板
     *      机制 --> session设置对应键值对 存储要复制文件的文件路径
     *
	 * @param request request
	 * @param response response
	 * @param modelAndView modelAndView
	 * @param filename fileame
     * @return springframework.web.servlet.ModelAndView
     * @Author ddwl.
     * @Date 2022/6/5 10:06
    **/
    @RequestMapping("/copy")
    public ModelAndView copy(HttpServletRequest request,
                             HttpServletResponse response,
                             ModelAndView modelAndView,
                             @RequestParam("filename") String filename)throws IOException{
        String username = CookieUtils.getUserName(request);
        String userPath = (String)request.getSession().getAttribute(Constant.userPath);
        String dirPath = Constant.sourcePath + username +  userPath;

        fileService.copy(request, dirPath, filename, username);
        modelAndView = new ModelAndView(Constant.uploadView);


        return modelAndView;
    }

    /**
     * TODO 如果粘贴板中有内容 就把内容粘贴过去
     *
	 * @param request request
	 * @param response response
	 * @param modelAndView modelAndView
     * @return springframework.web.servlet.ModelAndView
     * @Author ddwl.
     * @Date 2022/6/5 10:21
    **/
    @RequestMapping("/paste")
    public ModelAndView paste(HttpServletRequest request,
                      HttpServletResponse response,
                      ModelAndView modelAndView) throws IOException{
        // 获得粘贴板中要复制的文件
        String path = (String)request.getSession().getAttribute(Constant.Clipboard);
        String username = CookieUtils.getUserName(request);
        String userPath = (String) request.getSession().getAttribute(Constant.userPath);
        String destDir = Constant.sourcePath + username + userPath;
        if(path != null){
            // 粘贴文件
            fileService.paste(path, destDir, username);
            // 粘贴后粘贴板置空
            request.getSession().setAttribute(Constant.Clipboard, null);
        }
        modelAndView = new ModelAndView(Constant.uploadView);

        return modelAndView;
    }

    @RequestMapping("/cut")
    public ModelAndView cut(HttpServletRequest request,
                            HttpServletResponse response,
                            ModelAndView modelAndView) throws IOException{
        // 获得粘贴板中要剪切的文件
        String path = (String)request.getSession().getAttribute(Constant.Clipboard);
        String username = CookieUtils.getUserName(request);
        String userPath = (String) request.getSession().getAttribute(Constant.userPath);
        String destDir = Constant.sourcePath + username + userPath;

        if(path != null) {
            // 剪切文件
            fileService.cut(path, destDir, username);
            // 粘贴后粘贴板置空
            request.getSession().setAttribute(Constant.Clipboard, null);
        }
        modelAndView = new ModelAndView(Constant.uploadView);

        return modelAndView;
    }
}
