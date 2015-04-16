package com.doloop.www.myappmgr.material.interfaces;

import com.doloop.www.myappmgr.material.dao.AppInfo;
import com.doloop.www.myappmgr.material.utils.PopupListMenu.POPUP_MENU_LIST_ITEM;

public interface IPopupMenuClickListener {
    public void OnPopupMenuClick(POPUP_MENU_LIST_ITEM menuItem, int menuListPos, int appListPos, AppInfo appInfo);
}
