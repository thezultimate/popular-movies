package app.dafferianto.info.popularmovies;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

public class ReviewAdapter extends RecyclerView.Adapter<ReviewAdapter.ReviewAdapterViewHolder> {
    private List<String> reviewData;

    public void setReviewData(List<String> reviewData) {
        this.reviewData = reviewData;
        notifyDataSetChanged();
    }

    @Override
    public ReviewAdapterViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        int layoutIdForReviewItem = R.layout.detail_review_item;
        LayoutInflater inflater = LayoutInflater.from(context);
        boolean shouldAttachToParentImmediately = false;

        View view = inflater.inflate(layoutIdForReviewItem, parent, shouldAttachToParentImmediately);
        return new ReviewAdapterViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ReviewAdapterViewHolder holder, int position) {
        String review = reviewData.get(position);
        if (review != null && !review.isEmpty()) {
            holder.reviewTextView.setText(review);
        }
    }

    @Override
    public int getItemCount() {
        if (reviewData == null) return 0;
        return reviewData.size();
    }

    public class ReviewAdapterViewHolder extends RecyclerView.ViewHolder {
        public final TextView reviewTextView;

        public ReviewAdapterViewHolder(View itemView) {
            super(itemView);
            this.reviewTextView = (TextView) itemView.findViewById(R.id.review_textview_details);
        }

    }

}
