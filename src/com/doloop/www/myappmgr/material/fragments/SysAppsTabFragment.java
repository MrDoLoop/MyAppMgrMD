package com.doloop.www.myappmgr.material.fragments;

import java.util.ArrayList;
import java.util.TreeMap;

import android.app.Activity;
import android.content.Context;
import android.database.DataSetObserver;
import android.os.Bundle;
import android.support.v4.view.MenuItemCompat;
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
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.doloop.www.mayappmgr.material.events.AppBackupSuccEvent;
import com.doloop.www.mayappmgr.material.events.ViewNewBackupAppEvent;
import com.doloop.www.myappmgr.material.MainActivity;
import com.doloop.www.myappmgr.material.adapters.SysAppListAdapter;
import com.doloop.www.myappmgr.material.adapters.SysAppListAdapter.SysAppListDataSetChangedListener;
import com.doloop.www.myappmgr.material.utils.SysAppListItem;
import com.doloop.www.myappmgr.material.utils.Utils;
import com.doloop.www.myappmgr.material.widgets.IndexBarView;
import com.doloop.www.myappmgr.material.widgets.IndexBarView.OnIndexItemClickListener;
import com.doloop.www.myappmgr.material.widgets.PinnedSectionListView;
import com.doloop.www.myappmgrmaterial.R;
import com.nispok.snackbar.Snackbar;
import com.nispok.snackbar.listeners.ActionClickListener;

import de.greenrobot.event.EventBus;

public class SysAppsTabFragment extends BaseFrag implements AdapterView.OnItemLongClickListener{
    private SysAppListAdapter mAdapter;
    private PinnedSectionListView mPinnedSectionListView;
    private static Context mContext;
    private TextView PopTextView;
    private String mfragTitle = "";
    private MenuItem searchMenuItem;
    private DataSetObserver mDataSetObserver;
    private View mEmptyView;

   /* private OnSysAppListItemSelectedListener mListener;

    // Container Activity must implement this interface
    public interface OnSysAppListItemSelectedListener {
        public void onSysAppItemClick(View v, int position);
    }*/

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

    public void filterList(String str){
        mAdapter.getFilter().filter(str);
    }
    
    @Override
    public ListAdapter getListAdapter() {
        // TODO Auto-generated method stub
        return mAdapter;
    }

    /*public void ResetIndexBar() {
        PopTextView.setVisibility(View.INVISIBLE);
        mIndexBarView.InitIndexBar();
    }*/

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        // Complete the Fragment initialization – particularly anything
        // that requires the parent Activity to be initialized or the
        // Fragment’s view to be fully inflated.
        setRetainInstance(false);
        setHasOptionsMenu(false);
        // setEmptyText("No applications");

