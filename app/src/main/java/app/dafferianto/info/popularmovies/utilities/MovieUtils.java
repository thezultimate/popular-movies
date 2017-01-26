package app.dafferianto.info.popularmovies.utilities;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import app.dafferianto.info.popularmovies.data.Movie;

public final class MovieUtils {



    public static List<Movie> getMovieList(String jsonMovie) throws JSONException {
        final String RESULT_ARRAY = "results";
        final String MOVIE_ID = "id";
        final String POSTER_PATH = "poster_path";
        final String TITLE = "title";
        final String RELEASE_DATE = "release_date";
        final String VOTE_AVERAGE = "vote_average";
        final String OVERVIEW = "overview";

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

}
