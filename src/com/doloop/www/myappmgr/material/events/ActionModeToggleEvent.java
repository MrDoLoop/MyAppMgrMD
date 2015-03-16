package com.doloop.www.myappmgr.material.events;

public class ActionModeToggleEvent {
    public boolean isInActionMode = false;
    public ActionModeToggleEvent(boolean isActionMode){
        this.isInActionMode = isActionMode;
    }
}
