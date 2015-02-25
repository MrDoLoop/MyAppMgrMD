package com.doloop.www.myappmgr.material.utils;

import android.view.View;
import android.widget.Toast;

public class NanAppMark {
    private static int viewClickCtr = 0;
    private static Toast toast;
    private static long firstTimeClick = 0;

    public static void attach(View v) {
        if (toast == null) {
            toast = Toast.makeText(v.getContext(), "Thanks to NAN's outstanding contribution !", Toast.LENGTH_SHORT);
        }
        v.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                if (viewClickCtr == 0) {
                    firstTimeClick = System.currentTimeMillis();
                }

                viewClickCtr++;//3ÃëÄÚµã8´Î
                if (viewClickCtr >= 8) {
                    viewClickCtr = 0;

                    if (System.currentTimeMillis() - firstTimeClick <= 3 * 1000) {
                        toast.show();
                    }
                }
            }
        });
    }
}
