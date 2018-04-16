package com.ztesoft.fusion;

/**
 * 文件名称 : FusionCode
 * <p>
 * 作者信息 : chenjianming
 * <p>
 * 文件描述 : 保存应用常量
 * <p>
 * 创建时间 : 2017/3/23 14:55
 * <p>
 */
public class FusionCode {

    /**
     * 加密方式，默认不加密，根据需求修改
     */
    public final static int encryptFlag = 0;
    /**
     * 加密密钥，使用时可修改
     */
    public final static String encryptKey = "dfg*&#33";

    public final static String WELCOME_VERSION = "1";

    /**
     * 自定义文件夹跟目录
     */
    public final static String FILE_LOCAL_PATH = "Frame4.0/";

    /**
     * 自动升级下载相关
     */
    public final static String AUTO_UPDATE_LOCAL_PATH = FILE_LOCAL_PATH + "Update/";
    /**
     * 下载最新安装包的名称
     */
    public final static String AUTO_UPDATE_FILENAME = "app_update.apk";

    /**
     * 图片下载相关
     */
    public final static String IMAGES_LOCAL_PATH = FILE_LOCAL_PATH + "Images/";

    /**
     * 录音文件
     */
    public final static String RECORD_LOCAL_PATH = FILE_LOCAL_PATH + "Record/";

    /**
     * 文件下载相关
     */
    public final static String DOWNLOAD_LOCAL_PATH = FILE_LOCAL_PATH + "Download/";

    /**
     * 邮件截图路径
     */
    public final static String MAIL_LOCAL_PATH = FILE_LOCAL_PATH + "Mail/";

    /**
     * 错误日志保存地址
     */
    public final static String ERROR_LOCAL_PATH = FILE_LOCAL_PATH + "CrashInfo/";
}
