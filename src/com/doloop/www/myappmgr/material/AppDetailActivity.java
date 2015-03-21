package com.doloop.www.myappmgr.material;

import android.annotation.TargetApi;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.doloop.www.myappmgr.material.dao.AppInfo;
import com.doloop.www.myappmgr.material.filtermenu.FilterMenu;
import com.doloop.www.myappmgr.material.filtermenu.FilterMenuLayout;
import com.doloop.www.myappmgr.material.utils.Utils;
import com.doloop.www.myappmgrmaterial.R;
import com.readystatesoftware.systembartint.SystemBarTintManager;
import com.readystatesoftware.systembartint.SystemBarTintManager.SystemBarConfig;

public class AppDetailActivity extends ActionBarActivity {

    public static AppInfo curAppInfo;
    private LinearLayout rowContainer;

    @TargetApi(Build.VERSION_CODES.KITKAT)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_app_details);

        if (Build.VERSION.SDK_INT == Build.VERSION_CODES.KITKAT) {
            // setTranslucentStatus(true);

            Window window = this.getWindow();
            window.setFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS,
                    WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);

            SystemBarTintManager tintManager = new SystemBarTintManager(this);
            SystemBarConfig config = tintManager.getConfig();
            //处理状态栏
            tintManager.setStatusBarTintEnabled(true);
            tintManager.setStatusBarTintResource(R.color.transparent);
            tintManager.setStatusBarAlpha(0.5f);
            
            View rootView = findViewById(R.id.root_scroll_view);
            rootView.setPadding(0, config.getStatusBarHeight(), 0, config.getNavigationBarHeight());
            //处理底边导航栏
            /*tintManager.setNavigationBarTintEnabled(true);
            tintManager.setNavigationBarTintResource(R.color.transparent);
            View drawerHolder = findViewById(R.id.drawer_content_holder);
            drawerHolder.setPadding(0, 0, 0, config.getNavigationBarHeight());*/
        }
        
        
        // Row Container
        rowContainer = (LinearLayout) findViewById(R.id.row_container);
        FilterMenuLayout menuLayout = (FilterMenuLayout) findViewById(R.id.menu);
        new FilterMenu.Builder(this)
            .addItem(R.drawable.ic_action_add)
            .addItem(R.drawable.ic_action_clock)
            .addItem(R.drawable.ic_action_clock)
            .addItem(R.drawable.ic_action_clock)
            .addItem(R.drawable.ic_action_clock)
            .attach(menuLayout)
            .withListener(new FilterMenu.OnMenuChangeListener() {
                    
                    @Override
                    public void onMenuItemClick(View view, int position) {
                        MainActivity.T("菜单 "+position);
                    }

                    
                    @Override
                    public void onMenuCollapse() {

                    }

                    
                    @Override
                    public void onMenuExpand() {

                    }
                })
            .build();
        
        if (curAppInfo != null) {
            TextView appName = (TextView) findViewById(R.id.app_name);
            appName.setText(curAppInfo.appName);
            ImageView appIcon = (ImageView) findViewById(R.id.app_icon);
            appIcon.setImageDrawable(Utils.getIconDrawable(this, curAppInfo.packageName));
            
            
            View view = rowContainer.findViewById(R.id.row_pkgname);
            fillRow(view, getString(R.string.pkg_name), curAppInfo.packageName);

            view = rowContainer.findViewById(R.id.row_version);
            fillRow(view, getString(R.string.version), curAppInfo.versionName + " (" + curAppInfo.versionCode + ")");

            view = rowContainer.findViewById(R.id.row_apk_info);
            fillRow(view, getString(R.string.apk_info),
                    curAppInfo.apkFilePath + "\n" + Utils.formatFileSize(curAppInfo.appRawSize));

            view = rowContainer.findViewById(R.id.row_time_info);
            fillRow(view, getString(R.string.last_updated_time), curAppInfo.lastModifiedTimeStr);

            view = rowContainer.findViewById(R.id.row_activity);
            fillRow(view, getString(R.string.activity), "");

            view = rowContainer.findViewById(R.id.row_componement);
            Intent intent = getPackageManager().getLaunchIntentForPackage(curAppInfo.packageName);
            String componementStr = "";
            if(intent != null && intent.getComponent() != null){
                componementStr = intent.getComponent().toShortString();
            }
            fillRow(view, getString(R.string.componement), componementStr);
        }
    }

    @Override
    protected void onDestroy() {
        // TODO Auto-generated method stub
        super.onDestroy();
        curAppInfo = null;
    }

    private void fillRow(View view, final String title, final String description) {
        TextView titleView = (TextView) view.findViewById(R.id.title);
        titleView.setText(title);

        TextView descriptionView = (TextView) view.findViewById(R.id.description);
        descriptionView.setText(description);
    }

}
