package com.doloop.www.myappmgr.material.fragments;

import android.app.Activity;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.View;

import com.afollestad.materialdialogs.MaterialDialog;
import com.doloop.www.myappmgr.material.dao.AppInfo;
import com.doloop.www.myappmgr.material.utils.Utilities;
import com.doloop.www.myappmgrmaterial.R;


//@SuppressLint("ValidFragment")
public class UserAppListMoreActionDialogFragment extends DialogFragment {
	
	public final static String DialogTag = "UserAppListMoreActionDialogFragment";
	
	private AppInfo mAppinfo;
	
//	private String[] moreActionOpt;// = {"Google Play","Send"};
//	private final static int[] moreActionOptIcon = {R.drawable.google_paly_80x80,R.drawable.send1_80x80};

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
	        View pView = View.inflate(getActivity(), R.layout.user_more_action_dia, null);
	    	final MaterialDialog dialog = new MaterialDialog.Builder(getActivity())
            .title(mAppinfo.appName)
            .icon(Utilities.ZoomDrawable(mAppinfo.iconBitmap,getActivity())).customView(pView, false)
            //.negativeText(R.string.cancel)
            .build();
	    	
	    	pView.findViewById(R.id.marketLayout).setOnClickListener(new View.OnClickListener() {
                
                @Override
                public void onClick(View v) {
                    // TODO Auto-generated method stub
                    mUserAppMoreActionListItemClickListener.onUserAppMoreActionListItemClickListener(dialog, 0, mAppinfo);
                    dialog.dismiss();
                }
            });
	    	pView.findViewById(R.id.sendLayout).setOnClickListener(new View.OnClickListener() {
                
                @Override
                public void onClick(View v) {
                    // TODO Auto-generated method stub
                    mUserAppMoreActionListItemClickListener.onUserAppMoreActionListItemClickListener(dialog, 1, mAppinfo);
                    dialog.dismiss();
                }
            });
	    	dialog.setCanceledOnTouchOutside(true);
	        return dialog;
	    }
}
