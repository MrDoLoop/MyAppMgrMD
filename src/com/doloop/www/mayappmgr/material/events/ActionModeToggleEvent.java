package com.doloop.www.mayappmgr.material.events;

public class ActionModeToggleEvent {
    public boolean isInActionMode = false;
    public ActionModeToggleEvent(boolean isActionMode){
        this.isInActionMode = isActionMode;
    }
}
