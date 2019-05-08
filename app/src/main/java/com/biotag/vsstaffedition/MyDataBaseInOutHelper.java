package com.biotag.vsstaffedition;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.biotag.vsstaffedition.NFC.Constants;

/**
 * Created by Lxh on 2017/11/14.
 */

public class MyDataBaseInOutHelper extends SQLiteOpenHelper {

    private static final String DB_NAME = "InandOut.db";
    private Context mcontext;
    public static volatile MyDataBaseInOutHelper mInstance;

    public static final String CREAT_BOOK = "create table Inoutinfo("
            +"id integer primary key autoincrement,"
            +"StaffID text,"
            +"ChipCode text,"
            +"AreaNo text,"
            +"Action_Type text,"
            +"ActionTime text)";

    private MyDataBaseInOutHelper(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
        mcontext = context;
    }

    public static MyDataBaseInOutHelper getInstance(Context context){
        if(mInstance==null){
            synchronized (MyDataBaseInOutHelper.class){
                if(mInstance ==null){
                    mInstance = new MyDataBaseInOutHelper(context.getApplicationContext(),"InandOut.db",null,1);
                }
            }
        }
        return mInstance;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREAT_BOOK);
        Log.i(Constants.TAG,"create table suc");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}
