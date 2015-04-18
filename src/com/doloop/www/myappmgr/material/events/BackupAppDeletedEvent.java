package com.doloop.www.myappmgr.material.events;

import com.doloop.www.myappmgr.material.dao.AppInfo;

public class BackupAppDeletedEvent {
    public AppInfo mAppInfo;
    public BackupAppDeletedEvent(AppInfo appInfo){
        mAppInfo = appInfo;
    }
}
