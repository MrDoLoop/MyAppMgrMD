package com.doloop.www.mayappmgr.material.events;

import com.doloop.www.myappmgrmaterial.R;


public class DrawerItemClickEvent {
    
    public DrawerItem mDrawerItem;
    
    public static enum DrawerItem {
        REFRESH(0, R.string.refresh, R.drawable.ic_refresh),;
        public final int mCode;
        public final int mStrId;
        public final int mIconId;
        
        DrawerItem(int code, int strId, int iconId) {
            this.mCode = code;
            this.mStrId = strId;
            this.mIconId = iconId;
        }
    }
    
    public DrawerItemClickEvent(DrawerItem drawerItem){
        mDrawerItem = drawerItem;
    }
    
}
