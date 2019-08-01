package android.example.contactslist.dagger;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.example.contactslist.DBHelper;

import javax.inject.Inject;
import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

@Module
public class DBModule {

    private Context context;
    private String tableName;
    private String dbName;

    @Inject
    public DBModule(Context context, String tableName, String dbName) {
        this.context = context;
        this.tableName = tableName;
        this.dbName = dbName;
    }

    @Provides
    @Singleton
    DBHelper provideDBHelper()
    {
        return new DBHelper(this.context, this.dbName, this.tableName);
    }

    @Provides
    @Singleton
    SQLiteDatabase provideDB(DBHelper dbHelper){
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        return db;
    }


}
