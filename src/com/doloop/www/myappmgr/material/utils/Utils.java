package com.doloop.www.myappmgr.material.utils;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import net.sourceforge.pinyin4j.PinyinHelper;
import net.sourceforge.pinyin4j.format.HanyuPinyinCaseType;
import net.sourceforge.pinyin4j.format.HanyuPinyinOutputFormat;
import net.sourceforge.pinyin4j.format.HanyuPinyinToneType;
import net.sourceforge.pinyin4j.format.HanyuPinyinVCharType;
import net.sourceforge.pinyin4j.format.exception.BadHanyuPinyinOutputFormatCombination;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.comparator.LastModifiedFileComparator;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.Activity;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.pm.ResolveInfo;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.os.StatFs;
import android.provider.Settings;
import android.support.v4.app.DialogFragment;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.TypedValue;
import android.view.KeyCharacterMap;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.AbsListView;

import com.doloop.www.myappmgr.material.R;
import com.doloop.www.myappmgr.material.dao.AppInfo;
import com.doloop.www.myappmgr.material.fragments.SortTypeDialogFragment;
import com.doloop.www.myappmgr.material.utils.BkAppInListSatus.IN_LIST_STATUS;
import com.doloop.www.myappmgr.material.utils.Constants.BK_APP_INSTALL_STATUS;

public class Utils {

    public static boolean canBackupAppLaunch(Context ctx, AppInfo appInfo){
        try {
            PackageManager pm = ctx.getPackageManager();
            PackageInfo packageInfo = pm.getPackageInfo(appInfo.packageName, 0);
            if(packageInfo != null){
                Intent intent = pm.getLaunchIntentForPackage(appInfo.packageName);
                if (intent != null) {
                    return true;
                }
                else{
                    return false;
                }
            }
            else{
                return false;
            }
            
        } catch (NameNotFoundException e) {
            e.printStackTrace();
            return false;
        }
        
    } 
    
    public static boolean canLaunch(Context ctx, AppInfo appInfo){
        Intent intent = ctx.getPackageManager().getLaunchIntentForPackage(appInfo.packageName);
        if (intent != null) {
            return true;
        }
        else{
            return false;
        }
    }
    
    public static boolean launchApp(Context ctx, AppInfo appInfo) {
        Intent intent = ctx.getPackageManager().getLaunchIntentForPackage(appInfo.packageName);
        if (intent != null) {
            try {
                //检查自己
//                if(Constants.MY_PACKAGE_NAME.equals(appInfo.packageName)) {
//                    //启动了自己
//                }
//                else{
//                    ctx.startActivity(intent);
//                }
                
                ctx.startActivity(intent);
                return true;
            } catch (Exception e) {
                return false;
            }

        } else {
            return false;
        }
    }

