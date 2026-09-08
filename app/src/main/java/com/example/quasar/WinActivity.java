package com.example.quasar;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

public class WinActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_win);
        findViewById(R.id.win_back).setOnClickListener(v -> {
            finish(); // quay lại thử thách
        });
    }
}
