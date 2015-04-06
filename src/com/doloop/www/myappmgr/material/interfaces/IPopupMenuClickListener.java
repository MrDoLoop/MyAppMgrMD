package com.doloop.www.myappmgr.material.interfaces;

import com.doloop.www.myappmgr.material.dao.AppInfo;

public interface IPopupMenuClickListener {
    public void OnPopupMenuClick(int menuListPos, int appListPos, AppInfo appInfo);
}
