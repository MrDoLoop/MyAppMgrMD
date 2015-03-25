package com.doloop.www.myappmgr.material.widgets;

import android.content.Context;
import android.view.View;
import android.widget.TextView;

import com.afollestad.materialdialogs.MaterialDialog;
import com.doloop.www.myappmgr.material.R;

public class MyProgressDialog {
    private MaterialDialog progDialog;
    private ArcProgress arcProgress;
    private TextView progressTxt;

    public MyProgressDialog(Context ctx, String title, String contentTxt) {
        View pView = View.inflate(ctx, R.layout.progress_loading, null);
        arcProgress = (ArcProgress) pView.findViewById(R.id.arc_progress);
        progressTxt = (TextView) pView.findViewById(R.id.txt);

        progDialog = new MaterialDialog.Builder(ctx).title(title).customView(pView, false).build();
        progressTxt.setText(contentTxt);
    }

    public void setArcProgressMax(int max) {
        arcProgress.setMax(max);
    }

    public void setArcBottomText(String str) {
        arcProgress.setBottomText(str);
    }
    
    public void setArcProgress(int progress){
        arcProgress.setProgress(progress);
    }
    
    public int getArcProgress(){
        return arcProgress.getProgress();
    }
    
    
    public MaterialDialog getDialog(){
        return progDialog;
    }
    
    public void setDialogTitle(String str){
        progDialog.setTitle(str);
    }
    
    public void setDialogText(String str){
        progressTxt.setText(str);
    }

    public void setCancelable(boolean flag){
        progDialog.setCancelable(flag);
    }
    
    public void show(){
        progDialog.show();
    }
    
    public boolean isShowing(){
        return progDialog.isShowing();
    }
    
    public void dismiss(){
        progDialog.dismiss();
    }

}
