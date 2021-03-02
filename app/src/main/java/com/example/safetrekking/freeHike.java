package com.example.safetrekking;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.google.android.material.bottomnavigation.BottomNavigationView;

public class freeHike extends AppCompatActivity implements View.OnClickListener{

    EditText ed;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_free_hike);

        //nascondo la title bar
        getSupportActionBar().hide();

        // CODICE BOTTOM MENU

        // inizializzo e assegno variabili
        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_navigation);

        // set home selected
        bottomNavigationView.setSelectedItemId(R.id.freeHike);

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

                    case R.id.account:
                        startActivity(new Intent(getApplicationContext(),
                                MyAccount.class));
                        overridePendingTransition(0, 0);
                        return true;

                    case R.id.freeHike:

                        return true;
                }
                return false;
            }
        });
        // FINE BOTTOM MENU

        //prova edit text
        ed = findViewById(R.id.editTextTextPostalAddress);

        Button bt = findViewById(R.id.change);
        bt.setOnClickListener(this);

        //fine prova et

    }

    @Override
    public void onClick(View view) {
        ed.setText("via novara 31");
    }
}