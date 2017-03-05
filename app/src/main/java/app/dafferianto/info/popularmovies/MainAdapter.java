package app.dafferianto.info.popularmovies;

import android.content.Context;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;

import java.util.List;

import app.dafferianto.info.popularmovies.data.Movie;
import app.dafferianto.info.popularmovies.utilities.NetworkUtils;

public class MainAdapter extends RecyclerView.Adapter<MainAdapter.MainAdapterViewHolder> {
    private List<Movie> movieData;
    private final MainAdapterOnClickHandler clickHandler;

    public interface MainAdapterOnClickHandler {
        void onClick(Movie movie);
    }

    public MainAdapter(MainAdapterOnClickHandler clickHandler) {
        this.clickHandler = clickHandler;
    }

    public List<Movie> getMovieData() {
        return movieData;
    }

    public void setMovieData(List<Movie> movieData) {
        this.movieData = movieData;
        notifyDataSetChanged();
    }

    @Override
    public MainAdapterViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        Context context = viewGroup.getContext();
        int layoutIdForGridItem = R.layout.main_grid_item;
        LayoutInflater inflater = LayoutInflater.from(context);
        boolean shouldAttachToParentImmediately = false;

        View view = inflater.inflate(layoutIdForGridItem, viewGroup, shouldAttachToParentImmediately);
        return new MainAdapterViewHolder(view);
    }

    @Override
    public void onBindViewHolder(MainAdapterViewHolder holder, int position) {
        Context context = holder.posterImageView.getContext();
        Uri uri = NetworkUtils.buildPosterUri(context, movieData.get(position).getPosterPath());
        Picasso.with(context)
                .load(uri)
                .into(holder.posterImageView);
    }

    @Override
    public int getItemCount() {
        if (movieData == null) return 0;
        return movieData.size();
    }

    public class MainAdapterViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        public final ImageView posterImageView;

        public MainAdapterViewHolder(View view) {
            super(view);
            posterImageView = (ImageView) view.findViewById(R.id.poster_main);
            view.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            int adapterPosition = getAdapterPosition();
            Movie movie = movieData.get(adapterPosition);
            clickHandler.onClick(movie);
        }
    }

}
