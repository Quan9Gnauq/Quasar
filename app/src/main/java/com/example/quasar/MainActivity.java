package com.example.quasar;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;


import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
public class MainActivity extends AppCompatActivity {

    ImageView imgAvatar, imgChest;
    TextView txtCrystal;
    int crystal;
    csdl db;
    String username;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_game);

        imgAvatar = findViewById(R.id.icon1);
        imgChest = findViewById(R.id.imgChest);
        txtCrystal = findViewById(R.id.txtCrystal);

        if (imgAvatar == null || imgChest == null || txtCrystal == null) {
            Toast.makeText(this, "Lỗi layout main_game.xml", Toast.LENGTH_LONG).show();
            finish();
            return;
        }

        db = new csdl(this);

        username = getIntent().getStringExtra("username");
        if (username == null) {
            Toast.makeText(this, "Lỗi: không có username", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        crystal = db.getCrystal(username);
        txtCrystal.setText(String.valueOf(crystal));

        if (crystal > 0) {
            imgChest.setVisibility(View.GONE);
        }

        imgAvatar.setOnClickListener(v -> showProfileDialog());
        imgChest.setOnClickListener(v -> showRewardDialog());

        db.insertCard("Tấn công", "csword", "R");
        db.insertCard("Phòng thủ", "cshield", "R");
        db.insertCard("Ma thuật", "cmage", "SR");
        db.insertCard("Hatsune Miku", "cmiku1", "SSR");
        findViewById(R.id.gacha).setOnClickListener(v -> {
            Intent i = new Intent(this, GachaActivity.class);
            i.putExtra("username", username);
            startActivity(i);
        });

        ImageView imgInfo = findViewById(R.id.crystal);
        imgInfo.setOnClickListener(v -> showCrystalInfo());

        TextView imgplus = findViewById(R.id.pay);
        imgplus.setOnClickListener(v -> showNuoiemDialog());

        findViewById(R.id.gallery).setOnClickListener(v -> {
            Intent i = new Intent(this, CardGalleryActivity.class);
            i.putExtra("username", username);
            startActivity(i);
        });

        loadCrystal();
    }

    private void showProfileDialog() {
        Cursor c = db.getUserInfo(username);
        if (c != null && c.moveToFirst()) {
            String name = c.getString(c.getColumnIndexOrThrow("player_name"));
            int level = c.getInt(c.getColumnIndexOrThrow("level"));

            View v = getLayoutInflater().inflate(R.layout.profile, null);

            ((TextView) v.findViewById(R.id.txtPlayerName))
                    .setText("Tên: " + name);
            ((TextView) v.findViewById(R.id.txtLevel))
                    .setText("Cấp độ: " + level);

            new AlertDialog.Builder(this)
                    .setTitle("Thông tin người chơi")
                    .setView(v)
                    .setPositiveButton("OK", null)
                    .show();
        }
        if (c != null) c.close();
    }

    private void showRewardDialog() {
        new AlertDialog.Builder(this)
                .setTitle("Phần thưởng")
                .setMessage("Bạn nhận được 50 Crystal!")
                .setPositiveButton("OK", (d, w) -> {
                    crystal += 50;
                    txtCrystal.setText(String.valueOf(crystal));
                    db.updateCrystal(username, crystal);
                    imgChest.setVisibility(View.GONE);
                })
                .setCancelable(false)
                .show();
    }

    private void showCrystalInfo() {
        new AlertDialog.Builder(this)
                .setTitle("Crystal")
                .setMessage(
                        "Loại đá quý hiếm được tìm thấy ở/n" + "những nơi có nồng độ ma lực cao/n" + "Sử dụng để rút thẻ"
                )
                .setPositiveButton("OK", null)
                .show();
    }

    private void showNuoiemDialog() {

        View v = getLayoutInflater().inflate(R.layout.nuoiem, null);

        TextView btnCfPay = v.findViewById(R.id.cfpay);

        AlertDialog dialog = new AlertDialog.Builder(this)
                .setTitle("Nạp Crystal")
                .setView(v)
                .create();

        btnCfPay.setOnClickListener(view -> {

            int crystal = db.getCrystal(username);
            crystal += 500;
            db.updateCrystal(username, crystal);

            Toast.makeText(this, "+500 Crystal", Toast.LENGTH_SHORT).show();

            dialog.dismiss();
        });

        dialog.show();
    }

    private void loadCrystal() {
        crystal = db.getCrystal(username);
        txtCrystal.setText(String.valueOf(crystal));
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadCrystal();
    }

}