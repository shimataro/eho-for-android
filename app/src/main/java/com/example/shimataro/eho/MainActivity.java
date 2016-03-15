package com.example.shimataro.eho;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import java.util.Calendar;

public class MainActivity extends AppCompatActivity {

    private AlertDialog.Builder m_dialog = null;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        this._initActionBar();
        this._initYearText();
        this._initButton();
    }


    private void _initActionBar() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
    }

    private void _initYearText() {
        EditText editText = (EditText) findViewById(R.id.year);

        // 今年を入れておく
        Calendar cal = Calendar.getInstance();
        final int year = cal.get(Calendar.YEAR);
        editText.setText(String.valueOf(year));

        // テキストが空ならボタンを無効化
        editText.addTextChangedListener(new TextWatcher() {
            private Button m_button = null;

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                boolean enabled = (s.length() > 0);
                this._getButton().setEnabled(enabled);
            }

            private Button _getButton() {
                if(m_button == null) {
                    m_button = (Button) findViewById(R.id.button_calc_eho);
                }
                return m_button;
            }
        });
    }

    private void _initButton() {
        Button button = (Button)findViewById(R.id.button_calc_eho);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 確認ダイアログ
                MainActivity.this.getDialog().create().show();
            }
        });
    }

    public AlertDialog.Builder getDialog() {
        if (m_dialog == null) {
            m_dialog = new AlertDialog.Builder(this)
                    .setCancelable(false)
                    .setTitle(R.string.app_name)
                    .setMessage(R.string.eho_confirm)
                    .setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            // 年を取得
                            EditText editText = (EditText)findViewById(R.id.year);
                            int year = Integer.parseInt(editText.getText().toString());
                            Log.d("year", String.valueOf(year));

                            // 結果表示アクティビティーへ遷移
                            Intent intent = new Intent(MainActivity.this, ResultActivity.class);
                            intent.putExtra("year", year);
                            startActivity(intent);
                        }
                    });
        }

        return m_dialog;
    }

}
