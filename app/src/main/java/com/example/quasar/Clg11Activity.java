package com.example.quasar;

import android.content.Intent;
import android.os.Bundle;


public class Clg11Activity extends BaseBattleActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_clg11);

        bossHp = 10;
        mainHp = 10;

        initBase();
    }

    @Override
    protected void winBattle() {
        int crystal = db.getCrystal(username) + 100;
        db.updateCrystal(username, crystal);

        Intent i = new Intent(this, WinActivity.class);
        i.putExtra("username", username);
        startActivity(i);
        finish();
    }
}