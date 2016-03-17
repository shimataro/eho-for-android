package com.example.shimataro.eho;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.PixelFormat;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.widget.NumberPicker;
import android.widget.TextView;

import java.util.Calendar;

public class MainActivity extends AppCompatActivity implements SensorEventListener {

    private TextView m_textViewEho = null;
    private SurfaceView m_surfaceView = null;
    private Drawable m_drawableCompassBase = null;
    private Drawable m_drawableCompassNeedle = null;

    private Eho m_eho = new Eho();
    private double m_orientation = 0;

    // 方位取得用
    private SensorManager m_sensorManager = null;
    private float[] m_valuesMagnetic = null;
    private float[] m_valuesAccelerometer = null;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // http://teru2-bo2.blogspot.jp/2012/06/android.html
        m_sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);

        m_textViewEho = (TextView)findViewById(R.id.textView_eho);
        m_surfaceView = (SurfaceView)findViewById(R.id.surfaceView_compass);
        _initActionBar();
        _initCompass();
        _initInputYear();
    }

    @Override
    public void onPause() {
        // センサーイベントを削除
        m_sensorManager.unregisterListener(this);

        super.onPause();
    }

    @Override
    public void onResume() {
        super.onResume();

        // センサーイベントの登録
        m_sensorManager.registerListener(this, m_sensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD), SensorManager.SENSOR_DELAY_UI);
        m_sensorManager.registerListener(this, m_sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER), SensorManager.SENSOR_DELAY_UI);
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
    }


    @Override
    public void onSensorChanged(SensorEvent event) {
        if (event.accuracy == SensorManager.SENSOR_STATUS_UNRELIABLE) {
            return;
        }

        m_orientation = _getOrientation(event);
        _drawCompass(m_surfaceView.getHolder());
    }


    /**
     * アクションバーを初期化
     */
    private void _initActionBar() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
    }

    /**
     * コンパスを初期化
     */
    private void _initCompass() {
        if(!_canUseOrientationSensor()) {
            // 方位センサーを使えなければ警告メッセージ表示
            findViewById(R.id.textView_warning_magnetic_field).setVisibility(View.VISIBLE);
        }

        // 台紙
        m_drawableCompassBase = getDrawable(R.drawable.compass_base);
        m_drawableCompassBase.setBounds(0, 0, m_drawableCompassBase.getIntrinsicWidth(), m_drawableCompassBase.getIntrinsicHeight());

        // 針
        m_drawableCompassNeedle = getDrawable(R.drawable.compass_needle);
        m_drawableCompassNeedle.setBounds(0, 0, m_drawableCompassNeedle.getIntrinsicWidth(), m_drawableCompassNeedle.getIntrinsicHeight());

        // SurfaceViewの初期化
        m_surfaceView.setZOrderOnTop(true);
        m_surfaceView.getHolder().setFormat(PixelFormat.TRANSLUCENT);
        m_surfaceView.getHolder().addCallback(new SurfaceHolder.Callback() {
            @Override
            public void surfaceCreated(SurfaceHolder holder) {
                _drawCompass(holder);
            }

            @Override
            public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
            }

            @Override
            public void surfaceDestroyed(SurfaceHolder holder) {
            }
        });
    }

    /**
     * 年の入力コントロールを初期化
     */
    private void _initInputYear() {
        NumberPicker numberPicker = (NumberPicker) findViewById(R.id.year);

        // 最小値・最大値を設定（西暦1年〜9999年）
        numberPicker.setMinValue(1);
        numberPicker.setMaxValue(9999);

        // 今年を入れておく
        final int year = _getThisYear();
        numberPicker.setValue(year);

        // 今年の恵方を表示
        _showEho(year);

        // 年が変わったら恵方も変える
        numberPicker.setOnValueChangedListener(new NumberPicker.OnValueChangeListener() {
            @Override
            public void onValueChange(NumberPicker picker, int oldVal, int newVal) {
                _showEho(newVal);
            }
        });
    }


    /**
     * 方位センサーが使えるか？
     * 地磁気センサーおよび加速度センサーの両方が使えなければNG
     * @return OK/NG
     */
    private boolean _canUseOrientationSensor() {
        if(m_sensorManager.getSensorList(Sensor.TYPE_MAGNETIC_FIELD).size() == 0) {
            return false;
        }

        if(m_sensorManager.getSensorList(Sensor.TYPE_ACCELEROMETER).size() == 0) {
            return false;
        }

        return true;
    }

    /**
     * 方位を取得
     * @see http://wp.developapp.net/?p=3394
     * @see http://teru2-bo2.blogspot.jp/2012/06/android.html
     * @param event センサーイベント
     * @return 方位
     */
    private double _getOrientation(SensorEvent event) {
        switch (event.sensor.getType()) {
            case Sensor.TYPE_MAGNETIC_FIELD: // 地磁気センサー
                m_valuesMagnetic = event.values.clone();
                break;

            case Sensor.TYPE_ACCELEROMETER:  // 加速度センサー
                m_valuesAccelerometer = event.values.clone();
                break;
        }

        if (m_valuesMagnetic == null || m_valuesAccelerometer == null) {
            return 0;
        }

        float orientation[] = new float[3];

        float R[] = new float[16];
        float I[] = new float[16];

        // 加速度センサと地磁気センサから回転行列を取得
        SensorManager.getRotationMatrix(R, I, m_valuesAccelerometer, m_valuesMagnetic);
        SensorManager.getOrientation(R, orientation);

        // 方位を取得する
        return Math.toDegrees(orientation[0]);
    }


    /**
     * コンパスを描画
     * @param holder センサーのホルダー
     */
    private void _drawCompass(SurfaceHolder holder) {
        Canvas canvas = holder.lockCanvas();
        if(canvas == null) {
            return;
        }

        // 中央座標
        final int centerX = canvas.getWidth() / 2, centerY = canvas.getHeight() / 2;

        // 全部クリア
        canvas.drawColor(Color.TRANSPARENT, PorterDuff.Mode.CLEAR);

        // コンパスの台紙を描画
        canvas.rotate((float) -m_orientation, centerX, centerY);
        m_drawableCompassBase.draw(canvas);

        // コンパスの針を描画
        canvas.rotate(m_eho.getOrientation(), centerX, centerY);
        m_drawableCompassNeedle.draw(canvas);

        holder.unlockCanvasAndPost(canvas);
    }

    /**
     * 指定年の恵方を表示（文字・コンパス）
     * @param year 恵方を表示する年
     */
    private void _showEho(final int year) {
        m_eho.setYear(year);
        m_textViewEho.setText(_eho2str(m_eho));
        _drawCompass(m_surfaceView.getHolder());
    }


    /**
     * 今年を取得
     * @return 今年
     */
    private int _getThisYear() {
        Calendar cal = Calendar.getInstance();
        return cal.get(Calendar.YEAR);
    }

    /**
     * 恵方から文字列を取得
     * @param eho 恵方
     * @return 恵方の文字列
     */
    private String _eho2str(final Eho eho) {
        int str_id = R.string.unknown;
        switch(eho.getSymbol()) {
            case WSW:
                str_id = R.string.dir_wsw;
                break;

            case SSE:
                str_id = R.string.dir_sse;
                break;

            case NNW:
                str_id = R.string.dir_nnw;
                break;

            case ENE:
                str_id = R.string.dir_ene;
                break;
        }
        return getString(str_id);
    }

}
