package com.doloop.www.myappmgr.material.unused;

import java.util.ArrayList;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.RecyclerView.AdapterDataObserver;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.doloop.www.myappmgr.material.dao.AppInfo;
import com.doloop.www.myappmgr.material.events.AppBackupSuccEvent;
import com.doloop.www.myappmgr.material.fragments.BaseFrag;
import com.doloop.www.myappmgr.material.unused.BackupAppListAdapter.BackupAppListDataSetChangedListener;
import com.doloop.www.myappmgr.material.utils.BackupAppListLoader;
import com.doloop.www.myappmgr.material.utils.BackupAppListLoader.LoaderBckgrdIsAboutToDeliverListener;
import com.doloop.www.myappmgr.material.utils.Utils;
import com.doloop.www.myappmgr.material.R;

import de.greenrobot.event.EventBus;

public class BackupAppTabFragment extends BaseFrag implements LoaderManager.LoaderCallbacks<ArrayList<AppInfo>>,
    LoaderBckgrdIsAboutToDeliverListener {
    private static BackupAppTabFragment uniqueInstance = null;
    private static Context mContext;
    private RecyclerView mRecyclerView;
    private BackupAppListAdapter mAdapter;
    private ArrayList<AppInfo> mAppList = new ArrayList<AppInfo>();
    private AdapterDataObserver mDataSetObserver;
    private static final int LOADER_ID = 1;
    private BackupAppListLoader mBackupAppListLoader;
    private View mLoadingView;
    private ArrayList<AppInfo> mPendingNewAppInfo = new ArrayList<AppInfo>();

    @Nullable
    private View emptyView;

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
        // initData();
        EventBus.getDefault().register(this);
    }

    public void listBackToTop() {
        mRecyclerView.smoothScrollToPosition(0);
    }

    /*
     * private void initData(){ mAppList.clear(); File backupFolder = new File(Utilities.getBackUpAPKfileDir(mContext));
     * if (!backupFolder.exists()) { backupFolder.mkdir(); }
     * 
     * File[] files = backupFolder.listFiles(new ApkFileFilter()); PackageManager pkgMgr; PackageInfo packageInfo; if
     * (files.length > 0) { Arrays.sort(files, LastModifiedFileComparator.LASTMODIFIED_REVERSE); for (File file : files)
     * { //filesArray.add(file.getName());
     * 
     * pkgMgr = mContext.getPackageManager(); packageInfo = pkgMgr.getPackageArchiveInfo(file.getAbsolutePath(),
     * PackageManager.GET_ACTIVITIES); AppInfo appInfo; if(packageInfo != null){
     * 
     * ApplicationInfo applicationInfo = packageInfo.applicationInfo; applicationInfo.sourceDir =
     * file.getAbsolutePath(); applicationInfo.publicSourceDir = file.getAbsolutePath();
     * 
     * appInfo = Utilities.buildAppInfoEntry(mContext,packageInfo,pkgMgr,false); mAppList.add(appInfo); } } }
     * 
     * mAdapter = new BackupAppListAdapter(mContext,mAppList); mDataSetObserver = new AdapterDataObserver(){
     * 
     * @Override public void onChanged() { // TODO Auto-generated method stub super.onChanged(); checkIfEmpty();
     * mAdapter.getBackupAppListDataSetChangedListener().OnBackupAppListDataSetChanged(); }
     * 
     * @Override public void onItemRangeInserted(int positionStart, int itemCount) { checkIfEmpty();
     * mAdapter.getBackupAppListDataSetChangedListener().OnBackupAppListDataSetChanged(); }
     * 
     * @Override public void onItemRangeRemoved(int positionStart, int itemCount) { checkIfEmpty();
     * mAdapter.getBackupAppListDataSetChangedListener().OnBackupAppListDataSetChanged(); }
     * 
     * }; mAdapter.registerAdapterDataObserver(mDataSetObserver);
     * mAdapter.setUserAppListDataSetChangedListener((BackupAppListDataSetChangedListener)mContext); }
     */

    /*
     * public void setEmptyView(@Nullable View emptyView) { this.emptyView = emptyView; checkIfEmpty(); }
     */
    private void checkIfEmpty() {
        if (emptyView != null && mAdapter != null) {
            if (mAdapter.getItemCount() > 0) {
                emptyView.setVisibility(View.GONE);
            } else {
                emptyView.setVisibility(View.VISIBLE);
            }
            // emptyView.setVisibility(mAdapter.getItemCount() > 0 ? View.GONE : View.VISIBLE);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Create, or inflate the Fragment锟斤拷s UI, and return it.
        // If this Fragment has no UI then return null.
        View FragmentView = inflater.inflate(R.layout.backup_app_list_view, container, false);
        mLoadingView = FragmentView.findViewById(R.id.loading_bar);
        // mLoadingView.setColorSchemeColors(Color.BLACK,Color.BLUE,Color.RED);

        emptyView = FragmentView.findViewById(R.id.emptyView);
        mRecyclerView = (RecyclerView) FragmentView.findViewById(R.id.recyclerview);
        mRecyclerView.setHasFixedSize(true);
        LinearLayoutManager mLayoutManager = new LinearLayoutManager(getActivity());
        mLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        mRecyclerView.setLayoutManager(mLayoutManager);
        mRecyclerView.setItemAnimator(new DefaultItemAnimator());
        mAdapter = new BackupAppListAdapter(mContext, mAppList);
        mRecyclerView.setAdapter(mAdapter);
        // checkIfEmpty();
        return FragmentView;
        // return null;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
       
        /*
         * LinearLayoutManager layoutManager = new LinearLayoutManager( getActivity());
         * layoutManager.setOrientation(LinearLayoutManager.HORIZONTAL); mRecyclerView.setLayoutManager(layoutManager);
         * 
         * String[] dataset = new String[100]; for (int i = 0; i < dataset.length; i++) { dataset[i] = "item" + i; }
         * 
         * RecyclerAdapter mAdapter = new RecyclerAdapter(dataset, getActivity()); mRecyclerView.setAdapter(mAdapter);
         */
        super.onViewCreated(view, savedInstanceState);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        // Complete the Fragment initialization 锟紺 particularly anything
        // that requires the parent Activity to be initialized or the
        // Fragment锟斤拷s view to be fully inflated.
        setRetainInstance(false);
        setHasOptionsMenu(false);

        if (getLoaderManager().getLoader(LOADER_ID) == null) {
            getLoaderManager().initLoader(LOADER_ID, null, this);
        } else {
            getLoaderManager().restartLoader(0, null, this);
        }
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
        // that don锟斤拷t need to be updated when the Activity isn锟斤拷t
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
        // that aren锟斤拷t required when the Fragment isn锟斤拷t visible.
        super.onStop();
    }

    // Called when the Fragment锟斤拷s View has been detached.
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
        if (!mBackupAppListLoader.isLoadingRunning()) {
            mBackupAppListLoader.cancelLoad();
        }
        if (mAdapter != null && mDataSetObserver != null) {
            mAdapter.unregisterAdapterDataObserver(mDataSetObserver);
            mAdapter = null;
        }
        mBackupAppListLoader = null;
        mContext = null;
        mRecyclerView = null;
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

        if (mAdapter == null || mAdapter.getItemCount() == 1) {
            return mContext.getString(R.string.backup_apps);
        } else {
            return mContext.getString(R.string.backup_apps) + " (" + (mAdapter.getItemCount() - 1) + ")";
        }
    }

    public void filterList(String str) {
        mAdapter.filterList(str);
        // mAdapter.notifyDataSetChanged();
    }

    /*
     * private void processNewBackupApp(AppInfo theAppInfo, ArrayList<AppInfo> list) { boolean found = false; for (int i
     * = 0; i < list.size(); i++) { AppInfo appInfo = list.get(i); if
     * (appInfo.packageName.equals(theAppInfo.packageName)) { found = true; break; } } if (!found) { AppInfo appInfo =
     * Utilities.getLastBackupAppFromSD(mContext); list.add(0, appInfo); // mAdapter.getDisplayList().add(0, appInfo);
     * // mAdapter.notifyItemInserted(0); // mAdapter.notifyDataSetChanged(); } }
     */

    public void onEventMainThread(AppBackupSuccEvent ev) {

        if (!mBackupAppListLoader.isLoadingRunning()) {// loading 锟斤拷锟斤拷锟�
            
            // 锟斤拷锟斤拷欠锟斤拷锟斤拷锟绞撅拷锟絣ist锟斤拷
            for (AppInfo aInfo : ev.AppInfoList) {
                boolean found = Utils.isAppInfoInList(aInfo, mAdapter.getDisplayList()) != -1 ? true : false;
               
                if (!found) {
                    mAdapter.getDisplayList().add(1, aInfo);
                } 
            }
            mAdapter.notifyDataSetChanged();
        } else {// loading 锟斤拷没锟斤拷锟�
            for (AppInfo aInfo : ev.AppInfoList) {
                // 锟斤拷锟斤拷欠锟斤拷锟絧ending锟斤拷list锟叫达拷锟斤拷
                boolean found = Utils.isAppInfoInList(aInfo, mPendingNewAppInfo) != -1 ? true : false;
                if (!found) {
                    mPendingNewAppInfo.add(aInfo);
                }
            }
        }
        // processNewBackupApp(ev.AppInfo);
        // mAdapter.notifyItemInserted(0);
    }

    // LoaderManager.LoaderCallbacks--start
    @Override
    public Loader<ArrayList<AppInfo>> onCreateLoader(int id, Bundle args) {
        // TODO Auto-generated method stub
        return mBackupAppListLoader = new BackupAppListLoader(getActivity(), mLoadingView, mRecyclerView, this);
    }

    @Override
    public void onLoadFinished(Loader<ArrayList<AppInfo>> loader, ArrayList<AppInfo> data) {
        // TODO Auto-generated method stub
        mAppList.clear();
        mAppList = data;
        mAdapter.setDataSource(mAppList);
        //mAdapter = new BackupAppListAdapter(mContext, mAppList);
        mDataSetObserver = new AdapterDataObserver() {

            @Override
            public void onChanged() {
                // TODO Auto-generated method stub
                super.onChanged();
                checkIfEmpty();
                mAdapter.getBackupAppListDataSetChangedListener().OnBackupAppListDataSetChanged();
            }

            @Override
            public void onItemRangeInserted(int positionStart, int itemCount) {
                checkIfEmpty();
                mAdapter.getBackupAppListDataSetChangedListener().OnBackupAppListDataSetChanged();
            }

            @Override
            public void onItemRangeRemoved(int positionStart, int itemCount) {
                checkIfEmpty();
                mAdapter.getBackupAppListDataSetChangedListener().OnBackupAppListDataSetChanged();
            }

        };
        mAdapter.registerAdapterDataObserver(mDataSetObserver);
        mAdapter.setUserAppListDataSetChangedListener((BackupAppListDataSetChangedListener) mContext);
        //mRecyclerView.setAdapter(mAdapter);
        mAdapter.notifyDataSetChanged();
        checkIfEmpty();
        mAdapter.getBackupAppListDataSetChangedListener().OnBackupAppListDataSetChanged();
        //mAdapter.animateProgress(0, Utils.calculateTotalFileRawSize(mAppList), 0, Utils.getSdFreeSpaceRawSize());
        //mLoadingView.setVisibility(View.GONE);
    }

    @Override
    public void onLoaderReset(Loader<ArrayList<AppInfo>> loader) {
        // TODO Auto-generated method stub
        if (mRecyclerView != null) {
            mRecyclerView.setAdapter(null);
        }
    }

    // LoaderManager.LoaderCallbacks--end

    // LoaderBackgroundMoreWorkListener-start
    @Override
    public void onLoaderBckgrdIsAboutToDeliver(ArrayList<AppInfo> listReadyToDeliver) {
        // TODO Auto-generated method stub
        
         /*try { Thread.sleep(10000); } catch (InterruptedException e) { // TODO Auto-generated catch block
         e.printStackTrace(); }*/
         
        if (!mPendingNewAppInfo.isEmpty()) {
            for (int i = 0; i < mPendingNewAppInfo.size(); i++) {
                boolean found = Utils.isAppInfoInList(mPendingNewAppInfo.get(i), listReadyToDeliver) != -1 ? true : false;
                if (!found) {
                    listReadyToDeliver.add(0, mPendingNewAppInfo.get(i));
                }
            }
            mPendingNewAppInfo.clear();
        }
    }
    // LoaderBackgroundMoreWorkListener-end

}
