package com.clouddisk.service.serviceImpl;

import com.clouddisk.constants.Constant;
import com.clouddisk.mapper.FileMapper;

import com.clouddisk.service.FileService;
import com.clouddisk.utils.CookieUtils;
import com.clouddisk.utils.FileUtils;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * TODO 说明 路径命名【path: 文件的完整路径; fileName: 只代表文件名(包括后缀); filePath: 文件所在的路径; userPath: 用户所在目录;】
 *      即 path = filePath + fileName; filePath == userPath;
 *      下面有关命名均遵守如上说明
 *
 * @Author ddwl.
 * @Date 2022/5/29 10:05
**/


@Service
public class FileServiceImpl implements FileService {
    @Autowired
    private FileMapper fileMapper;

    // 查询用户文件列表
    @Override
    public com.clouddisk.pojo.File[] getUserFileArray(String userPath, String username) {
        return fileMapper.getFilesArrayByUserName(userPath, username);
    }

    /**
     * TODO 筛选用户文件为指定类型
     *
     * @param fileType fileType
     * @param userPath userPath
     * @param username username
     * @return clouddisk.pojo.File[]
     * @Author ddwl.
     * @Date 2022/5/30 11:22
     **/
    @Override
    public com.clouddisk.pojo.File[] getUserFileArray(String fileType, String userPath, String username) {


        com.clouddisk.pojo.File[] f = getUserFileArray(userPath, username);
        // 不筛选
        if (Constant.All.equals(fileType)) return f;

        List<com.clouddisk.pojo.File> fileList = null;
        switch (fileType) {
            case Constant.Wendang:
                fileList = FileUtils.getWendang(f);
                break;

            case Constant.Shipin:
                fileList = FileUtils.getShipin(f);
                break;
            case Constant.Tupian:
                fileList = FileUtils.getTupian(f);
                break;
            default:
                fileList = FileUtils.getOther(f);
                break;
        }
        return fileList.toArray(new com.clouddisk.pojo.File[fileList.size()]);
    }

    // 在数据库查询并判读上传的文件是否已经存在
    // TODO 后期还需要比对数据库中相同文件名但是否在同级目录下
    @Override
    public boolean fileIsExists(String filePath, String username, String fileName) {
        com.clouddisk.pojo.File file = fileMapper.getUserFileByFileName(filePath, username, fileName);
        return file != null;
    }

    /**
     * TODO 上传用户文件并保存 供controller层调用
     *
     * @param file    file
     * @param path    path
     * @param request request
     * @Author ddwl.
     * @Date 2022/5/26 21:17
     **/
    @Override
    public void fileUpload(MultipartFile file, String path, HttpServletRequest request) throws IOException {
        // 注意这个path是带文件名的
        String username = CookieUtils.getUserName(request);
        // 先查询数据库里是否已经有这个文件存在

        if (fileIsExists(FileUtils.getFilePathFromPath(path), username,
                FileUtils.FileRealName(
                        FileUtils.getFileNameFromPath(path)
                )
        )
        )
            return;

        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd-hh-mm-ss");
        com.clouddisk.pojo.File theFile = new com.clouddisk.pojo.File();
        theFile.setFileName(file.getOriginalFilename());
        theFile.setFileSize(file.getSize());
        theFile.setCreatDate(dateFormat.format(new Date()));
        theFile.setUsername(username);
        theFile.setFilePath(FileUtils.getFilePathFromPath(path));
        // 0: 普通文件; 1: 文件夹
        // 因为暂时不支持文件夹上传所以默认为0
        theFile.setType(0L);

        // 数据库和本地资源都要更新
        file.transferTo(new File(path));
        fileMapper.insertFile(theFile);
    }

