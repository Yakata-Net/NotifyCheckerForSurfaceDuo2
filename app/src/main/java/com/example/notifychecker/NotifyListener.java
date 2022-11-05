package com.example.notifychecker;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.IBinder;
import android.service.notification.NotificationListenerService;
import android.service.notification.StatusBarNotification;
import android.util.Log;

import androidx.core.app.NotificationManagerCompat;

import com.example.notifychecker.Common.CommonData;

import java.util.Set;

public class NotifyListener extends NotificationListenerService
{
    private final String LogTag = "NotifyReceiver:NotifyListener";

    // 初期化処理
    private void Initialize()
    {
        // 空処理。必要に応じて追加すること
    }

    @Override
    public void onCreate()
    {
        super.onCreate();

        Log.d(LogTag, "---- Service Created ----");
        Initialize();

    }
    @Override
    public IBinder onBind(Intent intent)
    {
        IBinder binder = super.onBind(intent);
        Log.d(LogTag, "---- Service Binded ----");
        return binder;
    }

    @Override
    public void onListenerConnected()
    {
        super.onListenerConnected();
        Log.d(LogTag, "---- Listener Connected ----");

        // 初回を想定
        notifyBroadcast();
    }

    @Override
    public void onNotificationPosted(StatusBarNotification notify)
    {
        super.onNotificationPosted(notify);
        if (CommonData.Constant.IsSelfNotifyId(notify.getId()))
        {
            Log.d(LogTag,
                    "---- --> Notification Posted (自身の通知のため無視) ----");
            return;
        }
        Log.d(LogTag,
                "---- --> Notification Posted (Key:" + notify.getKey() + ", Id:" + notify.getId() + ") ----");

        notifyBroadcast();
    }

    @Override
    public void onNotificationRemoved(StatusBarNotification notify)
    {
        super.onNotificationRemoved(notify);
        if (CommonData.Constant.IsSelfNotifyId(notify.getId())) {
            Log.d(LogTag,
                    "---- --> Notification Removed (自身の通知のため無視) ----");
            return;
        }
        Log.d(LogTag,
                "---- --> Notification Removed (Key:" + notify.getKey() + ", Id:" + notify.getId() + ") ----");

        notifyBroadcast();
    }

    private void notifyBroadcast()
    {
        StatusBarNotification[] statusBarNotificationList = getActiveNotifications();
        // 自身の通知・会話アイコンを除く
        int actualNotifyCount = getActualNotifyCount(statusBarNotificationList);

        Log.d(LogTag,
                "----       Now Notify Count = ("+ actualNotifyCount + "/" + statusBarNotificationList.length + ") ----");

        Intent broad = new Intent();
        broad.putExtra("notify_count", actualNotifyCount);
        broad.setAction("NOTIFY_RECEIVER_INTENT");
        getBaseContext().sendBroadcast(broad);
    }

    // 自身の通知・会話アイコンを除いた数を取得する
    private int getActualNotifyCount(StatusBarNotification[] notifyList)
    {
        int count = 0;
        for(StatusBarNotification sbNotify : notifyList)
        {
            if(!CommonData.Constant.IsSelfNotifyId(sbNotify.getId()))
            {
                count++;
            }
        }
        return count;
    }
}
