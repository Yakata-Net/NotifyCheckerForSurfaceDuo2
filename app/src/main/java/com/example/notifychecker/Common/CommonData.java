package com.example.notifychecker.Common;

import android.service.notification.StatusBarNotification;

public class CommonData {
    public static class Constant
    {
        public static final String CHANNEL_ID     = "NOTIFY_CHECKER_ANDON";
        public static final String CHANNEL_ID2    = "NOTIFY_CHECKER_SERVICE";
        public static final int NOTIFY_ID_MAIN    = 9600000;
        public static final int NOTIFY_ID_SERVICE = 9610000;

        public static final int COLOR_RED    = 0;
        public static final int COLOR_YELLOW = 1;
        public static final int COLOR_BLUE   = 2;
        public static final int COLOR_ORANGE = -1;

        // 通知が自分自身のものかを確認
        public static boolean IsSelfNotifyId(int id)
        {
            return (id == NOTIFY_ID_MAIN ||
                    id == NOTIFY_ID_SERVICE);
        }
        // 通知が会話かどうか確認(未実装)
        public static boolean IsConversation(StatusBarNotification notify)
        {
            //if(notify)
            return true;
        }

    }
}
