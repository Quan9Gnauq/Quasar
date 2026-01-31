package com.example.quasar;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class csdl extends SQLiteOpenHelper {
    private static final String DB_NAME = "CardGame.db";
    private static final int DB_VERSION = 2;

    public csdl(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(
                "CREATE TABLE users (" +
                        "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                        "username TEXT UNIQUE," +
                        "password TEXT," +
                        "player_name TEXT," +
                        "level INTEGER," +
                        "crystal INTEGER)"
        );
        db.execSQL(
                "CREATE TABLE cards (" +
                        "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                        "name TEXT," +
                        "image TEXT," +
                        "rarity TEXT)"
        );
        db.execSQL(
                "CREATE TABLE user_cards (" +
                        "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                        "username TEXT," +
                        "card_id INTEGER," +
                        "UNIQUE(username, card_id))"
        );
    }


    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

        if (oldVersion < 2) {
            db.execSQL("ALTER TABLE users ADD COLUMN crystal INTEGER DEFAULT 0");
        }
    }

    // đăng ký: level mặc định = 0
    public boolean register(String username, String password) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put("username", username);
        cv.put("password", password);
        cv.put("level", 0);
        cv.put("crystal", 0);

        long result = db.insert("users", null, cv);
        return result != -1;
    }

    // kiểm tra đăng nhập
    public Cursor login(String username, String password) {
        SQLiteDatabase db = this.getReadableDatabase();
        return db.rawQuery(
                "SELECT * FROM users WHERE username=? AND password=?",
                new String[]{username, password}
        );
    }

    // cập nhật tên người chơi + level
    public void updatePlayer(String username, String playerName, int level) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put("player_name", playerName);
        cv.put("level", level);

        db.update("users", cv, "username=?", new String[]{username});
    }
    public Cursor getUserInfo(String username) {
        SQLiteDatabase db = this.getReadableDatabase();
        return db.rawQuery(
                "SELECT player_name, level FROM users WHERE username=?",
                new String[]{username}
        );
    }

    public int getCrystal(String username) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor c = db.rawQuery(
                "SELECT crystal FROM users WHERE username=?",
                new String[]{username}
        );
        int crystal = 0;
        if (c.moveToFirst()) {
            crystal = c.getInt(0);
        }
        c.close();
        return crystal;
    }

    // cập nhật crystal
    public void updateCrystal(String username, int crystal) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put("crystal", crystal);
        db.update("users", cv, "username=?", new String[]{username});
    }
    // thêm card
    public void insertCard(String name, String image, String rarity) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put("name", name);
        cv.put("image", image);
        cv.put("rarity", rarity);
        db.insert("cards", null, cv);
    }
    // rut the theo rate
    public Cursor getRandomCardByRarity(String rarity) {
        SQLiteDatabase db = this.getReadableDatabase();
        return db.rawQuery(
                "SELECT * FROM cards WHERE rarity=? ORDER BY RANDOM() LIMIT 1",
                new String[]{rarity}
        );
    }
    // kt the trung
    public boolean hasCard(String username, int cardId) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor c = db.rawQuery(
                "SELECT 1 FROM user_cards WHERE username=? AND card_id=?",
                new String[]{username, String.valueOf(cardId)}
        );
        boolean exists = c.moveToFirst();
        c.close();
        return exists;
    }
    // luu card
    public void addUserCard(String username, int cardId) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put("username", username);
        cv.put("card_id", cardId);
        db.insert("user_cards", null, cv);
    }
    // lay the da so huu
    public Cursor getUserCards(String username) {
        SQLiteDatabase db = this.getReadableDatabase();
        return db.rawQuery(
                "SELECT c.id, c.name, c.image, c.rarity " +
                        "FROM cards c " +
                        "INNER JOIN user_cards uc ON c.id = uc.card_id " +
                        "WHERE uc.username = ?",
                new String[]{username}
        );
    }
}
