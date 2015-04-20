package com.doloop.www.myappmgr.material;

import java.io.File;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.Toolbar;
import android.text.TextPaint;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.afollestad.materialdialogs.MaterialDialog;
import com.afollestad.materialdialogs.util.TypefaceHelper;
import com.doloop.www.myappmgr.material.events.DrawerItemClickEvent;
import com.doloop.www.myappmgr.material.events.DrawerItemClickEvent.DrawerItem;
import com.doloop.www.myappmgr.material.fragments.FolderSelectorDialog;
import com.doloop.www.myappmgr.material.fragments.FolderSelectorDialog.FolderSelectCallback;
import com.doloop.www.myappmgr.material.swipeback.lib.SwipeBackActivity;
import com.doloop.www.myappmgr.material.utils.ScrimUtil;
import com.doloop.www.myappmgr.material.utils.Utils;
import com.nineoldandroids.animation.Animator;
import com.nineoldandroids.animation.AnimatorListenerAdapter;
import com.nineoldandroids.animation.ObjectAnimator;
import com.readystatesoftware.systembartint.SystemBarTintManager;
import com.readystatesoftware.systembartint.SystemBarTintManager.SystemBarConfig;

import de.greenrobot.event.EventBus;

public class SettingActivity extends SwipeBackActivity implements FolderSelectCallback {
    private TextView categoryCommon;
    private TextView categoryInfo;
    private View backDirView;
    private View playAniAppDetails;
    private View openSource;
    private View appInfoRow;
    private TextView emailTv;
    private TextView qqTv;
    private boolean iconAniRunning = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting_layout);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        ActionBar mActionBar = getSupportActionBar();
        mActionBar.setDisplayHomeAsUpEnabled(true);

        Drawable shadow = ScrimUtil.makeCubicGradientScrimDrawable(
                // Color.parseColor("#7d000000"),
                        Color.GRAY,// "#55000000"
                        8, // 渐变层数
                        Gravity.TOP);
        View shadowView = findViewById(R.id.shadow);
        Utils.setBackgroundDrawable(shadowView, shadow);
        
        emailTv = (TextView) findViewById(R.id.email_tv);
        emailTv.setOnClickListener(new View.OnClickListener() {
            
            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                sendMail(emailTv.getText().toString().trim());
               
            }
        });
        emailTv.setOnLongClickListener(new View.OnLongClickListener() {
            
            @Override
            public boolean onLongClick(View v) {
                // TODO Auto-generated method stub
                Utils.copyToClipboard(SettingActivity.this, "email", emailTv.getText().toString().trim());
                MainActivity.T("Email "+getString(R.string.copied));
                return true;
            }
        });
        qqTv = (TextView) findViewById(R.id.qq_tv);
        qqTv.setOnLongClickListener(new View.OnLongClickListener() {
            
            @Override
            public boolean onLongClick(View v) {
                // TODO Auto-generated method stub
                Utils.copyToClipboard(SettingActivity.this, "qq", qqTv.getText().toString().trim());
                MainActivity.T("QQ "+getString(R.string.copied));
                return true;
            }
        });
        categoryCommon = (TextView) findViewById(R.id.common_cate);
        fillCategory(categoryCommon, R.string.common);
        categoryInfo = (TextView) findViewById(R.id.info_cate);
        fillCategory(categoryInfo, R.string.app_info);

        backDirView = findViewById(R.id.backup_dir);
        fillTwoRowsInfo(backDirView, getString(R.string.back_dir), Utils.getBackUpAPKfileDir(this));
        backDirView.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                if(Utils.existSDCard())
                //if(MainActivity.sIsSdcardReady)
                {
                    new FolderSelectorDialog().show(getSupportFragmentManager(), SettingActivity.this);
                }
                else{
                    MainActivity.T(R.string.no_sd);
                }
            }
        });
        playAniAppDetails = findViewById(R.id.app_details_ani);
        boolean playAni = Utils.playAniAppDetails(this);
        if (playAni) {
            fillTwoRowsChkBox(playAniAppDetails, getString(R.string.play_ani_app_details),
                    getString(R.string.play_ani), playAni, false);
        } else {
            fillTwoRowsChkBox(playAniAppDetails, getString(R.string.play_ani_app_details),
                    getString(R.string.not_paly_ani), playAni, false);
        }
        playAniAppDetails.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                boolean playAni = !Utils.playAniAppDetails(SettingActivity.this);
                Utils.setPlayAniAppDetails(SettingActivity.this, playAni);
                if (playAni) {
                    fillTwoRowsChkBox(playAniAppDetails, getString(R.string.play_ani_app_details),
                            getString(R.string.play_ani), playAni, true);
                } else {
                    fillTwoRowsChkBox(playAniAppDetails, getString(R.string.play_ani_app_details),
                            getString(R.string.not_paly_ani), playAni, true);
                }
            }
        });

        openSource = findViewById(R.id.opensource);
        fillTwoRowsInfo(openSource, R.string.open_soure_license, R.string.thx_github);
        openSource.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
               
            }
        });
        appInfoRow = findViewById(R.id.app_info);
        fillTwoRowsIcon(appInfoRow, getString(R.string.app_info), Utils.getSelfVerName(this), R.drawable.app_icon);
        appInfoRow.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                if(!iconAniRunning){
                    iconAniRunning = true;
                    ObjectAnimator ani = ObjectAnimator.ofFloat(appInfoRow.findViewById(R.id.icon), "rotationY", 0, 360).setDuration(1000);
                    ani.addListener(new AnimatorListenerAdapter(){

                        @Override
                        public void onAnimationEnd(Animator arg0) {
                            // TODO Auto-generated method stub
                            iconAniRunning = false;
                        }} );
                    ani.start();
                }
            }
        });
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onPostCreate(savedInstanceState);
        if (Build.VERSION.SDK_INT == Build.VERSION_CODES.KITKAT) {
            // Window window = this.getWindow();
            // window.setFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS,
            // WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);

            boolean hasNavBar = Utils.hasNavBar(this);
            SystemBarTintManager tintManager = new SystemBarTintManager(this);
            SystemBarConfig config = tintManager.getConfig();
            // 处理状态栏
            tintManager.setStatusBarTintEnabled(true);
            tintManager.setStatusBarTintResource(R.color.transparent);
            tintManager.setStatusBarAlpha(0.5f);

            View contHolder = findViewById(R.id.root);
            if (hasNavBar) {
                tintManager.setNavigationBarTintEnabled(true);
                tintManager.setNavigationBarTintResource(R.color.primary);
                contHolder.setPadding(0, config.getStatusBarHeight(), 0, config.getNavigationBarHeight());
            } else {
                contHolder.setPadding(0, config.getStatusBarHeight(), 0, 0);
            }
        }
    }

    private void fillCategory(TextView tv, int strId) {
        tv.setTypeface(TypefaceHelper.get(SettingActivity.this, "RobotoMedium"));// RobotoRegular
        TextPaint tp = tv.getPaint();
        tp.setFakeBoldText(true);
        tv.setText(strId);
    }

    private void fillTwoRowsInfo(View ItemView, String titleStr, String summaryStr) {
        TextView title = (TextView) ItemView.findViewById(R.id.title);
        title.setTypeface(TypefaceHelper.get(SettingActivity.this, "RobotoRegular"));// RobotoRegular
//        TextPaint tp = title.getPaint();
//        tp.setFakeBoldText(true);
        title.setText(titleStr);
        TextView summary = (TextView) ItemView.findViewById(R.id.summary);
        summary.setTypeface(TypefaceHelper.get(SettingActivity.this, "RobotoRegular"));//
        summary.setText(summaryStr);
    }

    private void fillTwoRowsInfo(View ItemView, int titleId, int summaryId) {
        fillTwoRowsInfo(ItemView, getString(titleId), getString(summaryId));
    }

    private void fillTwoRowsChkBox(View ItemView, int titleId, int summaryId, boolean isChecked, boolean playCheckAni) {
        fillTwoRowsChkBox(ItemView, getString(titleId), getString(summaryId), isChecked, playCheckAni);
    }

    private void fillTwoRowsChkBox(View ItemView, String titleId, String summaryId, boolean isChecked, boolean playCheckAni) {
        fillTwoRowsInfo(ItemView, titleId, summaryId);
        com.rey.material.widget.CheckBox chk = (com.rey.material.widget.CheckBox) ItemView.findViewById(R.id.chkbox);
        if(playCheckAni){
            chk.setChecked(isChecked);
        }
        else{
            chk.setCheckedImmediately(isChecked);
        }
    }

    private void fillTwoRowsIcon(View ItemView, String title, String summary, int IconResId) {
        fillTwoRowsInfo(ItemView, title, summary);
        ImageView icon = (ImageView) ItemView.findViewById(R.id.icon);
        icon.setImageResource(IconResId);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            Utils.finishActivtyWithAni(this);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        Utils.finishActivtyWithAni(this);
    }

    public void sendMail(String addr) {
        boolean handled = false;
        
        try{
            //http://stackoverflow.com/questions/6506637/only-email-apps-to-resolve-an-intent
            //有bug如果没有emial客户端就崩溃了
            Intent intent = new Intent(Intent.ACTION_SENDTO);
            intent.setData(Uri.parse("mailto:"+addr));
            startActivity(intent);
            return;
        }
        catch(Exception e){
            e.printStackTrace();
            handled = false;
        }
        
        if(!handled){
            String[] reciver = new String[] { addr }; 
            String mybody = "";  
            Intent myIntent = new Intent(android.content.Intent.ACTION_SEND);  
            myIntent.setType("plain/text");  
            myIntent.putExtra(android.content.Intent.EXTRA_EMAIL, reciver);    
            myIntent.putExtra(android.content.Intent.EXTRA_TEXT, mybody);  
            startActivity(myIntent);  
        }
       
    }  
    
    
    @Override
    public void onFolderSelection(File folder) {
        // TODO Auto-generated method stub
        final String newpath = folder.getAbsolutePath()+"/";
        final String curBackupPath = Utils.getBackUpAPKfileDir(SettingActivity.this);
        
        if(!curBackupPath.equals(newpath)){
            fillTwoRowsInfo(backDirView, getString(R.string.back_dir), newpath);
            Utils.saveBackUpAPKfileDir(SettingActivity.this, newpath);
            MaterialDialog dialog = new MaterialDialog.Builder(SettingActivity.this)
            .content(R.string.move_apk)
            .positiveText(R.string.yes)
            .positiveColorRes(R.color.red_light)
            .negativeText(R.string.no)
            .callback(new MaterialDialog.ButtonCallback() {
                @Override
                public void onPositive(MaterialDialog dialog) {
                    DrawerItemClickEvent changeDirEv = new DrawerItemClickEvent(DrawerItem.CHG_BACKUP_DIR);
                    changeDirEv.oldPath = curBackupPath;
                    changeDirEv.newPath = newpath;
                    EventBus.getDefault().post(changeDirEv);
                }

                @Override
                public void onNegative(MaterialDialog dialog) {
                    
                }
            }).build();
            dialog.setCanceledOnTouchOutside(false);
            dialog.show();
        }
    }
}
