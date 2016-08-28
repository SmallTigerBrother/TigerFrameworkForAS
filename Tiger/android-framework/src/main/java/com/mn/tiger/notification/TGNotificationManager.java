package com.mn.tiger.notification;

import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;

import com.mn.tiger.log.Logger;

/**
 * 通知管理类
 */
public abstract class TGNotificationManager
{
    private static final Logger LOG = Logger.getLogger(TGNotificationManager.class);

    protected TGNotificationManager()
    {

    }

    /**
     * 显示通知
     * @param builder 通知信息建造者
     */
    public void showNotification(Context context, TGNotificationBuilder builder)
    {
        LOG.d("[Method:showNotification] type == " + builder.getNotificationType());
        NotificationManager notificationManager = (NotificationManager) context
                .getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.notify(builder.getId(), builder.build());
    }

    /**
     * 显示通知
     * @param builder 通知信息建造者
     */
    public void showCancelNotification(Context context, TGNotificationBuilder builder)
    {
        LOG.d("[Method:showCancelNotification] type == " + builder.getNotificationType());
        NotificationManager notificationManager = (NotificationManager) context
                .getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.notify(builder.getId(), builder.build());
        notificationManager.cancel(builder.getId());
    }

    /**
     * 接收通知
     */
    public void receiveNotification(Intent intent)
    {
        int notificationType = intent.getIntExtra(TGNotificationBuilder.NOTIFICATION_TYPE, 0);
        LOG.d("[Method:receiveNotification] notificationType == " + notificationType);
        onReceiveNotification(intent,notificationType);
    }

    /**
     * 处理通知点击事件
     *
     * @param intent           通知携带的intent
     * @param notificationType 通知的类型（用于区分不同的通知）
     */
    protected abstract void onReceiveNotification(Intent intent, int notificationType);

}
