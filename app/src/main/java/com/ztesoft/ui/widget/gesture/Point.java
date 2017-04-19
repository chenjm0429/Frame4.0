package com.ztesoft.ui.widget.gesture;

/**
 * 文件名称 : Point
 * <p>
 * 作者信息 : chenjianming
 * <p>
 * 文件描述 : 手势解锁，点位置
 * <p>
 * 创建时间 : 2017/3/23 17:19
 * <p>
 */
public class Point {

    public static int STATE_NORMAL = 0; // 未选中
    public static int STATE_CHECK = 1; // 选中 e
    public static int STATE_CHECK_ERROR = 2; // 已经选中,但是输错误

    public float x;
    public float y;
    public int state = 0;
    public int index = 0;// 下标

    public Point() {

    }

    public Point(float x, float y) {
        this.x = x;
        this.y = y;
    }
}