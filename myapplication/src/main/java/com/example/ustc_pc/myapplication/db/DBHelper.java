package com.example.ustc_pc.myapplication.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.example.ustc_pc.myapplication.unit.Strings;

/**
 * Created by ustc-pc on 2015/2/25.
 */
public class DBHelper extends SQLiteOpenHelper {

    private static final int DB_VERSION = 1;
    private static DBHelper dbHelper;

    private DBHelper(Context context){
        super(context, Strings.DB_NAME, null, DB_VERSION);
    }

    public synchronized static DBHelper getInstance(Context context){
        if(dbHelper == null){
            dbHelper = new DBHelper(context);
        }
        return dbHelper;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(Strings.CREATE_LOG_QUESTIONS_TABLE);
        db.execSQL(Strings.CREATE_HISTORY_PAPERS_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
//        db.execSQL("DROP TABLE IF　EXISTS" + Strings.TABLE_NAME_LOG_QUESTIONS);
//        db.execSQL("DROP TABLE IF　EXISTS" + Strings.TABLE_NAME_HISTORY_PAPERS);
//        onCreate(db);

    }

    public synchronized static void destoryInstance(){
        if(dbHelper != null){
            dbHelper.close();
        }
    }
}
