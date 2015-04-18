package com.doloop.www.myappmgr.material.utils;

import android.content.Context;

import com.squareup.picasso.LruCache;
import com.squareup.picasso.Picasso;

public class PicassoTools {
    
    private static Picasso instance;  
    private static LruCache picassoCache;
    
    public static void init(Context ctx) {
        if (instance == null) 
        {  
            Picasso.Builder builder = new Picasso.Builder(ctx);
            picassoCache = new LruCache(ctx);
            builder.memoryCache(picassoCache);
            instance = builder.build();
            //Picasso.setSingletonInstance(instance);
        }
    }
    
    
    public static synchronized Picasso getInstance() {  
        /*if (instance == null) {  
            Picasso.Builder builder = new Picasso.Builder(ctx);
            picassoCache = new LruCache(ctx);
            builder.memoryCache(picassoCache);
            instance = builder.build();
            Picasso.setSingletonInstance(instance);
        }  */
        return instance;  
    }
    
    public static void clearCache(){
        picassoCache.clear();
    }
    
    public static void destroy(){
        instance.shutdown();
        instance = null;
        picassoCache.clear();
        picassoCache = null;
    }
    
}
