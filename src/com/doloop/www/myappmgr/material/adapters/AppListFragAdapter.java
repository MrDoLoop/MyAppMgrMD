package com.doloop.www.myappmgr.material.adapters;

import java.util.ArrayList;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.doloop.www.myappmgr.material.fragments.BaseFrag;

public class AppListFragAdapter extends FragmentPagerAdapter {//FragmentPagerAdapter
    public AppListFragAdapter(FragmentManager fm) {
        super(fm);
        // TODO Auto-generated constructor stub
    }

    private ArrayList<BaseFrag> mFragmentlist;

    public AppListFragAdapter(FragmentManager fm, ArrayList<BaseFrag> Fragmentlist) {
        super(fm);
        this.mFragmentlist = Fragmentlist;
    }

    @Override
    public int getCount() {
        return mFragmentlist.size();
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return mFragmentlist.get(position).getFragmentTitle();
    }

   /* @Override
     public Object instantiateItem(ViewGroup container, int position) {
        BaseFrag f = (BaseFrag) super.instantiateItem(container, position);
        String title = mFragmentlist.get(position).getFragmentTitle();
        f.setFragmentTitle(title);
        return f;
    }*/
    
   /* @Override
    public int getItemPosition(Object object) {
        return PagerAdapter.POSITION_NONE;
    }*/

    @Override
    public Fragment getItem(int position) {
        // TODO Auto-generated method stub
        return mFragmentlist.get(position);
    }
}
