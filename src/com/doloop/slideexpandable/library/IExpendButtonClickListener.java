package com.doloop.slideexpandable.library;

import android.view.View;

public interface IExpendButtonClickListener {
    /**
     * 
     * @param position �б��е�λ��
     * @param expendableArea ����Ҫչ��/�رյ�view
     * @param expand true չ�� false �ر�
     */
    public void OnExpendButtonClick(int position,boolean expand, View expendableArea);
}
