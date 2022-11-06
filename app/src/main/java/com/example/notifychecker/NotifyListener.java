package com.example.notifychecker;

import android.app.AlertDialog;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;
import android.service.notification.NotificationListenerService;
import android.service.notification.StatusBarNotification;
import android.util.Log;

import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.core.content.LocusIdCompat;
import androidx.fragment.app.DialogFragment;

import com.example.notifychecker.Common.CommonData;
import com.example.notifychecker.Common.PermissionDialog;

import java.util.Set;

public class NotifyListener extends NotificationListenerService
{
    private final String LogTag = "NotifyReceiver:NotifyListener";

    // 初期化処理
    private void Initialize()
    {
        // チャンネル設定以外は空処理。必要に応じて追加すること
        setChannel(this);

        // サービス自体の通知
        NotificationCompat.Builder bld = new NotificationCompat.Builder(this, CommonData.Constant.CHANNEL_ID2)
                .setContentTitle("通知監視")
                .setContentText("サービスは動作中です")
                .setSmallIcon(R.drawable.ic_andon)
                .setOngoing(true)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT);

        NotificationManagerCompat notmCompat = NotificationManagerCompat.from(this);
        notmCompat.notify(CommonData.Constant.NOTIFY_ID_SERVICE, bld.build());
    }

    // チャンネル設定
    private void setChannel(Context context)
    {
        NotificationChannel Channel = new NotificationChannel(CommonData.Constant.CHANNEL_ID2,
                "NotifyListener", NotificationManager.IMPORTANCE_DEFAULT);
        Channel.setSound(null, null);  // 無音
        Channel.setDescription("通知監視");

        NotificationManager notm = context.getSystemService(NotificationManager.class);
        notm.createNotificationChannel(Channel);
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
                    "---- --> Notification Posted (SELF) ----");
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
                    "---- --> Notification Removed (SELF) ----");
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
