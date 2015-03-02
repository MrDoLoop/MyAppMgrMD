package com.doloop.www.myappmgr.material.adapters;

import java.util.ArrayList;
import java.util.Locale;
import java.util.TreeMap;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.doloop.www.myappmgr.material.dao.AppInfo;
import com.doloop.www.myappmgr.material.utils.Constants;
import com.doloop.www.myappmgrmaterial.R;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Picasso.LoadedFrom;
import com.squareup.picasso.Target;

public class UserAppListAdapter extends ArrayAdapter<AppInfo> implements Filterable {

    private int ItemResourceLayout = 0;
    // private int textViewID = 0;
    private ArrayList<AppInfo> full_AppList;
    private ArrayList<AppInfo> AppListDisplay;
    // private Context mContext;
    private LayoutInflater mInflater;
    private UserAppFilter filter;

    private Context mCtx;

    //private SparseBooleanArray mSelectedItemsPos = new SparseBooleanArray();
    private TreeMap<Integer, AppInfo> mSelectedItems = new TreeMap<Integer, AppInfo>();
    /*
     * public UserAppListFilterResultListener mFilterResultListener;
     * 
     * // Container Activity must implement this interface public interface UserAppListFilterResultListener { public
     * void onUserAppListFilterResultPublish(ArrayList<AppInfo> resultsList); }
     * 
     * public void setUserAppListFilterResultListener(UserAppListFilterResultListener userAppListFilterResultListener) {
     * this.mFilterResultListener = userAppListFilterResultListener; }
     */

    public IconClickListener mIconClickListener;

    public interface IconClickListener {
        public void OnIconClickListener(int position);
    }

    public void setIconClickListener(IconClickListener l) {
        this.mIconClickListener = l;
    }

    public void selectAll() {
        mSelectedItems.clear();
        for (int i = 0; i < getCount(); i++) {
            mSelectedItems.put(i, getItem(i));
        }
        this.notifyDataSetChanged();
    }

    public void deselectAll() {
        mSelectedItems.clear();
        this.notifyDataSetChanged();
    }

    public void toggleSelection(int position) {
        setSelectedItem(position, !mSelectedItems.containsKey(position));
    }

    public void setSelectedItem(int position, boolean val) {
        if (val)
            mSelectedItems.put(position, getItem(position));
        else
            mSelectedItems.remove(position);

        notifyDataSetChanged();
    }

    public int getSelectedItemCnt() {
        return mSelectedItems.size();
    }

    public ArrayList<AppInfo> getSelectedItemList(){
        ArrayList<AppInfo> list = new ArrayList<AppInfo>(mSelectedItems.values());
        return list;
    }
    
    public UserAppListDataSetChangedListener mUserAppListDataSetChangedListener;

    public interface UserAppListDataSetChangedListener {
        public void OnUserAppListDataSetChanged();
    }

    public void setUserAppListDataSetChangedListener(UserAppListDataSetChangedListener l) {
        this.mUserAppListDataSetChangedListener = l;
    }

    public UserAppListDataSetChangedListener getUserAppListDataSetChangedListener() {
        return this.mUserAppListDataSetChangedListener;
    }

    public UserAppListAdapter(Context context, int resource, int textViewResourceId, ArrayList<AppInfo> appList,
            IconClickListener l) {
        super(context, resource, textViewResourceId, appList);
        // TODO Auto-generated constructor stub
        this.ItemResourceLayout = resource;
        // this.textViewID = textViewResourceId;
        this.AppListDisplay = this.full_AppList = appList;
        // this.AppListDisplay = (ArrayList<AppInfo>) full_AppList.clone();
        // this.mContext = context;
        this.mInflater = LayoutInflater.from(context);
        this.mCtx = context;
        this.mIconClickListener = l;
    }

    public ArrayList<AppInfo> getDisplayList() {
        return this.AppListDisplay;
    }

    @Override
    public int getCount() {
        // TODO Auto-generated method stub
        return AppListDisplay.size();
    }

    @Override
    public AppInfo getItem(int position) {
        // TODO Auto-generated method stub
        if (position < getCount())// position > 0 &&
            return AppListDisplay.get(position);
        else
            return null;
    }

