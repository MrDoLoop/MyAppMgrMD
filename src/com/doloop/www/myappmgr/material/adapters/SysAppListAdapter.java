package com.doloop.www.myappmgr.material.adapters;

import java.util.ArrayList;
import java.util.Locale;
import java.util.TreeMap;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.doloop.www.myappmgr.material.MainActivity;
import com.doloop.www.myappmgr.material.dao.AppInfo;
import com.doloop.www.myappmgr.material.utils.Constants;
import com.doloop.www.myappmgr.material.utils.SysAppListItem;
import com.doloop.www.myappmgr.material.widgets.PinnedSectionListView.PinnedSectionListAdapter;
import com.doloop.www.myappmgrmaterial.R;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Picasso.LoadedFrom;
import com.squareup.picasso.Target;

public class SysAppListAdapter extends BaseAdapter implements PinnedSectionListAdapter, Filterable {

    private ArrayList<SysAppListItem> mSysAppListWapperFull;
    private ArrayList<SysAppListItem> mSysAppListWapperDisplay;
    
    private TreeMap<String, Integer> mSectionInListPosMapFull;
    private TreeMap<String, Integer> mSectionInListPosMapDisplay; 
    // private Context mContext;
    
    private SysAppFilter filter;

    private Context mCtx;

    
    public ArrayList<SysAppListItem> getSysAppListWapperDisplay() {
        return mSysAppListWapperDisplay;
    }
    public TreeMap<String, Integer> getSectionInListPosMapDisplay() {
        return mSectionInListPosMapDisplay;
    }
    
   /* public SysAppListFilterResultListener mFilterResultListener;
    
    // Container Activity must implement this interface
    public interface SysAppListFilterResultListener {
        public void onSysAppListFilterResultPublish(ArrayList<SysAppListItem> ResultSysAppList,TreeMap<String, Integer> ResultPosMap);
    }

    public void setSysAppListFilterResultListener(SysAppListFilterResultListener sysAppListFilterResultListener) {
        this.mFilterResultListener = sysAppListFilterResultListener;
    }*/
    
    public SysAppListDataSetChangedListener mSysAppListDataSetChangedListener;
    public interface SysAppListDataSetChangedListener {
        public void OnSysAppListDataSetChanged(ArrayList<SysAppListItem> ResultSysAppList,
                TreeMap<String, Integer> ResultPosMap);
    }
    public void setSysAppListDataSetChangedListener(SysAppListDataSetChangedListener l) {
        this.mSysAppListDataSetChangedListener = l;
    }
    public SysAppListDataSetChangedListener getSysAppListDataSetChangedListener() {
        return this.mSysAppListDataSetChangedListener;
    }
    
    
    public SysAppListAdapter(Context ctx, ArrayList<SysAppListItem> list, TreeMap<String, Integer> map) {
        mCtx = ctx;
        mSysAppListWapperFull = mSysAppListWapperDisplay = list;
        mSectionInListPosMapFull = mSectionInListPosMapDisplay =  map;
    }

    @Override
    public int getCount() {
        // TODO Auto-generated method stub
        return mSysAppListWapperDisplay.size();
    }

    @Override
    public SysAppListItem getItem(int position) {
        // TODO Auto-generated method stub
        return mSysAppListWapperDisplay.get(position);
    }

    @Override
    public long getItemId(int position) {
        // TODO Auto-generated method stub
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // TODO Auto-generated method stub
        SectionViewHolder sectionHolder;
        AppItemViewHolder appItemHolder;
        SysAppListItem item = getItem(position);
        int viewType = getItemViewType(position);

        if (viewType == SysAppListItem.LIST_SECTION) {
            if (convertView == null) {
                sectionHolder = new SectionViewHolder();
                convertView = LayoutInflater.from(mCtx).inflate(R.layout.sys_app_header_item, parent, false);
                sectionHolder.sectionTextView = (TextView) convertView.findViewById(R.id.textItem);
                convertView.setTag(sectionHolder);
            } else {
                sectionHolder = (SectionViewHolder) convertView.getTag();
            }
            sectionHolder.sectionTextView.setText(item.sectionTxt);
        } else if (viewType == SysAppListItem.APP_ITEM) {
            if (convertView == null) {
                appItemHolder = new AppItemViewHolder();
                convertView = LayoutInflater.from(mCtx).inflate(R.layout.sys_app_info_item, parent, false);

                appItemHolder.RootLayout = (RelativeLayout) convertView.findViewById(R.id.rootLayout);
                appItemHolder.AppNameTextView = (TextView) convertView.findViewById(R.id.app_name);
                appItemHolder.AppVersionTextView = (TextView) convertView.findViewById(R.id.app_version);
                appItemHolder.AppIconImageView = (ImageView) convertView.findViewById(R.id.app_icon);
                appItemHolder.AppIconImageView.setOnClickListener(new View.OnClickListener() {
                    
                    @Override
                    public void onClick(View v) {
                        // TODO Auto-generated method stub
                        int pos = (Integer) v.getTag();
                        MainActivity.T("系统图标被点击了: "+pos);
                    }
                });
                convertView.setTag(appItemHolder);

            } else {
                appItemHolder = (AppItemViewHolder) convertView.getTag();
            }

            AppInfo appInfo = item.appinfo;

            appItemHolder.AppNameTextView.setText(appInfo.appName);
            appItemHolder.AppVersionTextView.setText("v" + appInfo.versionName + " | " + appInfo.appSizeStr + " | "
                    + appInfo.lastModifiedTimeStr);
            appItemHolder.AppVersionTextView.setSelected(false);
            // appViewHolder.AppIconImageView.setImageDrawable(appInfo.iconDrawable);
            // Picasso.with(mCtx).load(new File(Utilities.getAppIconCacheDir(mCtx), appInfo.packageName +
            // ".png")).into(appViewHolder.AppIconImageView);
            appItemHolder.AppIconImageView.setTag(position);
            appItemHolder.RootLayout.setTag(appInfo);
            if (appInfo.iconBitmap == null) {
                Picasso.with(mCtx).load(appInfo.getAppIconCachePath(mCtx)).noFade().into(appItemHolder);
            } else {
                appItemHolder.AppIconImageView.setImageBitmap(appInfo.iconBitmap);
            }
        }

        return convertView;
    }
    
