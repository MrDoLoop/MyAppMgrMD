package com.doloop.www.myappmgr.material.dao;

import android.content.Context;

import com.doloop.www.myappmgr.material.dao.AppInfoDao.Properties;
import com.doloop.www.myappmgr.material.dao.DaoMaster.OpenHelper;
import com.doloop.www.myappmgr.material.utils.Utils;

import de.greenrobot.dao.query.DeleteQuery;
import de.greenrobot.dao.query.QueryBuilder;

public class DaoUtils {
    private static DaoMaster daoMaster;
    private static DaoSession daoSession;

    /**
     * 取得DaoMaster
     *
     * @param context
     * @return
     */
    public static DaoMaster getDaoMaster(Context context) {
        if (daoMaster == null) {
            OpenHelper helper = new DaoMaster.DevOpenHelper(context, "appinfo-db", null);
            daoMaster = new DaoMaster(helper.getWritableDatabase());
        }
        return daoMaster;
    }

    /**
     * 取得DaoSession
     *
     * @param context
     * @return
     */
    public static DaoSession getDaoSession(Context context, boolean newSession) {
        if (newSession) {
            if (daoSession != null) {
                daoSession.clear();
            }
            daoSession = null;
        }

        if (daoSession == null) {
            if (daoMaster == null) {
                daoMaster = getDaoMaster(context);
            }

            daoSession = daoMaster.newSession();
        }
        return daoSession;
    }

    public static AppInfo getByPackageName(Context context, String pName) {
        return getDaoSession(context, false).getAppInfoDao().queryBuilder()
                .where(AppInfoDao.Properties.PackageName.eq(pName)).build().unique();
    }

    public static void insert(Context context, AppInfo appInfo) {
        getDaoSession(context, false).getAppInfoDao().insert(appInfo);
    }

    public static void destroy() {
        if (daoSession != null) {
            daoSession.clear();
            daoSession.getDatabase().close();
        }
        daoMaster = null;
        daoSession = null;
    }

    // public static void updateAppInfo(Context context, AppInfo appInfo){
    // getDaoSession(context).getAppInfoDao().update(appInfo);
    // }

    public static void deleteAppInfo(Context context, AppInfo appInfo) {
        Utils.deleteAppIconInCache(context, appInfo);
        getDaoSession(context, false).getAppInfoDao().deleteByKey(appInfo.id);
    }

    public static void deleteAppInfo(Context context, String pkName) {
        QueryBuilder<AppInfo> qb = getDaoSession(context, false).getAppInfoDao().queryBuilder();
        AppInfo theApp = qb.where(Properties.PackageName.eq(pkName)).unique();
        if(theApp != null){
            Utils.deleteAppIconInCache(context, theApp);
        }
        DeleteQuery<AppInfo> bd = qb.where(Properties.PackageName.eq(pkName)).buildDelete();
        bd.executeDeleteWithoutDetachingEntities();
    }

    public static void deleteAllAppInfo(Context context) {
        Utils.setAppListInDb(context, false);
        Utils.deleteAppIconDir(Utils.getAppIconCacheDir(context));
        getDaoSession(context, false).getAppInfoDao().deleteAll();
        if (daoSession != null) {
            daoSession.clear();
        }
        daoMaster = null;
        daoSession = null;
    }

}
