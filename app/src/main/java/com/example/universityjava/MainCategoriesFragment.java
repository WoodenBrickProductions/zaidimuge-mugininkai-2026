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

import com.example.universityjava.database.Game;
import com.example.universityjava.database.Listing;

import java.util.ArrayList;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 * create an instance of this fragment.
 */
public class MainCategoriesFragment extends Fragment implements RecyclerViewEvent {

    String name;
    private List<Listing> list;
    private RecyclerView recyclerView;

    public MainCategoriesFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle args = getArguments();
        if (args != null)
            name = args.getString("name");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_main_categories, container, false);
        TextView textViewName = view.findViewById(R.id.textViewCategoryName);
        textViewName.setText(name);

        RecyclerView recyclerView = view.findViewById(R.id.recyclerViewMainCategories);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false));

        List<Integer> images = new ArrayList<>();
        for (int i = 0; i < 7; i++) {
            images.add(R.drawable.ic_game_test_icon);
        }
        list = AppActivity.getDatabase().listingDAO().getAllListings();
        ImageAdapter adapter = new ImageAdapter(getContext(), images);

//        recyclerView.setAdapter(new ListingItemAdapter(list, this));
        if(!list.isEmpty()) {
            var listingItemAdapter = new ListingItemAdapter(list, this, ListingItemAdapter.ListingMode.ADDABLE);
            recyclerView.setAdapter(listingItemAdapter);
        }
        return view;
    }

    @Override
    public void onItemClick(int position) {
        SharedPreferences prefs = getContext().getSharedPreferences("my_prefs", Context.MODE_PRIVATE);
        Fragment page = ListingPageFragment.newInstance(prefs.getLong("user_id", -1), "");
        ((MainActivity)getActivity()).replaceFragment(page);
    }
}