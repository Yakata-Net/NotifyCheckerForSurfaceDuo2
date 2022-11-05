package com.example.notifychecker.Common;

import android.service.notification.StatusBarNotification;

import androidx.core.graphics.drawable.IconCompat;

import com.example.notifychecker.R;

public class CommonData {
    public static class Constant
    {
        public static final String CHANNEL_ID    = "NOTIFY_CHECKER_ANDON";
        public static final int NOTIFY_ID_RED    = 9600000;
        public static final int NOTIFY_ID_YELLOW = 9600001;
        public static final int NOTIFY_ID_BLUE   = 9600002;
        public static final int NOTIFY_ID_OTHER  = 9699999;

        public static final int COLOR_RED    = 0;
        public static final int COLOR_YELLOW = 1;
        public static final int COLOR_BLUE   = 2;
        public static final int COLOR_ORANGE = -1;

        // 通知が自分自身のものかを確認
        public static boolean IsSelfNotifyId(int id)
        {
            return (id == NOTIFY_ID_RED    ||
                    id == NOTIFY_ID_YELLOW ||
                    id == NOTIFY_ID_BLUE   ||
                    id == NOTIFY_ID_OTHER);
        }
        // 通知が会話かどうか確認(未実装)
        public static boolean IsConversation(StatusBarNotification notify)
        {
            //if(notify)
            return true;
        }

    }
}
