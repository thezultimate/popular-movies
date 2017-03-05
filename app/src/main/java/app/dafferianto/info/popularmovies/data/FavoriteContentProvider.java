package app.dafferianto.info.popularmovies.data;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import static app.dafferianto.info.popularmovies.data.FavoriteContract.AUTHORITY;
import static app.dafferianto.info.popularmovies.data.FavoriteContract.FavoriteEntry;
import static app.dafferianto.info.popularmovies.data.FavoriteContract.FavoriteEntry.TABLE_NAME;
import static app.dafferianto.info.popularmovies.data.FavoriteContract.PATH_FAVORITE;

public class FavoriteContentProvider extends ContentProvider {

    public static final int FAVORITES = 100;
    public static final int FAVORITE_WITH_ID = 101;

    private static final UriMatcher sUriMatcher = buildUriMatcher();

    private FavoriteDbHelper favoriteDbHelper;

    public static UriMatcher buildUriMatcher() {
        UriMatcher uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
        uriMatcher.addURI(AUTHORITY, PATH_FAVORITE, FAVORITES);
        uriMatcher.addURI(AUTHORITY, PATH_FAVORITE + "/#", FAVORITE_WITH_ID);
        return uriMatcher;
    }


    @Override
    public boolean onCreate() {
        Context context = getContext();
        favoriteDbHelper = new FavoriteDbHelper(context);
        return true;
    }

//    @Nullable
    @Override
    public Cursor query(@NonNull Uri uri, @Nullable String[] projection, @Nullable String selection,
                        @Nullable String[] selectionArgs, @Nullable String sortOrder) {

        final SQLiteDatabase db = favoriteDbHelper.getReadableDatabase();
        int match = sUriMatcher.match(uri);
        Cursor retCursor;

        switch (match) {
            case FAVORITES:
                retCursor =  db.query(TABLE_NAME,
                        projection,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        sortOrder);
                break;

            case FAVORITE_WITH_ID:
                String movieId = uri.getLastPathSegment();
                retCursor =  db.query(TABLE_NAME,
                        projection,
                        "movie_id=?",
                        new String[]{movieId},
                        null,
                        null,
                        sortOrder);
                break;

            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }

        retCursor.setNotificationUri(getContext().getContentResolver(), uri);
        return retCursor;

    }

//    @Nullable
    @Override
    public String getType(@NonNull Uri uri) {
        throw new UnsupportedOperationException("Not yet implemented");
    }

//    @Nullable
    @Override
    public Uri insert(@NonNull Uri uri, @Nullable ContentValues values) {
        final SQLiteDatabase db = favoriteDbHelper.getWritableDatabase();
        int match = sUriMatcher.match(uri);
        Uri returnUri;

        switch (match) {
            case FAVORITES:
                long id = db.insert(TABLE_NAME, null, values);
                if (id > 0) {
                    returnUri = ContentUris.withAppendedId(FavoriteEntry.CONTENT_URI, id);
                } else {
                    throw new android.database.SQLException("Failed to insert row into " + uri);
                }
                break;

            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }

        getContext().getContentResolver().notifyChange(uri, null);
        return returnUri;
    }

    @Override
    public int delete(@NonNull Uri uri, @Nullable String selection, @Nullable String[] selectionArgs) {
        final SQLiteDatabase db = favoriteDbHelper.getWritableDatabase();
        int match = sUriMatcher.match(uri);
        int tasksDeleted;

        switch (match) {
            case FAVORITE_WITH_ID:
                String movieId = uri.getLastPathSegment();
                tasksDeleted = db.delete(TABLE_NAME, "movie_id=?", new String[]{movieId});
                break;

            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }

        if (tasksDeleted != 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }
        return tasksDeleted;
    }

    @Override
    public int update(@NonNull Uri uri, @Nullable ContentValues values, @Nullable String selection, @Nullable String[] selectionArgs) {
        throw new UnsupportedOperationException("Not yet implemented");
    }
}
