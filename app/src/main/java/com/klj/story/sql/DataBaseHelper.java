package com.klj.story.sql;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.io.ByteArrayOutputStream;
import java.io.ObjectOutputStream;

/**
 * Created by 娱乐人物 on 2016/10/30.
 */
public class DataBaseHelper extends SQLiteOpenHelper {

    private static final String DB_NAME = "Storys.db";
    private static final int DB_VERSION = 1;

    private static final String SQL_CREATE_TABLE = "create table story(_id integer primary key autoincrement ,sid varchar(12) unique,storyInfo blod,readTime long)";


    public DataBaseHelper(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(SQL_CREATE_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }

    /**
     * 插入
     * @param table
     * @param values
     * @return
     */
    public long insert(String table,ContentValues values){
        long insert=-1;
        SQLiteDatabase sqLiteDatabase = getWritableDatabase();
        sqLiteDatabase.beginTransaction();
        try {
            insert= sqLiteDatabase.insert(table, null, values);
            sqLiteDatabase.setTransactionSuccessful();
            sqLiteDatabase.endTransaction();
        } finally {
            sqLiteDatabase.close();
        }
        return insert;
    }

    /**
     * 删除
     * @param table
     * @param whereClause
     * @param whereArgs
     * @return
     */
    public boolean delete(String table,String whereClause, String[] whereArgs){
        boolean flag = false;
        SQLiteDatabase sqLiteDatabase = getWritableDatabase();
        sqLiteDatabase.beginTransaction();
        try {
            int delete = sqLiteDatabase.delete(table, whereClause, whereArgs);
            flag = delete > 0;
            sqLiteDatabase.setTransactionSuccessful();
            sqLiteDatabase.endTransaction();
        } finally {
            sqLiteDatabase.close();
        }
        return flag;
    }

    /**
     * 修改
     * @param table
     * @param values
     * @param whereClause
     * @param whereArgs
     * @return
     */
    public boolean update(String table, ContentValues values, String whereClause,
                          String[] whereArgs) {
        boolean flag = false;
        SQLiteDatabase sqLiteDatabase = getWritableDatabase();
        sqLiteDatabase.beginTransaction();
        try {
            int update = sqLiteDatabase.update(table, values, whereClause, whereArgs);
            flag = update > 0;
            sqLiteDatabase.setTransactionSuccessful();
            sqLiteDatabase.endTransaction();
        } finally {
            sqLiteDatabase.close();
        }
        return flag;
    }

    /**
     * 查询
     * @param table
     * @param selection
     * @param selectionArgs
     * @return
     */
    public Cursor query(String table, String selection, String[] selectionArgs,String orderBy) {
        Cursor query=null;
        SQLiteDatabase sqLiteDatabase = getReadableDatabase();
        sqLiteDatabase.beginTransaction();
        try {
            query = sqLiteDatabase.query(table, null, selection, selectionArgs, null, null, orderBy);
            sqLiteDatabase.setTransactionSuccessful();
            sqLiteDatabase.endTransaction();
        }finally {
            sqLiteDatabase.close();
        }
        return query;
    }
}
