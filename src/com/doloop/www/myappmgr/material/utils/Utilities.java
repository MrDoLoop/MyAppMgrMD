package com.doloop.www.myappmgr.material.utils;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
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
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.pm.ResolveInfo;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.Settings;
import android.text.TextUtils;
import android.util.Log;
import android.util.TypedValue;

import com.doloop.www.myappmgr.material.dao.AppInfo;
import com.doloop.www.myappmgr.material.fragments.SortTypeDialogFragment;
import com.doloop.www.myappmgrmaterial.R;

public class Utilities {

    public static boolean isAppInfoInList(AppInfo appInfo, ArrayList<AppInfo> list){
        for(AppInfo aEntry : list){
            if(appInfo.packageName.equals(aEntry.packageName)){
                return true;
            }
        }
        return false;
    }
    
    public static AppInfo getLastBackupAppFromSD(Context ctx) {
        File backupFolder = new File(Utilities.getBackUpAPKfileDir(ctx));
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

                AppInfo appInfo = Utilities.buildAppInfoEntry(ctx, packageInfo, pkgMgr, false);
                appInfo.backupFilePath = theApk.getAbsolutePath();
                return appInfo;
            }
            return null;
        } else {
            return null;
        }
    }

    public static AppInfo createAppInfoCopy(AppInfo appInfo) {
        AppInfo tmpInfo = new AppInfo();
        tmpInfo.appName = new String(appInfo.appName);
        if (tmpInfo.appName.startsWith("\u00a0")) {
            tmpInfo.appName = tmpInfo.appName.replaceFirst("\u00a0", "");
        }

        tmpInfo.packageName = new String(appInfo.packageName);

        tmpInfo.versionName = new String(appInfo.versionName);

        tmpInfo.versionCode = Integer.valueOf(tmpInfo.versionCode);
        tmpInfo.iconBitmap = null;

        tmpInfo.appSizeStr = new String(appInfo.appSizeStr);
        tmpInfo.appRawSize = Long.parseLong("" + appInfo.appRawSize);

        tmpInfo.lastModifiedTimeStr = new String(appInfo.lastModifiedTimeStr);
        tmpInfo.lastModifiedRawTime = Long.parseLong("" + appInfo.lastModifiedRawTime);
        tmpInfo.apkFilePath = new String(appInfo.apkFilePath);
        tmpInfo.appSortName = new String(appInfo.appSortName);
        tmpInfo.appNamePinyin = new String(appInfo.appNamePinyin);
        tmpInfo.isSysApp = Boolean.valueOf(appInfo.isSysApp);
        return tmpInfo;
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

    public static void verifyApp(Context context, AppInfo appInfo) {
        File file = appInfo.getAppIconCachePath(context);
        if (!file.exists()) {
            // appInfo.iconDrawable = getIconDrawable(context, appInfo.packageName);
            appInfo.iconBitmap = drawableToBitmap(getIconDrawable(context, appInfo.packageName));
            saveAppIconOnSd(context, appInfo);
        }
    }

    public static boolean deleteAppIconInCache(Context context, String pkgName) {
        File file = new File(Utilities.getAppIconCacheDir(context), pkgName + ".png");
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
        return context.getExternalFilesDir(null);
    }

    public static Bitmap getIconBitmap(Context context, String pkgName) {
        Drawable iconDrawable = getIconDrawable(context, pkgName);
        if (iconDrawable != null) {
            Bitmap bitmap = ((BitmapDrawable) iconDrawable).getBitmap();
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

    private static boolean saveAppIconOnSd(Context context, AppInfo appInfo) {

        OutputStream outStream = null;
        // String extStorageDirectory = Environment.getExternalStorageDirectory().toString();
        File file = appInfo.getAppIconCachePath(context);

        try {
            outStream = new FileOutputStream(file);
            Bitmap bitmap = appInfo.iconBitmap;

            if (bitmap.compress(Bitmap.CompressFormat.PNG, 100, outStream)) {
                outStream.flush();
                outStream.close();
                return true;
            }
            return false;
        } catch (FileNotFoundException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            return false;
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            return false;
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            return false;
        }
    }

    public static AppInfo buildAppInfoEntry(Context context, PackageInfo packageInfo, PackageManager pManager,
            boolean saveAppIconOnSd) {
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
        tmpInfo.iconBitmap = drawableToBitmap(packageInfo.applicationInfo.loadIcon(pManager));
        // tmpInfo.iconDrawable = packageInfo.applicationInfo.loadIcon(pManager);
        // tmpInfo.appIconBytes = Utilities.DrawableToByteArray(tmpInfo.iconDrawable);
        // tmpInfo.firstTimeInstallDate = simpleDateFormat.format(new Date(packageInfo.firstInstallTime));
        // tmpInfo.firstTimeInstallDate =
        // dateformat.format(packageInfo.firstInstallTime);
        File tmpAPKfile = new File(packageInfo.applicationInfo.publicSourceDir);
        tmpInfo.appSizeStr = Utilities.formatFileSize(tmpAPKfile.length()).toString();
        tmpInfo.appRawSize = tmpAPKfile.length();

        SimpleDateFormat simpleDateFormat = Utilities.getLocalDataDigitalDisplayFormat();
        tmpInfo.lastModifiedTimeStr = simpleDateFormat.format(new Date(tmpAPKfile.lastModified()));
        tmpInfo.lastModifiedRawTime = tmpAPKfile.lastModified();
        tmpInfo.apkFilePath = packageInfo.applicationInfo.publicSourceDir;
        Utilities.GetPingYin(tmpInfo);
        // 排序做处理
        if ((packageInfo.applicationInfo.flags & ApplicationInfo.FLAG_SYSTEM) == 0) {
            tmpInfo.isSysApp = false;
        } else// sys app
        {
            tmpInfo.isSysApp = true;
        }
        if (saveAppIconOnSd) {
            saveAppIconOnSd(context, tmpInfo);
        }

        return tmpInfo;
    }

    public static AppInfo buildAppInfoEntry(Context context, String packName) {
        try {
            PackageManager pm = context.getPackageManager();
            PackageInfo packageInfo = pm.getPackageInfo(packName, 0);
            return buildAppInfoEntry(context, packageInfo, pm, true);
        } catch (NameNotFoundException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            return null;
        }
    }

    public static Bitmap drawableToBitmap(Drawable drawable) {
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

    public static void chooseSendByApp(Context ctx, ArrayList<Uri> Uris) {
        Intent sendIntent = new Intent(Intent.ACTION_SEND_MULTIPLE);
        sendIntent.setType("application/vnd.android.package-archive");
        sendIntent.putParcelableArrayListExtra(Intent.EXTRA_STREAM, Uris);// 添加附件
        sendIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, "Share apps");// 主题
        sendIntent.putExtra(android.content.Intent.EXTRA_TEXT, "Enjoy apps, thanks"); // 邮件主体
        sendIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        ctx.startActivity(Intent.createChooser(sendIntent, ctx.getString(R.string.send_by)));// Chooser的标题
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

    public static void setUserAppListSortType(Context ctx, int sortType) {
        SharedPreferences.Editor shPrefEdit = ctx.getSharedPreferences("MyAppMgrSharedPreferences", 0).edit();
        shPrefEdit.putInt("UserAppListSortType", sortType);
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
    public static String BackupApp(AppInfo appInfo, String destFilePath) {
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

    public static String getBackUpAPKfileDir(Context ctx) {
        String path = Environment.getExternalStorageDirectory().toString() + "/MyAppMgrMD/";
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

    /**
     * 调用系统InstalledAppDetails界面显示已安装应用程序的详细信息。 对于Android 2.3（Api Level 9）以上，使用SDK提供的接口；
     * 2.3以下，使用非公开的接口（查看InstalledAppDetails源码）。
     * 
     * @param context
     * 
     * @param packageName 应用程序的包名
     */
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
            return "v "+pi.versionName;
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
}
