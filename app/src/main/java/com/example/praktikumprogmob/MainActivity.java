package com.example.praktikumprogmob;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                isFirstTime();
            }
        }, 1500);
    }

    private void isFirstTime() {
        //untuk mengecek apakah aplikasinya berjalan untuk pertama kalinya
        //valuenya di save di SharedPreferance
        SharedPreferences preferences = getApplication().getSharedPreferences("onboard", Context.MODE_PRIVATE);
        boolean isFirstTime = preferences.getBoolean("isFirstTime", true);

        if(isFirstTime){
            SharedPreferences.Editor editor = preferences.edit();
            editor.putBoolean("isFirstTime", false);
            editor.apply();

            startActivity(new Intent(MainActivity.this, OnBoardActivity.class));
            finish();
        }else{
            startActivity(new Intent(MainActivity.this, LoginActivity.class));
            finish();
        }

    }
}