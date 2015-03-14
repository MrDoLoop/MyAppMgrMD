package com.doloop.www.myappmgr.material;

import com.doloop.www.myappmgr.material.utils.Constants;
import com.doloop.www.myappmgr.material.utils.Utils;
import com.nostra13.universalimageloader.cache.disc.naming.Md5FileNameGenerator;
import com.nostra13.universalimageloader.cache.memory.impl.UsingFreqLimitedMemoryCache;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.QueueProcessingType;
import com.nostra13.universalimageloader.core.download.BaseImageDownloader;

import android.app.Application;
import android.content.Context;

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
        
        Constants.MY_PACKAGE_NAME = Utils.getSelfAppInfo(this).packageName;
        initImageLoader(this);
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

    private void initImageLoader(Context context) {
        // This configuration tuning is custom. You can tune every option, you may tune some of them,
        // or you can create default configuration by
        //  ImageLoaderConfiguration.createDefault(this);
        // method.
        ImageLoaderConfiguration config = new ImageLoaderConfiguration  
            .Builder(context)  
            // max width, max height锛屽嵆淇濆瓨鐨勬瘡涓紦瀛樻枃浠剁殑锟�?澶ч暱锟�?  
            .memoryCacheExtraOptions(480, 800) 
            .threadPoolSize(3)//绾跨▼姹犲唴鍔犺浇鐨勬暟锟�?  
            .threadPriority(Thread.NORM_PRIORITY - 2)  
            .denyCacheImageMultipleSizesInMemory()
            // You can pass your own memory cache implementation/浣犲彲浠ワ拷?锟借繃鑷繁鐨勫唴瀛樼紦瀛樺疄锟�?
            .memoryCache(new UsingFreqLimitedMemoryCache(2 * 1024 * 1024))
            .memoryCacheSize(2 * 1024 * 1024)
            .diskCacheFileNameGenerator(new Md5FileNameGenerator())
            .tasksProcessingOrder(QueueProcessingType.FIFO)  
            .defaultDisplayImageOptions(DisplayImageOptions.createSimple())  
            // connectTimeout (5 s), readTimeout (30 s)瓒呮椂鏃堕棿  
            .imageDownloader(new BaseImageDownloader(context, 5 * 1000, 30 * 1000)) 
            //.writeDebugLogs() // Remove for release app  
            .build();//锟�?濮嬫瀯锟�?  
            // Initialize ImageLoader with configuration.
        ImageLoader.getInstance().init(config);
    }
}
