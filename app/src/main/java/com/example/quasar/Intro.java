package com.example.quasar;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class Intro extends AppCompatActivity {
    EditText edtPlayerName;
    TextView btnStart;
    csdl db;
    String username;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_intro);

        edtPlayerName = findViewById(R.id.edtPlayerName);
        btnStart = findViewById(R.id.btnStart);

        db = new csdl(this);
        username = getIntent().getStringExtra("username");

        btnStart.setOnClickListener(v -> {
            String playerName = edtPlayerName.getText().toString().trim();

            if (playerName.isEmpty()) {
                Toast.makeText(this, "Vui lòng nhập tên người chơi", Toast.LENGTH_SHORT).show();
                return;
            }

            // lưu tên + cập nhật level = 1
            db.updatePlayer(username, playerName, 1);

            Intent i = new Intent(this, MainActivity.class);
            i.putExtra("username", username);
            startActivity(i);
            finish();
        });
    }
}
