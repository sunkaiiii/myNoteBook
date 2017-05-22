package com.example.sunkai.mynotebook;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by sunkai on 2016/12/14.
 */

public class mySqlLite extends SQLiteOpenHelper {
    public mySqlLite(Context context, String name, SQLiteDatabase.CursorFactory factory, int version)
    {
        super(context,name,factory,version);
    }
    public void onCreate(SQLiteDatabase db)
    {
        db.execSQL("create table if not exists registe_info("
                +"id integer primary key,"
                +"name varchar,"
                +"password varchar,"
                +"findPasswordQuestion varchar,"
                +"findPasswordAnswer varchar,"
                +"category int)"
        );
        db.execSQL("create table if not exists login_info("
                +"id integer primary key,"
                +"loginname varchar,"
                +"loginpassword varchar,"
                +"autologin int)"
        );
        db.execSQL("create table notesbook ("
                +"id integer primary key,"
                +"username varchar,"
                +"title varchar,"
                +"content varchar,"
                +"img varchar,"
                +"vedio varchar,"
                +"time varchar)"
        );
    }
    public  void onUpgrade(SQLiteDatabase db,int oldVersion,int newVersion)
    {}
}
