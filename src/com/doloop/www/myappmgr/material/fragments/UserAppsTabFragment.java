package com.doloop.www.myappmgr.material.fragments;

import java.util.ArrayList;
import java.util.Locale;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.database.DataSetObserver;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.widget.SearchView;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.style.ForegroundColorSpan;
import android.text.style.UnderlineSpan;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.RelativeLayout.LayoutParams;
import android.widget.TextView;

import com.doloop.slideexpandable.library.ActionSlideExpandableListView;
import com.doloop.www.mayappmgr.material.events.AppBackupSuccEvent;
import com.doloop.www.mayappmgr.material.events.ViewNewBackupAppEvent;
import com.doloop.www.myappmgr.material.MainActivity;
import com.doloop.www.myappmgr.material.adapters.UserAppListAdapter;
import com.doloop.www.myappmgr.material.adapters.UserAppListAdapter.UserAppListDataSetChangedListener;
//import com.doloop.www.myappmgr.material.adapters.UserAppListAdapter.UserAppListFilterResultListener;
import com.doloop.www.myappmgr.material.dao.AppInfo;
import com.doloop.www.myappmgr.material.utils.Utilities;
import com.doloop.www.myappmgrmaterial.R;
import com.nispok.snackbar.Snackbar;
import com.nispok.snackbar.listeners.ActionClickListener;

import de.greenrobot.event.EventBus;

