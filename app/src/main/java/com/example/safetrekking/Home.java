package com.example.safetrekking;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.view.Window;


public class Home extends AppCompatActivity {
    int idUPrefs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_home);

        //nascondo la title bar
        getSupportActionBar().hide();
        
        //recupero l'idU
        idUPrefs = getSharedPreferences("MySharedPreferences", 0).getInt("idu", 7);
        Log.d("home", idUPrefs + "");

    }

    //disabilito back button
    @Override
    public void onBackPressed() {
        // super.onBackPressed();
        // Not calling **super**, disables back button in current screen.
    }
}