package com.doloop.www.myappmgr.material.fragments;

import com.doloop.www.myappmgrmaterial.R;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;

public class SortTypeDialogFragment extends DialogFragment {
	
	public final static String DialogTag = "SortTypeDialogFragment";
	
	private static String[] sortTypeStr = {"Name ASC","Name DES","Size ASC","Size DES","LastModified Time ASC","LastModified Time DES"};
	
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
	        // Build the dialog and set up the button click handlers
	    	sortTypeStr = getActivity().getResources().getStringArray(R.array.sort_type); 
	    	int checkedItem = getArguments().getInt(SELECTED_ITEM, 0); 
	        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
	        builder.setTitle(R.string.sort_type).setSingleChoiceItems(sortTypeStr, checkedItem, new DialogInterface.OnClickListener(){

				@Override
				public void onClick(DialogInterface dialog, int which) {
					// TODO Auto-generated method stub
					mSortTypeListItemClickListener.onSortTypeListItemClick(dialog, which);
					dialog.dismiss();
				}});
	        AlertDialog dialog = builder.create();
	        dialog.setCanceledOnTouchOutside(true);
	        return dialog;
	    }
}
