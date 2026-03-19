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
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.universityjava.database.Game;
import com.example.universityjava.database.Listing;
import com.example.universityjava.database.ListingDAO;
import com.example.universityjava.database.PhysicalListingAttributes;
import com.example.universityjava.database.Platform;
import com.example.universityjava.database.Review;

import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link SellerFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class SellerFragment extends Fragment implements RecyclerViewEvent {

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
        SharedPreferences prefs = getContext().getSharedPreferences("my_prefs", Context.MODE_PRIVATE);
        TextView textViewUsername = view.findViewById(R.id.textViewProfileUsername);

        // Inflate the layout for this fragment
        RecyclerView recyclerView = view.findViewById(R.id.listings_list);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        long userID = prefs.getLong("user_id", -1);
        if (userID >= 0) {
            User user = AppActivity.getDatabase().userDAO().getUserByID(userID);
            // todo(Tautvydas): separate out textViews
            textViewUsername.setText(user.getName() + "\n" +
                    "0.0 / 5");
        }

        ListingDAO dao = AppActivity.getDatabase().listingDAO();
        List<Listing> list = dao.getAllListings();

        if(!list.isEmpty())
            recyclerView.setAdapter(new ListingItemAdapter(list, this));
        return view;
    }

    @Override
    public void onItemClick(int position) {
        // TODO(Tautvydas):
        Fragment page = new ListingPageFragment();
        ((MainActivity)getActivity()).replaceFragment(page);
    }
}