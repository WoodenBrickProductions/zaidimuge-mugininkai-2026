package com.example.universityjava;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.universityjava.database.Condition;
import com.example.universityjava.database.Game;
import com.example.universityjava.database.Listing;
import com.example.universityjava.database.PhysicalListingAttributes;
import com.example.universityjava.database.Platform;

import java.util.ArrayList;
import java.util.List;

public class CartFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    //private static final String ARG_PARAM1 = "param1";
    //private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    //private String mParam1;
    //private String mParam2;
    private RecyclerView recyclerView;
    private ListingItemAdapter adapter;

    public CartFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        /*Platform platform = new Platform();
        platform.setName("PC");
        AppActivity.getDatabase().platformDAO().insert(platform);
        platform = new Platform();
        platform.setName("Playstation");
        AppActivity.getDatabase().platformDAO().insert(platform);
        /*List<Listing> list = new ArrayList<Listing>();*/
        /*Listing listing = new Listing();
        listing.setIsdigital(true);
        listing.setPrice(10);
        listing.setFk_gameid(1);
        listing.setFk_seller(1);
        listing.setFk_platform(1);
        //list.add(listing);*/
        /*AppActivity.getDatabase().listingDAO().insert(listing);
        listing = new Listing();
        listing.setIsdigital(false);
        listing.setPrice(15);
        listing.setFk_gameid(2);
        listing.setFk_seller(1);
        listing.setFk_platform(2);
        AppActivity.getDatabase().listingDAO().insert(listing);
        PhysicalListingAttributes attr = new PhysicalListingAttributes();
        attr.setFk_listingid(2);
        attr.setFk_condition(Condition.Good);
        attr.setCondition_description("Package is a little scratched");
        attr.setImage("Image go brr");
        AppActivity.getDatabase().physicalListingAttributesDAO().insert(attr);*/
        //list.add(listing);
        //List<Game> gameList = new ArrayList<Game>();
        //Game game = new Game();
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_cart, container, false);
        recyclerView = view.findViewById(R.id.cart_list);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        List<Listing> list = AppActivity.getDatabase().listingDAO().getAllListings();
        if(!list.isEmpty())
            recyclerView.setAdapter(new ListingItemAdapter(list));
        //recyclerView.setAdapter(new ListingItemAdapter(AppActivity.getDatabase().gameDAO().getAllGames()));
        return view;
    }
}