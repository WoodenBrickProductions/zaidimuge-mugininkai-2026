package com.example.universityjava;

import com.example.universityjava.database.Game;
import com.example.universityjava.database.Listing;

public interface OrderRecyclerViewEvent {
    void onItemClick(Listing item);
    void onEditClick(Listing item);
    void onReviewClick(Listing item);
}
