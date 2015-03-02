package com.doloop.www.mayappmgr.material.events;

import java.util.ArrayList;

import com.doloop.www.myappmgr.material.dao.AppInfo;

public class BackupAppEvent {
    public ArrayList<AppInfo> appList;
    public boolean sendAfterBackup;
    public BackupAppEvent(ArrayList<AppInfo> list, boolean send){
        appList = list; 
        sendAfterBackup = send;
    }
}
