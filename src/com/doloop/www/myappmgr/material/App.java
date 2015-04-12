package com.doloop.www.myappmgr.material;

import com.doloop.www.myappmgr.material.dao.DaoUtils;
import com.doloop.www.myappmgr.material.utils.Constants;
import com.doloop.www.myappmgr.material.utils.Utils;

import android.app.Application;
/**
 * APP的基类
 */
public class App extends Application {

    private static App instance = null;

    @Override
    public void onCreate() {
        super.onCreate();

        // 崩溃处理初始化
        if(!BuildConfig.DEBUG)
        {
            InitCrashHandler();
        }
        
        Constants.MY_PACKAGE_NAME = Utils.getSelfAppInfo(this).packageName;
        
    }

    @Override
    public void onTerminate() {
        // TODO Auto-generated method stub
        super.onTerminate();
        DaoUtils.destroy();
    }
    
    /**
     * 获得单实例
     * 
     * @return 单实例
     */
    public static App getInstance() {
        return instance;
    }

    private void InitCrashHandler() {
        CrashHandler handler = CrashHandler.getInstance();
        handler.init(getApplicationContext());
        Thread.setDefaultUncaughtExceptionHandler(handler);
    }
}
