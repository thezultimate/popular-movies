package app.dafferianto.info.popularmovies;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.AsyncTaskLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.TypedValue;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;

import org.joda.time.DateTime;
import org.json.JSONException;

import java.net.URL;
import java.util.List;

import app.dafferianto.info.popularmovies.data.Movie;
import app.dafferianto.info.popularmovies.data.ReviewTrailerJson;
import app.dafferianto.info.popularmovies.utilities.DateUtils;
import app.dafferianto.info.popularmovies.utilities.MovieUtils;
import app.dafferianto.info.popularmovies.utilities.NetworkUtils;

import static app.dafferianto.info.popularmovies.data.FavoriteContract.FavoriteEntry;

public class DetailActivity extends AppCompatActivity implements
        TrailerAdapter.TrailerAdapterOnClickHandler {
    private static final int MAX_RATING = 10;
    private static final int REVIEW_TRAILER_LOADER_ID = 9;
    private static final int FAVORITE_LOADER_ID = 14;

    private TextView titleTextView;
    private ImageView posterImageView;
    private TextView releaseDateTextView;
    private TextView ratingTextView;
    private TextView descriptionTextView;
    private TextView reviewTitleTextView;
    private RecyclerView reviewRecyclerView;
    private ReviewAdapter reviewAdapter;
    private RecyclerView trailerRecyclerView;
    private TrailerAdapter trailerAdapter;
    private Button favoriteButton;

    private ProgressBar loadingIndicator;
    private TextView errorMessageTextView;

    private int movieId;
    private String movieTitle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        titleTextView = (TextView) findViewById(R.id.title_textview_details);
        posterImageView = (ImageView) findViewById(R.id.poster_details);
        releaseDateTextView = (TextView) findViewById(R.id.releasedate_textview_details);
        ratingTextView = (TextView) findViewById(R.id.rating_textview_details);
        descriptionTextView = (TextView) findViewById(R.id.description_textview_details);

        reviewRecyclerView = (RecyclerView) findViewById(R.id.recyclerview_review);
        trailerRecyclerView = (RecyclerView) findViewById(R.id.recyclerview_trailer);

        favoriteButton = (Button) findViewById(R.id.favorite_button_details);

        Intent intentThatStartedThisActivity = getIntent();
        Movie movie = intentThatStartedThisActivity.getParcelableExtra(MainActivity.MOVIE_KEY);

        movieId = movie.getId();
        movieTitle = movie.getTitle();
        titleTextView.setText(movieTitle);
        DateTime dateTime = DateTime.parse(movie.getReleaseDate());
        releaseDateTextView.setText("Release date: " + dateTime.getDayOfMonth() + " "
                + DateUtils.getMonthString(dateTime.getMonthOfYear()) + " " + dateTime.getYear());
        ratingTextView.setText("Rating: " + movie.getVoteAverage() + " / " + MAX_RATING);

        Context context = posterImageView.getContext();
        Uri uri = NetworkUtils.buildPosterUri(context, movie.getPosterPath());
        Picasso.with(context)
                .load(uri)
                .into(posterImageView);

        descriptionTextView.setText(movie.getOverview());

        LinearLayoutManager reviewLinearLayoutManager = new LinearLayoutManager(this);
        LinearLayoutManager trailerLinearLayoutManager = new LinearLayoutManager(this);

        reviewRecyclerView.setLayoutManager(reviewLinearLayoutManager);
        reviewRecyclerView.setHasFixedSize(true);
        reviewAdapter = new ReviewAdapter();
        reviewRecyclerView.setAdapter(reviewAdapter);
        reviewRecyclerView.setNestedScrollingEnabled(false);

        trailerRecyclerView.setLayoutManager(trailerLinearLayoutManager);
        trailerRecyclerView.setHasFixedSize(true);
        trailerAdapter = new TrailerAdapter(this);
        trailerRecyclerView.setAdapter(trailerAdapter);
        trailerRecyclerView.setNestedScrollingEnabled(false);

        loadingIndicator = (ProgressBar) findViewById(R.id.loading_indicator_details);
        errorMessageTextView = (TextView) findViewById(R.id.error_message_details);

        showFavoriteButton();
        showReviewTrailer(true);

    }

    private void showFavoriteButton() {
        getSupportLoaderManager().initLoader(FAVORITE_LOADER_ID, null, favoriteLoaderCallbacks);
    }

    private void showReviewTrailer(boolean isInitial) {
        showReviewTrailerUi();
        reviewAdapter.setReviewData(null);
        trailerAdapter.setTrailerData(null);
        if (isInitial) {
            getSupportLoaderManager().initLoader(REVIEW_TRAILER_LOADER_ID, null, reviewTrailerJsonLoaderCallbacks);
        } else {
            getSupportLoaderManager().restartLoader(REVIEW_TRAILER_LOADER_ID, null, reviewTrailerJsonLoaderCallbacks);
        }
    }

    private void showReviewTrailerUi() {
        errorMessageTextView.setVisibility(View.INVISIBLE);
        reviewRecyclerView.setVisibility(View.VISIBLE);
        trailerRecyclerView.setVisibility(View.VISIBLE);
    }

    private void showRefreshButton() {
        errorMessageTextView.setVisibility(View.VISIBLE);
        reviewRecyclerView.setVisibility(View.INVISIBLE);
        trailerRecyclerView.setVisibility(View.INVISIBLE);
    }

    private void setReviewTitleProperties() {
        reviewTitleTextView = (TextView) findViewById(R.id.review_title_textview_details);
        reviewTitleTextView.setText(R.string.review_detail_title);
        reviewTitleTextView.setTextSize(TypedValue.COMPLEX_UNIT_PX, getResources().getDimension(R.dimen.review_title_size_details));
        int padding = (int) getResources().getDimension(R.dimen.body_padding_details);
        int noPadding = (int) getResources().getDimension(R.dimen.no_padding);
        reviewTitleTextView.setPadding(padding, padding, padding, noPadding);
    }

    public void refreshDetails(View view) {
        showReviewTrailer(false);
    }

    public void toggleFavorite(View view) {
        if (favoriteButton.getText().equals(getString(R.string.favorite_add_detail_title))) {
            ContentValues contentValues = new ContentValues();
            contentValues.put(FavoriteEntry.COLUMN_MOVIE_ID, movieId);
            contentValues.put(FavoriteEntry.COLUMN_MOVIE_TITLE, movieTitle);
            Uri uri = getContentResolver().insert(FavoriteEntry.CONTENT_URI, contentValues);
            if (uri != null) {
                Toast.makeText(getBaseContext(), getString(R.string.favorite_add_success_message),
                        Toast.LENGTH_SHORT).show();
                favoriteButton.setText(R.string.favorite_remove_detail_title);
                favoriteButton.setBackgroundColor(getResources().getColor(android.R.color.holo_red_light));
            } else {
                Toast.makeText(getBaseContext(), getString(R.string.favorite_add_failure_message),
                        Toast.LENGTH_SHORT).show();
            }
        } else {
            Uri detailsFavoriteUri = FavoriteEntry.CONTENT_URI;
            detailsFavoriteUri = detailsFavoriteUri.buildUpon().appendPath(String.valueOf(movieId)).build();
            int rowsDeleted = getContentResolver().delete(detailsFavoriteUri, null, null);
            if (rowsDeleted > 0) {
                Toast.makeText(getBaseContext(), getString(R.string.favorite_remove_success_message),
                        Toast.LENGTH_SHORT).show();
                favoriteButton.setText(R.string.favorite_add_detail_title);
                favoriteButton.setBackgroundColor(getResources().getColor(android.R.color.holo_green_light));
            } else {
                Toast.makeText(getBaseContext(), getString(R.string.favorite_remove_failure_message),
                        Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    public void onClick(String youtubeKey) {
        Uri youtubeUri = NetworkUtils.buildYoutubeUri(this, youtubeKey);
        Intent intent = new Intent(Intent.ACTION_VIEW, youtubeUri);
        if (intent.resolveActivity(getPackageManager()) != null) {
            startActivity(intent);
        }
    }

    private LoaderManager.LoaderCallbacks<ReviewTrailerJson> reviewTrailerJsonLoaderCallbacks =
        new LoaderManager.LoaderCallbacks<ReviewTrailerJson>() {

            @Override
            public Loader<ReviewTrailerJson> onCreateLoader(int id, Bundle args) {
                return new AsyncTaskLoader<ReviewTrailerJson>(DetailActivity.this) {
                    ReviewTrailerJson reviewTrailerJson = null;

                    @Override
                    protected void onStartLoading() {
                        if (reviewTrailerJson != null) {
                            deliverResult(reviewTrailerJson);
                        } else {
                            loadingIndicator.setVisibility(View.VISIBLE);
                            forceLoad();
                        }
                    }

                    @Override
                    public ReviewTrailerJson loadInBackground() {
                        URL reviewRequestUrl = NetworkUtils.buildReviewUrl(DetailActivity.this, movieId);
                        URL trailerRequestUrl = NetworkUtils.buildTrailerUrl(DetailActivity.this, movieId);
                        try {
                            String jsonReviewResponse = NetworkUtils.getResponseFromHttpUrl(reviewRequestUrl);
                            String jsonTrailerResponse = NetworkUtils.getResponseFromHttpUrl(trailerRequestUrl);

                            ReviewTrailerJson jsonReviewTrailer =
                                    new ReviewTrailerJson(jsonReviewResponse, jsonTrailerResponse);
                            return jsonReviewTrailer;
                        } catch (Exception e) {
                            e.printStackTrace();
                            return null;
                        }
                    }

                    public void deliverResult(ReviewTrailerJson reviewTrailerJson) {
                        this.reviewTrailerJson = reviewTrailerJson;
                        super.deliverResult(reviewTrailerJson);
                    }

                };
            }

            @Override
            public void onLoadFinished(Loader<ReviewTrailerJson> loader, ReviewTrailerJson reviewTrailerJson) {
                loadingIndicator.setVisibility(View.INVISIBLE);
                if (reviewTrailerJson != null) {
                    showReviewTrailerUi();
                    String reviewJson = reviewTrailerJson.getReviewJson();
                    String trailerJson = reviewTrailerJson.getTrailerJson();

                    try {
                        List<String> reviews = MovieUtils.getReviewList(reviewJson);
                        List<String> trailerKeys = MovieUtils.getYoutubeKeyTrailerList(trailerJson);
                        reviewAdapter.setReviewData(reviews);
                        trailerAdapter.setTrailerData(trailerKeys);
                        if (reviews.size() != 0) {
                            setReviewTitleProperties();
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                } else {
                    showRefreshButton();
                }
            }

            @Override
            public void onLoaderReset(Loader<ReviewTrailerJson> loader) {
            }

        };

    private LoaderManager.LoaderCallbacks<Cursor> favoriteLoaderCallbacks =
        new LoaderManager.LoaderCallbacks<Cursor>() {

            @Override
            public Loader<Cursor> onCreateLoader(int id, Bundle args) {
                return new AsyncTaskLoader<Cursor>(DetailActivity.this) {
                    Cursor favoriteCursor = null;

                    @Override
                    protected void onStartLoading() {
                        if (favoriteCursor != null) {
                            deliverResult(favoriteCursor);
                        } else {
                            forceLoad();
                        }
                    }

                    @Override
                    public Cursor loadInBackground() {
                        Uri detailsFavoriteUri = FavoriteEntry.CONTENT_URI;
                        detailsFavoriteUri = detailsFavoriteUri.buildUpon().appendPath(String.valueOf(movieId)).build();
                        try {
                            Cursor favoriteCursor = getContentResolver().query(detailsFavoriteUri,
                                        null,
                                        null,
                                        null,
                                        null);

                            return favoriteCursor;
                        } catch (Exception e) {
                            e.printStackTrace();
                            return null;
                        }
                    }

                    public void deliverResult(Cursor favoriteCursor) {
                        this.favoriteCursor = favoriteCursor;
                        super.deliverResult(favoriteCursor);
                    }

                };
            }

            @Override
            public void onLoadFinished(Loader<Cursor> loader, Cursor favoriteCursor) {
                if (favoriteCursor != null) {
                    if (favoriteCursor.getCount() > 0) {
                        favoriteButton.setText(R.string.favorite_remove_detail_title);
                        favoriteButton.setBackgroundColor(getResources().getColor(android.R.color.holo_red_light));
                    } else {
                        favoriteButton.setText(R.string.favorite_add_detail_title);
                        favoriteButton.setBackgroundColor(getResources().getColor(android.R.color.holo_green_light));
                    }
                    favoriteButton.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onLoaderReset(Loader<Cursor> loader) {
            }

        };

}
