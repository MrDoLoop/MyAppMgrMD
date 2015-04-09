package com.doloop.www.myappmgr.material.fragments;

import java.util.ArrayList;
import java.util.Locale;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.DataSetObserver;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.view.ActionMode;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.ForegroundColorSpan;
import android.text.style.UnderlineSpan;
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
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.doloop.slideexpandable.library.ActionSlideExpandableListView;
import com.doloop.slideexpandable.library.IExpendButtonClickListener;
import com.doloop.www.myappmgr.material.AppDetailActivity;
import com.doloop.www.myappmgr.material.MainActivity;
import com.doloop.www.myappmgr.material.adapters.UserAppListAdapter;
import com.doloop.www.myappmgr.material.adapters.UserAppListAdapter.UserAppListDataSetChangedListener;
//import com.doloop.www.myappmgr.material.adapters.UserAppListAdapter.UserAppListFilterResultListener;
import com.doloop.www.myappmgr.material.dao.AppInfo;
import com.doloop.www.myappmgr.material.events.ActionModeToggleEvent;
import com.doloop.www.myappmgr.material.events.AppBackupSuccEvent;
import com.doloop.www.myappmgr.material.events.AppUpdateEvent;
import com.doloop.www.myappmgr.material.events.BackupAppEvent;
import com.doloop.www.myappmgr.material.events.ViewNewBackupAppEvent;
import com.doloop.www.myappmgr.material.fragments.SelectionDialogFragment.SelectionDialogClickListener;
import com.doloop.www.myappmgr.material.fragments.UserAppListMoreActionDialogFragment.UserAppMoreActionListItemClickListener;
import com.doloop.www.myappmgr.material.interfaces.IconClickListener;
import com.doloop.www.myappmgr.material.utils.Utils;
import com.doloop.www.myappmgr.material.R;
import com.nispok.snackbar.Snackbar;
import com.nispok.snackbar.listeners.ActionClickListener;

import de.greenrobot.event.EventBus;

