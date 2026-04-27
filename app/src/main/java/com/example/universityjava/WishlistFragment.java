package com.example.universityjava;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.universityjava.database.Game;
import com.example.universityjava.database.Listing;
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


    private RecyclerView recyclerView;

    private List<WishlistListing> list;

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
        recyclerView = view.findViewById(R.id.wishlist_list);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        list = AppActivity.getDatabase().wishlistListingDAO().getListingsByUserId(AppActivity.getCurrentUserID());
        List<Listing> listingList = new ArrayList<Listing>();
        for(int i = 0; i < list.size(); i++)
        {
            var e = list.get(i);
            listingList.add(AppActivity.getDatabase().listingDAO().getListingByID(e.getFk_listingid()));
        }
        if(!listingList.isEmpty()) {
            var listingItemAdapter = new ListingItemAdapter(listingList, (MainActivity)getActivity(), ListingItemAdapter.ListingMode.ADDABLE);
            listingItemAdapter.showWishlist = false;
            recyclerView.setAdapter(listingItemAdapter);
        }
        return view;
    }
}