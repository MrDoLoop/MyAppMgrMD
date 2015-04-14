package com.doloop.www.myappmgr.material;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Map.Entry;
import java.util.TreeMap;

import org.apache.commons.io.FileUtils;

import android.annotation.TargetApi;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.MenuItemCompat;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.view.ActionMode;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.AutoCompleteTextView;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.doloop.www.myappmgr.material.adapters.AppListFragAdapter;
import com.doloop.www.myappmgr.material.adapters.BackupAppListAdapterV2.BackupAppListDataSetChangedListener;
import com.doloop.www.myappmgr.material.adapters.SysAppListAdapter.SysAppListDataSetChangedListener;
import com.doloop.www.myappmgr.material.adapters.UserAppListAdapter.UserAppListDataSetChangedListener;
import com.doloop.www.myappmgr.material.dao.AppInfo;
import com.doloop.www.myappmgr.material.dao.AppInfoDao.Properties;
import com.doloop.www.myappmgr.material.dao.DaoSession;
import com.doloop.www.myappmgr.material.dao.DaoUtils;
import com.doloop.www.myappmgr.material.events.ActionModeToggleEvent;
import com.doloop.www.myappmgr.material.events.AppBackupSuccEvent;
import com.doloop.www.myappmgr.material.events.AppUpdateEvent;
import com.doloop.www.myappmgr.material.events.AppUpdateEvent.AppState;
import com.doloop.www.myappmgr.material.events.BackupAppEvent;
import com.doloop.www.myappmgr.material.events.DrawerItemClickEvent;
import com.doloop.www.myappmgr.material.events.ViewNewBackupAppEvent;
import com.doloop.www.myappmgr.material.fragments.BackupAppTabFragmentV2;
import com.doloop.www.myappmgr.material.fragments.BaseFrag;
import com.doloop.www.myappmgr.material.fragments.DrawerFragment;
import com.doloop.www.myappmgr.material.fragments.SortTypeDialogFragment;
import com.doloop.www.myappmgr.material.fragments.SortTypeDialogFragment.SortTypeListItemClickListener;
import com.doloop.www.myappmgr.material.fragments.SysAppsTabFragment;
import com.doloop.www.myappmgr.material.fragments.UserAppsTabFragment;
import com.doloop.www.myappmgr.material.swipeback.lib.SwipeBackUtils;
import com.doloop.www.myappmgr.material.utils.ApkFileFilter;
import com.doloop.www.myappmgr.material.utils.AppPinYinComparator;
import com.doloop.www.myappmgr.material.utils.Constants;
import com.doloop.www.myappmgr.material.utils.L;
import com.doloop.www.myappmgr.material.utils.ScrimUtil;
import com.doloop.www.myappmgr.material.utils.SharpStringComparator;
import com.doloop.www.myappmgr.material.utils.SysAppListItem;
import com.doloop.www.myappmgr.material.utils.Utils;
import com.doloop.www.myappmgr.material.widgets.MyProgressDialog;
import com.doloop.www.myappmgr.material.widgets.MyViewPager;
import com.doloop.www.myappmgr.material.widgets.PagerSlidingTabStrip;
import com.doloop.www.myappmgr.material.widgets.PagerSlidingTabStrip.OnTabClickListener;
import com.doloop.www.myappmgr.material.R;
import com.nineoldandroids.view.ViewHelper;
import com.nispok.snackbar.Snackbar;
import com.readystatesoftware.systembartint.SystemBarTintManager;
import com.readystatesoftware.systembartint.SystemBarTintManager.SystemBarConfig;

import de.greenrobot.event.EventBus;

