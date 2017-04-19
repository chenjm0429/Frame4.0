package com.ztesoft.ui.widget;

import android.content.Context;
import android.graphics.Color;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.ztesoft.R;
import com.ztesoft.ui.main.entity.MenuEntity;


/**
 * 文件名称 : MenuItemView
 * <p>
 * 作者信息 : chenjianming
 * <p>
 * 文件描述 : 单个菜单视图
 * <p>
 * 创建时间 : 2017/3/23 16:02
 * <p>
 */
public class MenuItemView extends RelativeLayout {
    /**
     * 当前上下文
     */
    private Context context;

    /**
     * 菜单名称
     */
    private TextView mMenuNameTv;

    /**
     * 菜单图标
     */
    private ImageView mMenuIv;

    /**
     * 菜单布局
     */
    private RelativeLayout mMenuLayout;

    /**
     * 菜单数值对象
     */
    private MenuEntity mMenuEntity;

    public MenuItemView(Context context) {
        super(context);
    }

    public MenuItemView(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.context = context;
        initView();
    }

    public MenuItemView(Context context, MenuEntity menuEntity) {
        super(context);
        this.context = context;
        this.mMenuEntity = menuEntity;
        initView();
    }

    /**
     * 初始化控件
     */
    private void initView() {
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context
                .LAYOUT_INFLATER_SERVICE);
        mMenuLayout = (RelativeLayout) inflater.inflate(R.layout.layout_menu_item, null);
        mMenuNameTv = (TextView) mMenuLayout.findViewById(R.id.menu_name);
        mMenuIv = (ImageView) mMenuLayout.findViewById(R.id.menu_icon);

        if (null != mMenuEntity) {
            mMenuIv.setBackgroundResource(mMenuEntity.getMenuIcon());
            mMenuNameTv.setText(mMenuEntity.getMenuName());
        }

        this.addView(mMenuLayout, new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams
                .WRAP_CONTENT));
    }

    /**
     * 返回该菜单的图标
     */
    public ImageView getMenuIcon() {
        return mMenuIv;
    }

    /**
     * 返回该菜单的名称布局
     */
    public TextView getMenuNameTv() {
        return mMenuNameTv;
    }

    /**
     * 选中菜单
     */
    public void selecteMenu() {
        if (null != mMenuIv) {
            mMenuIv.setBackgroundResource(mMenuEntity.getMenuIconSelected());
            mMenuNameTv.setTextColor(Color.parseColor("#1E95FF"));
        }
    }

    /**
     * 取消选中菜单
     */
    public void unSelecteMenu() {
        if (null != mMenuIv) {
            mMenuIv.setBackgroundResource(mMenuEntity.getMenuIcon());
            mMenuNameTv.setTextColor(Color.parseColor("#8D8D8D"));
        }
    }
}
