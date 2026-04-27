package com.example.universityjava;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.universityjava.database.Review;

import java.util.List;

public class ReviewItemAdapter extends RecyclerView.Adapter<ReviewItemAdapter.ViewHolder> {

    private final List<Review> reviews;
    private ReviewRecyclerViewEvent listener;


    public ReviewItemAdapter(List<Review> list, ReviewRecyclerViewEvent listener) {
        this.reviews = list;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.listing_review, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Review review = reviews.get(position);
        holder.buyerId.setText(AppActivity.getDatabase().userDAO().getUserByID(review.getFk_buyerid()).getName());
        holder.rating.setText(review.getRating() + " / 5");
        holder.comment.setText(review.getComment());
        holder.listener = listener;
    }

    @Override
    public int getItemCount() {
        return reviews.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        TextView buyerId, rating, comment;
        ReviewRecyclerViewEvent listener;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            buyerId = itemView.findViewById(R.id.reviewBuyerId);
            rating = itemView.findViewById(R.id.reviewRating);
            comment = itemView.findViewById(R.id.reviewComment);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            int position = getAbsoluteAdapterPosition();
            if(position != RecyclerView.NO_POSITION){
                listener.onItemClick(position);
            }
        }
    }
}