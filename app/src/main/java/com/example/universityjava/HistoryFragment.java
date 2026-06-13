package com.example.universityjava;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.universityjava.database.Listing;
import com.example.universityjava.database.Order;

import java.io.File;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link HistoryFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class HistoryFragment extends Fragment implements OrderRecyclerViewEvent {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";

    private ImageView imageViewProfile;
    private RecyclerView recyclerView;
    private List<Order> purchasesList;
    private List<Order> salesList;

    // TODO: Rename and change types of parameters
    private long userID;

    public HistoryFragment() {
        // Required empty public constructor
    }

    public static HistoryFragment newInstance(long userID) {
        HistoryFragment fragment = new HistoryFragment();
        Bundle args = new Bundle();
        args.putLong(ARG_PARAM1, userID);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            userID = getArguments().getLong(ARG_PARAM1);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_history, container, false);
        SharedPreferences prefs = requireContext().getSharedPreferences("my_prefs", Context.MODE_PRIVATE);
        if(userID <= 0) userID = AppActivity.getCurrentUserID();

        AppDatabase db = AppActivity.getDatabase();
        TextView textViewUsername = view.findViewById(R.id.textViewUsername);
        TextView textViewRating = view.findViewById(R.id.textViewUserRating);
        imageViewProfile = view.findViewById(R.id.imageViewProfile);
        imageViewProfile.setImageResource(R.drawable.baseline_person_outline_24);
        recyclerView = view.findViewById(R.id.history_list);
        Button _buttonPurchases = view.findViewById(R.id.purchases_button);
        Button _buttonSales = view.findViewById(R.id.sales_button);

        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        _buttonPurchases.setSelected(true);
        purchasesList = AppActivity.getDatabase().orderDAO().getOrdersByUserId(AppActivity.getCurrentUserID());
        salesList = AppActivity.getDatabase().orderDAO().getOrdersBySellerId(AppActivity.getCurrentUserID());
        if(!purchasesList.isEmpty()) {
            var listingItemAdapter = new OrderItemAdapter(purchasesList, this, OrderItemAdapter.OrderMode.PURCHASES);
            //listingItemAdapter.showEdit = false;
            //listingItemAdapter.isInWishlistFragment = true;
            recyclerView.setAdapter(listingItemAdapter);
        }

        _buttonPurchases.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(_buttonPurchases.isSelected()) return;
                _buttonSales.setSelected(false);
                _buttonPurchases.setSelected(true);
                var listingItemAdapter = new OrderItemAdapter(purchasesList, HistoryFragment.this, OrderItemAdapter.OrderMode.PURCHASES);
                //listingItemAdapter.showEdit = false;
                //listingItemAdapter.isInWishlistFragment = true;
                recyclerView.setAdapter(listingItemAdapter);
            }
        });


        _buttonSales.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(_buttonSales.isSelected()) return;
                _buttonPurchases.setSelected(false);
                _buttonSales.setSelected(true);
                var listingItemAdapter = new OrderItemAdapter(salesList, HistoryFragment.this, OrderItemAdapter.OrderMode.SALES);
                //listingItemAdapter.showEdit = false;
                //listingItemAdapter.isInWishlistFragment = true;
                recyclerView.setAdapter(listingItemAdapter);
            }
        });



        if (userID >= 0) {
            User user = db.userDAO().getUserByID(userID);
            textViewUsername.setText(user.getName());
            textViewRating.setText("");
            loadProfileImage(user);
        }

        return view;
    }

    private void loadProfileImage(User user) {
        if (imageViewProfile == null || user.getProfileImage() == null) return;
        File f = AppActivity.getCachedImageFile(requireContext(), user.getProfileImage());
        if (f != null) imageViewProfile.setImageURI(Uri.fromFile(f));
    }

    @Override
    public void onItemClick(Listing item) {
        ((MainActivity) requireActivity()).onItemClick(item);
    }

    @Override
    public void onEditClick(Listing item) {

    }

    @Override
    public void onReviewClick(Listing item) {

    }

    @Override
    public void onOrderStateDropdownClick(Listing item) {

    }
}