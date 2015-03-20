package com.doloop.www.myappmgr.material.fragments;

import java.io.File;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

import org.apache.commons.io.FileUtils;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.database.DataSetObserver;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.view.ActionMode;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.AnimationUtils;
import android.view.animation.Transformation;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import com.doloop.www.myappmgr.material.MainActivity;
import com.doloop.www.myappmgr.material.adapters.BackupAppListAdapterV2;
import com.doloop.www.myappmgr.material.adapters.BackupAppListAdapterV2.BackupAppListDataSetChangedListener;
import com.doloop.www.myappmgr.material.adapters.BackupAppListAdapterV2.ItemViewHolder;
import com.doloop.www.myappmgr.material.dao.AppInfo;
import com.doloop.www.myappmgr.material.events.ActionModeToggleEvent;
import com.doloop.www.myappmgr.material.events.AppBackupSuccEvent;
import com.doloop.www.myappmgr.material.events.ViewNewBackupAppEvent;
import com.doloop.www.myappmgr.material.fragments.SelectionDialogFragment.SelectionDialogClickListener;
import com.doloop.www.myappmgr.material.interfaces.IconClickListener;
import com.doloop.www.myappmgr.material.utils.BackupAppListLoader;
import com.doloop.www.myappmgr.material.utils.BackupAppListLoader.LoaderBckgrdIsAboutToDeliverListener;
import com.doloop.www.myappmgr.material.utils.Utils;
import com.doloop.www.myappmgrmaterial.R;
import com.nineoldandroids.animation.Animator;
import com.nineoldandroids.animation.Animator.AnimatorListener;
import com.nineoldandroids.animation.AnimatorListenerAdapter;
import com.nineoldandroids.animation.ValueAnimator;
import com.nineoldandroids.view.ViewHelper;
import com.nineoldandroids.view.ViewPropertyAnimator;

import de.greenrobot.event.EventBus;

