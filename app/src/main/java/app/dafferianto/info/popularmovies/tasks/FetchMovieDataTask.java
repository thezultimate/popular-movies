package app.dafferianto.info.popularmovies.tasks;

import android.content.Context;
import android.os.AsyncTask;

import java.net.URL;
import java.util.List;

import app.dafferianto.info.popularmovies.data.Movie;
import app.dafferianto.info.popularmovies.utilities.MovieUtils;
import app.dafferianto.info.popularmovies.utilities.NetworkUtils;

public class FetchMovieDataTask extends AsyncTask<String, Void, List<Movie>> {
    private Context context;
    private AsyncTaskCompleteListener<List<Movie>> listener;

    public FetchMovieDataTask(Context context, AsyncTaskCompleteListener<List<Movie>> listener) {
        this.context = context;
        this.listener = listener;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
    }

    @Override
    protected List<Movie> doInBackground(String... params) {
        if (params.length == 0) {
            return null;
        }

        String sorting = params[0];
        URL movieRequestUrl = NetworkUtils.buildMovieUrl(context, sorting);

        try {
            String jsonMovieResponse = NetworkUtils.getResponseFromHttpUrl(movieRequestUrl);
            List<Movie> movies = MovieUtils.getMovieList(jsonMovieResponse);
            return movies;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    protected void onPostExecute(List<Movie> movies) {
        super.onPostExecute(movies);
        listener.onTaskComplete(movies);
    }

}
