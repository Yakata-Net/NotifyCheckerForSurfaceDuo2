package com.example.notifychecker;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;
import android.service.notification.NotificationListenerService;
import android.service.notification.StatusBarNotification;
import android.util.Log;

import com.example.notifychecker.Common.CommonData;

import java.sql.Date;
import java.util.HashMap;
import java.util.Map;

public class NotifyListener extends NotificationListenerService
{
    public static Boolean IsRunnig = false;
    public static Date LatestServiceStart = new Date(0);
    private final String LogTag = "NotifyReceiver:NotifyListener";

    // 初期化処理
    private void Initialize()
    {
        // チャンネル設定以外は空処理。必要に応じて追加すること
        setChannel(this);

        IsRunnig = true;
        LatestServiceStart = new Date(System.currentTimeMillis());
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

    // インスタンス生成時の処理
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

    // 開始時の処理
    @Override
    public int onStartCommand(Intent intent, int flags, int startId)
    {
        Log.d(LogTag, "---- Listener Start Command Receive ----");

        // 自動的に再起動する
        return START_STICKY;
    }

    // 通知取得可能になったタイミング
    @Override
    public void onListenerConnected()
    {
        super.onListenerConnected();
        Log.d(LogTag, "---- Listener Connected ----");

        // 初回を想定
        notifyBroadcast();
    }

    // 通知取得時の処理
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

    // 通知削除時の処理
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

        sendNotifyBroadcast(actualNotifyCount);
    }

    // 自身の通知・会話アイコンを除いた数を取得する
    private int getActualNotifyCount(StatusBarNotification[] notifyList)
    {
        // グループ単位で分ける(Valueはダミーのため使わない)
        Map<String, Integer> groupMap = new HashMap();
        int count = 0;
        for(StatusBarNotification sbNotify : notifyList)
        {
            Log.d(LogTag, "Notify: PackageName:" + sbNotify.getPackageName() + ", Id:" + sbNotify.getId());
            if(!CommonData.Constant.IsSelfNotifyId(sbNotify.getId()))
            {
                groupMap.put(sbNotify.getGroupKey(), 1);
            }
        }
        for(Map.Entry<String, Integer> index: groupMap.entrySet())
        {
            count++;
        }
        return count;
    }

    // Broadcast通知
    private void sendNotifyBroadcast(int count)
    {
        if(count < 0)return;

        Intent broad = new Intent();
        broad.putExtra("notify_count", count);
        broad.setAction("NOTIFY_RECEIVER_INTENT");
        getBaseContext().sendBroadcast(broad);
    }

    private void sendNotifyErrorBroadcast()
    {
        Intent broad = new Intent();
        broad.putExtra("notify_count", -1);
        broad.setAction("NOTIFY_RECEIVER_INTENT");
        getBaseContext().sendBroadcast(broad);
    }

    @Override
    public void onDestroy()
    {
        IsRunnig = false;
        sendNotifyErrorBroadcast();
    }
}
