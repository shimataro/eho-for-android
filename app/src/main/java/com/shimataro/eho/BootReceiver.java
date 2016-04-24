package com.shimataro.eho;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

/**
 * システムブート受信
 * @see <a href="http://techbooster.org/andriod/application/1100/">システムの起動時にサービスを実行する «  Tech Booster</a>
 * @see <a href="http://qiita.com/amay077/items/e740015c00a6e7d45f6b">システムの起動時にアプリを起動する - Qiita</a>
 * @see <a href="http://blog.justoneplanet.info/2012/05/27/android%E3%81%A7%E6%9C%AC%E4%BD%93%E3%81%AE%E9%9B%BB%E6%BA%90%E3%82%92%E5%85%A5%E3%82%8C%E3%81%9F%E6%99%82%E3%81%AB%E3%82%A2%E3%83%97%E3%83%AA%E3%82%92%E8%B5%B7%E5%8B%95%E3%81%95%E3%81%9B%E3%82%8B/">Androidで本体の電源を入れた時にアプリを起動させる - @blog.justoneplanet.info</a>
 * Created by shimataro on 16/04/13.
 */
public class BootReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        Log.i("---- BootReceiver ----", "onReceive");

        // 毎日9時に起動
        Scheduler.setScheduleDaily(context, AlarmReceiver.class, 9, 0, 0);
    }
}
