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
    public final static int encryFlag = 0;
    /**
     * 加密密钥，使用时可修改
     */
    public final static String encrykey = "dfg*&#33";

    /**
     * SharePreferences文件名称，使用时可修改
     */
    public final static String SHARE_PREFERENCES_NAME = "com.ztesoft.govmrkt.preferences.config";

    public final static String WELCOME_VERSION = "1";

    /**
     * 自定义文件夹跟目录
     */
    public final static String FILE_LOCALPATH = "Frame4.0/";

    /**
     * 自动升级下载相关
     */
    public final static String AUTOUPDATE_LOCALPATH = FILE_LOCALPATH + "Update/";
    /**
     * 下载最新安装包的名称
     */
    public final static String AUTOUPDATE_FILENAME = "app_update.apk";

    /**
     * 图片下载相关
     */
    public final static String IMAGES_LOCALPATH = FILE_LOCALPATH + "Images/";

    /**
     * 录音文件
     */
    public final static String RECORD_LOCALPATH = FILE_LOCALPATH + "Record/";

    /**
     * 文件下载相关
     */
    public final static String DOWNLOAD_LOCALPATH = FILE_LOCALPATH + "Download/";

    /**
     * 邮件截图路径
     */
    public final static String MAIL_LOCALPATH = FILE_LOCALPATH + "Mail/";

    /**
     * 错误日志保存地址
     */
    public final static String ERROR_LOCALPATH = FILE_LOCALPATH + "CrashInfos/";
}