    @Override
    public long getItemId(int position) {
        // TODO Auto-generated method stub
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // TODO Auto-generated method stub
        AppInfo appInfo = AppListDisplay.get(position);

        ViewHolder holder;
        if (convertView == null) {
            // implement with XML layout file
            convertView = mInflater.inflate(ItemResourceLayout, parent, false);// R.layout.app_info_item
            holder = new ViewHolder();

            holder.AppNameTextView = (TextView) convertView.findViewById(R.id.app_name);
            holder.AppVersionTextView = (TextView) convertView.findViewById(R.id.app_version);
            holder.AppPkgnameTextView = (TextView) convertView.findViewById(R.id.app_pkgname);
            // 中文粗体fix
            // holder.AppPkgnameTextView.getPaint().setFakeBoldText(true);
            // holder.AppPkgnameTextView.setTypeface(null,Typeface.BOLD);
            holder.AppIconImageView = (ImageView) convertView.findViewById(R.id.app_icon);
            holder.AppIconImageView.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {
                    // TODO Auto-generated method stub
                    int pos = (Integer) v.getTag();
                    mIconClickListener.OnIconClickListener(pos);
                    // MainActivity.T("用户图标点击了: "+pos);
                }
            });
            holder.moreItemBtn = (LinearLayout) convertView.findViewById(R.id.expandable_toggle_button);
            holder.moreItemBtn.setFocusable(false);
            // holder.expandableLinearLayout = (LinearLayout) convertView.findViewById(R.id.expandable);
            holder.bgLayout = (LinearLayout) convertView.findViewById(R.id.bgLayout);

            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        holder.AppNameTextView.setText(appInfo.appName);
        holder.AppVersionTextView.setText("v" + appInfo.versionName + " | " + appInfo.appSizeStr + " | "
                + appInfo.lastModifiedTimeStr);
        holder.AppVersionTextView.setSelected(false);
        holder.AppPkgnameTextView.setText(appInfo.packageName);
        // holder.AppIconImageView.setImageDrawable(appInfo.iconDrawable);
        holder.AppIconImageView.setTag(position);
        holder.bgLayout.setTag(appInfo);
        if (appInfo.iconBitmap == null) {
            Picasso.with(mCtx).load(appInfo.getAppIconCachePath(mCtx)).noFade().into(holder);
        } else {
            holder.AppIconImageView.setImageBitmap(appInfo.iconBitmap);
        }
        if(mSelectedItems.containsKey(position)) {
            holder.bgLayout.setBackgroundColor(Color.CYAN);
            holder.AppIconImageView.setBackgroundResource(R.drawable.imageview_border_blue);
        } else {
            holder.bgLayout.setBackgroundResource(R.drawable.list_row_item_bg);
            holder.AppIconImageView.setBackgroundResource(R.drawable.user_app_icon_bg);
        }

        return convertView;
    }

    @Override
    public boolean isEnabled(int position) {
        // TODO Auto-generated method stub
        return super.isEnabled(position);
    }

    @Override
    public Filter getFilter() {
        // TODO Auto-generated method stub
        if (filter == null) {
            filter = new UserAppFilter();
        }
        return filter;
    }

    private class UserAppFilter extends Filter {

        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            // TODO Auto-generated method stub
            // 存储过滤的值

            FilterResults retval = new FilterResults();
            retval.values = full_AppList;
            retval.count = full_AppList.size();

            if (!TextUtils.isEmpty(constraint)) {

                ArrayList<AppInfo> filteredAppList = new ArrayList<AppInfo>();
                for (AppInfo appInfo : full_AppList) {
                    if (appInfo.appName.toLowerCase(Locale.getDefault()).contains(constraint)
                            || appInfo.appNamePinyin.toLowerCase(Locale.getDefault()).contains(constraint)) {
                        filteredAppList.add(appInfo);
                    }
                }

                retval.values = filteredAppList;
                retval.count = filteredAppList.size();
            }

            return retval;

        }

        @SuppressWarnings("unchecked")
        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {
            // TODO Auto-generated method stub
            AppListDisplay = (ArrayList<AppInfo>) results.values;
            /*
             * if (mFilterResultListener != null) {
             * mFilterResultListener.onUserAppListFilterResultPublish(AppListDisplay); }
             */
            notifyDataSetChanged();
        }

    }

    private static class ViewHolder implements Target {
        TextView AppNameTextView;
        TextView AppVersionTextView;
        TextView AppPkgnameTextView;
        ImageView AppIconImageView;
        // ImageButton moreItemBtn;
        LinearLayout moreItemBtn;
        // LinearLayout expandableLinearLayout;
        LinearLayout bgLayout;

        @Override
        public void onBitmapFailed(Drawable bitmap) {
            // TODO Auto-generated method stub

        }

        @Override
        public void onBitmapLoaded(Bitmap bitmap, LoadedFrom from) {
            // TODO Auto-generated method stub
            AppIconImageView.setImageBitmap(bitmap);
            if (Constants.SAVE_APP_ICON_IN_OBJ) {
                AppInfo appInfo = (AppInfo) bgLayout.getTag();
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
            AppIconImageView.setImageResource(R.drawable.userapp_holder);
        }
    }
}
