package com.example.notifychecker;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.fragment.app.DialogFragment;

import android.app.Activity;
import android.app.ActivityManager;
import android.app.AlertDialog;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.util.Log;
import android.view.TextureView;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.notifychecker.Common.CommonData;
import com.example.notifychecker.Common.DemiStaticSetting;
import com.example.notifychecker.Common.PermissionDialog;

import java.text.SimpleDateFormat;
import java.util.Set;
import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class MainActivity extends AppCompatActivity {
    IntentFilter ifilter;
    NotifyReceiver receiver;
    private final String LogTag = "MainActivity";
    ExecutorService exe = Executors.newSingleThreadExecutor();

    // 初期設定処理
    private  boolean Initialize()
    {
        boolean ret = true;
        Log.d(LogTag, "---- Package of Permitted ----");

        // 通知アクセス権限が必須のため、無ければ権限を要求
        if(!isPermittedNotifyAccess())
        {
            Log.d(LogTag, "---- No Permission ----");
            DialogFragment permDialog = new PermissionDialog();
            permDialog.show(getSupportFragmentManager(), "DIALOG_PERMISSION");

            ret = false;
        }

        setBroadcastReceiver();

        setContentView(R.layout.activity_main);
        setStatusText();

        return ret;
    }

    private boolean isPermittedNotifyAccess()
    {
        Set<String> s = NotificationManagerCompat.getEnabledListenerPackages(this);
        boolean found = false;
        for(String ss : s)
        {
            // ログ出しのため、見つかった場合も飛ばさずに最後まで回す
            Log.d(LogTag, "・" + ss);
            if(ss.equals(getPackageName())) { found = true; }
        }
        return found;
    }

    protected void setBroadcastReceiver()
    {
        receiver = new NotifyReceiver();
        ifilter  = new IntentFilter();
        ifilter.addAction("NOTIFY_RECEIVER_INTENT");
        ifilter.addAction("NOTIFY_TEST_INTENT");
        registerReceiver(receiver, ifilter);
    }

    private void setStatusText()
    {
        TextView statusText = findViewById(R.id.Service_Status);
        if(statusText != null)
        {
            statusText.setText((NotifyListener.IsRunnig) ?  R.string.text_view_ServiceStatus_On :
                    R.string.text_view_ServiceStatus_Off);
        }
        TextView latestText = findViewById(R.id.service_LatestStartDate);
        if(latestText != null)
        {
            SimpleDateFormat format = new SimpleDateFormat("YYYY/MM/dd HH:mm:ss.SSS");
            latestText.setText(getText(R.string.text_view_LatestServiceDate) +
                                format.format(NotifyListener.LatestServiceStart));
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        boolean result = Initialize();

        // 失敗の場合、エラー表示
        if(!result)
        {
            Intent broad = new Intent();
            broad.putExtra("notify_count", -1);
            broad.setAction("NOTIFY_RECEIVER_INTENT");
            getBaseContext().sendBroadcast(broad);
        }
    }

    @Override
    public void onResume()
    {
        super.onResume();
        setStatusText();
    }

    // 通知設定ボタン
    public void NotifyPermSettingButtonClicked(View view)
    {
        Intent intent = new Intent("android.settings.ACTION_NOTIFICATION_LISTENER_SETTINGS");
        startActivity(intent);
    }

    // テスト通知送信
    public void NotifyTestButtonClicked(View view)
    {
        NotifyTest notifyTest = new NotifyTest(this);
        exe.submit(notifyTest);
        Toast.makeText(this,
                    DemiStaticSetting.Threashold.TEST_NOTIFY_TIMER_INTERVAL / 1000 + getString(R.string.text_timer),
                        Toast.LENGTH_SHORT).show();
    }
}