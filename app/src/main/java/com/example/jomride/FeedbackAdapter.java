package com.example.jomride;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class FeedbackAdapter extends RecyclerView.Adapter<FeedbackAdapter.ViewHolder> {

    private List<Feedback> feedbackList;

    public FeedbackAdapter(List<Feedback> feedbackList) {
        this.feedbackList = feedbackList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_feedback, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Feedback feedback = feedbackList.get(position);

        // Bind data to the ViewHolder
        holder.textViewRating.setText("Rating: " + feedback.getRating());
        holder.textViewFeedbackText.setText("Feedback: " + feedback.getFeedbackText());
    }

    @Override
    public int getItemCount() {
        return feedbackList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public TextView textViewRating;
        public TextView textViewFeedbackText;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            textViewRating = itemView.findViewById(R.id.textViewRating);
            textViewFeedbackText = itemView.findViewById(R.id.textViewFeedbackText);
        }
    }
}
