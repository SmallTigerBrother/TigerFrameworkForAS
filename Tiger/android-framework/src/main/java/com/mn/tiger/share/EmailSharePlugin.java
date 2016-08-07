package com.mn.tiger.share;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;

/**
 * Created by Dalang on 2016/8/7.
 */
public class EmailSharePlugin extends TGSharePlugin<EmailSharePlugin.EmailShareMessage,TGShareResult>
{
    public EmailSharePlugin(Context context, String appID)
    {
        super(context, appID);
    }

    @Override
    protected void registerApp()
    {
        //无需注册
    }

    @Override
    protected void sendShareMsg(Activity activity, EmailShareMessage shareMsg)
    {
        Intent intent = new Intent(Intent.ACTION_SENDTO);
        intent.setData(Uri.parse("mailto:"));
        intent.putExtra(Intent.EXTRA_SUBJECT, shareMsg.title);
        intent.putExtra(Intent.EXTRA_TEXT, shareMsg.content);

        try
        {
            activity.startActivity(intent);
            handleShareResult(new EmailShareResult(true));
        }
        catch (android.content.ActivityNotFoundException e)
        {
            e.printStackTrace();
            handleShareResult(new EmailShareResult(false));
        }
    }

    private class EmailShareResult extends TGShareResult
    {
        private boolean success;
        public EmailShareResult(boolean success)
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
    }

    public static class EmailShareMessage
    {
        String title;

        String content;
    }

    public static class EmailShareMessageBuilder extends TGShareMsgBuilder<EmailShareMessage>
    {
        private EmailShareMessage msg;
        public EmailShareMessageBuilder(int shareType)
        {
            super(shareType);
            msg = new EmailShareMessage();
        }

        public void setTitle(String title)
        {
            msg.title = title;
        }

        public void setContent(String content)
        {
            msg.content = content;
        }

        @Override
        public EmailShareMessage build()
        {
            return msg;
        }
    }
}


