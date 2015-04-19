package com.doloop.www.myappmgr.material.utils;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import org.apache.commons.io.comparator.LastModifiedFileComparator;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.support.v4.content.AsyncTaskLoader;
import android.util.Log;
import android.view.View;

import com.doloop.www.myappmgr.material.dao.AppInfo;
import com.doloop.www.myappmgr.material.dao.DaoSession;
import com.doloop.www.myappmgr.material.dao.DaoUtils;
import com.nineoldandroids.animation.Animator;
import com.nineoldandroids.animation.AnimatorListenerAdapter;
import com.nineoldandroids.animation.ObjectAnimator;
import com.nineoldandroids.animation.ValueAnimator;
import com.nineoldandroids.animation.ValueAnimator.AnimatorUpdateListener;
import com.nineoldandroids.view.ViewHelper;

/**
 * An implementation of AsyncTaskLoader which loads a {@code List<AppEntry>} containing all installed applications on
 * the device.
 */
public class BackupAppListLoader extends AsyncTaskLoader<ArrayList<AppInfo>> {

    public LoaderBckgrdIsAboutToDeliverListener mLoaderBackgroundMoreWorkListener;
    public interface LoaderBckgrdIsAboutToDeliverListener {
        public void onLoaderBckgrdIsAboutToDeliver(ArrayList<AppInfo> listReadyToDeliver);
    }
    public void setUserAppListDataSetChangedListener(LoaderBckgrdIsAboutToDeliverListener l) {
        this.mLoaderBackgroundMoreWorkListener = l;
    }
    
    private static final String TAG = BackupAppListLoader.class.getSimpleName();
    private static final boolean DEBUG = true;

    final PackageManager pkgMgr;

    // We hold a reference to the Loader's data here.
    private ArrayList<AppInfo> mApps;
    //private Context mContext;
    private boolean mLoadInBackgroundCanceled = false;
    private View mLoadingView;
    private View mContentView;
    
    //private boolean mLoadingFinished = false;
    private boolean mLoadingRunning = false;
    

    public BackupAppListLoader(Context ctx, View loadingView, View contentView, LoaderBckgrdIsAboutToDeliverListener l) {
        // Loaders may be used across multiple Activitys (assuming they aren't
        // bound to the LoaderManager), so NEVER hold a reference to the context
        // directly. Doing so will cause you to leak an entire Activity's context.
        // The superclass constructor will store a reference to the Application
        // Context instead, and can be retrieved with a call to getContext().
        super(ctx);
        mLoaderBackgroundMoreWorkListener = l;
        mContentView = contentView;
        pkgMgr = getContext().getPackageManager();
        mLoadingView = loadingView;
    }

    public void showLoadingView(boolean withAni) {
        if (mLoadingView != null) {
            mLoadingView.setVisibility(View.VISIBLE);
            ViewHelper.setAlpha(mLoadingView, 1f);
            //mContentView.setVisibility(View.INVISIBLE);
            if(withAni){
                ObjectAnimator anim = ObjectAnimator.ofFloat(mLoadingView, "alpha", 0f, 1f).setDuration(800);
                anim.addUpdateListener(new AnimatorUpdateListener() {
                    @Override
                    public void onAnimationUpdate(ValueAnimator animation) {
                        ViewHelper.setAlpha(mContentView, 1 - (Float) animation.getAnimatedValue());
                    }
                });
                anim.start();
            }
           
            /*ObjectAnimator anim = ObjectAnimator.ofFloat(mLoadingView, "alpha", 0f, 1f);//.start();
            ObjectAnimator anim1 = ObjectAnimator.ofFloat(mContentView, "alpha", 1f, 0f);
            AnimatorSet AniSet = new AnimatorSet();
            AniSet.playTogether(anim,anim1);
            AniSet.setDuration(150);
            AniSet.start();*/
        }

    }