    public static void startMarketSearch(Context ctx, AppInfo appInfo) {
        if (Utils.isAnyStoreInstalled(ctx)) {
            ctx.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + appInfo.packageName)));
        } else {
            ctx.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("http://play.google.com/store/apps/details?id="
                    + appInfo.packageName)));
        }
    }

    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    @SuppressWarnings("deprecation")
    public static void copyToClipboard(Context ctx, CharSequence label, CharSequence copiedText) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
            ClipboardManager clipboard = (ClipboardManager) ctx.getSystemService(Context.CLIPBOARD_SERVICE);
            ClipData clip = ClipData.newPlainText(label, copiedText);
            clipboard.setPrimaryClip(clip);
        } else {
            android.text.ClipboardManager clipboardManager =
                    (android.text.ClipboardManager) ctx.getSystemService(Context.CLIPBOARD_SERVICE);
            clipboardManager.setText(copiedText);
        }
    }

    public static void startActivtyWithAni(Activity activity, Intent intent) {
        activity.startActivity(intent);
        activity.overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
    }

    public static void finishActivtyWithAni(Activity activity) {
        activity.finish();
        activity.overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
    }

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    @SuppressWarnings("deprecation")
    public static void setBackgroundDrawable(View view, Drawable drawable) {
        if (android.os.Build.VERSION.SDK_INT >= 16) {
            view.setBackground(drawable);
        } else {
            view.setBackgroundDrawable(drawable);
        }
    }

    public static int getScreenWidth(Context ctx) {
        WindowManager wm = (WindowManager) ctx.getSystemService(Context.WINDOW_SERVICE);
        DisplayMetrics outMetrics = new DisplayMetrics();
        wm.getDefaultDisplay().getMetrics(outMetrics);
        return outMetrics.widthPixels;
    }

    public static int getScreenHeight(Context ctx) {
        WindowManager wm = (WindowManager) ctx.getSystemService(Context.WINDOW_SERVICE);
        DisplayMetrics outMetrics = new DisplayMetrics();
        wm.getDefaultDisplay().getMetrics(outMetrics);
        return outMetrics.heightPixels;
    }

    public static int[] findViewCenterXY(View view) {
        final int[] l0 = new int[2];
        view.getLocationOnScreen(l0);
        int x = view.getWidth() / 2 + l0[0];
        int y = view.getHeight() / 2 + l0[1];
        return new int[] { x, y };
    }

    public static boolean isMeizuDev() {
        if (android.os.Build.BRAND.toLowerCase(Locale.getDefault()).contains("meizu")) {
            return true;
        } else {
            return false;
        }
    }

    /*
     * public static Point getLocationInView(View src, View target) { final int[] l0 = new int[2];
     * src.getLocationOnScreen(l0);
     * 
     * final int[] l1 = new int[2]; target.getLocationOnScreen(l1);
     * 
     * l1[0] = l1[0] - l0[0] + target.getWidth() / 2; l1[1] = l1[1] - l0[1] + target.getHeight() / 2;
     * 
     * return new Point(l1[0], l1[1]); }
     */

    @TargetApi(Build.VERSION_CODES.ICE_CREAM_SANDWICH)
    public static boolean hasNavBar(Context context) {

        if (android.os.Build.BRAND.toLowerCase(Locale.getDefault()).contains("meizu")
                || Build.VERSION.SDK_INT < Build.VERSION_CODES.ICE_CREAM_SANDWICH) {
            return false;
        } else {
            // https://stackoverflow.com/questions/16092431/check-for-navigation-bar/23181183#23181183
            Resources resources = context.getResources();
            int id = resources.getIdentifier("config_showNavigationBar", "bool", "android");
            if (id > 0) {
                return resources.getBoolean(id);
            } else { // Check for keys
                boolean hasMenuKey = ViewConfiguration.get(context).hasPermanentMenuKey();
                boolean hasBackKey = KeyCharacterMap.deviceHasKey(KeyEvent.KEYCODE_BACK);
                return !hasMenuKey && !hasBackKey;
            }
        }
    }

    public static boolean isSdcardSpaceEnough(long requiredSpace) {
        if (requiredSpace < getSdFreeSpaceRawSize()) {
            return true;
        } else {
            return false;
        }
    }

    public static void stopScroll(AbsListView view) {// http://stackoverflow.com/questions/6369491/stop-listview-scroll-animation
                                                     // http://stackoverflow.com/questions/7819145/stop-scrolling-in-a-listview
        try {
            Field field = android.widget.AbsListView.class.getDeclaredField("mFlingRunnable");
            field.setAccessible(true);
            Object flingRunnable = field.get(view);
            if (flingRunnable != null) {
                Method method = Class.forName("android.widget.AbsListView$FlingRunnable").getDeclaredMethod("endFling");
                method.setAccessible(true);
                method.invoke(flingRunnable);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void hideInputMethod(Context ctx, View focusedView) {
        InputMethodManager inputMethodManager = (InputMethodManager) ctx.getSystemService(Context.INPUT_METHOD_SERVICE);
        inputMethodManager.hideSoftInputFromWindow(focusedView.getWindowToken(), 0);
        /*
         * if (inputMethodManager.isActive()) { inputMethodManager.hideSoftInputFromWindow(focusedView.getWindowToken(),
         * 0); }
         */
    }

    // http://stackoverflow.com/questions/3394765/how-to-check-available-space-on-android-device-on-mini-sd-card

    public static String[] calculateTotalFileInfo(ArrayList<AppInfo> list) {
        long size = 0;
        for (AppInfo appInfo : list) {
            size += appInfo.appRawSize;
        }
        String[] retVal = new String[2];
        retVal[0] = "" + size;
        retVal[1] = formatFileSize(size);
        return retVal;
    }

    public static String calculateTotalFileSizeStr(ArrayList<AppInfo> list) {
        long size = 0;
        for (AppInfo appInfo : list) {
            size += appInfo.appRawSize;
        }
        return formatFileSize(size);
    }

    public static long calculateTotalFileRawSize(ArrayList<AppInfo> list) {
        long size = 0;
        for (AppInfo appInfo : list) {
            size += appInfo.appRawSize;
        }
        return size;
    }

    public static String getBackupDirSizeStr(Context ctx) {
        return FileUtils.byteCountToDisplaySize(FileUtils.sizeOfDirectory(new File(getBackUpAPKfileDir(ctx))));
    }

    /**
     * 
     * @return [0]:rawSize [1]: 显示string
     */
    @SuppressWarnings("deprecation")
    @TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR2)
    public static String[] getSdUsedSpaceInfo() {
        long usedSpace = -1L;
        StatFs stat = new StatFs(Environment.getExternalStorageDirectory().getPath());
        if (android.os.Build.VERSION.SDK_INT >= 18) {
            usedSpace = (stat.getBlockCountLong() - stat.getAvailableBlocksLong()) * stat.getBlockSizeLong();
        } else {
            usedSpace = ((long) stat.getBlockCount() - (long) stat.getAvailableBlocks()) * (long) stat.getBlockSize();
        }
        String[] retVal = new String[2];
        retVal[0] = "" + usedSpace;
        retVal[1] = formatFileSize(usedSpace);
        // Formatter.formatFileSize(ctx, availableSpace);
        return retVal;
    }

    @SuppressWarnings("deprecation")
    @TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR2)
    public static String getSdUsedSpaceStr() {
        long usedSpace = -1L;
        StatFs stat = new StatFs(Environment.getExternalStorageDirectory().getPath());
        if (android.os.Build.VERSION.SDK_INT >= 18) {
            usedSpace = (stat.getBlockCountLong() - stat.getAvailableBlocksLong()) * stat.getBlockSizeLong();
        } else {
            usedSpace = ((long) stat.getBlockCount() - (long) stat.getAvailableBlocks()) * (long) stat.getBlockSize();
        }

        return formatFileSize(usedSpace);
    }

    @SuppressWarnings("deprecation")
    @TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR2)
    public static long getSdUsedSpaceRawSize() {
        long usedSpace = -1L;
        StatFs stat = new StatFs(Environment.getExternalStorageDirectory().getPath());
        if (android.os.Build.VERSION.SDK_INT >= 18) {
            usedSpace = (stat.getBlockCountLong() - stat.getAvailableBlocksLong()) * stat.getBlockSizeLong();
        } else {
            usedSpace = ((long) stat.getBlockCount() - (long) stat.getAvailableBlocks()) * (long) stat.getBlockSize();
        }

        return usedSpace;
    }

    @SuppressWarnings("deprecation")
    @TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR2)
    public static long getSdFreeSpaceRawSize() {
        long availableSpace = -1L;
        StatFs stat = new StatFs(Environment.getExternalStorageDirectory().getPath());
        if (android.os.Build.VERSION.SDK_INT >= 18) {
            availableSpace = (long) stat.getAvailableBlocksLong() * (long) stat.getBlockSizeLong();
        } else {
            availableSpace = (long) stat.getAvailableBlocks() * (long) stat.getBlockSize();
        }
        // Formatter.formatFileSize(ctx, availableSpace);
        return availableSpace;
    }

    @SuppressWarnings("deprecation")
    @TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR2)
    public static String[] getSdFreeSpaceInfo() {
        long availableSpace = -1L;
        StatFs stat = new StatFs(Environment.getExternalStorageDirectory().getPath());
        if (android.os.Build.VERSION.SDK_INT >= 18) {
            availableSpace = (long) stat.getAvailableBlocksLong() * (long) stat.getBlockSizeLong();
        } else {
            availableSpace = (long) stat.getAvailableBlocks() * (long) stat.getBlockSize();
        }
        String[] retVal = new String[2];
        retVal[0] = "" + availableSpace;
        retVal[1] = formatFileSize(availableSpace);
        return retVal;
    }
    
    @SuppressWarnings("deprecation")
    @TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR2)
    public static String[] getSdTotalSpaceInfo() {
        long totalSpace = -1L;
        StatFs stat = new StatFs(Environment.getExternalStorageDirectory().getPath());
        if (android.os.Build.VERSION.SDK_INT >= 18) {
            totalSpace = (long) stat.getBlockCountLong() * (long) stat.getBlockSizeLong();
        } else {
            totalSpace = (long) stat.getBlockCount() * (long) stat.getBlockSize();
        }
        String[] retVal = new String[2];
        retVal[0] = "" + totalSpace;
        retVal[1] = formatFileSize(totalSpace);
        return retVal;
    }

    @SuppressWarnings("deprecation")
    @TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR2)
    public static long getSdTotalSpaceRawSize() {
        long totalSpace = -1L;
        StatFs stat = new StatFs(Environment.getExternalStorageDirectory().getPath());
        if (android.os.Build.VERSION.SDK_INT >= 18) {
            totalSpace = (long) stat.getBlockCountLong() * (long) stat.getBlockSizeLong();
        } else {
            totalSpace = (long) stat.getBlockCount() * (long) stat.getBlockSize();
        }

        return totalSpace;
    }

    @SuppressWarnings("deprecation")
    @TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR2)
    public static String getSdTotalSpace() {
        long totalSpace = -1L;
        StatFs stat = new StatFs(Environment.getExternalStorageDirectory().getPath());
        if (android.os.Build.VERSION.SDK_INT >= 18) {
            totalSpace = (long) stat.getBlockCountLong() * (long) stat.getBlockSizeLong();
        } else {
            totalSpace = (long) stat.getBlockCount() * (long) stat.getBlockSize();
        }
        String retVal = formatFileSize(totalSpace);
        // Formatter.formatFileSize(ctx, availableSpace);
        return retVal;
    }

    public static void DismissDialog(DialogFragment D_fragment) {
        if (D_fragment != null && D_fragment.getDialog() != null && D_fragment.getDialog().isShowing()) {
            D_fragment.dismiss();
        }
    }
    /**
     * 
     * @param appInfo
     * @param list
     * @return list中是否有数据变化
     */
    public static boolean verifyBkListWithRemovedAppInfo(AppInfo appInfo, ArrayList<AppInfo> list){
        boolean retVel = false;
        for (AppInfo aEntry : list) {
            if (appInfo.packageName.equals(aEntry.packageName)) {
                //aEntry.isBackupAppInstalledDiffVer = false;
                //aEntry.isBackupAppInstalledSameVer = false;
                aEntry.bkAppInstallStatus = BK_APP_INSTALL_STATUS.NOT_INSTALLED;
                retVel = true;
            }
        }
        return retVel;
    }
    /**
     * 
     * @param appInfo
     * @param list
     * @return list中是否有数据变化
     */
    public static boolean verifyBkListWithNewAppInfo(AppInfo appInfo, ArrayList<AppInfo> list){
        boolean retVel = false;
        for (AppInfo aEntry : list) {
            if (appInfo.packageName.equals(aEntry.packageName) && (appInfo.versionCode == aEntry.versionCode)) {
                //aEntry.isBackupAppInstalledDiffVer = false;
                //aEntry.isBackupAppInstalledSameVer = true;
                aEntry.bkAppInstallStatus = BK_APP_INSTALL_STATUS.INSTALLED_SAME_VER;
                retVel = true;
            }
            else if(appInfo.packageName.equals(aEntry.packageName) && (appInfo.versionCode != aEntry.versionCode)){
                //aEntry.isBackupAppInstalledDiffVer = true;
                //aEntry.isBackupAppInstalledSameVer = false;
                aEntry.bkAppInstallStatus = BK_APP_INSTALL_STATUS.INSTALLED_DIFF_VER;
                retVel = true;
            }
            else{//未安装
                //aEntry.isBackupAppInstalledDiffVer = false;
                //aEntry.isBackupAppInstalledSameVer = false;
            }
        }
        return retVel;
    }
    
    public static BkAppInListSatus appInfoInListStatus(AppInfo appInfo, ArrayList<AppInfo> list) {
        int i = 0;
        for (AppInfo aEntry : list) {
            if (appInfo.packageName.equals(aEntry.packageName) && (appInfo.versionCode == aEntry.versionCode)) {
                return new BkAppInListSatus(IN_LIST_STATUS.IN_LIST_SAME_VER, i);
            }
            else if(appInfo.packageName.equals(aEntry.packageName) && (appInfo.versionCode != aEntry.versionCode)){
                return new BkAppInListSatus(IN_LIST_STATUS.IN_LIST_DIFF_VER, i);
            }
            i++;
        }
        return new BkAppInListSatus(IN_LIST_STATUS.NOT_IN_LIST);
    }
    
    public static BkAppInListSatus appInfoInListStatus(PackageInfo thePackageInfo, ArrayList<AppInfo> list) {
        int i = 0;
        for (AppInfo aEntry : list) {
            if (thePackageInfo.packageName.equals(aEntry.packageName) && (thePackageInfo.versionCode == aEntry.versionCode)) {
                return new BkAppInListSatus(IN_LIST_STATUS.IN_LIST_SAME_VER, i);
            }
            else if (thePackageInfo.packageName.equals(aEntry.packageName) && (thePackageInfo.versionCode != aEntry.versionCode)) {
                return new BkAppInListSatus(IN_LIST_STATUS.IN_LIST_DIFF_VER, i);
            }
            i++;
        }
        return new BkAppInListSatus(IN_LIST_STATUS.NOT_IN_LIST);
    }
    
    
    /**
     * 返回这个app在list中的位置
     * 
     * @param appInfo
     * @param list
     * @return 返回这个app在list中的位置,没有找到返回-1
     */
    public static int isAppInfoInList(PackageInfo thePackageInfo, ArrayList<AppInfo> list) {
        int i = 0;
        for (AppInfo aEntry : list) {
            if (thePackageInfo.packageName.equals(aEntry.packageName) && (thePackageInfo.versionCode == aEntry.versionCode)) {
                return i;
            }
            i++;
        }
        return -1;
    }
    
    /**
     * 返回这个app在list中的位置
     * 
     * @param appInfo
     * @param list
     * @return 返回这个app在list中的位置,没有找到返回-1
     */
    public static int isAppInfoInList(AppInfo appInfo, ArrayList<AppInfo> list) {
        int i = 0;
        for (AppInfo aEntry : list) {
            if (appInfo.packageName.equals(aEntry.packageName) && (appInfo.versionCode == aEntry.versionCode)) {
                return i;
            }
            i++;
        }
        return -1;
    }

    public static AppInfo getLastBackupAppFromSD(Context ctx) {
        File backupFolder = new File(Utils.getBackUpAPKfileDir(ctx));
        File[] files = backupFolder.listFiles(new ApkFileFilter());
        Arrays.sort(files, LastModifiedFileComparator.LASTMODIFIED_REVERSE);
        if (files.length > 0) {
            File theApk = files[0];

            PackageManager pkgMgr = ctx.getPackageManager();
            PackageInfo packageInfo =
                    pkgMgr.getPackageArchiveInfo(theApk.getAbsolutePath(), PackageManager.GET_ACTIVITIES);
            if (packageInfo != null) {
                ApplicationInfo applicationInfo = packageInfo.applicationInfo;
                applicationInfo.sourceDir = theApk.getAbsolutePath();
                applicationInfo.publicSourceDir = theApk.getAbsolutePath();

                AppInfo appInfo = Utils.buildAppInfoEntry(ctx, packageInfo, pkgMgr, false, false);
                appInfo.backupFilePath = theApk.getAbsolutePath();
                return appInfo;
            }
            return null;
        } else {
            return null;
        }
    }

