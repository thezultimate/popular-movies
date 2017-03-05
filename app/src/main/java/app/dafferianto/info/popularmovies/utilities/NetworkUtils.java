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

import static app.dafferianto.info.popularmovies.MainActivity.TOP_RATED;

public final class NetworkUtils {

    private static final String API_KEY_PARAM = "api_key";
    private static final String SORTING_TOP_RATED = "top_rated";
    private static final String SORTING_POPULAR = "popular";
    private static final String POSTER_SIZE = "w185";
    private static final String REVIEWS = "reviews";
    private static final String TRAILERS = "videos";
    private static final String YOUTUBE_KEY_PARAM = "v";

    public static URL buildMovieUrl(Context context, String sorting) {
        String movieBaseUrl = context.getResources().getString(R.string.movie_base_url);
        String apiKey = context.getResources().getString(R.string.api_key);
        Uri builtUri = Uri.parse(movieBaseUrl)
                .buildUpon()
                .appendPath(sorting.equals(TOP_RATED) ? SORTING_TOP_RATED : SORTING_POPULAR)
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

    public static URL buildFavoriteUrl(Context context, int movieId) {
        String movieBaseUrl = context.getResources().getString(R.string.movie_base_url);
        String apiKey = context.getResources().getString(R.string.api_key);
        Uri builtUri = Uri.parse(movieBaseUrl)
                .buildUpon()
                .appendPath(String.valueOf(movieId))
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

    public static URL buildReviewUrl(Context context, int movieId) {
        return buildReviewTrailerUrl(context, movieId, REVIEWS);
    }

    public static URL buildTrailerUrl(Context context, int movieId) {
        return buildReviewTrailerUrl(context, movieId, TRAILERS);
    }

    private static URL buildReviewTrailerUrl(Context context, int movieId, String typePath) {
        String movieBaseUrl = context.getResources().getString(R.string.movie_base_url);
        String apiKey = context.getResources().getString(R.string.api_key);
        Uri builtUri = Uri.parse(movieBaseUrl)
                .buildUpon()
                .appendPath(String.valueOf(movieId))
                .appendPath(typePath)
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

    public static Uri buildYoutubeUri(Context context, String youtubeKey) {
        String youtubeBaseUrl = context.getResources().getString(R.string.youtube_base_url);
        Uri builtUri = Uri.parse(youtubeBaseUrl)
                .buildUpon()
                .appendQueryParameter(YOUTUBE_KEY_PARAM, youtubeKey)
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
