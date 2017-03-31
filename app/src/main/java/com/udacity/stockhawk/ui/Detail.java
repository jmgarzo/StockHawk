package com.udacity.stockhawk.ui;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.udacity.stockhawk.R;

public class Detail extends AppCompatActivity {

    private String symbol;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        Intent intent = getIntent();
        if (null != intent) {
            symbol = intent.getStringExtra(Intent.EXTRA_TEXT);
        }
    }
}