//    public static AppInfo createAppInfoCopy(AppInfo appInfo) {
//        AppInfo tmpInfo = new AppInfo();
//        tmpInfo.appName = new String(appInfo.appName);
//        if (tmpInfo.appName.startsWith("\u00a0")) {
//            tmpInfo.appName = tmpInfo.appName.replaceFirst("\u00a0", "");
//        }
//
//        tmpInfo.packageName = new String(appInfo.packageName);
//
//        tmpInfo.versionName = new String(appInfo.versionName);
//
//        tmpInfo.versionCode = Integer.valueOf(tmpInfo.versionCode);
//        tmpInfo.iconBitmap = null;
//
//        tmpInfo.appSizeStr = new String(appInfo.appSizeStr);
//        tmpInfo.appRawSize = Long.parseLong("" + appInfo.appRawSize);
//
//        tmpInfo.lastModifiedTimeStr = new String(appInfo.lastModifiedTimeStr);
//        tmpInfo.lastModifiedRawTime = Long.parseLong("" + appInfo.lastModifiedRawTime);
//        tmpInfo.apkFilePath = new String(appInfo.apkFilePath);
//        tmpInfo.appSortName = new String(appInfo.appSortName);
//        tmpInfo.appNamePinyin = new String(appInfo.appNamePinyin);
//        tmpInfo.isSysApp = Boolean.valueOf(appInfo.isSysApp);
//        return tmpInfo;
//    }

    public static void sortBackUpAppList(Context ctx, ArrayList<AppInfo> list) {
        boolean foundDummy = false;
        if (list.size() > 1 && list.get(0) == AppInfo.DUMMY_APPINFO) {
            list.remove(0);
            foundDummy = true;
        }
        switch (getBackUpAppListSortType(ctx)) {
            case SortTypeDialogFragment.LIST_SORT_TYPE_NAME_ASC:
                Collections.sort(list, new AppNameComparator(true));
                break;
            case SortTypeDialogFragment.LIST_SORT_TYPE_NAME_DES:
                Collections.sort(list, new AppNameComparator(false));
                break;
            case SortTypeDialogFragment.LIST_SORT_TYPE_SIZE_ASC:
                Collections.sort(list, new AppSizeComparator(true));
                break;
            case SortTypeDialogFragment.LIST_SORT_TYPE_SIZE_DES:
                Collections.sort(list, new AppSizeComparator(false));
                break;
            case SortTypeDialogFragment.LIST_SORT_TYPE_LAST_MOD_TIME_ASC:
                Collections.sort(list, new LastBackupTimeComparator(true));
                break;
            case SortTypeDialogFragment.LIST_SORT_TYPE_LAST_MOD_TIME_DES:
                Collections.sort(list, new LastBackupTimeComparator(false));
                break;
        }
        if (foundDummy) {
            list.add(0, AppInfo.DUMMY_APPINFO);
        }
    }

    public static void sortUserAppList(Context ctx, ArrayList<AppInfo> list) {
        switch (getUserAppListSortType(ctx)) {
            case SortTypeDialogFragment.LIST_SORT_TYPE_NAME_ASC:
                Collections.sort(list, new AppNameComparator(true));
                break;
            case SortTypeDialogFragment.LIST_SORT_TYPE_NAME_DES:
                Collections.sort(list, new AppNameComparator(false));
                break;
            case SortTypeDialogFragment.LIST_SORT_TYPE_SIZE_ASC:
                Collections.sort(list, new AppSizeComparator(true));
                break;
            case SortTypeDialogFragment.LIST_SORT_TYPE_SIZE_DES:
                Collections.sort(list, new AppSizeComparator(false));
                break;
            case SortTypeDialogFragment.LIST_SORT_TYPE_LAST_MOD_TIME_ASC:
                Collections.sort(list, new LastModifiedComparator(true));
                break;
            case SortTypeDialogFragment.LIST_SORT_TYPE_LAST_MOD_TIME_DES:
                Collections.sort(list, new LastModifiedComparator(false));
                break;
        }
    }

    
    /**
     * 函数里面什么都没有做
     * @param context
     * @param appInfo
     */
    @Deprecated
    public static void verifyApp(Context context, AppInfo appInfo) {
        
        // 检查sd上的icon缓存
//        if (!isAppIconOnSd(context, appInfo)) {
//            appInfo.iconBitmap = drawableToBitmap(getIconDrawable(context, appInfo.packageName));
//            saveAppIconOnSd(context, appInfo);
//        }
    }