    /**
     * TODO 删除指定文件集合 供controller层调用
     *
     * @param path    path
     * @param request request
     * @Author ddwl.
     * @Date 2022/5/27 21:40
     **/
    @Override
    public void fileDelete(String path, HttpServletRequest request) throws IOException {
        String username = CookieUtils.getUserName(request);
        // 这个名字是不带时间的名字
        String t = FileUtils.getFileNameFromPath(path);
        // 文件夹路径
        path = FileUtils.getFilePathFromPath(path);
        // 文件夹
        File dir = new File(path);
        if (dir.isDirectory() && dir.exists()) {
            File[] files = dir.listFiles();
            if (files != null) {
                // 遍历非空文件夹中的文件
                for (File file : files) {
                    String fileName = file.getName();
                    // 如果是文件才需要获得原始名称 文件夹则不需要
                    if (file.isFile()) {
                        fileName = FileUtils.FileRealName(fileName);
                    }
                    // 删掉匹配的文件
                    if (fileName.equals(t)) {
                        file.delete();
                        fileMapper.deleteUserFileByName(path, username, fileName);
                        if (file.isDirectory()) {
                            fileMapper.deleteUserFileUnderPath(path + fileName, username);
                            // 删除的是文件夹 但非空不能直接用delete()删除
                            FileUtils.deleteDir(file);
                        }

                    }
                    // 如果是文件夹则需要将数据库中的所有子文件也删除
                }
            }
        }

    }

    /**
     * TODO 下载单个指定文件 供controller层调用
     *
     * @param path     path
     * @param request  request
     * @param response response
     * @Author ddwl.
     * @Date 2022/5/27 21:14
     **/
    @Override
    public void fileDownload(String path, HttpServletRequest request, HttpServletResponse response) throws IOException {

        String username = CookieUtils.getUserName(request);
        // 要下载的文件存在
        // 获得文件名
        String t = FileUtils.getFileNameFromPath(path);
        // 得到用户目录
        path = FileUtils.getFilePathFromPath(path);
        if (fileIsExists(path, username, t)) {
            File dir = new File(path);
            if (dir.isDirectory() && dir.exists()) {
                File[] files = dir.listFiles();
                if (files != null) {
                    // 遍历非空文件夹中的文件
                    for (File file : files) {
                        String fileName = file.getName();
                        // 去掉时间后的名字
                        fileName = FileUtils.FileRealName(fileName);
                        // 下载匹配的文件
                        if (fileName.equals(t)) {
                            returnFileStream(fileName, path + '/' + file.getName(), request, response);
                        }
                    }
                }
            }


        }


    }

    /**
     * TODO 批量下载选中的文件集合 并返回zip压缩包 供controller层调用
     *
     * @param filePath  path 用户路径
     * @param fileNames fileNames 文件集合
     * @param request   request
     * @param response  response
     * @Author ddwl.
     * @Date 2022/5/28 20:13
     **/
    @Override
    public void batchFileDownload(String filePath, String[] fileNames, HttpServletRequest request, HttpServletResponse response) throws Exception {

        String zipPath = Constant.zipTemp + '/' + System.currentTimeMillis() + ".zip";
        List<String> dir = new ArrayList<>();
        // 添加同一级目录下的文件
        if (fileNames.length <= 100) {
            File tempDir = new File(filePath);

            // 比对目录下的所有文件是否有需要下载的
            if (tempDir.isDirectory() && tempDir.exists()) {
                File[] f = tempDir.listFiles();
                // 不为空
                if (f != null) {

                    for (int i = 0; i < fileNames.length; i++) {

                        for (File file : f) {
                            // 如果匹配 添加到待压缩集合(路径)中 注意区分文件夹和文件
                            if (file.isFile()) {

                                if (FileUtils.FileRealName(file.getName()).equals(fileNames[i])) {
                                    dir.add(filePath + file.getName());
                                }
                            } else {
                                if (file.getName().equals(fileNames[i])) {
                                    dir.add(filePath + file.getName());
                                }
                            }
                        }

                    }
                    FileUtils.zipDir(dir.toArray(new String[dir.size()]), zipPath, true);

                    //fileDownload(zipPath, request, response);
                    //returnFileStream(FileUtils.getFileNameFromPath(zipPath),zipPath, request,response);
                    FileUtils.downloadZip(response, zipPath);
                }

            }
        }
    }

