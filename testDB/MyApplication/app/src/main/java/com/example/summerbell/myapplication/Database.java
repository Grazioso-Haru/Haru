package com.example.summerbell.myapplication;

import android.content.Context;
import android.content.ContentValues;

import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import android.util.*;
import java.util.*;
import java.text.SimpleDateFormat;

/**
 * Created by summerbell on 16. 7. 16..
 */
public class Database {
    public static final String _DATABASENMAE = "HaruDB";
    public static final String _TABLENAME = "pins";
    public static final int DATABASE_VERSION = 1;

    public static final String btn_ID = "btn_id";
    public static final String DATE = "date";
    public static final String TIME = "time";
    public static final String LONG = "long";
    public static final String LAT = "lat";
    public static final String COMMENT = "comment";
    public static final String _CREATE =
            "create table "+_TABLENAME+" ("
                    + btn_ID + " integer primary key autoincrement, "
                    + DATE + " TEXT not null, "
                    + TIME + " TEXT, "
                    + LONG + " REAL, "
                    + LAT + " REAL, "
                    + COMMENT + " TEXT);";

    private Context mCtx;
    private DatabaseHelper mDbHelper;
    private SQLiteDatabase mDb;

    public static class DatabaseHelper extends SQLiteOpenHelper{
        DatabaseHelper(Context context) {
            super(context, _DATABASENMAE, null, DATABASE_VERSION);
        }

        @Override
        public void onCreate(SQLiteDatabase db) {
            //데이터베이스 최초 생성될때 실행 디비가 생성될때 실행된다
            Log.d("TEST","onCreate DATABSE_CREATE");
            db.execSQL(_CREATE);
        }

        @Override
        //데이터베이스가 업그레이드가 필요할때
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            //db.execSQL( SQL_DELETE_TABLE);
        }


    }

    public void open() throws SQLException {
        mDbHelper = new DatabaseHelper(mCtx);
        mDb = mDbHelper.getWritableDatabase();
        Log.d("TEST", "open");
    }

    public Database(Context ctx) {
        this.mCtx = ctx;
    }

    public void close() {
        mDbHelper.close();
    }

    public void insert(String comment) {
        ContentValues insertValues = new ContentValues();
        Log.d("Test", comment);

        Date date = new Date();
        String sdf = new SimpleDateFormat("yyyyMMdd hhmmss").format(date);
        System.out.println(comment);
        System.out.println(sdf);
        insertValues.put(DATE, sdf.substring(0, 8));
        insertValues.put(TIME, sdf.substring(9, 15));
        insertValues.put(LONG, 1);
        insertValues.put(LAT, 1);
        insertValues.put(COMMENT, comment);
        Log.d("TEST", "insert suc");

        mDb.insert(_TABLENAME, null, insertValues);
    }
}
