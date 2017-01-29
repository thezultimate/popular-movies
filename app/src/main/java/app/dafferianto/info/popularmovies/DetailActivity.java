package app.dafferianto.info.popularmovies;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import org.joda.time.DateTime;

import app.dafferianto.info.popularmovies.data.Movie;
import app.dafferianto.info.popularmovies.utilities.DateUtils;
import app.dafferianto.info.popularmovies.utilities.NetworkUtils;

public class DetailActivity extends AppCompatActivity {
    private static final int MAX_RATING = 10;

    private TextView titleTextView;
    private ImageView posterImageView;
    private TextView releaseDateTextView;
    private TextView ratingTextView;
    private TextView descriptionTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        titleTextView = (TextView) findViewById(R.id.title_textview_details);
        posterImageView = (ImageView) findViewById(R.id.poster_details);
        releaseDateTextView = (TextView) findViewById(R.id.releasedate_textview_details);
        ratingTextView = (TextView) findViewById(R.id.rating_textview_details);
        descriptionTextView = (TextView) findViewById(R.id.description_textview_details);

        Intent intentThatStartedThisActivity = getIntent();
        Movie movie = intentThatStartedThisActivity.getParcelableExtra(MainActivity.MOVIE_KEY);

        titleTextView.setText(movie.getTitle());
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
    }
}
