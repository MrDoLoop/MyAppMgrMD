package com.doloop.www.myappmgr.material.adapters;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
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
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.doloop.www.myappmgr.material.MainActivity;
import com.doloop.www.myappmgr.material.dao.AppInfo;
import com.doloop.www.myappmgr.material.utils.Constants;
import com.doloop.www.myappmgr.material.utils.Utils;
import com.doloop.www.myappmgr.material.widgets.ParallaxRecyclerAdapter;
import com.doloop.www.myappmgrmaterial.R;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;
import com.squareup.picasso.Picasso.LoadedFrom;

public class BackupAppListAdapter extends ParallaxRecyclerAdapter<AppInfo> {

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
        notifyDataSetChanged();
    }

    public BackupAppListAdapter(Context ctx, ArrayList<AppInfo> appList) {
        super(appList);
        mAppListFull = mAppListDisplay = appList;
        mCtx = ctx;
        super.implementRecyclerAdapterMethods(new ParallaxRecyclerAdapter.RecyclerAdapterMethods() {

            @Override
            public void onBindViewHolder(android.support.v7.widget.RecyclerView.ViewHolder viewHolder, int position) {
                // TODO Auto-generated method stub
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
                } else {
                    holder.AppIconImageView.setImageBitmap(appInfo.iconBitmap);
                }
            }

            @Override
            public android.support.v7.widget.RecyclerView.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
                // TODO Auto-generated method stub
                View v = null;
                ViewHolder holder = null;
                v = LayoutInflater.from(mCtx).inflate(R.layout.back_app_info_item, viewGroup, false);
                holder = new ItemViewHolder(v);
                return holder;
            }

            @Override
            public int getItemCount() {
                // TODO Auto-generated method stub
                return mAppListDisplay.size();
            }
        });
    }

    public ArrayList<AppInfo> getDisplayList() {
        return mAppListDisplay;
    }

    /*public AppInfo getItem(int position) {
        return mAppListDisplay.get(position);
    }*/
    
    public int getAppItemCount(){
        return mAppListDisplay.size();
    }

    /*
     * @Override public int getItemCount() { // TODO Auto-generated method stub return mAppListDisplay.size(); }
     */
    /*
     * @Override public void onBindViewHolder(ViewHolder viewHolder, int position) { // TODO Auto-generated method stub
     * AppInfo appInfo = mAppListDisplay.get(position); ItemViewHolder holder = (ItemViewHolder) viewHolder;
     * holder.AppNameTextView.setText(appInfo.appName); //holder.AppIconImageView.setImageBitmap(appInfo.iconBitmap);
     * holder.AppIconImageView.setTag(position); holder.AppVersionTextView.setText("v" + appInfo.versionName + " | " +
     * appInfo.appSizeStr + " | " + appInfo.lastModifiedTimeStr);
     * holder.AppFileNameTextView.setText(appInfo.getBackupApkFileName()); holder.RootLayout.setTag(appInfo);
     * 
     * 
     * if (appInfo.iconBitmap == null) {
     * Picasso.with(mCtx).load(appInfo.getAppIconCachePath(mCtx)).noFade().into(holder); } else {
     * holder.AppIconImageView.setImageBitmap(appInfo.iconBitmap); }
     * 
     * }
     */
    /*
     * @Override public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) { // TODO Auto-generated method
     * stub View v = null; ViewHolder holder = null; v =
     * LayoutInflater.from(parent.getContext()).inflate(R.layout.back_app_info_item, parent, false); holder = new
     * ItemViewHolder(v); return holder; }
     */

    @Override
    public void setData(List<AppInfo> data) {
        // TODO Auto-generated method stub
        mAppListFull = mAppListDisplay = (ArrayList<AppInfo>) data;
        super.setData(data);

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
                        appInfo = mAppListDisplay.get(getPosition() - (hasHeader() ? 1 : 0));
                    } catch (Exception e) {
                        return;
                    }

                    // if(FileUtils.deleteQuietly(new File(appInfo.apkFilePath))){
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

                        notifyItemRemoved(getPosition());
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
                    AppInfo theApp = mAppListDisplay.get(getPosition() - (hasHeader() ? 1 : 0));
                    if (TextUtils.isEmpty(theApp.backupFilePath)) {
                        Utils.installAPK(mCtx, theApp.apkFilePath);
                    } else {
                        Utils.installAPK(mCtx, theApp.backupFilePath);
                    }
                }
            });
        }
    }

}
