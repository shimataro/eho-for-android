package com.example.shimataro.eho;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.view.SurfaceHolder;

/**
 * Created by shimataro on 16/03/19.
 */
public class CompassCallback implements Runnable, SurfaceHolder.Callback {
    private Thread m_thread = null;
    private boolean m_threadQuit = false;

    private SurfaceHolder m_holder = null;

    private Drawable m_drawableCompassBase = null;
    private Drawable m_drawableCompassNeedle = null;
    private Drawable m_drawableCompassButton = null;

    private double m_orientationCompass = 0;
    private double m_orientationEho = 0;

    public CompassCallback(Drawable drawableCompassBase, Drawable drawableCompassNeedle, Drawable drawableCompassButton) {
        m_drawableCompassBase   = drawableCompassBase;
        m_drawableCompassNeedle = drawableCompassNeedle;
        m_drawableCompassButton = drawableCompassButton;
    }

    /**
     * 恵方を設定
     * @param orientationEho
     */
    public void setOrientationEho(final double orientationEho) {
        m_orientationEho = orientationEho;
        _doNotify();
    }

    /**
     * コンパスの方位を設定
     * @param orientationCompass
     */
    public void setOrientationCompass(final double orientationCompass) {
        m_orientationCompass = orientationCompass;
        _doNotify();
    }


    @Override
    public void run() {
        synchronized (this) {
            m_threadQuit = false;
        }
        while(!m_threadQuit) {
            _drawCompass();
            _doWait();
        }
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        m_holder = holder;
        _drawCompass();

        m_thread = new Thread(this);
        m_thread.start();
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        synchronized (this) {
            m_threadQuit = true;
        }
        _doNotify();

        try {
            m_thread.join();
        }
        catch (InterruptedException e) {
        }
        m_thread = null;
    }


    synchronized private void _doWait() {
        try {
            wait();
        }
        catch (InterruptedException e) {
        }
    }

    synchronized private void _doNotify() {
        notify();
    }

    /**
     * コンパスを描画
     * @param holder センサーのホルダー
     */
    private void _drawCompass() {
        SurfaceHolder holder = m_holder;
        Canvas canvas = holder.lockCanvas();
        if(canvas == null) {
            return;
        }

        // 中央座標
        final int centerX = canvas.getWidth() / 2, centerY = canvas.getHeight() / 2;

        // 全部クリア
        canvas.drawColor(Color.TRANSPARENT, PorterDuff.Mode.CLEAR);

        // コンパスの台紙を描画
        canvas.rotate((float) -m_orientationCompass, centerX, centerY);
        m_drawableCompassBase.draw(canvas);

        // コンパスの針を描画
        canvas.rotate((float) m_orientationEho, centerX, centerY);
        m_drawableCompassNeedle.draw(canvas);

        // コンパスのボタンを描画
        m_drawableCompassButton.draw(canvas);

        holder.unlockCanvasAndPost(canvas);
    }
}
