package com.doloop.www.mayappmgr.material.events;

import com.doloop.www.myappmgr.material.dao.AppInfo;

import android.content.Intent;
/**
 * Ð§ÂÊ²»¼Ñ
 * @author zhaonan07
 *
 */
@Deprecated 
public class AppUpdateEvent {
    public AppState mAppState;
    public String mPkgName;
    public AppInfo mAppInfo;

    public static enum AppState {
        ERROR, APP_ADDED, APP_REMOVED, APP_CHANGED, ;

        AppState() {

        }
    }

    public AppUpdateEvent(AppState appState,String pkgName,AppInfo appInfo) {
        mAppState = appState;
        mPkgName = pkgName;
        mAppInfo = appInfo;
    }

    public AppUpdateEvent(String actionName,String pkgName,AppInfo appInfo) {
        mPkgName = pkgName;
        mAppInfo = appInfo;
        if (actionName.equals(Intent.ACTION_PACKAGE_REMOVED)) {
            mAppState = AppState.APP_REMOVED;
        } else if (actionName.equals(Intent.ACTION_PACKAGE_ADDED)) {
            mAppState = AppState.APP_ADDED;
        } else if (actionName.equals(Intent.ACTION_PACKAGE_CHANGED)) {
            mAppState = AppState.APP_CHANGED;
        }
        else{
            mAppState = AppState.ERROR;
        }
    }
}
