package com.doloop.www.myappmgr.material.fragments;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.doloop.www.mayappmgr.material.events.DrawerItemClickEvent;
import com.doloop.www.mayappmgr.material.events.DrawerItemClickEvent.DrawerItem;
import com.doloop.www.myappmgr.material.utils.NanAppMark;
import com.doloop.www.myappmgr.material.utils.Utilities;
import com.doloop.www.myappmgrmaterial.R;

import de.greenrobot.event.EventBus;

public class DrawerFragment extends Fragment {
    private static DrawerFragment uniqueInstance = null;
    private static Context mContext;

    public DrawerFragment() {

    }

    public synchronized static DrawerFragment getInstance(Context ctx) {
        if (uniqueInstance == null) {
            uniqueInstance = new DrawerFragment();
        }
        mContext = ctx;
        return uniqueInstance;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Create, or inflate the Fragment¡¯s UI, and return it.
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
        
        View tv = FragmentView.findViewById(R.id.textView);
        NanAppMark.attach(tv);
        
        View headerImg = FragmentView.findViewById(R.id.header_image);
        headerImg.setOnClickListener(new View.OnClickListener() {
            
            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
            }
        });
        TextView verTv = (TextView) FragmentView.findViewById(R.id.ver);
        verTv.setText(Utilities.getSelfVerName(getActivity()));
        return FragmentView;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        // Complete the Fragment initialization ¨C particularly anything
        // that requires the parent Activity to be initialized or the
        // Fragment¡¯s view to be fully inflated.
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
        // that don¡¯t need to be updated when the Activity isn¡¯t
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
        // that aren¡¯t required when the Fragment isn¡¯t visible.
        super.onStop();
    }

    // Called when the Fragment¡¯s View has been detached.
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
        mContext = null;
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
        // mContext = activity;
        // Get a reference to the parent Activity.
        /*
         * try { mListener = (OnSysAppListItemSelectedListener) activity; } catch (ClassCastException e) { throw new
         * ClassCastException(activity.toString() + "must implement Listener"); }
         */
    }
    
}
