package com.doloop.www.myappmgr.material.fragments;

import java.io.File;
import java.util.ArrayList;

import org.apache.commons.io.FileUtils;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.database.DataSetObserver;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.view.ActionMode;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import com.doloop.www.mayappmgr.material.events.ActionModeToggleEvent;
import com.doloop.www.mayappmgr.material.events.AppBackupSuccEvent;
import com.doloop.www.myappmgr.material.MainActivity;
import com.doloop.www.myappmgr.material.adapters.BackupAppListAdapterV2.BackupAppListDataSetChangedListener;
import com.doloop.www.myappmgr.material.adapters.BackupAppListAdapterV2;
import com.doloop.www.myappmgr.material.dao.AppInfo;
import com.doloop.www.myappmgr.material.fragments.SelectionDialogFragment.SelectionDialogClickListener;
import com.doloop.www.myappmgr.material.interfaces.IconClickListener;
import com.doloop.www.myappmgr.material.utils.BackupAppListLoader;
import com.doloop.www.myappmgr.material.utils.BackupAppListLoader.LoaderBackgroundMoreWorkListener;
import com.doloop.www.myappmgr.material.utils.Utils;
import com.doloop.www.myappmgrmaterial.R;

import de.greenrobot.event.EventBus;

public class BackupAppTabFragmentV2 extends BaseFrag implements LoaderManager.LoaderCallbacks<ArrayList<AppInfo>>,
        LoaderBackgroundMoreWorkListener,AdapterView.OnItemLongClickListener, IconClickListener,SelectionDialogClickListener {
    private static BackupAppTabFragmentV2 uniqueInstance = null;
    private static Context mContext;
    private ListView mListView;
    private BackupAppListAdapterV2 mAdapter;
    private ArrayList<AppInfo> mAppList = new ArrayList<AppInfo>();
    private DataSetObserver mDataSetObserver;
    private static final int LOADER_ID = 1;
    private BackupAppListLoader mBackupAppListLoader;
    private View mLoadingView;
    private ArrayList<AppInfo> mPendingNewAppInfo = new ArrayList<AppInfo>();
    public static boolean isInActoinMode = false;

    @Nullable
    private View emptyView;

    public synchronized static BackupAppTabFragmentV2 getInstance(Context ctx) {
        if (uniqueInstance == null) {
            uniqueInstance = new BackupAppTabFragmentV2();
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
        mListView.smoothScrollToPosition(0);
    }

    private void checkIfEmpty() {
        if (emptyView != null && mAdapter != null) {
            if (mAdapter.getAppItemCount() > 0) {
                emptyView.setVisibility(View.GONE);
            } else {
                emptyView.setVisibility(View.VISIBLE);
            }
            // emptyView.setVisibility(mAdapter.getItemCount() > 0 ? View.GONE : View.VISIBLE);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Create, or inflate the Fragment’s UI, and return it.
        // If this Fragment has no UI then return null.
        View FragmentView = inflater.inflate(R.layout.backup_app_list_view_v2, container, false);
        mLoadingView = FragmentView.findViewById(R.id.loading_bar);
        // mLoadingView.setColorSchemeColors(Color.BLACK,Color.BLUE,Color.RED);

        emptyView = FragmentView.findViewById(R.id.emptyView);
        
        mListView = (ListView) FragmentView.findViewById(android.R.id.list);
        mListView.setOnItemLongClickListener(this);
        
        mAdapter = new BackupAppListAdapterV2(mContext, mAppList,BackupAppTabFragmentV2.this);
        mListView.setAdapter(mAdapter);
        // checkIfEmpty();
        return FragmentView;
        // return null;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        // 也可以试试在这里初始化数据
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
        // Complete the Fragment initialization – particularly anything
        // that requires the parent Activity to be initialized or the
        // Fragment’s view to be fully inflated.
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
        if (!mBackupAppListLoader.isLoadingRunning()) {
            mBackupAppListLoader.cancelLoad();
        }
        if (mAdapter != null && mDataSetObserver != null) {
            mAdapter.unregisterDataSetObserver(mDataSetObserver);
            mAdapter = null;
        }
        mBackupAppListLoader = null;
        mContext = null;
        mListView = null;
        uniqueInstance = null;
        isInActoinMode = false;
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
        
    }

    @Override
    public String getFragmentTitle() {
        // TODO Auto-generated method stub

        if (mAdapter == null || mAdapter.getAppItemCount() == 0) {
            return mContext.getString(R.string.backup_apps);
        } else {
            return mContext.getString(R.string.backup_apps) + " (" + mAdapter.getAppItemCount() + ")";
        }
    }

    public void filterList(String str) {
        mAdapter.filterList(str);
        // mAdapter.notifyDataSetChanged();
    }

    public void onEventMainThread(AppBackupSuccEvent ev) {

        if (!mBackupAppListLoader.isLoadingRunning()) {// loading 完成了
            
            // 检查是否在显示的list中
            for (AppInfo aInfo : ev.AppInfoList) {
                boolean found = Utils.isAppInfoInList(aInfo, mAdapter.getDisplayList());
               /* for (int i = 0, size = mAdapter.getDisplayList().size(); i < size; i++) {
                    AppInfo appInfo = mAdapter.getDisplayList().get(i);
                    if (aInfo.packageName.equals(appInfo.packageName)) {
                        found = true;
                        break;
                    }
                }*/
                if (!found) {
                    mAdapter.getDisplayList().add(1, aInfo);
                } 
            }
            mAdapter.notifyDataSetChanged();
        } else {// loading 还没完成
            for (AppInfo aInfo : ev.AppInfoList) {
                // 检查是否在pending的list中存在
                boolean found = Utils.isAppInfoInList(aInfo, mPendingNewAppInfo);
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
        return mBackupAppListLoader = new BackupAppListLoader(getActivity(), mLoadingView, mListView, this);
    }

    @Override
    public void onLoadFinished(Loader<ArrayList<AppInfo>> loader, ArrayList<AppInfo> data) {
        // TODO Auto-generated method stub
        mAppList.clear();
        mAppList = data;
        mAdapter.setDataSource(mAppList);
        //mAdapter = new BackupAppListAdapter(mContext, mAppList);
        mDataSetObserver = new DataSetObserver() {

            @Override
            public void onChanged() {
                // TODO Auto-generated method stub
                super.onChanged();
                checkIfEmpty();
                mAdapter.getBackupAppListDataSetChangedListener().OnBackupAppListDataSetChanged();
            }
        };
        mAdapter.registerDataSetObserver(mDataSetObserver);
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
        if (mListView != null) {
            mListView.setAdapter(null);
        }
    }

    // LoaderManager.LoaderCallbacks--end

    // LoaderBackgroundMoreWorkListener-start
    @Override
    public void onLoaderBackgroundMoreWork(ArrayList<AppInfo> listReadyToDeliver) {
        // TODO Auto-generated method stub
        
         /*try { Thread.sleep(10000); } catch (InterruptedException e) { // TODO Auto-generated catch block
         e.printStackTrace(); }*/
         
        if (!mPendingNewAppInfo.isEmpty()) {
            for (int i = 0; i < mPendingNewAppInfo.size(); i++) {
                boolean found = Utils.isAppInfoInList(mPendingNewAppInfo.get(i), listReadyToDeliver);
                if (!found) {
                    listReadyToDeliver.add(0, mPendingNewAppInfo.get(i));
                }
            }
            mPendingNewAppInfo.clear();
        }
    }
    // LoaderBackgroundMoreWorkListener-end

    @Override
    public void OnIconClickListener(int position) {
        // TODO Auto-generated method stub
        if (isInActoinMode) {
            // mAdapter.toggleSelection(position,true);
            // updateActionModeTitle();
        } else {
            MainActivity.sActionMode =
                    ((ActionBarActivity) getActivity()).startSupportActionMode(new ActionMode.Callback() {

                        @Override
                        public boolean onActionItemClicked(ActionMode mode, MenuItem menuItem) {
                            // TODO Auto-generated method stub
                            switch (menuItem.getItemId()) {
                                case R.id.menu_selection:
                                    if (mAdapter.getSelectedItemCnt() < mAdapter.getCount()) {// 选择全部
                                        mAdapter.selectAll();
                                    } else {// 都不选
                                        mAdapter.deselectAll();
                                    }
                                    updateActionModeTitle();
                                    break;
                                case R.id.menu_delete:
                                    if (mAdapter.getSelectedItemCnt() == 0) {
                                        MainActivity.T(R.string.nothing_selected);
                                    } else {
                                        AppInfo tmpAppInfo = null;
                                        ArrayList<AppInfo> list = mAdapter.getSelectedItemList();
                                        ArrayList<AppInfo> succlist = new ArrayList<AppInfo>(); 
                                        for (int i = 0; i < list.size(); i++) {
                                            tmpAppInfo = list.get(i);

                                            if (FileUtils.deleteQuietly(new File(tmpAppInfo.backupFilePath))) {
                                                succlist.add(tmpAppInfo);
                                            }else {
                                                MainActivity.T(tmpAppInfo.appName + "--" +mContext.getString(R.string.error));
                                            }
                                        }
                                        mAdapter.removeItem(succlist);
                                        MainActivity.sActionMode.finish();
                                    }

                                    break;
                            }
                            return true;
                        }

                        @Override
                        public boolean onCreateActionMode(ActionMode mode, Menu menu) {
                            // TODO Auto-generated method stub
                            MenuInflater inflater = getActivity().getMenuInflater();
                            inflater.inflate(R.menu.backup_app_action_menu, menu);
                            isInActoinMode = true;
                            EventBus.getDefault().post(new ActionModeToggleEvent(true));
                            return true;
                        }

                        @Override
                        public void onDestroyActionMode(ActionMode mode) {
                            // TODO Auto-generated method stub
                            mAdapter.deselectAll();
                            MainActivity.sActionMode = null;
                            isInActoinMode = false;
                            EventBus.getDefault().post(new ActionModeToggleEvent(false));
                        }

                        @Override
                        public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
                            // TODO Auto-generated method stub
                            return false;
                        }
                    });

            mAdapter.setSelectedItem(position, true, true);
            updateActionModeTitle();
        }
    }

    @Override
    public void onListItemClick(ListView l, View v, int position, long id) {
        // TODO Auto-generated method stub
        super.onListItemClick(l, v, position, id);

        AppInfo item = mAdapter.getItem(position);
        if (item != null) {
            if (isInActoinMode) {
                mAdapter.toggleSelection(position, true);
                updateActionModeTitle();
            } else {
                if (TextUtils.isEmpty(item.backupFilePath)) {
                    Utils.installAPK(getActivity(), item.apkFilePath);
                } else {
                    Utils.installAPK(getActivity(), item.backupFilePath);
                }
            }
        } else {
            MainActivity.T("BackupApp Item " + position);
        }
    }
    
    @Override
    public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
        // TODO Auto-generated method stub
        if (isInActoinMode) {
            if(mAdapter.getCount() > 2){//只有一项的时候不显示选择对话框, 因为有了header，所以多一项
                SelectionDialogFragment SelectionDialog = new SelectionDialogFragment();
                SelectionDialog.setArgs(mAdapter.getItem(position), position, position == 1 ? true:false, position == mAdapter.getAppItemCount() ? true:false, BackupAppTabFragmentV2.this);
                SelectionDialog.show(getActivity().getSupportFragmentManager(), SelectionDialogFragment.DialogTag);
                return true;
            } 
            return false;
        } else {
            view.findViewById(R.id.app_icon).performClick();
            return true;
        }
    }

    
    public void updateActionModeTitle() {
        MenuItem selItem = MainActivity.sActionMode.getMenu().findItem(R.id.menu_selection);
        if (mAdapter.getSelectedItemCnt() > 0) {
            MainActivity.sActionMode.setTitle(mAdapter.getSelectedItemCnt() + " / " + mAdapter.getCount());
            if (mAdapter.getSelectedItemCnt() == mAdapter.getCount()) {
                selItem.setTitle(R.string.deselect_all);
                selItem.setIcon(R.drawable.ic_deselect_all_white);
            } else {
                selItem.setTitle(R.string.select_all);
                selItem.setIcon(R.drawable.ic_select_all_white);
            }
        } else {
            MainActivity.sActionMode.setTitle("");
            selItem.setTitle(R.string.select_all);
            selItem.setIcon(R.drawable.ic_select_all_white);
        }
    }

    @Override
    public void onSelectionDialogClick(DialogInterface dialog, int selectType, int curPos) {
        // TODO Auto-generated method stub
        switch (selectType) {
            case SelectionDialogFragment.SELECT_ALL_ABOVE:
                for (int i = 0; i < curPos; i++) {
                    if (!mAdapter.getItem(i).selected) {
                        //mAdapter.getItem(i).selected = true;
                        mAdapter.setSelectedItem(i, true, false);
                    }
                }
                break;
            case SelectionDialogFragment.DESELECT_ALL_ABOVE:
                for (int i = 0; i < curPos; i++) {
                    if (mAdapter.getItem(i).selected) {
                        //mAdapter.getItem(i).selected = false;
                        mAdapter.setSelectedItem(i, false, false);
                    }
                }
                break;
            case SelectionDialogFragment.SELECT_ALL_BELOW:
                for (int i = curPos + 1; i < mAdapter.getCount(); i++) {
                    if (!mAdapter.getItem(i).selected) {
                        //mAdapter.getItem(i).selected = true;
                        mAdapter.setSelectedItem(i, true, false);
                        //UserAppActionModeSelectCnt++;
                    }
                }
                break;
            case SelectionDialogFragment.DESELECT_ALL_BELOW:
                for (int i = curPos + 1; i < mAdapter.getCount(); i++) {
                    if (mAdapter.getItem(i).selected) {
                        mAdapter.setSelectedItem(i, false, false);
                        //mAdapter.getItem(i).selected = false;
                        //UserAppActionModeSelectCnt--;
                    }
                }
                break;
        }

        updateActionModeTitle();
        mAdapter.notifyDataSetChanged();
    }
    
}
