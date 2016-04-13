package com.example.shimataro.eho;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;

import java.util.Calendar;

/**
 * スケジュールを設定
 * Created by shimataro on 16/04/13.
 */
public class Scheduler {

    /**
     * 毎日起動するアラームを設定
     * @param context コンテキスト
     * @param cls ターゲットクラス
     * @param hour 起動時間
     * @param minute 起動分
     * @param second 起動秒
     */
    public static void setScheduleDaily(Context context, Class<?> cls, final int hour, final int minute, final int second) {
        Calendar calendarAlarm = Calendar.getInstance();
        calendarAlarm.set(Calendar.HOUR_OF_DAY, hour);
        calendarAlarm.set(Calendar.MINUTE, minute);
        calendarAlarm.set(Calendar.SECOND, second);

        _setSchedule(context, cls, Notifier.REQCODE_DAILY, calendarAlarm.getTimeInMillis(), AlarmManager.INTERVAL_DAY, PendingIntent.FLAG_CANCEL_CURRENT);
    }

    /**
     * スケジュール設定
     * @param context コンテキスト
     * @param cls ターゲットクラス
     * @param requestCode リクエストコード
     * @param timeMillis 起動時間 [millisec]
     * @param interval 起動間隔（一度だけの場合は0）
     * @param flags インテントのフラグ
     * @return OK/NG
     */
    private static boolean _setSchedule(Context context, Class<?> cls, final int requestCode, final long timeMillis, final long interval, final int flags) {
        // リクエストコードを保存しておく
        Intent intent = new Intent(context, cls);
        intent.putExtra("requestCode", requestCode);

        PendingIntent sender = PendingIntent.getBroadcast(context, requestCode, intent, flags);

        // アラームを設定
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        if (alarmManager == null) {
            return false;
        }

        if (interval == 0) {
            // 一度だけ
            alarmManager.set(AlarmManager.RTC_WAKEUP, timeMillis, sender);
        } else {
            // 定期的
            alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, timeMillis, interval, sender);
        }
        return true;
    }
}
