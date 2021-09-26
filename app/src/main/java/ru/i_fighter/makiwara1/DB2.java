package ru.i_fighter.makiwara1;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by X550 on 21.01.2017.
 */

public class DB2 {
    private static final String DB_NAME2 = "mydb2";
    private static final int DB_VERSION = 1;
    private static final String DB_TABLE2 = "mytab2";

    public static final String COLUMN_ID = "_id";
    public static final String COLUMN_POWER = "";
    public static final String COLUMN_TIME_POWER = "";
    public static final String COLUMN_TIME = "";

    private static final String DB_CREATE2 =
            "create table " + DB_TABLE2 + "(" +
                    COLUMN_ID + " integer primary key autoincrement, " +
                    COLUMN_POWER + " text, " +
                    COLUMN_TIME_POWER + " text" +
                    COLUMN_TIME + " text" +
                    ");";

    private final Context mCtx;


    private DBHelper mDBHelper;
    private SQLiteDatabase mDB;

    public DB2(Context ctx) {
        mCtx = ctx;
    }

    // открыть подключение
    public void open() {
        mDBHelper = new DBHelper(mCtx, DB_NAME2, null, DB_VERSION);
        mDB = mDBHelper.getWritableDatabase();
    }

    // закрыть подключение
    public void close() {
        if (mDBHelper!=null) mDBHelper.close();
    }

    // получить все данные из таблицы DB_TABLE2
    public Cursor getAllData() {
        return mDB.query(DB_TABLE2, null, null, null, null, null, null);
    }

    // добавить запись в DB_TABLE
   /* public void addRec(String txt, int img) {
        ContentValues cv = new ContentValues();
        cv.put(COLUMN_TXT, txt);
        cv.put(COLUMN_IMG, img);
        mDB.insert(DB_TABLE, null, cv);
    }*/
    public void addRec(int power, int power_time, int time) {
        ContentValues cv = new ContentValues();
        cv.put(COLUMN_POWER, power);
        cv.put(COLUMN_TIME_POWER, power_time);
        cv.put(COLUMN_TIME, time);
        mDB.insert(DB_TABLE2, null, cv);
    }



    // удалить запись из DB_TABLE
    public void delRec(long id) {
        mDB.delete(DB_TABLE2, COLUMN_ID + " = " + id, null);
    }

    // класс по созданию и управлению БД
    private class DBHelper extends SQLiteOpenHelper {

        public DBHelper(Context context, String name, SQLiteDatabase.CursorFactory factory,
                        int version) {
            super(context, name, factory, version);
        }

        // создаем и заполняем БД
        @Override
        public void onCreate(SQLiteDatabase db) {
            db.execSQL(DB_CREATE2);

            ContentValues cv = new ContentValues();
            //for (int i = 1; i < 5; i++) {
            //  cv.put(COLUMN_TXT, "sometext " + i);
            //cv.put(COLUMN_IMG, R.drawable.ic_my_launcher96);
            //cv.put(COLUMN_IMG, "second");
            //db.insert(DB_TABLE, null, cv);
            //}
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        }


    }
}
