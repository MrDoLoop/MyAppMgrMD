package com.doloop.www.myappmgr.material.fragments;

import java.io.File;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.afollestad.materialdialogs.MaterialDialog;
import com.doloop.www.myappmgr.material.events.DrawerItemClickEvent;
import com.doloop.www.myappmgr.material.events.DrawerItemClickEvent.DrawerItem;
import com.doloop.www.myappmgr.material.utils.NanAppMark;
import com.doloop.www.myappmgr.material.utils.Utils;
import com.doloop.www.myappmgr.material.widgets.DrawerItem2Rows;
import com.doloop.www.myappmgr.material.R;
import com.nineoldandroids.animation.Animator;
import com.nineoldandroids.animation.AnimatorListenerAdapter;
import com.nineoldandroids.animation.ObjectAnimator;
import de.greenrobot.event.EventBus;

public class DrawerFragment extends Fragment implements FolderSelectorDialog.FolderSelectCallback {
    private static DrawerFragment uniqueInstance = null;
    private DrawerItem2Rows DrawerItemBackupDir;
    //private static Context mContext;

    public DrawerFragment() {

    }

    public synchronized static DrawerFragment getInstance(Context ctx) {
        if (uniqueInstance == null) {
            uniqueInstance = new DrawerFragment();
        }
        //mContext = ctx;
        return uniqueInstance;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Create, or inflate the Fragment’s UI, and return it.
        // If this Fragment has no UI then return null.
        View FragmentView = inflater.inflate(R.layout.drawer_content, container, false);
        View refItem = FragmentView.findViewById(R.id.drawerRefresh);
        refItem.setOnClickListener(new View.OnClickListener() {
            
            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                EventBus.getDefault().post(new DrawerItemClickEvent(DrawerItem.REFRESH));
            }
        });
        
        View tv = FragmentView.findViewById(R.id.logo_txt);
        NanAppMark.attach(tv);
        
        DrawerItemBackupDir = (DrawerItem2Rows) FragmentView.findViewById(R.id.backupApkDir);
        DrawerItemBackupDir.setSecondRowTxt(Utils.getBackUpAPKfileDir(getActivity()));
        DrawerItemBackupDir.setOnClickListener(new View.OnClickListener() {
            
            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                new FolderSelectorDialog().show(getActivity().getSupportFragmentManager(),DrawerFragment.this);
            }
        });
        
        final View headerImg = FragmentView.findViewById(R.id.header_image);
        headerImg.setOnClickListener(new View.OnClickListener() {
            
            @Override
            public void onClick(View v) {
                ObjectAnimator ani = ObjectAnimator.ofFloat(headerImg, "rotationY", 0, 360).setDuration(1000);
                ani.addListener(new AnimatorListenerAdapter(){

                    @Override
                    public void onAnimationEnd(Animator arg0) {
                        // TODO Auto-generated method stub
                        headerImg.setEnabled(true);
                    }

                    @Override
                    public void onAnimationStart(Animator arg0) {
                        // TODO Auto-generated method stub
                        headerImg.setEnabled(false);
                    }} );
                ani.start();
            }
        });
        TextView verTv = (TextView) FragmentView.findViewById(R.id.ver);
        verTv.setText(Utils.getSelfVerName(getActivity()));
        return FragmentView;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        // Complete the Fragment initialization – particularly anything
        // that requires the parent Activity to be initialized or the
        // Fragment’s view to be fully inflated.
        setRetainInstance(false);
        setHasOptionsMenu(false);
    }
    
    // Called at the start of the visible lifetime.
    @Override
    public void onStart() {
        super.onStart();
        // Apply any required UI change now that the Fragment is visible.
    }

    // Called at the start of the active lifetime.
    @Override
    public void onResume() {
        super.onResume();
        // Resume any paused UI updates, threads, or processes required
    }

    // Called at the end of the active lifetime.
    @Override
    public void onPause() {
        // Suspend UI updates, threads, or CPU intensive processes
        // that don’t need to be updated when the Activity isn’t
        // the active foreground activity.
        // Persist all edits or state changes
        // as after this call the process is likely to be killed.
        super.onPause();
    }

    // Called to save UI state changes at the
    // end of the active lifecycle.
    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
        // Save UI state changes to the savedInstanceState.
        // This bundle will be passed to onCreate, onCreateView, and
        // onCreateView if the parent Activity is killed and restarted.
        // super.onSaveInstanceState(savedInstanceState);
    }

    // Called at the end of the visible lifetime.
    @Override
    public void onStop() {
        // Suspend remaining UI updates, threads, or processing
        // that aren’t required when the Fragment isn’t visible.
        super.onStop();
    }

    // Called when the Fragment’s View has been detached.
    @Override
    public void onDestroyView() {
        // Clean up resources related to the View.
        super.onDestroyView();
    }

    // Called at the end of the full lifetime.
    @Override
    public void onDestroy() {
        // Clean up any resources including ending threads,
        // closing database connections etc.
        super.onDestroy();
        //mContext = null;
        uniqueInstance = null;
    }

    // Called when the Fragment has been detached from its parent Activity.
    @Override
    public void onDetach() {
        super.onDetach();
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
       
    }

    @Override
    public void onFolderSelection(File folder) {
        // TODO Auto-generated method stub
        final String newpath = folder.getAbsolutePath()+"/";
        final String curBackupPath = Utils.getBackUpAPKfileDir(getActivity());
        DrawerItemBackupDir.setSecondRowTxt(newpath);
        
        if(!curBackupPath.equals(newpath)){
            Utils.saveBackUpAPKfileDir(getActivity(), newpath);
            MaterialDialog dialog = new MaterialDialog.Builder(getActivity())
            .content(R.string.move_apk)
            .positiveText(R.string.ok)
            .negativeText(R.string.cancel)
            .callback(new MaterialDialog.ButtonCallback() {
                @Override
                public void onPositive(MaterialDialog dialog) {
                    DrawerItemClickEvent changeDirEv = new DrawerItemClickEvent(DrawerItem.CHG_BACKUP_DIR);
                    changeDirEv.oldPath = curBackupPath;
                    changeDirEv.newPath = newpath;
                    EventBus.getDefault().post(changeDirEv);
                }

                @Override
                public void onNegative(MaterialDialog dialog) {
                    
                }
            }).build();
            dialog.setCanceledOnTouchOutside(false);
            dialog.show();
          //把旧目录下的apk文件移动到新的目录下
            /*try {
                FileUtils.copyDirectory(new File(curBackupPath), new File(newpath));
                FileUtils.deleteDirectory(new File(curBackupPath));
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }*/
            
        }
    }
}
