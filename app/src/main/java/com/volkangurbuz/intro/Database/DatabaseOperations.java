package com.volkangurbuz.intro;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by volkan on 02.05.2017.
 */

public class DatabaseOperations extends SQLiteOpenHelper {


    private static final int DATABASE_VERSION = 1;
    private static final String DATABASE_NAME = "user.db";
    public static final String TABLE_NAME = "users";

    public static final String COLUMN_ID = "userid";
    public static final String COLUMN_ADSOYAD = "useradi";
    public static final String COLUMN_YAS = "useryas";
    public static final String COLUMN_CINSIYET = "usercinsiyet";

    public static final String COLUMN_KSANAT = "userksanat";
    public static final String COLUMN_BTEKNOLOJI = "userbteknoloji";
    public static final String COLUMN_ORTAM = "userortam";

    public static final String CREATE_TABLE = "CREATE TABLE " + TABLE_NAME + "(" + COLUMN_ID + " INTEGER PRIMARY KEY autoincrement, "
            + COLUMN_ADSOYAD + " TEXT not null, " + COLUMN_YAS + " TINYINY not null, " + COLUMN_CINSIYET + " TEXT not null, "
            + COLUMN_KSANAT + " TINYINT, " + COLUMN_BTEKNOLOJI + " TINYINT, "
            + COLUMN_ORTAM + " TINYINT" + ")";


    public DatabaseOperations(Context context, Object name,
                              Object factory, int version) {
        // TODO Auto-generated constructor stub
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {

        sqLiteDatabase.execSQL(CREATE_TABLE);

    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {

        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
        this.onCreate(sqLiteDatabase);


    }


    public boolean uyeEkle(User u) {

        try {

            SQLiteDatabase db = this.getWritableDatabase();
            ContentValues values = new ContentValues();

            values.put(COLUMN_ADSOYAD, u.getUserAd());
            values.put(COLUMN_YAS, u.getUserYas());
            values.put(COLUMN_CINSIYET, u.getUserCinsiyet());

            values.put(COLUMN_KSANAT, u.getUserKsanat());
            values.put(COLUMN_BTEKNOLOJI, u.getUserBTeknoloji());
            values.put(COLUMN_ORTAM, u.getUserOrtam());

            db.insert(TABLE_NAME, null, values);

            db.close();

            return true;

        } catch (SQLiteException sq) {
            Log.d("Hata olustu !", sq.getMessage());

            return false;

        }

    }

    public User getTheUser(String name) {

        String selectQuery = "SELECT  * FROM " + TABLE_NAME + " WHERE " + COLUMN_ADSOYAD + " = " + name;

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);
        User user = new User();
        for (cursor.moveToFirst(); !cursor.isAfterLast(); cursor.moveToNext()) {
            user.setUserid(Integer.parseInt(cursor.getString(0)));
            user.setUserAd(cursor.getString(1));
            user.setUserYas(Integer.parseInt(cursor.getString(2)));
            user.setUserCinsiyet(cursor.getString(3));
            user.setUserKsanat(Integer.parseInt(cursor.getString(4)));
            user.setUserBTeknoloji(Integer.parseInt(cursor.getString(5)));
            user.setUserBTeknoloji(Integer.parseInt(cursor.getString(6)));
        }


        return user;
    }


    public boolean updateUser(String name, int ksanatPuani, int bteknolojiPuani, int ortamPuani) {
        try {
            String strSQL = "UPDATE " + TABLE_NAME + " SET " + COLUMN_KSANAT + " = " + ksanatPuani + " , " + COLUMN_BTEKNOLOJI + " =  '" + bteknolojiPuani + "'" +
                    COLUMN_ORTAM + " =  '" + ortamPuani + "'" +
                    " WHERE " + COLUMN_ADSOYAD + " = " + name;

            SQLiteDatabase myDataBase = this.getWritableDatabase();

            myDataBase.execSQL(strSQL);
            return true;

        } catch (SQLException s) {
            return false;
        }

    }


}
