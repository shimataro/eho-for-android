package com.example.shimataro.eho;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.util.Log;
import android.view.SurfaceHolder;

/**
 * Created by shimataro on 16/03/19.
 */
public class CompassCallback implements Runnable, SurfaceHolder.Callback {
    private Thread m_thread = null;
    volatile private boolean m_threadRunning = false;

    private SurfaceHolder m_holder = null;

    private Drawable m_drawableCompassBase = null;
    private Drawable m_drawableCompassNeedle = null;
    private Drawable m_drawableCompassButton = null;

    private float m_orientationCompass = 0;
    private float m_orientationEho = 0;

    public CompassCallback(Drawable drawableCompassBase, Drawable drawableCompassNeedle, Drawable drawableCompassButton) {
        m_drawableCompassBase   = drawableCompassBase;
        m_drawableCompassNeedle = drawableCompassNeedle;
        m_drawableCompassButton = drawableCompassButton;
    }

    /**
     * 恵方を設定
     * @param orientationEho
     */
    public void setOrientationEho(final float orientationEho) {
        m_orientationEho = orientationEho;
        _threadNotify();
    }

    /**
     * コンパスの方位を設定
     * @param orientationCompass
     */
    public void setOrientationCompass(final float orientationCompass) {
        m_orientationCompass = orientationCompass;
        _threadNotify();
    }


    @Override
    public void run() {
        Log.d("Compass Thread", "Begin");
        while (m_threadRunning) {
            _drawCompass();
            _threadWait();
        }
        Log.d("Compass Thread", "End");
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


    private void _threadStart() {
        m_threadRunning = true;
        m_thread = new Thread(this);
        m_thread.start();
    }

    private void _threadQuit() {
        m_threadRunning = false;
        _threadNotify();

        // 終わるまで待つ
        try {
            m_thread.join();
        }
        catch (InterruptedException e) {
            Log.d("InterruptedException", e.getMessage());
        }
        m_thread = null;
    }

    synchronized private void _threadWait() {
        try {
            // ここのifがないと、_threadQuit()が先にコールされた場合に永久にストップする
            if (m_threadRunning) {
                wait();
            }
        }
        catch (InterruptedException e) {
            Log.d("InterruptedException", e.getMessage());
        }
    }

    synchronized private void _threadNotify() {
        notify();
    }

    /**
     * コンパスを描画
     * @param holder センサーのホルダー
     */
    private void _drawCompass() {
        SurfaceHolder holder = m_holder;
        Canvas canvas = holder.lockCanvas();
        if (canvas == null) {
            return;
        }

        // 中央座標
        final int centerX = canvas.getWidth() / 2, centerY = canvas.getHeight() / 2;

        // 全部クリア
        canvas.drawColor(Color.TRANSPARENT, PorterDuff.Mode.CLEAR);

        // コンパスの台紙を描画
        canvas.rotate(-m_orientationCompass, centerX, centerY);
        m_drawableCompassBase.draw(canvas);

        // コンパスの針を描画
        canvas.rotate(m_orientationEho, centerX, centerY);
        m_drawableCompassNeedle.draw(canvas);

        // コンパスのボタンを描画
        m_drawableCompassButton.draw(canvas);

        holder.unlockCanvasAndPost(canvas);
    }
}
