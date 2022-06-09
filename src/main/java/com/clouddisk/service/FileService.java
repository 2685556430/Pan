package com.clouddisk.service;

import com.clouddisk.pojo.File;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public interface FileService {

    File[] getUserFileArray(String userPath, String username);

    File[] getUserFileArray(String fileType, String userPath, String username);


    // 检查文件是否存在
    boolean fileIsExists(String filePath, String username, String fileName);

    // 文件的上传
    void fileUpload(MultipartFile file, String path, HttpServletRequest request) throws IOException;

    void fileDelete(String path, HttpServletRequest request) throws IOException;

    void fileDownload(String path, HttpServletRequest request, HttpServletResponse response) throws IOException;

    void batchFileDownload(String filePath, String []fileNames, HttpServletRequest request, HttpServletResponse response) throws Exception;

    boolean makeDir(String dirPath, String dirName, String username) throws IOException;

    void copy(HttpServletRequest request, String filePath, String fileName, String username) throws IOException;

    void rename(String originPath, String targetPath, String oldName, String newName, String username) throws IOException;

    void paste(String path, String destDir, String username)throws IOException;

    void cut(String path, String destDir, String username) throws IOException;

    void copyFileTo(java.io.File src, java.io.File des, String username) throws IOException;

    void copyDirTo(java.io.File src, java.io.File des, String username) throws IOException;

    void computeDirSize(String username) throws IOException;
}
