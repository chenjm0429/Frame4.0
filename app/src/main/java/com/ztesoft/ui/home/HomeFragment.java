package com.ztesoft.ui.home;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;

import com.ztesoft.R;
import com.ztesoft.level1.ui.AutoScrollTextView;
import com.ztesoft.level1.ui.ButtonGroupUI;
import com.ztesoft.level1.util.PromptUtils;
import com.ztesoft.ui.base.BaseFragment;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import okhttp3.Call;

/**
 * 文件名称 : HomeFragment
 * <p>
 * 作者信息 : chenjianming
 * <p>
 * 文件描述 :首页Fragment
 * <p>
 * 创建时间 : 2017/3/23 18:05
 * <p>
 */
public class HomeFragment extends BaseFragment {

    private Button mButton;

    private LinearLayout layout1, layout2;

    @Override
    protected int getContentViewId() {
        return R.layout.layout_home;
    }

    @Override
    protected void initData(Bundle arguments) {

    }

    @Override
    protected void initView(Bundle savedInstanceState) {

        if (null == getView()) {
            PromptUtils.instance.displayToastId(mActivity, false, R.string.error_page_load);
            return;
        }

        mButton = getView().findViewById(R.id.button);
        mButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                mActivity.menuSelected(1);
            }
        });

        layout1 = getView().findViewById(R.id.layout_tt);
        layout2 = getView().findViewById(R.id.layout_zz);

        AutoScrollTextView ast = new AutoScrollTextView(mActivity, new AutoScrollTextView
                .OnScrollChangeListener() {


            @Override
            public void onScrollChange(int numTag) {

            }
        });
        ast.setVertical(true);
        layout1.addView(ast, LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams
                .MATCH_PARENT);

        String[] names = {"测试看看效果啊1", "测试看看效果啊2", "测试看看效果啊3", "测试看看效果啊4", "测试看看效果啊5"};

        ast.create(names, 2);


        ButtonGroupUI bgi = new ButtonGroupUI(mActivity);
//        bgi.setBackgroundResource(R.color.layout_select_color);
        bgi.setButtonNum(3);
        bgi.setVertical(true);
        bgi.create(getData());

        bgi.setOnSelectListener(new ButtonGroupUI.OnSelectListener() {
            @Override
            public void onSelected(int position, String currentCode, String currentName) {
                PromptUtils.instance.displayToastString(mActivity, false, position + ", " +
                        currentCode + ", " + currentName);
            }
        });

        layout2.addView(bgi, LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams
                .WRAP_CONTENT);
    }

    @Override
    public void addParamObject(JSONObject param) throws JSONException {

    }

    @Override
    protected void changeTitleBarStatus() {
        if (null != mActivity)
            mActivity.mTitleText.setText("改变看看啊");
    }

    @Override
    public void updateUI(JSONObject resultJsonObject, Call call) throws Exception {

    }

    private JSONArray getData() {
        JSONArray array = new JSONArray();

        try {
            for (int i = 0; i < 7; i++) {
                JSONObject obj = new JSONObject();

                obj.put("code", "" + i);
                obj.put("name", "测试" + i);

                array.put(obj);
            }
        } catch (JSONException e) {

        }

        return array;
    }
}
