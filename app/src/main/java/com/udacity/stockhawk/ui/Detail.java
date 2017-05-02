package com.udacity.stockhawk.ui;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;

import com.udacity.stockhawk.R;

public class Detail extends AppCompatActivity {

    private static final String LOG_TAG = Detail.class.getSimpleName();




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        if(savedInstanceState== null){
            DetailFragment fragment = new DetailFragment();
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.stock_detail_container, fragment)
                    .commit();

        }



    }



    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.detail_menu,menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.action_settings:{
                Intent intent = new Intent(this,SettingsActivity.class);
                startActivity(intent);
                return true;
            }
        }
        return super.onOptionsItemSelected(item);
    }


}
