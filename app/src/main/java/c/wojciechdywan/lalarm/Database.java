package c.wojciechdywan.lalarm;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class Database extends SQLiteOpenHelper {
    public static final String DATABASE_NAME = "AlarmClock.db";
    public static final String TABLE_NAME = "Alarm";
    public static final String COL_1 = "ID";
    public static final String COL_2 = "TIME";
    public static final String COL_3 = "VIBRE";
    public static final String COL_4 = "VOLUME";
    public static final String COL_5 = "RATE";
    public static final String COL_6 = "SONG";

    public Database(Context context) {
        super(context, DATABASE_NAME, null, 6);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("create table " + TABLE_NAME +" (ID INTEGER PRIMARY KEY AUTOINCREMENT,TIME TEXT,VIBRE TEXT,VOLUME INTEGER,RATE INTEGER, SONG INTEGER)");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS "+TABLE_NAME);
        onCreate(db);
    }

    public boolean insertData(String time,String vibre,String volume,String rate, String song) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(COL_2,time);
        contentValues.put(COL_3,vibre);
        contentValues.put(COL_4,volume);
        contentValues.put(COL_5,rate);
        contentValues.put(COL_6,song);
        long result = db.insert(TABLE_NAME,null ,contentValues);
        if(result == -1)
            return false;
        else
            return true;
    }

    public Cursor getAllData(){
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor res = db.rawQuery("select * from "+TABLE_NAME, null);
        return res;
    }

    public boolean updateData(String id, String time,String vibre,String volume,int rate, String song){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(COL_1,id);
        contentValues.put(COL_2,time);
        contentValues.put(COL_3,vibre);
        contentValues.put(COL_4,volume);
        contentValues.put(COL_5,rate);
        contentValues.put(COL_6,song);
        long result = db.update(TABLE_NAME,contentValues,"ID = ?",new String[] {(id)});
        if(result == -1)
            return false;
        else
            return true;
    }

    public boolean updateRate(int rate){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(COL_5,rate);
        long result = db.update(TABLE_NAME,contentValues,"ID = (SELECT MAX(id) FROM "+TABLE_NAME+")",null);
        if(result == -1)
            return false;
        else
            return true;
    }

    public Integer delateData(String id){
        SQLiteDatabase db = this.getWritableDatabase();
        return db.delete(TABLE_NAME,"ID = ?",new String[] {(id)});
    }

}
