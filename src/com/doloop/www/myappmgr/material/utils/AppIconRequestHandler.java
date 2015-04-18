package com.doloop.www.myappmgr.material.utils;
import java.io.IOException;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;

import com.squareup.picasso.Request;
import com.squareup.picasso.RequestHandler;
import com.squareup.picasso.Picasso;

public class AppIconRequestHandler extends RequestHandler {

    /** Uri scheme for app icons */
    public static final String SCHEME_APP_ICON = "app-icon";
    public static final String SCHEME_BACKUP_APP_ICON = "backup-app-icon";

    private PackageManager mPackageManager;

    public AppIconRequestHandler(Context context) {
        mPackageManager = context.getPackageManager();
    }

    @Override
    public boolean canHandleRequest(Request data) {
        // only handle Uris matching our scheme
        return (SCHEME_APP_ICON.equals(data.uri.getScheme()) || SCHEME_BACKUP_APP_ICON.equals(data.uri.getScheme()));
    }

    @Override
    public Result load(Request request, int networkPolicy) throws IOException {

        Drawable drawable = null;
        Bitmap bitmap = null;
        String schemeStr = request.uri.getScheme();
        try {
            if(SCHEME_APP_ICON.equals(schemeStr)){
                String appPkgName = request.uri.toString().replace(SCHEME_APP_ICON + ":", "");
                drawable = mPackageManager.getApplicationIcon(appPkgName);
            }
            else if(SCHEME_BACKUP_APP_ICON.equals(schemeStr)){
                String apkFilePath = request.uri.toString().replace(SCHEME_BACKUP_APP_ICON + ":", "");
                PackageInfo packageInfo = mPackageManager.getPackageArchiveInfo(apkFilePath, PackageManager.GET_ACTIVITIES);
                if (packageInfo != null) {
                    ApplicationInfo applicationInfo = packageInfo.applicationInfo;
                    //以下2个属性赋值之后才能正确读出东西
                    applicationInfo.sourceDir = apkFilePath;
                    applicationInfo.publicSourceDir = apkFilePath;
                }
                drawable = packageInfo.applicationInfo.loadIcon(mPackageManager);
            }
        } catch (PackageManager.NameNotFoundException ignored) {
            ignored.printStackTrace();
        }

        if (drawable != null) {
            bitmap =  Utils.drawableToBitmap(drawable);
            //bitmap = ((BitmapDrawable) drawable).getBitmap();
        }
        return new Result(bitmap, Picasso.LoadedFrom.DISK);
    }
}