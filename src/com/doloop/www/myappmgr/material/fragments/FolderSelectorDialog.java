package com.doloop.www.myappmgr.material.fragments;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;

import android.app.Activity;
import android.app.Dialog;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentManager;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.afollestad.materialdialogs.MaterialDialog;
import com.afollestad.materialdialogs.MaterialDialog.Builder;
import com.afollestad.materialdialogs.MaterialDialog.ButtonCallback;
import com.doloop.www.myappmgr.material.utils.Constants;
import com.doloop.www.myappmgrmaterial.R;

/**
 * @author Aidan Follestad (afollestad)
 */
public class FolderSelectorDialog extends DialogFragment implements MaterialDialog.ListCallback {

    private File parentFolder;
    private File[] parentContents;
    private boolean canGoUp = true;
    private FolderSelectCallback mCallback;
    private ListView listView;
    private View headerView;

    private final MaterialDialog.ButtonCallback mButtonCallback = new MaterialDialog.ButtonCallback() {
        @Override
        public void onPositive(MaterialDialog materialDialog) {
            materialDialog.dismiss();
            mCallback.onFolderSelection(parentFolder);
        }

        @Override
        public void onNeutral(MaterialDialog materialDialog) {
            // TODO Auto-generated method stub
            materialDialog.dismiss();
            mCallback.onFolderSelection(new File(Constants.DEF_BACKUP_DIR));
        }

        @Override
        public void onNegative(MaterialDialog materialDialog) {
            materialDialog.dismiss();
        }
    };

    public static interface FolderSelectCallback {
        void onFolderSelection(File folder);
    }

    public FolderSelectorDialog() {
        parentFolder = Environment.getExternalStorageDirectory();
        parentContents = listFiles();
    }

    String[] getContentsArray() {
        String[] results = new String[parentContents.length];
        if (canGoUp){
            if(listView != null && listView.getHeaderViewsCount() == 0){
                listView.addHeaderView(headerView, null, false);
            }
        }
        else{
            if(listView != null){
                listView.removeHeaderView(headerView); 
            }    
        }
        for (int i = 0; i < parentContents.length; i++)
        {      
            results[i] = parentContents[i].getName();
        }
        return results;
    }

    File[] listFiles() {
        File[] contents = parentFolder.listFiles();
        List<File> results = new ArrayList<File>();
        for (File fi : contents) {
            if (fi.isDirectory()) results.add(fi);
        }
        Collections.sort(results, new FolderSorter());
        return results.toArray(new File[results.size()]);
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        MaterialDialog dialog =  new MaterialDialog.Builder(getActivity())
                .title(parentFolder.getAbsolutePath())
                .items(getContentsArray())
                .itemsCallback(this)
                .callback(mButtonCallback)
                .autoDismiss(false)
                .positiveText(R.string.choose)
                .neutralText(R.string.default_dir)
                .negativeText(R.string.cancel)
                .build();
        listView = dialog.getListView();
        if(listView != null){
            int colorPrimary = getActivity().getResources().getColor(R.color.primary);
            headerView = View.inflate(getActivity(), R.layout.md_listitem_folder_dia_header, null);
            TextView newFolder = (TextView) headerView.findViewById(R.id.newFolder);
            newFolder.setText(R.string.new_f);
            newFolder.setTextColor(colorPrimary);
            newFolder.setOnClickListener(new View.OnClickListener() {
                
                @Override
                public void onClick(View v) {
                    // TODO Auto-generated method stub
                   
                    FrameLayout layout = (FrameLayout) View.inflate(getActivity(), R.layout.md_input_dialog, null);
                    final EditText mEditText = (EditText) layout.findViewById(android.R.id.edit);
                    Builder mBuilder = new MaterialDialog.Builder(getActivity())
                    .title(R.string.new_folder)
                    //.icon(getDialogIcon())
                    .positiveText(R.string.ok)
                    .negativeText(R.string.cancel)
                    .autoDismiss(false)
                    .callback(new ButtonCallback(){
                        @Override
                        public void onPositive(MaterialDialog dialog) {
                            mEditText.setError(null);
                            String newFolderName = mEditText.getText().toString();
                            
                            if(TextUtils.isEmpty(newFolderName)){
                                mEditText.setError(getString(R.string.not_null));
                            }
                            else{
                                boolean foundSameName = false;
                                for(File file : parentContents){
                                    if(file.getName().equalsIgnoreCase(newFolderName)){
                                        foundSameName = true;
                                        break;
                                    }
                                }
                                if(foundSameName){
                                    mEditText.setError(getString(R.string.folder_name_exists));
                                }
                                else{
                                    //
                                    File newDir = new File(parentFolder.getAbsolutePath()+File.separator+newFolderName);
                                    newDir.mkdir();
                                    
                                    dialog.dismiss();
                                    
                                    parentContents = listFiles();
                                    MaterialDialog mDialog = (MaterialDialog) getDialog();
                                    mDialog.setTitle(parentFolder.getAbsolutePath());
                                    mDialog.setItems(getContentsArray());
                                }
                            }
                        }

                        @Override
                        public void onNegative(MaterialDialog dialog) {
                            // TODO Auto-generated method stub
                            dialog.dismiss();
                        }
                        
                        
                        
                    })
                    .customView(layout, false);
                    mBuilder.build().show();
                }
            });
            TextView goUp = (TextView) headerView.findViewById(R.id.goUp);
            goUp.setText(R.string.go_up);
            goUp.setTextColor(colorPrimary);
            goUp.setOnClickListener(new View.OnClickListener() {
                
                @Override
                public void onClick(View v) {
                    // TODO Auto-generated method stub
                    parentFolder = parentFolder.getParentFile();
                    canGoUp = parentFolder.getParent() != null;
                    
                    parentContents = listFiles();
                    MaterialDialog dialog = (MaterialDialog) getDialog();
                    dialog.setTitle(parentFolder.getAbsolutePath());
                    dialog.setItems(getContentsArray());
                }
            });
            listView.addHeaderView(headerView, null, false);
        }
       
        return dialog;
    }

    @Override
    public void onSelection(MaterialDialog materialDialog, View view, int i, CharSequence s) {
        parentFolder = parentContents[i];
        canGoUp = true;
        
        parentContents = listFiles();
        MaterialDialog dialog = (MaterialDialog) getDialog();
        dialog.setTitle(parentFolder.getAbsolutePath());
        dialog.setItems(getContentsArray());
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        //mCallback = (FolderSelectCallback) activity;
    }

    public void show(FragmentManager fm,FolderSelectCallback callback) {
        mCallback = callback;
        show(fm, "FOLDER_SELECTOR");
    }


    private static class FolderSorter implements Comparator<File> {
        @Override
        public int compare(File lhs, File rhs) {
            return lhs.getName().toLowerCase(Locale.getDefault()).compareTo(rhs.getName().toLowerCase(Locale.getDefault()));
        }
    }
}