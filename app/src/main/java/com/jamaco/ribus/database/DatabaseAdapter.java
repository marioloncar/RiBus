package com.jamaco.ribus.database;

import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.widget.Toast;

/**
 * Created by mario on 11.09.15..
 */
public class DatabaseAdapter {

    DatabaseHelper helper;
    public DatabaseAdapter(Context context)
    {
        helper = new DatabaseHelper(context);
    }


    public void insertData(String name, String work1, String work1notice, String work2, String workd2notice, String sat1, String sat1notice, String sat2, String sat2notice, String sun1, String sun1notice, String sun2, String sun2notice){

        SQLiteDatabase db = helper.getWritableDatabase();

        String sqlQuery = "INSERT INTO "+DatabaseHelper.TABLE_NAME+" ("+DatabaseHelper.BUSNAME+","+DatabaseHelper.WORKDAY1+","+DatabaseHelper.WD1NOTICE+","+DatabaseHelper.WORKDAY2+","+DatabaseHelper.WD2NOTICE+","+DatabaseHelper.SATURDAY1+","+DatabaseHelper.SAT1NOTICE+","+DatabaseHelper.SATURDAY2+","+DatabaseHelper.SAT2NOTICE+","+DatabaseHelper.SUNDAY1+","+DatabaseHelper.SUN1NOTICE+","+DatabaseHelper.SUNDAY2+","+DatabaseHelper.SUN2NOTICE+") VALUES (\""+name+"\",\""+work1+"\",\""+work1notice+"\",\""+work2+"\",\""+workd2notice+"\",\""+sat1+"\",\""+sat1notice+"\",\""+sat2+"\",\""+sat2notice+"\",\""+sun1+"\",\""+sun1notice+"\",\""+sun2+"\",\""+sun2notice+"\");";

        db.execSQL(sqlQuery);
        db.close();
    }

    public void updateData(String name, String work1, String work1notice, String work2, String workd2notice, String sat1, String sat1notice, String sat2, String sat2notice, String sun1, String sun1notice, String sun2, String sun2notice){

        SQLiteDatabase db = helper.getWritableDatabase();

        String sqlQuery = "UPDATE "+DatabaseHelper.TABLE_NAME+" SET "+DatabaseHelper.WORKDAY1+"=\""+work1+"\","+DatabaseHelper.WD1NOTICE+"=\""+work1notice+"\","+DatabaseHelper.WORKDAY2+"=\""+work2+"\","+DatabaseHelper.WD2NOTICE+"=\""+workd2notice+"\","+DatabaseHelper.SATURDAY1+"=\""+sat1+"\","+DatabaseHelper.SAT1NOTICE+"=\""+sat1notice+"\","+DatabaseHelper.SATURDAY2+"=\""+sat2+"\","+DatabaseHelper.SAT2NOTICE+"=\""+sat2notice+"\","+DatabaseHelper.SUNDAY1+"=\""+sun1+"\","+DatabaseHelper.SUN1NOTICE+"=\""+sun1notice+"\","+DatabaseHelper.SUNDAY2+"=\""+sun2+"\"" +
                ","+DatabaseHelper.SUN2NOTICE+"=\""+sun2notice+"\" WHERE " + DatabaseHelper.BUSNAME +"=\""+name+"\";";

        db.execSQL(sqlQuery);
        db.close();
    }

    public String[] getWorkday1(String name){

        SQLiteDatabase db = helper.getReadableDatabase();
        String[] buffer = {};
        String values;

        String sqlQuery = "SELECT "+DatabaseHelper.WORKDAY1+" FROM "+DatabaseHelper.TABLE_NAME+" WHERE "+DatabaseHelper.BUSNAME+"=\""+name+"\";";

        Cursor cursor = db.rawQuery(sqlQuery,null);

        while(cursor.moveToNext()){
            if (((cursor.getString(0)).compareTo("null")) == 0) {
                cursor.close();
                db.close();
                return null;
            } else {
                values = cursor.getString(0);
                buffer = values.split(",");
            }
        }
        cursor.close();
        db.close();
        return buffer;
    }

    public String getWorkday1Notice(String name){

        SQLiteDatabase db = helper.getReadableDatabase();
        String value = null;

        String sqlQuery = "SELECT "+DatabaseHelper.WD1NOTICE+" FROM "+DatabaseHelper.TABLE_NAME+" WHERE "+DatabaseHelper.BUSNAME+"=\""+name+"\";";

        Cursor cursor = db.rawQuery(sqlQuery,null);

        while(cursor.moveToNext()){
            if (((cursor.getString(0)).compareTo("null")) == 0) {
                cursor.close();
                db.close();
                return "";
            } else {
                value = cursor.getString(0);
            }
        }
        cursor.close();
        db.close();
        return value;
    }