    public void hideLoadingView(boolean withAni) {
        if (mLoadingView != null) {
            if(withAni){
                mContentView.setVisibility(View.VISIBLE);
                ValueAnimator  anim = ValueAnimator.ofFloat(1f, 0f).setDuration(800);
                anim.addUpdateListener(new AnimatorUpdateListener() {
                    @Override
                    public void onAnimationUpdate(ValueAnimator animation) {
                        Float val = (Float) animation.getAnimatedValue();
                        //ViewHelper.setAlpha(mContentView, 1 - val);
                        ViewHelper.setAlpha(mLoadingView, val);
                    }
                });
                anim.addListener(new AnimatorListenerAdapter(){
                    public void onAnimationEnd(Animator animation){
                        mLoadingView.setVisibility(View.GONE);
                    }
                });
                anim.start();
            }
            else{
                mLoadingView.setVisibility(View.GONE);
                mContentView.setVisibility(View.VISIBLE);
            }
            /*mLoadingView.setVisibility(View.GONE);
            mContentView.setVisibility(View.VISIBLE);
            ObjectAnimator anim = ObjectAnimator.ofFloat(mLoadingView, "alpha", 1f, 0f);//.start();
            ObjectAnimator anim1 = ObjectAnimator.ofFloat(mContentView, "alpha", 0f, 1f);
            AnimatorSet AniSet = new AnimatorSet();
            AniSet.playTogether(anim,anim1);
            AniSet.setDuration(1000);
            AniSet.start();*/
        }
    }

    public boolean isLoadingRunning(){
        return mLoadingRunning;
    }
    
    /****************************************************/
    /** (1) A task that performs the asynchronous load **/
    /****************************************************/

    /**
     * This method is called on a background thread and generates a List of {@link AppEntry} objects. Each entry
     * corresponds to a single installed application on the device.
     */
    @Override
    public ArrayList<AppInfo> loadInBackground() {
        //long startTime = System.currentTimeMillis();
        //后台加载
        /*try {
            Thread.sleep(20000);
        } catch (InterruptedException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }*/
        mLoadingRunning = true;
        mLoadInBackgroundCanceled = false;
        
        if (DEBUG)
            Log.i(TAG, "+++ loadInBackground() called! +++");

        File backupFolder = new File(Utils.getBackUpAPKfileDir(getContext()));
        if (!backupFolder.exists()) {
            backupFolder.mkdir();
        }

        File[] files = backupFolder.listFiles(new ApkFileFilter());
        ArrayList<AppInfo> entries;
        if(files == null){
            entries = new ArrayList<AppInfo>();
        }
        else{
            entries = new ArrayList<AppInfo>(files.length);
            // PackageManager pkgMgr;
            PackageInfo packageInfo;
            if (files.length > 0) {
                Arrays.sort(files, LastModifiedFileComparator.LASTMODIFIED_REVERSE);
                DaoSession appInfoSession = DaoUtils.getDaoSession(getContext(), false);
                ArrayList<AppInfo> mBaseList = (ArrayList<AppInfo>) appInfoSession.getAppInfoDao().loadAll();
                SimpleDateFormat simpleDateFormat = Utils.getLocalDataDigitalDisplayFormat();
                for (File file : files) {

                    packageInfo = pkgMgr.getPackageArchiveInfo(file.getAbsolutePath(), PackageManager.GET_ACTIVITIES);
                    AppInfo appInfo;
                    if (packageInfo != null) {

                        ApplicationInfo applicationInfo = packageInfo.applicationInfo;
                        //以下2个属性赋值之后才能正确读出东西
                        applicationInfo.sourceDir = file.getAbsolutePath();
                        applicationInfo.publicSourceDir = file.getAbsolutePath();
                        if (mLoadInBackgroundCanceled) {
                            entries.clear();
                            entries = new ArrayList<AppInfo>(files.length);
                            mBaseList.clear();
                            appInfoSession.clear();
                            break;
                        }
                        
                        int appPos = Utils.isAppInfoInList(packageInfo, mBaseList);
                        if(appPos != -1){
                            appInfo = mBaseList.get(appPos);
                        }
                        else{
                            appInfo = Utils.buildAppInfoEntry(getContext(), packageInfo, pkgMgr, false, false);
                        }
                        //appInfo = Utils.buildAppInfoEntry(getContext(), packageInfo, pkgMgr, false, false);
                        appInfo.apkFilePath = file.getAbsolutePath();
                        appInfo.backupFilePath = file.getAbsolutePath();
                        //加载本地文件之后,属性和本地文件的apk保持一致
                        appInfo.lastBackUpRawTime = file.lastModified();
                        appInfo.lastBackUpTimeStr = simpleDateFormat.format(new Date(file.lastModified()));  
                        appInfo.lastModifiedRawTime = file.lastModified();
                        appInfo.lastModifiedTimeStr = simpleDateFormat.format(new Date(file.lastModified()));   
                        entries.add(appInfo);
                        
                        /*if(!Utils.isAppIconOnSd(getContext(),appInfo)){
                            appInfo.iconBitmap = Utils.drawableToBitmap(packageInfo.applicationInfo.loadIcon(pkgMgr));
                            Utils.saveAppIconOnSd(getContext(),appInfo);
                        }
                        appInfo.iconBitmap = null;*/
                    }
                }
                mBaseList.clear();
                appInfoSession.clear();
            }
        }
       
        if(!mLoadInBackgroundCanceled && mLoaderBackgroundMoreWorkListener != null){
            mLoaderBackgroundMoreWorkListener.onLoaderBckgrdIsAboutToDeliver(entries);
        }
        //long endTime = System.currentTimeMillis();
        //L.d("load time: "+ (endTime - startTime));
        return entries;
    }

