package com.doloop.www.myappmgr.material.widgets;

import java.util.ArrayList;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.os.Handler;
import android.util.AttributeSet;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.doloop.www.myappmgr.material.R;

public class IndexBarView extends LinearLayout {

    public void setPopView(View view) {
        mPopView = view;
    }

    private OnIndexItemClickListener mOnIndexItemClickListener;

    public interface OnIndexItemClickListener {
        void onItemClick(String s);
    }

    public void setOnIndexItemClickListener(OnIndexItemClickListener listener) {
        this.mOnIndexItemClickListener = listener;
    }

    public final static String[] mIndexArray = { "A", "B", "C", "D", "E", "F", "G", "H", "I", "J", "K", "L", "M", "N",
            "O", "P", "Q", "R", "S", "T", "U", "V", "W", "X", "Y", "Z", "#" };

    private ArrayList<TextView> mIndexTextViewList = new ArrayList<TextView>();
    private PopupWindow mPopupWindow;
    private View mPopView = null;
    private TextView mPopupText;
    private Handler handler = new Handler();
    private int choose = -1;
    private int singleIndexHeight = 0;
    private boolean doReset = false;
    private int selectItemTextColor = getResources().getColor(R.color.theme_blue_light);
    private int noneExistPopupTextColor = getResources().getColor(R.color.white3);
    private int normalTextColor = Color.LTGRAY;
    private int existItemTextColor = Color.BLACK;
    private ArrayList<String> existedIndexArray = new ArrayList<String>();

    public IndexBarView(Context context) {
        super(context);
        InitIndexBar();
        // TODO Auto-generated constructor stub
    }

    public IndexBarView(Context context, AttributeSet attrs) {
        super(context, attrs);
        InitIndexBar();
    }

