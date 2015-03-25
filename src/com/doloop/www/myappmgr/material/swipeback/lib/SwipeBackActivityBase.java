package com.doloop.www.myappmgr.material.swipeback.lib;

/**
 * @author Yrom
 */
public interface SwipeBackActivityBase {
    /**��ȡSwipeBackLayout
     * @return the SwipeBackLayout associated with this activity.
     */
    public abstract SwipeBackLayout getSwipeBackLayout();
    /**
     * �Ƿ���Ի���
     * @param enable
     */
    public abstract void setSwipeBackEnable(boolean enable);

    /**
     * Scroll out contentView and finish the activity
     */
    public abstract void scrollToFinishActivity();

}
