package com.example.universityjava;

import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.SearchView;

import java.util.ArrayList;
import java.util.List;

public class HomeFragment extends Fragment {
    Button _buttonCreateListing;
    Button _buttonPopular;
    Button _buttonNewest;
    Button _buttonPhysical;

    public HomeFragment() {

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_home, container, false);
        _buttonCreateListing = (Button) view.findViewById(R.id.buttonCreateListing);
        _buttonCreateListing.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Fragment fragment = new AddListingFragment();
                ((MainActivity)getActivity()).replaceFragment(fragment);
            }
        });

        _buttonPopular = (Button) view.findViewById(R.id.buttonPopular);
        _buttonPopular.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Fragment fragment = new MainCategoriesFragment();
                Bundle bundle = new Bundle();
                bundle.putString("name", getResources().getString(R.string.popular_items));
                fragment.setArguments(bundle);
                ((MainActivity)getActivity()).replaceFragment(fragment);
            }
        });

        _buttonNewest = (Button) view.findViewById(R.id.buttonNewest);
        _buttonNewest.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Fragment fragment = new MainCategoriesFragment();
                Bundle bundle = new Bundle();
                bundle.putString("name", getResources().getString(R.string.newest_items));
                fragment.setArguments(bundle);
                ((MainActivity)getActivity()).replaceFragment(fragment);
            }
        });

        _buttonPhysical = (Button) view.findViewById(R.id.buttonPhysical);
        _buttonPhysical.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Fragment fragment = new MainCategoriesFragment();
                Bundle bundle = new Bundle();
                bundle.putString("name", getResources().getString(R.string.physical_items));
                fragment.setArguments(bundle);
                ((MainActivity)getActivity()).replaceFragment(fragment);
            }
        });

        SearchView searchView = view.findViewById(R.id.searchMain);
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextChange(String s) {
                return false;
            }

            @Override
            public boolean onQueryTextSubmit(String s) {
                Fragment fragment = new SearchResultsFragment();
                Bundle bundle = new Bundle();
                bundle.putString("query", s);
                fragment.setArguments(bundle);
                ((MainActivity)getActivity()).replaceFragment(fragment);
                return true;
            }
        });

        RecyclerView popularRW = view.findViewById(R.id.listPopular);
        RecyclerView newestRW = view.findViewById(R.id.listNewest);
        RecyclerView physicalRW = view.findViewById(R.id.listPhysical);

        popularRW.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));
        newestRW.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));
        physicalRW.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));

//        popularRW.setNestedScrollingEnabled(false);
//        newestRW.setNestedScrollingEnabled(false);
//        physicalRW.setNestedScrollingEnabled(false);

        List<Integer> images = new ArrayList<>();
        for (int i = 0; i < 7; i++) {
            images.add(R.drawable.ic_game_test_icon);
        }
        ImageAdapter popularAdapter = new ImageAdapter(getContext(), images);
        ImageAdapter newestAdapter = new ImageAdapter(getContext(), images);
        ImageAdapter physicalAdapter = new ImageAdapter(getContext(), images);

        popularRW.setAdapter(popularAdapter);
        newestRW.setAdapter(newestAdapter);
        physicalRW.setAdapter(physicalAdapter);
        return view;
    }
}