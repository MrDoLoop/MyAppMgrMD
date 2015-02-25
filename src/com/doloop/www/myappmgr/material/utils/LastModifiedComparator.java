package com.doloop.www.myappmgr.material.utils;

import java.util.Comparator;

import com.doloop.www.myappmgr.material.dao.AppInfo;

public class LastModifiedComparator implements Comparator<AppInfo>{
	private boolean mAsc = false;
	/***
	 * 
	 * @param Asc:true asc, false des
	 */
	public LastModifiedComparator(boolean Asc)
	{
		this.mAsc = Asc; 
	}
	
	@Override
	public int compare(AppInfo lhs, AppInfo rhs) {
		// TODO Auto-generated method stub
		long diff = lhs.lastModifiedRawTime - rhs.lastModifiedRawTime;
		
		if(mAsc)
		{
			if(diff == 0)
				return 0;
			else if(diff > 0)
				return 1;
			else
				return -1;
				
		}
		else
		{
			if(diff == 0)
				return 0;
			else if(diff > 0)
				return -1;
			else
				return 1;
		}
	}

}
