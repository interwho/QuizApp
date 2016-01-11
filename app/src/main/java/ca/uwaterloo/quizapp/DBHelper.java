package ca.uwaterloo.quizapp;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DBHelper extends SQLiteOpenHelper {
    public static final String DATABASE_NAME = "QuizApp.db";
    private static final int DATABASE_VERSION = 1;

    // Users Table
    public static final String USERS_TABLE_NAME = "users";
    public static final String USERS_COLUMN_ID = "_id";
    public static final String USERS_COLUMN_USERNAME = "username";
    public static final String USERS_COLUMN_PASSWORD = "password";

    // Questions Table
    public static final String QUESTIONS_TABLE_NAME = "questions";
    public static final String QUESTIONS_COLUMN_ID = "_id";
    public static final String QUESTIONS_COLUMN_Q = "question";
    public static final String QUESTIONS_COLUMN_A = "answer";
    public static final String QUESTIONS_COLUMN_I1 = "incorrect1";
    public static final String QUESTIONS_COLUMN_I2 = "incorrect2";
    public static final String QUESTIONS_COLUMN_I3 = "incorrect3";
    public static final String QUESTIONS_COLUMN_TIME = "allowed_time";

    // Stats Table
    public static final String STATS_TABLE_NAME = "stats";
    public static final String STATS_COLUMN_ID = "_id";
    public static final String STATS_COLUMN_UID = "uid";
    public static final String STATS_COLUMN_QID = "qid";
    public static final String STATS_COLUMN_SCORE = "score";

    public DBHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE " + USERS_TABLE_NAME + "(" +
                        USERS_COLUMN_ID + " INTEGER PRIMARY KEY, " +
                        USERS_COLUMN_USERNAME + " TEXT, " +
                        USERS_COLUMN_PASSWORD + " TEXT)"
        );

        db.execSQL("CREATE TABLE " + QUESTIONS_TABLE_NAME + "(" +
                        QUESTIONS_COLUMN_ID + " INTEGER PRIMARY KEY, " +
                        QUESTIONS_COLUMN_Q + " TEXT, " +
                        QUESTIONS_COLUMN_A + " TEXT, " +
                        QUESTIONS_COLUMN_I1 + " TEXT, " +
                        QUESTIONS_COLUMN_I2 + " TEXT, " +
                        QUESTIONS_COLUMN_I3 + " TEXT, " +
                        QUESTIONS_COLUMN_TIME + " INTEGER)"
        );

        db.execSQL("CREATE TABLE " + STATS_TABLE_NAME + "(" +
                        STATS_COLUMN_ID + " INTEGER PRIMARY KEY, " +
                        STATS_COLUMN_UID + " INTEGER, " +
                        STATS_COLUMN_QID + " INTEGER, " +
                        STATS_COLUMN_SCORE + " INTEGER)"
        );
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + USERS_TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS " + QUESTIONS_TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS " + STATS_TABLE_NAME);
        onCreate(db);
    }

    public boolean insertUser(String username, String password) {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(USERS_COLUMN_USERNAME, username);
        contentValues.put(USERS_COLUMN_PASSWORD, password);
        db.insert(USERS_TABLE_NAME, null, contentValues);
        return true;
    }

    public Cursor getUser(String username) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor res = db.rawQuery("SELECT * FROM " + USERS_TABLE_NAME + " WHERE " +
                USERS_COLUMN_USERNAME + "=?", new String[]{username});
        return res;
    }

    public Cursor getAllUsers() {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor res = db.rawQuery("SELECT * FROM " + USERS_TABLE_NAME, null);
        return res;
    }

    public Integer deleteUser(String username) {
        SQLiteDatabase db = this.getWritableDatabase();
        return db.delete(USERS_TABLE_NAME,
                USERS_COLUMN_USERNAME + " = ? ",
                new String[] { username });
    }

    public Cursor getAnswerByQuestion(String question) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor res = db.rawQuery("SELECT * FROM " + QUESTIONS_TABLE_NAME + " WHERE " +
                QUESTIONS_COLUMN_Q + "=?", new String[]{question});
        return res;
    }

    public Cursor getTotalStatsByUser(String uid) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor res = db.rawQuery("SELECT * FROM " + STATS_TABLE_NAME + " WHERE " +
                STATS_COLUMN_UID + "=?", new String[]{uid});
        return res;
    }

    public Cursor getCorrectStatsByUser(String uid) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor res = db.rawQuery("SELECT * FROM " + STATS_TABLE_NAME + " WHERE " +
                STATS_COLUMN_UID + "=? AND " + STATS_COLUMN_SCORE + "='1'", new String[]{uid});
        return res;
    }

    public boolean insertQuestion(String question, String answer, String i1, String i2, String i3, String time) {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(QUESTIONS_COLUMN_Q, question);
        contentValues.put(QUESTIONS_COLUMN_A, answer);
        contentValues.put(QUESTIONS_COLUMN_I1, i1);
        contentValues.put(QUESTIONS_COLUMN_I2, i2);
        contentValues.put(QUESTIONS_COLUMN_I3, i3);
        contentValues.put(QUESTIONS_COLUMN_TIME, time);
        db.insert(QUESTIONS_TABLE_NAME, null, contentValues);
        return true;
    }

    public Cursor getAllQuestions() {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor res = db.rawQuery("SELECT * FROM " + QUESTIONS_TABLE_NAME, null);
        return res;
    }

    public Cursor getRandomQuestion() {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor res = db.rawQuery("SELECT * FROM " + QUESTIONS_TABLE_NAME + " ORDER BY RANDOM() LIMIT 1", null);
        return res;
    }

    public Integer deleteQuestion(String id) {
        SQLiteDatabase db = this.getWritableDatabase();
        return db.delete(QUESTIONS_TABLE_NAME,
                QUESTIONS_COLUMN_ID + " = ? ",
                new String[] { id });
    }

    public boolean insertStat(String qid, String uid, String score) {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(STATS_COLUMN_QID, qid);
        contentValues.put(STATS_COLUMN_UID, uid);
        contentValues.put(STATS_COLUMN_SCORE, score);
        db.insert(STATS_TABLE_NAME, null, contentValues);
        return true;
    }

    public Cursor getAllStats() {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor res = db.rawQuery( "SELECT * FROM " + STATS_TABLE_NAME, null );
        return res;
    }

    public Integer deleteAllStatsByUser(String uid) {
        SQLiteDatabase db = this.getWritableDatabase();
        return db.delete(STATS_TABLE_NAME,
                STATS_COLUMN_UID + " = ? ",
                new String[] { uid });
    }
}
