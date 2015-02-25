package com.doloop.www.myappmgr.material.adapters;

import java.io.File;
import java.util.ArrayList;
import java.util.Locale;

import org.apache.commons.io.FileUtils;

import android.content.Context;
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
import com.doloop.www.myappmgr.material.utils.Utilities;
import com.doloop.www.myappmgrmaterial.R;

public class BackupAppListAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    
    
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
    
    public void filterList(String str){
        mAppListDisplay = mAppListFull;
        if (!TextUtils.isEmpty(str)) {
            ArrayList<AppInfo> filteredAppList = new ArrayList<AppInfo>();
            AppInfo appInfo;
            for(int i = 0;i<mAppListFull.size();i++){
                appInfo = mAppListFull.get(i);
                if(appInfo.appName.toLowerCase(Locale.getDefault()).contains(str)
                        || appInfo.appNamePinyin.toLowerCase(Locale.getDefault()).contains(str)){
                    filteredAppList.add(appInfo);
                }
            }
            mAppListDisplay = filteredAppList;
        }
        
    }
    
    
    public BackupAppListAdapter(Context ctx, ArrayList<AppInfo> appList){
        mAppListFull = mAppListDisplay = appList;
        mCtx = ctx;
    }
    
    public ArrayList<AppInfo> getDisplayList(){
        return mAppListDisplay;
    }
    
    public AppInfo getItem(int position){
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
        AppInfo appInfo = mAppListDisplay.get(position);
        ItemViewHolder holder = (ItemViewHolder) viewHolder;
        holder.AppNameTextView.setText(appInfo.appName);
        holder.AppIconImageView.setImageBitmap(appInfo.iconBitmap);
        holder.AppIconImageView.setTag(position);
        holder.AppVersionTextView.setText("v" + appInfo.versionName + " | " + appInfo.appSizeStr + " | "
                + appInfo.lastModifiedTimeStr);
        
        
    }
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        // TODO Auto-generated method stub
        View v = null;
        ViewHolder holder = null;
        v = LayoutInflater.from(parent.getContext()).inflate(R.layout.back_app_info_item, parent, false);
        holder = new ItemViewHolder(v);
        return holder;
    }

    public class ItemViewHolder extends RecyclerView.ViewHolder {

        TextView AppNameTextView;
        TextView AppVersionTextView;
        ImageView AppIconImageView;
        RelativeLayout RootLayout;

        public ItemViewHolder(View view) {
            super(view);
            AppNameTextView = (TextView) view.findViewById(R.id.app_name);
            AppVersionTextView = (TextView) view.findViewById(R.id.app_version);
            AppIconImageView = (ImageView) view.findViewById(R.id.app_icon);
            AppIconImageView.setOnClickListener(new View.OnClickListener() {
                
                @Override
                public void onClick(View v) {
                    // TODO Auto-generated method stub
                    //MainActivity.T("Backup图标被点击了: "+AppIconImageView.getTag());
                    /*mAppListDisplay.remove(getPosition());
                    notifyDataSetChanged();*/
                    AppInfo appInfo = mAppListDisplay.get(getPosition());
                    if(FileUtils.deleteQuietly(new File(appInfo.apkFilePath))){
                      //从显示的list中删除
                        mAppListDisplay.remove(getPosition());
                        //从总共的list中删除
                        for(int i =0;i<mAppListFull.size();i++){
                            if(mAppListFull.get(i).packageName.equals(appInfo.packageName)){
                                mAppListFull.remove(i);
                                break;
                            }
                        }
                        
                        notifyItemRemoved(getPosition());
                        //getBackupAppListDataSetChangedListener().OnBackupAppListDataSetChanged();
                    }
                    else{
                        MainActivity.T(R.string.error);
                    }
                }
            });
            RootLayout = (RelativeLayout) view.findViewById(R.id.rootLayout);
            RootLayout.setOnClickListener(new View.OnClickListener() {
                
                @Override
                public void onClick(View v) {
                    // TODO Auto-generated method stub
                    Utilities.installAPK(mCtx, mAppListDisplay.get(getPosition()).apkFilePath);
                }
            });
        }
    } 
    
}
