package com.example.shimataro.eho;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

/**
 * @see <a href="http://techbooster.jpn.org/andriod/application/1100/">システムの起動時にサービスを実行する «  Tech Booster</a>
 * @see <a href="http://qiita.com/amay077/items/e740015c00a6e7d45f6b">システムの起動時にアプリを起動する - Qiita</a>
 * @see <a href="http://blog.justoneplanet.info/2012/05/27/androidで本体の電源を入れた時にアプリを起動させる/">Androidで本体の電源を入れた時にアプリを起動させる - @blog.justoneplanet.info</a>
 * Created by odashima on 16/04/13.
 */
public class BootReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        Log.d("---- BootReceiver ----", "onReceive");

        // 毎日9時に起動
        Scheduler.setScheduleDaily(context, AlarmReceiver.class, 9, 0, 0);
    }
}
