package com.ztesoft.ui.main.entity;

import android.app.Fragment;

import java.io.Serializable;

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
public class MenuEntity implements Serializable {
    private static final long serialVersionUID = 1L;

    // 菜单id
    private String menuId;

    // 菜单名称
    private String menuName;

    // 菜单默认图标资源文件
    private int menuIcon;

    // 菜单选中图标资源文件
    private int menuIconSelected;

    private Fragment fragment;

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

    public Fragment getFragment() {
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

    public void setFragment(Fragment fragment) {
        this.fragment = fragment;
    }
}
