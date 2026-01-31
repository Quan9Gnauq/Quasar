package com.example.quasar;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class Nuoiem extends AppCompatActivity {

    csdl db;
    String username;
    int crystal;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.nuoiem);

        db = new csdl(this);

        // nhận username từ màn hình trước
        username = getIntent().getStringExtra("username");
        if (username == null) {
            finish();
            return;
        }

        TextView btnCfPay = findViewById(R.id.cfpay);

        btnCfPay.setOnClickListener(v -> {
            crystal = db.getCrystal(username);
            crystal += 500;
            db.updateCrystal(username, crystal);

            Toast.makeText(this, "+500 Crystal", Toast.LENGTH_SHORT).show();

            finish();
        });
    }
}
