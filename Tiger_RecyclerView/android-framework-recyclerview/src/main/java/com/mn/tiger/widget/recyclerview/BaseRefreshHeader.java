package com.mn.tiger.widget.recyclerview;

/**
 * Created by jianghejie on 15/11/22.
 */
interface BaseRefreshHeader
{
    public int getAnimDuration();

    public void onMove(float delta);

    public boolean releaseAction();

    public void refreshComplete();

    public final static int STATE_NORMAL = 0;
    public final static int STATE_RELEASE_TO_REFRESH = 1;
    public final static int STATE_REFRESHING = 2;
    public final static int STATE_DONE = 3;
}
