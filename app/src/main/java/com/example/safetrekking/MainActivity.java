package com.example.safetrekking;

import androidx.appcompat.app.AppCompatActivity;

import android.content.res.Resources;
import android.os.Bundle;
import android.util.Log;

public class MainActivity extends AppCompatActivity {

    private void initData(){
        Log.d("main", "init data");
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.d("main", "oncreate");

        initData();
        setTheme(R.style.Theme_AppCompat_DayNight);
        Log.d("main", "launcher");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Log.d("main", "setCV");


    }


}