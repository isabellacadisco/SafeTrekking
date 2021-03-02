package com.example.safetrekking;

import androidx.appcompat.app.AppCompatActivity;

import android.app.DownloadManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;


public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private EditText number;
    Context context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //salvo il context
        context = this;

        // recupero l'elemento del layout dove inserisco il numero
        number = findViewById(R.id.editTextNumber);

        // creo riferimento a bottone per entrare
        Button enterNumber = findViewById(R.id.buttonEnterApp);

        //assegno il ClickListener al bottone
        enterNumber.setOnClickListener(this);

        //controllo di non essere già loggato, se lo sono passo direttamente a schermata iniziale
        if(getSharedPreferences("MySharedPreferences",0).getInt("idu", 7) != 7){
            Intent intent = new Intent(this, Home.class);
            startActivity(intent);
        }
    }


    @Override
    public void onClick(View view) {

        // nuovo utente, numero da registrare o utente registrato ma non fatto login
        String numberToAdd = number.getText().toString();

        // Volley per mandare al server il numero da controllare o aggiungere

        //istanzio request queue
        RequestQueue queue = Volley.newRequestQueue(this);

        //url del server
        String url = "http://192.168.1.14:3000/enterApp";

        //creo il body json da mandare
        JSONObject jsonBody = new JSONObject();
        try {
            jsonBody.put("number", numberToAdd);
        } catch (JSONException e) {
            Log.d("main", "non costruisce oggetto json");
        }

        //gestisco la risposta
        JsonObjectRequest request = new JsonObjectRequest(
                url,
                jsonBody,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Log.d("onclick", "ok" + response.toString());

                        //recupero idu che mi manda in risposta
                        int Idu = 0;
                        try {
                            Idu = response.getInt("idu");
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        Log.d("main", " "+Idu);

                        //metto id ricevuto nelle Shared Preferences
                        SharedPreferences prefs = getSharedPreferences("MySharedPreferences",0);
                        SharedPreferences.Editor editor = prefs.edit();
                        editor.putInt("idu", Idu);
                        editor.apply();
                        //id messo

                        //vado alla schermata home
                        Intent intent = new Intent(context, Home.class);
                        startActivity(intent);
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.d("onclick","no ok" + error.toString());
                    }
                }
        );

        queue.add(request);



    }
}