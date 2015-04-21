package com.doloop.www.myappmgr.material.fragments;

import android.support.v4.app.ListFragment;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.RelativeSizeSpan;

import com.doloop.www.myappmgr.material.interfaces.IfragInterfaces;

public class BaseFrag extends ListFragment implements IfragInterfaces {

   
    public static SpannableString actionModeTitle(String plainStr){
        SpannableString titleSpanStr = new SpannableString(plainStr);
        titleSpanStr.setSpan(new RelativeSizeSpan(0.7f), plainStr.indexOf("/"), plainStr.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        return titleSpanStr;
    }
    
    @Override
    public void setFragmentTitle(String title) {
        // TODO Auto-generated method stub
        
    }

    @Override
    public String getFragmentTitle() {
        // TODO Auto-generated method stub
        return null;
    }
}
