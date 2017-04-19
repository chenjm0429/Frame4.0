package com.ztesoft.ui.home;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.ztesoft.R;
import com.ztesoft.ui.base.BaseActivity;
import com.ztesoft.ui.base.BaseFragment;
import com.ztesoft.ui.main.MainActivity;
import com.ztesoft.utils.PromptUtils;

import org.json.JSONObject;

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

    private Context mContext;
    private Button mButton;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle
            savedInstanceState) {

        View view = inflater.inflate(R.layout.layout_home, container, false);

        mContext = getActivity();

        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        mButton = (Button) getView().findViewById(R.id.button);
        mButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mFragmentCallBack.setTitleText("改变看看啊");

                if (getActivity() instanceof MainActivity) {
                    ((MainActivity) getActivity()).menuSelected(1);
                }
                
                
            }
        });

    }

    @Override
    public void updateUI(JSONObject resultJsonObject) {
        
    }
}
