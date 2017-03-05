package app.dafferianto.info.popularmovies.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import static app.dafferianto.info.popularmovies.data.FavoriteContract.FavoriteEntry;

public class FavoriteDbHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "favoriteDb.db";
    private static final int VERSION = 1;

    public FavoriteDbHelper(Context context) {
        super(context, DATABASE_NAME, null, VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        final String CREATE_TABLE = "CREATE TABLE " + FavoriteEntry.TABLE_NAME + " (" +
                FavoriteEntry._ID + " INTEGER PRIMARY KEY, " +
                FavoriteEntry.COLUMN_MOVIE_ID + " INTEGER NOT NULL, " +
                FavoriteEntry.COLUMN_MOVIE_TITLE + " TEXT NOT NULL);";

        db.execSQL(CREATE_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + FavoriteEntry.TABLE_NAME);
        onCreate(db);
    }

}
