package com.zybooks.thebanddatabase;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.Nullable;

import java.util.ArrayList;
import java.util.List;

public class LogInSQL extends SQLiteOpenHelper {
    private static final String DATABASE_NAME = "logIn.db";
    private static final int VERSION = 1;

    public LogInSQL(Context context) {
        super(context, DATABASE_NAME, null, VERSION);
    }

    private static final class SQLTable {
        private static final String TABLE = "logInTable";
        private static final String COL_ID = "_id";
        private static final String COL_AAA = "aaa";
        private static final String COL_NAME = "name";
        private static final String COL_PWORD = "pass";
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(
                "create table " + SQLTable.TABLE + " (" +
                        SQLTable.COL_ID + " integer primary key autoincrement, " +
                        SQLTable.COL_AAA + " text, " +
                        SQLTable.COL_NAME + " text, " +
                        SQLTable.COL_PWORD + " text)"
        );
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int i, int i1) {
        db.execSQL("drop table if exists " + SQLTable.TABLE);
        onCreate(db);
    }

    public long addUser(String name, String pWord) {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(SQLTable.COL_AAA, "aaa");
        values.put(SQLTable.COL_NAME, name);
        values.put(SQLTable.COL_PWORD, pWord);

        long rawID = db.insert(SQLTable.TABLE, null, values);
        return rawID;
    }

    public boolean getData(String uName) {
        boolean existOrNot = false;
        SQLiteDatabase db = getWritableDatabase();

        String sql = "select * from " + SQLTable.TABLE + " where name = ? ";
        Cursor cursor = db.rawQuery(sql, new String[] {uName});
        if (cursor.moveToFirst()) {
            do {
                long id = cursor.getLong(0);
                String aaa = cursor.getString(1);
                String name = cursor.getString (2);
                String pWord = cursor.getString(3);
                //Log.d(TAG, "???????????????????????????? = " + id + ", " + timeLong);
                //timeList.add(timeLong);
                existOrNot = true;
            } while (cursor.moveToNext());
        }
        cursor.close();
        return existOrNot;
    }

}
