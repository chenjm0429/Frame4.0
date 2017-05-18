package com.ztesoft.level1.util;

import android.os.Environment;
import android.os.Handler;
import android.os.Message;

import com.ztesoft.level1.Level1Bean;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;

/**
 * 文件名称 : SDCardUtil
 * <p>
 * 作者信息 : chenjianming
 * <p>
 * 文件描述 : SD卡工具类
 * <p>
 * 创建时间 : 2017/5/22 16:59
 * <p>
 */
public class SDCardUtil {

    private static SDCardUtil sdUtil = null;
    private int FILESIZE = 1 * 1024;
    //根目录
    private String rootPath = Level1Bean.SD_ROOTPATH;

    public static SDCardUtil getInstance() {
        if (sdUtil == null)
            sdUtil = new SDCardUtil();
        return sdUtil;
    }

    public void setRootPath(String rootPath) {
        this.rootPath = rootPath;
    }

    public String getRootPath() {
        return this.rootPath;
    }

    /**
     * 查看SD卡是否可用
     *
     * @return
     */
    public boolean getSDCardState() {//SD卡存在且可读写
        return Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED);
    }

    /**
     * 判断SD卡上的文件夹是否存在
     *
     * @param fileName 文件相对路径
     * @return
     */
    public boolean isFileExist(String fileName) {
        File file = new File(rootPath + fileName);
        return file.exists();
    }

    /**
     * 在SD卡上创建目录
     *
     * @param dirName 目录相对路径
     * @return
     */
    public File createSDDir(String dirName) {
        File dir = new File(rootPath + dirName);
        if (!dir.exists()) {
            dir.mkdirs();
        }
        return dir;
    }

    /**
     * 在SD卡上创建文件
     *
     * @param path     文件相对路径
     * @param fileName 文件名
     * @return
     * @throws IOException
     */
    public File createSDFile(String path, String fileName) throws IOException {
        createSDDir(path);
        File file = new File(rootPath + path + fileName);
        file.createNewFile();
        return file;
    }

    /**
     * 删除文件
     *
     * @param fileName 文件相对路径
     * @return
     */
    public boolean deleteFile(String fileName) {
        File file = new File(rootPath + fileName);
        return file.delete();
    }

    public void deleteFile(String[] fileNames, String path) {
        if (!path.endsWith("/")) {
            path = path + "/";
        }
        if (fileNames.length > 0) {
            for (int i = 0; i < fileNames.length; i++) {
                File file = new File(rootPath + path + fileNames[i]);
                if (file.exists()) {
                    file.delete();
                }
            }
        }
    }

    /**
     * 获取文件名称列表
     *
     * @param filePath SD卡文件目录的相对路径，如"/MobileBI_RC/Update/"
     * @return 文件名称数组
     */
    public String[] getFileNameList(String filePath) {
        File file = new File(rootPath + filePath);
        return file.list();
    }

    /**
     * 获取文件名称列表
     *
     * @param filePath SD卡文件目录的相对路径，如"/MobileBI_RC/Update/"
     * @param filter   文件名称过滤
     * @return 文件名称数组
     */
    public String[] getFileNameList(String filePath, FilenameFilter filter) {
        File file = new File(rootPath + filePath);
        if (!file.exists()) {
            file.mkdirs();
        }
        return file.list(filter);
    }

    /**
     * 获取文件名称列表
     *
     * @param filePath SD卡文件目录的相对路径，如"/MobileBI_RC/Update/"
     * @return 文件数组
     */
    public File[] getFileList(String filePath) {
        File file = new File(rootPath + filePath);
        return file.listFiles();
    }

    /**
     * 获取文件名称列表
     *
     * @param filePath SD卡文件目录的相对路径，如"/MobileBI_RC/Update/"
     * @param filter   文件名称过滤
     * @return 文件数组
     */
    public File[] getFileList(String filePath, FilenameFilter filter) {
        File file = new File(rootPath + filePath);
        return file.listFiles(filter);
    }

    /**
     * 将一个InputStream里面的数据写入到SD卡中
     *
     * @param path     SD卡目录的相对路径，如"/MobileBI_RC/Update/"
     * @param fileName 保存后的文件名，如"test1.pdf"
     * @param input    InputStream
     * @param handler  通过handler提示下载进度
     * @param fileSize 下载文件的总大小
     * @return File
     * @throws IOException
     */
    public File write2SDFromInput(String path, String fileName, InputStream input, Handler handler,
                                  int fileSize) throws IOException {
        File file = null;
        OutputStream output = null;
        if (!path.endsWith("/")) {
            path = path + "/";
        }
        try {
            createSDDir(path);
            file = createSDFile(path, fileName);
            output = new FileOutputStream(file);
            byte[] buffer = new byte[FILESIZE];
            int leng;

            int downloadSize = 0;//已下载大小
            int downloadCount = 0;

            while ((leng = input.read(buffer)) != -1) {
                output.write(buffer, 0, leng);

                if (fileSize > 1024 * 100) {//文件大小大于100K才处理
                    downloadSize += leng;
                    // 为了防止频繁的通知导致应用吃紧，百分比增加5往通知栏发布一次
                    if ((downloadCount == 0)
                            || (int) (downloadSize * 100 / fileSize) >= downloadCount) {
                        if (handler != null) {//如果没有handler，则认为不需要通知下载进度，则不提供
                            Message message = handler.obtainMessage();
                            message.arg1 = downloadCount;
                            message.what = Level1Bean.DOWNLOAD_PROS;
                            handler.sendMessage(message);
                        }
                        downloadCount += 5;
                    }
                }
            }
            output.flush();
        } finally {
            if (output != null)
                output.close();
            if (input != null)
                input.close();
        }
        return file;
    }

    /**
     * 从SD卡中读取文本文件
     *
     * @param path     SD卡目录的相对路径，如"/MobileBI_RC/Update"
     * @param fileName 需要读取的文件名
     * @return 文本内容
     */
    public String readTxtFileFromSD(String path, String fileName) {
        if (!path.endsWith("/")) {
            path = path + "/";
        }
        path = rootPath + path + fileName;
        return readTxtFileFromSD(path);
    }

    /**
     * 从SD卡中读取文本文件
     *
     * @param path SD卡文件的相对路径，如"/MobileBI_RC/Update/1.txt"
     * @return 文本内容
     */
    public String readTxtFileFromSD(String path) {
        File file = new File(path);
        if (file != null && file.exists() && file.isFile()) {
            return readTxtFileFromSD(file);
        } else {
            return "";
        }
    }

    /**
     * 从SD卡中读取文本文件
     *
     * @param file
     * @return 文本内容
     */
    public String readTxtFileFromSD(File file) {
        String line = null;
        StringBuffer sb = new StringBuffer();
        BufferedReader buffer = null;
        InputStreamReader inputReader = null;
        FileInputStream fileInput = null;
        try {
            fileInput = new FileInputStream(file);
            inputReader = new InputStreamReader(fileInput);
            buffer = new BufferedReader(inputReader);
            while ((line = buffer.readLine()) != null) {
                sb.append(line);
            }
        } catch (Exception e) {
//			Log.e("sd read file", e.getMessage());
        } finally {
            try {
                if (buffer != null)
                    buffer.close();
                if (inputReader != null)
                    inputReader.close();
                if (fileInput != null)
                    fileInput.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return sb.toString();
    }
}
