package com.doloop.www.myappmgr.material.adapters;

import java.util.ArrayList;
import java.util.Locale;
import java.util.TreeMap;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.text.Html;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.AnimationUtils;
import android.widget.BaseAdapter;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.afollestad.materialdialogs.util.TypefaceHelper;
import com.doloop.www.myappmgr.material.R;
import com.doloop.www.myappmgr.material.dao.AppInfo;
import com.doloop.www.myappmgr.material.fragments.SysAppsTabFragment;
import com.doloop.www.myappmgr.material.interfaces.IconClickListener;
import com.doloop.www.myappmgr.material.interfaces.IhoverMenuClickListener;
import com.doloop.www.myappmgr.material.interfaces.ItemMenuClickListener;
import com.doloop.www.myappmgr.material.utils.Constants;
import com.doloop.www.myappmgr.material.utils.SysAppListItem;
import com.doloop.www.myappmgr.material.utils.Utils;
import com.doloop.www.myappmgr.material.widgets.PinnedSectionListView.PinnedSectionListAdapter;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Picasso.LoadedFrom;
import com.squareup.picasso.Target;

public class SysAppListAdapter extends BaseAdapter implements PinnedSectionListAdapter, Filterable,
        View.OnClickListener {

    private ArrayList<SysAppListItem> mSysAppListWapperFull;
    private ArrayList<SysAppListItem> mSysAppListWapperDisplay;

    private TreeMap<String, Integer> mSectionInListPosMapFull;
    private TreeMap<String, Integer> mSectionInListPosMapDisplay;
    private int mHoverShowedPos = -1;
    // private View mLastShowedHoverView = null;
    private boolean hoverAniIsRunning = false;

    private SysAppFilter filter;

    private Context mCtx;
    private int selectedCnt = 0;

    private ItemMenuClickListener mItemMenuClickListener;
    private IhoverMenuClickListener mIhoverMenuClickListener;
    private IconClickListener mIconClickListener;

    public void setIconClickListener(IconClickListener l) {
        this.mIconClickListener = l;
    }

    public ArrayList<SysAppListItem> getSysAppListWapperDisplay() {
        return mSysAppListWapperDisplay;
    }

    public TreeMap<String, Integer> getSectionInListPosMapDisplay() {
        return mSectionInListPosMapDisplay;
    }

    /*
     * public SysAppListFilterResultListener mFilterResultListener;
     * 
     * // Container Activity must implement this interface public interface SysAppListFilterResultListener { public void
     * onSysAppListFilterResultPublish(ArrayList<SysAppListItem> ResultSysAppList,TreeMap<String, Integer>
     * ResultPosMap); }
     * 
     * public void setSysAppListFilterResultListener(SysAppListFilterResultListener sysAppListFilterResultListener) {
     * this.mFilterResultListener = sysAppListFilterResultListener; }
     */

    public boolean hoverAniIsRunning() {
        return hoverAniIsRunning;
    }

    public void setHoverShowedPos(int pos) {
        mHoverShowedPos = pos;
    }

    public void resetHoverShowedPos() {
        mHoverShowedPos = -1;
    }

    public int getLastHoverShowedPos() {
        return mHoverShowedPos;
    }

    public boolean isAnyHoverShowed() {
        if (mHoverShowedPos == -1) {
            return false;
        } else {
            return true;
        }
    }

    public void selectAll() {
        // mSelectedItems.clear();
        int size = getCount();
        for (int i = 0; i < size; i++) {
            if (getItem(i).type == SysAppListItem.APP_ITEM) {
                getItem(i).appinfo.selected = true;
            }

        }
        selectedCnt = getAppItemsCount();
        this.notifyDataSetChanged();
    }

    public void deselectAll() {
        // mSelectedItems.clear();
        int size = getCount();
        for (int i = 0; i < size; i++) {
            if (getItem(i).type == SysAppListItem.APP_ITEM) {
                getItem(i).appinfo.selected = false;
            }

        }
        selectedCnt = 0;
        this.notifyDataSetChanged();
    }

    public void toggleSelection(int position, boolean refreshList) {
        // setSelectedItem(position, !mSelectedItems.containsKey(position), refreshList);
        // String thePkgName = getItem(position).packageName;
        getItem(position).appinfo.selected = !getItem(position).appinfo.selected;
        setSelectedItem(position, getItem(position).appinfo.selected, refreshList);
    }

    public void setSelectedItem(int position, boolean val, boolean refreshList) {
        if (val) {
            getItem(position).appinfo.selected = true;
            selectedCnt++;
            if (selectedCnt > getAppItemsCount()) {
                selectedCnt = getAppItemsCount();
            }
        } else {
            getItem(position).appinfo.selected = false;
            selectedCnt--;
            if (selectedCnt < 0) {
                selectedCnt = 0;
            }
            // mSelectedItems.remove(getItem(position).packageName);
        }
        if (refreshList) {
            notifyDataSetChanged();
        }
    }

    public int getSelectedItemCnt() {
        // int i = 0;
        // ArrayList<AppInfo> list = getDisplayList();
        // for (AppInfo appInfo : list) {
        // if(appInfo.selected){
        // i++;
        // }
        // }
        // return i;
        // return mSelectedItems.size();
        return selectedCnt;
    }

    public ArrayList<AppInfo> getSelectedItemList() {
        ArrayList<AppInfo> retList = new ArrayList<AppInfo>();
        ArrayList<SysAppListItem> curList = getDisplayList();
        for (SysAppListItem listItem : curList) {
            if (listItem.type == SysAppListItem.APP_ITEM) {
                if (listItem.appinfo.selected) {
                    retList.add(listItem.appinfo);
                }
            }

        }

        return retList;
        /*
         * ArrayList<AppInfo> list = new ArrayList<AppInfo>(mSelectedItems.values()); return list;
         */
    }

    public ArrayList<SysAppListItem> getDisplayList() {
        return mSysAppListWapperDisplay;
    }

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

    public SysAppListAdapter(Context ctx, ArrayList<SysAppListItem> list, TreeMap<String, Integer> map,
            IconClickListener l1, ItemMenuClickListener l2, IhoverMenuClickListener l3) {
        mCtx = ctx;
        mSysAppListWapperFull = mSysAppListWapperDisplay = list;
        mSectionInListPosMapFull = mSectionInListPosMapDisplay = map;
        mIconClickListener = l1;
        mItemMenuClickListener = l2;
        mIhoverMenuClickListener = l3;
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

    public boolean isAppItem(int position) {
        if (getItem(position).type == SysAppListItem.APP_ITEM) {
            return true;
        } else {
            return false;
        }
    }

    @Override
    public long getItemId(int position) {
        // TODO Auto-generated method stub
        return 0;
    }

    public void showHover(View listItemView, int position, boolean withAni){
        showHover(listItemView, position, withAni, true);
    }
    
    public void showHover(View listItemView, final int position, boolean withAni, boolean slideBtmIn) {
        final View hoverView = listItemView.findViewById(R.id.hoverLayout);
        hoverView.setVisibility(View.VISIBLE);
        
        View launchView = hoverView.findViewById(R.id.launch);

        SysAppListItem item = getItem(position);
        
        if(Utils.canLaunch(mCtx, item.appinfo)){
            launchView.setVisibility(View.VISIBLE);
        }
        else{
            launchView.setVisibility(View.GONE); 
        }

        final View hoverMenuCover = listItemView.findViewById(R.id.item_menu_cover);
        //hoverMenuCover.setVisibility(View.VISIBLE);
        
        if (withAni) {
            hoverView.clearAnimation();
            Animation ani;
            if(slideBtmIn){
                ani = AnimationUtils.loadAnimation(mCtx, R.anim.slide_btm_in);
            }
            else{
                ani = AnimationUtils.loadAnimation(mCtx, R.anim.slide_up_in);
            } 
            //ani.setFillAfter(true);
            ani.setAnimationListener(new AnimationListener() {

                @Override
                public void onAnimationEnd(Animation animation) {
                    // TODO Auto-generated method stub
                    setHoverShowedPos(position);
                    hoverMenuCover.setVisibility(View.GONE);
                    hoverAniIsRunning = false;
                }

                @Override
                public void onAnimationRepeat(Animation animation) {
                    // TODO Auto-generated method stub
                }

                @Override
                public void onAnimationStart(Animation animation) {
                    // TODO Auto-generated method stub
                    hoverAniIsRunning = true;
                    hoverMenuCover.setVisibility(View.GONE);
                }
            });
            hoverView.startAnimation(ani);
            setHoverShowedPos(position);
        } else {
            hoverAniIsRunning = false;
            hoverMenuCover.setVisibility(View.GONE);
            setHoverShowedPos(position);
        }
    }

    public void hideHover(View listItemView, int position, boolean withAni){
        hideHover(listItemView, position, withAni, true);
    }
    public void hideHover(View listItemView, int position, boolean withAni, boolean slideUpAni) {
        final View hoverView = listItemView.findViewById(R.id.hoverLayout);
        hoverView.setVisibility(View.VISIBLE);
        
        final View hoverMenuCover = listItemView.findViewById(R.id.item_menu_cover);
        if (withAni) {
            Animation ani;
            hoverView.clearAnimation();
            if(slideUpAni){
                ani = AnimationUtils.loadAnimation(mCtx, R.anim.slide_up_out);
            }
            else{
                ani = AnimationUtils.loadAnimation(mCtx, R.anim.slide_btm_out);
            }
            
            //ani.setFillAfter(true);
            ani.setAnimationListener(new AnimationListener() {

                @Override
                public void onAnimationEnd(Animation animation) {
                    // TODO Auto-generated method stub
                    hoverAniIsRunning = false;
                    hoverMenuCover.setVisibility(View.VISIBLE);
                    hoverView.setVisibility(View.GONE);
                    //notifyDataSetChanged();
                }

                @Override
                public void onAnimationRepeat(Animation animation) {
                    // TODO Auto-generated method stub

                }

                @Override
                public void onAnimationStart(Animation animation) {
                    // TODO Auto-generated method stub
                    hoverAniIsRunning = true;
                    hoverView.setVisibility(View.INVISIBLE);
                    hoverMenuCover.setVisibility(View.VISIBLE);
                }
            });
            hoverView.startAnimation(ani);
            resetHoverShowedPos();
        } else {
            hoverAniIsRunning = false;
            hoverMenuCover.setVisibility(View.VISIBLE);
            hoverView.setVisibility(View.GONE);
            resetHoverShowedPos();
        }
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        // TODO Auto-generated method stub
        SectionViewHolder sectionHolder;
        final AppItemViewHolder appItemHolder;
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

                appItemHolder.hoverMenu = (TextView) convertView.findViewById(R.id.item_menu);
                String menuTxt = appItemHolder.hoverMenu.getText().toString();
                appItemHolder.hoverMenu.setText(Html.fromHtml("<sup>"+menuTxt+"</sup>"));
                appItemHolder.hoverMenu.setTypeface(TypefaceHelper.get(mCtx, "RobotoMedium"));
                
                appItemHolder.hoverMenuCover = convertView.findViewById(R.id.item_menu_cover);
                appItemHolder.hoverMenuCover.setOnTouchListener(new View.OnTouchListener() {
                    //为了使得 menu 的...变色，所以用了touch事件
                    @SuppressLint("ClickableViewAccessibility")
                    @Override
                    public boolean onTouch(View v, MotionEvent event) {
                        
                        // TODO Auto-generated method stub
//                        View listItem = (View)v.getParent();
//                        TextView hoverMenu = (TextView) listItem.findViewById(R.id.item_menu);
                        
                        switch (event.getAction()) {
                            case MotionEvent.ACTION_DOWN:
                            {
                             //按住事件发生后执行代码的区域
                                //v.setFocusable(true);
                                appItemHolder.hoverMenu.setTextColor(mCtx.getResources().getColor(R.color.lt_gray));
                                return true;
                            }
                            case MotionEvent.ACTION_MOVE:    
                            {
                             //移动事件发生后执行代码的区域
                                return true;
                            }
                            case MotionEvent.ACTION_CANCEL:
                            {
                                appItemHolder.hoverMenu.setTextColor(mCtx.getResources().getColor(R.color.secondary_text));
                                break;
                            }
                            case MotionEvent.ACTION_UP:
                            {
                             //松开事件发生后执行代码的区域
                                //v.requestFocus();
                                appItemHolder.hoverMenu.performClick();
                                appItemHolder.hoverMenu.setTextColor(mCtx.getResources().getColor(R.color.secondary_text));
                                //v.setFocusable(false);
                                return true;
                             
                            }
                        }
                        return true;
                    }
                });
//                appItemHolder.hoverMenuCover.setOnClickListener(new View.OnClickListener() {
//                    @Override
//                    public void onClick(View v) {
//                        appItemHolder.hoverMenu.performClick();
//                    }
//                });
  
                appItemHolder.hoverMenu.setOnClickListener(this);
                appItemHolder.HoverLayout = (LinearLayout) convertView.findViewById(R.id.hoverLayout);
                appItemHolder.HoverLayout.setOnClickListener(this);
                for(int i = 0;i<appItemHolder.HoverLayout.getChildCount();i++){
                    appItemHolder.HoverLayout.getChildAt(i).setOnClickListener(this);
                }
                /*appItemHolder.hoverMenuBackup = convertView.findViewById(R.id.backup);
                appItemHolder.hoverMenuDetails = convertView.findViewById(R.id.details);
                appItemHolder.hoverMenuLaunch = convertView.findViewById(R.id.launch);
                appItemHolder.hoverMenuMarket = convertView.findViewById(R.id.market);
                appItemHolder.hoverMenuSend = convertView.findViewById(R.id.send);
                
                appItemHolder.hoverMenuBackup.setOnClickListener(this);
                appItemHolder.hoverMenuDetails.setOnClickListener(this);
                appItemHolder.hoverMenuLaunch.setOnClickListener(this);
                appItemHolder.hoverMenuMarket.setOnClickListener(this);
                appItemHolder.hoverMenuSend.setOnClickListener(this);*/
               
                appItemHolder.RootLayout = (RelativeLayout) convertView.findViewById(R.id.rootLayout);
                appItemHolder.AppNameTextView = (TextView) convertView.findViewById(R.id.app_name);
                appItemHolder.AppVersionTextView = (TextView) convertView.findViewById(R.id.app_version);
                appItemHolder.AppPkgTextView = (TextView) convertView.findViewById(R.id.app_pkgname);
                appItemHolder.AppIconImageView = (ImageView) convertView.findViewById(R.id.app_icon);
                appItemHolder.AppIconImageView.setOnClickListener(this);
               
                convertView.setTag(appItemHolder);

            } else {
                appItemHolder = (AppItemViewHolder) convertView.getTag();
            }

            AppInfo appInfo = item.appinfo;
            appItemHolder.HoverLayout.clearAnimation();
            hoverAniIsRunning = false;
            if (mHoverShowedPos == position) {
                appItemHolder.HoverLayout.setVisibility(View.VISIBLE);
            } else {
                appItemHolder.HoverLayout.setVisibility(View.GONE);
            }

            appItemHolder.AppNameTextView.setText(appInfo.appName);
            appItemHolder.AppVersionTextView.setText("v" + appInfo.versionName + " | " + appInfo.appSizeStr + " | "
                    + appInfo.lastModifiedTimeStr);
            appItemHolder.AppPkgTextView.setText(appInfo.packageName);

            appItemHolder.AppVersionTextView.setSelected(false);
            // appViewHolder.AppIconImageView.setImageDrawable(appInfo.iconDrawable);
            // Picasso.with(mCtx).load(new File(Utilities.getAppIconCacheDir(mCtx), appInfo.packageName +
            // ".png")).into(appViewHolder.AppIconImageView);
            appItemHolder.HoverLayout.setTag(position);
            appItemHolder.hoverMenu.setTag(position);
            appItemHolder.AppIconImageView.setTag(position);
            appItemHolder.RootLayout.setTag(appInfo);

            if (appInfo.iconBitmap == null) {
                Picasso.with(mCtx).load(appInfo.getAppIconCachePath(mCtx)).noFade().into(appItemHolder);//.memoryPolicy(MemoryPolicy.NO_CACHE, MemoryPolicy.NO_STORE)
            } else {
                appItemHolder.AppIconImageView.setImageBitmap(appInfo.iconBitmap);
            }

            if (SysAppsTabFragment.isInActoinMode) {
                resetHoverShowedPos();
                appItemHolder.HoverLayout.setVisibility(View.GONE);
                appItemHolder.hoverMenu.setVisibility(View.GONE);
                appItemHolder.hoverMenuCover.setVisibility(View.GONE);
                //appItemHolder.hoverMenuCover.setClickable(false);
                appItemHolder.AppIconImageView.setOnClickListener(null);
                appItemHolder.AppIconImageView.setClickable(false);
                appItemHolder.AppIconImageView.setBackgroundResource(R.drawable.imageview_border_red);
                if (appInfo.selected) {
                    appItemHolder.RootLayout.setBackgroundResource(R.drawable.list_row_item_pressed_bg);
                } else {
                    appItemHolder.RootLayout.setBackgroundResource(R.drawable.list_row_item_bg);
                }
            } else {
                appItemHolder.hoverMenu.setVisibility(View.VISIBLE);
                if(mHoverShowedPos == position) {//hover显示行
                    //appItemHolder.hoverMenu.setClickable(false);
                    //appItemHolder.hoverMenu.setVisibility(View.GONE);
                    //appItemHolder.hoverMenuCover.setClickable(false);
                    appItemHolder.hoverMenuCover.setVisibility(View.GONE);
                    appItemHolder.AppIconImageView.setOnClickListener(null);
                    appItemHolder.AppIconImageView.setClickable(false);
                    appItemHolder.AppIconImageView.setBackgroundResource(R.drawable.imageview_border_red);
                }
                else{
                    //appItemHolder.hoverMenu.setClickable(true);
                    //appItemHolder.hoverMenu.setVisibility(View.VISIBLE);
                    //appItemHolder.hoverMenuCover.setClickable(true);
                    appItemHolder.hoverMenuCover.setVisibility(View.VISIBLE);
                    appItemHolder.AppIconImageView.setOnClickListener(this);
                    appItemHolder.AppIconImageView.setClickable(true); 
                    appItemHolder.AppIconImageView.setBackgroundResource(R.drawable.sys_app_icon_bg);
                }
                
                appItemHolder.RootLayout.setBackgroundResource(R.drawable.list_row_item_bg);
            }
            //appItemHolder.RootLayout.setBackgroundResource(0);
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

    public int getAppItemsCount() {
        return mSysAppListWapperDisplay.size() - mSectionInListPosMapDisplay.keySet().size();
    }

    /**
     * 
     * @param sectionTxt
     * @return -1: not found, >-1: position
     */
    public int getSectionPostionInList(String sectionTxt) {
        if (mSectionInListPosMapDisplay.get(sectionTxt) != null) {
            return mSectionInListPosMapDisplay.get(sectionTxt).intValue();
        }
        return -1;
    }

    private static class SectionViewHolder {
        TextView sectionTextView;
    }

    static class AppItemViewHolder implements Target {
        TextView AppNameTextView;
        TextView AppVersionTextView;
        TextView AppPkgTextView;
        ImageView AppIconImageView;
        RelativeLayout RootLayout;
        LinearLayout HoverLayout;
        TextView hoverMenu;
        View hoverMenuCover;
        View hoverMenuLaunch;
        View hoverMenuDetails;
        View hoverMenuBackup;
        View hoverMenuMarket;
        View hoverMenuSend;

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
            // mSysAppListWapperDisplay = mSysAppListWapperFull;
            mSectionInListPosMapDisplay = mSectionInListPosMapFull;

            FilterResults retval = new FilterResults();
            retval.values = mSysAppListWapperFull;
            retval.count = mSysAppListWapperFull.size();

            if (!TextUtils.isEmpty(constraint)) {

                ArrayList<SysAppListItem> filteredAppList = new ArrayList<SysAppListItem>();
                TreeMap<String, Integer> filteredSectionInListPosMap = new TreeMap<String, Integer>();

                for (SysAppListItem sysAppListItem : mSysAppListWapperFull) {

                    if (sysAppListItem.type == SysAppListItem.LIST_SECTION) {
                        if (filteredAppList.size() == 0) {
                            filteredAppList.add(sysAppListItem);
                            filteredSectionInListPosMap.put(sysAppListItem.sectionTxt, 0);
                        } else {
                            SysAppListItem lastItem = filteredAppList.get(filteredAppList.size() - 1);
                            if (lastItem.type == SysAppListItem.LIST_SECTION) {
                                filteredSectionInListPosMap.remove(lastItem.sectionTxt);
                                filteredAppList.remove(filteredAppList.size() - 1);
                            }

                            filteredAppList.add(sysAppListItem);
                            filteredSectionInListPosMap.put(sysAppListItem.sectionTxt, filteredAppList.size() - 1);
                        }

                    } else if (sysAppListItem.type == SysAppListItem.APP_ITEM) {
                        if (sysAppListItem.appinfo.appName.toLowerCase(Locale.getDefault()).contains(constraint)
                                || sysAppListItem.appinfo.appNamePinyin.toLowerCase(Locale.getDefault()).contains(
                                        constraint))
                            filteredAppList.add(sysAppListItem);
                    }
                }
                // 如果最后一项是section 去掉
                SysAppListItem lastItem = filteredAppList.get(filteredAppList.size() - 1);
                if (lastItem.type == SysAppListItem.LIST_SECTION) {
                    filteredSectionInListPosMap.remove(lastItem.sectionTxt);
                    filteredAppList.remove(filteredAppList.size() - 1);
                }

                // 一定要clear自后在addAll不能直接mSysAppListWapperDisplay = filteredAppList,
                // 似乎是内存没有刷新，list没有内容更新
                // mSysAppListWapperDisplay.clear();
                // mSysAppListWapperDisplay.addAll(filteredAppList);
                // mSectionInListPosMapDisplay = filteredSectionInListPosMap;

                retval.values = filteredAppList;
                retval.count = filteredAppList.size();
                mSectionInListPosMapDisplay = filteredSectionInListPosMap;
            }

            return retval;

        }

        @SuppressWarnings("unchecked")
        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {
            mSysAppListWapperDisplay = (ArrayList<SysAppListItem>) results.values;
            notifyDataSetChanged();
        }

    }

    @Override
    public void onClick(View v) {
        // TODO Auto-generated method stub
        int viewId = v.getId();
        if (viewId == R.id.app_icon) {
            int pos = (Integer) v.getTag();
            mIconClickListener.OnIconClick(pos);
        } else if (viewId == R.id.item_menu) {
            int pos = (Integer) v.getTag();
            mItemMenuClickListener.OnItemMenuClick(pos, findRootLayout(v));
        } else if (viewId == R.id.hoverLayout) {
            int pos = (Integer) v.getTag();
            mItemMenuClickListener.OnItemMenuClick(pos, findRootLayout(v));
        } else if (viewId == R.id.launch || viewId == R.id.details || viewId == R.id.backup || viewId == R.id.market
                || viewId == R.id.send) {
            View hoverLayout = (View) v.getParent();
            int pos = (Integer) hoverLayout.getTag();
            mIhoverMenuClickListener.OnMenuClick(viewId, pos, getItem(pos).appinfo);
        }

    }

    private View findRootLayout(View startView) {
        View retView = (View) startView.getParent();

        if (retView.getId() == R.id.list_item_root) {
            return retView;
        } else {
            return findRootLayout(retView);
        }
    }

}
