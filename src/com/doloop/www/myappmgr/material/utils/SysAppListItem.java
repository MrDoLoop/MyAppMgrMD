package com.doloop.www.myappmgr.material.utils;

import com.doloop.www.myappmgr.material.dao.AppInfo;

public class SysAppListItem {
    public static final int APP_ITEM = 0;
    public static final int LIST_SECTION = 1;

    public final int type;
    public final String sectionTxt;
    public AppInfo appinfo;

    public SysAppListItem(int type, String text, AppInfo info) {
        this.type = type;
        this.sectionTxt = text;
        this.appinfo = info;
    }

    @Override 
    public String toString() {
        if(appinfo != null){
            return "section:"+sectionTxt+"--appinfo:" + appinfo.appName;
        }
        return "section:"+sectionTxt+"--appinfo: null";
    }
}