    public String[] getWorkday2(String name){
        SQLiteDatabase db = helper.getReadableDatabase();
        String[] buffer = {};
        String values;

        String sqlQuery = "SELECT "+DatabaseHelper.WORKDAY2+" FROM "+DatabaseHelper.TABLE_NAME+" WHERE "+DatabaseHelper.BUSNAME+"=\""+name+"\";";

        Cursor cursor = db.rawQuery(sqlQuery,null);

        while(cursor.moveToNext()){
            if (((cursor.getString(0)).compareTo("null")) == 0) {
                cursor.close();
                db.close();
                return null;
            } else {
                values = cursor.getString(0);
                buffer = values.split(",");
            }
        }
        cursor.close();
        db.close();
        return buffer;
    }

    public String getWorkday2Notice(String name){

        SQLiteDatabase db = helper.getReadableDatabase();
        String value = null;

        String sqlQuery = "SELECT "+DatabaseHelper.WD2NOTICE+" FROM "+DatabaseHelper.TABLE_NAME+" WHERE "+DatabaseHelper.BUSNAME+"=\""+name+"\";";

        Cursor cursor = db.rawQuery(sqlQuery,null);

        while(cursor.moveToNext()){
            if (((cursor.getString(0)).compareTo("null")) == 0) {
                cursor.close();
                db.close();
                return "";
            } else {
                value = cursor.getString(0);
            }
        }
        cursor.close();
        db.close();
        return value;
    }

    public String[] getSaturday1(String name){
        SQLiteDatabase db = helper.getReadableDatabase();
        String[] buffer = {};
        String values;

        String sqlQuery = "SELECT "+DatabaseHelper.SATURDAY1+" FROM "+DatabaseHelper.TABLE_NAME+" WHERE "+DatabaseHelper.BUSNAME+"=\""+name+"\";";

        Cursor cursor = db.rawQuery(sqlQuery,null);

        while(cursor.moveToNext()){
            if (((cursor.getString(0)).compareTo("null")) == 0) {
                cursor.close();
                db.close();
                return null;
            } else {
                values = cursor.getString(0);
                buffer = values.split(",");
            }
        }
        cursor.close();
        db.close();
        return buffer;
    }

    public String getSaturday1Notice(String name){

        SQLiteDatabase db = helper.getReadableDatabase();
        String value = null;

        String sqlQuery = "SELECT "+DatabaseHelper.SAT1NOTICE+" FROM "+DatabaseHelper.TABLE_NAME+" WHERE "+DatabaseHelper.BUSNAME+"=\""+name+"\";";

        Cursor cursor = db.rawQuery(sqlQuery,null);

        while(cursor.moveToNext()){
            if (((cursor.getString(0)).compareTo("null")) == 0) {
                cursor.close();
                db.close();
                return "";
            } else {
                value = cursor.getString(0);
            }
        }
        cursor.close();
        db.close();
        return value;
    }

    public String[] getSaturday2(String name){
        SQLiteDatabase db = helper.getReadableDatabase();
        String[] buffer = {};
        String values;

        String sqlQuery = "SELECT "+DatabaseHelper.SATURDAY2+" FROM "+DatabaseHelper.TABLE_NAME+" WHERE "+DatabaseHelper.BUSNAME+"=\""+name+"\";";

        Cursor cursor = db.rawQuery(sqlQuery,null);

        while(cursor.moveToNext()){
            if (((cursor.getString(0)).compareTo("null")) == 0) {
                cursor.close();
                db.close();
                return null;
            } else {
                values = cursor.getString(0);
                buffer = values.split(",");
            }
        }
        cursor.close();
        db.close();
        return buffer;
    }

    public String getSaturday2Notice(String name){

        SQLiteDatabase db = helper.getReadableDatabase();
        String value = null;

        String sqlQuery = "SELECT "+DatabaseHelper.SAT2NOTICE+" FROM "+DatabaseHelper.TABLE_NAME+" WHERE "+DatabaseHelper.BUSNAME+"=\""+name+"\";";

        Cursor cursor = db.rawQuery(sqlQuery,null);

        while(cursor.moveToNext()){
            if (((cursor.getString(0)).compareTo("null")) == 0) {
                cursor.close();
                db.close();
                return "";
            } else {
                value = cursor.getString(0);
            }
        }
        cursor.close();
        db.close();
        return value;
    }

    public String[] getSunday1(String name){
        SQLiteDatabase db = helper.getReadableDatabase();
        String[] buffer = {};
        String values;

        String sqlQuery = "SELECT "+DatabaseHelper.SUNDAY1+" FROM "+DatabaseHelper.TABLE_NAME+" WHERE "+DatabaseHelper.BUSNAME+"=\""+name+"\";";

        Cursor cursor = db.rawQuery(sqlQuery,null);

        while(cursor.moveToNext()){
            if (((cursor.getString(0)).compareTo("null")) == 0) {
                cursor.close();
                db.close();
                return null;
            } else {
                values = cursor.getString(0);
                buffer = values.split(",");
            }
        }
        cursor.close();
        db.close();
        return buffer;
    }

