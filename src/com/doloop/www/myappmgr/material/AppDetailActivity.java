package com.doloop.www.myappmgr.material;

import java.io.File;
import java.util.ArrayList;
import java.util.Date;

import org.apache.commons.io.FileUtils;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
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
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.BounceInterpolator;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.Interpolator;
import android.widget.ImageView;
import android.widget.TextView;

import com.doloop.www.myappmgr.material.dao.AppInfo;
import com.doloop.www.myappmgr.material.events.AppBackupSuccEvent;
import com.doloop.www.myappmgr.material.events.AppUpdateEvent;
import com.doloop.www.myappmgr.material.events.BackupAppDeletedEvent;
import com.doloop.www.myappmgr.material.filtermenu.FilterMenu;
import com.doloop.www.myappmgr.material.filtermenu.FilterMenuItemWapper;
import com.doloop.www.myappmgr.material.filtermenu.FilterMenuLayout;
import com.doloop.www.myappmgr.material.swipeback.lib.SwipeBackActivity;
import com.doloop.www.myappmgr.material.utils.Constants;
import com.doloop.www.myappmgr.material.utils.PicassoTools;
import com.doloop.www.myappmgr.material.utils.ScrimUtil;
import com.doloop.www.myappmgr.material.utils.Utils;
import com.doloop.www.myappmgr.material.widgets.CircularRevealView;
import com.doloop.www.myappmgr.material.widgets.KenBurnsSupportView;
import com.doloop.www.myappmgr.material.widgets.ObservableScrollView;
import com.doloop.www.myappmgr.material.widgets.ObservableScrollViewCallbacks;
import com.doloop.www.myappmgr.material.widgets.ScrollState;
import com.nineoldandroids.animation.Animator;
import com.nineoldandroids.animation.AnimatorListenerAdapter;
import com.nineoldandroids.animation.AnimatorSet;
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
    private View rowContainer;
    private ObservableScrollView rootScrollView;
    private View headerView;
    private KenBurnsSupportView headerImgView;
    private CircularRevealView revealView;
    private View contentRootView;
    private View shadowView;
    private FilterMenuLayout menuLayout;
    private Point revealStartPosition;
    private ImageView appIcon;
    private View rootFrame;
    private ResolveInfo appResolveInfo = null;
    private boolean isBlockedScrollView = false;

    private static final String OPEN_ACTION = "OPEN_ACTION";
    private static final String INFO_ACTION = "INFO_ACTION";
    private static final String BACKUP_ACTION = "BACKUP_ACTION";
    private static final String SHARE_ACTION = "SHARE_ACTION";
    private static final String UNINSTALL_ACTION = "UNINSTALL_ACTION";
    private static final String DELETE_ACTION = "DELETE_ACTION";
    private static final String INSTALL_ACTION = "INSTALL_ACTION";
    
    
    public static final String REVEAL_START_POSITION = "REVEAL_START_POSITION";
    
    private boolean playStartAni = true;
    private boolean canLaunch = false;
    
    public static final String APP_TYPE = "APP_TYPE";
    public static final int APP_TYPE_USER = 0;
    public static final int APP_TYPE_SYS = 1;
    public static final int APP_TYPE_BACKUP = 2;
    private int curAppType = 0;  

    @TargetApi(Build.VERSION_CODES.KITKAT)
    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_app_details);
        if(curAppInfo == null){
            finish();
            return; 
        } 
        EventBus.getDefault().register(this);
        
        curAppType = getIntent().getIntExtra(APP_TYPE, APP_TYPE_USER);
        
        playStartAni = Utils.playAniAppDetails(this);
        
        rootFrame  = findViewById(R.id.root_frame);
        headerImgView = (KenBurnsSupportView) findViewById(R.id.header_image);
        headerImgView.setResourceIds(R.drawable.ic_user_background, R.drawable.background);
        appIcon = (ImageView) findViewById(R.id.app_icon);
       
        switch (curAppType){
            case APP_TYPE_USER:
                appIcon.setBackgroundResource(R.drawable.imageview_border_blue);
                PicassoTools.getInstance().load(curAppInfo.getPicassoScheme())
                .error(R.drawable.icon_error).noFade().into(appIcon);
                break;
            case APP_TYPE_SYS:
                PicassoTools.getInstance().load(curAppInfo.getPicassoScheme())
                .error(R.drawable.icon_error).noFade().into(appIcon);
                appIcon.setBackgroundResource(R.drawable.imageview_border_red);
                break;
            case APP_TYPE_BACKUP:
                PicassoTools.getInstance().load(curAppInfo.getPicassoBackupScheme())
                .error(R.drawable.icon_error).noFade().into(appIcon);
                appIcon.setBackgroundResource(R.drawable.imageview_border_orange);
                break;
        }
        
        
        
        contentRootView = findViewById(R.id.content_root);
        rootScrollView = (ObservableScrollView) findViewById(R.id.root_scroll_view);
        
        rootScrollView.setScrollViewCallbacks(this);
        headerView = findViewById(R.id.header);
        revealView = (CircularRevealView) findViewById(R.id.reveal);
        rowContainer = findViewById(R.id.row_container);

        Drawable shadow = ScrimUtil.makeCubicGradientScrimDrawable(
        // Color.parseColor("#7d000000"),
                Color.GRAY,// "#55000000"
                8, // 渐变层数
                Gravity.TOP);
        shadowView = this.findViewById(R.id.shadow);
        Utils.setBackgroundDrawable(shadowView, shadow);
        //ViewHelper.setAlpha(shadowView, 0f);

        if(playStartAni){
            //默认是从屏幕中心开始
            int[] pos = getIntent().getIntArrayExtra(REVEAL_START_POSITION);
            if(pos != null){
                revealStartPosition = new Point(pos[0], pos[1]);
            }
            else{
                revealStartPosition = new Point(Utils.getScreenWidth(AppDetailActivity.this)/2, Utils.getScreenHeight(AppDetailActivity.this)/2);
            }
            
            headerImgView.setVisibility(View.INVISIBLE);
            contentRootView.setVisibility(View.INVISIBLE);
            rootScrollView.setBackgroundColor(Color.TRANSPARENT);
            rootFrame.setBackgroundColor(Color.TRANSPARENT);
            //为了防止在动画还没有播放完就开始滑动scrollview
            isBlockedScrollView = true;
            rootScrollView.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    // TODO Auto-generated method stub
                    return isBlockedScrollView;
                }
            });
            
            Handler handler = new Handler();
            handler.postDelayed(new Runnable() {

                @Override
                public void run() {
                    int color = Color.parseColor("#00bcd4");//getResources().getColor(R.color.windows_bg);//Color.parseColor("#00bcd4");
                    revealView.reveal(revealStartPosition.x, revealStartPosition.y, color, 30, 700, new AnimatorListenerAdapter(){
                        
                        @Override
                        public void onAnimationStart(Animator animation) {
                            prepareAni();
                        }

                        @Override
                        public void onAnimationEnd(Animator animation) {
                            
                            ViewPropertyAnimator.animate(revealView).alpha(0).setDuration(500).start();
                            contentRootView.setVisibility(View.VISIBLE);
                            headerImgView.setVisibility(View.VISIBLE);
                            
                            Interpolator interpolator = new DecelerateInterpolator(3.f); 
                            ViewPropertyAnimator.animate(rowContainer).alpha(1f).translationY(0).setInterpolator(interpolator)
                            .setDuration(800).setListener(new AnimatorListenerAdapter() {
                                @Override
                                public void onAnimationEnd(Animator animation) {
                                    
                                    revealView.setVisibility(View.GONE);
                                    isBlockedScrollView = false;
                                   //这里设置底部的padding，在长屏幕下。如果先设置了底部的padding，reaveal消失的时候，底部会出现一个空条
                                    if (Build.VERSION.SDK_INT == Build.VERSION_CODES.KITKAT && Utils.hasNavBar(AppDetailActivity.this)) {
                                        SystemBarTintManager tintManager = new SystemBarTintManager(AppDetailActivity.this);
                                        SystemBarConfig config = tintManager.getConfig();
                                        contentRootView.setPadding(0, config.getStatusBarHeight(), 0, config.getNavigationBarHeight());
                                    }
                                }
                            })  
                            .start();
                            ViewPropertyAnimator.animate(headerImgView).alpha(1f).translationY(0).setInterpolator(interpolator)
                            .setDuration(800).start();  
                            
                            ViewPropertyAnimator.animate(shadowView).alpha(1f).translationY(0).setInterpolator(interpolator)
                            .setDuration(800).start();  
                            
                            ViewPropertyAnimator.animate(headerView).alpha(1f).setInterpolator(interpolator)
                            .setDuration(800).setStartDelay(300).start();
                           
                            
                            ViewPropertyAnimator.animate(appIcon).alpha(1).scaleX(1).scaleY(1).alpha(1f).setInterpolator(new DecelerateInterpolator(3.f))
                            .setDuration(1000).setStartDelay(400).setListener(new AnimatorListenerAdapter(){

                                @Override
                                public void onAnimationEnd(Animator animation) {
                                    // TODO Auto-generated method stub
                                    appIcon.setEnabled(true);
                                }
                                
                            }).start();
                            
                            rootScrollView.setBackgroundColor(getResources().getColor(R.color.windows_bg));
                            rootFrame.setBackgroundColor(getResources().getColor(R.color.windows_bg));
                        }
                        
                    });
                }
            }, 100);
        }

        // Row Container    
       

        if (curAppInfo != null) {

            Intent intent = getPackageManager().getLaunchIntentForPackage(
                    curAppInfo.packageName);

            try {
                appResolveInfo = getPackageManager().resolveActivity(intent, 0);
                canLaunch = true;
            } catch (Exception e) {
                e.printStackTrace();
                canLaunch = false;
            }

            TextView appName = (TextView) findViewById(R.id.app_name);
            appName.setText(curAppInfo.appName);
            //appIcon.setImageBitmap(curAppInfo.iconBitmap);
            appIcon.setOnClickListener(new View.OnClickListener() {
                
                @Override
                public void onClick(View v) {
                    appIcon.setEnabled(false);
                    //ObjectAnimator rotationAni = ObjectAnimator.ofFloat(appIcon, "rotationY", 0f, 360f);
                    ObjectAnimator scaleXAni = ObjectAnimator.ofFloat(appIcon, "scaleX", 1f, 0.5f, 1f);
                    ObjectAnimator scaleYAni = ObjectAnimator.ofFloat(appIcon, "scaleY", 1f, 0.5f, 1f);
                    //ObjectAnimator alphaAni = ObjectAnimator.ofFloat(appIcon, "alpha", 0.5f, 1f);
                    AnimatorSet set = new AnimatorSet();
                    set.playTogether(scaleXAni,scaleYAni);//alphaAni rotationAni
                    set.setDuration(1000);
                    set.setInterpolator(new BounceInterpolator ()); //AnticipateOvershootInterpolator OvershootInterpolator
                    set.addListener(new AnimatorListenerAdapter(){
                        @Override
                        public void onAnimationEnd(Animator arg0) {
                            // TODO Auto-generated method stub
                            appIcon.setEnabled(true);
                        }

                        @Override
                        public void onAnimationStart(Animator arg0) {
                            // TODO Auto-generated method stub
                            appIcon.setEnabled(false);
                        } 
                    });
                    set.start();
                    
                 /*   ani.addListener(new AnimatorListenerAdapter(){

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
                    ani.start();*/
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

            if(curAppType == APP_TYPE_BACKUP)
            {
                view = rowContainer.findViewById(R.id.row_apk_info);
                fillRow(view, getString(R.string.apk_info),
                        curAppInfo.backupFilePath + "\n" + Utils.formatFileSize(curAppInfo.appRawSize)); 
                view = rowContainer.findViewById(R.id.row_time_info);
                fillRow(view, getString(R.string.time_info),
                        getString(R.string.last_updated_time)+"\n"+
                        Utils.formatTimeDisplayFull(new Date(curAppInfo.lastBackUpRawTime)));
            }
            else{
                view = rowContainer.findViewById(R.id.row_apk_info);
                fillRow(view, getString(R.string.apk_info),
                        curAppInfo.apkFilePath + "\n" + Utils.formatFileSize(curAppInfo.appRawSize));
                view = rowContainer.findViewById(R.id.row_time_info);
                fillRow(view, getString(R.string.time_info),
                        getString(R.string.last_updated_time)+"\n"+
                        Utils.formatTimeDisplayFull(new Date(curAppInfo.lastModifiedRawTime)));
            }
            
            view = rowContainer.findViewById(R.id.row_activity);
            if (appResolveInfo != null) {
                fillRow(view, getString(R.string.activity), appResolveInfo.activityInfo.name);
            } else {
                fillRow(view, getString(R.string.activity), "");
            }

            view = rowContainer.findViewById(R.id.row_componement);
            if (appResolveInfo != null) {
                String componentStr = "";
                ComponentName componentName = intent.getComponent();
                if (componentName != null) {
                    componentStr = componentName.toString();
                }
                fillRow(view, getString(R.string.component), componentStr);
            } else {
                fillRow(view, getString(R.string.component), "");
            }
        }
        
        menuLayout = (FilterMenuLayout) findViewById(R.id.menu);
        FilterMenu.Builder menuBuilder = new FilterMenu.Builder(this);
        buildActionMenu(menuBuilder);
        menuBuilder.attach(menuLayout);
        menuBuilder.build();
        
    }
    
    private void prepareAni(){
        //向下移动+alpha
        ViewHelper.setTranslationY(rowContainer, 250);
        ViewHelper.setAlpha(rowContainer, 0.5f);
        
        //向上移动+alpha
        ViewHelper.setTranslationY(headerImgView, -200);
        ViewHelper.setAlpha(headerImgView, 0.5f);
        
        ViewHelper.setAlpha(shadowView, 0.5f);
        ViewHelper.setTranslationY(shadowView, -200);

        //ViewHelper.setTranslationY(headerView, -200);
        ViewHelper.setScaleX(appIcon, 0);
        ViewHelper.setScaleY(appIcon, 0);
        ViewHelper.setAlpha(appIcon, 0.0f);
        
        //隐藏
        ViewHelper.setAlpha(headerView, 0.0f);
        
        contentRootView.setVisibility(View.INVISIBLE);
        headerImgView.setVisibility(View.INVISIBLE);
        appIcon.setEnabled(false);
        //shadowView.setVisibility(View.INVISIBLE);
    }
    
    
    private void buildActionMenu(FilterMenu.Builder menuBuilder){
        ArrayList<FilterMenuItemWapper> itemsWapper = new ArrayList<FilterMenuItemWapper>();
        switch (curAppType){
            case APP_TYPE_USER:
                if(canLaunch){
                    itemsWapper.add(new FilterMenuItemWapper(R.drawable.play_white, OPEN_ACTION));
                }
                itemsWapper.add(new FilterMenuItemWapper(R.drawable.info_white, INFO_ACTION));
                itemsWapper.add(new FilterMenuItemWapper(R.drawable.backup_white, BACKUP_ACTION));
                itemsWapper.add(new FilterMenuItemWapper(R.drawable.delete_white2, UNINSTALL_ACTION));
                itemsWapper.add(new FilterMenuItemWapper(R.drawable.share_white, SHARE_ACTION));
                break;
            case APP_TYPE_SYS:
                if(canLaunch){
                    itemsWapper.add(new FilterMenuItemWapper(R.drawable.play_white, OPEN_ACTION));
                }
                itemsWapper.add(new FilterMenuItemWapper(R.drawable.info_white, INFO_ACTION));
                itemsWapper.add(new FilterMenuItemWapper(R.drawable.backup_white, BACKUP_ACTION));
                itemsWapper.add(new FilterMenuItemWapper(R.drawable.share_white, SHARE_ACTION));
                break;
            case APP_TYPE_BACKUP:
                if(Utils.canBackupAppLaunch(AppDetailActivity.this, curAppInfo)) {
                    itemsWapper.add(new FilterMenuItemWapper(R.drawable.play_white, OPEN_ACTION));
                }
                itemsWapper.add(new FilterMenuItemWapper(R.drawable.add_white, INSTALL_ACTION));
                itemsWapper.add(new FilterMenuItemWapper(R.drawable.delete_white2, DELETE_ACTION));
                itemsWapper.add(new FilterMenuItemWapper(R.drawable.share_white, SHARE_ACTION));
                break;  
        }
        menuBuilder.addItemList(itemsWapper);
        
        menuBuilder.withListener(new FilterMenu.OnMenuChangeListener() {

                    @Override
                    public void onMenuItemClick(View view, int position, FilterMenuItemWapper itemWapper) {
                        
                        if(curAppInfo == null){
                            return;
                        } 
                        
                       if(itemWapper.MenuTag.equalsIgnoreCase(OPEN_ACTION)) {
                           if (Constants.MY_PACKAGE_NAME.equals(curAppInfo.packageName))// 避免再次启动自己app
                           {
                               MainActivity.T(R.string.launch_myself);
                           } else {
                               if(!Utils.launchApp(AppDetailActivity.this, curAppInfo)) {
                                   MainActivity.T(R.string.launch_fail);
                               }
                           }
                       }
                       else if(itemWapper.MenuTag.equalsIgnoreCase(INFO_ACTION)) {
                           Utils.showInstalledAppDetails(AppDetailActivity.this, curAppInfo.packageName);
                       }
                       else if(itemWapper.MenuTag.equalsIgnoreCase(UNINSTALL_ACTION)) {
                           Utils.uninstallApp(AppDetailActivity.this, curAppInfo);
                       }
                       else if(itemWapper.MenuTag.equalsIgnoreCase(BACKUP_ACTION)) {
                           String mBackUpFolder = Utils.getBackUpAPKfileDir(AppDetailActivity.this);
                           String sdAPKfileName = Utils.BackupApp(AppDetailActivity.this,curAppInfo, mBackUpFolder);
                           if (sdAPKfileName != null) {
                               SpannableString spanString =
                                       new SpannableString(curAppInfo.appName + " "
                                               + getString(R.string.backup_success));
                               spanString.setSpan(new UnderlineSpan(), 0, curAppInfo.appName.length(),
                                       Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                               spanString.setSpan(
                                       new ForegroundColorSpan(getResources().getColor(
                                               R.color.theme_blue_light)), 0, curAppInfo.appName.length(),
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
                                       .text(spanString);
                               mSnackbar.show(AppDetailActivity.this);

                               EventBus.getDefault().post(new AppBackupSuccEvent(curAppInfo));
                           }
                           else {
                               MainActivity.T(R.string.error);
                           }
                       }
                        else if(itemWapper.MenuTag.equalsIgnoreCase(SHARE_ACTION)) {
                            if(curAppType == APP_TYPE_BACKUP){
                                Utils.chooseSendByApp(AppDetailActivity.this, Uri.parse("file://" + curAppInfo.backupFilePath));
                            }
                            else{
                                Utils.chooseSendByApp(AppDetailActivity.this, Uri.parse("file://" + curAppInfo.apkFilePath));
                            }
                            
                            /*String BACK_UP_FOLDER = Utils.getBackUpAPKfileDir(AppDetailActivity.this);
                            String sdApkfileName = Utils.BackupApp(AppDetailActivity.this, curAppInfo, BACK_UP_FOLDER);
                            if (sdApkfileName != null) {
                                ArrayList<AppInfo> list = new ArrayList<AppInfo>();
                                list.add(curAppInfo);
                                EventBus.getDefault().post(new AppBackupSuccEvent(list));
                                Utils.chooseSendByApp(AppDetailActivity.this, Uri.parse("file://" + sdApkfileName));
                            } else {
                                MainActivity.T(R.string.error);
                            }*/
                        }
                        else if(itemWapper.MenuTag.equalsIgnoreCase(INSTALL_ACTION)) {
                              if (TextUtils.isEmpty(curAppInfo.backupFilePath)) {
                                  Utils.installAPK(AppDetailActivity.this, curAppInfo.apkFilePath);
                              } else {
                                  Utils.installAPK(AppDetailActivity.this, curAppInfo.backupFilePath);
                              }
                        }
                        else if(itemWapper.MenuTag.equalsIgnoreCase(DELETE_ACTION)) {
                            FileUtils.deleteQuietly(new File(curAppInfo.backupFilePath));
                            EventBus.getDefault().post(new BackupAppDeletedEvent(curAppInfo));
                            finish();
                        }
                    }

                    @Override
                    public void onMenuCollapse() {

                    }

                    @Override
                    public void onMenuExpand() {

                    }
                });
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

            //改变headerImgView的高度，使得headerImgView插入状态栏
            int newHeight = headerImgView.getLayoutParams().height + config.getStatusBarHeight();
            headerImgView.getLayoutParams().height = newHeight;
            headerImgView.requestLayout();

            if (hasNavBar) {
                contentRootView.setPadding(0, config.getStatusBarHeight(), 0, 0);
                tintManager.setNavigationBarTintEnabled(true);
                tintManager.setNavigationBarTintResource(R.color.primary);
            } else {
                contentRootView.setPadding(0, config.getStatusBarHeight(), 0, 0);
            }
        }
    }

    @Override
    public void onBackPressed() {
        Utils.finishActivtyWithAni(this);
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
                    Utils.finishActivtyWithAni(this);
                }
                break;
            case APP_CHANGED:
                break;
            default:
                break;
        }
    }

//    private void finishActivtyWithAni(){
//        finish();
//        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
//    }

    private void fillRow(View view, final String title, final String description) {
        TextView titleView = (TextView) view.findViewById(R.id.title);
        titleView.setText(title);

        TextView descriptionView = (TextView) view.findViewById(R.id.description);
        if (!TextUtils.isEmpty(description)) {
            descriptionView.setText(description);
        }

        view.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                // TODO Auto-generated method stub
                String copiedStr = title + ":" + description;
               
                Utils.copyToClipboard(AppDetailActivity.this, title, copiedStr);
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
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        // TODO Auto-generated method stub
        // 停止list滚动
        // MotionEvent me = MotionEvent.obtain(SystemClock.uptimeMillis(), SystemClock.uptimeMillis(),
        // MotionEvent.ACTION_CANCEL, 0, 0, 0);
        // sysAppsFrg.getListView().dispatchTouchEvent(me);
        // me.recycle();

        if (keyCode == KeyEvent.KEYCODE_MENU && event.getRepeatCount() == 0) {
            menuLayout.toggle(true);
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }
    
    
    @Override
    public void onScrollChanged(int scrollY, boolean firstScroll, boolean dragging) {
        // TODO Auto-generated method stub
        MainActivity.dismissSnackbar();
        if(scrollY == 0){
            ViewHelper.setAlpha(shadowView, 1f);
        }
        else{
            //ViewHelper.setAlpha(shadowView, 0f);
        }
        
        //向上滚动 视察效果
        //ViewHelper.setTranslationY(headerView, scrollY / 2);
        
        //向下滚动
        ViewHelper.setTranslationY(headerView, scrollY * 1.5f);
        //header固定不动
        //ViewHelper.setTranslationY(headerImgView, scrollY);
    }

    @Override
    public void onDownMotionEvent() {
        // TODO Auto-generated method stub

    }

    @Override
    public void onUpOrCancelMotionEvent(ScrollState scrollState) {
        // TODO Auto-generated method stub

    }
    
    
    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
        // Save UI state changes to the savedInstanceState.
        // This bundle will be passed to onCreate, onCreateView, and
        // onCreateView if the parent Activity is killed and restarted.
        // super.onSaveInstanceState(savedInstanceState);
    }
}
