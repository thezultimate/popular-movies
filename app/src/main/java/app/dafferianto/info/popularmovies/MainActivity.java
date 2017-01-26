package app.dafferianto.info.popularmovies;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.net.URL;
import java.util.List;

import app.dafferianto.info.popularmovies.data.Movie;
import app.dafferianto.info.popularmovies.data.Sorting;
import app.dafferianto.info.popularmovies.utilities.MovieUtils;
import app.dafferianto.info.popularmovies.utilities.NetworkUtils;

public class MainActivity extends AppCompatActivity implements MainAdapter.MainAdapterOnClickHandler {
    public static final String MOVIE_KEY = "info.dafferianto.key.MOVIE";

    private static final int MAIN_COLUMN_COUNT = 2;
    private static final boolean FIXED_SIZE = true;

    private RecyclerView recyclerView;
    private MainAdapter mainAdapter;
    private ProgressBar loadingIndicator;
    private TextView errorMessageTextView;

    private Sorting currentSorting;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        recyclerView = (RecyclerView) findViewById(R.id.recyclerview_main);

        GridLayoutManager gridLayoutManager = new GridLayoutManager(this, MAIN_COLUMN_COUNT);
        recyclerView.setLayoutManager(gridLayoutManager);

        recyclerView.setHasFixedSize(FIXED_SIZE);

        mainAdapter = new MainAdapter(this);
        recyclerView.setAdapter(mainAdapter);

        loadingIndicator = (ProgressBar) findViewById(R.id.loading_indicator_main);
        errorMessageTextView = (TextView) findViewById(R.id.error_message_main);

        showPosters(Sorting.POPULAR);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_popular) {
            if (currentSorting != Sorting.POPULAR) {
                showPosters(Sorting.POPULAR);
            }
            return true;
        }

        if (id == R.id.action_top_rated) {
            if (currentSorting != Sorting.TOP_RATED) {
                showPosters(Sorting.TOP_RATED);
            }
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onClick(Movie movie) {
        Context context = this;
        Class destinationClass = DetailActivity.class;
        Intent intentToStartDetailActivity = new Intent(context, destinationClass);
        intentToStartDetailActivity.putExtra(MainActivity.MOVIE_KEY, movie);
        startActivity(intentToStartDetailActivity);
    }

    public void refreshMain(View view) {
        showPosters(currentSorting);
    }

    private void showPosters(Sorting sorting) {
        showPostersUi();
        mainAdapter.setMovieData(null);
        setTitle(sorting == Sorting.POPULAR ? R.string.popular_title : R.string.top_rated_title);
        currentSorting = sorting;
        loadPosters(sorting);
    }

    private void showPostersUi() {
        errorMessageTextView.setVisibility(View.INVISIBLE);
        recyclerView.setVisibility(View.VISIBLE);
    }

    private void showRefreshButton() {
        errorMessageTextView.setVisibility(View.VISIBLE);
        recyclerView.setVisibility(View.INVISIBLE);
    }

    private void loadPosters(Sorting sorting) {
        new FetchMovieDataTask().execute(sorting.name());
    }

    public class FetchMovieDataTask extends AsyncTask<String, Void, List<Movie>> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            loadingIndicator.setVisibility(View.VISIBLE);
        }

        @Override
        protected List<Movie> doInBackground(String... params) {
            if (params.length == 0) {
                return null;
            }

            String sorting = params[0];
            URL movieRequestUrl = NetworkUtils.buildMovieUrl(MainActivity.this, Sorting.valueOf(sorting));

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
            loadingIndicator.setVisibility(View.INVISIBLE);
            if (movies != null) {
                showPostersUi();
                mainAdapter.setMovieData(movies);
            } else {
                showRefreshButton();
            }
        }

    }

}
