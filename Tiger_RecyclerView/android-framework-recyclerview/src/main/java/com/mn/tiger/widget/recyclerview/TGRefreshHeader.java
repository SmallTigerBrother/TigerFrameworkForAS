package com.mn.tiger.widget.recyclerview;

import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.AnimationDrawable;
import android.os.Handler;
import android.support.v7.widget.RecyclerView;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.RotateAnimation;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.mn.tiger.utility.CR;

/**
 * Created by peng on 16/1/13.
 */
public class TGRefreshHeader extends LinearLayout implements BaseRefreshHeader
{
    private ImageView mHeaderArrowIv;
    private ImageView mHeaderChrysanthemumIv;
    private AnimationDrawable mHeaderChrysanthemumAd;
    private RotateAnimation mUpAnim;
    private RotateAnimation mDownAnim;

    private int mState = STATE_NORMAL;

    private LinearLayout mContainer;

    public int mMeasuredHeight;

    public TGRefreshHeader(Context context)
    {
        super(context);
        LayoutParams lp = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
        lp.setMargins(0, 0, 0, 0);
        this.setLayoutParams(lp);
        this.setPadding(0, 0, 0, 0);
        this.setBackgroundColor(Color.TRANSPARENT);
        inflate(context, R.layout.tiger_refresh_header, this);

        initView();
        initAnimation();
    }

    private void initAnimation()
    {
        mUpAnim = new RotateAnimation(0, -180, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
        mUpAnim.setDuration(150);
        mUpAnim.setFillAfter(true);

        mDownAnim = new RotateAnimation(-180, 0, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
        mDownAnim.setFillAfter(true);
    }

    private void initView()
    {
        mContainer = (LinearLayout)findViewById(CR.getViewId(getContext(), "tiger_refresh_header_layout"));
        LayoutParams layoutParams = (LayoutParams)mContainer.getLayoutParams();
        layoutParams.width = LayoutParams.MATCH_PARENT;
        layoutParams.height = 0;
        mContainer.setLayoutParams(layoutParams);

        mHeaderArrowIv = (ImageView) findViewById(CR.getViewId(getContext(), "tiger_iv_default_refresh_header_arrow"));
        mHeaderChrysanthemumIv = (ImageView) findViewById(CR.getViewId(getContext(), "tiger_iv_default_refresh_header_chrysanthemum"));
        mHeaderChrysanthemumAd = (AnimationDrawable) mHeaderChrysanthemumIv.getDrawable();

        measure(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        mMeasuredHeight = getMeasuredHeight();
    }

    @Override
    public int getAnimDuration()
    {
        return 450;
    }

    @Override
    public void onMove(float delta)
    {
        if (getVisibleHeight() > 0 || delta > 0)
        {
            setVisibleHeight((int) delta + getVisibleHeight());
            if (mState <= STATE_RELEASE_TO_REFRESH)
            { // 未处于刷新状态，更新箭头
                if (getVisibleHeight() > mMeasuredHeight)
                {
                    setState(STATE_RELEASE_TO_REFRESH);
                } else
                {
                    setState(STATE_NORMAL);
                }
            }
        }
    }

    @Override
    public boolean releaseAction()
    {
        boolean isOnRefresh = false;
        int height = getVisibleHeight();
        if (height == 0) // not visible.
        {
            isOnRefresh = false;
        }

        if (getVisibleHeight() > mMeasuredHeight && mState < STATE_REFRESHING)
        {
            setState(STATE_REFRESHING);
            isOnRefresh = true;
        }
        // refreshing and header isn't shown fully. do nothing.
        if (mState == STATE_REFRESHING && height <= mMeasuredHeight)
        {
            //return;
        }
        int destHeight = 0; // default: scroll back to dismiss header.
        // is refreshing, just scroll back to show all the header.
        if (mState == STATE_REFRESHING)
        {
            destHeight = mMeasuredHeight;
        }
        smoothScrollTo(destHeight);

        return isOnRefresh;
    }

    private void smoothScrollTo(int destHeight)
    {
        ValueAnimator animator = ValueAnimator.ofInt(getVisibleHeight(), destHeight);
        animator.setDuration(300).start();
        animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener()
        {
            @Override
            public void onAnimationUpdate(ValueAnimator animation)
            {
                setVisibleHeight((int) animation.getAnimatedValue());
            }
        });
        animator.start();
    }

    @Override
    public void refreshComplete()
    {
        setState(STATE_DONE);
        smoothScrollTo(0);
        setState(STATE_NORMAL);
    }

    @Override
    public void setProgressStyle(int progressStyle)
    {

    }

    public void setVisibleHeight(int height)
    {
        if (height < 0)
        {
            height = 0;
        }
        LayoutParams lp = (LayoutParams) mContainer.getLayoutParams();
        lp.height = height;
        mContainer.setLayoutParams(lp);
    }

    @Override
    public int getVisibleHeight()
    {
        LayoutParams lp = (LayoutParams) mContainer.getLayoutParams();
        return lp.height;
    }

    public void setState(int state)
    {
        if (state == mState)
        {
            return;
        }

        switch (state)
        {
            case STATE_REFRESHING:
               // 必须把动画清空才能隐藏成功
                mHeaderArrowIv.clearAnimation();
                mHeaderArrowIv.setVisibility(View.INVISIBLE);
                mHeaderChrysanthemumIv.setVisibility(View.VISIBLE);
                mHeaderChrysanthemumAd.start();

                break;
            case STATE_DONE:
                mHeaderChrysanthemumIv.setVisibility(View.INVISIBLE);
                mHeaderArrowIv.setVisibility(View.INVISIBLE);
                break;
            case STATE_NORMAL:
                if(mState == STATE_RELEASE_TO_REFRESH)
                {
                    mHeaderChrysanthemumIv.setVisibility(View.INVISIBLE);
                    mHeaderChrysanthemumAd.stop();
                    mHeaderArrowIv.setVisibility(View.VISIBLE);
                    mDownAnim.setDuration(150);
                    mHeaderArrowIv.startAnimation(mDownAnim);
                }
                break;
            case STATE_RELEASE_TO_REFRESH:
                mHeaderChrysanthemumIv.setVisibility(View.INVISIBLE);
                mHeaderChrysanthemumAd.stop();
                mHeaderArrowIv.setVisibility(View.VISIBLE);
                mHeaderArrowIv.startAnimation(mUpAnim);
                break;
        }

        mState = state;
    }

    @Override
    public int getState()
    {
        return mState;
    }
}
