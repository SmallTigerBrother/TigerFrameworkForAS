package com.mn.tiger.thirdparty.facebook;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;

import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.share.Sharer;
import com.facebook.share.model.ShareContent;
import com.facebook.share.model.ShareLinkContent;
import com.facebook.share.model.SharePhoto;
import com.facebook.share.model.SharePhotoContent;
import com.facebook.share.model.ShareVideo;
import com.facebook.share.model.ShareVideoContent;
import com.facebook.share.widget.ShareDialog;
import com.mn.tiger.share.TGSharePlugin;

import java.util.ArrayList;
import java.util.List;

/**
 * Facebook分享插件
 * Created by peng on 15/10/31.
 */
public class FacebookSharePlugin extends TGSharePlugin<ShareContent, FacebookShareResult>
{
    private static final String INDICATOR = "indicator";

    private CallbackManager callbackManager;

    public FacebookSharePlugin(Context context, String appID)
    {
        super(context, appID);
    }

    @Override
    protected void registerApp()
    {
        //TODO
        callbackManager = CallbackManager.Factory.create();
    }

    @Override
    protected void sendShareMsg(Activity activity, ShareContent shareMsg)
    {
        ShareDialog shareDialog = new ShareDialog(activity);
        shareDialog.registerCallback(callbackManager, new FacebookCallback<Sharer.Result>()
        {
            @Override
            public void onSuccess(Sharer.Result result)
            {
                FacebookShareResult shareResult = new FacebookShareResult();
                shareResult.setPostId(result.getPostId());
                shareResult.setSuccess(true);
                FacebookSharePlugin.this.handleShareResult(shareResult);
            }

            @Override
            public void onCancel()
            {
                FacebookShareResult shareResult = new FacebookShareResult();
                shareResult.setSuccess(false);
                shareResult.setErrorMsg("canceled");
                FacebookSharePlugin.this.handleShareResult(shareResult);
            }

            @Override
            public void onError(FacebookException error)
            {
                FacebookShareResult shareResult = new FacebookShareResult();
                shareResult.setSuccess(false);
                shareResult.setErrorMsg(error.getMessage());
                FacebookSharePlugin.this.handleShareResult(shareResult);
            }
        });

        shareDialog.show(shareMsg);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        if (null != callbackManager)
        {
            callbackManager.onActivityResult(requestCode, resultCode, data);
        }
    }

    public static class FacebookShareLinkMsgBuilder extends TGShareMsgBuilder<ShareLinkContent>
    {
        private ShareLinkContent.Builder builder;

        public FacebookShareLinkMsgBuilder(int shareType)
        {
            super(shareType);
            builder = new ShareLinkContent.Builder();
        }

        public void setContentTitle(String contentTitle)
        {
            builder.setContentTitle(contentTitle);
        }

        public void setContentDescription(String contentDescription)
        {
            builder.setContentDescription(contentDescription);
        }

        public void setContentUrl(String contentUrl)
        {
            builder.setContentUrl(Uri.parse(contentUrl));
        }

        public void setImageUrl(String imageUrl)
        {
            builder.setImageUrl(Uri.parse(imageUrl));
        }

        @Override
        public ShareLinkContent build()
        {
            return builder.build();
        }
    }

    public static class FacebookShareBitmapMsgBuilder extends TGShareMsgBuilder<ShareContent>
    {
        private SharePhotoContent.Builder photoContentBuilder;

        private SharePhoto.Builder photoBuilder;

        public FacebookShareBitmapMsgBuilder(int shareType)
        {
            super(shareType);
            photoContentBuilder = new SharePhotoContent.Builder();
            photoBuilder = new SharePhoto.Builder();
        }

        public void setBitmap(Bitmap bitmap)
        {
            photoBuilder.setBitmap(bitmap);
        }

        public void setImageUrl(String imageUrl)
        {
            photoBuilder.setImageUrl(Uri.parse(imageUrl));
        }

        @Override
        public ShareContent build()
        {
            return photoContentBuilder.addPhoto(photoBuilder.build()).build();
        }
    }

    public static class FacebookShareBitmapListMsgBuilder extends TGShareMsgBuilder<ShareContent>
    {
        private SharePhotoContent.Builder photoContentBuilder;

        private List<SharePhoto> photos;

        public FacebookShareBitmapListMsgBuilder(int shareType)
        {
            super(shareType);
            photoContentBuilder = new SharePhotoContent.Builder();
            photos = new ArrayList<SharePhoto>();
        }

        public void addBitmap(Bitmap bitmap)
        {
            SharePhoto.Builder photoBuilder = new SharePhoto.Builder();
            photoBuilder.setBitmap(bitmap);
            photos.add(photoBuilder.build());
        }

        @Override
        public ShareContent build()
        {

            return photoContentBuilder.setPhotos(photos).build();
        }
    }

    public static class FacebookShareVideoMsgBuilder extends TGShareMsgBuilder<ShareContent>
    {
        private ShareVideoContent.Builder videoContentBuilder;

        private Bitmap previewPhoto;

        private ShareVideo.Builder videoBuilder;

        public FacebookShareVideoMsgBuilder(int shareType)
        {
            super(shareType);
            videoContentBuilder = new ShareVideoContent.Builder();
        }

        public void setVideoUrl(String videoUrl)
        {
            videoBuilder.setLocalUrl(Uri.parse(videoUrl));
        }

        public void setContentDescription(String contentDescription)
        {
            videoContentBuilder.setContentDescription(contentDescription);
        }

        public void setContentTitle(String contentTitle)
        {
            videoContentBuilder.setContentTitle(contentTitle);
        }

        public void setPreviewPhoto(Bitmap previewPhoto)
        {
            SharePhoto photo = null;
            if (null != previewPhoto)
            {
                photo = new SharePhoto.Builder()
                        .setBitmap(previewPhoto)
                        .build();
                videoContentBuilder.setPreviewPhoto(photo);
            }
        }

        @Override
        public ShareContent build()
        {
            return videoContentBuilder.build();
        }
    }
}
