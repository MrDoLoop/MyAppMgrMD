package com.doloop.www.myappmgr.material.fragments;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;

import org.apache.commons.io.comparator.LastModifiedFileComparator;

import android.app.Activity;
import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.RecyclerView.AdapterDataObserver;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.doloop.www.mayappmgr.material.events.AppBackupSuccEvent;
import com.doloop.www.myappmgr.material.adapters.BackupAppListAdapter;
import com.doloop.www.myappmgr.material.adapters.BackupAppListAdapter.BackupAppListDataSetChangedListener;
import com.doloop.www.myappmgr.material.dao.AppInfo;
import com.doloop.www.myappmgr.material.utils.ApkFileFilter;
import com.doloop.www.myappmgr.material.utils.Utilities;
import com.doloop.www.myappmgrmaterial.R;

import de.greenrobot.event.EventBus;

public class BackupAppTabFragment extends BaseFrag {
    private static BackupAppTabFragment uniqueInstance = null;
    private static Context mContext;
    private RecyclerView mRecyclerView;
    private BackupAppListAdapter mAdapter;
    private ArrayList<AppInfo> mAppList = new ArrayList<AppInfo>();
    private AdapterDataObserver mDataSetObserver;

    public BackupAppTabFragment() {

    }

    public synchronized static BackupAppTabFragment getInstance(Context ctx) {
        if (uniqueInstance == null) {
            uniqueInstance = new BackupAppTabFragment();
        }
        mContext = ctx;
        return uniqueInstance;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);
        initData();
        
