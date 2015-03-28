package com.doloop.www.myappmgr.material.utils;

import android.os.Environment;

public class Constants {
    public final static int USR_APPS_TAB_POS = 0;
    public final static int SYS_APPS_TAB_POS = 1;
    public final static int BACKUP_APPS_TAB_POS = 2;
    
    public static final boolean SAVE_APP_ICON_IN_OBJ = false;
    
    public static String MY_PACKAGE_NAME = "";
    
    public static final boolean HANDLE_PKG_CHG = false;
    
    public static final String DEF_BACKUP_DIR = Environment.getExternalStorageDirectory().toString() + "/MyAppMgrMD/";
}
