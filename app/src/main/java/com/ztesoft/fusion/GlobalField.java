package com.ztesoft.fusion;

import android.content.Context;

/**
 * 文件名称 : GlobalField
 * <p>
 * 作者信息 : chenjianming
 * <p>
 * 文件描述 : 服务器传递的全局数据
 * <p>
 * 创建时间 : 2017/3/23 14:55
 * <p>
 */
public class GlobalField {

    private Context context;

    // 用户信息
    private String staffId;
    private String staffName;

    private String rangeId;
    private String jobId;
    private String jobName;

    private String provCode; // 省编码
    private String provName;
    private String cityCode; // 市编码
    private String countyCode; // 区县编码
    private String villageCode; // 乡镇编码

    public GlobalField(Context context) {
        this.context = context;
    }

    public String getStaffId() {
        return staffId;
    }

    public void setStaffId(String staffId) {
        this.staffId = staffId;
    }

    public String getStaffName() {
        return staffName;
    }

    public void setStaffName(String staffName) {
        this.staffName = staffName;
    }

    public String getRangeId() {
        return rangeId;
    }

    public void setRangeId(String rangeId) {
        this.rangeId = rangeId;
    }

    public String getJobId() {
        return jobId;
    }

    public void setJobId(String jobId) {
        this.jobId = jobId;
    }

    public String getJobName() {
        return jobName;
    }

    public void setJobName(String jobName) {
        this.jobName = jobName;
    }

    public String getProvCode() {
        return provCode;
    }

    public void setProvCode(String provCode) {
        this.provCode = provCode;
    }

    public String getProvName() {
        return provName;
    }

    public void setProvName(String provName) {
        this.provName = provName;
    }

    public String getCityCode() {
        return cityCode;
    }

    public void setCityCode(String cityCode) {
        this.cityCode = cityCode;
    }

    public String getCountyCode() {
        return countyCode;
    }

    public void setCountyCode(String countyCode) {
        this.countyCode = countyCode;
    }

    public String getVillageCode() {
        return villageCode;
    }

    public void setVillageCode(String villageCode) {
        this.villageCode = villageCode;
    }
}
