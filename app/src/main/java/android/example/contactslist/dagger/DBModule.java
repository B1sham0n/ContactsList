package android.example.contactslist.dagger;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.example.contactslist.db_helpers.DBHelper;
import android.example.contactslist.db_helpers.DBHelperFavorite;

import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

@Module
public class DBModule {

    private Context context;

    @Inject
    public DBModule(Context context) {
        this.context = context;
    }

    @Provides
    @Singleton
    DBHelper provideDBHelper()
    {
        return new DBHelper(this.context);
    }

    @Provides
    @Singleton
    @Named("peoples")
    SQLiteDatabase provideDB(DBHelper dbHelper){
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        return db;
    }

    @Provides
    @Singleton
    DBHelperFavorite provideDBHelperFavorite(){ return new DBHelperFavorite(this.context); }

    @Provides
    @Singleton
    @Named("favorite")
    SQLiteDatabase provideDBFav(DBHelperFavorite dbHelperFavorite){
        SQLiteDatabase dbFav = dbHelperFavorite.getWritableDatabase();
        return dbFav;
    }
}
