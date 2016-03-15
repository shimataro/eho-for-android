package com.example.shimataro.eho;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.widget.TextView;

public class ResultActivity extends AppCompatActivity {

    // 恵方
    private enum EHO {
        WSW, SSE, NNW, ENE,
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_result);

        final int year = this._getYear();
        this._initActionBar();
        this._initResultLeading(year);
        this._initResult(year);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        final int id = item.getItemId();
        switch(id) {
            case android.R.id.home:
                finish();
                return true;
        }

        return super.onOptionsItemSelected(item);
    }


    private int _getYear() {
        Intent intent = getIntent();
        Bundle bundle = intent.getExtras();
        return bundle.getInt("year");
    }

    private void _initActionBar() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        ActionBar actionbar = getSupportActionBar();
        if(actionbar == null) {
            return;
        }

        actionbar.setDisplayHomeAsUpEnabled(true);
        actionbar.setHomeButtonEnabled(true);
    }

    private void _initResultLeading(final int year) {
        final String text = getString(R.string.eho_result_leading, year);
        TextView textView = (TextView)findViewById(R.id.textView_eho_leading);
        textView.setText(text);
    }

    private void _initResult(final int year) {
        final EHO eho = this._getEho(year);
        TextView textView = (TextView)findViewById(R.id.textView_eho);
        textView.setText(this._eho2str(eho));
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
