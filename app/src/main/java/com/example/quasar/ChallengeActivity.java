package com.example.quasar;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class ChallengeActivity extends AppCompatActivity {

    String username;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.challenge);

        username = getIntent().getStringExtra("username");

        if (username == null) {
            Toast.makeText(this, "Thiếu username", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        TextView btnBack = findViewById(R.id.txtExit_clg);
        btnBack.setOnClickListener(v -> finish());

        ImageView imgClg11 = findViewById(R.id.clg11);
        imgClg11.setOnClickListener(v -> {
            Intent i = new Intent(this, Clg11Activity.class);
            i.putExtra("username", username);
            startActivity(i);
        });
        ImageView imgClg12 = findViewById(R.id.clg12);
        imgClg12.setOnClickListener(v -> {
            Intent i = new Intent(this, Clg12Activity.class);
            i.putExtra("username", username);
            startActivity(i);
        });
    }
}
