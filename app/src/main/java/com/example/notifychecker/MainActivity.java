package com.example.notifychecker;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.fragment.app.DialogFragment;

import android.app.AlertDialog;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;

import com.example.notifychecker.Common.PermissionDialog;

import java.util.Set;
import java.util.concurrent.Executor;

public class MainActivity extends AppCompatActivity {
    IntentFilter ifilter;
    NotifyReceiver receiver;
    private final String LogTag = "MainActivity";

    // 初期設定処理
    private  boolean Initialize()
    {
        Log.d(LogTag, "---- 権限があるパッケージ一覧 ----");

        // 通知アクセス宣言があるアプリを探し、無ければ権限を要求
        Set<String> s = NotificationManagerCompat.getEnabledListenerPackages(this);
        boolean found = false;
        for(String ss : s)
        {
            Log.d(LogTag, "・" + ss);
            if(ss.equals(getPackageName())) { found = true; }
        }

        if(!found)
        {
            Log.d(LogTag, "---- 権限がないため、設定 ----");
            DialogFragment permDialog = new PermissionDialog();
            permDialog.show(getSupportFragmentManager(), "DIALOG_PERMISSION");
        }

        // サービスからのブロードキャストレシーバーを設定
        setContentView(R.layout.activity_main);

        receiver = new NotifyReceiver();
        ifilter  = new IntentFilter();
        ifilter.addAction("NOTIFY_RECEIVER_INTENT");
        registerReceiver(receiver, ifilter);
        return true;  // 今のところはTrueのみ。エラーハンドリング実装時に変える。
    }

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        Initialize();
    }
}