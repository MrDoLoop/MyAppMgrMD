package com.doloop.www.myappmgr.material;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.Toast;

import com.doloop.www.myappmgr.material.dao.AppInfo;
import com.doloop.www.myappmgr.material.dao.DaoUtils;
import com.doloop.www.myappmgr.material.utils.Utils;

public class AppUpdateStaticReceiver extends BroadcastReceiver {
    public static boolean handleEvent = true;

    public AppUpdateStaticReceiver() {

    }

    @Override
    public void onReceive(Context context, Intent intent) {
        // TODO Auto-generated method stub
        if (handleEvent) {
          //���ǰ�װ�����յ�removed��Ȼ������added
            String PkgName = intent.getDataString().substring(8);
            // String packageName = intent.getData().getSchemeSpecificPart();
            String appName = Utils.pkgNameToAppName(context, PkgName);
            if (intent.getAction().equals(Intent.ACTION_PACKAGE_REMOVED)) {
                //EventBus.getDefault().post(new AppUpdateEvent(intent.getAction(), PkgName, null));
                DaoUtils.deleteAppInfo(context, PkgName);
                DaoUtils.destroy();
                Toast.makeText(context, "MD-removed:" + PkgName, Toast.LENGTH_SHORT).show();
                
            } else if (intent.getAction().equals(Intent.ACTION_PACKAGE_ADDED)) {
                Log.i("ttt", "AppUpdateStaticReceiver: added-- " + appName + " -- " + PkgName);
                AppInfo app = Utils.buildAppInfoEntry(context, PkgName);
                //EventBus.getDefault().post(new AppUpdateEvent(intent.getAction(),PkgName, app));

                DaoUtils.deleteAppInfo(context, PkgName);
                DaoUtils.insert(context, app);
                DaoUtils.destroy();
                
                Toast.makeText(context, "MD-added:" + appName, Toast.LENGTH_SHORT).show();
            } else if (intent.getAction().equals(Intent.ACTION_PACKAGE_CHANGED)) {
                Log.i("ttt", "AppUpdateStaticReceiver: changed-- " + PkgName);
                // DaoUtils.deleteAllAppInfo(context);
                DaoUtils.deleteAppInfo(context, PkgName);
                AppInfo app = Utils.buildAppInfoEntry(context, PkgName);
                DaoUtils.insert(context, app);
                DaoUtils.destroy();
                //EventBus.getDefault().post(new AppUpdateEvent(intent.getAction(),PkgName, app));
                Toast.makeText(context, "MD-changed:" + appName, Toast.LENGTH_SHORT).show();
            }
        }
    }

}