    /**
     * TODO 创建文件夹 供controller层调用
     *
     * @param dirPath  dirPath
     * @param dirName  dirName
     * @param username username
     * @return boolean
     * @Author ddwl.
     * @Date 2022/5/29 14:28
     **/
    @Override
    public boolean makeDir(String dirPath, String dirName, String username) throws IOException {
        File dir = new File(dirPath + dirName);

        if (!dir.exists()) {
            FileUtils.creatDir(dirPath + dirName);
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd-hh-mm-ss");
            com.clouddisk.pojo.File temp = new com.clouddisk.pojo.File();
            temp.setFileName(dirName);
            temp.setType(1L);
            temp.setUsername(username);
            temp.setCreatDate(dateFormat.format(new Date()));
            temp.setFilePath(dirPath);
            temp.setFileSize(0L);
            fileMapper.insertFile(temp);
            return true;
        }
        return false;
    }


    /**
     * TODO 供单个文件下载时调用 返回文件输出流
     *
     * @param filename filename
     * @param path     path
     * @param request  request
     * @param response response
     * @Author ddwl.
     * @Date 2022/5/27 20:13
     **/
    private void returnFileStream(String filename, String path, HttpServletRequest request, HttpServletResponse response) throws IOException {
        //  文件存在才下载
        File file = new File(path);
        if (file.exists()) {
            OutputStream out = null;
            FileInputStream in = null;

            in = new FileInputStream(file);


            // 防止文件名乱码问题，获取浏览器类型，转换对应文件名编码格式，IE要求文件名必须是utf-8, firefo要求是iso-8859-1编码
            String agent = request.getHeader("user-agent");
            if (agent.contains("FireFox")) {
                filename = new String(filename.getBytes(StandardCharsets.UTF_8), StandardCharsets.ISO_8859_1);
            } else {
                filename = URLEncoder.encode(filename, "UTF-8");
            }

            // 设置下载文件的mineType，告诉浏览器下载文件类型(image/...  text/...)
            String mineType = request.getServletContext().getMimeType(filename);
            response.setContentType(mineType);
            // 设置一个响应头，无论是否被浏览器解析都下载 文件名默认与服务端一致
            response.setHeader("Content-Disposition", "attachment; filename=" + filename);
            // 将要下载的文件内容通过输出流写到浏览器
            out = response.getOutputStream();

            int len = 0;
            byte[] buffer = new byte[102400];
            while ((len = in.read(buffer)) > 0) {
                out.write(buffer, 0, len);
            }
            if (out != null) {
                out.flush();
                out.close();
            }
            in.close();
            // 成功
            request.setAttribute(Constant.status, 1);
        }
        // 不成功
        request.setAttribute(Constant.status, 0);

    }

    @Override
    public void copy(HttpServletRequest request, String filePath, String fileName, String username) throws IOException {
        File dir = new File(filePath);
        if (dir.exists() && dir.isDirectory()) {
            File[] files = dir.listFiles();
            if (files != null) {
                for (File file : files) {
                    if (FileUtils.FileRealName(file.getName()).equals(fileName)) {
                        // 将文件的绝对路径 放入粘贴板
                        request.getSession().setAttribute(Constant.Clipboard, file.getAbsolutePath());
                        break;
                    }
                }
            }

        }
    }

    public void rename(String originPath, String targetPath, String oldName, String newName, String username) throws IOException {
        // 获得旧目录

        File oldDir = new File(originPath);

        // 目录下的文件
        File[] files = oldDir.listFiles();
        // 遍历找到对应要修改的文件
        for (File file : files) {
            if (FileUtils.FileRealName(file.getName()).equals(oldName)) {
                // 重名/移动
                file.renameTo(new File(targetPath + newName));
                // 数据库中也要修改
                fileMapper.rename(originPath, oldName, targetPath, newName, username);
                break;
            }
        }
    }

    /**
     * TODO 粘贴到指定位置
     *
     * @param path    path 选中的文件的完整路径
     * @param destDir destDir 目标文件夹
     * @Author ddwl.
     * @Date 2022/6/5 10:32
     **/
    @Override
    public void paste(String path, String destDir, String username) throws IOException {
        File originFile = new File(path);
        if (originFile.exists()) {
            String fileName = FileUtils.getFileNameFromPath(path);
            if (!destDir.endsWith("/")) destDir += "/";
            // 文件夹不可以直接复制 需要遍历文件夹下所有文件
            if (originFile.isDirectory()) {
                copyDirTo(originFile, new File(destDir), username);
                // TODO 数据库中的内容也要更新 想不到好办法了 只能把数据库更新操作糅杂到文件操作里了...
            } else {
                File destFile = new File(destDir + FileUtils.makeName(FileUtils.FileRealName(fileName)));
                if (!destFile.exists()) {
                    destFile.createNewFile();
                    copyFileTo(originFile, destFile, username);
                }
            }
        }
    }


