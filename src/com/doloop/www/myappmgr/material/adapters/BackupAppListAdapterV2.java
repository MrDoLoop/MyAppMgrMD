package com.doloop.www.myappmgr.material.adapters;

import java.util.ArrayList;
import java.util.Locale;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.doloop.www.myappmgr.material.dao.AppInfo;
import com.doloop.www.myappmgr.material.fragments.BackupAppTabFragmentV2;
import com.doloop.www.myappmgr.material.interfaces.IconClickListener;
import com.doloop.www.myappmgr.material.utils.Constants;
import com.doloop.www.myappmgr.material.utils.Utils;
import com.doloop.www.myappmgr.material.widgets.RoundCornerProgressBar;
import com.doloop.www.myappmgrmaterial.R;
import com.nineoldandroids.animation.AnimatorSet;
import com.nineoldandroids.animation.ObjectAnimator;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Picasso.LoadedFrom;
import com.squareup.picasso.Target;

public class BackupAppListAdapterV2 extends BaseAdapter implements View.OnClickListener {
    private static final int TYPE_HEADER = 0;
    private static final int TYPE_ITEM = 1;
    // private RoundCornerProgressBar mHeaderProcessbar;
    private HeaderViewHolder mHeaderViewHolder;
    private boolean mIsDataInit = false;

    private ArrayList<AppInfo> mAppListDisplay;
    private ArrayList<AppInfo> mAppListFull;
    private Context mCtx;

    private int selectedCnt = 0;
    
    public IconClickListener mIconClickListener;
    
    /*
     * private DisplayImageOptions options = new DisplayImageOptions.Builder()
     * .showImageOnLoading(R.drawable.backupapp_holder) .cacheInMemory(true) .cacheOnDisk(false)
     * .bitmapConfig(Bitmap.Config.ARGB_8888) .build();
     */

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

    public void selectAll() {
        // mSelectedItems.clear();
        int size = getCount();
        for (int i = 0; i < size; i++) {
            // mSelectedItems.put(getItem(i).packageName, getItem(i));
            getItem(i).selected = true;
        }
        selectedCnt = size;
        this.notifyDataSetChanged();
    }

    public void deselectAll() {
        // mSelectedItems.clear();
        ArrayList<AppInfo> list = getDisplayList();
        for (AppInfo appInfo : list) {
            appInfo.selected = false;
        }
        selectedCnt = 0;
        this.notifyDataSetChanged();
    }

    public void toggleSelection(int position, boolean refreshList) {
        // setSelectedItem(position, !mSelectedItems.containsKey(position), refreshList);
        // String thePkgName = getItem(position).packageName;
        getItem(position).selected = !getItem(position).selected;
        setSelectedItem(position, getItem(position).selected, refreshList);
    }

    public void setSelectedItem(int position, boolean val, boolean refreshList) {
        if (val) {
            getItem(position).selected = true;
            selectedCnt++;
            if(selectedCnt > getCount()){
                selectedCnt = getCount();
            }
            // mSelectedItems.put(getItem(position).packageName, getItem(position));
        } else {
            getItem(position).selected = false;
            selectedCnt--;
            if(selectedCnt < 0){
                selectedCnt = 0;
            }
            // mSelectedItems.remove(getItem(position).packageName);
        }
        if (refreshList) {
            notifyDataSetChanged();
        }
    }

    public int getSelectedItemCnt() {
        return selectedCnt;
    }

    public ArrayList<AppInfo> getSelectedItemList() {
        ArrayList<AppInfo> retList = new ArrayList<AppInfo>();
        ArrayList<AppInfo> curList = getDisplayList();
        for (AppInfo appInfo : curList) {
            if (appInfo.selected) {
                retList.add(appInfo);
            }
        }

        return retList;
        /*
         * ArrayList<AppInfo> list = new ArrayList<AppInfo>(mSelectedItems.values()); return list;
         */
    }
    
    public void removeItem(ArrayList<AppInfo> list){
        mAppListFull.remove(list);
        mAppListDisplay.removeAll(list);
        notifyDataSetChanged();
    }
    
