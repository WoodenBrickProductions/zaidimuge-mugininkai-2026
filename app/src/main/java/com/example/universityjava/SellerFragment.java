package com.example.universityjava;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.example.universityjava.database.Listing;
import com.example.universityjava.database.ListingDAO;
import com.example.universityjava.database.Review;
import com.example.universityjava.database.ReviewDAO;

import java.util.List;

public class SellerFragment extends Fragment implements ReviewRecyclerViewEvent {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    private long userID;
    private String mParam2;
    private boolean isListings = true;

    public SellerFragment() {
        // Required empty public constructor
    }

    public static SellerFragment newInstance(long userID, String param2) {
        SellerFragment fragment = new SellerFragment();
        Bundle args = new Bundle();
        args.putLong(ARG_PARAM1, userID);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            userID = getArguments().getLong(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_seller, container, false);
        // Inflate the layout for this fragment
        Button _buttonCreateListing = (Button) view.findViewById(R.id.createListing);
        _buttonCreateListing.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Fragment fragment = new AddListingFragment();
                ((MainActivity)getActivity()).replaceFragment(fragment);
            }
        });
        SharedPreferences prefs = getContext().getSharedPreferences("my_prefs", Context.MODE_PRIVATE);
        TextView textViewUsername = view.findViewById(R.id.textViewProfileUsername);

        // Inflate the layout for this fragment
        RecyclerView recyclerView = view.findViewById(R.id.listings_list);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        RecyclerView reviewsRecyclerView = view.findViewById(R.id.reviews_list);
        reviewsRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        Button buttonListings = view.findViewById(R.id.buttonListings);
        Button buttonReviews = view.findViewById(R.id.buttonReviews);

        buttonListings.setOnClickListener(v -> {
            recyclerView.setVisibility(View.VISIBLE);
            reviewsRecyclerView.setVisibility(View.GONE);
            isListings = true;
        });

        buttonReviews.setOnClickListener(v -> {
            recyclerView.setVisibility(View.GONE);
            reviewsRecyclerView.setVisibility(View.VISIBLE);
            isListings = false;
        });

        if (userID >= 0) {
            User user = AppActivity.getDatabase().userDAO().getUserByID(userID);
            // todo(Woody): separate out textViews
            textViewUsername.setText(user.getName() + "\n" +
                    "0.0 / 5");

            List<Review> reviews = AppActivity.getDatabase().reviewDAO().getReviewsBySellerID(userID);
            if (!reviews.isEmpty())
                reviewsRecyclerView.setAdapter(new ReviewItemAdapter(reviews, this));
        }

        ListingDAO dao = AppActivity.getDatabase().listingDAO();
        List<Listing> list = dao.getListingsByUserId(userID);

        if(!list.isEmpty()) {
            var listingItemAdapter = new ListingItemAdapter(list, (MainActivity)getActivity(), ListingItemAdapter.ListingMode.EDITABLE);
            recyclerView.setAdapter(listingItemAdapter);
        }
        return view;
    }

    @Override
    public void onItemClick(int position) {
        // TODO(Woody):
        if(!isListings) {
            System.out.println("Reviews");
            ReviewDAO dao = AppActivity.getDatabase().reviewDAO();
            List<Review> list = dao.getReviewsBySellerID(userID);

            Fragment fragment = SellerFragment.newInstance(list.get(position).getFk_buyerid(), "");
            ((MainActivity)getActivity()).replaceFragment(fragment);
        }
    }
}