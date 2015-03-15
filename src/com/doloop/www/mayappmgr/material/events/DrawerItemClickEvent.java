package com.doloop.www.mayappmgr.material.events;

import com.doloop.www.myappmgrmaterial.R;


public class DrawerItemClickEvent {
    
    public DrawerItem DrawerItem;
    
    //修改备份文件夹选项使用的
    public String oldPath = "";
    public String newPath = "";
    
    public static enum DrawerItem {
        REFRESH(0, R.string.refresh, R.drawable.ic_refresh),CHG_BACKUP_DIR(0, R.string.back_dir, R.drawable.folder_black);
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
        DrawerItem = drawerItem;
    }
    
}
