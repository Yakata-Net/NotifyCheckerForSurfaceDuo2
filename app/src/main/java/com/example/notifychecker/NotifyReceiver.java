package com.example.notifychecker;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.provider.Settings;
import android.util.Log;

import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.core.app.Person;
import androidx.core.content.LocusIdCompat;
import androidx.core.content.pm.ShortcutInfoCompat;
import androidx.core.content.pm.ShortcutManagerCompat;
import androidx.core.graphics.drawable.IconCompat;

import com.example.notifychecker.Common.CommonData;
import com.example.notifychecker.Common.DemiStaticSetting;
import com.example.notifychecker.Common.NotifyIconColor;
import com.example.notifychecker.Common.NotifyPerson;

import java.sql.Timestamp;

public class NotifyReceiver extends BroadcastReceiver
{
    private final String LogTag      = "NotifyReceiver:NotifyReceiver";
    private NotifyPerson DisplayNotifyPerson;

    @Override
    // 通知数監視サービスから通知の変化を受信したときの処理
    public void onReceive(Context context, Intent intent)
    {
        Log.d(LogTag, "---- Notification Broadcast Received ----");

        // チャンネル設定
        setChannel(context);

        // テスト・訓練用
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q && intent.getAction() == "NOTIFY_TEST_INTENT")
        {
            testNotify(context);
            return;
        }

        // 共通・インテント設定など
        PendingIntent pi = PendingIntent.getActivity(context, 0,
                                new Intent("android.settings.ACTION_NOTIFICATION_LISTENER_SETTINGS"),
                                PendingIntent.FLAG_MUTABLE);
        ShortcutInfoCompat shortc = new ShortcutInfoCompat.Builder(context, "SHORTCUT_NOUSE")
                                .setLongLived(true)
                                .setShortLabel("アンドン")
                                .setIntent(new Intent(Settings.ACTION_SETTINGS))
                                .build();

        ShortcutManagerCompat.pushDynamicShortcut(context, shortc);


        // 色種別取得・アイコンなどを設定
        DisplayNotifyPerson = new NotifyPerson(context);
        Person per;
        int notifyCount = intent.getIntExtra("notify_count",-1);
        int setColor;
        Log.d(LogTag, "---- notifyCount= " + notifyCount);

        NotificationManagerCompat notmCompatDel = NotificationManagerCompat.from(context);

        boolean isError = false;

        // 0件となった場合は通知を行わない(すでに行われている通知はキャンセル済)
        if(notifyCount == 0)
        {
            notmCompatDel.cancel(CommonData.Constant.NOTIFY_ID_MAIN);  // いったんキャンセルする
            return;
        }
        // エラー
        else if(notifyCount < 0)
        {
            per = DisplayNotifyPerson.getPerson(CommonData.Constant.COLOR_ORANGE);
            setColor = NotifyIconColor.GetColor(CommonData.Constant.COLOR_ORANGE);
            isError  = true;
        }
        else if(notifyCount < DemiStaticSetting.Threashold.THREASHOLD_YELLOW)
        {
            per = DisplayNotifyPerson.getPerson(CommonData.Constant.COLOR_BLUE);
            setColor = NotifyIconColor.GetColor(CommonData.Constant.COLOR_BLUE);
        }
        else if(notifyCount < DemiStaticSetting.Threashold.THREASHOLD_RED)
        {
            per = DisplayNotifyPerson.getPerson(CommonData.Constant.COLOR_YELLOW);
            setColor = NotifyIconColor.GetColor(CommonData.Constant.COLOR_YELLOW);
        }
        else
        {
            per = DisplayNotifyPerson.getPerson(CommonData.Constant.COLOR_RED);
            setColor = NotifyIconColor.GetColor(CommonData.Constant.COLOR_RED);
        }

        if(!isError)
        {
            notmCompatDel.cancel(CommonData.Constant.NOTIFY_ID_MAIN);  // エラー以外の場合はいったんキャンセルする
        }

        NotificationCompat.Builder bld = new NotificationCompat.Builder(context, CommonData.Constant.CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_andon)
                .setCategory(Notification.CATEGORY_MESSAGE)
                .setShortcutInfo(shortc)
                .setLocusId(new LocusIdCompat("PERSON"))
                .addPerson(per)
                .setColor(setColor)
                .setContentIntent(pi)
                .setPriority(NotificationCompat.PRIORITY_HIGH);

        NotificationManagerCompat notmCompat = NotificationManagerCompat.from(context);
        NotificationCompat.MessagingStyle style = new NotificationCompat.MessagingStyle(per).setConversationTitle("情報展開");

        if(isError)
        {
            style.addMessage("サービスが正常に動作していません(タップして通知アクセスの設定を有効化してください)" ,
                    new Timestamp(0).getTime(), per).setBuilder(bld);
            bld.setOngoing(true);  // エラーの場合は消せないようにする
        }
        else
        {
            style.addMessage("未処理の通知が" + notifyCount + "件あります(確認後は消去可能です)" ,
                    new Timestamp(0).getTime(), per).setBuilder(bld);
        }
        notmCompat.notify(CommonData.Constant.NOTIFY_ID_MAIN, bld.build());
    }

    // 通知のチャンネル登録
    private void setChannel(Context context)
    {
        NotificationChannel Channel = new NotificationChannel(CommonData.Constant.CHANNEL_ID,
                                                        "NotifyChecker",
                                                                NotificationManager.IMPORTANCE_HIGH);
        Channel.setSound(null, null);  // 無音
        Channel.setDescription("アンドン");

        NotificationManager notm = context.getSystemService(NotificationManager.class);
        notm.createNotificationChannel(Channel);
    }

    // 通知のテスト用
    private void testNotify(Context context)
    {
        NotificationCompat.Builder bld = new NotificationCompat.Builder(context, CommonData.Constant.CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_andon)
                .setContentTitle("テスト用通知")
                .setContentText("NotifyCheckerから送信されたテスト用通知です")
                .setPriority(NotificationCompat.PRIORITY_DEFAULT);

        NotificationManagerCompat notmCompat = NotificationManagerCompat.from(context);
        notmCompat.notify(CommonData.Constant.NOTIFY_ID_TEST, bld.build());
    }
}
