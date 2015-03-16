package com.doloop.www.myappmgr.material.events;

import java.util.ArrayList;

import com.doloop.www.myappmgr.material.dao.AppInfo;

public class BackupAppEvent {
    public ArrayList<AppInfo> appList;
    public boolean sendAfterBackup;
    /**
     * 
     * @param list
     * @param send ����֮���Ƿ�Ҫ����
     */
    public BackupAppEvent(ArrayList<AppInfo> list, boolean send){
        appList = list; 
        sendAfterBackup = send;
    }
}
