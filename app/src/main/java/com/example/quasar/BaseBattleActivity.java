package com.example.quasar;

import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import java.util.HashSet;
import java.util.Set;

public abstract class BaseBattleActivity extends AppCompatActivity {
    private String selectedCard = null;   // ⭐ BẮT BUỘC
    private Set<String> usedCards = new HashSet<>(); // nếu bạn dùng thẻ 1 lần
    // ===== COMMON GAME STATE =====
    protected int bossHp;
    protected int mainHp;
    protected boolean playerTurn = true;

    // ===== COMMON VIEW =====
    protected TextView txtBossHp, txtMainHp;
    protected ImageView imgBoss, imgPlayer;
    protected ImageView btnAtk, btnPickCard;

    // ===== COMMON DATA =====
    protected csdl db;
    protected String username;

    // ===== INIT =====
    protected void initBase() {
        db = new csdl(this);
        username = getIntent().getStringExtra("username");

        if (username == null) {
            finish();
            return;
        }

        txtBossHp = findViewById(R.id.boss11_hp);
        txtMainHp = findViewById(R.id.main_hp);
        imgBoss = findViewById(R.id.ic_boss11);
        imgPlayer = findViewById(R.id.ic_main);
        btnAtk = findViewById(R.id.nor_atk);
        btnPickCard = findViewById(R.id.pick_card);

        updateHpUI();
        setupCommonActions();
    }

    // ===== COMMON UI =====
    protected void updateHpUI() {
        txtBossHp.setText(String.valueOf(Math.max(bossHp, 0)));
        txtMainHp.setText(String.valueOf(Math.max(mainHp, 0)));
    }

    // ===== COMMON ACTIONS =====
    private void setupCommonActions() {

        // đánh thường
        btnAtk.setOnClickListener(v -> {
            if (!playerTurn) return;

            bossHp -= getNormalAttackDamage();
            showFloatingText(imgBoss, "-", Color.RED);
            onBossHit();
            updateHpUI();

            if (bossHp <= 0) {
                winBattle();
                return;
            }

            playerTurn = false;
            enemyTurn();
        });

        // chọn thẻ
        btnPickCard.setOnClickListener(v -> {
            if (!playerTurn) return;
            showPickCardDialog();
        });
    }

    // ===== TURN SYSTEM =====
    protected void enemyTurn() {
        new Handler(Looper.getMainLooper()).postDelayed(() -> {

            mainHp -= getEnemyDamage();
            onPlayerHit();
            showFloatingText(imgBoss, "-", Color.RED);
            updateHpUI();

            if (mainHp <= 0) {
                loseBattle();
                return;
            }

            playerTurn = true;

        }, 1000);
    }

    // ===== CARD EFFECT =====
    protected void useCardEffect(String cardName) {

        switch (cardName) {
            case "Kiếm thuật":
                bossHp -= 2;
                onBossHit();
                showFloatingText(imgBoss, "-1", Color.RED);
                break;

            case "Hỏa cầu":
                playFireball(() -> {
                    bossHp -= 4;
                    updateHpUI();
                    onBossHit();
                    showFloatingText(imgBoss, "-3", Color.RED);
                });
                break; // ⚠️ rất quan trọng

            case "Hồi phục":
                mainHp += 4;
                onHeal();
                showFloatingText(imgPlayer, "+4", Color.GREEN);
                break;

            case "Hatsune Miku":
                mainHp += 8;
                onHeal();
                showFloatingText(imgPlayer, "+8", Color.GREEN);
                break;
        }

        updateHpUI();

        if (bossHp <= 0) {
            winBattle();
            return;
        }
        if (mainHp <= 0) {
            loseBattle();
            return;
        }

        playerTurn = false;
        enemyTurn();
    }

    // ===== HOOKS (CÁC MÀN CÓ THỂ GHI ĐÈ) =====
    protected int getNormalAttackDamage() { return 1; }
    protected int getEnemyDamage() { return 2; }

    protected void onBossHit() {
        shakeView(imgBoss);
    }

    protected void onPlayerHit() {
        shakeView(imgPlayer);
    }

    protected void onHeal() { }

    // ===== ABSTRACT (MỖI MÀN TỰ XỬ LÝ) =====
    protected abstract void winBattle();
    protected void loseBattle(){
        Intent i = new Intent(this, LoseActivity.class);
        i.putExtra("username", username);
        startActivity(i);
        finish();
    };

