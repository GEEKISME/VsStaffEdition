package com.biotag.vsstaffedition;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.biotag.vsstaffedition.NFC.Constants;

/**
 * Created by Lxh on 2017/10/31.
 */

public class MydatabaseHelper extends SQLiteOpenHelper {

    private static final String DB_NAME = "StaffBook.db";
    private Context mcontext;
    public static volatile MydatabaseHelper mInstance ;
    public static final String CREAT_BOOK = "create table Book("  // 建一张名为Book的表
            +"id integer primary key autoincrement,"
            +"staffid text,"
            +"chipnumber text,"
            +"imgpath text,"
            +"iscomein text,"
            +"approachtime text)";

    private MydatabaseHelper(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, DB_NAME, factory, 1);
        mcontext = context;
    }


    public static  MydatabaseHelper getInstance(Context context){
        if(mInstance==null){
            synchronized (MydatabaseHelper.class){
                if(mInstance==null){
                    mInstance = new MydatabaseHelper(context.getApplicationContext(),"StaffBook.db",null,1);
                }
            }
        }
        return mInstance;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREAT_BOOK);
//        Toast.makeText(mcontext, "建表成功", Toast.LENGTH_SHORT).show();
        Log.i(Constants.TAG,"suc");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}
