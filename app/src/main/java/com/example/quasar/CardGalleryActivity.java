package com.example.quasar;

import android.database.Cursor;
import android.os.Bundle;
import android.widget.GridView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class CardGalleryActivity extends AppCompatActivity {

    GridView gridView;
    TextView txtExit;
    csdl db;
    String username;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_card_gallery);

        gridView = findViewById(R.id.gridCards);
        txtExit = findViewById(R.id.txtExit);

        db = new csdl(this);

        username = getIntent().getStringExtra("username");
        if (username == null) {
            finish();
            return;
        }

        loadCards();
        txtExit.setOnClickListener(v -> finish());
    }

    private void loadCards() {
        Cursor c = db.getUserCards(username);
        CardAdapter adapter = new CardAdapter(this, c);
        gridView.setAdapter(adapter);
    }
}
