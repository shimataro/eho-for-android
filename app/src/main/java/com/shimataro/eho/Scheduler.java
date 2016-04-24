package com.shimataro.eho;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import java.util.Calendar;

/**
 * スケジュールを設定
 * Created by shimataro on 16/04/13.
 */
public class Scheduler {

    // リクエストコード: 毎日
    public static final int REQCODE_DAILY = 1;

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

        _setSchedule(context, cls, REQCODE_DAILY, calendarAlarm.getTimeInMillis(), AlarmManager.INTERVAL_DAY, PendingIntent.FLAG_UPDATE_CURRENT);
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

        if (_scheduleExists(context, requestCode, intent)) {
            // スケジュール済み
            Log.i("---- Scheduler ----", "Schedule exists");
            return false;
        }

        PendingIntent sender = PendingIntent.getBroadcast(context, requestCode, intent, flags);

        // アラームを設定
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        if (alarmManager == null) {
            Log.i("---- Scheduler ----", "AlarmManager not exists");
            return false;
        }

        if (interval == 0) {
            // 一度だけ
            alarmManager.set(AlarmManager.RTC_WAKEUP, timeMillis, sender);
        } else {
            // 定期的
            alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, timeMillis, interval, sender);
        }

        Log.i("---- Scheduler ----", "Schedule set");
        return true;
    }

    /**
     * 指定のスケジュールが存在しているか？
     * @param context コンテキスト
     * @param requestCode リクエストコード
     * @param intent インテント
     * @return Yes/No
     */
    static private boolean _scheduleExists(Context context, final int requestCode, Intent intent) {
        PendingIntent pendingIntent = PendingIntent.getBroadcast(context, requestCode, intent, PendingIntent.FLAG_NO_CREATE);
        return pendingIntent != null;
    }
}
