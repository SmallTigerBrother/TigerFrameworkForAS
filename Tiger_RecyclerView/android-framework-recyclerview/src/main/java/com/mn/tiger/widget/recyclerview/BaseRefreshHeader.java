package com.mn.tiger.widget.recyclerview;

/**
 * Created by jianghejie on 15/11/22.
 */
public interface BaseRefreshHeader
{
    public final static int STATE_NORMAL = 0;
    public final static int STATE_RELEASE_TO_REFRESH = 1;
    public final static int STATE_REFRESHING = 2;
    public final static int STATE_DONE = 3;

    int getAnimDuration();

    void onMove(float delta);

    boolean releaseAction();

    void refreshComplete();

    void setProgressStyle(int progressStyle);

    int getVisibleHeight();

    int getState();
}
