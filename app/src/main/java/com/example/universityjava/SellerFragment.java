package com.example.universityjava;

import android.content.Context;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.universityjava.database.Listing;
import com.example.universityjava.database.ListingDAO;

import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link SellerFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class SellerFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public SellerFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment SellerFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static SellerFragment newInstance(String param1, String param2) {
        SellerFragment fragment = new SellerFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        View view = inflater.inflate(R.layout.fragment_seller, container, false);

        Context context = view.getContext();

//        parent.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,
//                LinearLayout.LayoutParams.WRAP_CONTENT));
//        parent.setOrientation(LinearLayout.HORIZONTAL);

//children of parent linearlayout
//children of layout2 LinearLayout

        // Inflate the layout for this fragment
        RecyclerView recyclerView = view.findViewById(R.id.listings_list);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        ListingDAO dao = AppActivity.getDatabase().listingDAO();
        Listing listing = new Listing();
        listing.setIssold(false);
        listing.setPrice(0.99d);
        listing.setFk_seller(0);
        listing.setIsdigital(true);
        listing.setFk_gameid(0);
        listing.setFk_platform(0);
        dao.insert(listing);
        List<Listing> list = dao.getAllListings();
        if(!list.isEmpty())
            recyclerView.setAdapter(new ListingItemAdapter(list));
        return view;
    }
}