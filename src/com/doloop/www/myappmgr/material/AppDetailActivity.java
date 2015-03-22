package com.doloop.www.myappmgr.material;

import java.util.Date;

import android.annotation.TargetApi;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.style.ForegroundColorSpan;
import android.text.style.UnderlineSpan;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.doloop.www.myappmgr.material.dao.AppInfo;
import com.doloop.www.myappmgr.material.filtermenu.FilterMenu;
import com.doloop.www.myappmgr.material.filtermenu.FilterMenuLayout;
import com.doloop.www.myappmgr.material.utils.ScrimUtil;
import com.doloop.www.myappmgr.material.utils.Utils;
import com.doloop.www.myappmgr.material.widgets.ObservableScrollView;
import com.doloop.www.myappmgr.material.widgets.ObservableScrollViewCallbacks;
import com.doloop.www.myappmgr.material.widgets.ScrollState;
import com.doloop.www.myappmgrmaterial.R;
import com.nineoldandroids.view.ViewHelper;
import com.nispok.snackbar.Snackbar;
import com.readystatesoftware.systembartint.SystemBarTintManager;
import com.readystatesoftware.systembartint.SystemBarTintManager.SystemBarConfig;

public class AppDetailActivity extends BaseActivity implements ObservableScrollViewCallbacks {

    public static AppInfo curAppInfo;
    private LinearLayout rowContainer;
    private ObservableScrollView scrollView;
    private View headerView;

    @TargetApi(Build.VERSION_CODES.KITKAT)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_app_details);

        if (Build.VERSION.SDK_INT == Build.VERSION_CODES.KITKAT) {
            // setTranslucentStatus(true);
            boolean hasNavBar = Utils.hasNavBar(this);
            Window window = this.getWindow();
            window.setFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS,
                    WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);

            SystemBarTintManager tintManager = new SystemBarTintManager(this);
            SystemBarConfig config = tintManager.getConfig();
            //处理状态栏
            tintManager.setStatusBarTintEnabled(true);
            tintManager.setStatusBarTintResource(R.color.primary);
            tintManager.setStatusBarAlpha(0.5f);
            
            View rootView = findViewById(R.id.content_root);
            if(hasNavBar){
                rootView.setPadding(0, config.getStatusBarHeight(), 0, config.getNavigationBarHeight());
            }
            else{
                rootView.setPadding(0, config.getStatusBarHeight(), 0, 0);
            }
           
            //处理底边导航栏
            /*tintManager.setNavigationBarTintEnabled(true);
            tintManager.setNavigationBarTintResource(R.color.transparent);
            View drawerHolder = findViewById(R.id.drawer_content_holder);
            drawerHolder.setPadding(0, 0, 0, config.getNavigationBarHeight());*/
        }
        scrollView = (ObservableScrollView) findViewById(R.id.root_scroll_view);
        scrollView.setScrollViewCallbacks(this);
        headerView = findViewById(R.id.header);
        
        Drawable shadow = ScrimUtil.makeCubicGradientScrimDrawable(
                //Color.parseColor("#7d000000"),
                Color.GRAY,//"#55000000"
                8, //渐变层数
                Gravity.BOTTOM);
        this.findViewById(R.id.shadow).setBackgroundDrawable(shadow);
        
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
            fillRow(view, getString(R.string.last_updated_time), Utils.formatTimeDisplayFull(new Date(curAppInfo.lastModifiedRawTime)));

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
        if(!TextUtils.isEmpty(description)){
            descriptionView.setText(description);
        }
        
        view.setOnLongClickListener(new View.OnLongClickListener() {
            @SuppressWarnings("deprecation")
            @TargetApi(Build.VERSION_CODES.HONEYCOMB)
            @Override
            public boolean onLongClick(View v) {
                // TODO Auto-generated method stub     
                String copiedStr = title+":"+description;
                if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB){
                    ClipboardManager clipboard = (ClipboardManager) getSystemService(CLIPBOARD_SERVICE);
                    ClipData clip = ClipData.newPlainText(title, copiedStr);
                    clipboard.setPrimaryClip(clip);
                }
                else{
                    android.text.ClipboardManager clipboardManager = (android.text.ClipboardManager)getSystemService(CLIPBOARD_SERVICE);  
                    clipboardManager.setText(copiedStr); 
                }
                SpannableString spanString = new SpannableString(title + " " +getString(R.string.copied));
                spanString.setSpan(new UnderlineSpan(), 0, title.length(),
                        Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                spanString.setSpan(
                        new ForegroundColorSpan(getResources().getColor(
                                R.color.theme_blue_light)), 0, title.length(),
                        Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                
                
                Snackbar mSnackbar = MainActivity.getSnackbar(false);
                boolean mShowAniSnackBar = true;
                boolean mAniText = false;
                if(mSnackbar != null){
                    if(mSnackbar.isShowing()){
                        mShowAniSnackBar = false;
                        if(!mSnackbar.getText().toString().equalsIgnoreCase(spanString.toString())){
                            mAniText = true;
                        }
                        
                        mSnackbar.dismissAnimation(false);
                        mSnackbar.dismiss();
                    }
                    mSnackbar = MainActivity.getSnackbar(true);
                }

                mSnackbar.text(spanString) // text to display
                .animationText(mAniText)
                .swipeToDismiss(false)
                .showAnimation(mShowAniSnackBar)
                .dismissAnimation(true)
                .show(AppDetailActivity.this);
                
//                Snackbar.with(getApplicationContext()).dismiss();
//                Snackbar.with(getApplicationContext()) // context
//                        .text(spanString) // text to display
//                        .animationText(true)
//                        .show(AppDetailActivity.this);
                return true;
            }
        });
       
    }

    @Override
    public void onScrollChanged(int scrollY, boolean firstScroll, boolean dragging) {
        // TODO Auto-generated method stub
        ViewHelper.setTranslationY(headerView, scrollY / 2);
    }

    @Override
    public void onDownMotionEvent() {
        // TODO Auto-generated method stub
        
    }

    @Override
    public void onUpOrCancelMotionEvent(ScrollState scrollState) {
        // TODO Auto-generated method stub
        
    }

}
