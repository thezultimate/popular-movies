package app.dafferianto.info.popularmovies;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

public class TrailerAdapter extends RecyclerView.Adapter<TrailerAdapter.TrailerAdapterViewHolder> {
    private List<String> trailerData;
    private final TrailerAdapterOnClickHandler clickHandler;

    public interface TrailerAdapterOnClickHandler {
        void onClick(String youtubeKey);
    }

    public TrailerAdapter(TrailerAdapterOnClickHandler clickHandler) {
        this.clickHandler = clickHandler;
    }

    public void setTrailerData(List<String> trailerData) {
        this.trailerData = trailerData;
        notifyDataSetChanged();
    }

    @Override
    public TrailerAdapterViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        int layoutIdForTrailerItem = R.layout.detail_trailer_item;
        LayoutInflater inflater = LayoutInflater.from(context);
        boolean shouldAttachToParentImmediately = false;

        View view = inflater.inflate(layoutIdForTrailerItem, parent, shouldAttachToParentImmediately);
        return new TrailerAdapterViewHolder(view);
    }

    @Override
    public void onBindViewHolder(TrailerAdapterViewHolder holder, int position) {
        String trailerKey = trailerData.get(position);
        if (trailerKey != null && !trailerKey.isEmpty()) {
            holder.trailerTextView.setText("Trailer " + (position + 1));
        }
    }

    @Override
    public int getItemCount() {
        if (trailerData == null) return 0;
        return trailerData.size();
    }

    public class TrailerAdapterViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        public final TextView trailerTextView;

        public TrailerAdapterViewHolder(View itemView) {
            super(itemView);
            this.trailerTextView = (TextView) itemView.findViewById(R.id.trailer_textview_details);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            int adapterPosition = getAdapterPosition();
            String youtubeKey = trailerData.get(adapterPosition);
            clickHandler.onClick(youtubeKey);
        }

    }
}
