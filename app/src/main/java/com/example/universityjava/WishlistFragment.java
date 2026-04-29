package com.example.universityjava;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.example.universityjava.database.Game;
import com.example.universityjava.database.Listing;
import com.example.universityjava.database.WishlistGame;
import com.example.universityjava.database.WishlistListing;

import java.util.ArrayList;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link WishlistFragment} factory method to
 * create an instance of this fragment.
 */
public class WishlistFragment extends Fragment{

    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER

    private Button listingButton;
    private Button categoryButton;
    private RecyclerView recyclerView;

    private List<Listing> listList;

    private List<Game> gameList;

    public WishlistFragment() {
        // Required empty public constructor
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_wishlist, container, false);
        categoryButton = view.findViewById(R.id.category_button);
        listingButton = view.findViewById(R.id.listings_button);
        recyclerView = view.findViewById(R.id.wishlist_list);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        listingButton.setSelected(true);
        listList = AppActivity.getDatabase().listingDAO().getWishlistListingsByUserId(AppActivity.getCurrentUserID());
        gameList = AppActivity.getDatabase().gameDAO().getWishlistGameByUserId(AppActivity.getCurrentUserID());
        if(!listList.isEmpty()) {
            var listingItemAdapter = new ListingItemAdapter(listList, (MainActivity)getActivity(), ListingItemAdapter.ListingMode.EDITABLE);
            listingItemAdapter.showEdit = false;
            recyclerView.setAdapter(listingItemAdapter);
        }

        listingButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(listingButton.isSelected()) return;
                categoryButton.setSelected(false);
                listingButton.setSelected(true);
                if(!listList.isEmpty()) {
                    var listingItemAdapter = new ListingItemAdapter(listList, (MainActivity)getActivity(), ListingItemAdapter.ListingMode.EDITABLE);
                    listingItemAdapter.showEdit = false;
                    recyclerView.setAdapter(listingItemAdapter);
                }
            }
        });

        categoryButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(categoryButton.isSelected()) return;
                categoryButton.setSelected(true);
                listingButton.setSelected(false);
                if(!gameList.isEmpty()) {
                    var listingItemAdapter = new GameItemAdapter(gameList, (MainActivity)getActivity(), GameItemAdapter.ListingMode.EDITABLE);
                    listingItemAdapter.showEdit = false;
                    recyclerView.setAdapter(listingItemAdapter);
                }
            }
        });
        return view;
    }
}