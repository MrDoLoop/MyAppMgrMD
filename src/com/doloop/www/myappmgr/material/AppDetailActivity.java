package com.doloop.www.myappmgr.material;

import java.util.Date;

import android.annotation.TargetApi;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.ComponentName;
import android.content.Intent;
import android.content.pm.ResolveInfo;
import android.graphics.Color;
import android.graphics.Point;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.style.ForegroundColorSpan;
import android.text.style.UnderlineSpan;
import android.view.Gravity;
import android.view.View;
import android.view.animation.DecelerateInterpolator;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.doloop.www.myappmgr.material.dao.AppInfo;
import com.doloop.www.myappmgr.material.events.AppUpdateEvent;
import com.doloop.www.myappmgr.material.filtermenu.FilterMenu;
import com.doloop.www.myappmgr.material.filtermenu.FilterMenuLayout;
import com.doloop.www.myappmgr.material.swipeback.lib.SwipeBackActivity;
import com.doloop.www.myappmgr.material.utils.Constants;
import com.doloop.www.myappmgr.material.utils.ScrimUtil;
import com.doloop.www.myappmgr.material.utils.Utils;
import com.doloop.www.myappmgr.material.widgets.CircularRevealView;
import com.doloop.www.myappmgr.material.widgets.ObservableScrollView;
import com.doloop.www.myappmgr.material.widgets.ObservableScrollViewCallbacks;
import com.doloop.www.myappmgr.material.widgets.ScrollState;
import com.doloop.www.myappmgrmaterial.R;
import com.nineoldandroids.animation.Animator;
import com.nineoldandroids.animation.AnimatorListenerAdapter;
import com.nineoldandroids.animation.ObjectAnimator;
import com.nineoldandroids.view.ViewHelper;
import com.nineoldandroids.view.ViewPropertyAnimator;
import com.nispok.snackbar.Snackbar;
import com.readystatesoftware.systembartint.SystemBarTintManager;
import com.readystatesoftware.systembartint.SystemBarTintManager.SystemBarConfig;

import de.greenrobot.event.EventBus;

//http://frogermcs.github.io/InstaMaterial-concept-part-6-user-profile/
public class AppDetailActivity extends SwipeBackActivity implements ObservableScrollViewCallbacks {//SwipeBackActivity ActionBarActivity

    public static AppInfo curAppInfo;
    private LinearLayout rowContainer;
    private ObservableScrollView rootScrollView;
    private View headerView;
    private View headerImgView;
    private CircularRevealView revealView;
    private View contentRootView;
    private View shadowView;
    private FilterMenuLayout menuLayout;
    private Point revalStartPosition;
    private View appIcon;
    
    public static final String REVEAL_START_POSITION = "REVEAL_START_POSITION";

