package com.ztesoft.fusion;

import android.content.Context;
import android.text.TextUtils;

import com.ztesoft.level1.Level1Bean;
import com.ztesoft.level1.util.SharedPreferencesUtil;

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

    /**
     * 是否保存在SharePreference中
     */
    private boolean isSaveFile = false;

    private SharedPreferencesUtil shareUtil;

    // 用户信息
    private String staffId;
    private String staffName;

    private String rangeId;
    private String jobId;
    private String jobName;

    public GlobalField(Context context, boolean isSaveFile) {
        this.isSaveFile = isSaveFile;

        if (isSaveFile)
            shareUtil = new SharedPreferencesUtil(context, Level1Bean.SHARE_PREFERENCES_NAME);
    }

    public String getStaffId() {
        if (TextUtils.isEmpty(staffId) && isSaveFile) {
            staffId = shareUtil.getString("staffId", "");
        }
        return staffId;
    }

    public void setStaffId(String staffId) {
        this.staffId = staffId;
        if (isSaveFile) {
            shareUtil.putString("staffId", staffId);
        }
    }

    public String getStaffName() {
        if (TextUtils.isEmpty(staffName) && isSaveFile) {
            staffName = shareUtil.getString("staffName", "");
        }
        return staffName;
    }

    public void setStaffName(String staffName) {
        this.staffName = staffName;
        if (isSaveFile) {
            shareUtil.putString("staffName", staffName);
        }
    }

    public String getRangeId() {
        if (TextUtils.isEmpty(rangeId) && isSaveFile) {
            rangeId = shareUtil.getString("rangeId", "");
        }
        return rangeId;
    }

    public void setRangeId(String rangeId) {
        this.rangeId = rangeId;
        if (isSaveFile) {
            shareUtil.putString("rangeId", rangeId);
        }
    }

    public String getJobId() {
        if (TextUtils.isEmpty(jobId) && isSaveFile) {
            jobId = shareUtil.getString("jobId", "");
        }
        return jobId;
    }

    public void setJobId(String jobId) {
        this.jobId = jobId;
        if (isSaveFile) {
            shareUtil.putString("jobId", jobId);
        }
    }

    public String getJobName() {
        if (TextUtils.isEmpty(jobName) && isSaveFile) {
            jobName = shareUtil.getString("jobName", "");
        }
        return jobName;
    }

    public void setJobName(String jobName) {
        this.jobName = jobName;
        if (isSaveFile) {
            shareUtil.putString("jobName", jobName);
        }
    }
}