    /*******************************************/
    /** (2) Deliver the results to the client **/
    /*******************************************/

    /**
     * Called when there is new data to deliver to the client. The superclass will deliver it to the registered listener
     * (i.e. the LoaderManager), which will forward the results to the client through a call to onLoadFinished.
     */
    @Override
    public void deliverResult(ArrayList<AppInfo> apps) {
        if(mLoadingRunning){
            hideLoadingView(true);
        }
        
        mLoadingRunning = false;
        if (isReset()) {
            if (DEBUG)
                Log.w(TAG, "+++ Warning! An async query came in while the Loader was reset! +++");
            // The Loader has been reset; ignore the result and invalidate the data.
            // This can happen when the Loader is reset while an asynchronous query
            // is working in the background. That is, when the background thread
            // finishes its work and attempts to deliver the results to the client,
            // it will see here that the Loader has been reset and discard any
            // resources associated with the new data as necessary.
            if (apps != null) {
                releaseResources(apps);
                return;
            }
        }

        // Hold a reference to the old data so it doesn't get garbage collected.
        // We must protect it until the new data has been delivered.
        List<AppInfo> oldApps = mApps;
        mApps = apps;

        if (isStarted()) {
            if (DEBUG)
                Log.i(TAG, "+++ Delivering results to the LoaderManager for" + " the ListFragment to display! +++");
            // If the Loader is in a started state, have the superclass deliver the
            // results to the client.
            super.deliverResult(apps);
        }

        // Invalidate the old data as we don't need it any more.
        if (oldApps != null && oldApps != apps) {
            if (DEBUG)
                Log.i(TAG, "+++ Releasing any old data associated with this Loader. +++");
            releaseResources(oldApps);
        }
    }

    /*********************************************************/
    /** (3) Implement the Loader锟絪 state-dependent behavior **/
    /*********************************************************/

