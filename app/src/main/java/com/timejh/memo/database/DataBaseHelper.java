package com.timejh.memo.database;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.timejh.memo.Logger;

/**
 * Created by tokijh on 2017. 2. 14..
 */

public class DataBaseHelper  extends SQLiteOpenHelper {

    public DataBaseHelper(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE `memo` (\n" +
                "     `id` INTEGER PRIMARY KEY AUTOINCREMENT ,\n" +
                "     `title` varchar(255) ,\n" +
                "     `content` text ,\n" +
                "     `date` varchar(255) \n" +
                "     );");
        db.execSQL("CREATE TABLE `imagedata`(\n" +
                "     `id` INTEGER PRIMARY KEY AUTOINCREMENT ,\n" +
                "     `image` varchar(255) \n" +
                "     );"
        );
        db.execSQL("CREATE TABLE `imagematch` (\n"+
                "     `id` INTEGER PRIMARY KEY AUTOINCREMENT ,\n" +
                "     `memo_id` INTEGER ,\n" +
                "     `image_id` INTEGER,\n" +
                "     FOREIGN KEY (memo_id) REFERENCES memo(id) ON DELETE CASCADE ON UPDATE CASCADE,\n" +
                "     FOREIGN KEY (image_id) REFERENCES imagedata(id) ON DELETE CASCADE ON UPDATE CASCADE\n" +
                "     );"
        );
//        db.execSQL("CREATE TABLE `memo` (\n" +
//                "     `id` bigint(20) PRIMARY KEY NOT NULL ,\n" +
//                "     `title` varchar(255) NOT NULL,\n" +
//                "     `content` text NOT NULL,\n" +
//                "     `date` varchar(255) NOT NULL\n" +
//                "     );\n" +
//                "\n" +
//                "     CREATE TABLE `image`(\n" +
//                "     `id` bigint(20) PRIMARY KEY NOT NULL ,\n" +
//                "     `memo_id` bigint(20) PRIMARY KEY NOT NULL,\n" +
//                "     FOREIGN KEY (memo_id) REFERENCES memo(id) ON DELETE CASCADE ON UPDATE CASCADE\n" +
//                "     );");
        Logger.d("ONCREATE", "ONCREATE");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }

    public void writableQuery(String query) {
        SQLiteDatabase db = getWritableDatabase();
        db.execSQL(query);
        db.close();
    }

    public String readableQuery(String query, int columCount) {
        // 읽기가 가능하게 DB 열기
        SQLiteDatabase db = getReadableDatabase();
        String result = "";

        // DB에 있는 데이터를 쉽게 처리하기 위해 Cursor를 사용하여 테이블에 있는 모든 데이터 출력
        Cursor cursor = db.rawQuery(query, null);
        while (cursor.moveToNext()) {
            for(int i=0;i<columCount;i++) {
                result += cursor.getString(i) + "|";
            }
            result += "--next--";
        }

        return result;
    }

    /**
     *
     CREATE TABLE `memo` (
     `id` bigint(20) NOT NULL AUTOINCREMENT,
     `title` varchar(255) NOT NULL,
     `content` text NOT NULL,
     `date` varchar(255) NOT NULL
     ) DEFAULT CHARSET=utf8;

     CREATE TABLE `image`(
     `id` bigint(20) NOT NULL AUTOINCREMENT,
     `memo_id` bigint(20) unsigned NOT NULL,
     UNIQUE KEY (`id`, `memo_id`),
     FOREIGN KEY (memo_id) REFERENCES memo(id) ON DELETE CASCADE ON UPDATE CASCADE
     ) DEFAULT CHARSET=utf8;
     */
}
