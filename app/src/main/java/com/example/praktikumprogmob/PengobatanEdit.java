package com.example.praktikumprogmob;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;

public class PengobatanEdit extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pengobatan_edit);
    }
    public void cancelEditMed(View view) {
        super.onBackPressed();
    }
}