    @Override
    protected void onStartLoading() {
        
        if(mLoadingRunning){
            return ;
        }
        
        if (DEBUG)
            Log.i(TAG, "+++ onStartLoading() called! +++");
        
        if (mApps != null && !mLoadInBackgroundCanceled) {
            // Deliver any previously loaded data immediately.
            if (DEBUG)
                Log.i(TAG, "+++ Delivering previously loaded data to the client...");
            deliverResult(mApps);
        }

        // Register the observers that will notify the Loader when changes are made.
        /*
         * if (mAppsObserver == null) { mAppsObserver = new InstalledAppsObserver(this); }
         * 
         * if (mLocaleObserver == null) { mLocaleObserver = new SystemLocaleObserver(this); }
         */

        if (takeContentChanged()) {
            // When the observer detects a new installed application, it will call
            // onContentChanged() on the Loader, which will cause the next call to
            // takeContentChanged() to return true. If this is ever the case (or if
            // the current data is null), we force a new load.
            mLoadInBackgroundCanceled = false;
            if (DEBUG)
                Log.i(TAG, "+++ A content change has been detected... so force load! +++");
            showLoadingView(false);
            forceLoad();
        } else if (mApps == null) {
            // If the current data is null... then we should make it non-null! :)
            if (DEBUG)
                Log.i(TAG, "+++ The current data is data is null... so force load! +++");
            showLoadingView(false);
            forceLoad();
        }
    }

    @Override
    protected void onStopLoading() {
        //mLoadingRunning = false;
        if (DEBUG)
            Log.i(TAG, "+++ onStopLoading() called! +++");

        // The Loader has been put in a stopped state, so we should attempt to
        // cancel the current load (if there is one).
        //hideLoadingView();
        //cancelLoad();

        // Note that we leave the observer as is; Loaders in a stopped state
        // should still monitor the data source for changes so that the Loader
        // will know to force a new load if it is ever started again.
    }

    @Override
    public boolean cancelLoad() {
        // TODO Auto-generated method stub
        mLoadInBackgroundCanceled = true;
        mLoadingRunning = false;
        return super.cancelLoad();
    }

    @Override
    protected void onReset() {
        mLoadingRunning = false;
        if (DEBUG)
            Log.i(TAG, "+++ onReset() called! +++");

        // Ensure the loader is stopped.
        onStopLoading();

        // At this point we can release the resources associated with 'apps'.
        if (mApps != null) {
            releaseResources(mApps);
            mApps = null;
        }

        // The Loader is being reset, so we should stop monitoring for changes.
        /*
         * if (mAppsObserver != null) { getContext().unregisterReceiver(mAppsObserver); mAppsObserver = null; }
         * 
         * if (mLocaleObserver != null) { getContext().unregisterReceiver(mLocaleObserver); mLocaleObserver = null; }
         */
    }

    @Override
    public void onCanceled(ArrayList<AppInfo> apps) {
        mLoadingRunning = false;
        if (DEBUG)
            Log.i(TAG, "+++ onCanceled() called! +++");

        // Attempt to cancel the current asynchronous load.
        super.onCanceled(apps);

        // The load has been canceled, so we should release the resources
        // associated with 'mApps'.
        hideLoadingView(false);
        releaseResources(apps);
    }

    @Override
    public void forceLoad() {
        showLoadingView(false);
        if (DEBUG)
            Log.i(TAG, "+++ forceLoad() called! +++");
        super.forceLoad();
    }

    /**
     * Helper method to take care of releasing resources associated with an actively loaded data set.
     */
    private void releaseResources(List<AppInfo> apps) {
        // For a simple List, there is nothing to do. For something like a Cursor,
        // we would close it in this method. All resources associated with the
        // Loader should be released here.
        apps.clear();
        System.gc();
        mLoadingRunning = false;
    }

    /*********************************************************************/
    /** (4) Observer which receives notifications when the data changes **/
    /*********************************************************************/

    // An observer to notify the Loader when new apps are installed/updated.
    // private InstalledAppsObserver mAppsObserver;

    // The observer to notify the Loader when the system Locale has been changed.
    // private SystemLocaleObserver mLocaleObserver;

    /**************************/
    /** (5) Everything else! **/
    /**************************/

    /**
     * Performs alphabetical comparison of {@link AppEntry} objects. This is used to sort queried data in
     * {@link loadInBackground}.
     */
    /*
     * private static final Comparator<AppEntry> ALPHA_COMPARATOR = new Comparator<AppEntry>() { Collator sCollator =
     * Collator.getInstance();
     * 
     * @Override public int compare(AppEntry object1, AppEntry object2) { return sCollator.compare(object1.getLabel(),
     * object2.getLabel()); } };
     */
}
