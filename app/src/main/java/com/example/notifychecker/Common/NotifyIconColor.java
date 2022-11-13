package com.example.notifychecker.Common;

import android.graphics.Color;

// 通知アイコンの色クラス
public class NotifyIconColor
{
    // 色取得
    public static int GetColor(int type)
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