    @Override
    public void cut(String path, String destDir, String username) throws IOException {
        File originFile = new File(path);
        if (originFile.exists()) {
            String fileName = FileUtils.getFileNameFromPath(path);
            if (!destDir.endsWith("/")) destDir += "/";
            // 文件夹不可以直接复制 需要遍历文件夹下所有文件
            if (originFile.isDirectory()) {
                copyDirTo(originFile, new File(destDir), username);
                // TODO 数据库中的内容也要更新 想不到好办法了 只能把数据库更新操作糅杂到文件操作里了...
            } else {
                File destFile = new File(destDir + FileUtils.makeName(FileUtils.FileRealName(fileName)));
                if (!destFile.exists()) {
                    destFile.createNewFile();
                    copyFileTo(originFile, destFile, username);
                }
            }
            // 剪切就是复制后删掉原
            FileUtils.deleteDir(originFile);
            // 数据库也删掉
            if(originFile.isDirectory())
                fileMapper.deleteUserFileUnderPath(path + '/', username);
            else
                fileMapper.deleteUserFileByName(FileUtils.getFilePathFromPath(path),
                        username,
                        FileUtils.getFileNameFromPath(path));
        }
    }

    /**
     * TODO 复制文件夹
     *
     * @param src src
     * @param des des
     * @Author ddwl.
     * @Date 2022/6/5 12:41
     **/
    public void copyDirTo(File src, File des, String username) throws IOException {
        File[] fileArray = src.listFiles();
        if (fileArray != null) {
            for (File src1 : fileArray) {
                File des1 = new File(des, src1.getName());
                if (src1.isDirectory()) {
                    des1.mkdir();
                    SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd-hh-mm-ss");
                    com.clouddisk.pojo.File file = new com.clouddisk.pojo.File();
                    file.setFileSize(src.length());
                    file.setFileName(FileUtils.FileRealName(src.getName()));
                    file.setFilePath(FileUtils.formatPath(des.getParentFile().getAbsolutePath()));
                    file.setType(0L);
                    file.setCreatDate(dateFormat.format(new Date()));
                    file.setUsername(username);
                    // 递归
                    copyDirTo(src1, des1, username);
                    fileMapper.insertFile(file);
                } else {
                    copyFileTo(src1, des1, username);
                }
            }
        }
    }

    /**
     * TODO 文件拷贝到指定位置
     *
     * @param src src
     * @param des des
     * @Author ddwl.
     * @Date 2022/6/5 12:39
     **/
    public void copyFileTo(File src, File des, String username) throws IOException {
        FileInputStream fis = null;
        FileOutputStream fos = null;
        try {
            fis = new FileInputStream(src);
            fos = new FileOutputStream(des);
            byte[] bys = new byte[1024];
            int len = 0;
            while ((len = fis.read(bys)) != -1) {
                fos.write(bys, 0, len);
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd-hh-mm-ss");
            com.clouddisk.pojo.File file = new com.clouddisk.pojo.File();
            file.setFileSize(src.length());
            file.setFileName(FileUtils.FileRealName(src.getName()));
            file.setFilePath(FileUtils.formatPath(des.getParentFile().getAbsolutePath()));
            file.setType(0L);
            file.setCreatDate(dateFormat.format(new Date()));
            file.setUsername(username);
            fileMapper.insertFile(file);
            try {
                fis.close();
                fos.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public void computeDirSize(String username) throws IOException{
        com.clouddisk.pojo.File[] dirList = fileMapper.getUserDirList(username);
        for (com.clouddisk.pojo.File file : dirList) {
            String dirName = file.getFileName();
            String dirPath = file.getFilePath();
            // 计算并修改指定路径下文件夹的大小
            Long dirSize = fileMapper.computeDirSize(dirPath + dirName + '/', username);

            Map<String, Object> value = new HashMap<>();
            value.put("dirPath", dirPath);
            value.put("dirName", dirName);
            value.put("username", username);
            value.put("dirSize", dirSize);
            fileMapper.setDirSize(value);
        }
    }
}