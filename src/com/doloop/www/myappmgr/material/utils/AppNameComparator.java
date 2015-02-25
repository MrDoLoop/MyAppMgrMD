package com.doloop.www.myappmgr.material.utils;

import java.text.Collator;
import java.util.Comparator;
import java.util.Locale;

import com.doloop.www.myappmgr.material.dao.AppInfo;

public class AppNameComparator implements Comparator<AppInfo>{
	private final Collator sCollator = Collator.getInstance();
	private boolean mAsc = false;
	/***
	 * 
	 * @param Asc:true asc, false des
	 */
	public AppNameComparator(boolean Asc)
	{
		this.mAsc = Asc; 
	}
	
	@Override
	public int compare(AppInfo lhs, AppInfo rhs) {
		// TODO Auto-generated method stub
		//return lhs.appName.toLowerCase().compareTo(rhs.appName.toLowerCase());
		if(mAsc)
		{
			return sCollator.compare(lhs.appName.toLowerCase(Locale.getDefault()), rhs.appName.toLowerCase(Locale.getDefault()));
		}
		else
		{
			return -sCollator.compare(lhs.appName.toLowerCase(Locale.getDefault()), rhs.appName.toLowerCase(Locale.getDefault()));
		}
		
	}

}
