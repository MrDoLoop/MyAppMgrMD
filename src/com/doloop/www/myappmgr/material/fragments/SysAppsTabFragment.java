package com.doloop.www.myappmgr.material.fragments;

import java.util.ArrayList;
import java.util.TreeMap;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.DataSetObserver;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.view.ActionMode;
import android.support.v7.widget.SearchView;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.style.ForegroundColorSpan;
import android.text.style.UnderlineSpan;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.doloop.www.myappmgr.material.AppDetailActivity;
import com.doloop.www.myappmgr.material.MainActivity;
import com.doloop.www.myappmgr.material.adapters.SysAppListAdapter;
import com.doloop.www.myappmgr.material.adapters.SysAppListAdapter.SysAppListDataSetChangedListener;
import com.doloop.www.myappmgr.material.dao.AppInfo;
import com.doloop.www.myappmgr.material.events.ActionModeToggleEvent;
import com.doloop.www.myappmgr.material.events.AppBackupSuccEvent;
import com.doloop.www.myappmgr.material.events.AppUpdateEvent;
import com.doloop.www.myappmgr.material.events.BackupAppEvent;
import com.doloop.www.myappmgr.material.events.ViewNewBackupAppEvent;
import com.doloop.www.myappmgr.material.fragments.SelectionDialogFragment.SelectionDialogClickListener;
import com.doloop.www.myappmgr.material.interfaces.IconClickListener;
import com.doloop.www.myappmgr.material.interfaces.IhoverMenuClickListener;
import com.doloop.www.myappmgr.material.interfaces.ItemMenuClickListener;
import com.doloop.www.myappmgr.material.utils.SysAppListItem;
import com.doloop.www.myappmgr.material.utils.Utils;
import com.doloop.www.myappmgr.material.widgets.IndexBarView;
import com.doloop.www.myappmgr.material.widgets.IndexBarView.OnIndexItemClickListener;
import com.doloop.www.myappmgr.material.widgets.PinnedSectionListView;
import com.doloop.www.myappmgr.material.R;
import com.nispok.snackbar.Snackbar;
import com.nispok.snackbar.listeners.ActionClickListener;

import de.greenrobot.event.EventBus;

