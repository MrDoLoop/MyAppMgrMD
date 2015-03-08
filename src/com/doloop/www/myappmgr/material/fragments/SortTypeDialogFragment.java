package com.doloop.www.myappmgr.material.fragments;

import com.afollestad.materialdialogs.MaterialDialog;
import com.doloop.www.myappmgrmaterial.R;

import android.app.Activity;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.View;

public class SortTypeDialogFragment extends DialogFragment {
	
	public final static String DialogTag = "SortTypeDialogFragment";
	
	//private static String[] sortTypeStr = {"Name ASC","Name DES","Size ASC","Size DES","LastModified Time ASC","LastModified Time DES"};
	
	public final static int LIST_SORT_TYPE_NAME_ASC = 0;
	public final static int LIST_SORT_TYPE_NAME_DES = 1;
	public final static int LIST_SORT_TYPE_SIZE_ASC = 2;
	public final static int LIST_SORT_TYPE_SIZE_DES = 3;
	public final static int LIST_SORT_TYPE_LAST_MOD_TIME_ASC = 4;
	public final static int LIST_SORT_TYPE_LAST_MOD_TIME_DES = 5;
	
	public static final String SELECTED_ITEM = "selected";
	
	public SortTypeListItemClickListener mSortTypeListItemClickListener;

	// Container Activity must implement this interface
	public interface SortTypeListItemClickListener {
		public void onSortTypeListItemClick(DialogInterface dialog, int which);
	}
	
	
	 @Override
	 public void onAttach(Activity activity) {
		 super.onAttach(activity);
	     try {
	    	 mSortTypeListItemClickListener = (SortTypeListItemClickListener) activity;
	     } catch (ClassCastException e) {
	            throw new ClassCastException(activity.toString()
	                    + " must implement SortTypeListItemClickListener");
	     }
	 }
	    @Override
	    public Dialog onCreateDialog(Bundle savedInstanceState) {
	    	int checkedItem = getArguments().getInt(SELECTED_ITEM, 0);
	    	MaterialDialog dialog = new MaterialDialog.Builder(getActivity())
	        .title(R.string.sort_type)
	        .items(R.array.sort_type)
	        .itemsCallbackSingleChoice(checkedItem, new MaterialDialog.ListCallback() {

                @Override
                public void onSelection(MaterialDialog dialog, View itemView, int which, CharSequence text) {
                    // TODO Auto-generated method stub
                    mSortTypeListItemClickListener.onSortTypeListItemClick(dialog, which);
                }
	        })
	        .autoDismiss(true)
	        .negativeText(R.string.cancel).build();
	    	
	        return dialog;
	    }
}
