package com.example.shimataro.eho;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.widget.TextView;

public class ResultActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_result);

        final int year = this._getYear();
        this._initActionBar();
        this._initResultLeading(year);
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
}
