package com.doloop.slideexpandable.library;

import android.view.View;

public interface IExpendButtonClickListener {
    /**
     * 
     * @param position 列表中的位置
     * @param expendableArea 即将要展开/关闭的view
     * @param expand true 展开 false 关闭
     */
    public void OnExpendButtonClick(int position,boolean expand, View expendableArea);
}
