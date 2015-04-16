package com.doloop.www.myappmgr.material.utils;

import java.util.ArrayList;

import android.content.Context;
import android.support.v7.app.ActionBar.LayoutParams;
import android.support.v7.widget.ListPopupWindow;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;

import com.doloop.www.myappmgr.material.R;
import com.doloop.www.myappmgr.material.dao.AppInfo;
import com.doloop.www.myappmgr.material.interfaces.IPopupMenuClickListener;

public class PopupListMenu {
    
//    private ArrayList<POPUP_MENU_LIST_ITEM> mMenuItems;
//    private View mAnchorView;
    private ListPopupWindow mListPopupWindow;
    
    public enum POPUP_MENU_LIST_ITEM {
        
        INSTALL(R.string.install), OPEN(R.string.open), 
        DELETE(R.string.delete), SHARE(R.string.share), 
        MARKET(R.string.google_play);
        
        private final int stringRes;

        POPUP_MENU_LIST_ITEM(int value) {
            this.stringRes = value;
        }
        
        public int getStringRes() {
            return stringRes;
        }
        
        public String getString(Context ctx) {
            return ctx.getString(stringRes);
        }
    }
    
    
    public PopupListMenu(Context ctx, final ArrayList<POPUP_MENU_LIST_ITEM> menuItems, View anchorView, final int appListPos, 
            final AppInfo appInfo, final IPopupMenuClickListener PopupMenuClickListener){
//        mMenuItems = menuItems;
//        mAnchorView = anchorView;
        
        String[] items = new String[menuItems.size()];
        for(int i =0;i<items.length;i++){
            items[i] = menuItems.get(i).getString(ctx);
        }
        
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(ctx, R.layout.popup_menu_item, items);
        mListPopupWindow = new ListPopupWindow(ctx);
        mListPopupWindow.setAdapter(adapter);
        //ListPopupWindowAdapter adapter = new ListPopupWindowAdapter(mCtx,itmes);
        //mListPopupWindow.setAdapter(adapter);
        mListPopupWindow.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> arg0, View arg1, int position,long arg3) {
                mListPopupWindow.dismiss();
                PopupMenuClickListener.OnPopupMenuClick(menuItems.get(position),position, appListPos, appInfo);
            }
          });
        mListPopupWindow.setAnchorView(anchorView);
        mListPopupWindow.setWidth(300);
        
        mListPopupWindow.setHeight(LayoutParams.WRAP_CONTENT);
        mListPopupWindow.setModal(false);
    }
    
    public void show(){
        mListPopupWindow.show();
    }
    
    
}