    @TargetApi(Build.VERSION_CODES.KITKAT)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_app_details);
        
        
        EventBus.getDefault().register(this);
        
        headerImgView = findViewById(R.id.header_image);
        appIcon = findViewById(R.id.app_icon);
        contentRootView = findViewById(R.id.content_root);
        rootScrollView = (ObservableScrollView) findViewById(R.id.root_scroll_view);
        rootScrollView.setBackgroundColor(Color.TRANSPARENT);
        
        //默认是从屏幕中心开始
        int[] pos = getIntent().getIntArrayExtra(REVEAL_START_POSITION);
        if(pos != null){
            revalStartPosition = new Point(pos[0], pos[1]);
        }
        else{
            revalStartPosition = new Point(Utils.getScreenWidth(AppDetailActivity.this)/2, Utils.getScreenHeight(AppDetailActivity.this)/2);
        }
        
        headerImgView.setVisibility(View.INVISIBLE);
        contentRootView.setVisibility(View.INVISIBLE);
        Drawable shadow = ScrimUtil.makeCubicGradientScrimDrawable(
        // Color.parseColor("#7d000000"),
                Color.GRAY,// "#55000000"
                8, // 渐变层数
                Gravity.BOTTOM);
        shadowView = this.findViewById(R.id.shadow);
        shadowView.setBackgroundDrawable(shadow);
        ViewHelper.setAlpha(shadowView, 0f);
        
        rootScrollView.setScrollViewCallbacks(this);
        headerView = findViewById(R.id.header);
        revealView = (CircularRevealView) findViewById(R.id.reveal);
        
       /* if (Build.VERSION.SDK_INT == Build.VERSION_CODES.KITKAT) {
            // setTranslucentStatus(true);
            boolean hasNavBar = Utils.hasNavBar(this);
           Window window = this.getWindow();
            window.setFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS,
                    WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);

            SystemBarTintManager tintManager = new SystemBarTintManager(this);
            SystemBarConfig config = tintManager.getConfig();
            // 处理状态栏
            tintManager.setStatusBarTintEnabled(true);
            tintManager.setStatusBarTintResource(R.color.primary);
            tintManager.setStatusBarAlpha(0.5f);

            
            int newHeight = headerImgView.getLayoutParams().height + config.getStatusBarHeight();
            headerImgView.getLayoutParams().height = newHeight;
            headerImgView.requestLayout();

            
            if (hasNavBar) {
                contentRootView.setPadding(0, config.getStatusBarHeight(), 0, config.getNavigationBarHeight());
                tintManager.setNavigationBarTintEnabled(true);
                tintManager.setNavigationBarTintResource(R.color.primary);
            } else {
                contentRootView.setPadding(0, config.getStatusBarHeight(), 0, 0);
            }
        }*/

        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {

            @Override
            public void run() {
                //Utils.getLocationInView(revealView, headerView);
                int color = Color.parseColor("#00bcd4");//getResources().getColor(R.color.windows_bg);//Color.parseColor("#00bcd4");
                revealView.reveal(revalStartPosition.x, revalStartPosition.y, color, 30, 700, new AnimatorListenerAdapter(){
                    
                    @Override
                    public void onAnimationStart(Animator animation) {
                      //向下移动+alpha
                        ViewHelper.setTranslationY(rowContainer, 300);
                        ViewHelper.setAlpha(rowContainer, 0.5f);
                        
                        //向上移动+alpha
                        ViewHelper.setTranslationY(headerImgView, -200);
                        ViewHelper.setAlpha(headerImgView, 0.5f);
          
                        //ViewHelper.setTranslationY(headerView, -200);
                        ViewHelper.setScaleX(appIcon, 0);
                        ViewHelper.setScaleY(appIcon, 0);
                        ViewHelper.setAlpha(appIcon, 0.0f);
                        
                        //隐藏
                        ViewHelper.setAlpha(headerView, 0.0f);
                        
                        contentRootView.setVisibility(View.INVISIBLE);
                        headerImgView.setVisibility(View.INVISIBLE);
                    }

                    @Override
                    public void onAnimationEnd(Animator animation) {
                        // TODO Auto-generated method stub
//                        revealView.hide(p.x, p.y, Color.TRANSPARENT, new AnimatorListenerAdapter(){
//
//                            @Override
//                            public void onAnimationEnd(Animator animation) {
//                                // TODO Auto-generated method stub
//                                revealView.setVisibility(View.GONE);
//                            }
//                            
//                        });
                        //revealView.setVisibility(View.GONE);
                        //ViewPropertyAnimator.animate(revealView).alpha(0).setDuration(150).start();
                        //getWindow().getDecorView().setBackgroundColor(getResources().getColor(R.color.primary_dark));
                        
                        ViewPropertyAnimator.animate(revealView).alpha(0).setDuration(500).start();
                        contentRootView.setVisibility(View.VISIBLE);
                        headerImgView.setVisibility(View.VISIBLE);
                        
                        ViewPropertyAnimator.animate(rowContainer).alpha(1f).translationY(0).setInterpolator(new DecelerateInterpolator(3.f))
                        .setDuration(1000).setListener(new AnimatorListenerAdapter() {
                            @Override
                            public void onAnimationEnd(Animator animation) {
                                //ViewHelper.setAlpha(shadowView, 1f);
                                revealView.setVisibility(View.GONE);
                            }
                        })  
                        .start();
                        ViewPropertyAnimator.animate(headerImgView).alpha(1f).translationY(0).setInterpolator(new DecelerateInterpolator(3.f))
                        .setDuration(1000).start();  
                        
                        ViewPropertyAnimator.animate(headerView).alpha(1f).setInterpolator(new DecelerateInterpolator(3.f))
                        .setDuration(1000).setStartDelay(300).start();
                        
                        ViewPropertyAnimator.animate(appIcon).alpha(1).scaleX(1).scaleY(1).alpha(1f).setInterpolator(new DecelerateInterpolator(3.f))
                        .setDuration(1000).setStartDelay(300).start();
                        
                        rootScrollView.setBackgroundColor(getResources().getColor(R.color.windows_bg));
                        
                    }
                    
                });
            }
        }, 100);



        // Row Container
        rowContainer = (LinearLayout) findViewById(R.id.row_container);
        menuLayout = (FilterMenuLayout) findViewById(R.id.menu);
        new FilterMenu.Builder(this).addItem(R.drawable.ic_action_add).addItem(R.drawable.ic_action_clock)
                .addItem(R.drawable.ic_action_clock).addItem(R.drawable.ic_action_clock)
                .addItem(R.drawable.ic_action_clock).attach(menuLayout)
                .withListener(new FilterMenu.OnMenuChangeListener() {

                    @Override
                    public void onMenuItemClick(View view, int position) {
                        switch (position) {
                            case 0://启动
                                if (Constants.MY_PACKAGE_NAME.equals(curAppInfo.packageName))// 避免再次启动自己app
                                {
                                    MainActivity.T("You catch me!! NAN Made app");
                                } else {
                                    Intent intent = getPackageManager().getLaunchIntentForPackage(
                                            curAppInfo.packageName);
                                    if (intent != null) {
                                        if (intent != null) {
                                            try {
                                                startActivity(intent);
                                            } catch (Exception e) {
                                                MainActivity.T(R.string.launch_fail);
                                            }
                                        }
                                    } else {
                                        MainActivity.T(R.string.launch_fail);
                                    }
                                }
                                break;
                            case 1:
                                Utils.showInstalledAppDetails(AppDetailActivity.this, curAppInfo.packageName);
                                break;
                            case 2:
                                Uri packageUri = Uri.parse("package:" + curAppInfo.packageName);
                                Intent uninstallIntent;
                                if (Build.VERSION.SDK_INT < Build.VERSION_CODES.ICE_CREAM_SANDWICH) {
                                    uninstallIntent = new Intent(Intent.ACTION_DELETE, packageUri);
                                } else {
                                    uninstallIntent = new Intent(Intent.ACTION_UNINSTALL_PACKAGE, packageUri);
                                }
                                startActivity(uninstallIntent);
                                break;
                            
                        }
                        //MainActivity.T("菜单 " + position);
                    }

                    @Override
                    public void onMenuCollapse() {

                    }

                    @Override
                    public void onMenuExpand() {

                    }
                }).build();

        if (curAppInfo != null) {

            Intent intent = new Intent();
            intent.setPackage(curAppInfo.packageName);
            intent.addCategory(Intent.CATEGORY_LAUNCHER);
            ResolveInfo appResolveInfo = null;
            try {
                appResolveInfo = getPackageManager().resolveActivity(intent, 0);
            } catch (Exception e) {
                e.printStackTrace();
            }

            TextView appName = (TextView) findViewById(R.id.app_name);
            appName.setText(curAppInfo.appName);
            final ImageView appIcon = (ImageView) findViewById(R.id.app_icon);
            appIcon.setImageDrawable(Utils.getIconDrawable(this, curAppInfo.packageName));
            appIcon.setOnClickListener(new View.OnClickListener() {
                
                @Override
                public void onClick(View v) {
                    // TODO Auto-generated method stub
                    ObjectAnimator ani = ObjectAnimator.ofFloat(appIcon, "rotationY", 0, 360).setDuration(1000);
                    ani.addListener(new AnimatorListenerAdapter(){

                        @Override
                        public void onAnimationEnd(Animator arg0) {
                            // TODO Auto-generated method stub
                            appIcon.setEnabled(true);
                        }

                        @Override
                        public void onAnimationStart(Animator arg0) {
                            // TODO Auto-generated method stub
                            appIcon.setEnabled(false);
                        }} );
                    ani.start();
                    /*ViewPropertyAnimator.animate(appIcon).cancel();
                    ViewPropertyAnimator.animate(appIcon).rotation(360f).setDuration(1000).setListener(new AnimatorListenerAdapter() {
                        
                        @Override
                        public void onAnimationStart(Animator animation) {
                            appIcon.setEnabled(false);
                        }

                        @Override
                        public void onAnimationEnd(Animator animation) {
                            appIcon.setEnabled(true);
                        }
                    }).start();*/
                }
            });
            
            View view = rowContainer.findViewById(R.id.row_pkgname);
            fillRow(view, getString(R.string.pkg_name), curAppInfo.packageName);

            view = rowContainer.findViewById(R.id.row_version);
            fillRow(view, getString(R.string.version), curAppInfo.versionName + " (" + curAppInfo.versionCode + ")");

            view = rowContainer.findViewById(R.id.row_apk_info);
            fillRow(view, getString(R.string.apk_info),
                    curAppInfo.apkFilePath + "\n" + Utils.formatFileSize(curAppInfo.appRawSize));

            view = rowContainer.findViewById(R.id.row_time_info);
            fillRow(view, getString(R.string.last_updated_time),
                    Utils.formatTimeDisplayFull(new Date(curAppInfo.lastModifiedRawTime)));

            view = rowContainer.findViewById(R.id.row_activity);
            if (appResolveInfo != null) {
                fillRow(view, getString(R.string.activity), appResolveInfo.activityInfo.name);
            } else {
                fillRow(view, getString(R.string.activity), "");
            }

            view = rowContainer.findViewById(R.id.row_componement);
            if (appResolveInfo != null) {
                String componementStr = "";
                ComponentName componentName =
                        new ComponentName(appResolveInfo.activityInfo.applicationInfo.packageName,
                                appResolveInfo.activityInfo.name);
                if (componentName != null) {
                    componementStr = componentName.toString();
                }
                fillRow(view, getString(R.string.componement), componementStr);
            } else {
                fillRow(view, getString(R.string.componement), "");
            }
        }
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onPostCreate(savedInstanceState);
        if (Build.VERSION.SDK_INT == Build.VERSION_CODES.KITKAT) {
            // setTranslucentStatus(true);
            boolean hasNavBar = Utils.hasNavBar(this);
//           Window window = this.getWindow();
//            window.setFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS,
//                    WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);

            SystemBarTintManager tintManager = new SystemBarTintManager(this);
            SystemBarConfig config = tintManager.getConfig();
            // 处理状态栏
            tintManager.setStatusBarTintEnabled(true);
            tintManager.setStatusBarTintResource(R.color.primary);
            tintManager.setStatusBarAlpha(0.5f);

            
            int newHeight = headerImgView.getLayoutParams().height + config.getStatusBarHeight();
            headerImgView.getLayoutParams().height = newHeight;
            headerImgView.requestLayout();

            
            if (hasNavBar) {
                contentRootView.setPadding(0, config.getStatusBarHeight(), 0, config.getNavigationBarHeight());
                tintManager.setNavigationBarTintEnabled(true);
                tintManager.setNavigationBarTintResource(R.color.primary);
            } else {
                contentRootView.setPadding(0, config.getStatusBarHeight(), 0, 0);
            }
        }
    }

    @Override
    public void onBackPressed() {
        // TODO Auto-generated method stub
        /*revealView.setVisibility(View.VISIBLE);
        final Point p = new Point(Utils.getScreenWidth(AppDetailActivity.this)/2, Utils.getScreenHeight(AppDetailActivity.this)/2);//Utils.getLocationInView(revealView, headerView);
        final int color = getResources().getColor(R.color.primary);//Color.parseColor("#00bcd4");
        revealView.reveal(p.x, p.y, color, 30, 440, new AnimatorListenerAdapter(){
            
            @Override
            public void onAnimationStart(Animator animation) {
                // TODO Auto-generated method stub
                
            }

            @Override
            public void onAnimationEnd(Animator animation) {
                // TODO Auto-generated method stub
                revealView.hide(p.x, p.y, Color.TRANSPARENT, new AnimatorListenerAdapter(){

                    @Override
                    public void onAnimationEnd(Animator animation) {
                        // TODO Auto-generated method stub
                        finish();
                        overridePendingTransition(0, 0);
                    }
                    
                });
                
                
                
            }
            
        });*/
        super.onBackPressed();
        //overridePendingTransition(0,0);
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
    }

    @Override
    protected void onDestroy() {
        // TODO Auto-generated method stub
        curAppInfo = null;
        EventBus.getDefault().unregister(this);
        super.onDestroy();
    }
    
    public void onEventMainThread(AppUpdateEvent ev) {
        switch (ev.mAppState){
            case APP_ADDED:
            case APP_REMOVED:
                if(ev.mPkgName.equals(curAppInfo.packageName)){
                    onBackPressed();
                }
                break;
            case APP_CHANGED:
                break;
            default:
                break;
        }
            
        
    }

    private void fillRow(View view, final String title, final String description) {
        TextView titleView = (TextView) view.findViewById(R.id.title);
        titleView.setText(title);

        TextView descriptionView = (TextView) view.findViewById(R.id.description);
        if (!TextUtils.isEmpty(description)) {
            descriptionView.setText(description);
        }

        view.setOnLongClickListener(new View.OnLongClickListener() {
            @SuppressWarnings("deprecation")
            @TargetApi(Build.VERSION_CODES.HONEYCOMB)
            @Override
            public boolean onLongClick(View v) {
                // TODO Auto-generated method stub
                String copiedStr = title + ":" + description;
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
                    ClipboardManager clipboard = (ClipboardManager) getSystemService(CLIPBOARD_SERVICE);
                    ClipData clip = ClipData.newPlainText(title, copiedStr);
                    clipboard.setPrimaryClip(clip);
                } else {
                    android.text.ClipboardManager clipboardManager =
                            (android.text.ClipboardManager) getSystemService(CLIPBOARD_SERVICE);
                    clipboardManager.setText(copiedStr);
                }
                SpannableString spanString = new SpannableString(title + " " + getString(R.string.copied));
                spanString.setSpan(new UnderlineSpan(), 0, title.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                spanString.setSpan(new ForegroundColorSpan(getResources().getColor(R.color.theme_blue_light)), 0,
                        title.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);

                Snackbar mSnackbar = MainActivity.getSnackbar(false);
                boolean mShowAniSnackBar = true;
                boolean mAniText = false;
                if (mSnackbar != null) {
                    if (mSnackbar.isShowing()) {
                        mShowAniSnackBar = false;
                        if (!mSnackbar.getText().toString().equalsIgnoreCase(spanString.toString())) {
                            mAniText = true;
                        }

                        mSnackbar.dismissAnimation(false);
                        mSnackbar.dismiss();
                    }
                    mSnackbar = MainActivity.getSnackbar(true);
                }

                mSnackbar.text(spanString)
                        // text to display
                        .animationText(mAniText).swipeToDismiss(false).showAnimation(mShowAniSnackBar)
                        .dismissAnimation(true).show(AppDetailActivity.this);

                // Snackbar.with(getApplicationContext()).dismiss();
                // Snackbar.with(getApplicationContext()) // context
                // .text(spanString) // text to display
                // .animationText(true)
                // .show(AppDetailActivity.this);
                return true;
            }
        });
    }

    @Override
    public void onScrollChanged(int scrollY, boolean firstScroll, boolean dragging) {
        // TODO Auto-generated method stub
        MainActivity.dismissSnackbar();
        if(scrollY == 0){
            ViewHelper.setAlpha(shadowView, 0f);
        }
        else{
            ViewHelper.setAlpha(shadowView, 1f);
        }
        // ViewHelper.setTranslationY(headerImgView, scrollY / 2);
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
