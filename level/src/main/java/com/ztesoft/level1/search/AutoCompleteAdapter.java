package com.ztesoft.level1.search;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.graphics.Color;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.TextView;

import com.ztesoft.level1.R;

public class AutoCompleteAdapter extends BaseAdapter implements Filterable {
    private Context context;
    private ArrayFilter mFilter;
    private List<String[]> mOriginalValues;//所有的Item  
    private List<String[]> mObjects;//过滤后的item  
    private final Object mLock = new Object();
    private int maxMatch = 10;//最多显示多少个选项,负数表示全部  
    private OnClickListener titleClick;
    private int STYLE = 0;

    private String bgColor = "#ffffff";
    private String textColor = "#000000";

    public AutoCompleteAdapter(Context context, List<String[]> mOriginalValues, List<String[]>
            mObjects, int maxMatch, OnClickListener titleClick) {
        this.context = context;
        this.mOriginalValues = mOriginalValues;
        this.maxMatch = maxMatch;
        this.titleClick = titleClick;
        this.mObjects = mObjects;
    }

    @Override
    public Filter getFilter() {
        if (mFilter == null) {
            mFilter = new ArrayFilter();
        }
        return mFilter;
    }

    private class ArrayFilter extends Filter {

        @Override
        protected FilterResults performFiltering(CharSequence prefix) {
            FilterResults results = new FilterResults();

            if (prefix == null || prefix.length() == 0) {
                synchronized (mLock) {
                    List<String[]> list = mOriginalValues;
                    results.values = list;
                    results.count = list.size();
                    return results;
                }
            } else {
                String prefixString = prefix.toString().toLowerCase();

                final int count = mOriginalValues.size();

                final ArrayList<String[]> newValues = new ArrayList<String[]>(count);

                for (int i = 0; i < count; i++) {
                    final String[] value = mOriginalValues.get(i);
                    final String valueText = value[1].toLowerCase();

                    if (valueText.contains(prefixString)) {//匹配所有  
                        newValues.add(value);
                    }
                    if (maxMatch > 0) {//有数量限制    
                        if (newValues.size() > maxMatch - 1) {//不要太多    
                            break;
                        }
                    }
                }

                results.values = newValues;
                results.count = newValues.size();
            }

            return results;
        }

        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {
            mObjects = (List<String[]>) results.values;
            if (results.count > 0) {
                notifyDataSetChanged();
            } else {
                notifyDataSetInvalidated();
            }
        }
    }

    @Override
    public int getCount() {
        return mObjects.size();
    }

    @Override
    public Object getItem(int position) {
        //此方法有误，尽量不要使用  
        return mObjects.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        ViewHolder holder = null;
        if (convertView == null) {
            holder = new ViewHolder();
            if (STYLE == 0) {
                convertView = new LinearLayout(context);
            } else {
                convertView = new LinearLayout(context) {
                    @Override
                    public boolean onInterceptTouchEvent(MotionEvent ev) {
                        return false;
                    }
                };
            }
            convertView.setBackgroundColor(Color.parseColor(bgColor));
            TextView tv = new TextView(context);
            tv.setTextColor(Color.parseColor(textColor));
            tv.setPadding(5, 0, 0, 0);
            TextView tv1 = new TextView(context);
            tv1.setVisibility(View.GONE);
            TextView iv = new TextView(context);
            iv.setText("进入");
            iv.setGravity(Gravity.CENTER);
            iv.setSingleLine(true);
            iv.setTextColor(Color.WHITE);
            iv.setBackgroundResource(R.drawable.box_bg6_2);
            if (STYLE == 0) {
                iv.setVisibility(View.VISIBLE);
            } else {
                iv.setVisibility(View.GONE);
            }
            LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(LayoutParams
                    .WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
            //此处相当于布局文件中的android:layout_marginRight
            lp.setMargins(0, 0, 5, 0);
            iv.setLayoutParams(lp);
            ((LinearLayout) convertView).setOrientation(LinearLayout.HORIZONTAL);
            ((LinearLayout) convertView).setPadding(0, 5, 0, 5);
            ((LinearLayout) convertView).addView(tv, new LinearLayout.LayoutParams(LayoutParams
                    .MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT, 1f));
            ((LinearLayout) convertView).addView(tv1);
            ((LinearLayout) convertView).addView(iv);
            holder.tv = tv;
            holder.tvid = tv1;
            holder.iv = iv;

            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        holder.tv.setText(mObjects.get(position)[1]);
        holder.tvid.setText(mObjects.get(position)[0]);
        if (STYLE == 0) {
            holder.iv.setOnClickListener(titleClick);

        } else {
            holder.tv.setOnClickListener(titleClick);
        }
        convertView.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
            }
        });
        return convertView;
    }

    class ViewHolder {
        TextView tv;
        TextView tvid;
        TextView iv;
    }

    public List<String[]> getAllItems() {
        return mOriginalValues;
    }

    public void setSTYLE(int sTYLE) {
        STYLE = sTYLE;
    }

    public void setBgColor(String bgColor) {
        this.bgColor = bgColor;
    }

    public void setTextColor(String textColor) {
        this.textColor = textColor;
    }
} 