package com.mn.tiger.thirdparty.twitter;

import com.mn.tiger.share.TGShareResult;

/**
 * Created by peng on 15/10/31.
 */
public class TwitterShareResult extends TGShareResult
{
    @Override
    public boolean isSuccess()
    {
        return false;
    }

    @Override
    public boolean isCanceled()
    {
        return false;
    }
}
