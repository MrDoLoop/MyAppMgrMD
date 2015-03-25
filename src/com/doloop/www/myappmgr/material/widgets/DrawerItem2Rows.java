package com.doloop.www.myappmgr.material.widgets;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.res.TypedArray;
import android.os.Build;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.doloop.www.myappmgr.material.R;

public class DrawerItem2Rows extends LinearLayout {

    private ImageView iconIm;
    private TextView firstRowTxt;
    private TextView secondRowTxt;

    public DrawerItem2Rows(Context context) {
        super(context);
        // TODO Auto-generated constructor stub
        init(context, null, 0);
    }

    public DrawerItem2Rows(Context context, AttributeSet attrs) {
        super(context, attrs);
        // TODO Auto-generated constructor stub
        init(context,attrs,0);
    }
    
    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    public DrawerItem2Rows(Context context, AttributeSet attrs,int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        // TODO Auto-generated constructor stub
        init(context, attrs, defStyleAttr);
    }
    

    public void init(Context context,AttributeSet attrs,int defStyleAttr) {
        LayoutInflater layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        layoutInflater.inflate(R.layout.drawer_item_two_row, this);
        iconIm = (ImageView) findViewById(R.id.icon);
        firstRowTxt = (TextView) findViewById(R.id.txtFirstRow);
        secondRowTxt = (TextView) findViewById(R.id.txtSecondRow);
        TypedArray attributes = context.getTheme().obtainStyledAttributes(attrs, R.styleable.MenuItem2Rows, defStyleAttr, 0);
        
        int iconResource = (int) attributes.getResourceId(R.styleable.MenuItem2Rows_iconSrc, R.drawable.ic_refresh_black);
        iconIm.setImageResource(iconResource);
        
        String firstRowStr = attributes.getString(R.styleable.MenuItem2Rows_firstRowTxt);
        firstRowTxt.setText(firstRowStr);
        
        String secondRowStr = attributes.getString(R.styleable.MenuItem2Rows_secondRowTxt);
        secondRowTxt.setText(secondRowStr);
        
        attributes.recycle();
    }
    
    public void setIcon(int resId){
        iconIm.setImageResource(resId);
    }
    
    public void setFirstRowTxt(CharSequence text){
        firstRowTxt.setText(text);
    }
    
    public void setSecondRowTxt(CharSequence text){
        secondRowTxt.setText(text);
    }
}
