package com.example.quasar;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class LoseActivity extends AppCompatActivity {

    String username;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_lose);

        username = getIntent().getStringExtra("username");

        if (username == null) {
            Toast.makeText(this, "Thiếu username", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        findViewById(R.id.lose_retry).setOnClickListener(v -> {
            Intent i = new Intent(this, Clg11Activity.class);
            i.putExtra("username", username);
            startActivity(i);
            finish();
        });

        findViewById(R.id.lose_back).setOnClickListener(v -> {
            finish(); // quay lại thử thách
        });
    }
}
