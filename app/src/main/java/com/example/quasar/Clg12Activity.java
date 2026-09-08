package com.example.quasar;

import android.content.Intent;
import android.os.Bundle;

public class Clg12Activity extends BaseBattleActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_clg12);

        bossHp = 15;
        mainHp = 12;

        initBase();
    }
    @Override
    protected  int getEnemyDamage() { return 3; }
    @Override
    protected int getNormalAttackDamage() { return 2; }
    @Override
    protected void winBattle() {
        int crystal = db.getCrystal(username) + 200;
        db.updateCrystal(username, crystal);

        Intent i = new Intent(this, WinActivity.class);
        i.putExtra("username", username);
        startActivity(i);
        finish();
    }

}
