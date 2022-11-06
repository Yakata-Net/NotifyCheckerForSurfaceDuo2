package com.example.notifychecker;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.provider.Settings;
import android.util.Log;

import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.core.content.LocusIdCompat;
import androidx.core.content.pm.ShortcutInfoCompat;
import androidx.core.content.pm.ShortcutManagerCompat;
import androidx.core.graphics.drawable.IconCompat;

import com.example.notifychecker.Common.CommonData;
import com.example.notifychecker.Common.DemiStaticSetting;

import java.sql.Timestamp;

public class NotifyReceiver extends BroadcastReceiver
{
    private final String LogTag      = "NotifyReceiver:NotifyReceiver";

    @Override
    // 通知数監視サービスから通知の変化を受信したときの処理
    public void onReceive(Context context, Intent intent)
    {
        Log.d(LogTag, "---- レシーバーの通知を受信 ----");
        // チャンネル設定
        setChannel(context);

        // 色種別取得・アイコンなどを設定
        int notifyCount = intent.getIntExtra("notify_count",-1);
        int setColor;
        int type;
        Log.d(LogTag, "---- notifyCount= " + notifyCount);

        NotificationManagerCompat notmCompatDel = NotificationManagerCompat.from(context);
        notmCompatDel.cancel(CommonData.Constant.NOTIFY_ID_MAIN);  // いったんキャンセルする

        boolean isError = false;

        // 0件となった場合は通知を行わない(すでに行われている通知はキャンセル済)
        if(notifyCount == 0) { return; }
        // エラー
        else if(notifyCount < 0)
        {
            type     = CommonData.Constant.COLOR_ORANGE;
            setColor = getColor(CommonData.Constant.COLOR_ORANGE);
            isError  = true;
        }
        else if(notifyCount < DemiStaticSetting.Threashold.THREASHOLD_YELLOW)
        {
            type     = CommonData.Constant.COLOR_BLUE;
            setColor = getColor(CommonData.Constant.COLOR_BLUE);
        }
        else if(notifyCount < DemiStaticSetting.Threashold.THREASHOLD_RED)
        {
            type     = CommonData.Constant.COLOR_YELLOW;
            setColor = getColor(CommonData.Constant.COLOR_YELLOW);
        }
        else
        {
            type     = CommonData.Constant.COLOR_RED;
            setColor = getColor(CommonData.Constant.COLOR_RED);
        }

        androidx.core.app.Person per = createPersonPerColor(context, type);

        ShortcutInfoCompat shortc = new ShortcutInfoCompat.Builder(context, "SHORTCUT_NOUSE")
                .setLongLived(true)
                .setShortLabel("アンドン")
                .setIntent(new Intent(Settings.ACTION_SETTINGS))
                .build();

        ShortcutManagerCompat.pushDynamicShortcut(context, shortc);

        PendingIntent pi = PendingIntent.getActivity(context, 0,
                                                     new Intent("android.settings.ACTION_NOTIFICATION_LISTENER_SETTINGS"),
                                                     PendingIntent.FLAG_MUTABLE);

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
            style.addMessage("サービスが正常に動作していません(タップして通知アクセスの設定を有効かしてください)" ,
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

    // 表示する色に応じたPersonクラス作成
    private androidx.core.app.Person createPersonPerColor(Context context, int type)
    {
        androidx.core.app.Person per;
        // 赤
        if(type == CommonData.Constant.COLOR_RED)
        {
            IconCompat ic = IconCompat.createWithResource(context, R.mipmap.ic_people_r);
            per = new androidx.core.app.Person.Builder()
                    .setName("赤")
                    .setIcon(ic)
                    .build();
        }
        // 黄
        else if(type == CommonData.Constant.COLOR_YELLOW)
        {
            IconCompat ic = IconCompat.createWithResource(context, R.mipmap.ic_people_y);
            per = new androidx.core.app.Person.Builder()
                    .setName("黄")
                    .setIcon(ic)
                    .build();
        }
        // 青
        else if(type == CommonData.Constant.COLOR_BLUE)
        {
            IconCompat ic = IconCompat.createWithResource(context, R.mipmap.ic_people_b);
            per = new androidx.core.app.Person.Builder()
                    .setName("青")
                    .setIcon(ic)
                    .build();
        }
        // 今は、障害時その他はオレンジに倒す
        else
        {
            IconCompat ic = IconCompat.createWithResource(context, R.mipmap.ic_people_e);
            per = new androidx.core.app.Person.Builder()
                    .setName("橙")
                    .setIcon(ic)
                    .build();
        }
        return per;
    }

    // 色取得
    private int getColor(int type)
    {
        int col;
        // 赤
        if(type == CommonData.Constant.COLOR_RED) { col = Color.RED; }
        // 黄
        else if(type == CommonData.Constant.COLOR_YELLOW) { col = Color.YELLOW; }
        // 青
        else if(type == CommonData.Constant.COLOR_BLUE) { col = Color.CYAN; }
        // 今は、障害時その他はオレンジに倒す
        else { col = Color.argb(192, 255, 144, 0); }
        return col;
    }
}
