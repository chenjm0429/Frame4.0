package com.ztesoft.level1.gesture;

import com.ztesoft.level1.Level1Bean;
import com.ztesoft.level1.R;
import com.ztesoft.level1.util.SharedPreferencesUtil;

import android.content.Context;
import android.graphics.Color;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

public class GestureCodeView extends LinearLayout {
    private SharedPreferencesUtil mPrefs;
    private Context ctx;
    //	Thread sendThread;
    com.ztesoft.level1.gesture.GestureGridView gridview = null;

    private TextView showT = null;
    private TextView promptT = null;
    private int gesNumTmp = 0;
    private static int status;
    private Handler myHandler;
    private int minNum = 4;//手势码最少个数
    private int[] images = new int[]{R.drawable.an_brown, R.drawable.an_red, R.drawable.an_green};

    public GestureCodeView(Context context) {
        super(context);
        this.setOrientation(LinearLayout.VERTICAL);
        this.ctx = context;
        mPrefs = new SharedPreferencesUtil(ctx, "gestureCode");
    }

    public void unlock(Handler handler) {
        if (getGestureCode() == 0) {
            Toast.makeText(ctx, R.string.gesture_null, Toast.LENGTH_SHORT).show();
            return;
        }
        this.myHandler = handler;
        drawGesture(1, handler);
    }

    public void setting(Handler handler) {
        this.myHandler = handler;
        drawGesture(2, handler);
    }

    public void modify(Handler handler) {
        if (getGestureCode() == 0) {
            Toast.makeText(ctx, R.string.gesture_null, Toast.LENGTH_SHORT).show();
            return;
        }
        this.myHandler = handler;
        drawGesture(1, modifyHandler);
    }

    public void clear(Handler handler) {
        if (getGestureCode() == 0) {
            Toast.makeText(ctx, R.string.gesture_null, Toast.LENGTH_SHORT).show();
            return;
        }
        this.myHandler = handler;
        drawGesture(0, handler);
    }

    /**
     * @param status 状态，1 表示解锁  2表示重置   0表示删除
     */
    private void drawGesture(int status, final Handler handler) {
        this.removeAllViews();
        this.status = status;
        showT = new TextView(ctx);
        showT.setGravity(Gravity.CENTER_HORIZONTAL);

        promptT = new TextView(ctx);
        promptT.setGravity(Gravity.CENTER_HORIZONTAL);

        gridview = new com.ztesoft.level1.gesture.GestureGridView(ctx, images).getView();
        gridview.setVerticalSpacing(Level1Bean.actualWidth / 6);
        gridview.setPageListener(new GestureGridView.G_PageListener() {
            @Override
            public void unlock(String xxx) {
                if (2 == GestureCodeView.status) {//设置
                    if (xxx.length() >= minNum) {
                        gesNumTmp = Integer.parseInt(xxx);
                        drawGesture(3, handler);
                        return;
                    }
                } else if (3 == GestureCodeView.status) {//确认设置
                    if (gesNumTmp == Integer.parseInt(xxx)) {
                        mPrefs.putInt("gestureCode", gesNumTmp);
                        handler.sendEmptyMessage(1);
                        return;
                    }
                } else if (1 == GestureCodeView.status) {//解锁
                    int imageNum = mPrefs.getInt("gestureCode", 0);
                    if (Integer.parseInt(xxx) == imageNum) {
                        mPrefs.putInt("gestureFailNum", 5);
                        handler.sendEmptyMessage(1);
                        return;
                    }
                } else if (0 == GestureCodeView.status) {//删除
                    int imageNum = mPrefs.getInt("gestureCode", 0);
                    if (Integer.parseInt(xxx) == imageNum) {
                        mPrefs.putInt("gestureCode", 0);
                        mPrefs.putInt("gestureFailNum", 5);
                        handler.sendEmptyMessage(1);
                        return;
                    }
                }
                setError(xxx);
            }

            @Override
            public void down() {
//				if(sendThread!=null){
//					sendThread.interrupt();
//				}
                setDefault();
            }
        });

        LinearLayout bLayout = new LinearLayout(ctx);
        bLayout.setGravity(Gravity.CENTER_HORIZONTAL);
        if (2 == status) {//设置
            showT.setText(ctx.getString(R.string.gesture_new));
            promptT.setText(ctx.getString(R.string.gesture_new));
        } else if (3 == status) {//确认设置
            showT.setText(ctx.getString(R.string.gesture_new));
            promptT.setText(ctx.getString(R.string.gesture_new_again));
            Button cancel = new Button(ctx);
            cancel.setText("重新设置手势");
            bLayout.addView(cancel);
            cancel.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    drawGesture(2, handler);
                }
            });
        } else if (1 == status || 0 == status) {//解锁
            showT.setText(mPrefs.getString("userName", ""));
        }

        this.addView(showT);
        this.addView(promptT);
        this.addView(gridview);
        this.addView(bLayout);
    }

    // 将手势图片换成红色，重置为未触发状态
    private void setError(String xxx) {
        for (int i = 0; i < xxx.length(); i++) {
            int num = Integer.valueOf(String.valueOf(xxx.charAt(i))).intValue();
            RelativeLayout rl = (RelativeLayout) gridview.getChildAt(num - 1);
            ImageView temp = (ImageView) rl.getChildAt(0);
            temp.setImageResource(images[1]);// 更换图片
        }
//		sendThread = new Thread(){
//			@Override  
//            public void run() {
//				try {
//					Thread.sleep(2*1000);
//					handler.sendEmptyMessage(1);
//				} catch (InterruptedException e) {
//					e.printStackTrace();
//				}
//			}
//		};
//		sendThread.start();

        promptT.setTextColor(Color.RED);
        if (2 == status) {//设置
            promptT.setText(ctx.getString(R.string.gesture_error_num, minNum));
        } else if (3 == status) {//确认设置
            promptT.setText(R.string.gesture_error_second);
        } else if (1 == status || 0 == status) {//解锁或删除
            if (getGestureCode() == 0) {
                promptT.setText(ctx.getString(R.string.gesture_null));
            } else {
                //仅在解锁时判断错误次数
                int failNum = mPrefs.getInt("gestureFailNum", 5) - 1;
                if (failNum == 0) {
                    myHandler.sendEmptyMessage(0);
                    return;
                }
                mPrefs.putInt("gestureFailNum", failNum);
                promptT.setText(ctx.getString(R.string.gesture_retry, failNum));
            }
        }
    }

//	private Handler handler = new Handler(Looper.getMainLooper()) {
//		public void handleMessage(Message msg) { // 处理Message，更新ListView
//			setDefault();
//		}
//	};

    private Handler modifyHandler = new Handler(Looper.getMainLooper()) {
        public void handleMessage(Message msg) {
            if (msg.what == 1) {
                drawGesture(2, myHandler);
            } else {
                Toast.makeText(ctx, "----------", Toast.LENGTH_SHORT).show();
            }
        }
    };

    // 替换全部图片
    private void setDefault() {
        for (int i = 0; i < gridview.getChildCount(); i++) {
            ImageView temp = (ImageView) ((RelativeLayout) gridview.getChildAt(i)).getChildAt(0);
            temp.setImageResource(images[0]);// 更换图片
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    public void setGestureCode(int num) {
        mPrefs.putInt("gestureCode", num);
    }

    public int getGestureCode() {
        return mPrefs.getInt("gestureCode");
    }

    public void setMinNum(int minNum) {
        if (minNum > 1 && minNum <= 9) {
            this.minNum = minNum;
        }
    }

    public void setButtonImage(int[] images) {
        this.images = images;
    }
}
