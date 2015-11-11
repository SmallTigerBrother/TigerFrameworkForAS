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
     *
     * @param context
     * @param type    通知类型
     * @param builder 通知信息建造者
     */
    public void showNotification(Context context, int type, TGNotificationBuilder builder)
    {
        LOG.d("[Method:showNotification] type == " + type);
        NotificationManager notificationManager = (NotificationManager) context
                .getSystemService(Context.NOTIFICATION_SERVICE);
        builder.setNotificationType(type);
        notificationManager.notify(type, builder.build());
    }

    /**
     * 显示通知
     *
     * @param context
     * @param type    通知类型（用户区分不同的通知）
     * @param builder 通知信息建造者
     */
    public void showCancelNotification(Context context, int type, TGNotificationBuilder builder)
    {
        LOG.d("[Method:showCancelNotification] type == " + type);
        NotificationManager notificationManager = (NotificationManager) context
                .getSystemService(Context.NOTIFICATION_SERVICE);
        builder.setNotificationType(type);
        notificationManager.notify(type, builder.build());
        notificationManager.cancel(type);
    }

    /**
     * 接收通知
     * @param intent
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
