package com.doloop.www.myappmgr.material.utils;

import java.text.Collator;
import java.util.Comparator;
import java.util.Locale;

import com.doloop.www.myappmgr.material.dao.AppInfo;

public class AppPinYinComparator implements Comparator<AppInfo>{
	private final Collator sCollator = Collator.getInstance();
	@Override
	public int compare(AppInfo lhs, AppInfo rhs) {
		// TODO Auto-generated method stub	
		String str1 = "";
		String str2 = "";
		if(lhs.appSortName.length()>0 && rhs.appSortName.length()>0)//中 : 中
		{
			str1 = lhs.appSortName;
			str2 = rhs.appSortName;
		}
		else if(lhs.appSortName.length()==0 && rhs.appSortName.length()==0)//英 : 英
		{
			str1 = lhs.appName;
			str2 = rhs.appName;
		}
		else if(lhs.appSortName.length()==0 && rhs.appSortName.length()>0)//英 : 中
		{
			return -1;
		}
		else if(lhs.appSortName.length()>0 && rhs.appSortName.length()==0)//中 : 英
		{
			return 1;
		}
		
		return sCollator.compare(str1.toLowerCase(Locale.getDefault()), str2.toLowerCase(Locale.getDefault()));
	}

}
