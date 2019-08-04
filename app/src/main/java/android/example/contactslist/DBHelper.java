package android.example.contactslist;

import android.content.ContentValues;
import android.content.Context;
import android.content.res.Resources;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;


import javax.inject.Inject;
import javax.inject.Singleton;

@Singleton
public class DBHelper extends SQLiteOpenHelper {
    String nameDB;// = "peoplesDB";
    String nameTable;// = "peoplesTable"
    public static final String USER_TABLE_NAME = "peoplesTable";
    public static final String USER_DB_NAME = "peoplesDB";
    public static final String USER_COLUMN_USER_ID = "id";
    public static final String USER_COLUMN_USER_NAME = "full_name";
    public static final String USER_COLUMN_USER_USERNAME= "username";
    public static final String USER_COLUMN_USER_EMAIL = "email";
    public static final String USER_COLUMN_USER_PHOTO = "photo";
    public static final String USER_COLUMN_USER_PHONE = "telephone";
    @Inject
    public DBHelper(Context context, String dbName, String tbName) {
        //super(context, nameDB, null, 1);
        super(context, dbName, null, 1);
        this.nameDB = dbName;
        this.nameTable = tbName;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
            //Log.d(LOG_TAG, "--- onCreate database ---");
            // создаем таблицу с полями
            db.execSQL("create table " + nameTable + " ("
            + "id integer primary key autoincrement,"
            + "full_name text,"
            + "photo text,"
            + "telephone text" + ");");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }

    public void deleteDB(SQLiteDatabase db){
        db.delete(USER_TABLE_NAME, null,null);
        db.execSQL("DELETE FROM SQLITE_SEQUENCE WHERE NAME = '" + USER_TABLE_NAME + "'");
    }
    public void insertToDB(SQLiteDatabase db, String name, String photo, String phone){
        ContentValues cv = new ContentValues();
        cv.put(USER_COLUMN_USER_NAME, name);
        //cv.put(USER_COLUMN_USER_USERNAME, username);
        //cv.put(USER_COLUMN_USER_EMAIL, email);
        cv.put(USER_COLUMN_USER_PHOTO, photo);
        cv.put(USER_COLUMN_USER_PHONE, phone);

        db.insert(USER_TABLE_NAME, null, cv);
    }

}
