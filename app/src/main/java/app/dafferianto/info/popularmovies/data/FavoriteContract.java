package app.dafferianto.info.popularmovies.data;

import android.net.Uri;
import android.provider.BaseColumns;

public class FavoriteContract {

    public static final String AUTHORITY = "info.dafferianto.popularmovies";
    public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + AUTHORITY);
    public static final String PATH_FAVORITE = "favorite";

    public static final class FavoriteEntry implements BaseColumns {

        public static final Uri CONTENT_URI =
                BASE_CONTENT_URI.buildUpon().appendPath(PATH_FAVORITE).build();

        public static final String TABLE_NAME = "favorite";
        public static final String COLUMN_MOVIE_ID = "movie_id";
        public static final String COLUMN_MOVIE_TITLE = "movie_title";

    }

}
