package com.mn.tiger.thirdparty.facebook;

import com.facebook.share.Sharer;
import com.mn.tiger.share.TGShareResult;

/**
 * Created by peng on 15/10/31.
 */
public class FacebookShareResult extends TGShareResult
{
    private String postId;

    private boolean success;

    private boolean canceled;

    private String errorMsg;

    public FacebookShareResult()
    {
    }

    public void setPostId(String postId)
    {
        this.postId = postId;
    }

    public String getPostId()
    {
        return postId;
    }

    public void setSuccess(boolean success)
    {
        this.success = success;
    }

    @Override
    public boolean isSuccess()
    {
        return success;
    }

    @Override
    public boolean isCanceled()
    {
        return false;
    }

    public void setCanceled(boolean canceled)
    {
        this.canceled = canceled;
    }

    public String getErrorMsg()
    {
        return errorMsg;
    }

    public void setErrorMsg(String errorMsg)
    {
        this.errorMsg = errorMsg;
    }
}
