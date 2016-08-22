package com.mn.tiger.share;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;

/**
 * Created by Dalang on 2016/8/7.
 */
public class SMSSharePlugin extends TGSharePlugin<SMSSharePlugin.SMSShareMessage, TGShareResult>
{
    public SMSSharePlugin(Context context, String appID)
    {
        super(context, appID);
    }

    @Override
    protected void registerApp()
    {
        //无需注册
    }

    @Override
    protected void sendShareMsg(Activity activity, SMSShareMessage shareMsg)
    {
        Uri smsToUri = Uri.parse("smsto:");
        Intent intent = new Intent(android.content.Intent.ACTION_SENDTO, smsToUri);
        intent.putExtra("sms_body", shareMsg.content);
        try
        {
            activity.startActivity(intent);
            handleShareResult(new SMSShareResult(true));
        }
        catch (android.content.ActivityNotFoundException e)
        {
            e.printStackTrace();
            handleShareResult(new SMSShareResult(false));
        }
    }

    private class SMSShareResult extends TGShareResult
    {
        private boolean success;

        public SMSShareResult(boolean success)
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

    public static class SMSShareMessage
    {
        private SMSShareMessage()
        {}

        String content;
    }

    public static class SMSShareMessageBuilder extends TGShareMsgBuilder<SMSShareMessage>
    {
        private SMSShareMessage msg;

        public SMSShareMessageBuilder(int shareType)
        {
            super(shareType);
            msg = new SMSShareMessage();
        }

        public void setContent(String content)
        {
            msg.content = content;
        }

        @Override
        public SMSShareMessage build()
        {
            return msg;
        }
    }
}
