package app.dafferianto.info.popularmovies.utilities;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import app.dafferianto.info.popularmovies.data.Movie;

public final class MovieUtils {

    static final String RESULT_ARRAY = "results";
    static final String MOVIE_ID = "id";
    static final String POSTER_PATH = "poster_path";
    static final String TITLE = "title";
    static final String RELEASE_DATE = "release_date";
    static final String VOTE_AVERAGE = "vote_average";
    static final String OVERVIEW = "overview";
    static final String REVIEW_CONTENT = "content";
    static final String TRAILER_KEY = "key";
    static final String TRAILER_SITE = "site";
    static final String TRAILER_SITE_YOUTUBE = "YouTube";

    public static List<Movie> getMovieList(String jsonMovie) throws JSONException {
        List<Movie> movies = new ArrayList<>();

        JSONObject rootObject = new JSONObject(jsonMovie);
        JSONArray movieArray = rootObject.getJSONArray(RESULT_ARRAY);

        for (int i = 0; i < movieArray.length(); i++) {
            JSONObject movieObject = movieArray.getJSONObject(i);
            int id = movieObject.getInt(MOVIE_ID);
            String posterPath = movieObject.getString(POSTER_PATH);
            String title = movieObject.getString(TITLE);
            String releaseDate = movieObject.getString(RELEASE_DATE);
            double voteAverage = movieObject.getDouble(VOTE_AVERAGE);
            String overview = movieObject.getString(OVERVIEW);
            Movie movie = new Movie(id, posterPath, title, releaseDate, voteAverage, overview);
            movies.add(movie);
        }

        return movies;
    }

    public static Movie getFavorite(String jsonFavorite) throws JSONException {
        JSONObject rootObject = new JSONObject(jsonFavorite);
        int id = rootObject.getInt(MOVIE_ID);
        String posterPath = rootObject.getString(POSTER_PATH);
        String title = rootObject.getString(TITLE);
        String releaseDate = rootObject.getString(RELEASE_DATE);
        double voteAverage = rootObject.getDouble(VOTE_AVERAGE);
        String overview = rootObject.getString(OVERVIEW);
        Movie favorite = new Movie(id, posterPath, title, releaseDate, voteAverage, overview);
        return favorite;
    }

    public static List<String> getReviewList(String jsonReview) throws JSONException {
        List<String> reviews = new ArrayList<>();

        JSONObject rootObject = new JSONObject(jsonReview);
        JSONArray reviewArray = rootObject.getJSONArray(RESULT_ARRAY);

        for (int i = 0; i < reviewArray.length(); i++) {
            JSONObject reviewObject = reviewArray.getJSONObject(i);
            String content = reviewObject.getString(REVIEW_CONTENT);
            reviews.add(content);
        }

        return reviews;
    }

    public static List<String> getYoutubeKeyTrailerList(String jsonTrailer) throws JSONException {
        List<String> trailerKeys = new ArrayList<>();

        JSONObject rootObject = new JSONObject(jsonTrailer);
        JSONArray trailerArray = rootObject.getJSONArray(RESULT_ARRAY);

        for (int i = 0; i < trailerArray.length(); i++) {
            JSONObject trailerObject = trailerArray.getJSONObject(i);
            String trailerKey = trailerObject.getString(TRAILER_KEY);
            String trailerSite = trailerObject.getString(TRAILER_SITE);
            if (trailerSite.equals(TRAILER_SITE_YOUTUBE)) {
                trailerKeys.add(trailerKey);
            }
        }

        return trailerKeys;
    }

}