    // ===== ANIMATION CHUNG =====
    protected void shakeView(View v) {
        v.animate()
                .translationXBy(15f)
                .setDuration(50)
                .withEndAction(() -> v.setTranslationX(0))
                .start();
    }
    private void showFloatingText(View anchor, String text, int color) {

        TextView tv = new TextView(this);
        tv.setText(text);
        tv.setTextSize(20f);
        tv.setTextColor(color);

        FrameLayout root = findViewById(android.R.id.content);
        root.addView(tv);

        int[] loc = new int[2];
        anchor.getLocationOnScreen(loc);

        tv.setX(loc[0] + anchor.getWidth() / 2f);
        tv.setY(loc[1]);

        tv.animate()
                .translationYBy(-100f)
                .alpha(0f)
                .setDuration(800)
                .withEndAction(() -> root.removeView(tv))
                .start();
    }

    protected void showPickCardDialog(){
        try {
            View v = getLayoutInflater().inflate(R.layout.dialog_pick_card, null);

            LinearLayout container = v.findViewById(R.id.cardContainer);
            Button btnUse = v.findViewById(R.id.btnUseCard);

            if (container == null || btnUse == null) {
                Toast.makeText(this, "Lỗi layout dialog", Toast.LENGTH_SHORT).show();
                return;
            }

            selectedCard = null;
            container.removeAllViews();

            Cursor c = db.getUserCards(username);
            if (c == null || c.getCount() == 0) {
                Toast.makeText(this, "Bạn chưa có thẻ nào", Toast.LENGTH_SHORT).show();
                if (c != null) c.close();
                return;
            }

            if (c.moveToFirst()) {
                do {
                    String cardName = c.getString(
                            c.getColumnIndexOrThrow("name"));
                    String imageName = c.getString(
                            c.getColumnIndexOrThrow("image"));

                    View cardView = getLayoutInflater()
                            .inflate(R.layout.item_pick_card, container, false);

                    ImageView img = cardView.findViewById(R.id.imgPickCard);
                    TextView txt = cardView.findViewById(R.id.txtPickCardName);

                    txt.setText(cardName);

                    int imgRes = 0;
                    if (imageName != null && !imageName.isEmpty()) {
                        imgRes = getResources()
                                .getIdentifier(imageName, "drawable", getPackageName());
                    }

                    if (imgRes != 0) {
                        img.setImageResource(imgRes);
                    } else {
                        img.setImageResource(R.drawable.backcardwood);
                    }

                    if (usedCards.contains(cardName)) {
                        cardView.setAlpha(0.4f);
                    } else {
                        cardView.setOnClickListener(v1 -> {
                            for (int i = 0; i < container.getChildCount(); i++) {
                                container.getChildAt(i).setBackground(null);
                            }
                            cardView.setBackgroundColor(0x5533B5E5);
                            selectedCard = cardName;
                        });
                    }

                    container.addView(cardView);

                } while (c.moveToNext());
            }
            c.close();

            AlertDialog dialog = new AlertDialog.Builder(this)
                    .setTitle("Chọn thẻ")
                    .setView(v)
                    .setCancelable(true)
                    .create();

            btnUse.setOnClickListener(v1 -> {
                if (selectedCard == null) {
                    Toast.makeText(this, "Hãy chọn 1 thẻ", Toast.LENGTH_SHORT).show();
                    return;
                }

                useCardEffect(selectedCard);
                usedCards.add(selectedCard);
                dialog.dismiss();
            });

            dialog.show();

        } catch (Exception e) {
            Log.e("CARD_DIALOG", "Dialog error", e);
            Toast.makeText(this, e.getClass().getSimpleName(), Toast.LENGTH_LONG).show();
        }
    }

    protected void playFireball(Runnable onHit) {
        ImageView fireball = findViewById(R.id.imgFireball);

        int[] from = new int[2];
        int[] to = new int[2];

        imgPlayer.getLocationOnScreen(from);
        imgBoss.getLocationOnScreen(to);

        fireball.setX(from[0]);
        fireball.setY(from[1]);
        fireball.setVisibility(View.VISIBLE);

        fireball.animate()
                .x(to[0])
                .y(to[1])
                .setDuration(500)
                .withEndAction(() -> {
                    fireball.setVisibility(View.GONE);
                    onHit.run();
                })
                .start();
    }
}
