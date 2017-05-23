package com.ztesoft.level1;

import android.content.Intent;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.TableRow;

import com.ztesoft.level1.radiobutton.DateUI;
import com.ztesoft.level1.table.ScrollListView;
import com.ztesoft.level1.util.DateUtil;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * 日志查看类
 *
 * @author wanghx2
 *         必须在bundle中传递当前用户编码staffId
 */
public class LogTemplate extends CommonPage {

    private LinearLayout topLayout;
    private LinearLayout bodyLayout;
    private DateUI dateUI;

    private String checkRptCode;
    private String checkRptName;
    private String checkStaffId;
    private String checkStaffName;

    private String visitType;
    private String statDate;
    private String dateType;
    private String rptCode;
    private String staffId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        layout = new LinearLayout(this);
        layout.setOrientation(LinearLayout.VERTICAL);
//		layout.setBackgroundColor(ThemeColorBean.layout_bg);
        setContentView(layout);

        topLayout = new LinearLayout(this);
        int pad = Level1Util.dip2px(this, 3);
        int pad1 = Level1Util.dip2px(this, 10);
        topLayout.setPadding(pad1, pad, pad1, pad);
        topLayout.setGravity(Gravity.LEFT | Gravity.CENTER_VERTICAL);


        bodyLayout = new LinearLayout(this);
        bodyLayout.setOrientation(LinearLayout.VERTICAL);

        layout.addView(topLayout, LayoutParams.FILL_PARENT,
                LayoutParams.WRAP_CONTENT);
        layout.addView(bodyLayout);

        Bundle bundle = this.getIntent().getExtras();
        if (bundle != null) {
            visitType = bundle.getString("visitType");
            statDate = bundle.getString("statDate");
            staffId = bundle.getString("staffId");
            checkRptCode = bundle.getString("checkRptCode");
            checkStaffId = bundle.getString("checkStaffId");
            checkRptName = bundle.getString("checkRptName");
            checkStaffName = bundle.getString("checkStaffName");
        }
        if (statDate == null || "".equals(statDate)) {
            statDate = DateUtil.getInstance().getToday("yyyyMM");
        }
        if (visitType == null || "".equals(visitType)) {
            visitType = "root";
        }
        initTopLayout();
        initKpiData(Level1Bean.SERVICE_PATH + "LogTemplate");
    }

    public void addParamObject(JSONObject param) throws JSONException {
        param.put("visitType", visitType);
        param.put("statDate", statDate);
        param.put("staffId", staffId);
        if ("sub1".equals(visitType)) {
            param.put("checkRptCode", checkRptCode);
        } else if ("sub2".equals(visitType)) {
            param.put("checkRptCode", checkRptCode);
            param.put("checkStaffId", checkStaffId);
        }
    }

    public void initAllLayout() throws Exception {
        if (jsonObj.has("statDate")) {
            statDate = jsonObj.optString("statDate");
        }
        dateUI.setMinDate(jsonObj.optString("firstDate"));
        dateUI.setMaxDate(jsonObj.optString("latestDate"));
        initBodyLayout();
    }

    public void initTopLayout() {
        if (topLayout.getChildCount() > 0) {
            topLayout.removeAllViews();
        }
        LinearLayout dateLayout = new LinearLayout(this);
        dateLayout.setGravity(Gravity.LEFT | Gravity.CENTER_VERTICAL);

        dateUI = new DateUI(this);
        dateUI.setOnSelectListener(new DateUI.OnSelectListener() {
            @Override
            public void OnSelected(String date) {
                statDate = date;
                initKpiData(Level1Bean.SERVICE_PATH + "LogTemplate");
            }
        });
        dateUI.setDateType("M");
        dateUI.create(statDate);

        dateLayout.addView(dateUI);
        topLayout.addView(dateLayout);
    }

    public void initBodyLayout() throws Exception {
        if (bodyLayout.getChildCount() > 0) {
            bodyLayout.removeAllViews();
        }
        ScrollListView sv = new ScrollListView(this);
        sv.setTableHeadJSON(jsonObj);
        sv.setTableBodyJSON(jsonObj.optJSONArray("tableArray"));
        if (!"sub2".equals(visitType)) {
            sv.setRowListener(new cityOnclickListener());
        } else {
            sv.setColumnLockNum(0);
        }
        sv.create(Level1Bean.actualWidth);
        bodyLayout.addView(sv, LinearLayout.LayoutParams.FILL_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT);

    }

    class cityOnclickListener implements OnClickListener {
        @Override
        public void onClick(View v) {
            TableRow tableRow = (TableRow) v;
            JSONObject itemObj = (JSONObject) tableRow.getTag();
            if ("root".equals(visitType)) {
                checkRptName = itemObj.optString("v2");
                gotoNext(itemObj.optString("c2"));
            } else if ("sub1".equals(visitType)) {
                checkStaffName = itemObj.optString("v1");
                gotoNext(itemObj.optString("c1"));
            }
        }
    }

    /***
     * 向下钻取
     */
    private void gotoNext(String value) {
        Bundle bundle = new Bundle();
        bundle.putString("staffId", staffId);
        bundle.putString("rptCode", rptCode);
        bundle.putString("statDate", statDate);
        bundle.putString("dateType", dateType);
        if ("root".equals(visitType)) {
            bundle.putString("checkRptCode", value);
            bundle.putString("checkRptName", checkRptName);
            bundle.putString("visitType", "sub1");
        } else if ("sub1".equals(visitType)) {
            bundle.putString("checkRptCode", checkRptCode);
            bundle.putString("checkStaffId", value);
            bundle.putString("checkRptName", checkRptName);
            bundle.putString("checkStaffName", checkStaffName);
            bundle.putString("visitType", "sub2");
        }
        nextActivity(LogTemplate.class, bundle);
    }

    private void nextActivity(Class<?> nextClass, Bundle bundle) {
        if (!isClickable()) {
            return;
        }
        Intent intent = new Intent();
        intent.setClass(getApplicationContext(), nextClass);
        intent.putExtras(bundle);
        startActivity(intent);
    }
}
