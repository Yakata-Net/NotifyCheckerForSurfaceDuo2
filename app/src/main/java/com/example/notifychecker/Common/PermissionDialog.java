package com.example.notifychecker.Common;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.DialogFragment;

public class PermissionDialog extends DialogFragment
{
    @Override
    // 権限要求ダイアログ作成
    public Dialog onCreateDialog(Bundle State)
    {
        Dialog aBld = new AlertDialog.Builder(getActivity())
                .setTitle("通知アクセス権限の要求")
                .setMessage("サービスの起動のため、通知へのアクセス権限が必要です。次の画面でアクセスを許可してください。")
                .setPositiveButton("OK", (dialogInterface, id) ->
                {
                    Intent intent = new Intent("android.settings.ACTION_NOTIFICATION_LISTENER_SETTINGS");
                    startActivity(intent);
                })
                .setNegativeButton("CANCEL", (dialogInterface, id) ->{})
                // キャンセルされたときの動作はダイアログを閉じた後のMainActivityに任せる

                .create();
        return aBld;
    }

    @Override
    public void onPause()
    {
        super.onPause();
        dismiss();
    }
}
