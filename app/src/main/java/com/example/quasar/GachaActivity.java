package com.example.quasar;

import android.database.Cursor;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import java.util.Random;

public class GachaActivity extends AppCompatActivity {

    TextView txtExitGacha;
    csdl db;
    String username;
    int crystal;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layoutgacha);

        txtExitGacha = findViewById(R.id.txtExitGacha);

        db = new csdl(this);
        username = getIntent().getStringExtra("username");

        findViewById(R.id.txtrut).setOnClickListener(v -> pullCard());

        ImageView imgInfo = findViewById(R.id.clam);

        imgInfo.setOnClickListener(v -> showRateDialog());
        txtExitGacha.setOnClickListener(v -> finish());
    }

    private void pullCard() {

        crystal = db.getCrystal(username);

        if (crystal < 10) {
            Toast.makeText(this, "Không đủ crystal", Toast.LENGTH_SHORT).show();
            return;
        }

        // trừ 10 crystal
        crystal -= 10;
        db.updateCrystal(username, crystal);

        // xác định rarity theo tỉ lệ
        int roll = new Random().nextInt(100) + 1;
        String rarity;

        if (roll <= 75) rarity = "R";
        else if (roll <= 95) rarity = "SR";
        else rarity = "SSR";

        Cursor c = db.getRandomCardByRarity(rarity);

        if (c.moveToFirst()) {

            int cardId = c.getInt(c.getColumnIndexOrThrow("id"));
            String imageName = c.getString(c.getColumnIndexOrThrow("image"));

            boolean owned = db.hasCard(username, cardId);

            if (owned) {
                crystal += 5;
                db.updateCrystal(username, crystal);
            } else {
                db.addUserCard(username, cardId);
            }

            showCardDialog(imageName, owned);
        }
        c.close();
    }

    private void showCardDialog(String imageName, boolean owned) {

        View v = getLayoutInflater().inflate(R.layout.dialog_gacha, null);
        ImageView img = v.findViewById(R.id.imgCard);

        int resId = getResources().getIdentifier(
                imageName, "drawable", getPackageName());

        img.setImageResource(resId);

        String msg = owned ? "Thẻ trùng! +5 crystal" : "Bạn nhận được thẻ mới!";

        new AlertDialog.Builder(this)
                .setTitle(msg)
                .setView(v)
                .setPositiveButton("OK", null)
                .show();
    }

    private void showRateDialog() {
        new AlertDialog.Builder(this)
                .setTitle("Tỉ lệ rút thẻ")
                .setMessage(
                        "R: 75%\n" +
                                "SR: 20%\n" +
                                "SSR: 5%"
                )
                .setPositiveButton("OK", null)
                .show();
    }
}
