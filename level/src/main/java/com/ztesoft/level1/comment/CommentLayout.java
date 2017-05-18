package com.ztesoft.level1.comment;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.FontMetrics;
import android.graphics.drawable.ColorDrawable;
import android.text.Html;
import android.text.TextPaint;
import android.text.TextUtils.TruncateAt;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.BaseAdapter;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.ztesoft.level1.Level1Bean;
import com.ztesoft.level1.Level1Util;
import com.ztesoft.level1.R;
import com.ztesoft.level1.ui.MyAlertDialog;
import com.ztesoft.level1.ui.MyProgressDialog;
import com.ztesoft.level1.util.ServiceThread;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class CommentLayout extends LinearLayout {
    protected TextView title;
    protected LinearLayout topLayoutEdit;
    protected com.ztesoft.level1.ui.ClearEditText editor;
    protected TextView b;//发布按钮
    protected TextView rp;//回复按钮
    protected TextView cancle;//取消按钮
    protected String replayStr = "";
    protected com.ztesoft.level1.comment.CustomListView listView;
    protected String refreshVisity = "allComment";
    protected TextView tipText;//提示句
    protected JSONObject jsonObj;
    protected boolean delFlag;
    protected JSONArray commentArray;
    protected BaseAdapter adapter;
    protected String operFlag;
    protected View operView;

    private Context context;
    private String staffId;//评论人
    private String serviceUrl;//服务地址
    private String jobId;
    private String strThemeColor = "#cccccc";//主题色
    private int strHeadPic = R.drawable.person;//头像图片
    private int strSelfBubbleBGImage = R.drawable.self_bg;//自身气泡背景
    private int strOtherBubbleBGImage = R.drawable.other_bg;//其他人的气泡背景

    private long specialcommentid = 1;   //值为1说明不是从推送消息跳转到评论界面

    public String getStrThemeColor() {
        return strThemeColor;
    }

    public void setStrThemeColor(String strThemeColor) {
        this.strThemeColor = strThemeColor;
    }

    public int getStrHeadPic() {
        return strHeadPic;
    }

    public void setStrHeadPic(int strHeadPic) {
        this.strHeadPic = strHeadPic;
    }

    public int getStrSelfBubbleBGImage() {
        return strSelfBubbleBGImage;
    }

    public void setStrSelfBubbleBGImage(int strSelfBubbleBGImage) {
        this.strSelfBubbleBGImage = strSelfBubbleBGImage;
    }

    public int getStrOtherBubbleBGImage() {
        return strOtherBubbleBGImage;
    }

    public void setStrOtherBubbleBGImage(int strOtherBubbleBGImage) {
        this.strOtherBubbleBGImage = strOtherBubbleBGImage;
    }

    public void setJobId(String jobId) {
        this.jobId = jobId;
    }

    public void setStaffId(String staffId) {
        this.staffId = staffId;
    }

    public void setServiceUrl(String serviceUrl) {
        this.serviceUrl = serviceUrl;
    }

    public void setSpecialcommentid(long specialcommentid) {
        this.specialcommentid = specialcommentid;
    }

    public CommentLayout(Context context) {
        super(context);
        this.context = context;
        this.setOrientation(LinearLayout.VERTICAL);
    }

    public void create() {
        FrameLayout topFrame = new FrameLayout(context);
        LinearLayout topLayout = new LinearLayout(context);
        topLayout.setOrientation(LinearLayout.VERTICAL);

        LinearLayout topLayout1 = new LinearLayout(context);
        topLayout1.setOrientation(LinearLayout.HORIZONTAL);

        //标题
        title = new TextView(context);
        TextPaint tp = title.getPaint();
        tp.setFakeBoldText(true);
        title.setTextColor(Color.BLACK);
        title.setTextSize(TypedValue.COMPLEX_UNIT_SP, 20);
        title.setGravity(Gravity.CENTER);
        topFrame.addView(title, LayoutParams.FILL_PARENT, LayoutParams.FILL_PARENT);

        topFrame.addView(topLayout1);
        topLayout.addView(topFrame);

        topLayoutEdit = new LinearLayout(context);

        editor = new com.ztesoft.level1.ui.ClearEditText(context);
        editor.setHint(R.string.comment_hint);
        editor.setSelected(false);
        editor.clearFocus();
        topLayoutEdit.addView(editor, new LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT,
                LayoutParams.MATCH_PARENT, 1f));

        b = new TextView(context);
        b.setText(R.string.comment_publish);
        b.setTextColor(Color.BLACK);
        b.setGravity(Gravity.CENTER);
        Level1Util.textToButtonStyle(context, b, Color.parseColor(strThemeColor));
        b.setSingleLine(true);
        b.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                showCommentDialog("push");
            }

        });
        rp = new TextView(context);
        rp.setVisibility(View.GONE);
        rp.setText(R.string.system_answer);
        rp.setTextColor(Color.BLACK);
        rp.setGravity(Gravity.CENTER);
        rp.setSingleLine(true);
        Level1Util.textToButtonStyle(context, rp, Color.parseColor(strThemeColor));
        rp.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                showCommentDialog("reply");
            }
        });

        cancle = new TextView(context);
        cancle.setVisibility(View.GONE);
        cancle.setText(R.string.system_cancel);
        cancle.setTextColor(Color.BLACK);
        cancle.setGravity(Gravity.CENTER);
        cancle.setSingleLine(true);
        Level1Util.textToButtonStyle(context, cancle, Color.parseColor(strThemeColor));
        cancle.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                b.setVisibility(View.VISIBLE);
                rp.setVisibility(View.GONE);
                cancle.setVisibility(View.GONE);
                replayStr = "";
                title.setText(R.string.comment_status);
            }
        });

        LinearLayout fabuLayout = new LinearLayout(context);
        fabuLayout.setOrientation(LinearLayout.HORIZONTAL);
        fabuLayout.setGravity(Gravity.RIGHT);
        fabuLayout.addView(b);
        fabuLayout.addView(rp);
        fabuLayout.addView(cancle);
        topLayoutEdit.addView(fabuLayout);

        topLayout.addView(topLayoutEdit, LayoutParams.MATCH_PARENT, Level1Util.getRawSize
                (context, TypedValue.COMPLEX_UNIT_DIP, 40));
        this.addView(topLayout, LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);

        View v = new View(context);//分割线
        v.setBackgroundColor(Color.parseColor("#9A9A9A"));
        this.addView(v, LayoutParams.MATCH_PARENT, 1);

        listView = new com.ztesoft.level1.comment.CustomListView(context);
        listView.setSelector(new ColorDrawable(Color.TRANSPARENT));
        listView.setFadingEdgeLength(0);
        listView.setDividerHeight(0);
        listView.setCacheColorHint(Color.TRANSPARENT);
        listView.setDivider(null);
        this.addView(listView, LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);

        tipText = new TextView(context);
        tipText.setVisibility(View.GONE);
        tipText.setGravity(Gravity.CENTER);
        this.addView(tipText, LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
        this.setBackgroundColor(Color.WHITE);
        initKpiData();
    }

    //发布评论框
    public void showCommentDialog(String flag) {
        operFlag = flag;
        String commContent = editor.getText().toString();
        if (commContent == null || "".equals(commContent.trim())) {//评论为空
            Animation shake = AnimationUtils.loadAnimation(context, R.anim.shake);
            editor.startAnimation(shake);//评论为空，则抖动提示
            return;
        }
        if (commContent.length() > 800) {
            Toast.makeText(context, R.string.comment_tolong, Toast.LENGTH_SHORT).show();
            return;
        }
        commContent = commContent.replaceAll("\n", "#n#");
        JSONObject param = new JSONObject();
        try {
            param.put("visitType", flag);
            param.put("staffId", staffId);
            param.put("jobId", jobId);
            param.put("content", commContent);
            param.put("rptCode", "");
            //回复
            if ("reply".equals(flag)) {
                param.put("visitType", "reply");
                param.put("content", commContent + replayStr);
            }

        } catch (JSONException e) {
            Toast.makeText(context, context.getString(R.string.error_json), Toast.LENGTH_SHORT)
                    .show();
        }
        myShowDialog(R.string.system_loading);
        editor.setText("");//清空内容
        ServiceThread st = new ServiceThread(serviceUrl, param, context);
        st.setServiceHandler(sh);
        st.start();

        title.setText(R.string.comment_status);
    }

    public void initKpiData() {
        myShowDialog(R.string.system_loading);
        // 判断用户权限，并预录入相关信息
        JSONObject param = new JSONObject();
        try {
            param.put("visitType", refreshVisity);
            param.put("topicCode", "Comment");
            param.put("staffId", staffId);
            param.put("jobId", jobId);
            param.put("rptCode", "");
            param.put("commentId", specialcommentid == 1 ? "" : specialcommentid);
            param.put("commentWay", specialcommentid == 1 ? "new" : "his");
            specialcommentid = 1;

        } catch (JSONException e) {
            Toast.makeText(context, context.getString(R.string.error_json), Toast.LENGTH_LONG)
                    .show();
        }
        ServiceThread st = new ServiceThread(serviceUrl, param, context);
        st.setServiceHandler(sh);
        st.start();
    }

    public void initAllLayout() throws JSONException {
        listView.setVisibility(View.VISIBLE);
        tipText.setVisibility(View.GONE);
        if (jsonObj.has("commentArray")) {
            if (jsonObj.has("delFlag")) {//默认没有删除权限
                delFlag = jsonObj.getBoolean("delFlag");
            } else {
                delFlag = false;
            }

            b.setVisibility(View.VISIBLE);
            rp.setVisibility(View.GONE);
            cancle.setVisibility(View.GONE);
            replayStr = "";

            title.setText(R.string.comment_status);
            topLayoutEdit.setVisibility(View.VISIBLE);
            commentArray = jsonObj.getJSONArray("commentArray");

            adapter = new BaseAdapter() {
                public View getView(int position, View convertView, ViewGroup parent) {
                    return commentLayout(position);
                }

                public long getItemId(int position) {
                    return 0;
                }

                public Object getItem(int position) {
                    return null;
                }

                public int getCount() {
                    return commentArray.length();
                }
            };
            listView.setAdapter(adapter);
            listView.setDivider(null);

            listView.setOnRefreshListener(new CustomListView.OnRefreshListener() {
                @Override
                public void onRefresh() {
                    //重新触发请求，获取最新的评论内容
                    JSONObject param = new JSONObject();
                    try {
                        param.put("visitType", refreshVisity);

                        if (commentArray.length() > 0) {
                            param.put("commentId", commentArray.getJSONObject(0).getString("v_1"));
                        } else {
                            param.put("commentId", "");
                        }
                        param.put("commentWay", "new");
                        param.put("staffId", staffId);
                        param.put("jobId", jobId);
                        param.put("rptCode", "");
                        queryComment(param);
                    } catch (JSONException e) {
                        Toast.makeText(context, context.getString(R.string.error_json), Toast
                                .LENGTH_SHORT).show();
                    }
                }
            });
            listView.setOnLoadListener(new CustomListView.OnLoadMoreListener() {

                @Override
                public void onLoadMore() {
                    // 重新触发请求，获取下一页的数据
                    JSONObject param = new JSONObject();
                    try {
                        param.put("visitType", refreshVisity);
                        param.put("staffId", staffId);
                        param.put("jobId", jobId);
                        if (commentArray.length() > 0) {
                            param.put("commentId", commentArray.getJSONObject(commentArray.length
                                    () - 1).getString("v_1"));
                        } else {
                            Toast.makeText(context, R.string.comment_nomore, Toast.LENGTH_SHORT)
                                    .show();
                            return;
                        }
                        param.put("commentWay", "his");
                        param.put("showDialog", "true");
                        param.put("rptCode", "");
                        queryComment(param);
                    } catch (JSONException e) {
                        Toast.makeText(context, context.getString(R.string.error_json), Toast
                                .LENGTH_SHORT).show();
                    }
                }
            });
        } else {//插入评论，则无需刷新
            if (Boolean.parseBoolean(jsonObj.optString("returnFlag", "false"))) {
                if ("push".equals(operFlag)) {
                    Toast.makeText(context, R.string.comment_success, Toast.LENGTH_SHORT).show();
                    initKpiData();
                } else if ("reply".equals(operFlag)) {
                    b.setVisibility(View.VISIBLE);
                    rp.setVisibility(View.GONE);
                    cancle.setVisibility(View.GONE);
                    initKpiData();
                    Toast.makeText(context, R.string.oper_success, Toast.LENGTH_SHORT).show();
                } else if ("delete".equals(operFlag)) {
                    operView.setVisibility(View.GONE);
                    JSONArray tempArray = new JSONArray();
                    for (int i = 0; i < commentArray.length(); i++) {
                        if (!commentArray.getJSONObject(i).getString("v_1").equals(jsonObj
                                .getString("commentId"))) {
                            tempArray.put(commentArray.getJSONObject(i));
                        }
                    }
                    commentArray = tempArray;
                    adapter.notifyDataSetChanged();
                }
            } else {
                Toast.makeText(context, jsonObj.getString("reason"), Toast.LENGTH_LONG).show();
            }
        }
    }

    //绘制单元格
    protected LinearLayout commentLayout(int position) {
        LinearLayout l = new LinearLayout(context);
        l.setOrientation(LinearLayout.HORIZONTAL);
        l.setPadding(0, 15, 0, 15);
        try {
            JSONObject obj = commentArray.getJSONObject(position);
            //评论时间+内容
            TextView staffView = new TextView(context);
            staffView.setText(obj.getString("v_3"));
            staffView.setTextColor(Color.BLACK);
            TextView timeView = new TextView(context);
            timeView.setText(obj.getString("v_4"));
            timeView.setTextColor(Color.BLACK);
            timeView.setGravity(Gravity.RIGHT);
            LinearLayout top = new LinearLayout(context);
            top.setOrientation(LinearLayout.HORIZONTAL);
            staffView.setLayoutParams(new LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT,
                    LayoutParams.MATCH_PARENT, 1));
            top.addView(staffView);
            top.addView(timeView, LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout
                    .LayoutParams.WRAP_CONTENT);

            TextView descView = new TextView(context);
            String descContent = obj.getString("v_5");
            String laterStr = descContent;
            //替换为回车符
            laterStr = laterStr.replaceAll("#n#", "<br>");
            descView.setText(Html.fromHtml(laterStr));
            //设置文本最大宽度==屏幕宽度-图片边框-头像宽度
            descView.setMaxWidth(Level1Bean.actualWidth * 2 / 3);
            descView.setMinWidth(Level1Bean.actualWidth / 3);
            descView.setTextColor(Color.BLACK);
            descView.setEllipsize(TruncateAt.END);
            Paint paint = descView.getPaint();
            FontMetrics fm = paint.getFontMetrics();
            final int textHeight = (int) Math.ceil(fm.descent - fm.ascent) * 3;
            descView.setMaxHeight(textHeight);
            descView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    TextView x = (TextView) view;
                    if (x.getEllipsize() == TruncateAt.END) {
                        x.setEllipsize(null);
                        x.setMaxHeight(10000);
                    } else {
                        x.setEllipsize(TruncateAt.END);
                        x.setMaxHeight(textHeight);
                    }
                }
            });
            LinearLayout commet = new LinearLayout(context);
            commet.setOrientation(LinearLayout.VERTICAL);
            commet.addView(top);
            commet.addView(descView);
            LinearLayout bottom = new LinearLayout(context);
            bottom.setGravity(Gravity.RIGHT | Gravity.BOTTOM);

            //自己可以删除自己的评论
            TextView delImage = new TextView(context);
            delImage.setTag(obj.getString("v_1"));
            delImage.setText(R.string.system_delete);
            delImage.setGravity(Gravity.CENTER);
            delImage.setTextColor(Color.BLACK);
            delImage.setBackgroundResource(R.drawable.comment_empty);
            if (!delFlag) {//无删除权限的只能删除自身评论
                if (obj.getString("v_2").equals(staffId)) {
                    bottom.addView(delImage, LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout
                            .LayoutParams.WRAP_CONTENT);
                }
            } else {
                bottom.addView(delImage, LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout
                        .LayoutParams.WRAP_CONTENT);
            }
            bottom.addView(new TextView(context), 5, 5);
            delImage.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(final View view) {
                    final MyAlertDialog mal = new MyAlertDialog(context);
                    mal.setTitle("是否确定删除");
                    mal.setPositiveButton("确定", new OnClickListener() {

                        @Override
                        public void onClick(View v) {
                            operFunc((View) view.getParent().getParent().getParent(), (String)
                                    view.getTag(), "delete");
                            mal.dismiss();
                        }
                    });
                    mal.setNegativeButton("取消", new OnClickListener() {

                        @Override
                        public void onClick(View v) {
                            mal.dismiss();
                        }
                    });
                    mal.show();
                }
            });
            //回复
            if ("allComment".equals(refreshVisity)) {
                TextView reply = new TextView(context);
                reply.setBackgroundResource(R.drawable.comment_empty);
                reply.setText(R.string.system_answer);
                reply.setTextColor(Color.BLACK);
                reply.setGravity(Gravity.CENTER);
                reply.setTag(obj);
                reply.setOnClickListener(new View.OnClickListener() {

                    @Override
                    public void onClick(View view) {
                        JSONObject obj = (JSONObject) view.getTag();
                        try {
                            replayStr = "//@" + obj.getString("v_3") + ":" + obj.getString("v_5");
                            title.setText(context.getString(R.string.comment_answer, "'" + obj
                                    .getString("v_3") + ":" + obj.getString("v_5") + "'"));
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        editor.requestFocus();
                        b.setVisibility(View.GONE);
                        rp.setVisibility(View.VISIBLE);
                        cancle.setVisibility(View.VISIBLE);
                    }
                });
                bottom.addView(reply);
            }
            commet.addView(bottom);

            //长按弹出删除、回复等按钮
            commet.setOnLongClickListener(new OnLongClickListener() {

                @Override
                public boolean onLongClick(View v) {

                    Toast.makeText(context, "", Toast.LENGTH_LONG).show();
                    return false;
                }
            });

            //用户头像
            LinearLayout staff = new LinearLayout(context);
            staff.setOrientation(LinearLayout.VERTICAL);
            ImageView staffImage = new ImageView(context);
            staffImage.setImageResource(strHeadPic);
            staff.setLayoutParams(new LinearLayout.LayoutParams(Level1Util.getRawSize(context,
                    TypedValue.COMPLEX_UNIT_DIP, 40), LinearLayout.LayoutParams.WRAP_CONTENT));
            staff.addView(staffImage);
            staffImage.setTag(obj);
            if (staffId.equals(obj.getString("v_2"))) {//自己发的评论，头像在右侧
                commet.setBackgroundResource(strOtherBubbleBGImage);
                l.addView(commet);
                l.addView(staff);
                l.setGravity(Gravity.RIGHT);
            } else {//其他人发的评论，头像在左侧
                commet.setBackgroundResource(strSelfBubbleBGImage);
                l.addView(staff);
                l.addView(commet);
                l.setGravity(Gravity.LEFT);
            }
        } catch (JSONException e) {
            Toast.makeText(context, context.getString(R.string.error_json), Toast.LENGTH_SHORT)
                    .show();
        }
        return l;
    }

    /***
     * 顶踩触发事件
     */
    public void operFunc(View v, String commentId, String flag) {
        JSONObject param = new JSONObject();
        operView = v;
        operFlag = flag;
        try {
            param.put("visitType", flag);
            param.put("staffId", staffId);
            param.put("jobId", jobId);
            param.put("commentId", commentId);
            param.put("rptCode", "");
        } catch (JSONException e) {
            Toast.makeText(context,
                    context.getString(R.string.error_json), Toast.LENGTH_SHORT).show();
        }
        myShowDialog(R.string.system_loading);
        ServiceThread st = new ServiceThread(serviceUrl, param, context);
        st.setServiceHandler(sh);
        st.start();
    }

    //查询评论列表
    public void queryComment(JSONObject param) {
        ServiceThread st = new ServiceThread(serviceUrl, param, context);
        st.setServiceHandler(sh2);
        st.start();
    }

    ServiceThread.ServiceHandler sh = new ServiceThread.ServiceHandler() {

        @Override
        public void begin(ServiceThread st) {

        }

        @Override
        public void success(ServiceThread st, JSONObject dataObj) {
            jsonObj = dataObj;
            if ("false".equals(jsonObj.optString("flag", "false"))) {
                myDismissDialog();
                Toast.makeText(context.getApplicationContext(), dataObj.optString("reason",
                        context.getString(R.string.error_serivce)), Toast.LENGTH_SHORT).show();
            } else {
                try {
                    initAllLayout();
                } catch (Exception e) {
                    Toast.makeText(context.getApplicationContext(), context.getString(R.string
                            .error_json), Toast.LENGTH_SHORT).show();
                } finally {
                    myDismissDialog();
                }
            }
        }

        @Override
        public void fail(ServiceThread st, String errorCode, String errorMessage) {
            myDismissDialog();
            Toast.makeText(context.getApplicationContext(), errorMessage, Toast.LENGTH_SHORT)
                    .show();
        }
    };

    ServiceThread.ServiceHandler sh2 = new ServiceThread.ServiceHandler() {

        @Override
        public void begin(ServiceThread st) {

        }

        @Override
        public void success(ServiceThread st, JSONObject dataObj) {
            JSONArray array = new JSONArray();
            String commentWay = dataObj.optString("commentWay");
            array = dataObj.optJSONArray("commentArray");
            if (array.length() == 0)
                Toast.makeText(context, R.string.comment_nomore, Toast.LENGTH_SHORT).show();

            if (commentWay.equals("new")) {
                JSONArray tempArray = new JSONArray();
                for (int i = 0; i < array.length(); i++) {
                    tempArray.put(array.optJSONObject(i));
                }
                for (int i = 0; i < commentArray.length(); i++) {
                    tempArray.put(commentArray.optJSONObject(i));
                }
                commentArray = tempArray;
                adapter.notifyDataSetChanged();
                listView.onRefreshComplete();
            } else {
                for (int i = 0; i < array.length(); i++) {
                    commentArray.put(array.optJSONObject(i));
                }
                adapter.notifyDataSetChanged();// 重新绘制listview
                listView.onLoadMoreComplete();
            }
        }

        @Override
        public void fail(ServiceThread st, String errorCode, String errorMessage) {
            Toast.makeText(context.getApplicationContext(), errorMessage, Toast.LENGTH_SHORT)
                    .show();
        }
    };

    MyProgressDialog mAD;

    public void myShowDialog(int id) {
        if (mAD == null) {
            mAD = new MyProgressDialog(context);
        }
        switch (id) {
            case 1:
                mAD.setTitle(context.getString(R.string.system_loading));
                break;
            case 2:
                mAD.setTitle(context.getString(R.string.audit_update));
                break;
        }
        mAD.show();
    }

    public void myDismissDialog() {
        if (mAD != null) {
            mAD.dismiss();
        }
    }
}
