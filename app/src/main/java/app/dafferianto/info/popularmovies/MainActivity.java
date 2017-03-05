package app.dafferianto.info.popularmovies;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.AsyncTaskLoader;
import android.support.v4.content.Loader;
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
import java.util.ArrayList;
import java.util.List;

import app.dafferianto.info.popularmovies.data.Movie;
import app.dafferianto.info.popularmovies.utilities.MovieUtils;
import app.dafferianto.info.popularmovies.utilities.NetworkUtils;

import static app.dafferianto.info.popularmovies.data.FavoriteContract.FavoriteEntry;

public class MainActivity extends AppCompatActivity implements
        MainAdapter.MainAdapterOnClickHandler,
        LoaderManager.LoaderCallbacks<List<Movie>> {

    public static final String MOVIE_KEY = "info.dafferianto.key.MOVIE";
    public static final String POPULAR = "POPULAR";
    public static final String TOP_RATED = "TOP_RATED";
    public static final String FAVORITE = "FAVORITE";
    public static final String SORTING_KEY = "info.dafferianto.key.SORTING";

    private static final int MAIN_COLUMN_COUNT = 2;
    private static final boolean FIXED_SIZE = true;
    private static final int MOVIE_LOADER_ID = 5;

    private RecyclerView recyclerView;
    private MainAdapter mainAdapter;
    private ProgressBar loadingIndicator;
    private TextView errorMessageTextView;
    private TextView emptyFavoriteMessageTextView;

    private String currentSorting;

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
        emptyFavoriteMessageTextView = (TextView) findViewById(R.id.empty_message_main);

        if (savedInstanceState != null) {
            if (savedInstanceState.containsKey(SORTING_KEY)) {
                showPosters(savedInstanceState.getString(SORTING_KEY), false);
            }
        } else {
            showPosters(POPULAR, true);
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString(SORTING_KEY, currentSorting);
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (currentSorting.equals(FAVORITE)) {
            showPosters(currentSorting, false);
        }
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
            if (currentSorting != POPULAR) {
                showPosters(POPULAR, false);
            }
            return true;
        }

        if (id == R.id.action_top_rated) {
            if (currentSorting != TOP_RATED) {
                showPosters(TOP_RATED, false);
            }
            return true;
        }

        if (id == R.id.action_favorite) {
            if (currentSorting != FAVORITE) {
                showPosters(FAVORITE, false);
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
        showPosters(currentSorting, false);
    }

    private void showPosters(String sorting, boolean isInitial) {
        showPostersUi();
        mainAdapter.setMovieData(null);
        setTitle(sorting.equals(POPULAR)
                ? R.string.popular_title
                : sorting.equals(TOP_RATED)
                    ? R.string.top_rated_title
                    : R.string.favorite_title);
        currentSorting = sorting;
        if (isInitial) {
            getSupportLoaderManager().initLoader(MOVIE_LOADER_ID, null, MainActivity.this);
        } else {
            getSupportLoaderManager().restartLoader(MOVIE_LOADER_ID, null, MainActivity.this);
        }
    }

    private void showPostersUi() {
        recyclerView.setVisibility(View.VISIBLE);
        errorMessageTextView.setVisibility(View.INVISIBLE);
        emptyFavoriteMessageTextView.setVisibility(View.INVISIBLE);
    }

    private void showEmptyFavoriteUi() {
        emptyFavoriteMessageTextView.setVisibility(View.VISIBLE);
        errorMessageTextView.setVisibility(View.INVISIBLE);
        recyclerView.setVisibility(View.INVISIBLE);
    }

    private void showRefreshButton() {
        errorMessageTextView.setVisibility(View.VISIBLE);
        recyclerView.setVisibility(View.INVISIBLE);
        emptyFavoriteMessageTextView.setVisibility(View.INVISIBLE);
    }

    @Override
    public Loader<List<Movie>> onCreateLoader(int id, Bundle args) {

        return new AsyncTaskLoader<List<Movie>>(this) {
            List<Movie> movies = null;

            @Override
            protected void onStartLoading() {
                if (movies != null) {
                    deliverResult(movies);
                } else {
                    loadingIndicator.setVisibility(View.VISIBLE);
                    forceLoad();
                }
            }

            @Override
            public List<Movie> loadInBackground() {
                if (currentSorting == POPULAR  || currentSorting == TOP_RATED) {
                    URL movieRequestUrl = NetworkUtils.buildMovieUrl(MainActivity.this, currentSorting);
                    try {
                        String jsonMovieResponse = NetworkUtils.getResponseFromHttpUrl(movieRequestUrl);
                        List<Movie> movies = MovieUtils.getMovieList(jsonMovieResponse);
                        return movies;
                    } catch (Exception e) {
                        e.printStackTrace();
                        return null;
                    }
                } else { // FAVORITE
                    Cursor favoriteCursor = getContentResolver().query(FavoriteEntry.CONTENT_URI,
                            null,
                            null,
                            null,
                            null);
                    if (favoriteCursor.getCount() > 0) {
                        try {
                            List<Movie> favorites = new ArrayList<>();
                            while (favoriteCursor.moveToNext()) {
                                int movieIdIndex = favoriteCursor.getColumnIndex(FavoriteEntry.COLUMN_MOVIE_ID);
                                int movieId = favoriteCursor.getInt(movieIdIndex);
                                URL favoriteRequestUrl = NetworkUtils.buildFavoriteUrl(MainActivity.this, movieId);
                                String jsonFavoriteResponse = NetworkUtils.getResponseFromHttpUrl(favoriteRequestUrl);
                                Movie favorite = MovieUtils.getFavorite(jsonFavoriteResponse);
                                favorites.add(favorite);
                            }
                            return favorites;
                        } catch (Exception e) {
                            e.printStackTrace();
                            return null;
                        } finally {
                            favoriteCursor.close();
                        }
                    } else {
                        return new ArrayList<>();
                    }
                }
            }

            public void deliverResult(List<Movie> movies) {
                this.movies = movies;
                super.deliverResult(movies);
            }

        };

    }

    @Override
    public void onLoadFinished(Loader<List<Movie>> loader, List<Movie> movies) {
        loadingIndicator.setVisibility(View.INVISIBLE);
        if (movies != null) {
            if (movies.size() == 0) {
                showEmptyFavoriteUi();
            } else {
                showPostersUi();
            }
            mainAdapter.setMovieData(movies);
        } else {
            showRefreshButton();
        }
    }

    @Override
    public void onLoaderReset(Loader<List<Movie>> loader) {
    }

}
