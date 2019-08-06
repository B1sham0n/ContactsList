package android.example.contactslist.db_helpers;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import javax.inject.Inject;
import javax.inject.Singleton;

@Singleton
public class DBHelperFavorite extends SQLiteOpenHelper {

    public static final String USER_TABLE_NAME = "favoritesTable";
    public static final String USER_DB_NAME = "favoritesDB";
    public static final String USER_COLUMN_USER_ID = "id";
    public static final String USER_COLUMN_USER_NAME = "full_name";
    public static final String USER_COLUMN_USER_USERNAME= "username";
    public static final String USER_COLUMN_USER_EMAIL = "email";
    public static final String USER_COLUMN_USER_PHOTO = "photo";
    public static final String USER_COLUMN_USER_PHONE = "telephone";

    @Inject
    public DBHelperFavorite(Context context) {
        //super(context, nameDB, null, 1);
        super(context, USER_DB_NAME, null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
            // создаем таблицу с полями
        db.execSQL("create table " + USER_TABLE_NAME + " ("
                + USER_COLUMN_USER_ID + " integer primary key autoincrement,"
                + USER_COLUMN_USER_NAME +" text,"
                + USER_COLUMN_USER_PHOTO + " text,"
                + USER_COLUMN_USER_PHONE + " text" + ");");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) { }

    public void deleteDB(SQLiteDatabase db){
        db.delete(USER_TABLE_NAME, null,null);
        db.execSQL("DELETE FROM SQLITE_SEQUENCE WHERE NAME = '" + USER_TABLE_NAME + "'");
    }
    public void insertToDB(SQLiteDatabase db, String name, String photo, String phone){
        ContentValues cv = new ContentValues();
        cv.put(USER_COLUMN_USER_NAME, name);
        cv.put(USER_COLUMN_USER_PHOTO, photo);
        cv.put(USER_COLUMN_USER_PHONE, phone);

        db.insert(USER_TABLE_NAME, null, cv);
        System.out.println("inserted " + name);
    }
    public void removeFromDB(SQLiteDatabase db, String name){
        Cursor c = db.query(USER_TABLE_NAME, null, null, null,
                null, null, null);
        Integer id = null;
        if(c.moveToFirst()){
            do{
                if(name.equals(c.getString(c.getColumnIndex(USER_COLUMN_USER_NAME))))
                    id = c.getInt(c.getColumnIndex(USER_COLUMN_USER_ID));
            }while (c.moveToNext());
        }
        if(id != null) {
            db.delete(USER_TABLE_NAME,"id = " + id, null);
            System.out.println("deleted id = " + id);
        }

    }

}