public class UserAppsTabFragment extends BaseFrag implements ListView.OnScrollListener,
        AdapterView.OnItemLongClickListener {

    private static Context mContext;
    private UserAppListAdapter mAdapter;
    private static ActionSlideExpandableListView mActionSlideExpandableListView;
    private static UserAppsTabFragment uniqueInstance = null;
    private String thisAppPackageName = "";// 避免点击自己，启动自己
    private boolean isAnyStoreInstalled = false;

    private int currentSortType = SortTypeDialogFragment.LIST_SORT_TYPE_NAME_ASC;
    private String mfragTitle = "";
    private MenuItem searchMenuItem;

    private DataSetObserver mDataSetObserver;
    private View mEmptyView;

    public void setListSortType(int sortType) {
        currentSortType = sortType;
        switch (currentSortType) {
            case SortTypeDialogFragment.LIST_SORT_TYPE_NAME_ASC:
            case SortTypeDialogFragment.LIST_SORT_TYPE_NAME_DES:
                // mDialogText = nameText;
                int size =
                        (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 80, getResources()
                                .getDisplayMetrics());
                // 放在list的中间
                LayoutParams paramsFixSize = new LayoutParams(size, size);
                paramsFixSize.alignWithParent = true;
                paramsFixSize.addRule(RelativeLayout.CENTER_IN_PARENT);

                mDialogText.setTextSize(TypedValue.COMPLEX_UNIT_SP, 40);
                mDialogText.setPadding(0, 0, 0, 0);
                mDialogText.setLayoutParams(paramsFixSize);
                break;
            case SortTypeDialogFragment.LIST_SORT_TYPE_SIZE_ASC:
            case SortTypeDialogFragment.LIST_SORT_TYPE_SIZE_DES:
            case SortTypeDialogFragment.LIST_SORT_TYPE_LAST_MOD_TIME_ASC:
            case SortTypeDialogFragment.LIST_SORT_TYPE_LAST_MOD_TIME_DES:
                int padding =
                        (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 5, getResources()
                                .getDisplayMetrics());
                LayoutParams paramsWrapContent = new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
                paramsWrapContent.alignWithParent = true;
                // paramsWrapContent.addRule(RelativeLayout.CENTER_IN_PARENT);
                // 放在list的top
                paramsWrapContent.addRule(RelativeLayout.CENTER_HORIZONTAL);
                paramsWrapContent.addRule(RelativeLayout.ALIGN_PARENT_TOP);
                paramsWrapContent.setMargins(0, padding, 0, 0);

                mDialogText.setTextSize(TypedValue.COMPLEX_UNIT_SP, 25);
                mDialogText.setPadding(padding, 0, padding, 0);
                mDialogText.setLayoutParams(paramsWrapContent);
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
    private TextView mDialogText;
    private boolean mShowing = false;
    private boolean mListIsScrolling = false;

    private void removeWindow() {
        if (mShowing) {
            mShowing = false;
            mDialogText.setVisibility(View.INVISIBLE);
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
        isAnyStoreInstalled = Utilities.isAnyStoreInstalled(getActivity());
        thisAppPackageName = Utilities.getSelfAppInfo(getActivity()).packageName;
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
                                    Intent intent =
                                            getActivity().getPackageManager().getLaunchIntentForPackage(
                                                    targetpackageName);
                                    if (intent != null) {
                                        if (intent != null) {
                                            try{
                                                startActivity(intent);
                                            }catch(Exception e){
                                                MainActivity.T(R.string.error);
                                            }
                                        }
                                    } else {
                                        MainActivity.T(R.string.error);
                                    }
                                }
                                break;
                            case R.id.infoActionLayout:
                                Utilities.showInstalledAppDetails(getActivity(), targetpackageName);
                                break;
                            case R.id.backupActionLayout:
                                String mBackUpFolder = Utilities.getBackUpAPKfileDir(getActivity());
                                String sdAPKfileName = Utilities.BackupApp(selectItem, mBackUpFolder);
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
                                    .attachToAbsListView(mActionSlideExpandableListView)
                                    .text(spanString)
                                    .show(getActivity());

                                    EventBus.getDefault().post(new AppBackupSuccEvent(selectItem));
                                } else {
                                    MainActivity.T(R.string.error);
                                }
                                break;
                            case R.id.uninstallActionLayout:
                                Uri packageUri = Uri.parse("package:" + targetpackageName);
                                Intent uninstallIntent;
                                if (Build.VERSION.SDK_INT < Build.VERSION_CODES.ICE_CREAM_SANDWICH) {
                                    uninstallIntent = new Intent(Intent.ACTION_DELETE, packageUri);
                                } else {
                                    uninstallIntent = new Intent(Intent.ACTION_UNINSTALL_PACKAGE, packageUri);
                                }
                                startActivity(uninstallIntent);
                                break;
                            case R.id.moreActionLayout:
                                if (selectItem.iconBitmap == null) {
                                    selectItem.iconBitmap =
                                            Utilities.getIconBitmap(getActivity(), selectItem.packageName);
                                }

                                /*
                                 * UserAppListMoreActionDialog =
                                 * UserAppListMoreActionDialogFragment.newInstance(selectItem);
                                 * UserAppListMoreActionDialog.show(getSupportFragmentManager(),
                                 * UserAppListMoreActionDialogFragment.DialogTag);
                                 */

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
        // mActionSlideExpandableListView.setOnItemLongClickListener(this);
        mDialogText = (TextView) contentView.findViewById(R.id.popTextView);
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
        mAdapter.unregisterDataSetObserver(mDataSetObserver);
        mAdapter = null;
        mActionSlideExpandableListView = null;
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
         * try { mItemClickListener = (OnUserAppListItemSelectedListener) activity; mActionClickListener =
         * (OnUserAppListItemActionClickListener) activity; mItemLongClickListener =
         * (OnUserAppListItemLongClickListener) activity; } catch (ClassCastException e) { throw new
         * ClassCastException(activity.toString() + "must implement Listener"); }
         */
    }

    public void setData(ArrayList<AppInfo> userAppList) {
        mAdapter = new UserAppListAdapter(getActivity(), R.layout.user_app_expandable_list_item, 0, userAppList);
        // mAdapter.setUserAppListFilterResultListener((UserAppListFilterResultListener)mContext);
        mAdapter.setUserAppListDataSetChangedListener((UserAppListDataSetChangedListener) mContext);
        mActionSlideExpandableListView.setAdapter(mAdapter);
        mActionSlideExpandableListView.setEmptyView(mEmptyView);
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
        final AppInfo selectItem = mAdapter.getItem(position);
        mActionSlideExpandableListView.collapse(true);
        // 滚动text
        TextView appVersion = (TextView) v.findViewById(R.id.app_version);
        if (appVersion.isSelected()) {
            appVersion.setSelected(false);
        }
        appVersion.setSelected(true);
        MainActivity.T(selectItem.apkFilePath);

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
                    mDialogText.setText(((Character) firstLetter).toString().toUpperCase(Locale.getDefault()));
                    break;
                case SortTypeDialogFragment.LIST_SORT_TYPE_SIZE_ASC:
                case SortTypeDialogFragment.LIST_SORT_TYPE_SIZE_DES:
                    mDialogText.setText(firstVisiableApp.appSizeStr);
                    break;
                case SortTypeDialogFragment.LIST_SORT_TYPE_LAST_MOD_TIME_ASC:
                case SortTypeDialogFragment.LIST_SORT_TYPE_LAST_MOD_TIME_DES:
                    mDialogText.setText(firstVisiableApp.lastModifiedTimeStr);
                    break;
            }

            mShowing = true;
            mDialogText.setVisibility(View.VISIBLE);
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

    @Override
    public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
        // TODO Auto-generated method stub
        mItemLongClickListener.onUserAppItemLongClick(parent, view, position, id);
        return true;
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
            // moveHeight += Utilities.dp2px(mContext, 10);
            mActionSlideExpandableListView.smoothScrollBy(moveHeight, 800);
        }
    }

    @Override
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
    }

    public void collapseLastOpenItem(boolean anim) {
        mActionSlideExpandableListView.collapse(anim);
    }

    @Override
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
    }

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

}