        // setListShown(false);
        // 监听back按钮事件
       /* getView().setFocusableInTouchMode(true);
        getView().requestFocus();
        getView().setOnKeyListener(new OnKeyListener() {

            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                // TODO Auto-generated method stub
                if (event.getAction() == KeyEvent.ACTION_DOWN) {
                    if (keyCode == KeyEvent.KEYCODE_BACK) {
                        if (MenuItemCompat.isActionViewExpanded(searchMenuItem)) {
                            MenuItemCompat.collapseActionView(searchMenuItem);
                            return true;
                        }
                    }
                }
                return false;
            }
        });*/
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        // TODO Auto-generated method stub
        super.onCreateOptionsMenu(menu, inflater);
        //inflater.inflate(R.menu.sys_applist_menu, menu);

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
        mAdapter.unregisterDataSetObserver(mDataSetObserver);
        mAdapter = null;
        mPinnedSectionListView = null;
        mContext = null;
        uniqueInstance = null;
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
        mAdapter = new SysAppListAdapter(mContext, datalist, map);
        mAdapter.setSysAppListDataSetChangedListener((SysAppListDataSetChangedListener)mContext);
        mPinnedSectionListView.setAdapter(mAdapter);
        mIndexBarView.setExistIndexArray(new ArrayList<String>(map.keySet()));
        mDataSetObserver = new DataSetObserver() {

            @Override
            public void onChanged() {
                // TODO Auto-generated method stub
                super.onChanged();
                mAdapter.getSysAppListDataSetChangedListener().OnSysAppListDataSetChanged(mAdapter.getSysAppListWapperDisplay(),
                        mAdapter.getSectionInListPosMapDisplay());
            }

        };
        mAdapter.registerDataSetObserver(mDataSetObserver);
        mPinnedSectionListView.setEmptyView(mEmptyView);
    }

    public void resetIndexBarView() {
        mIndexBarView.reset();
    }
    
    public void setExistIndexArray(ArrayList<String> list){
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
                String toastMsg = item.appinfo.appName + " \n"
                        + item.appinfo.packageName + " \n"
                        + item.appinfo.apkFilePath;              
                MainActivity.T(toastMsg);              
             // 滚动text
                TextView appVersion = (TextView) v.findViewById(R.id.app_version);
                if (appVersion.isSelected()) {
                    appVersion.setSelected(false);
                }
                appVersion.setSelected(true);               
            }
        } else {
            MainActivity.T("SysApp Item " + position);
        }

        /*
         * String viewContentDesStr = v.getContentDescription().toString(); if (viewContentDesStr.contains("-"))// app
         * view "section-position" { // 滚动text TextView appVersion = (TextView) v.findViewById(R.id.app_version); if
         * (appVersion.isSelected()) { appVersion.setSelected(false); } appVersion.setSelected(true);
         * 
         * String[] DesStr = viewContentDesStr.split("-"); int section = Integer.parseInt(DesStr[0]); int pos =
         * Integer.parseInt(DesStr[1]);
         * 
         * AppInfo appinfo = mAdapter.getItem(section, pos);
         * 
         * 
         * String toastMsg = appinfo.appName + " \n" + appinfo.packageName + " \n" + appinfo.apkFilePath;
         * 
         * MainActivity.T(toastMsg); } else// section { mPinnedSectionListView.setSelection(position); }
         */
    }

    public void listBackToTop(){
        mPinnedSectionListView.smoothScrollToPosition(0);
    }
    
    @Override
    public void setFragmentTitle(String title) {
        // TODO Auto-generated method stub
        mfragTitle = title;
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

    //长按
    @Override
    public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
        // TODO Auto-generated method stub
        SysAppListItem item = mAdapter.getItem(position);
        if(mAdapter.getItemViewType(position) == SysAppListItem.APP_ITEM){
            //备份app
            
            String mBackUpFolder = Utils.getBackUpAPKfileDir(mContext);
            String sdAPKfileName = Utils.BackupApp(item.appinfo, mBackUpFolder);
            if (sdAPKfileName != null) {
                // MainActivity.T(R.string.backup_success);
                SpannableString spanString =
                        new SpannableString(item.appinfo.appName + " "
                                + mContext.getString(R.string.backup_success));
                spanString.setSpan(new UnderlineSpan(), 0, item.appinfo.appName.length(),
                        Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                spanString.setSpan(
                        new ForegroundColorSpan(mContext.getResources().getColor(
                                R.color.theme_blue_light)), 0, item.appinfo.appName.length(),
                        Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);

                Snackbar mSnackbar = MainActivity.getSnackbar(false);
                boolean mAniText = false;
                boolean mShowAniSnackBar = true;
                if (mSnackbar != null) {
                    if (mSnackbar.isShowing()) {
                        if(!spanString.toString().equalsIgnoreCase(mSnackbar.getText().toString())){
                            mAniText = true;
                        }                                         
                        mShowAniSnackBar = false;
                        mSnackbar.dismissAnimation(false);
                        mSnackbar.dismiss();
                    }
                    mSnackbar = MainActivity.getSnackbar(true);
                }
                mSnackbar.actionLabel(R.string.view)
                .actionListener(new ActionClickListener() {
                    @Override
                    public void onActionClicked(Snackbar snackbar) {
                        EventBus.getDefault().post(new ViewNewBackupAppEvent());
                    }
                })
                .actionColorList(mContext.getResources().getColorStateList(R.color.snackbar_action_sel))
                .swipeToDismiss(false)
                .showAnimation(mShowAniSnackBar)
                .dismissAnimation(true)
                .animationText(mAniText)
                .duration(Snackbar.SnackbarDuration.LENGTH_SHORT)
                .attachToAbsListView(mPinnedSectionListView)
                .text(spanString)
                .show(getActivity());

                EventBus.getDefault().post(new AppBackupSuccEvent(item.appinfo));
            } else {
                MainActivity.T(R.string.error);
            }

            return true;
        }
        else{
            return false;
        }
    }

}
