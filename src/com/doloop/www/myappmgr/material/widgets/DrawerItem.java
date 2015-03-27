package com.doloop.www.myappmgr.material.widgets;

import com.doloop.www.myappmgr.material.R;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

public class DrawerItem extends LinearLayout {
    
    private ImageView iconIm;
    private TextView txt;
    
    public DrawerItem(Context context) {
        super(context);
        // TODO Auto-generated constructor stub
        init(context, null);
    }
    
    public DrawerItem(Context context, AttributeSet attrs) {
        super(context, attrs);
        // TODO Auto-generated constructor stub
        init(context,attrs);
    }
    
    
    public void init(Context ctx, AttributeSet attrs){
        LayoutInflater layoutInflater = (LayoutInflater) ctx.
                getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        layoutInflater.inflate(R.layout.drawer_item, this);
        
        iconIm = (ImageView) findViewById(R.id.icon);
        txt = (TextView) findViewById(R.id.txt);
        
        TypedArray attributes = ctx.getTheme().obtainStyledAttributes(attrs, R.styleable.MenuItem, 0, 0);
        
        int iconResource = (int) attributes.getResourceId(R.styleable.MenuItem_iconSrcId, R.drawable.ic_refresh_black);
        iconIm.setImageResource(iconResource);
        
        String firstRowStr = attributes.getString(R.styleable.MenuItem_txt);
        txt.setText(firstRowStr);
        
       
        attributes.recycle();
        
    }
    
    public void setIcon(int resId){
        iconIm.setImageResource(resId);
    }
    
    public void setTxt(CharSequence text){
        txt.setText(text);
    }
}
