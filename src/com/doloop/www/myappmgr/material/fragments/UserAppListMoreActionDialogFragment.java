package com.doloop.www.myappmgr.material.fragments;

import android.app.Activity;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.afollestad.materialdialogs.MaterialDialog;
import com.doloop.www.myappmgr.material.adapters.ArrayAdapterWithIcon;
import com.doloop.www.myappmgr.material.dao.AppInfo;
import com.doloop.www.myappmgr.material.utils.Utilities;
import com.doloop.www.myappmgrmaterial.R;


//@SuppressLint("ValidFragment")
public class UserAppListMoreActionDialogFragment extends DialogFragment {
	
	public final static String DialogTag = "UserAppListMoreActionDialogFragment";
	
	private AppInfo mAppinfo;
	
	private String[] moreActionOpt;// = {"Google Play","Send"};
	private final static int[] moreActionOptIcon = {R.drawable.google_paly_80x80,R.drawable.send1_80x80};

	public final static String ArgumentsTag = "ArgumentsTag";
	
	public UserAppMoreActionListItemClickListener mUserAppMoreActionListItemClickListener;

	// Container Activity must implement this interface
	public interface UserAppMoreActionListItemClickListener {
		public void onUserAppMoreActionListItemClickListener(DialogInterface dialog, int item, AppInfo appInfo);
	}

	public AppInfo getCurrentAppInfo()
	{
		return mAppinfo;
	}
	
//	public static UserAppListMoreActionDialogFragment newInstance(AppInfo appInfo) {
//		UserAppListMoreActionDialogFragment fragmentInstance = new UserAppListMoreActionDialogFragment();
//		mAppinfo = appInfo;
//		return fragmentInstance;
//	}
	
	public void setArgs(AppInfo appInfo,UserAppMoreActionListItemClickListener l){
	    mAppinfo = appInfo;
	    mUserAppMoreActionListItemClickListener = l;
	}
	
	 @Override
	 public void onAttach(Activity activity) {
		 super.onAttach(activity);
//	     try {
//	    	 mUserAppMoreActionListItemClickListener = (UserAppMoreActionListItemClickListener) activity;
//	     } catch (ClassCastException e) {
//	            throw new ClassCastException(activity.toString()
//	                    + " must implement UserAppMoreActionListItemClickListener");
//	     }
	 }
	    @Override
	    public Dialog onCreateDialog(Bundle savedInstanceState) {
	        // Build the dialog and set up the button click handlers
	    	moreActionOpt = new String[] {getActivity().getString(R.string.google_play), getActivity().getString(R.string.send)};
	    	ArrayAdapterWithIcon adapter = new ArrayAdapterWithIcon(getActivity(), moreActionOpt, moreActionOptIcon);

	    	final MaterialDialog dialog = new MaterialDialog.Builder(getActivity())
            .title(mAppinfo.appName)
            .icon(Utilities.ZoomDrawable(mAppinfo.iconBitmap,getActivity()))
            .adapter(adapter)
            .negativeText(R.string.cancel)
            .build();
	    	ListView listView = dialog.getListView();
	    	if (listView != null) {
	    	    listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
	    	        @Override
	    	        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
	    	            mUserAppMoreActionListItemClickListener.onUserAppMoreActionListItemClickListener(dialog, position, mAppinfo);
	    	            dialog.dismiss();
	    	        }
	    	    });
	    	}
	        return dialog;
	    }
}
