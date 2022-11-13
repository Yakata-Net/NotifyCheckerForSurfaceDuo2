package com.example.notifychecker.Common;

import android.content.Context;

import androidx.core.app.Person;
import androidx.core.graphics.drawable.IconCompat;

import com.example.notifychecker.R;

// 通知するアイコンクラス
public class NotifyPerson
{
    private Person PersonBlue;
    private Person PersonYellow;
    private Person PersonRed;
    private Person PersonOrange;

    public NotifyPerson(Context context)
    {
        PersonBlue = new androidx.core.app.Person.Builder()
                            .setName("青")
                            .setIcon(IconCompat.createWithResource(context, R.mipmap.ic_people_b))
                            .build();

        PersonYellow = new androidx.core.app.Person.Builder()
                            .setName("黄")
                            .setIcon(IconCompat.createWithResource(context, R.mipmap.ic_people_y))
                            .build();

        PersonRed     = new androidx.core.app.Person.Builder()
                            .setName("赤")
                            .setIcon(IconCompat.createWithResource(context, R.mipmap.ic_people_r))
                            .build();

        PersonOrange = new androidx.core.app.Person.Builder()
                            .setName("橙")
                            .setIcon(IconCompat.createWithResource(context, R.mipmap.ic_people_e))
                            .build();
    }

    public Person getPerson(int type)
    {
        // 赤
        if(type == CommonData.Constant.COLOR_RED)
        {
            return PersonRed;
        }
        // 黄
        else if(type == CommonData.Constant.COLOR_YELLOW)
        {
            return PersonYellow;
        }
        // 青
        else if(type == CommonData.Constant.COLOR_BLUE)
        {
            return PersonBlue;
        }
        // 今は、障害時その他はオレンジに倒す
        else
        {
            return PersonOrange;
        }
    }
}