/*    public static boolean deleteAppIconInCache(Context context, AppInfo appInfo) {
        File file = appInfo.getAppIconCachePath(context);
        // File file = new File(Utils.getAppIconCacheDir(context), pkgName + ".png");
        if (file.exists()) {
            if (file.isFile()) {
                return file.delete();
            }
        }
        return false;
    }

    public static boolean deleteAppIconDir(File dir) {
        if (dir.exists()) {
            // if (dir.isDirectory()) {
            // String[] children = dir.list();
            // for (int i = 0; i < children.length; i++)
            // {
            // boolean success = deleteAppIconDir(new File(dir, children[i]));
            // if (!success) {
            // return false;
            // }
            // }
            // } //目录此时为空，可以删除
            // return dir.delete();
            return FileUtils.deleteQuietly(dir);
        }
        return false;
    }

    public static File getAppIconCacheDir(Context context) {
        File retVal = context.getExternalFilesDir(null);
        if(retVal != null){
            return retVal;
        }
        else{
            if(existSDCard()){
                String filePath = Environment.getExternalStorageDirectory()+ File.separator + 
                        "Android/data/" + context.getPackageName()+ File.separator + "files";
                return new File(filePath);
            }
            else{
                return null;
            }
        }
       
    }*/

    public static boolean existSDCard() {  
        if (android.os.Environment.getExternalStorageState().equals(  
          android.os.Environment.MEDIA_MOUNTED)) {  
            return true;  
        } else  
            return false;  
       }  
    
    public static Bitmap getIconBitmap(Context context, String pkgName) {
        Drawable iconDrawable = getIconDrawable(context, pkgName);
        if (iconDrawable != null) {
            Bitmap bitmap = drawableToBitmap(iconDrawable);
            //Bitmap bitmap = ((BitmapDrawable) iconDrawable).getBitmap();
            return bitmap;
        }
        return null;
    }

    public static Drawable getIconDrawable(Context context, String pkgName) {
        try {
            PackageManager pm = context.getPackageManager();
            PackageInfo packageInfo = pm.getPackageInfo(pkgName, 0);
            return packageInfo.applicationInfo.loadIcon(pm);
        } catch (NameNotFoundException e) {
            e.printStackTrace();
            return null;
        }

    }

    public static String pkgNameToAppName(Context context, String pkgName) {
        PackageManager pm = context.getPackageManager();
        ApplicationInfo ai = null;
        try {
            ai = pm.getApplicationInfo(pkgName, 0);
        } catch (final NameNotFoundException e) {
            ai = null;
        }
        String theAppName = (String) (ai != null ? pm.getApplicationLabel(ai) : "(unknown)");
        return theAppName;
    }