    public void removeItemAtPosition(int position){
        AppInfo theApp = mAppListDisplay.get(position);
        mAppListDisplay.remove(position);
        mAppListFull.remove(theApp);
        notifyDataSetChanged();
    }
    
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
        if (mAppListDisplay.size() == 0) {
            mAppListDisplay.add(AppInfo.DUMMY_APPINFO);
        } else if (mAppListDisplay.get(0) != AppInfo.DUMMY_APPINFO) {
            mAppListDisplay.add(0, AppInfo.DUMMY_APPINFO);
        }

        notifyDataSetChanged();
    }

    public void checkHeader() {

    }

    public BackupAppListAdapterV2(Context ctx, ArrayList<AppInfo> appList,IconClickListener l) {
        appList.add(0, AppInfo.DUMMY_APPINFO);
        mIsDataInit = false;
        mAppListFull = mAppListDisplay = appList;
        mCtx = ctx;
        mIconClickListener = l;
    }

    public void setDataSource(ArrayList<AppInfo> appList) {
        appList.add(0, AppInfo.DUMMY_APPINFO);
        mIsDataInit = true;
        mAppListFull = mAppListDisplay = appList;
    }

    public ArrayList<AppInfo> getDisplayList() {
        return mAppListDisplay;
    }

    public AppInfo getItem(int position) {
        return mAppListDisplay.get(position);
    }

    /**
     * 
     * @param progressFrom 短的
     * @param progressTo
     * @param secondaryProgressFrom 长的
     * @param secondaryProgressTo
     */
    public void animateProgress(float progressFrom, float progressTo, float secondaryProgressFrom,
            float secondaryProgressTo) {
        mHeaderViewHolder.headerProcessbar.animateProgress(progressFrom, progressTo, secondaryProgressFrom,
                secondaryProgressTo);
    }

    public void setHeaderProgress(float progress) {
        mHeaderViewHolder.headerProcessbar.setProgress(progress);
    }

    public void setHeaderSecProgress(float secondaryProgress) {
        mHeaderViewHolder.headerProcessbar.setSecondaryProgress(secondaryProgress);
    }

    private AnimatorSet buildHeaderTextItemAni(View targetView) {
        int playTime = 1200;
        AnimatorSet AniSet = new AnimatorSet();
        ObjectAnimator ani1 = ObjectAnimator.ofFloat(targetView, "rotationX", -90, 15, 0).setDuration(playTime);// .setDuration(500);
        ObjectAnimator ani2 = ObjectAnimator.ofFloat(targetView, "alpha", 0.5f, 0.75f, 1).setDuration(playTime);// .setDuration(500);
        AniSet.playTogether(ani1, ani2);
        return AniSet;
    }

    static class HeaderViewHolder {

        TextView backupDirTv;
        TextView sdUsedTv;
        TextView sdTotalTv;
        LinearLayout RootLayout;
        LinearLayout textLinear;
        RoundCornerProgressBar headerProcessbar;
    }

    /**
     * 
     * @return [0]:process [1]:secProcess
     */
    private float[] getHeaderProgress() {
        float process =
                (float) Utils.calculateTotalFileRawSize(mAppListFull) / (float) Utils.getSdTotalSpaceRawSize() * 100;
        float secProcess = (float) Utils.getSdUsedSpaceRawSize() / (float) Utils.getSdTotalSpaceRawSize() * 100;
        return new float[] { process, secProcess };
    }

    @Override
    public int getCount() {
        // TODO Auto-generated method stub
        return mAppListDisplay.size();
    }
    
    public int getAppItemCount(){
        if(mAppListDisplay.get(0) == AppInfo.DUMMY_APPINFO){
            return mAppListDisplay.size() - 1;
        }
        else{
            return mAppListDisplay.size();
        }
    }
    

    @Override
    public long getItemId(int position) {
        // TODO Auto-generated method stub
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // TODO Auto-generated method stub
        AppInfo appInfo = mAppListDisplay.get(position);

        int viewType = getItemViewType(position);
        ItemViewHolder itemHolder;
        HeaderViewHolder headerHolder;

        if (viewType == TYPE_HEADER) {
            if (convertView == null) {
                convertView = LayoutInflater.from(mCtx).inflate(R.layout.backup_list_header, parent, false);
                mHeaderViewHolder = headerHolder = new HeaderViewHolder();

                headerHolder.headerProcessbar = (RoundCornerProgressBar) convertView.findViewById(R.id.capacity_bar);
                headerHolder.textLinear = (LinearLayout) convertView.findViewById(R.id.textLinear);
                headerHolder. backupDirTv = (TextView) convertView.findViewById(R.id.textBackupDir);
                headerHolder.sdUsedTv = (TextView) convertView.findViewById(R.id.textSdUsed);
                headerHolder.sdTotalTv = (TextView) convertView.findViewById(R.id.textSdTotal);
                headerHolder.RootLayout = (LinearLayout) convertView.findViewById(R.id.bgLayout);
                headerHolder.RootLayout.setOnClickListener(new View.OnClickListener() {

                    @Override
                    public void onClick(View v) {

                        AnimatorSet AniSet1 = buildHeaderTextItemAni(mHeaderViewHolder.backupDirTv);
                        AnimatorSet AniSet2 = buildHeaderTextItemAni(mHeaderViewHolder.sdUsedTv);
                        AnimatorSet AniSet3 = buildHeaderTextItemAni(mHeaderViewHolder.sdTotalTv);

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
                convertView.setTag(headerHolder);
            }
            else{
                headerHolder = (HeaderViewHolder) convertView.getTag();
            }
            
            String[] sdUsedInfo = Utils.getSdUsedSpaceInfo();
            String[] sdTotalInfo = Utils.getSdTotalSpaceInfo();
            String[] backupDirInfo = Utils.calculateTotalFileInfo(mAppListFull);

            if(mIsDataInit){
                headerHolder.backupDirTv.setText(mCtx.getString(R.string.back_dir) + "\n" + backupDirInfo[1]);
            }
            else{
                headerHolder.backupDirTv.setText(mCtx.getString(R.string.back_dir) + "\n" + "---" );
            }
            
            headerHolder.sdUsedTv.setText(mCtx.getString(R.string.sd_used) + "\n" + sdUsedInfo[1]);
            headerHolder.sdTotalTv.setText(mCtx.getString(R.string.sd_total) + "\n" + sdTotalInfo[1]);

            float[] progressInfo = getHeaderProgress();
            float process = progressInfo[0];
            float secProcess = progressInfo[1];

            if (secProcess >= 80f) {
                headerHolder.sdUsedTv.setTextColor(mCtx.getResources().getColor(R.color.red_light));
                mHeaderViewHolder.headerProcessbar.setProgressColor(
                        mCtx.getResources().getColor(R.color.orange_light),
                        mCtx.getResources().getColor(R.color.red_light));
            } else {
                headerHolder.sdUsedTv.setTextColor(mCtx.getResources().getColor(R.color.green_light));
                mHeaderViewHolder.headerProcessbar.setProgressColor(
                        mCtx.getResources().getColor(R.color.orange_light),
                        mCtx.getResources().getColor(R.color.green_light));
            }

            mHeaderViewHolder.headerProcessbar.setProgress(process);
            mHeaderViewHolder.headerProcessbar.setSecondaryProgress(secProcess);
            
        }
        else if (viewType == TYPE_ITEM) {
            if (convertView == null || ((ItemViewHolder)convertView.getTag()).needInflate) {
                convertView = LayoutInflater.from(mCtx).inflate(R.layout.back_app_info_item, parent, false);
                itemHolder = new ItemViewHolder();
                itemHolder.AppNameTextView = (TextView) convertView.findViewById(R.id.app_name);
                itemHolder.AppVersionTextView = (TextView) convertView.findViewById(R.id.app_version);
                itemHolder.AppFileNameTextView = (TextView) convertView.findViewById(R.id.app_filename);
                itemHolder.AppIconImageView = (ImageView) convertView.findViewById(R.id.app_icon);
                itemHolder.AppIconImageView.setOnClickListener(this);
                itemHolder.RootLayout = (RelativeLayout) convertView.findViewById(R.id.rootLayout);
                itemHolder.needInflate = false;
                /*itemHolder.RootLayout.setOnClickListener(new View.OnClickListener() {

                    @Override
                    public void onClick(View v) {
                        // TODO Auto-generated method stub
                        int pos = (Integer) v.getTag();
                        AppInfo theApp = mAppListDisplay.get(pos);
                        if (TextUtils.isEmpty(theApp.backupFilePath)) {
                            Utils.installAPK(mCtx, theApp.apkFilePath);
                        } else {
                            Utils.installAPK(mCtx, theApp.backupFilePath);
                        }
                    }
                });*/
                convertView.setTag(itemHolder);
                //L.d("Item convertView inflate");
            }
            else{
                itemHolder = (ItemViewHolder) convertView.getTag();
                //L.d("Item convertView getTag");
            }
            
            
            itemHolder.AppNameTextView.setText(appInfo.appName);
            itemHolder.AppIconImageView.setTag(position);
            itemHolder.AppVersionTextView.setText("v" + appInfo.versionName + " | " + appInfo.appSizeStr + " | "
                    + appInfo.lastModifiedTimeStr);
            itemHolder.AppFileNameTextView.setText(appInfo.getBackupApkFileName());

            if (appInfo.iconBitmap == null) {
                // ImageLoader.getInstance().displayImage(Scheme.FILE.wrap(appInfo.getAppIconCachePath(mCtx).getAbsolutePath()),
                // holder.AppIconImageView, options);
                Picasso.with(mCtx).load(appInfo.getAppIconCachePath(mCtx)).noFade().into(itemHolder);
                // holder.AppIconImageView.setImageDrawable(Utils.getIconDrawable(mCtx, appInfo.packageName));
            } else {
                itemHolder.AppIconImageView.setImageBitmap(appInfo.iconBitmap);
            }

            itemHolder.AppIconImageView.setTag(position);
            itemHolder.RootLayout.setTag(position);
            
            if (BackupAppTabFragmentV2.isInActoinMode) {
                itemHolder.AppIconImageView.setOnClickListener(null);
                itemHolder.AppIconImageView.setClickable(false);
                itemHolder.AppIconImageView.setBackgroundResource(R.drawable.imageview_border_orange);
                if (appInfo.selected) {
                    itemHolder.RootLayout.setBackgroundResource(R.drawable.list_row_item_pressed_bg);
                    //holder.AppIconImageView.setBackgroundResource(R.drawable.imageview_border_blue);
                } else {
                    itemHolder.RootLayout.setBackgroundResource(R.drawable.list_row_item_bg);
                    //holder.AppIconImageView.setBackgroundResource(R.drawable.user_app_icon_bg);
                }
            } else {
                itemHolder.AppIconImageView.setOnClickListener(this);
                itemHolder.AppIconImageView.setClickable(true);
                itemHolder.RootLayout.setBackgroundResource(R.drawable.list_row_item_bg);
                itemHolder.AppIconImageView.setBackgroundResource(R.drawable.backup_app_icon_bg);
            }    
        }
        
        return convertView;
    }

    public static class ItemViewHolder implements Target {

        TextView AppNameTextView;
        TextView AppVersionTextView;
        TextView AppFileNameTextView;
        ImageView AppIconImageView;
        RelativeLayout RootLayout;
        public boolean needInflate;

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
     * 返回所有的layout的数量
     * 
     * */
    @Override
    public int getViewTypeCount() {
        return 2;
    }

    @Override
    public void onClick(View v) {
        // TODO Auto-generated method stub
        int pos = (Integer) v.getTag();
        mIconClickListener.OnIconClickListener(pos);
        /*AppInfo appInfo = null;
        try {// 因为添加了动画，点击过快的话，删除动画还没有完成，此处就崩溃了
            appInfo = mAppListDisplay.get(pos);
        } catch (Exception e) {
            return;
        }

        if (FileUtils.deleteQuietly(new File(appInfo.backupFilePath))) {
            // 从显示的list中删除
            mAppListDisplay.remove(pos);
            // 从总共的list中删除
            for (int i = 0; i < mAppListFull.size(); i++) {
                if (mAppListFull.get(i).packageName.equals(appInfo.packageName)) {
                    mAppListFull.remove(i);
                    break;
                }
            }
            notifyDataSetChanged();
        } else {
            MainActivity.T(R.string.error);
        }*/
    }

}
