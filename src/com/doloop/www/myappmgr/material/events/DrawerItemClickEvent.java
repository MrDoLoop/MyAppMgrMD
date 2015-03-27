package com.doloop.www.myappmgr.material.events;

import com.doloop.www.myappmgr.material.R;


public class DrawerItemClickEvent {
    
    public DrawerItem DrawerItem;
    
    //�޸ı����ļ���ѡ��ʹ�õ�
    public String oldPath = "";
    public String newPath = "";
    
    public static enum DrawerItem {
        REFRESH(0, R.string.refresh, R.drawable.ic_refresh),
        SETTINGS(0, R.string.settings, R.drawable.ic_settings_black_24dp),
        CHG_BACKUP_DIR(0, R.string.back_dir, R.drawable.folder_black);
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
