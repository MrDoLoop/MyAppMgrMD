package com.doloop.www.myappmgr.material.widgets;

import android.content.Context;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.MotionEvent;

public class MyViewPager extends ViewPager{
    private boolean isPagingEnabled = true;  //用来控制是否可以滑动
    
    public MyViewPager(Context context) {  
        super(context);  
    }  
  
    public MyViewPager(Context context, AttributeSet attrs) {  
        super(context, attrs);  
    }  
  
//    public void setScanScroll(boolean isCanScroll){  
//        this.isPagingEnabled = isCanScroll;  
//    }  
  
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (this.isPagingEnabled) {
            return super.onTouchEvent(event);
        }

        return false;
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent event) {
        if (this.isPagingEnabled) {
            return super.onInterceptTouchEvent(event);
        }

        return false;
    }

    public void setPagingEnabled(boolean b) {
        this.isPagingEnabled = b;
    }
    
    
  
//    @Override  
//    public void scrollTo(int x, int y){  
//        if (isPagingEnabled){  
//            super.scrollTo(x, y);  
//        }  
//    }
}
