package com.example.notifychecker;

import static java.lang.Thread.sleep;

import android.content.Context;
import android.content.Intent;

import com.example.notifychecker.Common.DemiStaticSetting;

public class NotifyTest implements Runnable
{
    private Context context;

    public NotifyTest(Context context)
    {
        this.context = context;
    }

    @Override
    public void run()
    {
        try
        {
            sleep(DemiStaticSetting.Threashold.TEST_NOTIFY_TIMER_INTERVAL);

            Intent broad = new Intent();
            broad.setAction("NOTIFY_TEST_INTENT");
            context.sendBroadcast(broad);

        } catch (InterruptedException e)
        {
            e.printStackTrace();
        }
    }
}
