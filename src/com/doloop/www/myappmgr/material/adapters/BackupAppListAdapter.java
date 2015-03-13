package com.doloop.www.myappmgr.material.adapters;

import java.io.File;
import java.util.ArrayList;
import java.util.Locale;

import org.apache.commons.io.FileUtils;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.RecyclerView.ViewHolder;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.doloop.www.myappmgr.material.MainActivity;
import com.doloop.www.myappmgr.material.dao.AppInfo;
import com.doloop.www.myappmgr.material.utils.Constants;
import com.doloop.www.myappmgr.material.utils.Utils;
import com.doloop.www.myappmgr.material.widgets.RoundCornerProgressBar;
import com.doloop.www.myappmgrmaterial.R;
import com.nineoldandroids.animation.AnimatorSet;
import com.nineoldandroids.animation.ObjectAnimator;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Picasso.LoadedFrom;
import com.squareup.picasso.Target;

public class BackupAppListAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private static final int TYPE_HEADER = 0;
    private static final int TYPE_ITEM = 1;
    //private RoundCornerProgressBar mHeaderProcessbar;
    private HeaderViewHolder mHeaderViewHolder;

    public BackupAppListDataSetChangedListener mBackupAppListDataSetChangedListener;

    public interface BackupAppListDataSetChangedListener {
        public void OnBackupAppListDataSetChanged();
    }

    public void setUserAppListDataSetChangedListener(BackupAppListDataSetChangedListener l) {
        this.mBackupAppListDataSetChangedListener = l;
    }

    public BackupAppListDataSetChangedListener getBackupAppListDataSetChangedListener() {
        return this.mBackupAppListDataSetChangedListener;
    }

    private ArrayList<AppInfo> mAppListDisplay;
    private ArrayList<AppInfo> mAppListFull;
    private Context mCtx;

    public void filterList(String str) {
        mAppListDisplay = mAppListFull;
        if (!TextUtils.isEmpty(str)) {
            ArrayList<AppInfo> filteredAppList = new ArrayList<AppInfo>();
            AppInfo appInfo;
            for (int i = 0; i < mAppListFull.size(); i++) {
                appInfo = mAppListFull.get(i);
                if (appInfo.appName.toLowerCase(Locale.getDefault()).contains(str)
                        || appInfo.appNamePinyin.toLowerCase(Locale.getDefault()).contains(str)) {
                    filteredAppList.add(appInfo);
                }
            }
            mAppListDisplay = filteredAppList;
        }
        if(mAppListDisplay.size() == 0){
            mAppListDisplay.add(AppInfo.DUMMY_APPINFO);
        }
        else if(mAppListDisplay.get(0) != AppInfo.DUMMY_APPINFO){
            mAppListDisplay.add(0, AppInfo.DUMMY_APPINFO);
        }
        
        notifyDataSetChanged();
    }

    public void checkHeader(){
        
    }
    
    public BackupAppListAdapter(Context ctx, ArrayList<AppInfo> appList) {
        appList.add(0, AppInfo.DUMMY_APPINFO);
        mAppListFull = mAppListDisplay = appList;
        mCtx = ctx;
    }
    
    public void setDataSource(ArrayList<AppInfo> appList){
        appList.add(0, AppInfo.DUMMY_APPINFO);
        mAppListFull = mAppListDisplay = appList;
    }

    public ArrayList<AppInfo> getDisplayList() {
        return mAppListDisplay;
    }

    public AppInfo getItem(int position) {
        return mAppListDisplay.get(position);
    }

    @Override
    public int getItemCount() {
        // TODO Auto-generated method stub
        return mAppListDisplay.size();
    }

    @Override
    public void onBindViewHolder(ViewHolder viewHolder, int position) {
        // TODO Auto-generated method stub
        if (getItemViewType(position) == TYPE_HEADER) {
            HeaderViewHolder holder = (HeaderViewHolder) viewHolder;
            String[] sdUsedInfo = Utils.getSdUsedSpaceInfo();
            String[] sdTotalInfo = Utils.getSdTotalSpaceInfo();
            String[] backupDirInfo = Utils.calculateTotalFileInfo(mAppListFull);
            
            holder.backupDirTv.setText(mCtx.getString(R.string.back_dir)+"\n"+backupDirInfo[1]);
            holder.sdUsedTv.setText(mCtx.getString(R.string.sd_used)+"\n"+sdUsedInfo[1]);
            holder.sdTotalTv.setText(mCtx.getString(R.string.sd_total)+"\n"+sdTotalInfo[1]);

            float[] progressInfo = getHeaderProgress();
            float process = progressInfo[0];
            float secProcess = progressInfo[1];
            
            if(secProcess >= 80f){
                holder.sdUsedTv.setTextColor(mCtx.getResources().getColor(R.color.red_light));
                mHeaderViewHolder.headerProcessbar.setProgressColor(mCtx.getResources().getColor(R.color.orange_light), mCtx.getResources().getColor(R.color.red_light));
            }
            else{
                holder.sdUsedTv.setTextColor(mCtx.getResources().getColor(R.color.green_light));
                mHeaderViewHolder.headerProcessbar.setProgressColor(mCtx.getResources().getColor(R.color.orange_light), mCtx.getResources().getColor(R.color.green_light));
            }
            
            mHeaderViewHolder.headerProcessbar.setProgress(process);
            mHeaderViewHolder.headerProcessbar.setSecondaryProgress(secProcess);
            
        } else if (getItemViewType(position) == TYPE_ITEM) {
            AppInfo appInfo = mAppListDisplay.get(position);
            ItemViewHolder holder = (ItemViewHolder) viewHolder;
            holder.AppNameTextView.setText(appInfo.appName);
            // holder.AppIconImageView.setImageBitmap(appInfo.iconBitmap);
            holder.AppIconImageView.setTag(position);
            holder.AppVersionTextView.setText("v" + appInfo.versionName + " | " + appInfo.appSizeStr + " | "
                    + appInfo.lastModifiedTimeStr);
            holder.AppFileNameTextView.setText(appInfo.getBackupApkFileName());
            holder.RootLayout.setTag(appInfo);

            if (appInfo.iconBitmap == null) {
                Picasso.with(mCtx).load(appInfo.getAppIconCachePath(mCtx)).noFade().into(holder);
                //holder.AppIconImageView.setImageDrawable(Utils.getIconDrawable(mCtx, appInfo.packageName));
            } else {
                holder.AppIconImageView.setImageBitmap(appInfo.iconBitmap);
            }
        }
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        // TODO Auto-generated method stub
        Context context = parent.getContext();
        if (viewType == TYPE_HEADER) {
            final View view = LayoutInflater.from(context).inflate(R.layout.backup_list_header, parent, false);
            return mHeaderViewHolder = new HeaderViewHolder(view);
        } else if (viewType == TYPE_ITEM) {
            final View view = LayoutInflater.from(context).inflate(R.layout.back_app_info_item, parent, false);
            return new ItemViewHolder(view);
        }
        throw new RuntimeException("There is no type that matches the type " + viewType
                + " + make sure your using types correctly");
    }

    @Override
    public int getItemViewType(int position) {
        if (mAppListDisplay.get(position) == AppInfo.DUMMY_APPINFO) {
            return TYPE_HEADER;
        } else {
            return TYPE_ITEM;
        }

    }
    /**
     * 
     * @param progressFrom 短的
     * @param progressTo
     * @param secondaryProgressFrom 长的
     * @param secondaryProgressTo
     */
    public void animateProgress(float progressFrom, float progressTo, float secondaryProgressFrom, float secondaryProgressTo){
        mHeaderViewHolder.headerProcessbar.animateProgress(progressFrom, progressTo, secondaryProgressFrom, secondaryProgressTo);
    }
    
    public void setHeaderProgress(float progress){
        mHeaderViewHolder.headerProcessbar.setProgress(progress);
    }
    
    public void setHeaderSecProgress(float secondaryProgress){
        mHeaderViewHolder.headerProcessbar.setSecondaryProgress(secondaryProgress);
    }
    
    private AnimatorSet buildHeaderTextItemAni(View targetView){
        int playTime = 1200;
        AnimatorSet AniSet = new AnimatorSet();
        ObjectAnimator ani1 = ObjectAnimator.ofFloat(targetView, "rotationX", -90, 15, 0).setDuration(playTime);//.setDuration(500);
        ObjectAnimator ani2 = ObjectAnimator.ofFloat(targetView, "alpha", 0.5f, 0.75f, 1).setDuration(playTime);//.setDuration(500);
        AniSet.playTogether(ani1,ani2);
        return AniSet;
    }
    
    public class HeaderViewHolder extends RecyclerView.ViewHolder {

        TextView backupDirTv;
        TextView sdUsedTv;
        TextView sdTotalTv;
        LinearLayout RootLayout;
        LinearLayout textLinear;
        RoundCornerProgressBar headerProcessbar;
        

        public HeaderViewHolder(View view) {
            super(view);
            headerProcessbar = (RoundCornerProgressBar)view.findViewById(R.id.capacity_bar);
            textLinear = (LinearLayout) view.findViewById(R.id.textLinear);
            backupDirTv = (TextView) view.findViewById(R.id.textBackupDir);
            sdUsedTv = (TextView) view.findViewById(R.id.textSdUsed);
            sdTotalTv = (TextView) view.findViewById(R.id.textSdTotal);
            RootLayout = (LinearLayout) view.findViewById(R.id.bgLayout);
            RootLayout.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {
                    
                    AnimatorSet AniSet1 = buildHeaderTextItemAni(backupDirTv);
                    AnimatorSet AniSet2 = buildHeaderTextItemAni(sdUsedTv);
                    AnimatorSet AniSet3 = buildHeaderTextItemAni(sdTotalTv);
                    
                    AniSet1.start();
                    AniSet2.setStartDelay(200);
                    AniSet2.start();
                    AniSet3.setStartDelay(400);
                    AniSet3.start();

                    float[] progressInfo = getHeaderProgress();
                    float process = progressInfo[0];
                    float secProcess = progressInfo[1];
                    mHeaderViewHolder.headerProcessbar.animateProgress(0, process, 0, secProcess);
                }
            });
            
            //mHeaderProcessbar = (RoundCornerProgressBar)view.findViewById(R.id.capacity_bar);
            
            
            /*
             * AppNameTextView = (TextView) view.findViewById(R.id.app_name); AppVersionTextView = (TextView)
             * view.findViewById(R.id.app_version); AppFileNameTextView = (TextView)
             * view.findViewById(R.id.app_filename); AppIconImageView = (ImageView) view.findViewById(R.id.app_icon);
             * AppIconImageView.setOnClickListener(new View.OnClickListener() {
             * 
             * @Override public void onClick(View v) { // TODO Auto-generated method stub
             * //MainActivity.T("Backup图标被点击了: "+AppIconImageView.getTag()); mAppListDisplay.remove(getPosition());
             * notifyDataSetChanged(); AppInfo appInfo = null; try{//因为添加了动画，点击过快的话，删除动画还没有完成，此处就崩溃了 appInfo =
             * mAppListDisplay.get(getPosition()); }catch(Exception e){ return ; }
             * 
             * 
             * //if(FileUtils.deleteQuietly(new File(appInfo.apkFilePath))){ if(FileUtils.deleteQuietly(new
             * File(appInfo.backupFilePath))){ //从显示的list中删除 mAppListDisplay.remove(getPosition()); //从总共的list中删除
             * for(int i =0;i<mAppListFull.size();i++){ if(mAppListFull.get(i).packageName.equals(appInfo.packageName)){
             * mAppListFull.remove(i); break; } }
             * 
             * notifyItemRemoved(getPosition());
             * //getBackupAppListDataSetChangedListener().OnBackupAppListDataSetChanged(); } else{
             * MainActivity.T(R.string.error); } } }); RootLayout = (RelativeLayout) view.findViewById(R.id.rootLayout);
             * RootLayout.setOnClickListener(new View.OnClickListener() {
             * 
             * @Override public void onClick(View v) { // TODO Auto-generated method stub AppInfo theApp =
             * mAppListDisplay.get(getPosition()); if(TextUtils.isEmpty(theApp.backupFilePath)){ Utils.installAPK(mCtx,
             * theApp.apkFilePath); } else{ Utils.installAPK(mCtx, theApp.backupFilePath); } } });
             */
        }
    }

    public class ItemViewHolder extends RecyclerView.ViewHolder implements Target {

        TextView AppNameTextView;
        TextView AppVersionTextView;
        TextView AppFileNameTextView;
        ImageView AppIconImageView;
        RelativeLayout RootLayout;

        @Override
        public void onBitmapFailed(Drawable bitmap) {
            // TODO Auto-generated method stub

        }

        @Override
        public void onBitmapLoaded(Bitmap bitmap, LoadedFrom from) {
            // TODO Auto-generated method stub
            AppIconImageView.setImageBitmap(bitmap);
            if (Constants.SAVE_APP_ICON_IN_OBJ) {
                AppInfo appInfo = (AppInfo) RootLayout.getTag();
                // if (appInfo.iconBitmap == null)
                // if(appInfo != null)
                {
                    appInfo.iconBitmap = bitmap;
                }
            }
        }

        @Override
        public void onPrepareLoad(Drawable placeHolderDrawable) {
            // TODO Auto-generated method stub
            AppIconImageView.setImageResource(R.drawable.backupapp_holder);
        }

        public ItemViewHolder(View view) {
            super(view);
            AppNameTextView = (TextView) view.findViewById(R.id.app_name);
            AppVersionTextView = (TextView) view.findViewById(R.id.app_version);
            AppFileNameTextView = (TextView) view.findViewById(R.id.app_filename);
            AppIconImageView = (ImageView) view.findViewById(R.id.app_icon);
            AppIconImageView.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {
                    // TODO Auto-generated method stub
                    // MainActivity.T("Backup图标被点击了: "+AppIconImageView.getTag());
                    /*
                     * mAppListDisplay.remove(getPosition()); notifyDataSetChanged();
                     */
                    AppInfo appInfo = null;
                    try {// 因为添加了动画，点击过快的话，删除动画还没有完成，此处就崩溃了
                        appInfo = mAppListDisplay.get(getPosition());
                    } catch (Exception e) {
                        return;
                    }

                    if (FileUtils.deleteQuietly(new File(appInfo.backupFilePath))) {
                        // 从显示的list中删除
                        mAppListDisplay.remove(getPosition());
                        // 从总共的list中删除
                        for (int i = 0; i < mAppListFull.size(); i++) {
                            if (mAppListFull.get(i).packageName.equals(appInfo.packageName)) {
                                mAppListFull.remove(i);
                                break;
                            }
                        }
                        onBindViewHolder(mHeaderViewHolder,0);
                        /*String[] sdUsedInfo = Utils.getSdUsedSpaceInfo();
                        String[] sdTotalInfo = Utils.getSdTotalSpaceInfo();
                        String[] backupDirInfo = Utils.calculateTotalFileInfo(mAppListFull);
                               
                        mHeaderViewHolder.backupDirTv.setText(mCtx.getString(R.string.back_dir)+"\n"+backupDirInfo[1]);
                        mHeaderViewHolder.sdUsedTv.setText(mCtx.getString(R.string.sd_used)+"\n"+sdUsedInfo[1]);
                        mHeaderViewHolder.sdTotalTv.setText(mCtx.getString(R.string.sd_total)+"\n"+sdTotalInfo[1]);

                        float[] progressInfo = getHeaderProgress();
                        float process = progressInfo[0];
                        float secProcess = progressInfo[1];
                        
                        mHeaderViewHolder.headerProcessbar.animateProgress(mHeaderViewHolder.headerProcessbar.getProgress(), process, 
                                mHeaderViewHolder.headerProcessbar.getSecondaryProgress(), secProcess);*/
                        
                        notifyItemRemoved(getPosition());
                        
//                        MainActivity.T("dir: "+Utils.getBackupDirSizeStr(mCtx)+" sd used "+Utils.getSdUsedSpaceStr()
//                                +" sd total: "+Utils.getSdTotalSpace());
                        // getBackupAppListDataSetChangedListener().OnBackupAppListDataSetChanged();
                    } else {
                        MainActivity.T(R.string.error);
                    }
                }
            });
            RootLayout = (RelativeLayout) view.findViewById(R.id.rootLayout);
            RootLayout.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {
                    // TODO Auto-generated method stub
                    
                    AppInfo theApp = mAppListDisplay.get(getPosition());
                    if (TextUtils.isEmpty(theApp.backupFilePath)) {
                        Utils.installAPK(mCtx, theApp.apkFilePath);
                    } else {
                        Utils.installAPK(mCtx, theApp.backupFilePath);
                    }
                }
            });
        }
    }
    /**
     * 
     * @return [0]:process  [1]:secProcess
     */
    private float[] getHeaderProgress(){
        float process = (float)Utils.calculateTotalFileRawSize(mAppListFull) / (float)Utils.getSdTotalSpaceRawSize() * 100;
        float secProcess = (float)Utils.getSdUsedSpaceRawSize() / (float)Utils.getSdTotalSpaceRawSize() * 100;
        return new float[]{process,secProcess};
    }
    

}
