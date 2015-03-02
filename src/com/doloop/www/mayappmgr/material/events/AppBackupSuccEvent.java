package com.doloop.www.mayappmgr.material.events;

import java.util.ArrayList;

import com.doloop.www.myappmgr.material.dao.AppInfo;

public class AppBackupSuccEvent {
    public ArrayList<AppInfo> AppInfoList;
    
    public AppBackupSuccEvent(ArrayList<AppInfo> list){
        AppInfoList = list;
    }
    
    public AppBackupSuccEvent(AppInfo appInfo){
        AppInfoList = new ArrayList<AppInfo>();
        AppInfoList.add(appInfo);
    }
    
    
    public AppInfo getSingleAppInfo(){
        return AppInfoList.get(0);
    }
    
}
