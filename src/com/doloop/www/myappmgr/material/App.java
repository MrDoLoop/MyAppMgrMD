package com.doloop.www.myappmgr.material;

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
        //InitCrashHandler();
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
