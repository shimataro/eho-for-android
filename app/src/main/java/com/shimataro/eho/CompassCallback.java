package com.shimataro.eho;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.util.Log;
import android.view.SurfaceHolder;

/**
 * コンパス描画用コールバッククラス
 * Created by shimataro on 16/03/19.
 */
public class CompassCallback implements Runnable, SurfaceHolder.Callback {
    // スレッド関連
    private Thread m_thread = null;
    volatile private boolean m_threadRunning = false;

    // 描画関連
    private SurfaceHolder m_holder = null;
    private Bitmap m_bitmapCompassBase   = null;
    private Bitmap m_bitmapCompassNeedle = null;

    // 方位関連
    private float m_orientationCompass = 0;
    private float m_orientationEho = 0;

    public CompassCallback(Drawable drawableCompassBase, Drawable drawableCompassNeedle) {
        // VectorDrawableを直接描画すると高精細ディスプレイでは描画時に毎回拡大処理が走ってモザイクっぽくなることがあるので、一旦ビットマップに変換する
        m_bitmapCompassBase   = Drawable2Bitmap(drawableCompassBase);
        m_bitmapCompassNeedle = Drawable2Bitmap(drawableCompassNeedle);
    }

    /**
     * 恵方を設定
     * @param orientationEho 恵方[degree]
     */
    public void setOrientationEho(final float orientationEho) {
        m_orientationEho = orientationEho;
        _threadNotify(false);
    }

    /**
     * コンパスの方位を設定
     * @param orientationCompass 方位[degree]
     */
    public void setOrientationCompass(final float orientationCompass) {
        m_orientationCompass = orientationCompass;
        _threadNotify(false);
    }


    @Override
    public void run() {
        while (m_threadRunning) {
            _drawCompass();
            _threadWait();
        }
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        m_holder = holder;
        _drawCompass();

        _threadStart();
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        _threadQuit();
    }


    /**
     * スレッドを開始
     */
    private void _threadStart() {
        m_threadRunning = true;
        m_thread = new Thread(this);
        m_thread.start();
    }

    /**
     * スレッドを終了
     */
    private void _threadQuit() {
        // 終了通知を送る
        _threadNotify(true);

        // 終わるまで待つ
        try {
            m_thread.join();
        }
        catch (InterruptedException e) {
            Log.i("InterruptedException", e.getMessage());
        }
        m_thread = null;
    }

    /**
     * スレッドを一時停止
     */
    synchronized private void _threadWait() {
        try {
            // ここのifがないと、_threadQuit()が先にコールされた場合に永久にストップする
            if (m_threadRunning) {
                wait();
            }
        }
        catch (InterruptedException e) {
            Log.i("InterruptedException", e.getMessage());
        }
    }

    /**
     * スレッドを再開
     * @param quit 終了させるか？
     */
    synchronized private void _threadNotify(final boolean quit) {
        if (quit) {
            m_threadRunning = false;
        }
        notify();
    }

    /**
     * コンパスを描画
     */
    private void _drawCompass() {
        Canvas canvas = m_holder.lockCanvas();
        if (canvas == null) {
            return;
        }

        // 中央座標
        final int centerX = canvas.getWidth() / 2, centerY = canvas.getHeight() / 2;

        // 全部クリア
        canvas.drawColor(Color.TRANSPARENT, PorterDuff.Mode.CLEAR);

        // コンパスの台紙を描画
        canvas.rotate(-m_orientationCompass, centerX, centerY);
        canvas.drawBitmap(m_bitmapCompassBase, 0, 0, null);

        // コンパスの針を描画
        canvas.rotate(m_orientationEho, centerX, centerY);
        canvas.drawBitmap(m_bitmapCompassNeedle, 0, 0, null);

        m_holder.unlockCanvasAndPost(canvas);
    }


    /**
     * Drawableオブジェクトをビットマップに変換
     * @param drawable 変換元Drawable
     * @return ビットマップ
     */
    private static Bitmap Drawable2Bitmap(Drawable drawable) {
        // キャンバスにビットマップを割り当て
        final int width  = drawable.getIntrinsicWidth ();
        final int height = drawable.getIntrinsicHeight();
        Bitmap bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);

        // Drawableオブジェクトをキャンバスに描画
        drawable.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
        drawable.draw(canvas);

        return bitmap;
    }
}
