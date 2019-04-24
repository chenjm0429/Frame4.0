package com.ztesoft.level1.pickview;

import com.contrarywind.interfaces.IPickerViewData;

import java.util.ArrayList;
import java.util.List;

/**
 * 文件名称 : PickerViewBean
 * <p>
 * 作者信息 : chenjianming
 * <p>
 * 文件描述 : 选择器数据对象
 * <p>
 * 创建时间 : 2018/11/20 13:51
 * <p>
 */
public class PickerViewBean implements IPickerViewData {

    private String code = "";
    private String name = "";
    private List<PickerViewBean> childs = new ArrayList<>();

    public PickerViewBean() {
        
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<PickerViewBean> getChilds() {
        return childs;
    }

    public void setChilds(List<PickerViewBean> childs) {
        this.childs = childs;
    }

    @Override
    public String getPickerViewText() {
        return name;
    }
}