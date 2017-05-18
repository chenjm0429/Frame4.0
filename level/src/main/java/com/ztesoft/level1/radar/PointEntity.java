package com.ztesoft.level1.radar;

/**
 * 文件名称 : PointEntity
 * <p>
 * 作者信息 : chenjianming
 * <p>
 * 文件描述 : 坐标点数据模型
 * <p>
 * 创建时间 : 2017/3/24 15:20
 * <p>
 */
public class PointEntity {

    // 编码(kpiId)
    private String id;

    // 指标名称(kpiName)
    private String name;

    // 指标问题严重等级，3表示非常严重，2表示严重，1表示一般严重
    private int level;

    private String chkId;

    // x坐标
    private int xPoint;

    // y坐标
    private int yPoint;

    public PointEntity() {

    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getLevel() {
        return level;
    }

    public void setLevel(int level) {
        this.level = level;
    }

    public String getChkId() {
        return chkId;
    }

    public void setChkId(String chkId) {
        this.chkId = chkId;
    }

    public int getxPoint() {
        return xPoint;
    }

    public void setxPoint(int xPoint) {
        this.xPoint = xPoint;
    }

    public int getyPoint() {
        return yPoint;
    }

    public void setyPoint(int yPoint) {
        this.yPoint = yPoint;
    }
}