public class MainActivity extends BaseActivity implements // UserAppListFilterResultListener,
        UserAppListDataSetChangedListener, SysAppListDataSetChangedListener, SortTypeListItemClickListener,// BackupAppListDataSetChangedListener,
        BackupAppListDataSetChangedListener {
    private DrawerLayout mDrawerLayout;
    private ActionBarDrawerToggle mDrawerToggle;
    private ActionBar mActionBar;

    private MyProgressDialog progDialog;

    private ArrayList<BaseFrag> Fragmentlist;
    private static long back_pressed = 0;

    // 用户app列表
    private UserAppsTabFragment usrAppsFrg;
    // private UserAppListAdapter mUserAppListAdapter;
    private ArrayList<AppInfo> UserAppFullList = new ArrayList<AppInfo>();

    // 系统app列表
    private SysAppsTabFragment sysAppsFrg;
    private ArrayList<AppInfo> SysAppFullList = new ArrayList<AppInfo>();
    private ArrayList<SysAppListItem> SysAppFullListWapper = new ArrayList<SysAppListItem>();
    private TreeMap<String, Integer> mSectionInListPosMap = new TreeMap<String, Integer>();

    // 备份app列表
    private BackupAppTabFragmentV2 backupAppsFrg;
    // private BackupAppTabFragment backupAppsFrg;

    private EditText searchViewEdt;

    private AppListFragAdapter mFragAdapter;
    private int screenWidth = 0;

    private MyViewPager mPager;
    private PagerSlidingTabStrip mPagerSlidingTabStrip;

    private MenuItem searchMenuItem;
    private MenuItem sortMenuItem;

    private AppUpdateReceiver mAppUpdateReceiver;
    private IntentFilter AppIntentFilter;

    private LangUpdateReceiver mLangUpdateReceiver;
    private IntentFilter LangIntentFilter;

    private SdcardUpdateReceiver mSdcardUpdateReceiver;
    private IntentFilter sdcardIntentFilter;

    private static Snackbar mSnackbar;
    private static Context thisActivityCtx;
    public static ActionMode sActionMode = null;
    private static Toast toast;

    public static boolean sIsSdcardReady = false;

    private SortTypeDialogFragment SortTypeDialog;
    // private UserAppListMoreActionDialogFragment UserAppListMoreActionDialog;
    // private SelectionDialogFragment SelectionDialog;

    // private DrawerItemClickEvent mDrawerItemClickEvent;

    private int oldPagePos = 0;
    private Toolbar toolbar;

    @TargetApi(Build.VERSION_CODES.KITKAT)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        
        if (Build.VERSION.SDK_INT == Build.VERSION_CODES.KITKAT) {
            // setTranslucentStatus(true);

            Window window = this.getWindow();
            window.setFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS,
                    WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);

            boolean hasNavBar = Utils.hasNavBar(this);
            SystemBarTintManager tintManager = new SystemBarTintManager(this);
            SystemBarConfig config = tintManager.getConfig();
            // 处理状态栏
            tintManager.setStatusBarTintEnabled(true);
            tintManager.setStatusBarTintResource(R.color.transparent);
            tintManager.setStatusBarAlpha(0.5f);

            View contHolder = findViewById(R.id.content_linear);
            if (hasNavBar) {
                contHolder.setPadding(0, config.getStatusBarHeight(), 0, config.getNavigationBarHeight());
            } else {
                contHolder.setPadding(0, config.getStatusBarHeight(), 0, 0);
            }

            // 处理底边导航栏
            if (hasNavBar) {
                tintManager.setNavigationBarTintEnabled(true);
                tintManager.setNavigationBarTintResource(R.color.primary);
                View drawerHolder = findViewById(R.id.drawer_content_holder);
                drawerHolder.setPadding(0, 0, 0, config.getNavigationBarHeight());
            }
            // contLinear.setPadding(0, config.getPixelInsetTop(true), 0, config.getPixelInsetBottom());
            // statusBarHolder.getLayoutParams().height = config.getStatusBarHeight();
            // statusBarHolder.setVisibility(View.VISIBLE);

        }

        AppUpdateStaticReceiver.handleEvent = false;
        thisActivityCtx = MainActivity.this;
        toast = Toast.makeText(thisActivityCtx, "", Toast.LENGTH_SHORT);
        screenWidth = getWindowManager().getDefaultDisplay().getWidth();
        EventBus.getDefault().register(this);
        if (savedInstanceState != null) {
            L.d("savedInstanceState != null" + savedInstanceState.toString());
        }

        Drawable shadow = ScrimUtil.makeCubicGradientScrimDrawable(Color.GRAY,// "#55000000"
                8, // 渐变层数
                Gravity.TOP);
        Utils.setBackgroundDrawable(findViewById(R.id.shadow), shadow);

        // 初始化抽屉
        FragmentTransaction t = this.getSupportFragmentManager().beginTransaction();
        DrawerFragment drawFrag = DrawerFragment.getInstance(thisActivityCtx);
        t.replace(R.id.drawer_content_holder, drawFrag);
        t.commit();

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        mActionBar = getSupportActionBar();
        mActionBar.setDisplayHomeAsUpEnabled(true);
        // mActionBar.setBackgroundDrawable(new ColorDrawable(this.getResources().getColor(R.color.primary)));

        // 抽屉的初始化--start
        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer);
        mDrawerToggle =
                new ActionBarDrawerToggle(this, mDrawerLayout, toolbar, R.string.drawer_open, R.string.drawer_close) {

                    @Override
                    public void onDrawerSlide(View drawerView, float slideOffset) {
                        super.onDrawerSlide(drawerView, slideOffset);
                        // ViewHelper.setAlpha(MenuItemCompat.getActionView(searchMenuItem), 1 - slideOffset);
                        ViewHelper.setAlpha(toolbar, 1 - slideOffset);
                    }

                    /** Called when a drawer has settled in a completely closed state. */
                    @Override
                    public void onDrawerClosed(View view) {
                        super.onDrawerClosed(view);

                        searchViewEdt.setFocusable(true);
                        searchViewEdt.setFocusableInTouchMode(true);
                        /*
                         * if (!MenuItemCompat.isActionViewExpanded(searchMenuItem)) { switch (mPager.getCurrentItem())
                         * { case Constants.USR_APPS_TAB_POS: searchMenuItem.setVisible(true);
                         * sortMenuItem.setVisible(true); break; case Constants.SYS_APPS_TAB_POS:
                         * searchMenuItem.setVisible(true); break; case Constants.BACKUP_APPS_TAB_POS:
                         * searchMenuItem.setVisible(true); sortMenuItem.setVisible(true); break; }
                         * 
                         * // sortMenuItem.setVisible(true); }
                         */

                        if (mPager.getCurrentItem() != Constants.USR_APPS_TAB_POS) {
                            mDrawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED);
                        }

                        /*
                         * //抽屉里面的一项被点击了 if(mDrawerItemClickEvent != null){ switch (mDrawerItemClickEvent.DrawerItem) {
                         * case REFRESH: new GetApps().execute(true); break; } mDrawerItemClickEvent = null; }
                         */

                        // invalidateOptionsMenu(); // creates call to onPrepareOptionsMenu()
                    }

                    /** Called when a drawer has settled in a completely open state. */
                    @Override
                    public void onDrawerOpened(View drawerView) {
                        super.onDrawerOpened(drawerView);

                        hideKeyboard();
                        searchViewEdt.clearFocus();
                        searchViewEdt.setFocusable(false);
                        mDrawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_UNLOCKED);
                        /*
                         * if (!MenuItemCompat.isActionViewExpanded(searchMenuItem)) { searchMenuItem.setVisible(false);
                         * sortMenuItem.setVisible(false); }
                         */
                        // invalidateOptionsMenu(); // creates call to onPrepareOptionsMenu()
                    }
                };
        // mDrawerToggle.syncState();
        mDrawerLayout.setDrawerListener(mDrawerToggle);
        // 抽屉的初始化--end

        Fragmentlist = new ArrayList<BaseFrag>();
        usrAppsFrg = UserAppsTabFragment.getInstance(thisActivityCtx);
        sysAppsFrg = SysAppsTabFragment.getInstance(thisActivityCtx);
        backupAppsFrg = BackupAppTabFragmentV2.getInstance(thisActivityCtx);
        // backupAppsFrg = BackupAppTabFragment.getInstance(thisActivityCtx);

        Fragmentlist.add(usrAppsFrg);
        Fragmentlist.add(sysAppsFrg);
        Fragmentlist.add(backupAppsFrg);

        mPager = (MyViewPager) findViewById(R.id.pager);
        mPager.setOffscreenPageLimit(2);
        mFragAdapter = new AppListFragAdapter(this.getSupportFragmentManager(), Fragmentlist);

        /*
         * pager.setOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener() {
         * 
         * @Override public void onPageSelected(int position) { super.onPageSelected(position); if(position ==
         * Constants.USR_APPS_TAB_POS){ mDrawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_UNLOCKED); } else{
         * mDrawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED); } } });
         */
        mPager.setAdapter(mFragAdapter);

        // 导航条初始化--start
        mPagerSlidingTabStrip = (PagerSlidingTabStrip) findViewById(R.id.tabs);
        mPagerSlidingTabStrip.setOnPageChangeListener(new OnPageChangeListener() {

            @Override
            public void onPageSelected(int position) {
                int curPage = mPager.getCurrentItem();
                // 停止之前list的滑动
                Fragmentlist.get(oldPagePos).getListView().smoothScrollBy(0, 0);

                // 禁止在别的tab下从屏幕边缘划出，但是还是可以通过menu按键呼出抽屉，
                // 所以在open的时候记得要解锁
                if (position == Constants.USR_APPS_TAB_POS) {
                    mDrawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_UNLOCKED);
                } else {
                    mDrawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED);
                }

                if (position == Constants.USR_APPS_TAB_POS) {
                    if (MenuItemCompat.isActionViewExpanded(searchMenuItem)) {
                        sortMenuItem.setVisible(false);
                    } else {
                        processSortMenuIcon(Utils.getUserAppListSortType(thisActivityCtx));
                        sortMenuItem.setVisible(true);
                    }
                } else if (position == Constants.SYS_APPS_TAB_POS) {
                    sortMenuItem.setVisible(false);
                } else if (position == Constants.BACKUP_APPS_TAB_POS) {
                    if (MenuItemCompat.isActionViewExpanded(searchMenuItem)) {
                        sortMenuItem.setVisible(false);
                    } else {
                        processSortMenuIcon(Utils.getBackUpAppListSortType(thisActivityCtx));
                        sortMenuItem.setVisible(true);
                    }
                }

                oldPagePos = curPage;
            }

            @Override
            public void onPageScrolled(int arg0, float arg1, int arg2) {
            }

            @Override
            public void onPageScrollStateChanged(int arg0) {
            }
        });
        mPagerSlidingTabStrip.setOnTabClickListener(new OnTabClickListener() {

            @Override
            public boolean onTabClick(int position) {
                // TODO Auto-generated method stub
                if (mPager.getCurrentItem() == position) {
                    switch (mPager.getCurrentItem()) {
                        case Constants.USR_APPS_TAB_POS:
                            usrAppsFrg.listBackToTop();
                            break;
                        case Constants.SYS_APPS_TAB_POS:
                            sysAppsFrg.listBackToTop();
                            break;
                        case Constants.BACKUP_APPS_TAB_POS:
                            backupAppsFrg.listBackToTop();
                            break;
                    // case ALL_APPS_TAB_POS:
                    //
                    // break;
                    }
                    return true;
                }

                if (isInActionMode()) {
                    return true;
                }

                return false;

            }
        });
        mPagerSlidingTabStrip.setViewPager(mPager);
        // 选中的文字颜色
        // mPagerSlidingTabStrip.setSelectedTextColorResource(R.color.white);
        // 正常文字颜色
        // mPagerSlidingTabStrip.setTextColorResource(R.color.white3);

        mPagerSlidingTabStrip.setTextColorList(getResources().getColorStateList(R.color.srtipe_tab_sel));

        // 导航条初始化--end

        mAppUpdateReceiver = new AppUpdateReceiver();
        AppIntentFilter = new IntentFilter();
        AppIntentFilter.addAction(Intent.ACTION_PACKAGE_ADDED);
        AppIntentFilter.addAction(Intent.ACTION_PACKAGE_REMOVED);
        if (Constants.HANDLE_PKG_CHG) {
            AppIntentFilter.addAction(Intent.ACTION_PACKAGE_CHANGED);
        }
        AppIntentFilter.addDataScheme("package");
        registerReceiver(mAppUpdateReceiver, AppIntentFilter);

        LangIntentFilter = new IntentFilter();
        LangIntentFilter.addAction(Intent.ACTION_LOCALE_CHANGED);

        mLangUpdateReceiver = new LangUpdateReceiver();
        registerReceiver(mLangUpdateReceiver, LangIntentFilter);

        // http://www.cnblogs.com/crazywenza/archive/2013/01/07/2848913.html
        mSdcardUpdateReceiver = new SdcardUpdateReceiver();
        sdcardIntentFilter = new IntentFilter();
        sdcardIntentFilter.addAction(Intent.ACTION_MEDIA_EJECT);
        sdcardIntentFilter.addAction(Intent.ACTION_MEDIA_MOUNTED);
        sdcardIntentFilter.addAction(Intent.ACTION_MEDIA_REMOVED);
        sdcardIntentFilter.addAction(Intent.ACTION_MEDIA_BAD_REMOVAL);
        sdcardIntentFilter.addDataScheme("file");
        registerReceiver(mSdcardUpdateReceiver, sdcardIntentFilter);

        new GetApps().execute(false);
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        // Sync the toggle state after onRestoreInstanceState has occurred.
        mDrawerToggle.syncState();
        SwipeBackUtils.convertActivityFromTranslucent(this);
    }

    public static boolean isInActionMode() {
        if (sActionMode == null)
            return false;
        else
            return true;
    }

    @Override
    public void onBackPressed() {
        if (isInActionMode()) {
            super.onBackPressed();
        } else if (back_pressed + 2000 > System.currentTimeMillis()) {
            super.onBackPressed();
        } else {
            // 抽屉开
            if (mDrawerLayout.isDrawerOpen(GravityCompat.START)) {
                mDrawerLayout.closeDrawer(GravityCompat.START);
            }
            // searchview开
            else if (MenuItemCompat.isActionViewExpanded(searchMenuItem)) {
                MenuItemCompat.collapseActionView(searchMenuItem);
            } else {
                back_pressed = System.currentTimeMillis();
                toast.setText(R.string.press_back_again_to_exit);
                toast.show();
            }
        }
    }

    private void appDestroyCleanup() {
        if (progDialog != null && progDialog.isShowing()) {
            progDialog.dismiss();
        }

        SysAppFullList.clear();
        UserAppFullList.clear();
        unregisterReceivers();
        EventBus.getDefault().unregister(this);
        AppUpdateStaticReceiver.handleEvent = true;
        toast = null;
        sActionMode = null;
        mSnackbar = null;
        thisActivityCtx = null;
        mDrawerLayout = null;
        sIsSdcardReady = false;
        DaoUtils.destroy();
    }

    /**
     * 默认是多行的
     * 
     * @param newInstance
     * @return
     */
    public static Snackbar getSnackbar(boolean newInstance) {
        if (mSnackbar == null || newInstance) {
            mSnackbar = Snackbar.with(thisActivityCtx);
        }
        return mSnackbar;
    }

    public static void dismissSnackbar() {
        if (mSnackbar != null) {
            mSnackbar.dismiss();
        }
    }

    private void registerReceivers() {
        registerReceiver(mAppUpdateReceiver, AppIntentFilter);
        registerReceiver(mLangUpdateReceiver, LangIntentFilter);
        registerReceiver(mSdcardUpdateReceiver, sdcardIntentFilter);
    }

    private void unregisterReceivers() {
        try {
            unregisterReceiver(mAppUpdateReceiver);
            unregisterReceiver(mLangUpdateReceiver);
            unregisterReceiver(mSdcardUpdateReceiver);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void T(String msg) {
        toast.setText(msg);
        toast.show();
    }

    public static void T(int msgId) {
        toast.setText(msgId);
        toast.show();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.activity_menu, menu);

        searchMenuItem = menu.findItem(R.id.menu_search).setVisible(true);
        sortMenuItem = menu.findItem(R.id.menu_sort).setVisible(true);
        processSortMenuIcon(Utils.getUserAppListSortType(thisActivityCtx));

        SearchView searchView = (SearchView) MenuItemCompat.getActionView(searchMenuItem);
        searchView.setQueryHint(getString(R.string.search));

        // //Get the search bar Linearlayout
        LinearLayout searchBar = (LinearLayout) searchView.findViewById(android.support.v7.appcompat.R.id.search_bar);
        //
        // //Give the Linearlayout a transition animation.searchview显示动画
        // searchBar.setLayoutTransition(new LayoutTransition());

        searchViewEdt = ((EditText) searchView.findViewById(android.support.v7.appcompat.R.id.search_src_text));
        try {// 设置光标颜色
             // https://github.com/android/platform_frameworks_base/blob/kitkat-release/core/java/android/widget/TextView.java#L562-564
            Field f = TextView.class.getDeclaredField("mCursorDrawableRes");
            f.setAccessible(true);
            f.set(searchViewEdt, R.drawable.cursor_white);
        } catch (Exception ignored) {
        }

        searchViewEdt.setHintTextColor(getResources().getColor(R.color.white));
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
                String mCurFilter = !TextUtils.isEmpty(newText) ? newText.toLowerCase(Locale.getDefault()) : null;

                switch (mPager.getCurrentItem()) {
                    case Constants.USR_APPS_TAB_POS:
                        usrAppsFrg.filterList(mCurFilter);
                        break;
                    case Constants.SYS_APPS_TAB_POS:
                        sysAppsFrg.filterList(mCurFilter);
                        break;
                    case Constants.BACKUP_APPS_TAB_POS:
                        backupAppsFrg.filterList(mCurFilter);
                        break;
                // case ALL_APPS_TAB_POS:
                //
                // break;
                }
                return true;
            }
        });
        MenuItemCompat.setOnActionExpandListener(searchMenuItem, new MenuItemCompat.OnActionExpandListener() {

            @Override
            public boolean onMenuItemActionExpand(MenuItem item) {
                // TODO Auto-generated method stub
                sortMenuItem.setVisible(false);
                return true;
            }

            @Override
            public boolean onMenuItemActionCollapse(MenuItem item) {
                // TODO Auto-generated method stub

                switch (mPager.getCurrentItem()) {
                    case Constants.USR_APPS_TAB_POS:
                    case Constants.BACKUP_APPS_TAB_POS:
                        sortMenuItem.setVisible(true);
                        break;
                    case Constants.SYS_APPS_TAB_POS:

                        break;
                // case ALL_APPS_TAB_POS:
                //
                // break;
                }

                usrAppsFrg.filterList(null);
                if (!TextUtils.isEmpty(searchViewEdt.getText())) {
                    usrAppsFrg.collapseLastOpenItem(false);
                }

                sysAppsFrg.filterList(null);
                backupAppsFrg.filterList(null);
                return true;
            }
        });

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        switch (id) {

            case R.id.menu_sort:

                SortTypeDialog = new SortTypeDialogFragment();
                Bundle bundle = new Bundle();
                if (mPager.getCurrentItem() == Constants.USR_APPS_TAB_POS) {
                    bundle.putInt(SortTypeDialogFragment.SELECTED_ITEM, usrAppsFrg.getListSortType());
                } else if (mPager.getCurrentItem() == Constants.BACKUP_APPS_TAB_POS) {
                    bundle.putInt(SortTypeDialogFragment.SELECTED_ITEM, backupAppsFrg.getListSortType());
                }
                SortTypeDialog.setArguments(bundle);
                SortTypeDialog.show(getSupportFragmentManager(), SortTypeDialogFragment.DialogTag);

                return true;
        }
        /*
         * if (id == R.id.action_settings) { return true; }
         */
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
        // Save UI state changes to the savedInstanceState.
        // This bundle will be passed to onCreate, onCreateView, and
        // onCreateView if the parent Activity is killed and restarted.
        // super.onSaveInstanceState(savedInstanceState);
    }

    @Override
    protected void onDestroy() {
        // TODO Auto-generated method stub
        super.onDestroy();
        appDestroyCleanup();
    }

    private class MoveAppsToNewPath extends AsyncTask<Void, Integer, Void> {
        private String oldPath = "";
        private String newPath = "";
        private String curFileName = "";
        private int totalFileSize = 0;
        private int curFileNum = 0;

        public MoveAppsToNewPath(String old_path, String new_path) {
            oldPath = old_path;
            newPath = new_path;
        }

        @Override
        protected void onPreExecute() {
            progDialog =
                    new MyProgressDialog(MainActivity.this, getString(R.string.moving_akps),
                            getString(R.string.moving_akps));
            progDialog.setCancelable(false);
            progDialog.setArcProgressMax(100);
            progDialog.setArcBottomText(getString(R.string.moving_akps));
            progDialog.show();
        }

        @Override
        protected void onProgressUpdate(Integer...values) {
            // TODO Auto-generated method stub
            super.onProgressUpdate(values);
            if (!TextUtils.isEmpty(curFileName)) {
                progDialog.setDialogText(curFileName);
            }

            int percentage = (int) (((float) curFileNum / (float) totalFileSize) * 100f);
            progDialog.setArcProgress(percentage);
            progDialog.setArcBottomText(curFileNum + "/" + totalFileSize);
        }

        @Override
        protected Void doInBackground(Void...params) {
            // TODO Auto-generated method stub
            File oldDir = new File(oldPath);
            File newDir = new File(newPath);
            File[] files = oldDir.listFiles(new ApkFileFilter());
            if (files != null) {
                totalFileSize = files.length;
                for (File fileEntry : files) {
                    try {
                        curFileNum++;
                        FileUtils.moveFileToDirectory(fileEntry, newDir, true);
                        // FileUtils.copyFileToDirectory(fileEntry, newDir);
                        curFileName = fileEntry.getName();
                        publishProgress();
                    } catch (IOException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }
                }
            }

            /*
             * try { //FileUtils.copyDirectory(new File(oldPath), new File(newPath));
             * FileUtils.cleanDirectory(oldDir);//.deleteDirectory(new File(ev.oldPath)); } catch (IOException e) { //
             * TODO Auto-generated catch block e.printStackTrace(); }
             */

            return null;
        }

        @Override
        protected void onCancelled() {
        }

        // can use UI thread here
        @Override
        protected void onPostExecute(final Void unused) {
            if (progDialog.isShowing()) {
                progDialog.dismiss();
            }
            backupAppsFrg.forceReLoad();
            toast.setText(R.string.move_success);
            toast.show();
        }

    }

    private class GetApps extends AsyncTask<Boolean, Integer, Void> {
        private List<PackageInfo> packages;
        private PackageManager pManager;
        private String curAppName;
        private int fullAppListSize;

        @Override
        protected void onProgressUpdate(Integer...values) {
            // TODO Auto-generated method stub
            super.onProgressUpdate(values);
            if (!TextUtils.isEmpty(curAppName)) {
                progDialog.setDialogText(curAppName);
            }

            int percentage = (int) (((float) values[0] / (float) fullAppListSize) * 100f);
            progDialog.setArcProgress(percentage);
            progDialog.setArcBottomText(values[0] + "/" + fullAppListSize);
        }

        @Override
        protected void onPreExecute() {
            unregisterReceivers();

            // DismissDialog(SortTypeDialog);
            // DismissDialog(UserAppListMoreActionDialog);
            // DismissDialog(SelectionDialog);

            pManager = getPackageManager();
            packages = pManager.getInstalledPackages(0);
            fullAppListSize = packages.size();

            progDialog =
                    new MyProgressDialog(MainActivity.this, getString(R.string.loading_apps),
                            getString(R.string.loading_apps));
            progDialog.setCancelable(false);
            progDialog.setArcProgressMax(100);
            progDialog.setArcBottomText("" + fullAppListSize);
            progDialog.show();
        }

        @Override
        protected Void doInBackground(Boolean...params) {
            // 用于显示用户app的list
            UserAppFullList.clear();
            // 用于显示系统app的list
            SysAppFullList.clear();
            mSectionInListPosMap.clear();
            SysAppFullListWapper.clear();

            // PackageManager pManager = getPackageManager();
            // List<PackageInfo> packages = pManager.getInstalledPackages(0);

            // List<ApplicationInfo> apps = pManager.getInstalledApplications(
            // PackageManager.GET_UNINSTALLED_PACKAGES |
            // PackageManager.GET_DISABLED_COMPONENTS);
            sIsSdcardReady = Utils.getAppIconCacheDir(thisActivityCtx) == null ? false : true;
            if (!sIsSdcardReady) {
                PackageInfo packageInfo;
                AppInfo tmpInfo;
                for (int i = 0; i < fullAppListSize; i++) {
                    Log.i("ttt", "processing app " + (i + 1) + " / " + fullAppListSize);
                    packageInfo = packages.get(i);
                    tmpInfo = Utils.buildAppInfoEntry(thisActivityCtx, packageInfo, pManager, false, true);
                    if (tmpInfo.isSysApp) {
                        SysAppFullList.add(tmpInfo);
                    } else {
                        UserAppFullList.add(tmpInfo);
                    }
                    curAppName = tmpInfo.appName;
                    publishProgress(i + 1);
                }
            } else {
                boolean deleteAllAppInfoDone = false;
                if (params[0]) {
                    DaoUtils.deleteAllAppInfo(thisActivityCtx);
                    deleteAllAppInfoDone = true;
                }
                DaoSession appInfoSession = DaoUtils.getDaoSession(MainActivity.this, true);
                if (Utils.isAppListInDb(MainActivity.this)) {
                    //UserAppFullList
                    ArrayList<AppInfo> UserAppFullListInDB = 
                            (ArrayList<AppInfo>) appInfoSession.getAppInfoDao().queryBuilder()
                                    .where(Properties.IsSysApp.eq("false")).list();
                    //SysAppFullList
                    ArrayList<AppInfo> SysAppFullListInDB = 
                            (ArrayList<AppInfo>) appInfoSession.queryBuilder(AppInfo.class)
                                    .where(Properties.IsSysApp.eq("true")).list();

                    boolean foundMyself = false;
                    for(int i = 0 ; i < fullAppListSize ; i++) {
                        PackageInfo thePackageInfo = packages.get(i);
                        boolean thePackageInfoFound = false;
                        int posInList = -1;
                        publishProgress((i + 1));
                        
                        if ((thePackageInfo.applicationInfo.flags & ApplicationInfo.FLAG_SYSTEM) == 0) {
                           //user app
                            posInList = Utils.isAppInfoInList(thePackageInfo, UserAppFullListInDB);
                            if(posInList != -1){
                                thePackageInfoFound = true;
                                if(Constants.MY_PACKAGE_NAME.equals(thePackageInfo.packageName)) {
                                    foundMyself = true;
                                    //重新生成自己
                                    UserAppFullList.add(Utils.buildAppInfoEntry(thisActivityCtx, Constants.MY_PACKAGE_NAME));
                                }
                                else{
                                    UserAppFullList.add(UserAppFullListInDB.get(posInList));
                                }
                                UserAppFullListInDB.remove(posInList);
                            }
                        } 
                        else// sys app
                        {
                            posInList = Utils.isAppInfoInList(thePackageInfo, SysAppFullListInDB);
                            if(posInList != -1){
                                thePackageInfoFound = true;
                                SysAppFullList.add(SysAppFullListInDB.get(posInList));
                                SysAppFullListInDB.remove(posInList);
                            }
                        }

                        if(!thePackageInfoFound) {
                            //建立appInfo并且插入数据库
                            AppInfo tmpInfo = Utils.buildAppInfoEntry(thisActivityCtx, thePackageInfo, pManager, true, false);
                            try {
                                // appInfoSession.insertOrReplace(tmpInfo);
                                appInfoSession.insert(tmpInfo);
                            } catch (Exception e) {
                                Log.e("ttt", "app error: " + tmpInfo.appName + ", error: " + e.toString());
                            } finally {
                                tmpInfo.appIconBytes = null;
                            }
                            
                            if (tmpInfo.isSysApp) {
                                SysAppFullList.add(tmpInfo);
                            } else {
                                if(Constants.MY_PACKAGE_NAME.equals(thePackageInfo.packageName)) {
                                    foundMyself = true;
                                }
                                UserAppFullList.add(tmpInfo);
                            }
                        }
                    }
                    
                    if(!foundMyself){
                        UserAppFullList.add(Utils.buildAppInfoEntry(thisActivityCtx, Constants.MY_PACKAGE_NAME));
                    }
                    
                    DaoUtils.deleteAppinfo(thisActivityCtx, UserAppFullListInDB);
                    DaoUtils.deleteAppinfo(thisActivityCtx, SysAppFullListInDB);
                    UserAppFullListInDB = null;
                    SysAppFullListInDB = null;
                    
                } else {
                    PackageInfo packageInfo;
                    AppInfo tmpInfo;

                    if (!deleteAllAppInfoDone) {
                        appInfoSession.getAppInfoDao().deleteAll();
                        Utils.deleteAppIconDir(Utils.getAppIconCacheDir(thisActivityCtx));
                    }

                    for (int i = 0; i < fullAppListSize; i++) {

                        // publishProgress("" + (i + 1));
                        Log.i("ttt", "processing app " + (i + 1) + " / " + fullAppListSize);
                        packageInfo = packages.get(i);
                        tmpInfo = Utils.buildAppInfoEntry(thisActivityCtx, packageInfo, pManager, true, false);
                        if (tmpInfo.isSysApp) {
                            SysAppFullList.add(tmpInfo);
                        } else {
                            UserAppFullList.add(tmpInfo);
                        }
                        curAppName = tmpInfo.appName;
                        publishProgress((i + 1));
                        try {
                            // appInfoSession.insertOrReplace(tmpInfo);
                            appInfoSession.insert(tmpInfo);
                        } catch (Exception e) {
                            Log.e("ttt", "app error: " + tmpInfo.appName + ", error: " + e.toString());
                        } finally {
                            tmpInfo.appIconBytes = null;
                        }
                    }
                    Utils.setAppListInDb(thisActivityCtx, true);
                }
                appInfoSession.clear();
            }

            // 用户程序排序
            Utils.sortUserAppList(thisActivityCtx, UserAppFullList);

            // build 系统applist
            TreeMap<String, ArrayList<AppInfo>> sectionItemsTreeMap =
                    new TreeMap<String, ArrayList<AppInfo>>(new SharpStringComparator());

            AppInfo curAppInfo;
            String curSectionStr = "";
            ArrayList<AppInfo> sectionItemsTmp;
            for (int i = 0; i < SysAppFullList.size(); i++) {
                curAppInfo = SysAppFullList.get(i);
                // curSectionStr = curAppInfo.appName.substring(0, 1).toUpperCase(Locale.getDefault());
                curSectionStr = Utils.GetFirstChar(curAppInfo.appName);// .substring(0,
                                                                       // 1).toUpperCase(Locale.getDefault());
                if (!Character.isLetter(curSectionStr.charAt(0)))// 其他的开始的字母，放入#未分类
                {
                    curSectionStr = "#";
                }

                // 新代码
                if (sectionItemsTreeMap.get(curSectionStr) == null) {
                    sectionItemsTmp = new ArrayList<AppInfo>();
                    sectionItemsTmp.add(curAppInfo);
                    sectionItemsTreeMap.put(curSectionStr, sectionItemsTmp);
                } else {
                    sectionItemsTreeMap.get(curSectionStr).add(curAppInfo);
                }

            }

            // 确保#在最后

            // 新代码
            AppPinYinComparator mAppPinYinComparator = new AppPinYinComparator();
            Iterator<Entry<String, ArrayList<AppInfo>>> itera = sectionItemsTreeMap.entrySet().iterator();
            int posCtr = 0;
            while (itera.hasNext()) {
                Map.Entry<String, ArrayList<AppInfo>> entry = (Map.Entry<String, ArrayList<AppInfo>>) itera.next();
                String sectionText = entry.getKey();
                // key作为list section
                SysAppFullListWapper.add(new SysAppListItem(SysAppListItem.LIST_SECTION, sectionText, null));
                mSectionInListPosMap.put(sectionText, posCtr);
                ArrayList<AppInfo> sectionItemsList = (ArrayList<AppInfo>) entry.getValue();
                Collections.sort(sectionItemsList, mAppPinYinComparator);
                posCtr += sectionItemsList.size() + 1;
                for (int i = 0, size = sectionItemsList.size(); i < size; i++) {
                    SysAppFullListWapper.add(new SysAppListItem(SysAppListItem.APP_ITEM, sectionText, sectionItemsList
                            .get(i)));
                }
            }
            sectionItemsTreeMap.clear();
            sectionItemsTreeMap = null;
            // appInfoSession.clear();
            System.gc();
            return null;
        }

        @Override
        protected void onCancelled() {
        }

        // can use UI thread here
        @Override
        protected void onPostExecute(final Void unused) {
            if (progDialog.isShowing()) {
                progDialog.dismiss();
            }

            // list设置数据
            usrAppsFrg.setListSortType(Utils.getUserAppListSortType(thisActivityCtx));
            usrAppsFrg.setData(UserAppFullList);

            sysAppsFrg.setData(SysAppFullListWapper, mSectionInListPosMap);

            mPagerSlidingTabStrip.notifyDataSetChanged();
            registerReceivers();
        }
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        if (ev.getAction() == MotionEvent.ACTION_DOWN) {
            final View currentFocus = getCurrentFocus();
            if (!(currentFocus instanceof AutoCompleteTextView) || !isTouchInsideView(ev, currentFocus)) {// AutoCompleteTextView
                // ((InputMethodManager) getApplicationContext().getSystemService(Context.INPUT_METHOD_SERVICE))
                // .hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
                hideKeyboard();
            }
        }

        return super.dispatchTouchEvent(ev);
    }

    private boolean isTouchInsideView(final MotionEvent ev, final View currentFocus) {
        final int[] loc = new int[2];
        currentFocus.getLocationOnScreen(loc);
        return ev.getRawX() > loc[0] && ev.getRawY() > loc[1] && ev.getRawX() < (loc[0] + screenWidth)// (loc[0] +
                                                                                                      // currentFocus.getWidth())
                && ev.getRawY() < (loc[1] + currentFocus.getHeight());
    }

    private void hideKeyboard() {
        ((InputMethodManager) getApplicationContext().getSystemService(Context.INPUT_METHOD_SERVICE))
                .hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        // TODO Auto-generated method stub
        // 停止list滚动
        // MotionEvent me = MotionEvent.obtain(SystemClock.uptimeMillis(), SystemClock.uptimeMillis(),
        // MotionEvent.ACTION_CANCEL, 0, 0, 0);
        // sysAppsFrg.getListView().dispatchTouchEvent(me);
        // me.recycle();

        if (keyCode == KeyEvent.KEYCODE_MENU && event.getRepeatCount() == 0 && !isInActionMode()) {
            toggleDrawerMenu();
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        mDrawerToggle.onConfigurationChanged(newConfig);

    }

    private void toggleDrawerMenu() {
        if (mDrawerLayout.isDrawerOpen(GravityCompat.START)) {
            closeDrawerMenu();
        } else {
            openDrawerMenu();
        }
    }

    private void closeDrawerMenu() {
        mDrawerLayout.closeDrawer(GravityCompat.START);
    }

    private void openDrawerMenu() {
        mDrawerLayout.openDrawer(GravityCompat.START);
    }

    private void lockDrawerMenuClosed() {
        mDrawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED);
    }

    private void unlockDrawerMenu() {
        mDrawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_UNLOCKED);
    }

    // 用户app搜索之后
    /*
     * @Override public void onUserAppListFilterResultPublish(ArrayList<AppInfo> resultsList) { // TODO Auto-generated
     * method stub updateSlidingTabTitle(Constants.USR_APPS_TAB_POS); }
     */

    /*
     * @Override public void onSysAppListFilterResultPublish(ArrayList<SysAppListItem> ResultSysAppList, TreeMap<String,
     * Integer> ResultPosMap) { // TODO Auto-generated method stub updateSlidingTabTitle(Constants.SYS_APPS_TAB_POS);
     * sysAppsFrg.setExistIndexArray(new ArrayList<String>(ResultPosMap.keySet())); }
     */

    // private void updateSlidingTabTitle(int position, int appNum) {
    // String newTitle = "";
    // switch (position) {
    // case Constants.USR_APPS_TAB_POS:
    // newTitle = getString(R.string.user_apps) + " (" + appNum + ")";
    // mPagerSlidingTabStrip.setTitleForChild(position, newTitle);
    // break;
    // case Constants.SYS_APPS_TAB_POS:
    // newTitle = getString(R.string.sys_apps) + " (" + appNum + ")";
    // mPagerSlidingTabStrip.setTitleForChild(position, newTitle);
    // break;
    // case Constants.BACKUP_APPS_TAB_POS:
    // newTitle = getString(R.string.backup_apps) + " (" + appNum + ")";
    // mPagerSlidingTabStrip.setTitleForChild(position, newTitle);
    // break;
    // }
    // }

    private void updateSlidingTabTitle(int position) {
        switch (position) {
            case Constants.USR_APPS_TAB_POS:
                mPagerSlidingTabStrip.setTitleForChild(position, usrAppsFrg.getFragmentTitle());
                break;
            case Constants.SYS_APPS_TAB_POS:
                mPagerSlidingTabStrip.setTitleForChild(position, sysAppsFrg.getFragmentTitle());
                break;
            case Constants.BACKUP_APPS_TAB_POS:
                mPagerSlidingTabStrip.setTitleForChild(position, backupAppsFrg.getFragmentTitle());
                break;
        }
    }

    // eventbus 处理
    public void onEventMainThread(DrawerItemClickEvent ev) {
        closeDrawerMenu();
        // mDrawerItemClickEvent = ev;
        switch (ev.DrawerItem) {
            case REFRESH:
                new GetApps().execute(true);
                // also refresh backupTab
                /*
                 * if(backupAppsFrg.isLoadingRunning()){ backupAppsFrg.cancelLoading(); } backupAppsFrg.forceReLoad();
                 */
                break;
            case SETTINGS:
                break;
            case CHG_BACKUP_DIR:

                if (backupAppsFrg.isLoadingRunning()) {
                    backupAppsFrg.cancelLoading();
                }
                new MoveAppsToNewPath(ev.oldPath, ev.newPath).execute();
                /*
                 * try { FileUtils.copyDirectory(new File(ev.oldPath), new File(ev.newPath));
                 * FileUtils.cleanDirectory(new File(ev.oldPath));//.deleteDirectory(new File(ev.oldPath)); } catch
                 * (IOException e) { // TODO Auto-generated catch block e.printStackTrace(); }
                 * 
                 * backupAppsFrg.startLoading();
                 */

                break;
        }
    }

    public void onEventMainThread(ActionModeToggleEvent ev) {

        if (ev.isInActionMode) {
            mPager.setPagingEnabled(false);
            mDrawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED);
        } else {
            mPager.setPagingEnabled(true);
            if (mPager.getCurrentItem() == Constants.USR_APPS_TAB_POS) {
                mDrawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_UNLOCKED);
            }
        }

    }

    public void onEventMainThread(ViewNewBackupAppEvent ev) {
        mPager.setCurrentItem(Constants.BACKUP_APPS_TAB_POS);
    }

    public void onEventMainThread(BackupAppEvent ev) {
        ArrayList<AppInfo> list = ev.appList;
        long requiredSpace = Utils.calculateTotalFileRawSize(list);
        if (Utils.isSdcardSpaceEnough(requiredSpace)) {
            new BackUpApps(ev.appList, ev.sendAfterBackup).execute();
        } else {
            T(R.string.no_enough_space);
        }

    }

    private class AppUpdateReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            // TODO Auto-generated method stub
            closeDrawerMenu();
            DismissAllDialog();
            // usrAppsFrg.collapseLastOpenItem(false);

            if (intent.getAction().equals(Intent.ACTION_PACKAGE_ADDED)) {
                // app被安装
                String NewPkgName = intent.getDataString().substring(8);
                AppInfo newAppInfo = null;
                if (DaoUtils.getByPackageName(thisActivityCtx, NewPkgName) != null)// 安装过--更新
                {
                    // verify user list
                    for (int i = 0, size = UserAppFullList.size(); i < size; i++) {
                        if (UserAppFullList.get(i).getPackageName().equals(NewPkgName)) {

                            DaoUtils.deleteAppInfo(thisActivityCtx, UserAppFullList.get(i));

                            AppInfo tmpAppInfo = Utils.buildAppInfoEntry(thisActivityCtx, NewPkgName);
                            DaoUtils.insert(thisActivityCtx, tmpAppInfo);

                            newAppInfo = DaoUtils.getByPackageName(thisActivityCtx, NewPkgName);
                            UserAppFullList.set(i, newAppInfo);

                            // ((ActionSlideExpandableListView) usrAppsFrg.getListView()).collapse(false);
                            usrAppsFrg.notifyDataSetChanged();
                            break;
                        }
                    }

                    // verify sys list
                    for (int i = 0; i < SysAppFullListWapper.size(); i++) {
                        if (SysAppFullListWapper.get(i).type == SysAppListItem.APP_ITEM) {
                            if (SysAppFullListWapper.get(i).appinfo.getPackageName().equals(NewPkgName)) {

                                DaoUtils.deleteAppInfo(thisActivityCtx, SysAppFullListWapper.get(i).appinfo);

                                AppInfo tmpAppInfo = Utils.buildAppInfoEntry(thisActivityCtx, NewPkgName);
                                tmpAppInfo.isSysApp = true;
                                DaoUtils.insert(thisActivityCtx, tmpAppInfo);

                                newAppInfo = DaoUtils.getByPackageName(thisActivityCtx, NewPkgName);
                                SysAppFullListWapper.get(i).appinfo = newAppInfo;

                                // ((ActionSlideExpandableListView) usrAppsFrg.getListView()).collapse(false);
                                sysAppsFrg.notifyDataSetChanged();
                                break;
                            }
                        }
                    }
                } else// 没有安装过, 重新来过
                {
                    // startRefreshAppInfoList();
                    newAppInfo = Utils.buildAppInfoEntry(thisActivityCtx, NewPkgName);
                    DaoUtils.insert(thisActivityCtx, newAppInfo);
                    UserAppFullList.add(newAppInfo);
                    Utils.sortUserAppList(thisActivityCtx, UserAppFullList);

                    // ((ActionSlideExpandableListView) usrAppsFrg.getListView()).collapse(false);
                    usrAppsFrg.notifyDataSetChanged();
                    // updateSlidingTabTitle(Constants.USR_APPS_TAB_POS);

                }
                toast.setText(getString(R.string.new_app_installed) + " "
                        + Utils.pkgNameToAppName(thisActivityCtx, NewPkgName));
                toast.show();
                EventBus.getDefault().post(new AppUpdateEvent(AppState.APP_ADDED, NewPkgName, newAppInfo));
                L.d("app---added: " + NewPkgName);
                // startRefreshAppInfoList();

            } else if (intent.getAction().equals(Intent.ACTION_PACKAGE_REMOVED)) {
                // app被删除
                toast.setText(R.string.app_removed);
                String RemovedPkgName = intent.getDataString().substring(8);
                ArrayList<AppInfo> tmpUserDisplayList = usrAppsFrg.getDisplayList();
                AppInfo targetAppInfo = null;
                for (int i = 0, size = UserAppFullList.size(); i < size; i++) {
                    if (UserAppFullList.get(i).packageName.equals(RemovedPkgName)) {
                        targetAppInfo = UserAppFullList.get(i);
                        DaoUtils.deleteAppInfo(thisActivityCtx, targetAppInfo);
                        toast.setText(getString(R.string.app_removed_name) + " " + targetAppInfo.appName);
                        UserAppFullList.remove(i);
                        usrAppsFrg.notifyDataSetChanged();
                    }
                    if (i < tmpUserDisplayList.size()) {
                        if (tmpUserDisplayList.get(i).packageName.equals(RemovedPkgName)) {
                            tmpUserDisplayList.remove(i);
                        }
                    }
                    if (targetAppInfo != null) {
                        break;
                    }
                }

                /*
                 * if(targetAppInfo == null){ //verify sys list for(int i = 0 ; i < SysAppFullListWapper.size();i++){
                 * if(SysAppFullListWapper.get(i).type == SysAppListItem.APP_ITEM){ if
                 * (SysAppFullListWapper.get(i).appinfo.getPackageName().equals(RemovedPkgName)) { targetAppInfo =
                 * SysAppFullListWapper.get(i).appinfo; //DaoUtils.deleteAppInfo(thisActivityCtx, targetAppInfo);
                 * toast.setText(getString(R.string.app_removed_name) + " " + targetAppInfo.appName);
                 * //SysAppFullListWapper.remove(i); sysAppsFrg.notifyDataSetChanged(); break; } } } }
                 */
                toast.show();
                EventBus.getDefault().post(new AppUpdateEvent(AppState.APP_REMOVED, RemovedPkgName, targetAppInfo));
                L.d("app---removed: " + RemovedPkgName);
                // updateSlidingTabTitle(Constants.USR_APPS_TAB_POS);
            } else if (intent.getAction().equals(Intent.ACTION_PACKAGE_CHANGED)) {
                String PkgName = intent.getDataString().substring(8);
                String appName = Utils.pkgNameToAppName(thisActivityCtx, PkgName);
                toast.setText("PACKAGE_CHANGED: " + appName);
                toast.show();
                EventBus.getDefault().post(new AppUpdateEvent(AppState.APP_CHANGED, PkgName, null));
                Log.i("ttt", "PACKAGE_CHANGED: " + PkgName);
                // startRefreshAppInfoList();
            }
        }
    }

    private class LangUpdateReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            // TODO Auto-generated method stub
            if (intent.getAction().compareTo(Intent.ACTION_LOCALE_CHANGED) == 0) {
                DaoUtils.deleteAllAppInfo(thisActivityCtx);
                finish();
            }
        }

    }

    private class SdcardUpdateReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            // TODO Auto-generated method stub
            String action = intent.getAction();
            T(R.string.sdcard_changed);
            finish();
            // if(action.equals(Intent.ACTION_MEDIA_EJECT)){
            // finish();
            // }else if(action.equals(Intent.ACTION_MEDIA_MOUNTED)){
            //
            // }
        }

    }

    @Override
    public void OnUserAppListDataSetChanged() {
        // TODO Auto-generated method stub
        updateSlidingTabTitle(Constants.USR_APPS_TAB_POS);
    }

    @Override
    public void OnSysAppListDataSetChanged(ArrayList<SysAppListItem> ResultSysAppList,
            TreeMap<String, Integer> ResultPosMap) {
        // TODO Auto-generated method stub
        sysAppsFrg.setExistIndexArray(new ArrayList<String>(ResultPosMap.keySet()));
        updateSlidingTabTitle(Constants.SYS_APPS_TAB_POS);
    }

    @Override
    public void OnBackupAppListDataSetChanged() {
        // TODO Auto-generated method stub
        updateSlidingTabTitle(Constants.BACKUP_APPS_TAB_POS);
    }

    private class BackUpApps extends AsyncTask<Void, String, Void> {
        private boolean errorHappened = false;
        private ArrayList<Uri> SnedApkUris = new ArrayList<Uri>();
        private ArrayList<String> FailedApp = new ArrayList<String>();
        private ArrayList<AppInfo> AppList;
        private int AppListSize;
        private ArrayList<AppInfo> SuccAppList = new ArrayList<AppInfo>();
        private boolean SendAfterBackup = false;
        private String BACK_UP_FOLDER;

        public BackUpApps(ArrayList<AppInfo> list, boolean sendAfterBackup) {
            AppList = list;
            AppListSize = list.size();
            SendAfterBackup = sendAfterBackup;
            BACK_UP_FOLDER = Utils.getBackUpAPKfileDir(thisActivityCtx);
        }

        @Override
        protected void onProgressUpdate(String...values) {
            // TODO Auto-generated method stub
            super.onProgressUpdate(values);
            // progDialog.setArcProgress(Integer.valueOf(values[0]));
            // progDialog.setMessage(getString(R.string.saving_app) + " " + values[1]);

            int percentage = (int) (((float) Integer.valueOf(values[0]) / (float) AppListSize) * 100f);
            progDialog.setArcProgress(percentage);
            progDialog.setArcBottomText(values[0] + "/" + AppListSize);
            progDialog.setDialogText(values[1]);
        }

        @Override
        protected void onPreExecute() {
            unregisterReceivers();

            progDialog =
                    new MyProgressDialog(MainActivity.this, getString(R.string.saving_apps),
                            getString(R.string.saving_apps));
            progDialog.setCancelable(false);
            progDialog.setArcProgressMax(100);
            progDialog.setArcBottomText("" + AppList.size());
            progDialog.show();
        }

        @Override
        protected Void doInBackground(Void...arg0) {
            // TODO Auto-generated method stub

            String[] dialogInfo = new String[2];
            int counter = 0;
            AppInfo tmpAppInfo = null;
            for (int i = 0; i < AppList.size(); i++) {
                tmpAppInfo = AppList.get(i);

                counter++;
                dialogInfo[0] = "" + counter;
                dialogInfo[1] = tmpAppInfo.appName;
                publishProgress(dialogInfo);

                String sdAPKfileName = Utils.BackupApp(tmpAppInfo, BACK_UP_FOLDER);
                if (sdAPKfileName != null) {
                    SuccAppList.add(tmpAppInfo);
                    if (SendAfterBackup) {
                        SnedApkUris.add(Uri.parse("file://" + sdAPKfileName));
                    }
                    Log.i("ttt", "appBackup succ: " + tmpAppInfo.appName);
                } else {
                    errorHappened = true;
                    FailedApp.add(tmpAppInfo.appName);
                    Log.i("ttt", "appBackup Fail: " + tmpAppInfo.appName);
                }

            }

            return null;
        }

        @Override
        protected void onCancelled() {
        }

        // can use UI thread here
        @Override
        protected void onPostExecute(final Void unused) {
            if (progDialog.isShowing()) {
                progDialog.dismiss();
            }

            if (errorHappened) {// 有错误发生
                String ErrorApp = "";
                for (int i = 0; i < FailedApp.size(); i++) {
                    ErrorApp += FailedApp.get(i) + "\n";
                }

                ErrorApp = ErrorApp.substring(0, ErrorApp.lastIndexOf("\n"));

                toast.setText(getString(R.string.error) + ":\n" + ErrorApp);
                toast.show();
            } else {// 没有错误发生
                if (SendAfterBackup) {
                    if (SnedApkUris.size() > 1) {
                        Utils.chooseSendByApp(thisActivityCtx, SnedApkUris);
                    } else {
                        Utils.chooseSendByApp(thisActivityCtx, SnedApkUris.get(0));
                    }
                } else {
                    toast.setText(R.string.backup_success);
                    toast.show();
                }
            }
            registerReceivers();
            if (SuccAppList.size() > 0) {
                if (sActionMode != null) {
                    sActionMode.finish();
                }
                EventBus.getDefault().post(new AppBackupSuccEvent(SuccAppList));
            }
        }
    }

    // 排序图表点击
    @Override
    public void onSortTypeListItemClick(DialogInterface dialog, int which) {
        // TODO Auto-generated method stub
        switch (mPager.getCurrentItem()) {
            case Constants.USR_APPS_TAB_POS:
                if (usrAppsFrg.getListSortType() == which)
                    return;
                Utils.setUserAppListSortType(thisActivityCtx, which);
                Utils.sortUserAppList(thisActivityCtx, UserAppFullList);
                processSortMenuIcon(which);

                usrAppsFrg.setListSortType(which);
                usrAppsFrg.collapseLastOpenItem(false);
                usrAppsFrg.notifyDataSetChanged();

                break;
            case Constants.SYS_APPS_TAB_POS:

                break;
            case Constants.BACKUP_APPS_TAB_POS:
                if (backupAppsFrg.getListSortType() == which)
                    return;
                Utils.setBackUpAppListSortType(thisActivityCtx, which);
                Utils.sortBackUpAppList(thisActivityCtx, backupAppsFrg.getAppList());
                processSortMenuIcon(which);

                backupAppsFrg.setListSortType(which);
                backupAppsFrg.notifyDataSetChanged();

                break;
        }
    }

    private void processSortMenuIcon(int which) {
        switch (which) {
            case SortTypeDialogFragment.LIST_SORT_TYPE_NAME_ASC:
                sortMenuItem.setIcon(R.drawable.name_asc);
                break;
            case SortTypeDialogFragment.LIST_SORT_TYPE_NAME_DES:
                sortMenuItem.setIcon(R.drawable.name_des);
                break;
            case SortTypeDialogFragment.LIST_SORT_TYPE_SIZE_ASC:
                sortMenuItem.setIcon(R.drawable.size_asc_white);
                break;
            case SortTypeDialogFragment.LIST_SORT_TYPE_SIZE_DES:
                sortMenuItem.setIcon(R.drawable.size_des_white);
                break;
            case SortTypeDialogFragment.LIST_SORT_TYPE_LAST_MOD_TIME_ASC:
                sortMenuItem.setIcon(R.drawable.time_asc);
                break;
            case SortTypeDialogFragment.LIST_SORT_TYPE_LAST_MOD_TIME_DES:
                sortMenuItem.setIcon(R.drawable.time_des);
                break;
        }
    }

    private void DismissAllDialog() {
        Utils.DismissDialog(SortTypeDialog);
    }
}
