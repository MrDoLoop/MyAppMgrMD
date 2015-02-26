package com.doloop.www.myappmgr.material;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.TreeMap;

import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.MenuItemCompat;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
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
import android.widget.Toast;

import com.doloop.www.mayappmgr.material.events.DrawerItemClickEvent;
import com.doloop.www.mayappmgr.material.events.ViewNewBackupAppEvent;
import com.doloop.www.myappmgr.material.adapters.AppListFragAdapter;
import com.doloop.www.myappmgr.material.adapters.BackupAppListAdapter.BackupAppListDataSetChangedListener;
import com.doloop.www.myappmgr.material.adapters.SysAppListAdapter.SysAppListDataSetChangedListener;
import com.doloop.www.myappmgr.material.adapters.UserAppListAdapter.UserAppListDataSetChangedListener;
import com.doloop.www.myappmgr.material.dao.AppInfo;
import com.doloop.www.myappmgr.material.dao.AppInfoDao.Properties;
import com.doloop.www.myappmgr.material.dao.DaoSession;
import com.doloop.www.myappmgr.material.dao.DaoUtils;
import com.doloop.www.myappmgr.material.fragments.BackupAppTabFragment;
import com.doloop.www.myappmgr.material.fragments.BaseFrag;
import com.doloop.www.myappmgr.material.fragments.DrawerFragment;
import com.doloop.www.myappmgr.material.fragments.SysAppsTabFragment;
import com.doloop.www.myappmgr.material.fragments.UserAppsTabFragment;
import com.doloop.www.myappmgr.material.utils.AppPinYinComparator;
import com.doloop.www.myappmgr.material.utils.Constants;
import com.doloop.www.myappmgr.material.utils.L;
import com.doloop.www.myappmgr.material.utils.SharpStringComparator;
import com.doloop.www.myappmgr.material.utils.SysAppListItem;
import com.doloop.www.myappmgr.material.utils.Utilities;
import com.doloop.www.myappmgr.material.widgets.PagerSlidingTabStrip;
import com.doloop.www.myappmgr.material.widgets.PagerSlidingTabStrip.OnTabClickListener;
import com.doloop.www.myappmgrmaterial.R;
import com.nispok.snackbar.Snackbar;
import com.readystatesoftware.systembartint.SystemBarTintManager;
import com.readystatesoftware.systembartint.SystemBarTintManager.SystemBarConfig;

import de.greenrobot.event.EventBus;

