package com.doloop.www.mayappmgr.material.events;

import com.doloop.www.myappmgr.material.dao.AppInfo;

public class AppBackupSuccEvent {
    public AppInfo mAppInfo;
    
    public AppBackupSuccEvent(AppInfo appInfo){
        mAppInfo = appInfo;
    }
    
}
