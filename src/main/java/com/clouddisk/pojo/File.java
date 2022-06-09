package com.clouddisk.pojo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class File {
    // 文件实体
    private Long fileId;
    private String fileName;
    private String filePath;
    private Long fileSize;
    private String creatDate;
    private String username;
    private Long type;

    // 文件重名依据  同一路径下&&名称相同&&类型相同
    public boolean equals(File file){
        return (this.filePath.equals(file.getFilePath())
                && this.fileName.equals(file.getFileName())
                && this.type.equals(file.getType()));
    }

}
