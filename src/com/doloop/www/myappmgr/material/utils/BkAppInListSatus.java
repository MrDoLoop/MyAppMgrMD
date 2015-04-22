package com.doloop.www.myappmgr.material.utils;

public class BkAppInListSatus {
    public enum IN_LIST_STATUS {
        NOT_IN_LIST, IN_LIST_SAME_VER, IN_LIST_DIFF_VER;
    }
    
    public IN_LIST_STATUS mIN_LIST_STATUS;
    public int inListPos = -1;
    public BkAppInListSatus(IN_LIST_STATUS status){
        mIN_LIST_STATUS = status;
    }
    
    public BkAppInListSatus(IN_LIST_STATUS status, int pos){
        mIN_LIST_STATUS = status;
        inListPos = pos;
    }
}