public class MainActivity extends ActionBarActivity implements //UserAppListFilterResultListener,
    UserAppListDataSetChangedListener,
    SysAppListDataSetChangedListener,
    BackupAppListDataSetChangedListener {
    private DrawerLayout mDrawerLayout;
    private ActionBarDrawerToggle mDrawerToggle;
    private ActionBar mActionBar;
    private ProgressDialog progDialog;

    private ArrayList<BaseFrag> Fragmentlist;
    private static long back_pressed = 0;
    private static Toast toast;

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
    private BackupAppTabFragment backupAppsFrg;

    private EditText searchViewEdt;
    private static Context thisActivityCtx;
    private AppListFragAdapter mFragAdapter;
    private int screenWidth = 0;
    private ViewPager mPager;
    private PagerSlidingTabStrip mPagerSlidingTabStrip;

    // public static boolean sDrawerIsOpen = false;

    private MenuItem searchMenuItem;
    private MenuItem sortMenuItem;

    private AppUpdateReceiver mAppUpdateReceiver;
    private IntentFilter AppIntentFilter;

    private LangUpdateReceiver mLangUpdateReceiver;
    private IntentFilter LangIntentFilter;
    
    private static Snackbar mSnackbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        
        if (Build.VERSION.SDK_INT == Build.VERSION_CODES.KITKAT) {
            //setTranslucentStatus(true);
            
            Window window = this.getWindow();
            window.setFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS,
                    WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            
            SystemBarTintManager tintManager = new SystemBarTintManager(this);
            tintManager.setStatusBarTintEnabled(true);
            tintManager.setStatusBarTintResource(R.color.transparent);
            tintManager.setStatusBarAlpha(0.5f);
            SystemBarConfig config = tintManager.getConfig();
            //View statusBarHolder = findViewById(R.id.statusbar_holder);
            
            View contLinear = findViewById(R.id.content_linear);
            //contLinear.setPadding(0, config.getPixelInsetTop(true), 0, config.getPixelInsetBottom());
            contLinear.setPadding(0, config.getStatusBarHeight(), 0, config.getPixelInsetBottom());
            
            //statusBarHolder.getLayoutParams().height = config.getStatusBarHeight();
            //statusBarHolder.setVisibility(View.VISIBLE);
            
        }
        
        
        AppUpdateStaticReceiver.handleEvent = false;
        thisActivityCtx = MainActivity.this;
        toast = Toast.makeText(thisActivityCtx, "", Toast.LENGTH_SHORT);
        screenWidth = getWindowManager().getDefaultDisplay().getWidth();
        EventBus.getDefault().register(this);
        if (savedInstanceState != null) {
            L.d("savedInstanceState != null" + savedInstanceState.toString());
        }

        // 初始化抽屉
        FragmentTransaction t = this.getSupportFragmentManager().beginTransaction();
        DrawerFragment drawFrag = DrawerFragment.getInstance(thisActivityCtx);
        t.replace(R.id.drawer_content_holder, drawFrag);
        t.commit();

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        // toolbar.inflateMenu(R.menu.menu_main);
        setSupportActionBar(toolbar);

        mActionBar = getSupportActionBar();
        mActionBar.setDisplayHomeAsUpEnabled(true);

        // 抽屉的初始化--start
        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer);
        mDrawerToggle =
                new ActionBarDrawerToggle(this, mDrawerLayout, toolbar, R.string.drawer_open, R.string.drawer_close) {

                    /*
                     * @Override public void onDrawerSlide(View drawerView, float slideOffset) { // TODO Auto-generated
                     * method stub super.onDrawerSlide(drawerView, slideOffset); L.d("slideOffset "+slideOffset); }
                     */

                    /** Called when a drawer has settled in a completely closed state. */
                    public void onDrawerClosed(View view) {
                        super.onDrawerClosed(view);

                        searchViewEdt.setFocusable(true);
                        searchViewEdt.setFocusableInTouchMode(true);
                        // searchViewEdt.setEnabled(true);
                        // invalidateOptionsMenu(); // creates call to onPrepareOptionsMenu()
                    }

                    /** Called when a drawer has settled in a completely open state. */
                    public void onDrawerOpened(View drawerView) {
                        super.onDrawerOpened(drawerView);

                        hideKeyboard();
                        searchViewEdt.clearFocus();
                        searchViewEdt.setFocusable(false);
                        // searchViewEdt.setEnabled(false);
                        // searchViewEdt.setVisibility(View.INVISIBLE);

                        // invalidateOptionsMenu(); // creates call to onPrepareOptionsMenu()
                    }
                };
        //mDrawerToggle.syncState();
        mDrawerLayout.setDrawerListener(mDrawerToggle);
        // 抽屉的初始化--end

        Fragmentlist = new ArrayList<BaseFrag>();
        usrAppsFrg = UserAppsTabFragment.getInstance(thisActivityCtx);
        sysAppsFrg = SysAppsTabFragment.getInstance(thisActivityCtx);
        backupAppsFrg = BackupAppTabFragment.getInstance(thisActivityCtx);

        Fragmentlist.add(usrAppsFrg);
        Fragmentlist.add(sysAppsFrg);
        Fragmentlist.add(backupAppsFrg);

        mPager = (ViewPager) findViewById(R.id.pager);
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
                if (position == Constants.USR_APPS_TAB_POS) {
                    mDrawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_UNLOCKED);
                } else {
                    mDrawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED);
                }

                if (position == Constants.USR_APPS_TAB_POS) {
                    if (MenuItemCompat.isActionViewExpanded(searchMenuItem)) {
                        sortMenuItem.setVisible(false);
                    } else {
                        sortMenuItem.setVisible(true);
                    }
                } else {
                    sortMenuItem.setVisible(false);
                }

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
                return false;

            }
        });
        mPagerSlidingTabStrip.setViewPager(mPager);
        // 选中的文字颜色
        mPagerSlidingTabStrip.setSelectedTextColorResource(R.color.white);
        // 正常文字颜色
        mPagerSlidingTabStrip.setTextColorResource(R.color.white3);
        // 导航条初始化--end

        mAppUpdateReceiver = new AppUpdateReceiver();
        AppIntentFilter = new IntentFilter();
        AppIntentFilter.addAction(Intent.ACTION_PACKAGE_ADDED);
        AppIntentFilter.addAction(Intent.ACTION_PACKAGE_REMOVED);
        AppIntentFilter.addAction(Intent.ACTION_PACKAGE_CHANGED);
        AppIntentFilter.addDataScheme("package");
        registerReceiver(mAppUpdateReceiver, AppIntentFilter);

        LangIntentFilter = new IntentFilter();
        LangIntentFilter.addAction(Intent.ACTION_LOCALE_CHANGED);

        mLangUpdateReceiver = new LangUpdateReceiver();
        registerReceiver(mLangUpdateReceiver, LangIntentFilter);

        new GetApps().execute(false);
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState)
    {
        super.onPostCreate(savedInstanceState);
        // Sync the toggle state after onRestoreInstanceState has occurred.
        mDrawerToggle.syncState();
    }
    
    @Override
    public void onBackPressed() {
        if (back_pressed + 2000 > System.currentTimeMillis()) {
            appDestroyCleanup();
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
        if(progDialog!=null&&progDialog.isShowing()){
            progDialog.dismiss();
        }
        SysAppFullList.clear();
        UserAppFullList.clear();
        unregisterReceivers();
        EventBus.getDefault().unregister(this);
        DaoUtils.destroy();
        AppUpdateStaticReceiver.handleEvent = true;
        toast = null;
        mSnackbar = null;
        thisActivityCtx = null;
    }

    public static Snackbar getSnackbar(boolean newInstance){
        if(mSnackbar == null || newInstance){
            mSnackbar = Snackbar.with(thisActivityCtx);
        }
        return mSnackbar; 
    }
    
    
    private void registerReceivers() {
        registerReceiver(mAppUpdateReceiver, AppIntentFilter);
        registerReceiver(mLangUpdateReceiver, LangIntentFilter);
    }

    private void unregisterReceivers() {
        try {
            unregisterReceiver(mAppUpdateReceiver);
            unregisterReceiver(mLangUpdateReceiver);
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

        SearchView searchView = (SearchView) MenuItemCompat.getActionView(menu.findItem(R.id.menu_search));
        searchView.setQueryHint(this.getString(R.string.search));

        searchViewEdt = ((EditText) searchView.findViewById(android.support.v7.appcompat.R.id.search_src_text));
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
                String mCurFilter = !TextUtils.isEmpty(newText) ? newText : null;
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

    private class GetApps extends AsyncTask<Boolean, String, Void> {
        private List<PackageInfo> packages;
        private PackageManager pManager;
        private String curAppName;

        @Override
        protected void onProgressUpdate(String...values) {
            // TODO Auto-generated method stub
            super.onProgressUpdate(values);
            if (!TextUtils.isEmpty(curAppName)) {
                progDialog.setTitle(curAppName);
            }
            progDialog.setProgress(Integer.valueOf(values[0]));
        }

        @Override
        protected void onPreExecute() {
            unregisterReceivers();

            // DismissDialog(SortTypeDialog);
            // DismissDialog(UserAppListMoreActionDialog);
            // DismissDialog(SelectionDialog);

            pManager = getPackageManager();
            packages = pManager.getInstalledPackages(0);

            progDialog = new ProgressDialog(MainActivity.this);
            progDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
            progDialog.setProgress(0);
            progDialog.setMax(packages.size());
            progDialog.setCancelable(false);
            progDialog.setTitle(getString(R.string.loading_apps));
            progDialog.setMessage(getString(R.string.loading_apps));
            progDialog.setOnCancelListener(new OnCancelListener() {

                @Override
                public void onCancel(DialogInterface dialog) {
                    GetApps.this.cancel(true);
                }

            });
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
            boolean deleteAllAppInfoDone = false;
            if (params[0]) {
                DaoUtils.deleteAllAppInfo(thisActivityCtx);
                deleteAllAppInfoDone = true;
            }
            DaoSession appInfoSession = DaoUtils.getDaoSession(MainActivity.this, true);
            if (Utilities.isAppListInDb(MainActivity.this)) {
                UserAppFullList =
                        (ArrayList<AppInfo>) appInfoSession.getAppInfoDao().queryBuilder()
                                .where(Properties.IsSysApp.eq("false")).list();

                SysAppFullList =
                        (ArrayList<AppInfo>) appInfoSession.queryBuilder(AppInfo.class)
                                .where(Properties.IsSysApp.eq("true")).list();

                int appCount = 0;
                for (int i = 0; i < UserAppFullList.size(); i++, appCount++) {
                    publishProgress("" + (appCount + 1));
                    Utilities.verifyAppIcon(thisActivityCtx, UserAppFullList.get(i));
                    UserAppFullList.get(i).appIconBytes = null;
                }

                for (int i = 0; i < SysAppFullList.size(); i++, appCount++) {
                    publishProgress("" + (appCount + 1));
                    Utilities.verifyAppIcon(thisActivityCtx, SysAppFullList.get(i));
                    SysAppFullList.get(i).appIconBytes = null;
                }
            } else {
                PackageInfo packageInfo;
                AppInfo tmpInfo;

                if (!deleteAllAppInfoDone) {
                    appInfoSession.getAppInfoDao().deleteAll();
                    Utilities.deleteAppIconDir(Utilities.getAppIconCacheDir(thisActivityCtx));
                }

                for (int i = 0, limit = packages.size(); i < limit; i++) {

                    // publishProgress("" + (i + 1));
                    Log.i("ttt", "processing app " + (i + 1) + " / " + limit);
                    packageInfo = packages.get(i);
                    tmpInfo = Utilities.buildAppInfoEntry(thisActivityCtx, packageInfo, pManager,true);
                    if (tmpInfo.isSysApp) {
                        SysAppFullList.add(tmpInfo);
                    } else {
                        UserAppFullList.add(tmpInfo);
                    }
                    curAppName = tmpInfo.appName;
                    publishProgress("" + (i + 1));
                    try {
                        // appInfoSession.insertOrReplace(tmpInfo);
                        appInfoSession.insert(tmpInfo);
                    } catch (Exception e) {
                        Log.e("ttt", "app error: " + tmpInfo.appName + ", error: " + e.toString());
                    } finally {
                        tmpInfo.appIconBytes = null;
                    }
                }
                Utilities.setAppListInDb(thisActivityCtx, true);
            }

            // 用户程序排序
            Utilities.sortUserAppList(thisActivityCtx, UserAppFullList);

            // build 系统applist
            TreeMap<String, ArrayList<AppInfo>> sectionItemsTreeMap =
                    new TreeMap<String, ArrayList<AppInfo>>(new SharpStringComparator());

            AppInfo curAppInfo;
            String curSectionStr = "";
            ArrayList<AppInfo> sectionItemsTmp;
            for (int i = 0; i < SysAppFullList.size(); i++) {
                curAppInfo = SysAppFullList.get(i);
                // curSectionStr = curAppInfo.appName.substring(0, 1).toUpperCase(Locale.getDefault());
                curSectionStr = Utilities.GetFirstChar(curAppInfo.appName);// .substring(0,
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
            appInfoSession.clear();
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
            usrAppsFrg.setListSortType(Utilities.getUserAppListSortType(thisActivityCtx));
            usrAppsFrg.setData(UserAppFullList);

            sysAppsFrg.setData(SysAppFullListWapper, mSectionInListPosMap);

            
            mPagerSlidingTabStrip.notifyDataSetChanged();
            registerReceivers();
            // registerReceiver(mAppUpdateReceiver, AppIntentFilter);
            // registerReceiver(LangUpdateReceiver, LangIntentFilter);
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

        if (keyCode == KeyEvent.KEYCODE_MENU && event.getRepeatCount() == 0) {
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

    // 用户app搜索之后
    /*@Override
    public void onUserAppListFilterResultPublish(ArrayList<AppInfo> resultsList) {
        // TODO Auto-generated method stub
        updateSlidingTabTitle(Constants.USR_APPS_TAB_POS);
    }*/

    /*@Override
    public void onSysAppListFilterResultPublish(ArrayList<SysAppListItem> ResultSysAppList,
            TreeMap<String, Integer> ResultPosMap) {
        // TODO Auto-generated method stub
        updateSlidingTabTitle(Constants.SYS_APPS_TAB_POS);
        sysAppsFrg.setExistIndexArray(new ArrayList<String>(ResultPosMap.keySet()));
    }*/

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

    public void onEventMainThread(DrawerItemClickEvent ev) {
        toggleDrawerMenu();
        switch (ev.mDrawerItem) {
            case REFRESH:
                new GetApps().execute(true);
                break;
        }
    }
    
    public void onEventMainThread(ViewNewBackupAppEvent ev) {
        mPager.setCurrentItem(Constants.BACKUP_APPS_TAB_POS);
    }
    

    private class AppUpdateReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            // TODO Auto-generated method stub
            closeDrawerMenu();
            usrAppsFrg.collapseLastOpenItem(false);

            if (intent.getAction().equals(Intent.ACTION_PACKAGE_ADDED)) {
                // app被安装
                String NewPkgName = intent.getDataString().substring(8);
                toast.setText(getString(R.string.new_app_installed) + " "
                        + Utilities.pkgNameToAppName(thisActivityCtx, NewPkgName));
                toast.show();

                if (DaoUtils.getByPackageName(thisActivityCtx, NewPkgName) != null)// 安装过--更新
                {
                    for (int i = 0; i < UserAppFullList.size(); i++) {
                        if (UserAppFullList.get(i).getPackageName().equals(NewPkgName)) {

                            DaoUtils.deleteAppInfo(thisActivityCtx, UserAppFullList.get(i));

                            AppInfo tmpAppInfo = Utilities.buildAppInfoEntry(thisActivityCtx, NewPkgName);
                            DaoUtils.insert(thisActivityCtx, tmpAppInfo);

                            AppInfo tmp = DaoUtils.getByPackageName(thisActivityCtx, NewPkgName);
                            UserAppFullList.set(i, tmp);

                            // ((ActionSlideExpandableListView) usrAppsFrg.getListView()).collapse(false);
                            usrAppsFrg.notifyDataSetChanged();
                            break;
                        }
                    }
                } else// 没有安装过, 重新来过
                {
                    // startRefreshAppInfoList();
                    AppInfo tmpAppInfo = Utilities.buildAppInfoEntry(thisActivityCtx, NewPkgName);
                    DaoUtils.insert(thisActivityCtx, tmpAppInfo);
                    UserAppFullList.add(tmpAppInfo);
                    Utilities.sortUserAppList(thisActivityCtx, UserAppFullList);

                    // ((ActionSlideExpandableListView) usrAppsFrg.getListView()).collapse(false);
                    usrAppsFrg.notifyDataSetChanged();
                    //updateSlidingTabTitle(Constants.USR_APPS_TAB_POS);

                }

                // startRefreshAppInfoList();

            } else if (intent.getAction().equals(Intent.ACTION_PACKAGE_REMOVED)) {
                // app被删除
                toast.setText(R.string.app_removed);
                String RemovedPkgName = intent.getDataString().substring(8);
                ArrayList<AppInfo> tmpUserDisplayList = usrAppsFrg.getDisplayList();
                for (int i = 0; i < UserAppFullList.size(); i++) {
                    if (UserAppFullList.get(i).packageName.equals(RemovedPkgName)) {
                        /*
                         * if (mActionMode != null) { if (UserAppFullList.get(i).selected) {
                         * UserAppActionModeSelectCnt--; mActionMode.setTitle("" + UserAppActionModeSelectCnt); } }
                         * 
                         * if (UserAppListMoreActionDialog != null &&
                         * UserAppListMoreActionDialog.getCurrentAppInfo().packageName.equals(RemovedPkgName)) {
                         * UserAppListMoreActionDialog.dismiss(); }
                         */

                        DaoUtils.deleteAppInfo(thisActivityCtx, UserAppFullList.get(i));
                        toast.setText(getString(R.string.app_removed_name) + " " + UserAppFullList.get(i).appName);
                        UserAppFullList.remove(i);
                    }
                    if (i < tmpUserDisplayList.size()) {
                        if (tmpUserDisplayList.get(i).packageName.equals(RemovedPkgName)) {
                            tmpUserDisplayList.remove(i);
                        }
                    }
                }

                toast.show();
                usrAppsFrg.notifyDataSetChanged();
                //updateSlidingTabTitle(Constants.USR_APPS_TAB_POS);
            } else if (intent.getAction().equals(Intent.ACTION_PACKAGE_CHANGED)) {
                String PkgName = intent.getDataString().substring(8);
                String appName = Utilities.pkgNameToAppName(thisActivityCtx, PkgName);
                toast.setText("PACKAGE_CHANGED: " + appName);
                toast.show();
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

    /*
     * public void onEventMainThread(AppUpdateEvent ev) { toggleDrawerMenu(); usrAppsFrg.collapseLastOpenItem(false);
     * closeDrawerMenu(); switch (ev.mAppState){ case APP_ADDED:
     * 
     * if (DaoUtils.getByPackageName(thisActivityCtx, ev.mPkgName) != null)// 安装过--更新 { for (int i = 0; i <
     * UserAppFullList.size(); i++) { if(UserAppFullList.get(i).getPackageName().equals(ev.mPkgName)) {
     * 
     * DaoUtils.deleteAppInfo(thisActivityCtx, UserAppFullList.get(i));
     * 
     * AppInfo tmpAppInfo = Utilities.buildAppInfoEntry(thisActivityCtx, ev.mPkgName); DaoUtils.insert(thisActivityCtx,
     * tmpAppInfo);
     * 
     * AppInfo tmp = DaoUtils.getByPackageName(thisActivityCtx, ev.mPkgName); UserAppFullList.set(i, tmp);
     * 
     * //((ActionSlideExpandableListView) usrAppsFrg.getListView()).collapse(false); usrAppsFrg.notifyDataSetChanged();
     * break; } } } else// 没有安装过, 重新来过
     * 
     * //startRefreshAppInfoList(); //AppInfo tmpAppInfo = Utilities.buildAppInfoEntry(thisActivityCtx, ev.mPkgName);
     * //DaoUtils.insert(thisActivityCtx, ev.mAppInfo); UserAppFullList.add(ev.mAppInfo);
     * Utilities.sortUserAppList(thisActivityCtx, UserAppFullList); updateSlidingTabTitle(Constants.USR_APPS_TAB_POS);
     * usrAppsFrg.notifyDataSetChanged(); break; case APP_REMOVED: ArrayList<AppInfo> tmpUserDisplayList =
     * usrAppsFrg.getDisplayList(); for (int i = 0; i < UserAppFullList.size(); i++) { if
     * (UserAppFullList.get(i).packageName.equals(ev.mPkgName)) { if (mActionMode != null) { if
     * (UserAppFullList.get(i).selected) { UserAppActionModeSelectCnt--; mActionMode.setTitle("" +
     * UserAppActionModeSelectCnt); } }
     * 
     * if (UserAppListMoreActionDialog != null &&
     * UserAppListMoreActionDialog.getCurrentAppInfo().packageName.equals(RemovedPkgName)) {
     * UserAppListMoreActionDialog.dismiss(); }
     * 
     * //DaoUtils.deleteAppInfo(thisActivityCtx, UserAppFullList.get(i));
     * //toast.setText(getString(R.string.app_removed_name) + " " + UserAppFullList.get(i).appName);
     * UserAppFullList.remove(i); } if (i < tmpUserDisplayList.size()) { if
     * (tmpUserDisplayList.get(i).packageName.equals(ev.mPkgName)) { tmpUserDisplayList.remove(i); } } }
     * updateSlidingTabTitle(Constants.USR_APPS_TAB_POS); usrAppsFrg.notifyDataSetChanged();
     * 
     * break; case APP_CHANGED:
     * 
     * break; default: break; } }
     */
}
