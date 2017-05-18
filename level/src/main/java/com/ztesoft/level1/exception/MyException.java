package com.ztesoft.level1.exception;

/**
 * 网络错误：
 * 未连接到服务
 * 未接到服务返回
 */
public class MyException extends Exception {

    private static final long serialVersionUID = 1L;

    public MyException(String message) {
        super(message);
    }
}