    public void InitIndexBar() {
        removeAllViews();
        mIndexTextViewList.clear();
        setOrientation(LinearLayout.VERTICAL);
        setPadding(0, 10, 0, 10);
        LayoutParams params = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT, 1);
        TextView tmpTV = null;
        for (int i = 0; i < mIndexArray.length; i++) {
            tmpTV = new TextView(getContext());
            tmpTV.setGravity(Gravity.CENTER);
            tmpTV.setLayoutParams(params);
            tmpTV.setTextColor(normalTextColor);
            tmpTV.setText(mIndexArray[i]);
            tmpTV.setTextSize(TypedValue.COMPLEX_UNIT_SP, 10);
            addView(tmpTV);
            mIndexTextViewList.add(tmpTV);
        }
    }

    public void setExistIndexArray(ArrayList<String> list){
        existedIndexArray = list;
        if(existedIndexArray == null || existedIndexArray.size() == 0){
            existedIndexArray = new ArrayList<String>();
        }
        processExistIndexArray();
    }
    
    private void processExistIndexArray(){
        if(existedIndexArray != null && existedIndexArray.size() > 0){
            for(int i = 0, size = mIndexTextViewList.size(); i < size ; i++){
                TextView tmpTV = mIndexTextViewList.get(i);
                tmpTV.setTypeface(null, Typeface.NORMAL);
                if(existedIndexArray.contains(tmpTV.getText().toString())){
                    tmpTV.setTextColor(existItemTextColor);
                }
                else{
                    tmpTV.setTextColor(normalTextColor);
                }
            }
        }
        else{
            for(int i = 0, size = mIndexTextViewList.size(); i < size ; i++){
                TextView tmpTV = mIndexTextViewList.get(i);
                tmpTV.setTextColor(normalTextColor);
                tmpTV.setTypeface(null, Typeface.NORMAL);
            }
        }
    }
    
    @Override
    protected void onDraw(Canvas canvas) {
        // TODO Auto-generated method stub
        super.onDraw(canvas);
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent event) {
        final int action = event.getAction();
        final float TouchEventYpos = event.getY();
        final int oldChoose = choose;
        singleIndexHeight = getHeight() / mIndexArray.length;
        int c = (int) (TouchEventYpos / singleIndexHeight);
        if (c < 0) {
            c = 0;
        } else if (c >= mIndexArray.length) {
            c = mIndexArray.length - 1;
        }
        switch (action) {
            case MotionEvent.ACTION_DOWN:
                doReset = true;
                // setBackgroundColor(Color.LTGRAY);
                //setBackgroundResource(R.drawable.rounded_rectangle_shape);
                processTouchdownStyle();
                if (oldChoose != c) {
                    // clearIndexListItemBG(Color.BLACK,true);
                    // mIndexTextViewList.get(c).setBackgroundColor(Color.RED);
                    //mIndexTextViewList.get(c).setBackgroundResource(R.drawable.rounded_rectangle_shape_index_selected);
                    mIndexTextViewList.get(c).setTextColor(selectItemTextColor);
                    performItemClicked(c);
                    choose = c;
                    invalidate();
                }
                Log.i("ttt", "DOWN");
                break;
            case MotionEvent.ACTION_MOVE:
                if (oldChoose != c) {
                    if (oldChoose >= 0)// && oldChoose < mIndexArray.length
                    {
                        TextView theTv = mIndexTextViewList.get(oldChoose);
                        if(existedIndexArray.contains(theTv.getText().toString())){
                            theTv.setTextColor(Color.BLACK);
                        }
                        else{
                            theTv.setTextColor(normalTextColor);
                        }
                        
                    }
                    mIndexTextViewList.get(c).setTextColor(selectItemTextColor);
                    performItemClicked(c);
                    choose = c;
                    invalidate();
                }
                Log.i("ttt", "MOVE");
                break;
            case MotionEvent.ACTION_CANCEL:
            case MotionEvent.ACTION_UP:
                // setBackgroundColor(Color.TRANSPARENT);
                // clearIndexListItemBG(Color.GRAY,false);
                // choose = -1;
                // dismissPopup();
                reset();
                Log.i("ttt", "UP");
                break;
        }
        return true;
    }

   private void processTouchdownStyle() {
       
       if(existedIndexArray == null || existedIndexArray.size() == 0) {
           return;
       }
       
       for (TextView tv : mIndexTextViewList) {
            //tv.setBackgroundColor(Color.TRANSPARENT);
      
            if(existedIndexArray.contains(tv.getText().toString())){
                tv.setTextColor(Color.BLACK);
            }
            else{
                tv.setTextColor(normalTextColor);
            }
            tv.setTypeface(null, Typeface.BOLD);
        }
    }

    private void showPopup(int item) {
        if (mPopView != null)// 如果设定了显示用的view就不用popupwindow显示
        {
            handler.removeCallbacks(dismissRunnable);
            mPopView.clearAnimation();
            mPopView.setVisibility(View.VISIBLE);
            TextView popTv = (TextView) mPopView;
            if(existedIndexArray.contains(mIndexArray[item])){
                popTv.setTextColor(Color.WHITE);
            }
            else{
                popTv.setTextColor(noneExistPopupTextColor);
            }
            popTv.setText(mIndexArray[item]);
            return;
        }

        if (mPopupWindow == null) {
            View contentView = LayoutInflater.from(getContext()).inflate(R.layout.popup_text, null);
            mPopupText = (TextView) contentView.findViewById(R.id.popText);
            mPopupWindow = new PopupWindow(getContext());
            mPopupWindow.setContentView(contentView);
            mPopupWindow.setWidth(LayoutParams.WRAP_CONTENT);
            mPopupWindow.setHeight(LayoutParams.WRAP_CONTENT);
            mPopupWindow.setBackgroundDrawable(new ColorDrawable(android.R.color.transparent));

            handler.removeCallbacks(dismissRunnable);
            // mPopupText = new TextView(getContext());
            // mPopupText.setBackgroundColor(Color.GRAY);
            // mPopupText.setBackgroundResource(R.drawable.rounded_rectangle_shape);
            // mPopupText.setTextColor(Color.CYAN);
            // mPopupText.setTextSize(25);//mScaledDensity
            // mPopupText.setGravity(Gravity.CENTER_HORIZONTAL
            // | Gravity.CENTER_VERTICAL);
            // mPopupWindow = new PopupWindow(mPopupText, 150, 150);

        }

        mPopupText.setText(mIndexArray[item]);
        if (mPopupWindow.isShowing()) {
            mPopupWindow.update();
        } else {
            mPopupWindow.showAtLocation(getRootView(), Gravity.CENTER_HORIZONTAL | Gravity.CENTER_VERTICAL, 0, 0);
        }
    }

    private void dismissPopup() {
        handler.postDelayed(dismissRunnable, 800);
    }

    Runnable dismissRunnable = new Runnable() {

        @Override
        public void run() {
            // TODO Auto-generated method stub
            if (mPopView != null) {
                //mPopView.setVisibility(View.INVISIBLE);
                Animation ani = new AlphaAnimation(1, 0);
                ani.setDuration(350);
                ani.setAnimationListener(new AnimationListener() {

                    public void onAnimationStart(Animation animation) {
                        // TODO Auto-generated method stub

                    }

                    public void onAnimationRepeat(Animation animation) {
                        // TODO Auto-generated method stub

                    }

                    public void onAnimationEnd(Animation animation) {
                        mPopView.setVisibility(View.INVISIBLE);
                    }
                });
                
                mPopView.startAnimation(ani);
                
                return;
            }
            if (mPopupWindow != null) {
                mPopupWindow.dismiss();
            }
        }
    };

    private void performItemClicked(int item) {
        showPopup(item);
        if (mOnIndexItemClickListener != null) {
            mOnIndexItemClickListener.onItemClick(mIndexArray[item]);
            // showPopup(item);
        }
    }

    public void reset() {
        if (doReset) {
            //setBackgroundColor(Color.TRANSPARENT);
            //clearIndexListItemBG(Color.GRAY, false);asfdadf
            processExistIndexArray();
            choose = -1;
            dismissPopup();
            doReset = false;
            Log.i("ttt", "IndexBar reset done");
        }
    }

}
