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
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.afollestad.materialdialogs.MaterialDialog;
import com.afollestad.materialdialogs.MaterialDialog.Builder;
import com.afollestad.materialdialogs.MaterialDialog.ButtonCallback;
import com.doloop.www.myappmgr.material.R;
import com.doloop.www.myappmgr.material.utils.Constants;
import com.doloop.www.myappmgr.material.utils.Utils;

/**
 * @author Aidan Follestad (afollestad)
 */
public class FolderSelectorDialog extends DialogFragment implements MaterialDialog.ListCallback {

    private File parentFolder;
    private File[] parentContents;
    //private boolean canGoUp = true;
    private FolderSelectCallback mCallback;
    private ListView listView;
    private View headerView;
    private TextView goUp;

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
        /*if (canGoUp){
            if(listView != null && listView.getHeaderViewsCount() == 0){
                listView.addHeaderView(headerView, null, false);
                goUp.setVisibility(View.VISIBLE);
            }
        }
        else{
            if(listView != null){
                listView.removeHeaderView(headerView);
                goUp.setVisibility(View.INVISIBLE);
            }    
        }*/
        for (int i = 0; i < parentContents.length; i++)
        {      
            results[i] = parentContents[i].getName();
        }
        return results;
    }

    File[] listFiles() {
        File[] contents = parentFolder.listFiles();
        if(contents == null){
            return new File[]{};
        } 
        
        List<File> results = new ArrayList<File>();
        for (File fi : contents) {
            if (fi.isDirectory()) results.add(fi);
        }
        Collections.sort(results, new FolderSorter());
        return results.toArray(new File[results.size()]);
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        headerView = View.inflate(getActivity(), R.layout.md_listitem_folder_dia_header, null);
        MaterialDialog dialog =  new MaterialDialog.Builder(getActivity())
                .title(parentFolder.getAbsolutePath())
                .listViewHeader(headerView)
                .items(getContentsArray())
                .itemsCallback(this)
                .callback(mButtonCallback)
                .autoDismiss(false)
                .positiveText(R.string.choose)
                .neutralText(R.string.default_dir)
                .negativeText(R.string.cancel)
                .build();
        dialog.setCanceledOnTouchOutside(true);
        listView = dialog.getListView();
        if(listView != null){
            int colorPrimary = getActivity().getResources().getColor(R.color.primary);
            //headerView = View.inflate(getActivity(), R.layout.md_listitem_folder_dia_header, null);
            TextView newFolder = (TextView) headerView.findViewById(R.id.newFolder);
            newFolder.setText(" "+getString(R.string.new_f));
            newFolder.setTextColor(colorPrimary);
            newFolder.setOnClickListener(new View.OnClickListener() {
                
                @Override
                public void onClick(View v) {
                    // TODO Auto-generated method stub
                   
                    FrameLayout layout = (FrameLayout) View.inflate(getActivity(), R.layout.simple_input, null);
                    final com.rey.material.widget.EditText mEditText = (com.rey.material.widget.EditText) layout.findViewById(android.R.id.edit);
                    
                   /* mEditText.setOnFocusChangeListener(new View.OnFocusChangeListener() {
                        
                        @Override
                        public void onFocusChange(View v, boolean hasFocus) {
                            if(!hasFocus)
                                mEditText.setError(null);
                        }
                    });*/
                    mEditText.addTextChangedListener(new TextWatcher(){

                        @Override
                        public void afterTextChanged(Editable s) {
                            // TODO Auto-generated method stub
                            mEditText.setDividerAni(true);
                            String newFolderName = s.toString().trim();
                            if(isFileNameExists(newFolderName)){
                                mEditText.setError(getString(R.string.folder_name_exists));
                            }
                            else{
                                mEditText.setError(null);
                            }
                        }

                        @Override
                        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                            // TODO Auto-generated method stub
                            
                        }

                        @Override
                        public void onTextChanged(CharSequence s, int start, int before, int count) {
                            // TODO Auto-generated method stub
                            
                        }});
                    
                    
                    Builder mBuilder = new MaterialDialog.Builder(getActivity())
                    .title(R.string.new_folder)
                    //.icon(getDialogIcon())
                    .positiveText(R.string.ok)
                    .negativeText(R.string.cancel)
                    .autoDismiss(false)
                    .callback(new ButtonCallback(){
                        @Override
                        public void onPositive(MaterialDialog dialog) {
                            //mEditText.setError(null);
                            mEditText.setDividerAni(true);
                            String newFolderName = mEditText.getText().toString().trim();
                            
                            if(TextUtils.isEmpty(newFolderName)){
                                mEditText.setError(getString(R.string.not_null));
                            }
                            else{
                                if(isFileNameExists(newFolderName)) {
                                    mEditText.setError(getString(R.string.folder_name_exists));
                                }
                                else{
                                    //
                                    File newDir = new File(parentFolder.getAbsolutePath()+File.separator+newFolderName);
                                    newDir.mkdir();
                                    Utils.hideInputMethod(getActivity(),mEditText);
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
                    MaterialDialog newFolderDia = mBuilder.build();
                    newFolderDia.setCanceledOnTouchOutside(true);
                    newFolderDia.show();
                }
            });
            goUp = (TextView) headerView.findViewById(R.id.goUp);
            goUp.setText(" "+getString(R.string.go_up));
            goUp.setTextColor(colorPrimary);
            goUp.setOnClickListener(new View.OnClickListener() {
                
                @Override
                public void onClick(View v) {
                    // TODO Auto-generated method stub
                    parentFolder = parentFolder.getParentFile();
                    //canGoUp = parentFolder.getParent() != null;
                    if(parentFolder.equals(Environment.getExternalStorageDirectory())){
                        //canGoUp = false;
                        goUp.setVisibility(View.INVISIBLE);
                    }
                    else{
                        //canGoUp = true;
                        goUp.setVisibility(View.VISIBLE);
                    }
                    
                    
                    parentContents = listFiles();
                    MaterialDialog dialog = (MaterialDialog) getDialog();
                    dialog.setTitle(parentFolder.getAbsolutePath());
                    dialog.setItems(getContentsArray());
                }
            });
            goUp.setVisibility(View.INVISIBLE);
            //listView.addHeaderView(headerView, null, false);
        }
       
        return dialog;
    }

    @Override
    public void onSelection(MaterialDialog materialDialog, View view, int i, CharSequence s) {
        parentFolder = parentContents[i];
        //canGoUp = true;
        goUp.setVisibility(View.VISIBLE);
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

    private boolean isFileNameExists(String filename){
        boolean foundSameName = false;
        for(File file : parentContents){
            if(file.getName().equalsIgnoreCase(filename)){
                foundSameName = true;
                break;
            }
        }
        return foundSameName;
    }
    
    
    private static class FolderSorter implements Comparator<File> {
        @Override
        public int compare(File lhs, File rhs) {
            return lhs.getName().toLowerCase(Locale.getDefault()).compareTo(rhs.getName().toLowerCase(Locale.getDefault()));
        }
    }
}
