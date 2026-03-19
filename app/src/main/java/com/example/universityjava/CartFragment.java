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
import android.widget.TextView;
import android.widget.Toast;

import com.example.universityjava.database.Listing;

import java.util.List;

public class CartFragment extends Fragment implements RecyclerViewEvent {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    //private static final String ARG_PARAM1 = "param1";
    //private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    //private String mParam1;
    //private String mParam2;
    private RecyclerView recyclerView;
    private List<Listing> list;

    public CartFragment() {
        // Required empty public constructor
    }
    @Override
    public void onItemClick(int position) {
        Fragment page = ListingPageFragment.newInstance(list.get(position).getId(), "");
        ((MainActivity)getActivity()).replaceFragment(page);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_cart, container, false);
        recyclerView = view.findViewById(R.id.cart_list);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        list = AppActivity.getDatabase().listingDAO().getAllListings();
        if(!list.isEmpty())
            recyclerView.setAdapter(new ListingItemAdapter(list, this));
        return view;
    }


}