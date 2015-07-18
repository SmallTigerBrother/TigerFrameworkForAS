package com.mn.tiger.widget.pulltorefresh.loading;

import android.view.View;

/**
 * Created by peng on 15/7/17.
 */
public interface ILoadingFooterView
{
    void normal();

    void show();

    void hide();

    void loading();

    void setState(int state);

    int getBottomMargin();

    void setBottomMargin(int margin);

    void setOnClickListener(View.OnClickListener listener);
}
