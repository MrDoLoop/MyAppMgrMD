package com.doloop.www.myappmgr.material.dao;

import java.io.File;

import android.content.Context;
import android.graphics.Bitmap;
import android.text.TextUtils;
import android.util.Log;

import com.doloop.www.myappmgr.material.utils.Utils;

// THIS CODE IS GENERATED BY greenDAO, DO NOT EDIT. Enable "keep" sections if you want to edit. 
/**
 * Entity mapped to table APP_INFO.
 */
public class AppInfo {

    public Long id;
    /** Not-null value. */
    public String appName = "";
    public String appNamePinyin = "";
    public String appSortName = "";
    /** Not-null value. */
    public String packageName = "";
    public String versionName = "";
    public int versionCode = -1;
    public byte[] appIconBytes = null;
    /** Not-null value. */
    public String appSizeStr = "";
    public long appRawSize = 0;
    public String firstTimeInstallDate = "";
    /** Not-null value. */
    public String lastModifiedTimeStr = "";
    public long lastModifiedRawTime = 0;
    /** Not-null value. */
    public String apkFilePath = "";
    public boolean isSysApp = false;
    
    //�Լ����ӵ�
    public boolean selected = false;
    //public Drawable iconDrawable = null;
    public Bitmap iconBitmap = null;
    
    public String backupFilePath = "";
    
    public String getBackupApkFileName(){
        if(!TextUtils.isEmpty(backupFilePath)){
//            String fileName = backupFilePath.substring(backupFilePath.lastIndexOf("\\/")+1);
//            return fileName;
            File f = new File(backupFilePath);
            return f.getName();
        }
        else
            return "";
    }
    
    public final static AppInfo DUMMY_APPINFO = new AppInfo();
    ////////////////
    
    public AppInfo() {
    }

    public AppInfo(Long id) {
        this.id = id;
    }

    public AppInfo(Long id, String appName, String appNamePinyin, String appSortName, String packageName, String versionName, int versionCode, byte[] appIconBytes, String appSizeStr, long appRawSize, String firstTimeInstallDate, String lastModifiedTimeStr, long lastModifiedRawTime, String apkFilePath, boolean isSysApp) {
        this.id = id;
        this.appName = appName;
        this.appNamePinyin = appNamePinyin;
        this.appSortName = appSortName;
        this.packageName = packageName;
        this.versionName = versionName;
        this.versionCode = versionCode;
        this.appIconBytes = appIconBytes;
        this.appSizeStr = appSizeStr;
        this.appRawSize = appRawSize;
        this.firstTimeInstallDate = firstTimeInstallDate;
        this.lastModifiedTimeStr = lastModifiedTimeStr;
        this.lastModifiedRawTime = lastModifiedRawTime;
        this.apkFilePath = apkFilePath;
        this.isSysApp = isSysApp;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    /** Not-null value. */
    public String getAppName() {
        return appName;
    }

    /** Not-null value; ensure this value is available before it is saved to the database. */
    public void setAppName(String appName) {
        this.appName = appName;
    }

    public String getAppNamePinyin() {
        return appNamePinyin;
    }

    public void setAppNamePinyin(String appNamePinyin) {
        this.appNamePinyin = appNamePinyin;
    }

    public String getAppSortName() {
        return appSortName;
    }

    public void setAppSortName(String appSortName) {
        this.appSortName = appSortName;
    }

    /** Not-null value. */
    public String getPackageName() {
        return packageName;
    }

    /** Not-null value; ensure this value is available before it is saved to the database. */
    public void setPackageName(String packageName) {
        this.packageName = packageName;
    }

    public String getVersionName() {
        return versionName;
    }

    public void setVersionName(String versionName) {
        this.versionName = versionName;
    }

    public int getVersionCode() {
        return versionCode;
    }

    public void setVersionCode(int versionCode) {
        this.versionCode = versionCode;
    }

    public byte[] getAppIconBytes() {
        return appIconBytes;
    }

    public void setAppIconBytes(byte[] appIconBytes) {
        this.appIconBytes = appIconBytes;
    }

    /** Not-null value. */
    public String getAppSizeStr() {
        return appSizeStr;
    }

    /** Not-null value; ensure this value is available before it is saved to the database. */
    public void setAppSizeStr(String appSizeStr) {
        this.appSizeStr = appSizeStr;
    }

    public long getAppRawSize() {
        return appRawSize;
    }

    public void setAppRawSize(long appRawSize) {
        this.appRawSize = appRawSize;
    }

    public String getFirstTimeInstallDate() {
        return firstTimeInstallDate;
    }

    public void setFirstTimeInstallDate(String firstTimeInstallDate) {
        this.firstTimeInstallDate = firstTimeInstallDate;
    }

    /** Not-null value. */
    public String getLastModifiedTimeStr() {
        return lastModifiedTimeStr;
    }

    /** Not-null value; ensure this value is available before it is saved to the database. */
    public void setLastModifiedTimeStr(String lastModifiedTimeStr) {
        this.lastModifiedTimeStr = lastModifiedTimeStr;
    }

    public long getLastModifiedRawTime() {
        return lastModifiedRawTime;
    }

    public void setLastModifiedRawTime(long lastModifiedRawTime) {
        this.lastModifiedRawTime = lastModifiedRawTime;
    }

    /** Not-null value. */
    public String getApkFilePath() {
        return apkFilePath;
    }

    /** Not-null value; ensure this value is available before it is saved to the database. */
    public void setApkFilePath(String apkFilePath) {
        this.apkFilePath = apkFilePath;
    }

    public boolean getIsSysApp() {
        return isSysApp;
    }

    public void setIsSysApp(boolean isSysApp) {
        this.isSysApp = isSysApp;
    }

    public void print() {
        Log.v("app", "Name:" + appName + " Package:" + packageName);
        Log.v("app", "Name:" + appName + " versionName:" + versionName);
        Log.v("app", "Name:" + appName + " versionCode:" + versionCode);
    }

    public String getBackupFileName_pkgName()
    {
        return packageName+"_v"+versionName+".apk";
    }
    
    public String getBackupFileName_AppName()
    {
        return appName+"_v"+versionName+".apk";
    }
    
    public File getAppIconCachePath(Context context){
        return new File(Utils.getAppIconCacheDir(context), packageName + "-" +versionCode +".png");
    }
    
}
