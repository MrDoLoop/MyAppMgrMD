package com.doloop.www.mayappmgr.material.events;

import com.doloop.www.myappmgr.material.dao.AppInfo;

public class AppBackupSuccEvent {
    public AppInfo AppInfo;
    
    public AppBackupSuccEvent(AppInfo appInfo){
        AppInfo = appInfo;
    }
    
}
