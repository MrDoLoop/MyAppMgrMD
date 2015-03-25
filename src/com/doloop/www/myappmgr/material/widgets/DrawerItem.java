package com.doloop.www.myappmgr.material.widgets;

import com.doloop.www.myappmgr.material.R;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.LinearLayout;

public class DrawerItem extends LinearLayout {
    public DrawerItem(Context context) {
        super(context);
        // TODO Auto-generated constructor stub
        init(context);
    }
    
    public DrawerItem(Context context, AttributeSet attrs) {
        super(context, attrs);
        // TODO Auto-generated constructor stub
        init(context);
    }
    
    
    public void init(Context ctx){
        LayoutInflater layoutInflater = (LayoutInflater) ctx.
                getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        layoutInflater.inflate(R.layout.drawer_item, this);
    }
}
