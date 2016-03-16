package com.example.shimataro.eho;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.widget.NumberPicker;
import android.widget.TextView;

import java.util.Calendar;

public class MainActivity extends AppCompatActivity {

    // 恵方
    private enum EHO {
        WSW, SSE, NNW, ENE,
    }

    private TextView m_textViewEho = null;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        m_textViewEho = (TextView)findViewById(R.id.textView_eho);
        _initActionBar();
        _initInputYear();
    }


    private void _initActionBar() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
    }

    private void _initInputYear() {
        NumberPicker numberPicker = (NumberPicker) findViewById(R.id.year);

        // 最小値・最大値を設定（西暦1年〜9999年）
        numberPicker.setMinValue(1);
        numberPicker.setMaxValue(9999);

        // 今年を入れておく
        final int year = _getThisYear();
        numberPicker.setValue(year);

        // 今年の恵方を表示
        _setResult(year);

        // 年が変わったら恵方も変える
        numberPicker.setOnValueChangedListener(new NumberPicker.OnValueChangeListener() {
            @Override
            public void onValueChange(NumberPicker picker, int oldVal, int newVal) {
                MainActivity.this._setResult(newVal);
            }
        });
    }

    private void _setResult(final int year) {
        final EHO eho = _getEho(year);
        m_textViewEho.setText(_eho2str(eho));
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
     * 恵方を取得
     * @param year 年
     * @return 恵方（のリソースID）
     */
    private EHO _getEho(final int year) {
        final EHO dirs[] = {EHO.WSW, EHO.SSE, EHO.NNW, EHO.SSE, EHO.ENE};
        return dirs[year % 5];
    }

    /**
     * 恵方から文字列を取得
     * @param eho 恵方
     * @return 恵方の文字列
     */
    private String _eho2str(final EHO eho) {
        int str_id = R.string.unknown;
        switch(eho) {
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