public class SysAppsTabFragment extends BaseFrag implements AdapterView.OnItemLongClickListener, 
    IconClickListener,SelectionDialogClickListener, ItemMenuClickListener,IhoverMenuClickListener{
    private SysAppListAdapter mAdapter;
    private PinnedSectionListView mPinnedSectionListView;
    private static Context mContext;
    private TextView PopTextView;
    private MenuItem searchMenuItem;
    private DataSetObserver mDataSetObserver;
    private View mEmptyView;
    public static boolean isInActoinMode = false;
    private SelectionDialogFragment SelectionDialog;
    private Handler mHandler = new Handler();

    /*
     * private OnSysAppListItemSelectedListener mListener;
     * 
     * // Container Activity must implement this interface public interface OnSysAppListItemSelectedListener { public
     * void onSysAppItemClick(View v, int position); }
     */

    private IndexBarView mIndexBarView;

    private static SysAppsTabFragment uniqueInstance = null;

    public SysAppsTabFragment() {

    }

    public synchronized static SysAppsTabFragment getInstance(Context ctx) {
        if (uniqueInstance == null) {
            uniqueInstance = new SysAppsTabFragment();
        }
        mContext = ctx;
        return uniqueInstance;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Create, or inflate the Fragment’s UI, and return it.
        // If this Fragment has no UI then return null.
        View FragmentView = inflater.inflate(R.layout.sys_app_pinned_section_list, container, false);
        mIndexBarView = (IndexBarView) FragmentView.findViewById(R.id.indexBarView);
        mPinnedSectionListView = (PinnedSectionListView) FragmentView.findViewById(android.R.id.list);
        mEmptyView = FragmentView.findViewById(R.id.emptyView);
        
        mPinnedSectionListView.setOnItemLongClickListener(this);
        PopTextView = (TextView) FragmentView.findViewById(R.id.popTextView);
        mIndexBarView.setOnIndexItemClickListener(new OnIndexItemClickListener() {

            @Override
            public void onItemClick(String s) {

                int SecPos = mAdapter.getSectionPostionInList(s);
                if (SecPos > -1) {
                    mPinnedSectionListView.setSelection(SecPos);
                }
            }
        });
        mIndexBarView.setPopView(PopTextView);
        return FragmentView;
    }

    @Override
    public ListView getListView() {
        // TODO Auto-generated method stub
        // return super.getListView();
        return mPinnedSectionListView;
    }

    public void filterList(String str) {
        mAdapter.getFilter().filter(str);
    }

    public void notifyDataSetChanged() {
        mAdapter.notifyDataSetChanged();
    }
    
    @Override
    public ListAdapter getListAdapter() {
        // TODO Auto-generated method stub
        return mAdapter;
    }

    /*
     * public void ResetIndexBar() { PopTextView.setVisibility(View.INVISIBLE); mIndexBarView.InitIndexBar(); }
     */

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        // Complete the Fragment initialization – particularly anything
        // that requires the parent Activity to be initialized or the
        // Fragment’s view to be fully inflated.
        setRetainInstance(false);
        setHasOptionsMenu(false);
        EventBus.getDefault().register(this);
        // setEmptyText("No applications");

        // setListShown(false);
        // 监听back按钮事件
        /*
         * getView().setFocusableInTouchMode(true); getView().requestFocus(); getView().setOnKeyListener(new
         * OnKeyListener() {
         * 
         * @Override public boolean onKey(View v, int keyCode, KeyEvent event) { // TODO Auto-generated method stub if
         * (event.getAction() == KeyEvent.ACTION_DOWN) { if (keyCode == KeyEvent.KEYCODE_BACK) { if
         * (MenuItemCompat.isActionViewExpanded(searchMenuItem)) { MenuItemCompat.collapseActionView(searchMenuItem);
         * return true; } } } return false; } });
         */
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        // TODO Auto-generated method stub
        super.onCreateOptionsMenu(menu, inflater);
        // inflater.inflate(R.menu.sys_applist_menu, menu);

        searchMenuItem = menu.findItem(R.id.menu_search).setVisible(true);

        SearchView searchView = (SearchView) MenuItemCompat.getActionView(menu.findItem(R.id.menu_search));
        searchView.setQueryHint(this.getString(R.string.search));

        ((EditText) searchView.findViewById(android.support.v7.appcompat.R.id.search_src_text))
                .setHintTextColor(getResources().getColor(R.color.white));
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {

            @Override
            public boolean onQueryTextSubmit(String query) {
                // TODO Auto-generated method stub
                onQueryTextChange(query);
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                // TODO Auto-generated method stub
                String mCurFilter = !TextUtils.isEmpty(newText) ? newText : null;
                mAdapter.getFilter().filter(mCurFilter);
                return true;
            }
        });
        MenuItemCompat.setOnActionExpandListener(searchMenuItem, new MenuItemCompat.OnActionExpandListener() {

            @Override
            public boolean onMenuItemActionExpand(MenuItem item) {
                // TODO Auto-generated method stub

                return true;
            }

            @Override
            public boolean onMenuItemActionCollapse(MenuItem item) {
                // TODO Auto-generated method stub

                mAdapter.getFilter().filter(null);

                return true;
            }
        });
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
        mHandler.removeCallbacksAndMessages(null);
        EventBus.getDefault().unregister(this);
        if (mAdapter != null && mDataSetObserver != null){
            mAdapter.unregisterDataSetObserver(mDataSetObserver);
        }
        
        mAdapter = null;
        mPinnedSectionListView = null;
        mContext = null;
        uniqueInstance = null;
        isInActoinMode = false;
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

    public void setData(ArrayList<SysAppListItem> datalist, TreeMap<String, Integer> map) {
        mAdapter = new SysAppListAdapter(mContext, datalist, map, this, this, this);
        mAdapter.setSysAppListDataSetChangedListener((SysAppListDataSetChangedListener) mContext);
        mPinnedSectionListView.setAdapter(mAdapter);
        mIndexBarView.setExistIndexArray(new ArrayList<String>(map.keySet()));
        mDataSetObserver = new DataSetObserver() {

            @Override
            public void onChanged() {
                // TODO Auto-generated method stub
                super.onChanged();
                if(mAdapter.isAnyHoverShowed()) {
                    hideLastShowedHover(false, false);
                }
                mAdapter.getSysAppListDataSetChangedListener().OnSysAppListDataSetChanged(
                        mAdapter.getSysAppListWapperDisplay(), mAdapter.getSectionInListPosMapDisplay());
            }

        };
        mAdapter.registerDataSetObserver(mDataSetObserver);
        mPinnedSectionListView.setEmptyView(mEmptyView);
        mPinnedSectionListView.setOnScrollListener(new OnScrollListener(){

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
                // TODO Auto-generated method stub
//                if(mAdapter.isAnyHoverShowed()){
//                    int lastHoverPos = mAdapter.getLastHoverShowedPos();
//                    if(lastHoverPos >= mPinnedSectionListView.getFirstVisiblePosition() &&  lastHoverPos <= mPinnedSectionListView.getLastVisiblePosition()){
//                        View itemView = mPinnedSectionListView.getChildAt(lastHoverPos - mPinnedSectionListView.getFirstVisiblePosition());
//                        mAdapter.hideHover(itemView, lastHoverPos, true);
//                    }
//                    else{
//                        
//                    }
//                }
            }

            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {
                // TODO Auto-generated method stub
                MainActivity.dismissSnackbar();
                switch (scrollState) {
                    case AbsListView.OnScrollListener.SCROLL_STATE_IDLE:
                        
                        break;
                    case AbsListView.OnScrollListener.SCROLL_STATE_FLING: 
                    case AbsListView.OnScrollListener.SCROLL_STATE_TOUCH_SCROLL:
                      if(mAdapter.isAnyHoverShowed() && !mAdapter.hoverAniIsRunning()){
                          int lastHoverPos = mAdapter.getLastHoverShowedPos();
                          if(lastHoverPos >= mPinnedSectionListView.getFirstVisiblePosition() &&  lastHoverPos <= mPinnedSectionListView.getLastVisiblePosition()){
                              View itemView = mPinnedSectionListView.getChildAt(lastHoverPos - mPinnedSectionListView.getFirstVisiblePosition());
                              mAdapter.hideHover(itemView, lastHoverPos, true);
                              //mAdapter.resetHoverShowedPos();
                          }
                          else{
                              
                          }
                      }
                      break;
                }
            }});
    }

    public void hideLastShowedHover(boolean palyAni, boolean aniDirection){
        int lastHoverPos = mAdapter.getLastHoverShowedPos();
        if(lastHoverPos >= mPinnedSectionListView.getFirstVisiblePosition() &&  lastHoverPos <= mPinnedSectionListView.getLastVisiblePosition()){
            View itemView = mPinnedSectionListView.getChildAt(lastHoverPos - mPinnedSectionListView.getFirstVisiblePosition());
            mAdapter.hideHover(itemView, lastHoverPos, palyAni, aniDirection);
        }
    }
    
    
    public void resetIndexBarView() {
        mIndexBarView.reset();
    }

    public void setExistIndexArray(ArrayList<String> list) {
        mIndexBarView.setExistIndexArray(list);
    }
    
    @Override
    public void onListItemClick(ListView l, View v, int position, long id) {
        // TODO Auto-generated method stub
        super.onListItemClick(l, v, position, id);

        SysAppListItem item = mAdapter.getItem(position);
        if (item != null) {
            if (item.type == SysAppListItem.LIST_SECTION) {
                getListView().setSelection(position);
            } else if (item.type == SysAppListItem.APP_ITEM) {
                if (isInActoinMode) {
                    mAdapter.toggleSelection(position, true);
                    updateActionModeTitle();
                } else {
                    if(!mAdapter.hoverAniIsRunning()){
                        if(mAdapter.getLastHoverShowedPos() == position){
                            mAdapter.hideHover(v, position, true);
                        }
                        else{ 
                          //启动详情页的
                            AppDetailActivity.curAppInfo = item.appinfo;
                            Intent intent = new Intent(getActivity(), AppDetailActivity.class);
                            intent.putExtra(AppDetailActivity.APP_TYPE, AppDetailActivity.APP_TYPE_SYS);
                            if(Utils.playAniAppDetails(getActivity())){
                                int[] revealStartPos = Utils.findViewCenterXY(v);
                                intent.putExtra(AppDetailActivity.REVEAL_START_POSITION, revealStartPos);
                                startActivity(intent);
                                getActivity().overridePendingTransition(0, 0);
                            }
                            else{
                                Utils.startActivtyWithAni(getActivity(), intent);
                            }
                        }
                    }
                }
            }
        } else {
            MainActivity.T("SysApp Item " + position);
        }
    }

    public void updateActionModeTitle() {
        MenuItem selItem = MainActivity.sActionMode.getMenu().findItem(R.id.menu_selection);
        if (mAdapter.getSelectedItemCnt() > 0) {
            MainActivity.sActionMode.setTitle(mAdapter.getSelectedItemCnt() + " / " + mAdapter.getAppItemsCount());
            if (mAdapter.getSelectedItemCnt() == mAdapter.getAppItemsCount()) {
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

    public void listBackToTop() {
        mPinnedSectionListView.smoothScrollToPosition(0);
    }

    @Override
    public void setFragmentTitle(String title) {
        // TODO Auto-generated method stub
    }

    @Override
    public String getFragmentTitle() {
        // TODO Auto-generated method stub
        if (mAdapter == null || mAdapter.getAppItemsCount() == 0) {
            return mContext.getString(R.string.sys_apps);
        } else {
            return mContext.getString(R.string.sys_apps) + " (" + mAdapter.getAppItemsCount() + ")";
        }
    }

    // 长按
    @Override
    public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
        // TODO Auto-generated method stub
        SysAppListItem item = mAdapter.getItem(position);
        if (mAdapter.getItemViewType(position) == SysAppListItem.APP_ITEM) {
            if (isInActoinMode) {
                if(mAdapter.getCount() > 2){//只有一项的时候不显示选择对话框
                    SelectionDialog = new SelectionDialogFragment();
                    SelectionDialog.setArgs(item.appinfo, position,position==1?true:false, position == mAdapter.getCount()-1?true:false,
                            SysAppsTabFragment.this);
                    SelectionDialog.show(getActivity().getSupportFragmentManager(), SelectionDialogFragment.DialogTag);
                    return true;
                }
                return false;
            } else {
                view.findViewById(R.id.app_icon).performClick();
                return true;
            }
        } else {
            return false;
        }
    }

    // 备份app
    private void backupApp(AppInfo itemAppInfo) {
        String mBackUpFolder = Utils.getBackUpAPKfileDir(mContext);
        String sdAPKfileName = Utils.BackupApp(itemAppInfo, mBackUpFolder);
        if (sdAPKfileName != null) {
            // MainActivity.T(R.string.backup_success);
            SpannableString spanString =
                    new SpannableString(itemAppInfo.appName + " " + mContext.getString(R.string.backup_success));
            spanString.setSpan(new UnderlineSpan(), 0, itemAppInfo.appName.length(),
                    Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            spanString.setSpan(new ForegroundColorSpan(mContext.getResources().getColor(R.color.theme_blue_light)), 0,
                    itemAppInfo.appName.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);

            Snackbar mSnackbar = MainActivity.getSnackbar(false);
            boolean mAniText = false;
            boolean mShowAniSnackBar = true;
            if (mSnackbar != null) {
                if (mSnackbar.isShowing()) {
                    if (!spanString.toString().equalsIgnoreCase(mSnackbar.getText().toString())) {
                        mAniText = true;
                    }
                    mShowAniSnackBar = false;
                    mSnackbar.dismissAnimation(false);
                    mSnackbar.dismiss();
                }
                mSnackbar = MainActivity.getSnackbar(true);
            }
            mSnackbar.actionLabel(R.string.view).actionListener(new ActionClickListener() {
                @Override
                public void onActionClicked(Snackbar snackbar) {
                    EventBus.getDefault().post(new ViewNewBackupAppEvent());
                }
            }).actionColorList(mContext.getResources().getColorStateList(R.color.snackbar_action_sel))
                    .swipeToDismiss(false).showAnimation(mShowAniSnackBar).dismissAnimation(true)
                    .animationText(mAniText).duration(Snackbar.SnackbarDuration.LENGTH_SHORT)
                    .text(spanString).show(getActivity());

            EventBus.getDefault().post(new AppBackupSuccEvent(itemAppInfo));
        }
    }

    @Override
    public void OnIconClick(int position) {
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
                                    if (mAdapter.getSelectedItemCnt() < mAdapter.getAppItemsCount()) {// 选择全部
                                        mAdapter.selectAll();
                                        // menuItem.setTitle(R.string.deselect_all);
                                        // menuItem.setIcon(R.drawable.ic_action_deselect_all);
                                    } else {// 都不选
                                        mAdapter.deselectAll();
                                        // menuItem.setTitle(R.string.select_all);
                                        // menuItem.setIcon(R.drawable.ic_select_all_white);
                                    }
                                    updateActionModeTitle();
                                    break;
                                case R.id.menu_backup:
                                    if (mAdapter.getSelectedItemCnt() == 0) {
                                        MainActivity.T(R.string.nothing_selected);
                                    } else {
                                        EventBus.getDefault().post(
                                                new BackupAppEvent(mAdapter.getSelectedItemList(), false));

                                    }

                                    break;
                                case R.id.menu_send:
                                    if (mAdapter.getSelectedItemCnt() == 0) {
                                        MainActivity.T(R.string.nothing_selected);
                                    } else {
                                        Utils.chooseSendByAppWithAppList(getActivity(), mAdapter.getSelectedItemList());
                                        finishActionMode();
                                        //MainActivity.sActionMode.finish();
//                                        EventBus.getDefault().post(
//                                                new BackupAppEvent(mAdapter.getSelectedItemList(), true));

                                    }
                                    break;
                            }
                            return true;
                        }

                        @Override
                        public boolean onCreateActionMode(ActionMode mode, Menu menu) {
                            // TODO Auto-generated method stub
                            MenuInflater inflater = getActivity().getMenuInflater();
                            inflater.inflate(R.menu.sys_app_action_menu, menu);
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

    public void onEventMainThread(AppUpdateEvent ev) {
        switch (ev.mAppState) {
            case APP_ADDED:
            case APP_REMOVED:
                Utils.DismissDialog(SelectionDialog);
                break;
            default:
                break;
        }
    }

    @Override
    public void onSelectionDialogClick(DialogInterface dialog, int selectType, int curPos) {
        // TODO Auto-generated method stub
        switch (selectType) {
            case SelectionDialogFragment.SELECT_ALL_ABOVE:
                for (int i = 0; i < curPos; i++) {
                    if(mAdapter.isAppItem(i)){
                        if (!mAdapter.getItem(i).appinfo.selected) {
                            //mAdapter.getItem(i).selected = true;
                            mAdapter.setSelectedItem(i, true, false);
                        }
                    }   
                }
                break;
            case SelectionDialogFragment.DESELECT_ALL_ABOVE:
                for (int i = 0; i < curPos; i++) {
                    if(mAdapter.isAppItem(i)){
                        if (mAdapter.getItem(i).appinfo.selected) {
                            //mAdapter.getItem(i).selected = false;
                            mAdapter.setSelectedItem(i, false, false);
                        }
                    }
                }
                break;
            case SelectionDialogFragment.SELECT_ALL_BELOW:
                for (int i = curPos + 1; i < mAdapter.getCount(); i++) {
                    if(mAdapter.isAppItem(i)){
                        if (!mAdapter.getItem(i).appinfo.selected) {
                            //mAdapter.getItem(i).selected = true;
                            mAdapter.setSelectedItem(i, true, false);
                            //UserAppActionModeSelectCnt++;
                        }
                    } 
                }
                break;
            case SelectionDialogFragment.DESELECT_ALL_BELOW:
                for (int i = curPos + 1; i < mAdapter.getCount(); i++) {
                    if(mAdapter.isAppItem(i)){
                        if (mAdapter.getItem(i).appinfo.selected) {
                            mAdapter.setSelectedItem(i, false, false);
                            //mAdapter.getItem(i).selected = false;
                            //UserAppActionModeSelectCnt--;
                        }
                    }
                }
                break;
        }

        updateActionModeTitle();
        mAdapter.notifyDataSetChanged();
    }

    private void finishActionMode(){
        if(android.os.Build.VERSION.SDK_INT >= 11){
            MainActivity.sActionMode.finish();
        }
        else{//2.3系统的toolbar有bug
            //https://github.com/JakeWharton/ActionBarSherlock/issues/487
            mHandler.postDelayed(new Runnable(){

                @Override
                public void run() {
                    // TODO Auto-generated method stub
                    MainActivity.sActionMode.finish();
                }}, 500);
        }
    }

    @Override
    public void OnItemMenuClick(int position, View listItemView) {
        // TODO Auto-generated method stub
        if(!mAdapter.hoverAniIsRunning()){
            if(mAdapter.getLastHoverShowedPos() == position){
                mAdapter.hideHover(listItemView, position, true);
            }
            else{
                boolean aniDirection = true;
                if(mAdapter.isAnyHoverShowed()){
                    if(mAdapter.getLastHoverShowedPos() <= position){
                        aniDirection = false;
                    }
                    hideLastShowedHover(true, aniDirection);
                }
                mAdapter.showHover(listItemView, position, true,aniDirection);   
            }
        }
    }

    @Override
    public void OnMenuClick(int viewId,int listPos, AppInfo appInfo) {
        // TODO Auto-generated method stub
        switch (viewId){
            case R.id.launch:
                if(!Utils.launchApp(getActivity(), appInfo)){
                    MainActivity.T(R.string.launch_fail);
                }
                break;
            case R.id.details:
                Utils.showInstalledAppDetails(getActivity(), appInfo.packageName);
                break;
            case R.id.backup:
                backupApp(appInfo);
                break;
            case R.id.market:
                Utils.startMarketSearch(getActivity(), appInfo);
                break;
            case R.id.send:
                Utils.chooseSendByApp(getActivity(), Uri.parse("file://" + appInfo.apkFilePath));
                break;
                
        }
    }
    
}