//    public static boolean isAppIconOnSd(Context ctx, AppInfo appInfo) {
//        File file = appInfo.getAppIconCachePath(ctx);
//        if (file.exists()) {
//            return true;
//        } else {
//            return false;
//        }
//    }

    /**废弃了，里面什么都没有做
     * save之后iconBitmap字段==null
     * 
     * @param context
     * @param appInfo
     * @return
     */
    @Deprecated
    public static boolean saveAppIconOnSd(Context context, AppInfo appInfo) {
        //appInfo.iconBitmap = null;
        return false;
//        OutputStream outStream = null;
//        // String extStorageDirectory = Environment.getExternalStorageDirectory().toString();
//        File file = appInfo.getAppIconCachePath(context);
//        Bitmap bitmap = null;
//        try {
//            outStream = new FileOutputStream(file);
//            if (appInfo.iconBitmap == null) {
//                bitmap = Utils.getIconBitmap(context, appInfo.packageName);
//            } else {
//                bitmap = appInfo.iconBitmap;
//            }
//
//            if (bitmap.compress(Bitmap.CompressFormat.PNG, 100, outStream)) {
//                outStream.flush();
//                outStream.close();
//                appInfo.iconBitmap = null;
//                return true;
//            }
//            appInfo.iconBitmap = null;
//            return false;
//        } catch (Exception e) {
//            // TODO Auto-generated catch block
//            e.printStackTrace();
//            appInfo.iconBitmap = null;
//            return false;
//        }
    }

    /**
     * 
     * @param context
     * @param packageInfo
     * @param pManager
     * @param saveAppIconOnSd 是否保存icon到sd
     * @param holdAppIcon 保存之后是否hold icon的bitmap在appinfo变量中
     * @return
     */
    public static AppInfo buildAppInfoEntry(Context context, PackageInfo packageInfo, PackageManager pManager,
            boolean saveAppIconOnSd, boolean holdAppIcon) {
        AppInfo tmpInfo = new AppInfo();
        tmpInfo.appName = packageInfo.applicationInfo.loadLabel(pManager).toString().trim();
        if (tmpInfo.appName.startsWith("\u00a0")) {
            tmpInfo.appName = tmpInfo.appName.replaceFirst("\u00a0", "");
        }

        tmpInfo.packageName = packageInfo.packageName;

        if (TextUtils.isEmpty(packageInfo.versionName)) {
            tmpInfo.versionName = context.getString(R.string.unknown);
        } else {
            tmpInfo.versionName = packageInfo.versionName;
        }

        tmpInfo.versionCode = packageInfo.versionCode;
        // tmpInfo.iconBitmap = drawableToBitmap(packageInfo.applicationInfo.loadIcon(pManager));
        // tmpInfo.iconDrawable = packageInfo.applicationInfo.loadIcon(pManager);
        // tmpInfo.appIconBytes = Utilities.DrawableToByteArray(tmpInfo.iconDrawable);
        //tmpInfo.firstTimeInstallRaw = packageInfo.firstInstallTime;
        File tmpAPKfile = new File(packageInfo.applicationInfo.publicSourceDir);
        tmpInfo.appSizeStr = Utils.formatFileSize(tmpAPKfile.length()).toString();
        tmpInfo.appRawSize = tmpAPKfile.length();

        SimpleDateFormat simpleDateFormat = Utils.getLocalDataDigitalDisplayFormat();
        tmpInfo.lastModifiedTimeStr = simpleDateFormat.format(new Date(tmpAPKfile.lastModified()));
        tmpInfo.lastModifiedRawTime = tmpAPKfile.lastModified();
        tmpInfo.apkFilePath = packageInfo.applicationInfo.publicSourceDir;
        Utils.GetPingYin(tmpInfo);
        // 排序做处理
        if ((packageInfo.applicationInfo.flags & ApplicationInfo.FLAG_SYSTEM) == 0) {
            tmpInfo.isSysApp = false;
        } else// sys app
        {
            tmpInfo.isSysApp = true;
        }

        /*if (holdAppIcon || saveAppIconOnSd) {
            Bitmap iconBitmap = drawableToBitmap(packageInfo.applicationInfo.loadIcon(pManager));

            if (saveAppIconOnSd) {
                tmpInfo.iconBitmap = iconBitmap;
                saveAppIconOnSd(context, tmpInfo);// save之后iconBitmap字段==null
            }

            if (holdAppIcon) {
                tmpInfo.iconBitmap = iconBitmap;
            }
            else{
                tmpInfo.iconBitmap = null;
            }
        }*/

        return tmpInfo;
    }

    public static AppInfo buildAppInfoEntry(Context context, String packName) {
        try {
            PackageManager pm = context.getPackageManager();
            PackageInfo packageInfo = pm.getPackageInfo(packName, 0);
            return buildAppInfoEntry(context, packageInfo, pm, true, false);
        } catch (NameNotFoundException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            return null;
        }
    }

    public static Bitmap drawableToBitmap(Drawable drawable) {

        if (drawable == null) {
            return null;
        }

        if (drawable instanceof BitmapDrawable) {
            return ((BitmapDrawable) drawable).getBitmap();
        }

        // We ask for the bounds if they have been set as they would be most
        // correct, then we check we are > 0
        final int width = !drawable.getBounds().isEmpty() ? drawable.getBounds().width() : drawable.getIntrinsicWidth();

        final int height =
                !drawable.getBounds().isEmpty() ? drawable.getBounds().height() : drawable.getIntrinsicHeight();

        // Now we check we are > 0
        final Bitmap bitmap =
                Bitmap.createBitmap(width <= 0 ? 1 : width, height <= 0 ? 1 : height, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        drawable.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
        drawable.draw(canvas);

        return bitmap;
    }

    public static byte[] DrawableToByteArray(Drawable drawable) {
        try {
            Bitmap bitmap = ((BitmapDrawable) drawable).getBitmap();
            ByteArrayOutputStream stream = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
            return stream.toByteArray();
        } catch (Exception e) {
            Log.e("ttt", "DrawableToByteArray error: " + e.toString());
            return null;
        }

    }

    public static Drawable BitmapToDrawable(Context context, Bitmap bitmap) {
        BitmapDrawable bd = new BitmapDrawable(context.getResources(), bitmap);
        Drawable d = (Drawable) bd;
        return d;
    }

    public static Drawable ByteArrayToDrawable(Context ctx, byte[] bytes) {
        if (bytes == null)
            return null;

        Bitmap bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
        BitmapDrawable bd = new BitmapDrawable(ctx.getResources(), bitmap);
        Drawable d = (Drawable) bd;
        bitmap = null;
        return d;

    }

    public static Drawable ZoomDrawable(Bitmap bitmap, Context ctx) {
        if(bitmap == null) return null;
        
        int width = bitmap.getWidth();// drawable.getIntrinsicWidth();
        int height = bitmap.getHeight();// drawable.getIntrinsicHeight();
        int scale =
                (int) TypedValue
                        .applyDimension(TypedValue.COMPLEX_UNIT_DIP, 42, ctx.getResources().getDisplayMetrics());

        // 创建操作图片用的Matrix对象
        Matrix matrix = new Matrix();
        // 计算缩放比例
        float scaleWidth = ((float) scale / width);
        float scaleHeight = ((float) scale / height);
        // 设置缩放比例
        matrix.postScale(scaleWidth, scaleHeight);
        // 建立新的bitmap，其内容是对原bitmap的缩放后的图
        Bitmap newbmp = Bitmap.createBitmap(bitmap, 0, 0, width, height, matrix, true);
        return new BitmapDrawable(ctx.getResources(), newbmp);
    }

    public static Drawable ZoomDrawable(Drawable drawable, Context ctx) {
        int width = drawable.getIntrinsicWidth();
        int height = drawable.getIntrinsicHeight();
        int scale =
                (int) TypedValue
                        .applyDimension(TypedValue.COMPLEX_UNIT_DIP, 42, ctx.getResources().getDisplayMetrics());
        // drawable转换成bitmap
        Bitmap oldbmp = ((BitmapDrawable) drawable).getBitmap();
        // 创建操作图片用的Matrix对象
        Matrix matrix = new Matrix();
        // 计算缩放比例
        float scaleWidth = ((float) scale / width);
        float scaleHeight = ((float) scale / height);
        // 设置缩放比例
        matrix.postScale(scaleWidth, scaleHeight);
        // 建立新的bitmap，其内容是对原bitmap的缩放后的图
        Bitmap newbmp = Bitmap.createBitmap(oldbmp, 0, 0, width, height, matrix, true);
        return new BitmapDrawable(ctx.getResources(), newbmp);
    }

    public static void chooseSendByApp(Context ctx, Uri uri) {
        Intent sendIntent = new Intent(Intent.ACTION_SEND);
        sendIntent.setType("application/vnd.android.package-archive");
        sendIntent.putExtra(Intent.EXTRA_STREAM, uri);// 添加附件
        sendIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, ctx.getString(R.string.share_apps));// 主题
        sendIntent.putExtra(android.content.Intent.EXTRA_TEXT, ctx.getString(R.string.email_body)); // 邮件主体
        sendIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        ctx.startActivity(Intent.createChooser(sendIntent, ctx.getString(R.string.send_by)));// Chooser的标题
    }

    public static void chooseSendByAppWithAppList(Context ctx, ArrayList<AppInfo> appList) {
        ArrayList<Uri> SnedApkUris = new ArrayList<Uri>();
        for (AppInfo appInfo : appList) {
            SnedApkUris.add(Uri.parse("file://" + appInfo.apkFilePath));
        }
        Utils.chooseSendByApp(ctx, SnedApkUris);
    }

    public static void chooseSendByApp(Context ctx, ArrayList<Uri> Uris) {

        if (Uris.size() == 1) {
            chooseSendByApp(ctx, Uris.get(0));
        } else {
            Intent sendIntent = new Intent(Intent.ACTION_SEND_MULTIPLE);
            sendIntent.setType("application/vnd.android.package-archive");
            sendIntent.putParcelableArrayListExtra(Intent.EXTRA_STREAM, Uris);// 添加附件
            sendIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, ctx.getString(R.string.share_apps));// 主题
            sendIntent.putExtra(android.content.Intent.EXTRA_TEXT, ctx.getString(R.string.email_body)); // 邮件主体
            sendIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            ctx.startActivity(Intent.createChooser(sendIntent, ctx.getString(R.string.send_by)));// Chooser的标题
        }
    }

    public static boolean playAniAppDetails(Context ctx) {
        boolean retVal = ctx.getSharedPreferences("MyAppMgrSharedPreferences", 0).getBoolean("playAniAppDetails", true);
        return retVal;
    }

    public static void setPlayAniAppDetails(Context ctx, boolean flag) {
        SharedPreferences.Editor shPrefEdit = ctx.getSharedPreferences("MyAppMgrSharedPreferences", 0).edit();
        shPrefEdit.putBoolean("playAniAppDetails", flag);
        // shPrefEdit.commit();
        SharedPreferencesCompat.apply(shPrefEdit);
    }

    public static boolean isAppListInDb(Context ctx) {
        boolean retVal = ctx.getSharedPreferences("MyAppMgrSharedPreferences", 0).getBoolean("isAppListInDb", false);
        return retVal;
    }

    public static void setAppListInDb(Context ctx, boolean flag) {
        SharedPreferences.Editor shPrefEdit = ctx.getSharedPreferences("MyAppMgrSharedPreferences", 0).edit();
        shPrefEdit.putBoolean("isAppListInDb", flag);
        // shPrefEdit.commit();
        SharedPreferencesCompat.apply(shPrefEdit);
    }

    public static int getUserAppListSortType(Context ctx) {
        int type =
                ctx.getSharedPreferences("MyAppMgrSharedPreferences", 0).getInt("UserAppListSortType",
                        SortTypeDialogFragment.LIST_SORT_TYPE_LAST_MOD_TIME_DES);
        return type;
    }

    public static int getBackUpAppListSortType(Context ctx) {
        int type =
                ctx.getSharedPreferences("MyAppMgrSharedPreferences", 0).getInt("BackUpAppListSortType",
                        SortTypeDialogFragment.LIST_SORT_TYPE_LAST_MOD_TIME_DES);
        return type;
    }

    public static void setUserAppListSortType(Context ctx, int sortType) {
        SharedPreferences.Editor shPrefEdit = ctx.getSharedPreferences("MyAppMgrSharedPreferences", 0).edit();
        shPrefEdit.putInt("UserAppListSortType", sortType);
        // shPrefEdit.commit();
        SharedPreferencesCompat.apply(shPrefEdit);
    }

    public static void setBackUpAppListSortType(Context ctx, int sortType) {
        SharedPreferences.Editor shPrefEdit = ctx.getSharedPreferences("MyAppMgrSharedPreferences", 0).edit();
        shPrefEdit.putInt("BackUpAppListSortType", sortType);
        // shPrefEdit.commit();
        SharedPreferencesCompat.apply(shPrefEdit);
    }

    public static int dp2px(Context ctx, float dpValue) {
        final float scale = ctx.getResources().getDisplayMetrics().density;
        return (int) (dpValue * scale + 0.5f);
    }

    /**
     * 
     * @param appInfo
     * @param destFilePath sd卡文件存储目录(/sd/MyAppMgr/)
     * @return 成功返回备份的文件位置(/sd/MyAppMgr/XXX.apk),失败返回null
     */
    public static String BackupApp(Context ctx, AppInfo appInfo, String destFilePath) {
        boolean fPkgName = forcePkgName(ctx,  appInfo,  destFilePath + appInfo.getBackupFileName_AppName());
        if(fPkgName){
            if (copyFile(appInfo.apkFilePath, destFilePath + appInfo.getBackupFileName_pkgName())) {
                appInfo.backupFilePath = destFilePath + appInfo.getBackupFileName_pkgName();
                return appInfo.backupFilePath;
            }
            else{
                return null;
            }
        }
        
        if (copyFile(appInfo.apkFilePath, destFilePath + appInfo.getBackupFileName_AppName())) {
            appInfo.backupFilePath = destFilePath + appInfo.getBackupFileName_AppName();
            return appInfo.backupFilePath;
        } else if (copyFile(appInfo.apkFilePath, destFilePath + appInfo.getBackupFileName_pkgName())) {
            appInfo.backupFilePath = destFilePath + appInfo.getBackupFileName_pkgName();
            return appInfo.backupFilePath;
        } else {
            appInfo.backupFilePath = "";
            return null;
        }
    }

    public static boolean forcePkgName(Context ctx, AppInfo appInfo, String apkFilePath){
        File file = new File(apkFilePath);
        if(file.exists()){
            PackageManager mPackageManager = ctx.getPackageManager();
            PackageInfo packageInfo = mPackageManager.getPackageArchiveInfo(apkFilePath, PackageManager.GET_ACTIVITIES);
            ApplicationInfo applicationInfo = packageInfo.applicationInfo;
            applicationInfo.sourceDir = apkFilePath;
            applicationInfo.publicSourceDir = apkFilePath;
            if(appInfo.packageName.equals(packageInfo.packageName) && appInfo.versionCode == packageInfo.versionCode){
                return false;
            }
            else{
                return true;
            }
        }
        else{
            return false;
        }
        
    }
    
    
    public static void saveBackUpAPKfileDir(Context ctx, String path) {
        SharedPreferences.Editor shPrefEdit = ctx.getSharedPreferences("MyAppMgrSharedPreferences", 0).edit();
        shPrefEdit.putString("backupApkFileDir", path);
        SharedPreferencesCompat.apply(shPrefEdit);
    }

    public static String getBackUpAPKfileDir(Context ctx) {
        String path =
                ctx.getSharedPreferences("MyAppMgrSharedPreferences", 0).getString("backupApkFileDir",
                        Constants.DEF_BACKUP_DIR);

        File backUpFileDir = new File(path);
        if (!backUpFileDir.exists()) {
            backUpFileDir.mkdirs();
        }
        return path;
    }

    public static void installAPK(Context ctx, String file) {
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setDataAndType(Uri.fromFile(new File(file)), "application/vnd.android.package-archive");
        ctx.startActivity(intent);
    }

    /**
     * 
     * @param SrcFile 源文件 (/sd/MyAppMgr/source.apk)
     * @param DestFile 目标文件 (/sd/MyAppMgr/dest.apk)
     * @return
     */
    public static boolean copyFile(String SrcFile, String DestFile) {

        long startTime = System.currentTimeMillis();

        boolean success = false;
        /*
         * File srcFile = new File(SrcFile); File destFile = new File(DestFile); try { FileUtils.copyFile(srcFile,
         * destFile); success = true; } catch (IOException e) { // TODO Auto-generated catch block e.printStackTrace();
         * success = false; } long finishTime = System.currentTimeMillis(); L.d("复制用时: "+(finishTime-startTime));
         */

        // boolean success = false;

        File srcFile = new File(SrcFile);
        File destFile = new File(DestFile);

        try {
            FileInputStream fcin = new FileInputStream(srcFile);
            FileOutputStream fcout = new FileOutputStream(destFile);
            fcin.getChannel().transferTo(0, fcin.getChannel().size(), fcout.getChannel());
            fcin.close();
            fcout.close();
            success = true;
        } catch (FileNotFoundException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        long finishTime = System.currentTimeMillis();
        L.d("复制用时: " + (finishTime - startTime));
        return success;
    }

    public static boolean ContainsChinese(String str) {
        char[] ch = str.trim().toCharArray();
        for (int i = 0; i < ch.length; i++) {
            char c = ch[i];
            if (isChinese(c)) {
                return true;
            }
        }
        return false;
    }

    // GENERAL_PUNCTUATION 判断中文的“号
    // CJK_SYMBOLS_AND_PUNCTUATION 判断中文的。号
    // HALFWIDTH_AND_FULLWIDTH_FORMS 判断中文的，号
    public static final boolean isChinese(char c) {
        Character.UnicodeBlock ub = Character.UnicodeBlock.of(c);
        if (ub == Character.UnicodeBlock.CJK_UNIFIED_IDEOGRAPHS
                || ub == Character.UnicodeBlock.CJK_COMPATIBILITY_IDEOGRAPHS
                || ub == Character.UnicodeBlock.CJK_UNIFIED_IDEOGRAPHS_EXTENSION_A
                || ub == Character.UnicodeBlock.GENERAL_PUNCTUATION
                || ub == Character.UnicodeBlock.CJK_SYMBOLS_AND_PUNCTUATION
                || ub == Character.UnicodeBlock.HALFWIDTH_AND_FULLWIDTH_FORMS) {
            return true;
        }
        return false;
    }

    /**
     * 
     * @param inputString
     * @return 返回大写开头字母
     */
    public static String GetFirstChar(String inputString) {
        String output = "";
        char[] input = inputString.trim().toCharArray();
        if (java.lang.Character.toString(input[0]).matches("[\\u4E00-\\u9FA5]+")) // 为汉字
        {
            HanyuPinyinOutputFormat format = new HanyuPinyinOutputFormat();
            format.setCaseType(HanyuPinyinCaseType.LOWERCASE);
            format.setToneType(HanyuPinyinToneType.WITHOUT_TONE);
            format.setVCharType(HanyuPinyinVCharType.WITH_V);
            try {
                String[] temp = PinyinHelper.toHanyuPinyinStringArray(input[0], format);
                output += temp[0].charAt(0);
            } catch (BadHanyuPinyinOutputFormatCombination e) {
                e.printStackTrace();
            }
        } else
            output += java.lang.Character.toString(input[0]);

        return output.toUpperCase(Locale.getDefault());
    }

    /**
     * 参考网站 http://hi.baidu.com/daqing15/item/613e59e0eb2424f32b09a413
     * 
     * @param inputString 可以是多种字符混合,包含汉字转成拼音，没有汉字返回 "" I love 北京-->I love beijing,
     * @return
     */
    public static void GetPingYin(AppInfo mAppInfo) {
        HanyuPinyinOutputFormat format = new HanyuPinyinOutputFormat();
        format.setCaseType(HanyuPinyinCaseType.LOWERCASE);
        format.setToneType(HanyuPinyinToneType.WITHOUT_TONE);
        format.setVCharType(HanyuPinyinVCharType.WITH_V);
        boolean ContainsChinese = false;
        mAppInfo.appSortName = "";
        mAppInfo.appNamePinyin = "";
        char[] input = mAppInfo.appName.trim().toCharArray();
        String output = "";

        try {
            for (int i = 0; i < input.length; i++) {
                if (java.lang.Character.toString(input[i]).matches("[\\u4E00-\\u9FA5]+")) // 为汉字
                {
                    String[] temp = PinyinHelper.toHanyuPinyinStringArray(input[i], format);
                    output += "z" + temp[0] + " ";// 说明英文字符是汉字转换的拼音，例如 英文a和啊计较
                    ContainsChinese = true;
                    mAppInfo.appNamePinyin += temp[0];
                } else
                    output += java.lang.Character.toString(input[i]) + " ";
            }
        } catch (BadHanyuPinyinOutputFormatCombination e) {
            e.printStackTrace();
        }

        if (ContainsChinese) {
            output = output.trim();
            mAppInfo.appSortName = output;
            // return output;
        } else {
            mAppInfo.appSortName = "";
            // return "";
        }
    }

    @TargetApi(Build.VERSION_CODES.ICE_CREAM_SANDWICH)
    public static void uninstallApp(Context ctx, AppInfo appInfo){
        Uri packageUri = Uri.parse("package:" + appInfo.packageName);
        Intent uninstallIntent;
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.ICE_CREAM_SANDWICH) {
            uninstallIntent = new Intent(Intent.ACTION_DELETE, packageUri);
        } else {
            uninstallIntent = new Intent(Intent.ACTION_UNINSTALL_PACKAGE, packageUri);
        }
        ctx.startActivity(uninstallIntent);   
    }
    
    /**
     * 调用系统InstalledAppDetails界面显示已安装应用程序的详细信息。 对于Android 2.3（Api Level 9）以上，使用SDK提供的接口；
     * 2.3以下，使用非公开的接口（查看InstalledAppDetails源码）。
     * 
     * @param context
     * 
     * @param packageName 应用程序的包名
     */
    @TargetApi(Build.VERSION_CODES.ICE_CREAM_SANDWICH)
    @SuppressLint("InlinedApi")
    public static void showInstalledAppDetails(Context context, String packageName) {
        String SCHEME = "package";
        /**
         * 调用系统InstalledAppDetails界面所需的Extra名称(用于Android 2.1及之前版本)
         */
        String APP_PKG_NAME_21 = "com.android.settings.ApplicationPkgName";
        /**
         * 调用系统InstalledAppDetails界面所需的Extra名称(用于Android 2.2)
         */
        String APP_PKG_NAME_22 = "pkg";
        /**
         * InstalledAppDetails所在包名
         */
        String APP_DETAILS_PACKAGE_NAME = "com.android.settings";
        /**
         * InstalledAppDetails类名
         */
        String APP_DETAILS_CLASS_NAME = "com.android.settings.InstalledAppDetails";

        Intent intent = new Intent();
        final int apiLevel = Build.VERSION.SDK_INT;
        if (apiLevel >= 9) { // 2.3（ApiLevel 9）以上，使用SDK提供的接口
            intent.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
            Uri uri = Uri.fromParts(SCHEME, packageName, null);
            intent.setData(uri);
        } else { // 2.3以下，使用非公开的接口（查看InstalledAppDetails源码）
            // 2.2和2.1中，InstalledAppDetails使用的APP_PKG_NAME不同。
            final String appPkgName = (apiLevel == 8 ? APP_PKG_NAME_22 : APP_PKG_NAME_21);
            intent.setAction(Intent.ACTION_VIEW);
            intent.setClassName(APP_DETAILS_PACKAGE_NAME, APP_DETAILS_CLASS_NAME);
            intent.putExtra(appPkgName, packageName);
        }
        context.startActivity(intent);
    }

    public static String getSelfVerName(Context context)// 获取版本号
    {
        try {
            PackageInfo pi = context.getPackageManager().getPackageInfo(context.getPackageName(), 0);
            return "v " + pi.versionName;
        } catch (NameNotFoundException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            return "";
        }
    }

    public static ApplicationInfo getSelfAppInfo(Context ctx) {
        PackageManager pm = ctx.getPackageManager();
        ApplicationInfo appInfo = null;
        try {
            appInfo = pm.getApplicationInfo(ctx.getPackageName(), 0);
        } catch (final NameNotFoundException e) {
            appInfo = null;
        }
        return appInfo;
    }

    public static boolean isAnyStoreInstalled(Context ctx) {
        Intent market = new Intent(Intent.ACTION_VIEW, Uri.parse("market://search?q=dummy"));
        PackageManager manager = ctx.getPackageManager();
        List<ResolveInfo> list = manager.queryIntentActivities(market, 0);

        return list.size() > 0;
    }

    @SuppressLint("SimpleDateFormat")
    public static SimpleDateFormat getLocalDataDigitalDisplayFormat() {
        /**
         * it dd/MM/yy ch yy-M-d eng M/d/yy
         */
        SimpleDateFormat dateformat = new SimpleDateFormat(); // new
                                                              // SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String datePatternStr = dateformat.toLocalizedPattern().split(" ")[0];
        String daySection = "";
        String MonthSection = "";
        String YearSection = "";
        for (int i = 0; i < datePatternStr.length(); i++) {
            if (datePatternStr.charAt(i) == 'd') {
                daySection += "d";
            } else if (datePatternStr.charAt(i) == 'M') {
                MonthSection += "M";
            } else if (datePatternStr.charAt(i) == 'y') {
                YearSection += "y";
            }
        }

        if (!daySection.equals("dd")) {
            datePatternStr = datePatternStr.replace(daySection, "dd");
        }

        if (!MonthSection.equals("MM")) {
            datePatternStr = datePatternStr.replace(MonthSection, "MM");
        }

        if (!YearSection.equals("yyyy")) {
            datePatternStr = datePatternStr.replace(YearSection, "yyyy");
        }

        dateformat.applyPattern(datePatternStr);

        return dateformat;
    }

    @SuppressLint("SimpleDateFormat")
    public static SimpleDateFormat getLocalDataDigitalDisplayFormatFull() {
        /**
         * it dd/MM/yy ch yy-M-d eng M/d/yy
         */
        SimpleDateFormat dateformat = new SimpleDateFormat(); // new
                                                              // SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String datePatternStr = dateformat.toLocalizedPattern().split(" ")[0];
        String daySection = "";
        String MonthSection = "";
        String YearSection = "";
        for (int i = 0; i < datePatternStr.length(); i++) {
            if (datePatternStr.charAt(i) == 'd') {
                daySection += "d";
            } else if (datePatternStr.charAt(i) == 'M') {
                MonthSection += "M";
            } else if (datePatternStr.charAt(i) == 'y') {
                YearSection += "y";
            }
        }

        if (!daySection.equals("dd")) {
            datePatternStr = datePatternStr.replace(daySection, "dd");
        }

        if (!MonthSection.equals("MM")) {
            datePatternStr = datePatternStr.replace(MonthSection, "MM");
        }

        if (!YearSection.equals("yyyy")) {
            datePatternStr = datePatternStr.replace(YearSection, "yyyy");
        }
        datePatternStr += " HH:mm:ss";
        dateformat.applyPattern(datePatternStr);

        return dateformat;
    }

    /**
     * 只有年月日
     * 
     * @param date
     * @return
     */
    public static String formatTimeDisplay(Date date) {
        SimpleDateFormat dateFormat = getLocalDataDigitalDisplayFormat();
        return dateFormat.format(date);
    }

    /**
     * 年月日时分秒
     * 
     * @param date
     * @return
     */
    public static String formatTimeDisplayFull(Date date) {
        SimpleDateFormat dateFormat = getLocalDataDigitalDisplayFormatFull();
        return dateFormat.format(date);
    }

    public static String formatFileSize(long length) {
        String result = null;
        int sub_string = 0;
        if (length >= 1073741824) {
            sub_string = String.valueOf((float) length / 1073741824).indexOf(".");
            result = ((float) length / 1073741824 + "000").substring(0, sub_string + 3) + " GB";
        } else if (length >= 1048576) {
            sub_string = String.valueOf((float) length / 1048576).indexOf(".");
            result = ((float) length / 1048576 + "000").substring(0, sub_string + 3) + " MB";
        } else if (length >= 1024) {
            sub_string = String.valueOf((float) length / 1024).indexOf(".");
            result = ((float) length / 1024 + "000").substring(0, sub_string + 3) + " KB";
        } else if (length < 1024)
            result = Long.toString(length) + " B";
        return result;
    }

    public static float dp2px(Resources resources, float dp) {
        final float scale = resources.getDisplayMetrics().density;
        return dp * scale + 0.5f;
    }

    public static float sp2px(Resources resources, float sp) {
        final float scale = resources.getDisplayMetrics().scaledDensity;
        return sp * scale;
    }

}
