package com.example.shimataro.eho;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import java.util.Calendar;

/**
 * アラーム受信
 * Created by shimataro on 16/04/13.
 */
public class AlarmReceiver extends BroadcastReceiver {

    /** 通知ID: 節分 */
    public final int NID_SETSUBUN = 1;

    @Override
    public void onReceive(Context context, Intent intent) {
        Log.d("---- AlarmReceiver ----", "onReceive");

        final int requestCode = intent.getIntExtra("requestCode", -1);

        // 通知がクリックされた時に発行されるIntentの生成
        Intent sendIntent = new Intent(context, MainActivity.class);
        PendingIntent sender = PendingIntent.getActivity(context, requestCode, sendIntent, 0);
        if (sender == null) {
            return;
        }

        Calendar calendar = Calendar.getInstance();
        switch (requestCode) {
            case Scheduler.REQCODE_DAILY:
                _handleDaily(calendar, context, sender);
                break;
        }
    }

    /**
     * デイリーハンドラ
     * @param calendar カレンダー
     * @param context コンテキスト
     * @param sender 送信元
     */
    private void _handleDaily(Calendar calendar, Context context, PendingIntent sender) {
        _handleDaily_Setsubun(calendar, context, sender);
    }

    /**
     * デイリーハンドラ: 節分
     * @param calendar カレンダー
     * @param context コンテキスト
     * @param sender 送信元
     */
    private void _handleDaily_Setsubun(Calendar calendar, Context context, PendingIntent sender) {
        final int month = 2, day = 3;
        if (calendar.get(Calendar.MONTH) != month - 1) {
            return;
        }
        if (calendar.get(Calendar.DATE) != day) {
            return;
        }

        // 通知オブジェクトの生成
        Notification notification = new NotificationCompat.Builder(context)
                .setTicker      (context.getText(R.string.notify_ticker_setsubun))
                .setContentTitle(context.getText(R.string.notify_content_title_setsubun))
                .setContentText (context.getText(R.string.notify_content_text_setsubun))
                .setSmallIcon(R.drawable.ehomaki)
                .setVibrate(new long[]{0, 200})
                .setAutoCancel(true)
                .setContentIntent(sender)
                .build();

        NotificationManager manager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        manager.notify(NID_SETSUBUN, notification);
    }
}