    public String getSunday1Notice(String name){

        SQLiteDatabase db = helper.getReadableDatabase();
        String value = null;

        String sqlQuery = "SELECT "+DatabaseHelper.SUN1NOTICE+" FROM "+DatabaseHelper.TABLE_NAME+" WHERE "+DatabaseHelper.BUSNAME+"=\""+name+"\";";

        Cursor cursor = db.rawQuery(sqlQuery,null);

        while(cursor.moveToNext()){
            if (((cursor.getString(0)).compareTo("null")) == 0) {
                cursor.close();
                db.close();
                return "";
            } else {
                value = cursor.getString(0);
            }
        }
        cursor.close();
        db.close();
        return value;
    }

    public String[] getSunday2(String name){
        SQLiteDatabase db = helper.getReadableDatabase();
        String[] buffer = {};
        String values;

        String sqlQuery = "SELECT "+DatabaseHelper.SUNDAY2+" FROM "+DatabaseHelper.TABLE_NAME+" WHERE "+DatabaseHelper.BUSNAME+"=\""+name+"\";";

        Cursor cursor = db.rawQuery(sqlQuery,null);

        while(cursor.moveToNext()){
            if (((cursor.getString(0)).compareTo("null")) == 0) {
                cursor.close();
                db.close();
                return null;
            } else {
                values = cursor.getString(0);
                buffer = values.split(",");
            }
        }
        cursor.close();
        db.close();
        return buffer;
    }

    public String getSunday2Notice(String name){

        SQLiteDatabase db = helper.getReadableDatabase();
        String value = null;

        String sqlQuery = "SELECT "+DatabaseHelper.SUN2NOTICE+" FROM "+DatabaseHelper.TABLE_NAME+" WHERE "+DatabaseHelper.BUSNAME+"=\""+name+"\";";

        Cursor cursor = db.rawQuery(sqlQuery,null);

        while(cursor.moveToNext()){
            if (((cursor.getString(0)).compareTo("null")) == 0) {
                cursor.close();
                db.close();
                return "";
            } else {
                value = cursor.getString(0);
            }
        }
        cursor.close();
        db.close();
        return value;
    }

    public boolean isEmpty(){
        SQLiteDatabase db = helper.getReadableDatabase();
        String query = ("SELECT count(*) FROM " + DatabaseHelper.TABLE_NAME);
        Cursor cursor = db.rawQuery(query, null);
        cursor.moveToFirst();
        int count = cursor.getInt(0);
        if (count > 0){
            cursor.close();
            db.close();
            return false;
        }
        else{
            cursor.close();
            db.close();
            return true;

        }
    }

    public static class DatabaseHelper extends SQLiteOpenHelper{
        private static final String DATABASE_NAME = "timetable_local.db";
        private static final String TABLE_NAME = "timetable";
        private static final String BUSNAME = "busname";
        private static final String WORKDAY1 = "workday1";
        private static final String WORKDAY2 = "workday2";
        private static final String SATURDAY1 = "saturday1";
        private static final String SATURDAY2 = "saturday2";
        private static final String SUNDAY1 = "sunday1";
        private static final String SUNDAY2 = "sunday2";
        private static final String WD1NOTICE = "wd1notice";
        private static final String WD2NOTICE = "wd2notice";
        private static final String SAT1NOTICE = "sat1notice";
        private static final String SAT2NOTICE = "sat2notice";
        private static final String SUN1NOTICE = "sun1notice";
        private static final String SUN2NOTICE = "sun2notice";
        private Context context;
        private static final int DATABASE_VERSION = 1;
        private static final String CREATE_TABLE = "CREATE TABLE " + TABLE_NAME
                +"("+BUSNAME+" TEXT PRIMARY KEY, "+WORKDAY1+" TEXT, "+WD1NOTICE+" TEXT, "+WORKDAY2+" TEXT, "+WD2NOTICE+" TEXT, "+SATURDAY1+" TEXT, "+SAT1NOTICE+" TEXT, "+SATURDAY2+" TEXT, "+SAT2NOTICE+" TEXT,"+SUNDAY1+" TEXT, "+SUN1NOTICE+" TEXT, "+SUNDAY2+" TEXT, "+SUN2NOTICE+" TEXT);";
        private static final String DROP_TABLE = "DROP TABLE " + TABLE_NAME + " IF EXISTS";

        public DatabaseHelper(Context context) {
            super(context, DATABASE_NAME, null, DATABASE_VERSION);
            this.context = context;
        }

        @Override
        public void onCreate(SQLiteDatabase database) {
            try{
                database.execSQL(CREATE_TABLE);
            }catch(SQLException e){
                Toast.makeText(context, "Error creating database.",
                        Toast.LENGTH_LONG).show();
            }

        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            try{
                db.execSQL(DROP_TABLE);
                onCreate(db);
            }catch(SQLException e){
                Toast.makeText(context, "Error updating database.",
                        Toast.LENGTH_LONG).show();
            }

        }
    }
}