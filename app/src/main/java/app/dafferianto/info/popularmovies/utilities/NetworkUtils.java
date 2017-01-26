package app.dafferianto.info.popularmovies.utilities;

import android.content.Context;
import android.net.Uri;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Scanner;

import app.dafferianto.info.popularmovies.R;
import app.dafferianto.info.popularmovies.data.Sorting;

public final class NetworkUtils {

    private static final String API_KEY_PARAM = "api_key";
    private static final String SORTING_TOP_RATED = "top_rated";
    private static final String SORTING_POPULAR = "popular";

    private static final String POSTER_SIZE = "w185";

    public static URL buildMovieUrl(Context context, Sorting sorting) {
        String movieBaseUrl = context.getResources().getString(R.string.movie_base_url);
        String apiKey = context.getResources().getString(R.string.api_key);
        Uri builtUri = Uri.parse(movieBaseUrl)
                .buildUpon()
                .appendPath(sorting == Sorting.TOP_RATED ? SORTING_TOP_RATED : SORTING_POPULAR)
                .appendQueryParameter(API_KEY_PARAM, apiKey)
                .build();

        URL url = null;
        try {
            url = new URL(builtUri.toString());
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }

        return url;
    }

    public static Uri buildPosterUri(Context context, String posterPath) {
        String posterBaseUrl = context.getResources().getString(R.string.poster_base_url);
        Uri builtUri = Uri.parse(posterBaseUrl)
                .buildUpon()
                .appendPath(POSTER_SIZE)
                .appendEncodedPath(posterPath)
                .build();

        return builtUri;
    }

    public static String getResponseFromHttpUrl(URL url) throws IOException {
        HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
        try {
            InputStream in = urlConnection.getInputStream();

            Scanner scanner = new Scanner(in);
            scanner.useDelimiter("\\A");

            boolean hasInput = scanner.hasNext();
            if (hasInput) {
                return scanner.next();
            } else {
                return null;
            }
        } finally {
            urlConnection.disconnect();
        }
    }

}
