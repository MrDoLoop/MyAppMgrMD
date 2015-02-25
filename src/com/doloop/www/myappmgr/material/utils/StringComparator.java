package com.doloop.www.myappmgr.material.utils;

import java.text.Collator;
import java.util.Comparator;
import java.util.Locale;

public class StringComparator implements Comparator<String>{
	private final Collator sCollator = Collator.getInstance();
	@Override
	public int compare(String lhs, String rhs) {
		// TODO Auto-generated method stub
		//return lhs.appName.toLowerCase().compareTo(rhs.appName.toLowerCase());
		
		return sCollator.compare(lhs.toLowerCase(Locale.getDefault()), rhs.toLowerCase(Locale.getDefault()));
	}

}
