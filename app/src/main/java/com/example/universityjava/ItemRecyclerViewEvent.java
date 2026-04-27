package com.example.universityjava;

import com.example.universityjava.database.Game;
import com.example.universityjava.database.Listing;

public interface ItemRecyclerViewEvent {
    void onItemClick(Listing item);
    void onItemClick(Game item);
    void onEditClick(Listing item);
}
