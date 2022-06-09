package com.clouddisk.utils;

import com.clouddisk.constants.Constant;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;


@Component("FileUtils")
public class FileUtils {
    // buffer尺寸
    private static final int BUFFER_SIZE = 2 * 1024;


    /**
     * TODO 文件大小单位转换(byte为单位)
     *
     * @param fileLength fileLength
     *
     * @return lang.String
     * @Author ddwl.
     * @Date 2022/5/28 22:58
    **/
    public static String formetFileSize(Long fileLength) {
        String fileSizeString = "";
        if (fileLength == null) {
            return fileSizeString;
        }
        DecimalFormat df = new DecimalFormat("#.00");
        if (fileLength < 1024) {
            fileSizeString = df.format((double) fileLength) + "B";
        } else if (fileLength < 1048576) {
            fileSizeString = df.format((double) fileLength / 1024) + "K";
        } else if (fileLength < 1073741824) {
            fileSizeString = df.format((double) fileLength / 1048576) + "M";
        } else {
            fileSizeString = df.format((double) fileLength / 1073741824) + "G";
        }
        return fileSizeString;
    }

    /**
     * TODO 根据当前时间戳生成文件名
     *
     * @param fileName fileName
     *
     *  * @return lang.String
     * @Author ddwl.
     * @Date 2022/5/28 19:15
    **/
    public static String makeName(String fileName){

        StringBuilder res = new StringBuilder("");
        StringBuilder sb = new StringBuilder(fileName);

        int index = sb.lastIndexOf(".");

        if(index == -1) index = sb.length()-1;

        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd-hh-mm-ss");
        String time = "-" + dateFormat.format(new Date());
        return sb.substring(0, index) + time + sb.substring(index);
    }

    /**
     * TODO 创建一个合法路径下的文件夹
     *
     * @param dirPath dirPath
     *
     *  * @return void
     * @Author ddwl.
     * @Date 2022/5/28 19:14
    **/
    public static void creatDir(String dirPath) throws IOException{
        File file = new File(dirPath);
        if(!file.exists() && !file.isDirectory())
            file.mkdirs();
    }

    // 保存文件到指定位置
    public static void FilesSave(MultipartFile file, String saveTo) throws IOException {
        file.transferTo(new File(saveTo));
    }

    /**
     * TODO 存在磁盘中的文件名都是带日期的 需要进行处理获得实际用户上传时的文件名
     *
     * @param fileName fileName 传入的只是名字不是路径
     *
     * @return lang.String
     * @Author ddwl.
     * @Date 2022/5/28 19:13
    **/
    public static String FileRealName(String fileName){
        if(fileName.lastIndexOf('.')==-1) return fileName;

        return fileName.substring(0, fileName.lastIndexOf('.') - 20)
                + fileName.substring(fileName.lastIndexOf('.'));
    }

    /**
     * TODO 从完整路径中获取文件名
     *
     * @param path path 文件的路径
     *
     * @return lang.String
     * @Author ddwl.
     * @Date 2022/5/28 19:13
    **/
    public static String getFileNameFromPath(String path){
        return path.substring(path.lastIndexOf('/') + 1);
    }

    // 返回文件路径 末尾带‘/’
    public static String getFilePathFromPath(String path){
        return path.substring(0, path.lastIndexOf('/') + 1);
    }
    /**
     * TODO 获得文件后缀
     *
     * @param path path
     *
     * @return lang.String
     * @Author ddwl.
     * @Date 2022/5/28 19:12
    **/
    public static String getFileSuffix(String path){
        if(path.lastIndexOf('.') == -1)return "";
        return path.substring(path.lastIndexOf('.'));
    }

    /**
     * TODO 用于压缩指定的单个文件 不能用来压缩文件夹
     *
     * @param fileList fileList
     * @param zipFileName zipFileName
     *
     * @Author ddwl.
     * @Date 2022/5/28 19:11
    **/
    public static void zip(List<File> fileList, String zipFileName) {
        FileInputStream fileInputStream = null;
        FileOutputStream fileOutputStream = null;
        ZipOutputStream zipOutputStream = null;
        BufferedInputStream bufferInputStream = null;
        try {
            // zipFileName为压缩文件的名称（xx.zip），首先在某个目录下（路径可以根据自己的需求进行修改）创建一个.zip结尾的文件
            fileOutputStream = new FileOutputStream(Constant.zipTemp + '/' + zipFileName);
            zipOutputStream = new ZipOutputStream(new BufferedOutputStream(fileOutputStream));
            // 创建读写缓冲区
            byte[] bufs = new byte[1024 * 10];

            for (File file : fileList) {
                // 创建ZIP实体，并添加进压缩包
                ZipEntry zipEntry = new ZipEntry(FileRealName(file.getName()));
                zipOutputStream.putNextEntry(zipEntry);

                // 读取待压缩的文件并写进压缩包里
                fileInputStream = new FileInputStream(file);
                bufferInputStream = new BufferedInputStream(fileInputStream, 1024 * 10);
                int read = 0;
                while ((read = bufferInputStream.read(bufs, 0, 1024 * 10)) != -1) {
                    zipOutputStream.write(bufs, 0, read);
                }
            }

        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (bufferInputStream != null) {
                    bufferInputStream.close();
                }
                if (zipOutputStream != null) {
                    zipOutputStream.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }

        }
    }