public class BackupAppTabFragmentV2 extends BaseFrag implements LoaderManager.LoaderCallbacks<ArrayList<AppInfo>>,
        ListView.OnScrollListener, IconClickListener,AdapterView.OnItemLongClickListener,
        LoaderBckgrdIsAboutToDeliverListener,SelectionDialogClickListener {
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
    private boolean deleteAniIsRunning = false;
    private int currentSortType = SortTypeDialogFragment.LIST_SORT_TYPE_NAME_ASC;
    private int newBackupAppPos = -1;
    
    //private Handler mHandlder = new Handler();
                     
    

    private final class RemoveWindow implements Runnable {
        public void run() {
            removeWindow();
        }
    }

    private RemoveWindow mRemoveWindow = new RemoveWindow();
    private Handler mHandler = new Handler();
    private TextView mCentralDialogText;
    private TextView mTopDialogText;
    private boolean mShowing = false;
    private boolean mListIsScrolling = false;

    private void removeWindow() {
        if (mShowing) {
            // mShowing = false;
            // mDialogText.setVisibility(View.INVISIBLE);

            AlphaAnimation ani = new AlphaAnimation(1, 0);
            ani.setDuration(350);
            ani.setAnimationListener(new AnimationListener() {

                public void onAnimationStart(Animation animation) {
                    // TODO Auto-generated method stub

                }

                public void onAnimationRepeat(Animation animation) {
                    // TODO Auto-generated method stub

                }

                public void onAnimationEnd(Animation animation) {
                    mShowing = false;
                    hideDialogText();
                }
            });
            
            if(mTopDialogText.isShown()){
                mTopDialogText.startAnimation(ani);
            }
            else if(mCentralDialogText.isShown()){
                mCentralDialogText.startAnimation(ani);
            }
            
        }
    }    
    
    public void hideDialogText(){
        if(mTopDialogText.isShown()){
            mTopDialogText.setVisibility(View.GONE);
        }
        else if(mCentralDialogText.isShown()){
            mCentralDialogText.setVisibility(View.GONE);
        }
    }
    
    public void setListSortType(int sortType) {
        currentSortType = sortType;
        switch (currentSortType) {
            case SortTypeDialogFragment.LIST_SORT_TYPE_NAME_ASC:
            case SortTypeDialogFragment.LIST_SORT_TYPE_NAME_DES:
                mTopDialogText.setVisibility(View.GONE);
                break;
            case SortTypeDialogFragment.LIST_SORT_TYPE_SIZE_ASC:
            case SortTypeDialogFragment.LIST_SORT_TYPE_SIZE_DES:
            case SortTypeDialogFragment.LIST_SORT_TYPE_LAST_MOD_TIME_ASC:
            case SortTypeDialogFragment.LIST_SORT_TYPE_LAST_MOD_TIME_DES:
                mCentralDialogText.setVisibility(View.GONE);
                break;
        }
    }

    public int getListSortType() {
        return currentSortType;
    }
    
    public ArrayList<AppInfo> getAppList(){
        return mAppList;
    } 
    
    public void notifyDataSetChanged(){
        mAdapter.notifyDataSetChanged();
    }
    
    
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
        // Create, or inflate the Fragment锟斤拷s UI, and return it.
        // If this Fragment has no UI then return null.
        View FragmentView = inflater.inflate(R.layout.backup_app_list_view_v2, container, false);
        mLoadingView = FragmentView.findViewById(R.id.loading_bar);
        mTopDialogText = (TextView) FragmentView.findViewById(R.id.topPopTextView);
        mCentralDialogText = (TextView) FragmentView.findViewById(R.id.centralPopTextView);
        // mLoadingView.setColorSchemeColors(Color.BLACK,Color.BLUE,Color.RED);

        emptyView = FragmentView.findViewById(R.id.emptyView);
        
        mListView = (ListView) FragmentView.findViewById(android.R.id.list);
        mListView.setOnItemLongClickListener(this);
        mListView.setOnScrollListener(this);
        
        mAdapter = new BackupAppListAdapterV2(mContext, mAppList,BackupAppTabFragmentV2.this);
        mListView.setAdapter(mAdapter);

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
        mHandler.removeCallbacksAndMessages(null);
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
    }

    public void onEventMainThread(ViewNewBackupAppEvent ev) {
        if(newBackupAppPos != -1){
            mListView.setSelection(newBackupAppPos);

            
            mHandler.postDelayed(new Runnable(){
                @Override
                public void run() {
                    View itemView = mListView.getChildAt(newBackupAppPos-mListView.getFirstVisiblePosition());
                    //View iconView = itemView.findViewById(R.id.app_icon);
                    if(itemView != null){
                        Animation ani = AnimationUtils.loadAnimation(getActivity(), R.anim.shake);
                        itemView.startAnimation(ani);
                    }
                }},500);
        }
    }
    
    
    public void onEventMainThread(AppBackupSuccEvent ev) {

        if (!mBackupAppListLoader.isLoadingRunning()) {// loading 已经结束了
            for (AppInfo aInfo : ev.AppInfoList) {
                int pos = Utils.isAppInfoInList(aInfo, mAppList);
               
                if (pos == -1) {//没有找到
                    Date date = new Date();
                    aInfo.lastBackUpRawTime = date.getTime();
                    aInfo.lastBackUpTimeStr = Utils.formatTimeDisplay(date);
                    mAppList.add(aInfo);
                    Utils.sortBackUpAppList(mContext, mAppList);
                    
                    newBackupAppPos = mAppList.indexOf(aInfo);
                   
                    mAdapter.notifyDataSetChanged();
                }
                else{
                    newBackupAppPos = pos;
                }
            }
            
        } else {// loading 没有结束
            for (AppInfo aInfo : ev.AppInfoList) {
                
                int pos = Utils.isAppInfoInList(aInfo, mPendingNewAppInfo);
                if (pos == -1) {
                    Date date = new Date();
                    aInfo.lastBackUpRawTime = date.getTime();
                    aInfo.lastBackUpTimeStr = Utils.formatTimeDisplay(date);
                    mPendingNewAppInfo.add(aInfo);
                }
            }
        }
    }

    
    public void cancelLoading(){
        if(mBackupAppListLoader.isLoadingRunning()){
            mBackupAppListLoader.cancelLoad();
        }
        
    }
    
    public boolean isLoadingRunning(){
        return mBackupAppListLoader.isLoadingRunning();
    }
    
    public void forceReLoad(){
        if(mAppList != null){
            mAppList.clear();
        }
        hideDialogText();
        mAppList = new ArrayList<AppInfo>();
        mAdapter.setDataSource(mAppList);
        mAdapter.notifyDataSetChanged();
        emptyView.setVisibility(View.GONE);
        mBackupAppListLoader.forceLoad();
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
        setListSortType(Utils.getBackUpAppListSortType(mContext));
        Utils.sortBackUpAppList(mContext, mAppList);
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
    public void onLoaderBckgrdIsAboutToDeliver(ArrayList<AppInfo> listReadyToDeliver) {
        // TODO Auto-generated method stub
        
         /*try { Thread.sleep(10000); } catch (InterruptedException e) { // TODO Auto-generated catch block
         e.printStackTrace(); }*/
         
        if (!mPendingNewAppInfo.isEmpty()) {
            for (int i = 0; i < mPendingNewAppInfo.size(); i++) {
                int pos = Utils.isAppInfoInList(mPendingNewAppInfo.get(i), listReadyToDeliver);
                if (pos == -1) {
                    //listReadyToDeliver.add(0, mPendingNewAppInfo.get(i));
                    listReadyToDeliver.add(mPendingNewAppInfo.get(i));
                }
            }
            mPendingNewAppInfo.clear();
        }
    }
    // LoaderBackgroundMoreWorkListener-end

    @Override
    public void OnIconClickListener(final int position) {
        // TODO Auto-generated method stub
/*        final View view = mListView.getChildAt(position - mListView.getFirstVisiblePosition());
        if(view != null){
            //dismissRun(view, position);
            ViewPropertyAnimator.animate(view)
            .alpha(0)
            .setDuration(200)
            .setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    performDismiss(view,position);
                }
            });

            ViewPropertyAnimator.animate(view)
            .alpha(0)
            .translationY(200)
            .setDuration(200)
            .setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    mAdapter.removeItemAtPosition(position);
                    ViewHelper.setAlpha(view, 1f);
                }
            });
            
            if(deleteAniIsRunning){
                return;
            } 
            
            deleteAniIsRunning = true;
            //View iconView = view.findViewById(R.id.app_icon);
            ViewPropertyAnimator.animate(view)
            //.scaleX(0)
            //.scaleY(0)
            .translationX(view.getMeasuredWidth())
            .alpha(0)
            //.translationY(-view.getMeasuredHeight())
            .setDuration(250)
            .setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    if(android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.HONEYCOMB){
                        collapseView(view, position, mAdapter.getItem(position));
                    }
                    else{
                        //直接归位
                        ViewHelper.setAlpha(view, 1f);
                         ViewHelper.setTranslationX(view, 0);
                         FileUtils.deleteQuietly(new File(mAdapter.getItem(position).backupFilePath));
                         mAdapter.removeItemAtPosition(position);
                         deleteAniIsRunning = false;
                    }
                }
            }); 
        }*/
        
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
                                    if (mAdapter.getSelectedItemCnt() < mAdapter.getCount()) {// 选锟斤拷全锟斤拷
                                        mAdapter.selectAll();
                                    } else {// 锟斤拷锟斤拷选
                                        mAdapter.deselectAll();
                                    }
                                    updateActionModeTitle();
                                    break;
                                case R.id.menu_send:
                                    if (mAdapter.getSelectedItemCnt() == 0) {
                                        MainActivity.T(R.string.nothing_selected);
                                    } else {
                                        ArrayList<AppInfo> selectedItems = mAdapter.getSelectedItemList();
                                        ArrayList<Uri> SnedApkUris = new ArrayList<Uri>();
                                        for(AppInfo appinfo : selectedItems){
                                            SnedApkUris.add(Uri.parse("file://" + appinfo.backupFilePath));
                                        }
                                        Utils.chooseSendByApp(mContext, SnedApkUris);
                                    }
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
                                            tmpAppInfo.selected = false;
                                            if (FileUtils.deleteQuietly(new File(tmpAppInfo.backupFilePath))) 
                                            //if (new File(tmpAppInfo.backupFilePath).delete()) 
                                            {
                                                succlist.add(tmpAppInfo);
                                            }else {
                                                if(!TextUtils.isEmpty(tmpAppInfo.appName)){
                                                    MainActivity.T(tmpAppInfo.appName + "--" +mContext.getString(R.string.error));
                                                } 
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

    private void runDeleteItemAni(final View tagerView, final int position){
        if(deleteAniIsRunning){
            return;
        } 
        
        deleteAniIsRunning = true;
        //View iconView = view.findViewById(R.id.app_icon);
        ViewPropertyAnimator.animate(tagerView)
        //.scaleX(0)
        //.scaleY(0)
        .translationX(tagerView.getMeasuredWidth())
        .alpha(0)
        //.translationY(-view.getMeasuredHeight())
        .setDuration(250)
        .setListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                if(android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.HONEYCOMB){
                    collapseView(tagerView, position, mAdapter.getItem(position));
                }
                else{
                    //直接归位
                    ViewHelper.setAlpha(tagerView, 1f);
                     ViewHelper.setTranslationX(tagerView, 0);
                     FileUtils.deleteQuietly(new File(mAdapter.getItem(position).backupFilePath));
                     mAdapter.removeItemAtPosition(position);
                     deleteAniIsRunning = false;
                }
            }
        });
    }
    
    
    @Override
    public void onListItemClick(ListView l, View v, int position, long id) {
        // TODO Auto-generated method stub
        super.onListItemClick(l, v, position, id);
        if(position == 0){//header 的位置
            return ;
        }
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
        
        if(position == 0){//header 的位置
            return false;
        }
        
        if (isInActoinMode) {
            if(mAdapter.getCount() > 2){//只锟斤拷一锟斤拷锟绞憋拷锟斤拷锟绞狙★拷锟皆伙拷锟斤拷, 锟斤拷为锟斤拷锟斤拷header锟斤拷锟斤拷锟皆讹拷一锟斤拷
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
    
    
    private void collapseView(final View v, final int dismissPosition, final AppInfo appInfo) {
        
        final int initialHeight = v.getMeasuredHeight();
       
        final Animation anim = new Animation() {
            @Override
            protected void applyTransformation(float interpolatedTime, Transformation t) {
                if (interpolatedTime == 1) {
                    v.setVisibility(View.GONE);
                }
                else {
                    ViewHelper.setAlpha(v, 0f);
                    v.getLayoutParams().height = initialHeight - (int)(initialHeight * interpolatedTime);
                    v.requestLayout();
                    deleteAniIsRunning = true;
                }
            }

            @Override
            public boolean willChangeBounds() {
                return true;
            }
        };
        AnimationListener al = new AnimationListener() {
            @Override
            public void onAnimationEnd(Animation arg0) {
                ItemViewHolder vh = (ItemViewHolder)v.getTag();
                vh.needInflate = true;
                FileUtils.deleteQuietly(new File(appInfo.backupFilePath));
                mAdapter.removeItemAtPosition(dismissPosition);
                deleteAniIsRunning = false;
            }
            @Override public void onAnimationRepeat(Animation animation) {}
            @Override public void onAnimationStart(Animation animation) {
                //deleteAniIsRunning = true;
                }
        };
        //anim.setInterpolator(new AccelerateInterpolator());
        anim.setAnimationListener(al);
        anim.setDuration(200);
        v.startAnimation(anim);
    }
    
    
    private void performDismiss(final View dismissView, final int dismissPosition) {
        final ViewGroup.LayoutParams lp = dismissView.getLayoutParams();//锟斤拷取item锟侥诧拷锟街诧拷锟斤拷
        final int originalHeight = dismissView.getHeight();//item锟侥高讹拷

        ValueAnimator animator = ValueAnimator.ofInt(originalHeight, 0).setDuration(200);
        animator.start();

        animator.addListener(new AnimatorListener(){

            @Override
            public void onAnimationCancel(Animator animation) {
                // TODO Auto-generated method stub
                
            }

            @Override
            public void onAnimationEnd(Animator animation) {
                // TODO Auto-generated method stub

                //锟斤拷未锟斤拷锟斤拷锟斤拷要锟斤拷锟斤拷为锟斤拷锟角诧拷没锟叫斤拷item锟斤拷ListView锟斤拷锟狡筹拷锟斤拷锟斤拷锟角斤拷item锟侥高讹拷锟斤拷锟斤拷为0
                //锟斤拷锟斤拷锟斤拷锟斤拷锟节讹拷锟斤拷执锟斤拷锟斤拷锟街拷锟絠tem锟斤拷锟矫伙拷锟斤拷
                ViewHelper.setAlpha(dismissView, 1f);
                ViewHelper.setTranslationX(dismissView, 0);
                ViewGroup.LayoutParams lp = dismissView.getLayoutParams();
                lp.height = originalHeight;
                dismissView.setLayoutParams(lp);
                mAdapter.removeItemAtPosition(dismissPosition);
            }

            @Override
            public void onAnimationRepeat(Animator animation) {
                // TODO Auto-generated method stub
                
            }

            @Override
            public void onAnimationStart(Animator animation) {
                // TODO Auto-generated method stub
                //ViewHelper.setAlpha(dismissView, 0f);
            }});
        
        animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator valueAnimator) {
                //锟斤拷未锟斤拷锟斤拷效锟斤拷锟斤拷ListView删锟斤拷某item之锟斤拷锟斤拷锟斤拷锟斤拷item锟斤拷锟较伙拷锟斤拷锟斤拷效锟斤拷
                //ViewHelper.setAlpha(dismissView, 0f);
                lp.height = (Integer) valueAnimator.getAnimatedValue();
                dismissView.setLayoutParams(lp);
            }
        });

    }



    @Override
    public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
        // TODO Auto-generated method stub
        if (mAdapter == null)
            return;

        Log.i("ttt", "backup appList onScroll");
        Log.i("ttt", "mListIsScrolling is " + mListIsScrolling);

        if (mListIsScrolling) {
            AppInfo firstVisiableApp = mAdapter.getItem(firstVisibleItem);
            if (firstVisiableApp == null || firstVisiableApp == AppInfo.DUMMY_APPINFO)
                return;

            switch (currentSortType) {
                case SortTypeDialogFragment.LIST_SORT_TYPE_NAME_ASC:
                case SortTypeDialogFragment.LIST_SORT_TYPE_NAME_DES:
                    char firstLetter = firstVisiableApp.appName.charAt(0);
                    mCentralDialogText.setText(((Character) firstLetter).toString().toUpperCase(Locale.getDefault()));
                    mCentralDialogText.clearAnimation();
                    mCentralDialogText.setVisibility(View.VISIBLE);
                    break;
                case SortTypeDialogFragment.LIST_SORT_TYPE_SIZE_ASC:
                case SortTypeDialogFragment.LIST_SORT_TYPE_SIZE_DES:
                    mTopDialogText.setText(firstVisiableApp.appSizeStr);
                    mTopDialogText.clearAnimation();
                    mTopDialogText.setVisibility(View.VISIBLE);
                    break;
                case SortTypeDialogFragment.LIST_SORT_TYPE_LAST_MOD_TIME_ASC:
                case SortTypeDialogFragment.LIST_SORT_TYPE_LAST_MOD_TIME_DES:
                    mTopDialogText.setText(firstVisiableApp.lastBackUpTimeStr);
                    mTopDialogText.clearAnimation();
                    mTopDialogText.setVisibility(View.VISIBLE);
                    break;
            }

            mShowing = true;
            mHandler.removeCallbacks(mRemoveWindow);
            mHandler.postDelayed(mRemoveWindow, 1000);
        }
    }

    @Override
    public void onScrollStateChanged(AbsListView view, int scrollState) {
        // TODO Auto-generated method stub
        switch (scrollState) {
            case AbsListView.OnScrollListener.SCROLL_STATE_IDLE:
                mListIsScrolling = false;
                break;
            case AbsListView.OnScrollListener.SCROLL_STATE_FLING:
                mListIsScrolling = true;
                break;
            case AbsListView.OnScrollListener.SCROLL_STATE_TOUCH_SCROLL:
                mListIsScrolling = true;
                break;
        }
    }
}
