package com.mn.tiger.widget.pulltorefresh;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.AnimationDrawable;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.RotateAnimation;
import android.widget.ImageView;

import com.mn.tiger.utility.CR;
import com.norbsoft.typefacehelper.TypefaceHelper;


/**
 * Created by peng on 15/9/15.
 */
public class TGRefreshViewHolder extends BGARefreshViewHolder
{
    private ImageView mHeaderArrowIv;
    private ImageView mHeaderChrysanthemumIv;
    private AnimationDrawable mHeaderChrysanthemumAd;
    private RotateAnimation mUpAnim;
    private RotateAnimation mDownAnim;

    /**
     * @param context
     */
    public TGRefreshViewHolder(Context context)
    {
        super(context, true);
        setLoadingMoreText(context.getString(CR.getStringId(context, "tiger_ptr_loading")));
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

    @Override
    public View getRefreshHeaderView()
    {
        if (mRefreshHeaderView == null)
        {
            mRefreshHeaderView = View.inflate(mContext, CR.getLayoutId(mContext, "default_refresh_header"), null);
            mRefreshHeaderView.setBackgroundColor(Color.TRANSPARENT);
            if (mRefreshViewBackgroundColorRes != -1)
            {
                mRefreshHeaderView.setBackgroundResource(mRefreshViewBackgroundColorRes);
            }
            if (mRefreshViewBackgroundDrawableRes != -1)
            {
                mRefreshHeaderView.setBackgroundResource(mRefreshViewBackgroundDrawableRes);
            }
            mHeaderArrowIv = (ImageView) mRefreshHeaderView.findViewById(CR.getViewId(mContext, "iv_default_refresh_header_arrow"));
            mHeaderChrysanthemumIv = (ImageView) mRefreshHeaderView.findViewById(CR.getViewId(mContext, "iv_default_refresh_header_chrysanthemum"));
            mHeaderChrysanthemumAd = (AnimationDrawable) mHeaderChrysanthemumIv.getDrawable();
        }

        TypefaceHelper.typeface(mRefreshHeaderView);

        return mRefreshHeaderView;
    }

    @Override
    public void handleScale(float scale, int moveYDistance)
    {
    }

    @Override
    public void changeToIdle()
    {
    }

    @Override
    public void changeToPullDown()
    {
        mHeaderChrysanthemumIv.setVisibility(View.INVISIBLE);
        mHeaderChrysanthemumAd.stop();
        mHeaderArrowIv.setVisibility(View.VISIBLE);
        mDownAnim.setDuration(150);
        mHeaderArrowIv.startAnimation(mDownAnim);
    }

    @Override
    public void changeToReleaseRefresh()
    {
        mHeaderChrysanthemumIv.setVisibility(View.INVISIBLE);
        mHeaderChrysanthemumAd.stop();
        mHeaderArrowIv.setVisibility(View.VISIBLE);
        mHeaderArrowIv.startAnimation(mUpAnim);
    }

    @Override
    public void changeToRefreshing()
    {
        // 必须把动画清空才能隐藏成功
        mHeaderArrowIv.clearAnimation();
        mHeaderArrowIv.setVisibility(View.INVISIBLE);
        mHeaderChrysanthemumIv.setVisibility(View.VISIBLE);
        mHeaderChrysanthemumAd.start();
    }

    @Override
    public void onEndRefreshing()
    {
        mHeaderChrysanthemumIv.setVisibility(View.INVISIBLE);
        mHeaderChrysanthemumAd.stop();
        mHeaderArrowIv.setVisibility(View.VISIBLE);
        mDownAnim.setDuration(0);
        mHeaderArrowIv.startAnimation(mDownAnim);
    }

}
