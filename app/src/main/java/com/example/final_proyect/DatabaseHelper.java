package com.example.final_proyect;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DatabaseHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "users.db";
    private static final int DATABASE_VERSION = 1;
    private static final String TABLE_USERS = "users";
    private static final String COLUMN_ID = "id";
    private static final String COLUMN_USERNAME = "username";
    private static final String COLUMN_PASSWORD = "password";
    private static final String COLUMN_MAX_SCORE = "max_score";

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String createTable = "CREATE TABLE users (id INTEGER PRIMARY KEY AUTOINCREMENT, username TEXT, password TEXT, max_score INTEGER DEFAULT 0)";
        db.execSQL(createTable);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_USERS);
        onCreate(db);
    }

    public boolean addUser(String username, String password) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_USERNAME, username);
        values.put(COLUMN_PASSWORD, password);

        long result = db.insert(TABLE_USERS, null, values);
        db.close();
        return result != -1;
    }

    public boolean checkUser(String username, String password) {
        SQLiteDatabase db = this.getReadableDatabase();
        String[] columns = {COLUMN_ID};
        String selection = COLUMN_USERNAME + "=? AND " + COLUMN_PASSWORD + "=?";
        String[] selectionArgs = {username, password};

        Cursor cursor = db.query(TABLE_USERS, columns, selection, selectionArgs, null, null, null);
        int count = cursor.getCount();
        cursor.close();
        db.close();
        return count > 0;
    }

    public void updateMaxScore(String username, int newScore) {
        SQLiteDatabase db = this.getWritableDatabase();

        String query = "SELECT max_score FROM users WHERE username = ?";
        Cursor cursor = db.rawQuery(query, new String[]{username});

        if (cursor.moveToFirst()) {
            int currentMax = cursor.getInt(0);
            if (newScore > currentMax) {
                ContentValues values = new ContentValues();
                values.put(COLUMN_MAX_SCORE, newScore);
                db.update(TABLE_USERS, values, COLUMN_USERNAME + "=?", new String[]{username});
            }
        }
        cursor.close();
        db.close();
    }

    public int getMaxScore(String username) {
        SQLiteDatabase db = this.getReadableDatabase();
        String query = "SELECT max_score FROM users WHERE USERNAME = ?";
        Cursor cursor = db.rawQuery(query, new String[]{username});

        int maxScore = 0;
        if (cursor.moveToFirst()) {
            maxScore = cursor.getInt(0);
        }
        cursor.close();
        db.close();
        return maxScore;
    }
}
