package com.ztesoft.ui.main.entity;

import android.os.Parcel;
import android.os.Parcelable;

import com.ztesoft.ui.base.BaseFragment;

/**
 * 文件名称 : MenuEntity
 * <p>
 * 作者信息 : chenjianming
 * <p>
 * 文件描述 : 底部菜单模型
 * <p>
 * 创建时间 : 2017/3/23 15:57
 * <p>
 */
public class MenuEntity implements Parcelable {

    // 菜单id
    private String menuId;

    // 菜单名称
    private String menuName;

    // 菜单默认图标资源文件
    private int menuIcon;

    // 菜单选中图标资源文件
    private int menuIconSelected;

    private BaseFragment fragment;

    public MenuEntity() {

    }

    public String getMenuId() {
        return menuId;
    }

    public String getMenuName() {
        return menuName;
    }

    public int getMenuIcon() {
        return menuIcon;
    }

    public int getMenuIconSelected() {
        return menuIconSelected;
    }

    public BaseFragment getFragment() {
        return fragment;
    }

    public void setMenuId(String menuId) {
        this.menuId = menuId;
    }

    public void setMenuName(String menuName) {
        this.menuName = menuName;
    }

    public void setMenuIcon(int menuIcon) {
        this.menuIcon = menuIcon;
    }

    public void setMenuIconSelected(int menuIconSelected) {
        this.menuIconSelected = menuIconSelected;
    }

    public void setFragment(BaseFragment fragment) {
        this.fragment = fragment;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(menuId);
        dest.writeString(menuName);
        dest.writeInt(menuIcon);
        dest.writeInt(menuIconSelected);
    }

    public static final Parcelable.Creator<MenuEntity> CREATOR = new Creator<MenuEntity>() {
        @Override
        public MenuEntity createFromParcel(Parcel source) {
            return new MenuEntity(source);
        }

        @Override
        public MenuEntity[] newArray(int size) {
            return new MenuEntity[size];
        }
    };

    private MenuEntity(Parcel in) {
        menuId = in.readString();
        menuName = in.readString();
        menuIcon = in.readInt();
        menuIconSelected = in.readInt();
    }
}