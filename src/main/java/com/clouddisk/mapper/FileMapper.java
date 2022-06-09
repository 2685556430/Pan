package com.clouddisk.mapper;

import com.clouddisk.pojo.File;
import org.apache.ibatis.annotations.Mapper;

import java.util.Map;

@Mapper
public interface FileMapper {
    // 查询用户当前路径下存放在服务端的所有文件
    File[] getFilesArrayByUserName(String filePath, String username);
    // 插入新的文件
    void insertFile(File file);
    // 删除用户的制定文件
    void deleteUserFileByName(String filePath, String username, String fileName);
    // 删除用户指定目录下的所有
    void deleteUserFileUnderPath(String dir, String username);
    // 通过fileId查询文件
    File getFileByFileId(Long fileId);
    // 查询用户的指定文件
    File getUserFileByFileName(String filePath, String username, String fileName);
    // 重命名
    void rename(String filePath, String fileName, String newPath, String newName, String username);

    File[] getUserDirList(String username);

    Long computeDirSize(String path, String username);

    void setDirSize(Map<String, Object> value);
}
