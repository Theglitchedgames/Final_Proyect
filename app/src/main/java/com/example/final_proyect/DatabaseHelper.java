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
    private static final String COLUMN_MAX_SCORE_GAME1 = "max_score_game1";
    private static final String COLUMN_MAX_SCORE_GAME2 = "max_score_game2";

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String createTable = "CREATE TABLE " + TABLE_USERS + " (" + COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " + COLUMN_USERNAME + " TEXT, " + COLUMN_PASSWORD + " TEXT, " + COLUMN_MAX_SCORE_GAME1 + " INTEGER DEFAULT 0, " + COLUMN_MAX_SCORE_GAME2 + " INTEGER DEFAULT 0)";
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

    /**
     * Actualiza la puntuación máxima del juego 1 (2048) para un usuario específico.
     * Solo actualiza si la nueva puntuación es mayor que la existente.
     *
     * @param username Nombre de usuario
     * @param newScore Nueva puntuación
     */
    public void updateMaxScoreGame1(String username, long newScore) {
        SQLiteDatabase db = this.getWritableDatabase();

        String query = "SELECT " + COLUMN_MAX_SCORE_GAME1 + " FROM " + TABLE_USERS + " WHERE " + COLUMN_USERNAME + " =?";
        Cursor cursor = db.rawQuery(query, new String[]{username});

        if (cursor.moveToFirst()) {
            long currentMax = cursor.getLong(0);
            if (newScore > currentMax) {
                ContentValues values = new ContentValues();
                values.put(COLUMN_MAX_SCORE_GAME1, newScore);
                db.update(TABLE_USERS, values, COLUMN_USERNAME + "=?", new String[]{username});
            }
        }
        cursor.close();
        db.close();
    }

    /**
     * Actualiza la puntuación máxima del juego 2 (Galacta) para un usuario específico.
     * Solo actualiza si la nueva puntuación es mayor que la existente.
     *
     * @param username Nombre de usuario
     * @param newScore Nueva puntuación
     */
    public void updateMaxScoreGame2(String username, int newScore) {
        SQLiteDatabase db = this.getWritableDatabase();

        String query = "SELECT " + COLUMN_MAX_SCORE_GAME2 + " FROM " + TABLE_USERS + " WHERE " + COLUMN_USERNAME + " = ?";
        Cursor cursor = db.rawQuery(query, new String[]{username});

        if (cursor.moveToFirst()) {
            int currentMax = cursor.getInt(0);
            if (newScore > currentMax) {
                ContentValues values = new ContentValues();
                values.put(COLUMN_MAX_SCORE_GAME2, newScore);
                db.update(TABLE_USERS, values, COLUMN_USERNAME + "=?", new String[]{username});
            }
        }
        cursor.close();
        db.close();
    }

    /**
     * Obtiene la puntuación máxima del juego 1 (2048) para un usuario específico.
     *
     * @param username Nombre de usuario
     * @return Puntuación máxima del juego 1
     */
    public long getMaxScoreGame1(String username) {
        SQLiteDatabase db = this.getReadableDatabase();
        String query = "SELECT " + COLUMN_MAX_SCORE_GAME1 + " FROM " + TABLE_USERS + " WHERE " + COLUMN_USERNAME + " = ?";
        Cursor cursor = db.rawQuery(query, new String[]{username});

        long maxScore = 0;
        if (cursor.moveToFirst()) {
            maxScore = cursor.getLong(0);
        }
        cursor.close();
        db.close();
        return maxScore;
    }

    /**
     * Obtiene la puntuación máxima del juego 2 (Galacta) para un usuario específico.
     *
     * @param username Nombre de usuario
     * @return Puntuación máxima del juego 2
     */
    public int getMaxScoreGame2(String username) {
        SQLiteDatabase db = this.getReadableDatabase();
        String query = "SELECT " + COLUMN_MAX_SCORE_GAME2 + " FROM " + TABLE_USERS + " WHERE " + COLUMN_USERNAME + " = ?";
        Cursor cursor = db.rawQuery(query, new String[]{username});

        int maxScore = 0;
        if (cursor.moveToFirst()) {
            maxScore = cursor.getInt(0);
        }
        cursor.close();
        db.close();
        return maxScore;
    }

    /**
     * Método de compatibilidad con el código anterior.
     * Actualiza la puntuación del juego 2 (Galacta).
     */
    public void updateMaxScore(String username, int newScore) {
        updateMaxScoreGame2(username, newScore);
    }

    /**
     * Método de compatibilidad con el código anterior.
     * Obtiene la puntuación del juego 2 (Galacta).
     */
    public int getMaxScore(String username) {
        return getMaxScoreGame2(username);
    }
}