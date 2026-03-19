package com.example.universityjava;

import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.example.universityjava.database.Listing;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link ListingPageFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ListingPageFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private long listingID;
    private Listing listing;
    private String mParam2;

    public ListingPageFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param2 Parameter 2.
     * @return A new instance of fragment ListingPageFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static ListingPageFragment newInstance(long listingID, String param2) {
        ListingPageFragment fragment = new ListingPageFragment();
        Bundle args = new Bundle();
        args.putLong(ARG_PARAM1, listingID);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            listingID = getArguments().getLong(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);

            listing = AppActivity.getDatabase().listingDAO().getListingByID(listingID);
        }
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        //View view = inflater.inflate(R.layout.fragment_home, container, false);
        View view = inflater.inflate(R.layout.fragment_listing_page, container, false);
        /*Button _buttonCreateListing = (Button) view.findViewById(R.id.buttonCreateListing);
        _buttonCreateListing.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                Intent intent = new Intent(getBaseContext(), LoginActivity.class);
//                startActivity(intent);
            }
        });*/

        /*Button _buttonPopular = (Button) view.findViewById(R.id.buttonPopular);
        _buttonPopular.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Fragment fragment = new MainCategoriesFragment();
                Bundle bundle = new Bundle();
                bundle.putString("name", "Populiariausios prekės");
                fragment.setArguments(bundle);
                ((MainActivity)getActivity()).replaceFragment(fragment);
            }
        });

        Button _buttonNewest = (Button) view.findViewById(R.id.buttonNewest);
        _buttonNewest.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Fragment fragment = new MainCategoriesFragment();
                Bundle bundle = new Bundle();
                bundle.putString("name", "Naujausios prekės");
                fragment.setArguments(bundle);
                ((MainActivity)getActivity()).replaceFragment(fragment);
            }
        });*/

        Button _buttonPhysical = (Button) view.findViewById(R.id.buttonSeller);
        _buttonPhysical.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Fragment fragment = SellerFragment.newInstance(listing.getFk_seller(), "");
                ((MainActivity)getActivity()).replaceFragment(fragment);
            }
        });
        return view;
    }
}