        EventBus.getDefault().register(this);
    }

    public void listBackToTop(){
        mRecyclerView.smoothScrollToPosition(0);
    }
    
    private void initData(){
        mAppList.clear();
        File backupFolder = new File(Utilities.getBackUpAPKfileDir(mContext));
        if (!backupFolder.exists()) {
            backupFolder.mkdir();
        }

        File[] files = backupFolder.listFiles(new ApkFileFilter());
        PackageManager pkgMgr;
        PackageInfo packageInfo;
        if (files.length > 0) {
            Arrays.sort(files, LastModifiedFileComparator.LASTMODIFIED_REVERSE);
            for (File file : files) {
                //filesArray.add(file.getName());
                
                pkgMgr = mContext.getPackageManager();
                packageInfo = pkgMgr.getPackageArchiveInfo(file.getAbsolutePath(), 
                        PackageManager.GET_ACTIVITIES);
                AppInfo appInfo;
                if(packageInfo != null){
                    
                    ApplicationInfo applicationInfo = packageInfo.applicationInfo;
                    applicationInfo.sourceDir = file.getAbsolutePath();
                    applicationInfo.publicSourceDir = file.getAbsolutePath();
                    
                    appInfo = Utilities.buildAppInfoEntry(mContext,packageInfo,pkgMgr,false);
                    mAppList.add(appInfo);
                    
//                    ApplicationInfo appInfo = packageInfo.applicationInfo;
//                    appInfo.sourceDir = backupFolder.getAbsolutePath();
//                    appInfo.publicSourceDir = backupFolder.getAbsolutePath();
                    
                    //String appName = packageInfo.applicationInfo.loadLabel(pkgMgr).toString().trim();
                    
                   /* String appName = pkgMgr.getApplicationLabel(appInfo).toString().trim();
                    Drawable icon;
                    try {
                        icon = pkgMgr.getApplicationIcon(appInfo.packageName);
                        iconsArray.add(icon);
                    } catch (NameNotFoundException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }
                    filesArray.add(appName);*/
                }
            }
        }

        mAdapter = new BackupAppListAdapter(mContext,mAppList);
        mDataSetObserver = new AdapterDataObserver(){

            @Override
            public void onChanged() {
                // TODO Auto-generated method stub
                super.onChanged();
                mAdapter.getBackupAppListDataSetChangedListener().OnBackupAppListDataSetChanged();
            }
        };
        mAdapter.registerAdapterDataObserver(mDataSetObserver);
        mAdapter.setUserAppListDataSetChangedListener((BackupAppListDataSetChangedListener)mContext);
    }
    
    
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Create, or inflate the Fragment’s UI, and return it.
        // If this Fragment has no UI then return null.
        View FragmentView = inflater.inflate(R.layout.backup_app_list_view, container, false);
        mRecyclerView = (RecyclerView) FragmentView.findViewById(R.id.recyclerview);
        mRecyclerView.setHasFixedSize(true);
        //mRecyclerView = (RecyclerView) FragmentView.findViewById(android.R.id.list);
        LinearLayoutManager mLayoutManager = new LinearLayoutManager(getActivity());
        mLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        mRecyclerView.setLayoutManager(mLayoutManager);
        mRecyclerView.setAdapter(mAdapter);
        mRecyclerView.setItemAnimator(new DefaultItemAnimator());
        return FragmentView;
        // return null;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        //也可以试试在这里初始化数据
        /*LinearLayoutManager layoutManager = new LinearLayoutManager(
                getActivity());
         layoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
        mRecyclerView.setLayoutManager(layoutManager);

        String[] dataset = new String[100];
        for (int i = 0; i < dataset.length; i++) {
            dataset[i] = "item" + i;
        }

        RecyclerAdapter mAdapter = new RecyclerAdapter(dataset, getActivity());
        mRecyclerView.setAdapter(mAdapter);*/
        super.onViewCreated(view, savedInstanceState);
    }
    
    
    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        // Complete the Fragment initialization – particularly anything
        // that requires the parent Activity to be initialized or the
        // Fragment’s view to be fully inflated.
        setRetainInstance(false);
        setHasOptionsMenu(false);
    }

    // Called at the start of the visible lifetime.
    @Override
    public void onStart() {
        super.onStart();
        // Apply any required UI change now that the Fragment is visible.
    }

    // Called at the start of the active lifetime.
    @Override
    public void onResume() {
        super.onResume();
        // Resume any paused UI updates, threads, or processes required
    }

    // Called at the end of the active lifetime.
    @Override
    public void onPause() {
        // Suspend UI updates, threads, or CPU intensive processes
        // that don’t need to be updated when the Activity isn’t
        // the active foreground activity.
        // Persist all edits or state changes
        // as after this call the process is likely to be killed.
        super.onPause();
    }

    // Called to save UI state changes at the
    // end of the active lifecycle.
    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
        // Save UI state changes to the savedInstanceState.
        // This bundle will be passed to onCreate, onCreateView, and
        // onCreateView if the parent Activity is killed and restarted.
        // super.onSaveInstanceState(savedInstanceState);
    }

    // Called at the end of the visible lifetime.
    @Override
    public void onStop() {
        // Suspend remaining UI updates, threads, or processing
        // that aren’t required when the Fragment isn’t visible.
        super.onStop();
    }

    // Called when the Fragment’s View has been detached.
    @Override
    public void onDestroyView() {
        // Clean up resources related to the View.
        super.onDestroyView();
    }

    // Called at the end of the full lifetime.
    @Override
    public void onDestroy() {
        // Clean up any resources including ending threads,
        // closing database connections etc.
        super.onDestroy();
        mAdapter.unregisterAdapterDataObserver(mDataSetObserver);
        mContext = null;
        mRecyclerView = null;
        mAdapter = null;
        uniqueInstance = null;
        EventBus.getDefault().unregister(this);
    }

    // Called when the Fragment has been detached from its parent Activity.
    @Override
    public void onDetach() {
        super.onDetach();
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        // mContext = activity;
        // Get a reference to the parent Activity.
        /*
         * try { mListener = (OnSysAppListItemSelectedListener) activity; } catch (ClassCastException e) { throw new
         * ClassCastException(activity.toString() + "must implement Listener"); }
         */
    }
    @Override
    public String getFragmentTitle() {
        // TODO Auto-generated method stub
       
        if (mAdapter == null || mAdapter.getItemCount() == 0) { 
            return mContext.getString(R.string.backup_apps); 
        } 
        else { 
            return mContext.getString(R.string.backup_apps) + " (" + mAdapter.getItemCount() + ")"; }
    }

    public void filterList(String str){
        mAdapter.filterList(str);
        mAdapter.notifyDataSetChanged();
    }
    
    
    public void onEventMainThread(AppBackupSuccEvent ev){
        boolean found = false;
        for(int i = 0;i<mAdapter.getItemCount();i++){
            AppInfo appInfo = mAdapter.getItem(i);
            if(appInfo.packageName.equals(ev.mAppInfo.packageName)){
                found = true;
                break;
            }
        }
        if(!found) {
            AppInfo appInfo = Utilities.getLastBackupAppFromSD(mContext);
            mAdapter.getDisplayList().add(0, appInfo);
            mAdapter.notifyDataSetChanged();
        }
        
        
//        initData();
//        mRecyclerView.setAdapter(mAdapter);
//        mAdapter.getBackupAppListDataSetChangedListener().OnBackupAppListDataSetChanged();
    }
    
    
}
