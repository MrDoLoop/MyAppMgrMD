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
public class SelectionDialogFragment extends DialogFragment {

    public final static String DialogTag = "SelectiongDialogFragment";

    public final static String ArgumentsTag = "ArgumentsTag";

    private String[] selectionOpt;// = {"Select all above","Select all below"};
    private int[] selectionOptIcon;// = {"Select all above","Select all below"};

    public final static int SELECT_ALL_ABOVE = 0;
    public final static int DESELECT_ALL_ABOVE = 1;
    public final static int SELECT_ALL_BELOW = 2;
    public final static int DESELECT_ALL_BELOW = 3;

    public SelectionDialogClickListener mSelectionDialogClickListener;

    // Container Activity must implement this interface
    public interface SelectionDialogClickListener {
        public void onSelectionDialogClick(DialogInterface dialog, int selectType, int curPos);
    }

    private AppInfo mAppinfo;
    private int mCurPos;
    private int mListTotleSize;

    /*
     * public static SelectionDialogFragment newInstance(AppInfo appInfo, int curPos, int listTotleSize) {
     * SelectionDialogFragment fragmentInstance = new SelectionDialogFragment(); int[] ArgumentsArray = new
     * int[2];//0:��ǰλ��, 1: �б��ܳ��� ArgumentsArray[0] = curPos; ArgumentsArray[1] = listTotleSize; Bundle bundle = new
     * Bundle(); bundle.putIntArray(SelectionDialogFragment.ArgumentsTag, ArgumentsArray);
     * fragmentInstance.setArguments(bundle);
     * 
     * mAppinfo = appInfo;
     * 
     * 
     * 
     * return fragmentInstance; }
     */

    public void setArgs(AppInfo appInfo, int curPos, int listTotleSize, SelectionDialogClickListener l) {
        mAppinfo = appInfo;
        mCurPos = curPos;
        mListTotleSize = listTotleSize;
        mSelectionDialogClickListener = l;
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        /*
         * try { mSelectionDialogClickListener = (SelectionDialogClickListener) activity; } catch (ClassCastException e)
         * { throw new ClassCastException(activity.toString() + " must implement SelectionDialogClickListener"); }
         */
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        // Build the dialog and set up the button click handlers
        /*
         * int[] arguments = getArguments().getIntArray(ArgumentsTag); //0��ǰλ��, 1,�б��� final int curPos = arguments[0];
         * final int listTotleSize = arguments[1];
         */
        if (mCurPos == 0)// �б��һ��
        {
            selectionOpt = new String[2];
            selectionOpt[0] = getActivity().getString(R.string.select_all_below);
            selectionOpt[1] = getActivity().getString(R.string.deselect_all_below);

            selectionOptIcon = new int[2];
            selectionOptIcon[0] = R.drawable.select_all_below;
            selectionOptIcon[1] = R.drawable.deselect_all_below;
        } else if (mCurPos == mListTotleSize - 1)// �б����һ��
        {
            selectionOpt = new String[2];
            selectionOpt[0] = getActivity().getString(R.string.select_all_above);
            selectionOpt[1] = getActivity().getString(R.string.deselect_all_above);

            selectionOptIcon = new int[2];
            selectionOptIcon[0] = R.drawable.select_all_above;
            selectionOptIcon[1] = R.drawable.deselect_all_above;
        } else {
            selectionOpt = new String[4];
            selectionOpt[0] = getActivity().getString(R.string.select_all_above);
            selectionOpt[1] = getActivity().getString(R.string.deselect_all_above);
            selectionOpt[2] = getActivity().getString(R.string.select_all_below);
            selectionOpt[3] = getActivity().getString(R.string.deselect_all_below);

            selectionOptIcon = new int[4];
            selectionOptIcon[0] = R.drawable.select_all_above;
            selectionOptIcon[1] = R.drawable.deselect_all_above;
            selectionOptIcon[2] = R.drawable.select_all_below;
            selectionOptIcon[3] = R.drawable.deselect_all_below;
        }

        ArrayAdapterWithIcon adapter = new ArrayAdapterWithIcon(getActivity(), selectionOpt, selectionOptIcon);

        // AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        MaterialDialog.Builder builder =
                new MaterialDialog.Builder(getActivity()).title(mAppinfo.appName)
                        .icon(Utilities.ZoomDrawable(mAppinfo.iconBitmap, getActivity())).adapter(adapter);
        final MaterialDialog dialog = builder.build();

        ListView listView = dialog.getListView();
        if (listView != null) {
            listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    if (selectionOpt.length == 4) {
                        mSelectionDialogClickListener.onSelectionDialogClick(dialog, position, mCurPos);
                    } else if (selectionOpt.length == 2) {
                        if (mCurPos == 0)// �б��һ��
                        {
                            if (position == 0) {
                                mSelectionDialogClickListener.onSelectionDialogClick(dialog, SELECT_ALL_BELOW, mCurPos);
                            } else if (position == 1) {
                                mSelectionDialogClickListener.onSelectionDialogClick(dialog, DESELECT_ALL_BELOW,
                                        mCurPos);
                            }
                        } else if (mCurPos == mListTotleSize - 1)// �б����һ��
                        {
                            if (position == 0) {
                                mSelectionDialogClickListener.onSelectionDialogClick(dialog, SELECT_ALL_ABOVE, mCurPos);
                            } else if (position == 1) {
                                mSelectionDialogClickListener.onSelectionDialogClick(dialog, DESELECT_ALL_ABOVE,
                                        mCurPos);
                            }
                        }
                    }
                    dialog.dismiss();
                }
            });
        }

        return dialog;
    }
}