public class UserAppsTabFragment extends BaseFrag implements ListView.OnScrollListener,
        UserAppMoreActionListItemClickListener, SelectionDialogClickListener,AdapterView.OnItemLongClickListener, IconClickListener, 
        IExpendButtonClickListener {

    private static Context mContext;
    private UserAppListAdapter mAdapter;
    private static ActionSlideExpandableListView mActionSlideExpandableListView;
    private static UserAppsTabFragment uniqueInstance = null;
    private String thisAppPackageName = "";// 避免点击自己，启动自己
    //private boolean isAnyStoreInstalled = false;

    private int currentSortType = SortTypeDialogFragment.LIST_SORT_TYPE_NAME_ASC;
    //private MenuItem searchMenuItem;

    private DataSetObserver mDataSetObserver;
    private View mEmptyView;
    public static boolean isInActoinMode = false;
    private UserAppListMoreActionDialogFragment UserAppListMoreActionDialog;
    private SelectionDialogFragment SelectionDialog;

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

    public UserAppsTabFragment() {

    }

    public synchronized static UserAppsTabFragment getInstance(Context ctx) {
        if (uniqueInstance == null) {
            uniqueInstance = new UserAppsTabFragment();
        }
        mContext = ctx;
        return uniqueInstance;
    }

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
                    if(mTopDialogText.isShown()){
                        mTopDialogText.setVisibility(View.GONE);
                    }
                    else if(mCentralDialogText.isShown()){
                        mCentralDialogText.setVisibility(View.GONE);
                    }
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

    // list长按事件
    public OnUserAppListItemLongClickListener mItemLongClickListener;

    // Container Activity must implement this interface
    public interface OnUserAppListItemLongClickListener {
        public void onUserAppItemLongClick(AdapterView<?> parent, View view, int position, long id);
    }

    // list条目点击事件
    public OnUserAppListItemSelectedListener mItemClickListener;

    // Container Activity must implement this interface
    public interface OnUserAppListItemSelectedListener {
        public void onUserAppItemClick(View v, int position);
    }

    // list action 点击事件
    public OnUserAppListItemActionClickListener mActionClickListener;

    // Container Activity must implement this interface
    public interface OnUserAppListItemActionClickListener {
        public void onUserAppItemActionClick(View listView, View buttonview, int position);
    }

    @Override
    public ListView getListView() {
        // TODO Auto-generated method stub
        return mActionSlideExpandableListView;
    }

    @Override
    public ListAdapter getListAdapter() {
        // TODO Auto-generated method stub
        return mAdapter;
    }

    public ArrayList<AppInfo> getDisplayList() {
        return mAdapter.getDisplayList();
    }

    public void notifyDataSetChanged() {
        mAdapter.notifyDataSetChanged();
    }

    public void filterList(String str) {
        mAdapter.getFilter().filter(str);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        //isAnyStoreInstalled = Utilities.isAnyStoreInstalled(getActivity());
        thisAppPackageName = Utils.getSelfAppInfo(getActivity()).packageName;
        View contentView = inflater.inflate(R.layout.user_app_slide_expandable_list, container, false);
        mActionSlideExpandableListView = (ActionSlideExpandableListView) contentView.findViewById(android.R.id.list);
        mActionSlideExpandableListView.setItemActionListener(
                new ActionSlideExpandableListView.OnActionClickListener() {
                    @TargetApi(Build.VERSION_CODES.ICE_CREAM_SANDWICH)
                    @Override
                    public void onClick(View listView, View buttonview, int position) {
                        final AppInfo selectItem = mAdapter.getItem(position);
                        String targetpackageName = selectItem.packageName;
                        switch (buttonview.getId()) {
                            case R.id.openActionLayout:
                                if (thisAppPackageName.equals(targetpackageName))// 避免再次启动自己app
                                {
                                    MainActivity.T("You catch me!! NAN Made app");
                                } else {
                                    if(!Utils.launchApp(getActivity(), selectItem)){
                                        MainActivity.T(R.string.launch_fail);
                                    }
                                }
                                break;
                            case R.id.infoActionLayout:
                                Utils.showInstalledAppDetails(getActivity(), targetpackageName);
                                break;
                            case R.id.backupActionLayout:
                                String mBackUpFolder = Utils.getBackUpAPKfileDir(getActivity());
                                String sdAPKfileName = Utils.BackupApp(selectItem, mBackUpFolder);
                                if (sdAPKfileName != null) {
                                    // MainActivity.T(R.string.backup_success);
                                    SpannableString spanString =
                                            new SpannableString(selectItem.appName + " "
                                                    + mContext.getString(R.string.backup_success));
                                    spanString.setSpan(new UnderlineSpan(), 0, selectItem.appName.length(),
                                            Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                                    spanString.setSpan(
                                            new ForegroundColorSpan(mContext.getResources().getColor(
                                                    R.color.theme_blue_light)), 0, selectItem.appName.length(),
                                            Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);

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
                                    mSnackbar.swipeToDismiss(false).showAnimation(mShowAniSnackBar)
                                            .dismissAnimation(true).animationText(mAniText)
                                            .duration(Snackbar.SnackbarDuration.LENGTH_SHORT)
                                            // .attachToAbsListView(mActionSlideExpandableListView)
                                            .text(spanString);

                                    if (!isInActoinMode) {
                                        mSnackbar
                                                .actionLabel(R.string.view)
                                                .actionListener(new ActionClickListener() {
                                                    @Override
                                                    public void onActionClicked(Snackbar snackbar) {
                                                        EventBus.getDefault().post(new ViewNewBackupAppEvent());
                                                    }
                                                })
                                                .actionColorList(
                                                        mContext.getResources().getColorStateList(
                                                                R.color.snackbar_action_sel));
                                    }
                                    mSnackbar.show(getActivity());

                                    EventBus.getDefault().post(new AppBackupSuccEvent(selectItem));
                                } else {
                                    MainActivity.T(R.string.error);
                                }
                                break;
                            case R.id.uninstallActionLayout:
                                Utils.uninstallApp(getActivity(), selectItem);
                                /*Uri packageUri = Uri.parse("package:" + targetpackageName);
                                Intent uninstallIntent;
                                if (Build.VERSION.SDK_INT < Build.VERSION_CODES.ICE_CREAM_SANDWICH) {
                                    uninstallIntent = new Intent(Intent.ACTION_DELETE, packageUri);
                                } else {
                                    uninstallIntent = new Intent(Intent.ACTION_UNINSTALL_PACKAGE, packageUri);
                                }
                                startActivity(uninstallIntent);*/
                                break;
                            case R.id.moreActionLayout:
                                UserAppListMoreActionDialog = new UserAppListMoreActionDialogFragment();
                                UserAppListMoreActionDialog.setArgs(selectItem, UserAppsTabFragment.this);
                                UserAppListMoreActionDialog.show(getActivity().getSupportFragmentManager(),
                                        UserAppListMoreActionDialogFragment.DialogTag);

                                break;
                        }
                    }

                    // note that we also add 1 or more ids to the
                    // setItemActionListener
                    // this is needed in order for the listview to discover the
                    // buttons
                }, R.id.openActionLayout, R.id.infoActionLayout, R.id.backupActionLayout, R.id.uninstallActionLayout,
                R.id.moreActionLayout);
        mActionSlideExpandableListView.setOnScrollListener(this);
        mActionSlideExpandableListView.setOnItemLongClickListener(this);
        mTopDialogText = (TextView) contentView.findViewById(R.id.topPopTextView);
        mCentralDialogText = (TextView) contentView.findViewById(R.id.centralPopTextView);
        mEmptyView = contentView.findViewById(R.id.emptyView);
        return contentView;
    }

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
         * @Override public boolean onKey(View v, int keyCode, KeyEvent event) { // TODO Auto-generated method stub
         * if(!MainActivity.sDrawerIsOpen) { if (event.getAction() == KeyEvent.ACTION_DOWN) { if (keyCode ==
         * KeyEvent.KEYCODE_BACK) { if(MenuItemCompat.isActionViewExpanded(searchMenuItem)){
         * MenuItemCompat.collapseActionView(searchMenuItem); return true; } } } } return false; } });
         */

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
        if(mAdapter != null){
            mAdapter.unregisterDataSetObserver(mDataSetObserver);
        }
        mAdapter = null;
        mActionSlideExpandableListView = null;
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
         * try { mItemClickListener = (OnUserAppListItemSelectedListener) activity; mActionClickListener =
         * (OnUserAppListItemActionClickListener) activity; mItemLongClickListener =
         * (OnUserAppListItemLongClickListener) activity; } catch (ClassCastException e) { throw new
         * ClassCastException(activity.toString() + "must implement Listener"); }
         */
    }

    public void setData(ArrayList<AppInfo> userAppList) {
        mAdapter = new UserAppListAdapter(getActivity(), R.layout.user_app_expandable_list_item, 0, userAppList, this);
        mAdapter.setUserAppListDataSetChangedListener((UserAppListDataSetChangedListener) mContext);
        mActionSlideExpandableListView.setAdapter(mAdapter);
        mActionSlideExpandableListView.setEmptyView(mEmptyView);
        mActionSlideExpandableListView.setOnExpendButtonClickListener(this);
        mDataSetObserver = new DataSetObserver() {

            @Override
            public void onChanged() {
                // TODO Auto-generated method stub
                super.onChanged();
                collapseLastOpenItem(false);
                mAdapter.getUserAppListDataSetChangedListener().OnUserAppListDataSetChanged();
            }

        };
        mAdapter.registerDataSetObserver(mDataSetObserver);

    }

    @Override
    public void onListItemClick(ListView l, View v, int position, long id) {
        // TODO Auto-generated method stub
        super.onListItemClick(l, v, position, id);

        /*
         * Drawable bgDrawable = v.findViewById(R.id.bgLayout).getBackground(); if(bgDrawable instanceof
         * TransitionDrawable){ ((TransitionDrawable)bgDrawable).reverseTransition(250); } else{ TransitionDrawable td =
         * new TransitionDrawable(new Drawable[] { new ColorDrawable(Color.WHITE),
         * mContext.getResources().getDrawable(R.drawable.list_row_item_pressed_bg) }); // TransitionDrawable td = new
         * TransitionDrawable(new Drawable[] { new ColorDrawable(Color.WHITE), // new
         * ColorDrawable(mContext.getResources().getColor(R.color.theme_blue_light)) });
         * v.findViewById(R.id.bgLayout).setBackgroundDrawable(td); td.startTransition(250); }
         */

        if (isInActoinMode) {
            mAdapter.toggleSelection(position, true);
            updateActionModeTitle();
        } else {
            AppInfo selectItem = mAdapter.getItem(position);
            //mActionSlideExpandableListView.collapse(true);
            // 滚动text
            /*TextView appVersion = (TextView) v.findViewById(R.id.app_version);
            if (appVersion.isSelected()) {
                appVersion.setSelected(false);
            }
            appVersion.setSelected(true);
            MainActivity.T(selectItem.apkFilePath);*/
            
            AppDetailActivity.curAppInfo = selectItem;
            Intent intent = new Intent(getActivity(), AppDetailActivity.class);
            if(Utils.playAniAppDetails(getActivity())){
                int[] revealStartPos = Utils.findViewCenterXY(v);//v.findViewById(R.id.app_icon)
                intent.putExtra(AppDetailActivity.REVEAL_START_POSITION, revealStartPos);
                intent.putExtra(AppDetailActivity.APP_TYPE, AppDetailActivity.APP_TYPE_USER);
                startActivity(intent);
                getActivity().overridePendingTransition(0, 0);
            }
            else{
                Utils.startActivtyWithAni(getActivity(), intent);
            }
        }

        /*
         * if (mActionMode != null) { if (selectItem.selected) { UserAppActionModeSelectCnt--; selectItem.selected =
         * false; } else { UserAppActionModeSelectCnt++; selectItem.selected = true; }
         * 
         * if (UserAppActionModeSelectCnt < mUserAppListAdapter.getCount()) {
         * mActionMode.getMenu().getItem(ACTIONMODE_MENU_SELECT).setTitle(R.string.select_all);
         * mActionMode.getMenu().getItem(ACTIONMODE_MENU_SELECT).setIcon(R.drawable.ic_action_select_all); } else {
         * mActionMode.getMenu().getItem(ACTIONMODE_MENU_SELECT).setTitle(R.string.deselect_all);
         * mActionMode.getMenu().getItem(ACTIONMODE_MENU_SELECT).setIcon(R.drawable.ic_action_deselect_all); }
         * 
         * mActionMode.setTitle("" + UserAppActionModeSelectCnt); mUserAppListAdapter.notifyDataSetChanged();
         * 
         * } else { ((ActionSlideExpandableListView) usrAppsFrg.getListView()).collapse(true); // 滚动text TextView
         * appVersion = (TextView) v.findViewById(R.id.app_version); if (appVersion.isSelected()) {
         * appVersion.setSelected(false); } appVersion.setSelected(true); toast.setText(selectItem.apkFilePath);
         * toast.show(); }
         */
        // mItemClickListener.onUserAppItemClick(v, position);
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
    public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
        // TODO Auto-generated method stub
        if (mAdapter == null)
            return;

        Log.i("ttt", "user appList onScroll");
        Log.i("ttt", "mListIsScrolling is " + mListIsScrolling);

        if (mListIsScrolling) {
            AppInfo firstVisiableApp = mAdapter.getItem(firstVisibleItem);
            if (firstVisiableApp == null)
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
                    mTopDialogText.setText(firstVisiableApp.lastModifiedTimeStr);
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
        MainActivity.dismissSnackbar();
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

    @Override
    public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
        // TODO Auto-generated method stub
        // mItemLongClickListener.onUserAppItemLongClick(parent, view, position, id);

        if (isInActoinMode) {
            if(mAdapter.getCount() > 1){//只有一项的时候不显示选择对话框
                SelectionDialog = new SelectionDialogFragment();
                SelectionDialog.setArgs(mAdapter.getItem(position), position, position == 0 ? true:false, position == mAdapter.getCount()-1 ? true:false, UserAppsTabFragment.this);
                SelectionDialog.show(getActivity().getSupportFragmentManager(), SelectionDialogFragment.DialogTag);
                return true;
            } 
            return false;
        } else {
            view.findViewById(R.id.app_icon).performClick();
            return true;
        }
    }

    public static void ExpandAnimationFinsh(int ExpandableViewBtm) {
        int[] loc = new int[2];
        mActionSlideExpandableListView.getLocationOnScreen(loc);
        int moveHeight = ExpandableViewBtm - (loc[1] + mActionSlideExpandableListView.getMeasuredHeight());
        if (moveHeight > 0) {

            moveHeight +=
                    mContext.getResources().getDimensionPixelSize(R.dimen.card_content_padding)
                            + mContext.getResources().getDimensionPixelSize(R.dimen.card_elevation)
                            + mContext.getResources().getDimensionPixelSize(R.dimen.card_max_elevation);
           
            mActionSlideExpandableListView.smoothScrollBy(moveHeight, 800);
        }
    }

   /* @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        // TODO Auto-generated method stub
        super.onCreateOptionsMenu(menu, inflater);
        // inflater.inflate(R.menu.user_applist_menu, menu);

        final MenuItem SortMenuItem = menu.findItem(R.id.menu_sort).setVisible(true);
        searchMenuItem = menu.findItem(R.id.menu_search).setVisible(true);

        SearchView searchView = (SearchView) MenuItemCompat.getActionView(menu.findItem(R.id.menu_search));
        searchView.setQueryHint(this.getString(R.string.search));

        EditText edTxt = ((EditText) searchView.findViewById(android.support.v7.appcompat.R.id.search_src_text));
        edTxt.setHintTextColor(getResources().getColor(R.color.white));

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
                mActionSlideExpandableListView.collapse(false);
                String mCurFilter = !TextUtils.isEmpty(newText) ? newText : null;
                mAdapter.getFilter().filter(mCurFilter);
                return true;
            }
        });
        MenuItemCompat.setOnActionExpandListener(searchMenuItem, new MenuItemCompat.OnActionExpandListener() {

            @Override
            public boolean onMenuItemActionExpand(MenuItem item) {
                // TODO Auto-generated method stub
                // 搜索打开的时候隐藏sort
                SortMenuItem.setVisible(false);
                return true;
            }

            @Override
            public boolean onMenuItemActionCollapse(MenuItem item) {
                // TODO Auto-generated method stub
                SortMenuItem.setVisible(true);
                mActionSlideExpandableListView.collapse(false);
                mAdapter.getFilter().filter(null);
                return true;
            }
        });
    }*/

    public void collapseLastOpenItem(boolean anim) {
        mActionSlideExpandableListView.collapse(anim);
    }

   /* @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // TODO Auto-generated method stub

        switch (item.getItemId()) {

            case R.id.menu_sort:

                break;

            case R.id.menu_search:
                // mSearchCheck = true;

                break;
        }
        return true;
    }*/

    public void listBackToTop() {
        mActionSlideExpandableListView.smoothScrollToPosition(0);
    }

    @Override
    public void setFragmentTitle(String title) {
        // TODO Auto-generated method stub

    }

    @Override
    public String getFragmentTitle() {
        // TODO Auto-generated method stub
        if (mAdapter == null || mAdapter.getCount() == 0) {
            return mContext.getString(R.string.user_apps);
        } else {
            return mContext.getString(R.string.user_apps) + " (" + mAdapter.getCount() + ")";
        }
    }

    // IconClickListener--start
    @TargetApi(Build.VERSION_CODES.ICE_CREAM_SANDWICH)
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
                                    if (mAdapter.getSelectedItemCnt() < mAdapter.getCount()) {// 选择全部
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
                                        /*EventBus.getDefault().post(
                                                new BackupAppEvent(mAdapter.getSelectedItemList(), true));*/
                                    }
                                    break;

                                case R.id.menu_uninstall:
                                    if (mAdapter.getSelectedItemCnt() == 0) {
                                        MainActivity.T(R.string.nothing_selected);
                                    } else {
                                        AppInfo tmpAppInfo = null;
                                        ArrayList<AppInfo> list = mAdapter.getSelectedItemList();
                                        for (int i = 0; i < list.size(); i++) {
                                            tmpAppInfo = list.get(i);
                                            Utils.uninstallApp(getActivity(), tmpAppInfo);
                                            /*Uri packageUri = Uri.parse("package:" + tmpAppInfo.packageName);
                                            Intent uninstallIntent;
                                            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.ICE_CREAM_SANDWICH) {
                                                uninstallIntent = new Intent(Intent.ACTION_DELETE, packageUri);
                                            } else {
                                                uninstallIntent = new Intent(Intent.ACTION_UNINSTALL_PACKAGE, packageUri);
                                            }
                                            startActivity(uninstallIntent);*/
                                        }
                                        
                                        finishActionMode();
                                    }
                                    break;
                            }
                            return true;
                        }

                        @Override
                        public boolean onCreateActionMode(ActionMode mode, Menu menu) {
                            // TODO Auto-generated method stub
                            MenuInflater inflater = getActivity().getMenuInflater();
                            inflater.inflate(R.menu.user_app_action_menu, menu);
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

    // IconClickListener--end

    
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
    public void onUserAppMoreActionListItemClickListener(DialogInterface dialog, int item, AppInfo appInfo) {
        // TODO Auto-generated method stub
        if (item == 0)// google play
        {
//            if (Utils.isAnyStoreInstalled(getActivity())) {
//                startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + appInfo.packageName)));
//            } else {
//                startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("http://play.google.com/store/apps/details?id="
//                        + appInfo.packageName)));
//            }
            Utils.startMarketSearch(getActivity(), appInfo);
        } 
        else if (item == 1) // send
        {
            Utils.chooseSendByApp(getActivity(), Uri.parse("file://" + appInfo.apkFilePath));
            
            /*String BACK_UP_FOLDER = Utils.getBackUpAPKfileDir(getActivity());
            String sdAPKfileName = Utils.BackupApp(appInfo, BACK_UP_FOLDER);
            if (sdAPKfileName != null) {
                ArrayList<AppInfo> list = new ArrayList<AppInfo>();
                list.add(appInfo);
                EventBus.getDefault().post(new AppBackupSuccEvent(list));
                Utils.chooseSendByApp(getActivity(), Uri.parse("file://" + sdAPKfileName));
            } else {
                MainActivity.T(R.string.error);
            }*/
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
    
    
    
    public void onEventMainThread(AppUpdateEvent ev) {
        switch (ev.mAppState) {
            case APP_ADDED:
            case APP_REMOVED:
                Utils.DismissDialog(UserAppListMoreActionDialog);
                Utils.DismissDialog(SelectionDialog);
                break;
            default:
                break;
        }
    }

    @Override
    public void OnExpendButtonClick(int position, boolean expand, View expendableArea) {
        // TODO Auto-generated method stub
        if(expand){
            AppInfo appInfo = mAdapter.getItem(position);
            View launchAction = expendableArea.findViewById(R.id.openActionLayout);
            if(Utils.canLaunch(mContext, appInfo))
            {
                launchAction.setVisibility(View.VISIBLE);
            }
            else
            {
                launchAction.setVisibility(View.GONE);
            }
            
        }
    }

}