    /**
     * TODO 通过文件名集合 得到文件列表
     *
     * @param path path
     * @param fileNames fileNames
     *
     *  * @return List<java.io.File>
     * @Author ddwl.
     * @Date 2022/5/28 19:16
    **/
    public static List<File> getFileList(String path, String []fileNames){

        List<File> fileList = new ArrayList<>();
        // 进入文件夹
        File dir = new File(path);

        if(dir.exists() && dir.isDirectory()){
            File []files = dir.listFiles();

            if(files != null) {
                for (File file : files) {
                    String fileRealName = FileRealName(file.getName());
                    for (String fileName : fileNames) {
                        if(fileRealName.equals(fileName)){

                            fileList.add(file);
                            break;
                        }
                    }
                }
            }
        }


        return fileList;
    }

    /**
     * TODO 将打包好的压缩包通过response输出流返回(即告知浏览器下载)
     *
     * @param response response
     * @param zipFilePath zipFilePath 压缩包的完整路径
     *
     * @Author ddwl.
     * @Date 2022/5/28 19:17
    **/
    public static void downloadZip(HttpServletResponse response, String zipFilePath) {

        File zipFile = new File(zipFilePath);
        response.reset(); // 重点突出
        // 不同类型的文件对应不同的MIME类型
        response.setContentType("application/octet-stream");
        response.setCharacterEncoding("utf-8");
        response.setHeader("Content-Disposition", "attachment; filename=" +
                FileUtils.getFileNameFromPath(zipFilePath));

        FileInputStream fileInputStream = null;
        OutputStream outputStream = null;
        try {
            outputStream = response.getOutputStream();
            fileInputStream = new FileInputStream(zipFile);
            byte[] bufs = new byte[1024 * 10];
            int read = 0;
            while ((read = fileInputStream.read(bufs, 0, 1024 * 10)) != -1) {
                outputStream.write(bufs, 0, read);
            }
            fileInputStream.close();
            outputStream.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        finally {
            try {
                // 删除压缩包
                File file = new File(zipFilePath);
                file.delete();

                if (fileInputStream != null) {
                    fileInputStream.close();
                }
                if (outputStream != null) {
                    outputStream.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }


    /**
     * TODO 打包指定的文件包括文件夹
     *
     * @param srcDir srcDir 是一个文件路径集合 也就是这个集合中的文件和文件夹将会被压缩打包
     * @param outDir outDir 压缩包输出路径
     * @param KeepDirStructure KeepDirStructure
     *                         为true时保留文件夹结构 不建议为false(必须确保不重名)
     *
     * @Author ddwl.
     * @Date 2022/5/28 19:19
    **/
    public static void zipDir(String[] srcDir, String outDir,
                             boolean KeepDirStructure) throws RuntimeException, Exception {

        FileOutputStream out = new FileOutputStream(new File(outDir));
        ZipOutputStream zos = null;
        try {
            zos = new ZipOutputStream(out);
            List<File> sourceFileList = new ArrayList<>();
            for (String dir : srcDir) {


                File sourceFile = new File(dir);

                sourceFileList.add(sourceFile);
            }
            compress(sourceFileList, zos, KeepDirStructure);

        } catch (Exception e) {
            throw new RuntimeException("zip error from ZipUtils", e);
        } finally {
            if (zos != null) {
                try {
                    zos.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

    }

    /**
     * TODO 压缩重载1 针对文件
     *
     * @param sourceFile sourceFile
     * @param zos zos
     * @param name name
     * @param KeepDirStructure KeepDirStructure
     *
     * @Author ddwl.
     * @Date 2022/5/28 19:22
    **/
    private static void compress(File sourceFile, ZipOutputStream zos,
                                 String name, boolean KeepDirStructure) throws Exception {
        byte[] buf = new byte[BUFFER_SIZE];
        if (sourceFile.isFile()) {
            // 如果是文件需要获得原始名称
            name = FileRealName(name);
            zos.putNextEntry(new ZipEntry(name));
            int len;
            FileInputStream in = new FileInputStream(sourceFile);
            while ((len = in.read(buf)) != -1) {
                zos.write(buf, 0, len);
            }

            zos.closeEntry();
            in.close();
        }
        else {
            File[] listFiles = sourceFile.listFiles();
            if (listFiles == null || listFiles.length == 0) {
                if (KeepDirStructure) {
                    zos.putNextEntry(new ZipEntry(name + "/"));
                    zos.closeEntry();
                }

            }
            else {
                for (File file : listFiles) {
                    // 文件夹要递归调用
                    if (KeepDirStructure) {
                        compress(file, zos, name + "/" + file.getName(),
                                KeepDirStructure);
                    } else {
                        compress(file, zos, file.getName(), KeepDirStructure);
                    }

                }
            }
        }
    }

    /**
     * TODO 压缩重载2 针对子文件夹
     *
     * @param sourceFileList sourceFileList
     * @param zos zos
     * @param KeepDirStructure KeepDirStructure
     *
     * @Author ddwl.
     * @Date 2022/5/28 19:25
    **/
    private static void compress(List<File> sourceFileList,
                                 ZipOutputStream zos, boolean KeepDirStructure) throws Exception {
        byte[] buf = new byte[BUFFER_SIZE];
        for (File sourceFile : sourceFileList) {
            String name = sourceFile.getName();
            if (sourceFile.isFile()) {
                // 如果是文件需要获得原始名称
                name = FileRealName(name);
                zos.putNextEntry(new ZipEntry(name));
                int len;
                FileInputStream in = new FileInputStream(sourceFile);
                while ((len = in.read(buf)) != -1) {
                    zos.write(buf, 0, len);
                }
                zos.closeEntry();
                in.close();
            } else {
                File[] listFiles = sourceFile.listFiles();
                if (listFiles == null || listFiles.length == 0) {
                    if (KeepDirStructure) {
                        zos.putNextEntry(new ZipEntry(name + "/"));
                        zos.closeEntry();
                    }

                } else {
                    for (File file : listFiles) {
                        if (KeepDirStructure) {
                            compress(file, zos, name + "/" + file.getName(),
                                    KeepDirStructure);
                        } else {
                            compress(file, zos, file.getName(),
                                    KeepDirStructure);
                        }

                    }
                }
            }
        }
    }


    /**
     * TODO 判断文件类型
     * @param fileName fileName
     * @return boolean
     * @Author ddwl.
     * @Date 2022/5/30 11:04
    **/
    public static boolean isWendang(String fileName){
        return Constant.wendang.contains(getFileSuffix(fileName).toLowerCase());
    }

    public static boolean isShipin(String fileName){
        return Constant.shipin.contains(getFileSuffix(fileName).toLowerCase());
    }

    public static boolean isTupian(String fileName){
        return Constant.tupian.contains(getFileSuffix(fileName).toLowerCase());
    }

    public static boolean isYasuo(String fileName){
        return Constant.yasuo.contains(getFileSuffix(fileName).toLowerCase());
    }

    public static boolean isOther(String fileName){
        return !isWendang(fileName)&&
               !isTupian(fileName)&&
               !isShipin(fileName)&&
               !isYasuo(fileName);
    }
    // 筛选视频
    public static List<com.clouddisk.pojo.File> getShipin(com.clouddisk.pojo.File[] fileList){
        List<com.clouddisk.pojo.File> resList = new ArrayList<>();
        for (com.clouddisk.pojo.File file : fileList) {
            if(isShipin(file.getFileName()))
                resList.add(file);
        }
        return resList;
    }
    // 筛选图片
    public static List<com.clouddisk.pojo.File> getTupian(com.clouddisk.pojo.File[] fileList){
        List<com.clouddisk.pojo.File> resList = new ArrayList<>();
        for (com.clouddisk.pojo.File file : fileList) {
            if(isTupian(file.getFileName()))
                resList.add(file);
        }
        return resList;
    }
    // 筛选文档
    public static List<com.clouddisk.pojo.File> getWendang(com.clouddisk.pojo.File[] fileList){
        List<com.clouddisk.pojo.File> resList = new ArrayList<>();
        for (com.clouddisk.pojo.File file : fileList) {
            if(isWendang(file.getFileName()))
                resList.add(file);
        }
        return resList;
    }
    // 其他
    public static List<com.clouddisk.pojo.File> getOther(com.clouddisk.pojo.File[] fileList){
        List<com.clouddisk.pojo.File> resList = new ArrayList<>();
        for (com.clouddisk.pojo.File file : fileList) {
            if((!isWendang(file.getFileName()))&&
               (!isTupian(file.getFileName()))&&
               (!isShipin(file.getFileName())))
                resList.add(file);
        }
        return resList;
    }

    /**
     * TODO 非空文件夹不能直接删除，需要将子文件夹先删除
     *
	 * @param dir dir
     * @Author ddwl.
     * @Date 2022/6/2 9:33
    **/
    public static void deleteDir(File dir) {
        if (!dir.exists()) {
            return;
        }
        if (dir.isFile()) {//删除文件
            dir.delete();
            return;
        }
        File[] files = dir.listFiles();
        for (File f : files) {
            if (f.isDirectory() && f.list().length > 0) {
                deleteDir(f);
            } else {
                f.delete();//删除空目录和文件
            }
        }
        dir.delete();//删除空目录
    }



    public static String formatPath(String path){
        path = path.replace('\\','/');
        if(!path.endsWith("/")) path += "/";
        return path;
    }
//
//    public static void main(String []args){
//        File one = new File("D:\\mySource\\javaP\\CloudDisk\\src\\main\\resources\\mapper\\FileMapper.xml");
//        File two = new File("D:\\mySource\\javaP\\CloudDisk\\src\\main\\resources\\mapper\\FileMapper.xml");
//
//
//    }
}
