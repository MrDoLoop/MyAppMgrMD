package com.doloop.www.myappmgr.material.swipeback.lib;

/**
 * @author Yrom
 */
public interface SwipeBackActivityBase {
    /**获取SwipeBackLayout
     * @return the SwipeBackLayout associated with this activity.
     */
    public abstract SwipeBackLayout getSwipeBackLayout();
    /**
     * 是否可以滑动
     * @param enable
     */
    public abstract void setSwipeBackEnable(boolean enable);

    /**
     * Scroll out contentView and finish the activity
     */
    public abstract void scrollToFinishActivity();

}
