package com.example.universityjava;

import android.content.Context;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.universityjava.database.Listing;
import com.example.universityjava.database.Order;
import com.example.universityjava.database.Review;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link ReviewFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ReviewFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";

    private ImageView imageViewProfile;
    private RecyclerView recyclerView;
    private Listing purchaseList;
    private List<Button> ratingButtonList;
    private int ratingValue;
    private String reviewDescription = "";

    // TODO: Rename and change types of parameters
    private long userID;
    private long listingID;

    public ReviewFragment() {
        // Required empty public constructor
    }

    public static ReviewFragment newInstance(long listing) {
        ReviewFragment fragment = new ReviewFragment();
        Bundle args = new Bundle();
        args.putLong(ARG_PARAM1, listing);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            listingID = getArguments().getLong(ARG_PARAM1);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_review, container, false);
        SharedPreferences prefs = requireContext().getSharedPreferences("my_prefs", Context.MODE_PRIVATE);
        userID = AppActivity.getCurrentUserID();


        AppDatabase db = AppActivity.getDatabase();
        TextView textViewUsername = view.findViewById(R.id.textViewUsername);
        TextView textViewRating = view.findViewById(R.id.textViewUserRating);
        imageViewProfile = view.findViewById(R.id.imageViewProfile);
        imageViewProfile.setImageResource(R.drawable.baseline_person_outline_24);
        // Listing initializing
        recyclerView = view.findViewById(R.id.review_listing);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        purchaseList = AppActivity.getDatabase().listingDAO().getListingByID(listingID);
        var listingItemAdapter = new ListingItemAdapter( Arrays.asList(purchaseList), (MainActivity) getActivity(),
                ListingItemAdapter.ListingMode.EDITABLE);
        listingItemAdapter.showEdit = false;
        listingItemAdapter.showDelete = false;
        recyclerView.setAdapter(listingItemAdapter);

        // Seller initializing
        TextView textViewSellerName = view.findViewById(R.id.textSellerName);
        ImageView imageViewSeller = view.findViewById(R.id.imageViewSellerProfile);
        imageViewSeller.setImageResource(R.drawable.baseline_person_outline_24);
        User seller = db.userDAO().getUserByID(purchaseList.getFk_seller());
        textViewSellerName.setText(seller.getName());
        loadProfileImage(seller, imageViewSeller);

        Review oldReview = db.reviewDAO().getReviewByListingID(listingID);

        Button _buttonStar1 = view.findViewById(R.id.buttonStar1);
        Button _buttonStar2 = view.findViewById(R.id.buttonStar2);
        Button _buttonStar3 = view.findViewById(R.id.buttonStar3);
        Button _buttonStar4 = view.findViewById(R.id.buttonStar4);
        Button _buttonStar5 = view.findViewById(R.id.buttonStar5);
        ratingButtonList = new ArrayList<>();
        ratingButtonList.add(_buttonStar1);
        ratingButtonList.add(_buttonStar2);
        ratingButtonList.add(_buttonStar3);
        ratingButtonList.add(_buttonStar4);
        ratingButtonList.add(_buttonStar5);

        EditText description = view.findViewById(R.id.reviewDescription);
        if(oldReview != null){
            ratingValue = oldReview.getRating();
            description.setText(oldReview.getComment());
            setStarButtons(ratingValue);
        }else{
            _buttonStar1.setSelected(true);
            ratingValue = 1;
        }

        Button _buttonSubmit = view.findViewById(R.id.buttonSubmit);



        _buttonSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                reviewDescription = description.getText().toString();
                Review newReview = new Review();
                newReview.setFk_buyerid(userID);
                newReview.setFk_listingid(listingID);
                newReview.setComment(reviewDescription);
                newReview.setRating(ratingValue);
                if(oldReview!= null){
                    db.reviewDAO().update(newReview);
                } else
                { db.reviewDAO().insert(newReview);}
                ((MainActivity) requireActivity()).replaceFragment(
                        SellerFragment.newInstance(seller.getId(), "reviews"));
            }
        });

        if (userID >= 0) {
            User user = db.userDAO().getUserByID(userID);
            textViewUsername.setText(user.getName());
            double rating = db.reviewDAO().getAverageRatingBySellerID(userID);
            textViewRating.setText(String.format("%,.2f / 5",rating));
            loadProfileImage(user, imageViewProfile);
        }

        _buttonStar1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(ratingValue == 1) return;
                ratingValue = 1;
                setStarButtons(ratingValue);
            }
        });
        _buttonStar2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(ratingValue == 2) return;
                ratingValue = 2;
                setStarButtons(ratingValue);
            }
        });
        _buttonStar3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(ratingValue == 3) return;
                ratingValue = 3;
                setStarButtons(ratingValue);
            }
        });
        _buttonStar4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(ratingValue == 4) return;
                ratingValue = 4;
                setStarButtons(ratingValue);
            }
        });
        _buttonStar5.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(ratingValue == 5) return;
                ratingValue = 5;
                setStarButtons(ratingValue);
            }
        });
        return view;
    }

    private void loadProfileImage(User user, ImageView view) {
        if (view == null || user.getProfileImage() == null) return;
        File f = AppActivity.getCachedImageFile(requireContext(), user.getProfileImage());
        if (f != null) view.setImageURI(Uri.fromFile(f));
    }

    private void setStarButtons(int RatingValue){
        for(int i = 0; i < ratingValue; i++){
            ratingButtonList.get(i).setSelected(true);
        }
        for(int i = ratingValue; i < 5; i++){
            ratingButtonList.get(i).setSelected(false);
        }
    }
}