    @Override
    public int getViewTypeCount() {
        return 2;
    }

    @Override
    public int getItemViewType(int position) {
        return getItem(position).type;
    }

    @Override
    public boolean isItemViewTypePinned(int viewType) {
        return viewType == SysAppListItem.LIST_SECTION;
    }

    public int getAppItemsCount(){
        return mSysAppListWapperDisplay.size() - mSectionInListPosMapDisplay.keySet().size();
    }
    /**
     * 
     * @param sectionTxt
     * @return -1: not found, >-1: position
     */
    public int getSectionPostionInList(String sectionTxt) {
        if(mSectionInListPosMapDisplay.get(sectionTxt) != null){
           return mSectionInListPosMapDisplay.get(sectionTxt).intValue();
        }
        return -1;
    }
    
    private static class SectionViewHolder {
        TextView sectionTextView;
    }

    private static class AppItemViewHolder implements Target {
        TextView AppNameTextView;
        TextView AppVersionTextView;
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
            if(Constants.SAVE_APP_ICON_IN_OBJ){
                AppInfo appInfo = (AppInfo) RootLayout.getTag();
                //if (appInfo.iconBitmap == null) 
                //if(appInfo != null)
                {
                    appInfo.iconBitmap = bitmap;
                }
            }
        }

        @Override
        public void onPrepareLoad(Drawable placeHolderDrawable) {
            // TODO Auto-generated method stub
            AppIconImageView.setImageResource(R.drawable.sysapp_holder);
        }

    }

    @Override
    public Filter getFilter() {
        if (filter == null) {
            filter = new SysAppFilter();
        }
        return filter;
    }

    
    private class SysAppFilter extends Filter {
        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            // TODO Auto-generated method stub
            // 存储过滤的值
            //mSysAppListWapperDisplay = mSysAppListWapperFull;
            mSectionInListPosMapDisplay = mSectionInListPosMapFull;

            FilterResults retval = new FilterResults();
            retval.values = mSysAppListWapperFull;
            retval.count = mSysAppListWapperFull.size();
       
            if (!TextUtils.isEmpty(constraint)) {
   
                ArrayList<SysAppListItem> filteredAppList = new ArrayList<SysAppListItem>();
                TreeMap<String, Integer> filteredSectionInListPosMap = new TreeMap<String, Integer>(); 
                
                for (SysAppListItem sysAppListItem : mSysAppListWapperFull) {
     
                    if(sysAppListItem.type == SysAppListItem.LIST_SECTION){
                        if(filteredAppList.size() == 0){
                            filteredAppList.add(sysAppListItem);
                            filteredSectionInListPosMap.put(sysAppListItem.sectionTxt, 0);
                        }
                        else{
                            SysAppListItem lastItem = filteredAppList.get(filteredAppList.size()-1);
                            if(lastItem.type == SysAppListItem.LIST_SECTION){
                                filteredSectionInListPosMap.remove(lastItem.sectionTxt);
                                filteredAppList.remove(filteredAppList.size()-1);
                            }
                            
                            filteredAppList.add(sysAppListItem);
                            filteredSectionInListPosMap.put(sysAppListItem.sectionTxt, filteredAppList.size()-1);
                        }
                        
                    }
                    else if(sysAppListItem.type == SysAppListItem.APP_ITEM){
                        if(sysAppListItem.appinfo.appName.toLowerCase(Locale.getDefault()).contains(constraint)
                                || sysAppListItem.appinfo.appNamePinyin.toLowerCase(Locale.getDefault()).contains(constraint))
                            filteredAppList.add(sysAppListItem);
                    }
                }
                //如果最后一项是section 去掉
                SysAppListItem lastItem = filteredAppList.get(filteredAppList.size()-1);
                if(lastItem.type == SysAppListItem.LIST_SECTION){
                    filteredSectionInListPosMap.remove(lastItem.sectionTxt);
                    filteredAppList.remove(filteredAppList.size()-1);
                }

                //一定要clear自后在addAll不能直接mSysAppListWapperDisplay = filteredAppList,
                //似乎是内存没有刷新，list没有内容更新
//                mSysAppListWapperDisplay.clear();
//                mSysAppListWapperDisplay.addAll(filteredAppList);
//                mSectionInListPosMapDisplay = filteredSectionInListPosMap;
                
                retval.values = filteredAppList;
                retval.count = filteredAppList.size();
                mSectionInListPosMapDisplay = filteredSectionInListPosMap;    
            }

            return retval;

        }

        @SuppressWarnings("unchecked")
        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {
            
            mSysAppListWapperDisplay = (ArrayList<SysAppListItem>)results.values;
            /*if (mFilterResultListener != null) {
                mFilterResultListener.onSysAppListFilterResultPublish(mSysAppListWapperDisplay, mSectionInListPosMapDisplay);
            }*/
            notifyDataSetChanged();
        }

    }
    
    
}
