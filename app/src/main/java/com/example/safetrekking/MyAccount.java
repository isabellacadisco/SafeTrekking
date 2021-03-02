package com.example.safetrekking;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;

import com.google.android.material.bottomnavigation.BottomNavigationView;

public class MyAccount extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_account);

        //nascondo la title bar
        getSupportActionBar().hide();

        // CODICE BOTTOM MENU

        // inizializzo e assegno variabili
        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_navigation);

        // set home selected
        bottomNavigationView.setSelectedItemId(R.id.account);

        //item selected listener
        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()){
                    case R.id.myHikes:
                        startActivity(new Intent(getApplicationContext(),
                                myHikes.class));
                        overridePendingTransition(0, 0);
                        return true;

                    case R.id.newHike:
                        startActivity(new Intent(getApplicationContext(),
                                Home.class));
                        overridePendingTransition(0, 0);
                        return true;

                    case R.id.freeHike:
                        startActivity(new Intent(getApplicationContext(),
                                freeHike.class));
                        overridePendingTransition(0, 0);

                        return true;

                    case R.id.account:

                        return true;
                }
                return false;
            }
        });
        // FINE BOTTOM MENU
    }
}