package com.example.universityjava;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SearchView;
import android.widget.Toast;

import com.example.universityjava.database.Listing;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class SearchResultsFragment extends Fragment{
    String _query;
    private List<Listing> list;
    private RecyclerView recyclerView;
    ShakeDetector shakeDetector;

    public SearchResultsFragment() {
        // Required empty public constructor
    }
    boolean reversed = false;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle args = getArguments();
        if (args != null)
            _query = args.getString("query");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_search_results, container, false);
        SearchView searchView = view.findViewById(R.id.searchViewResult);
        searchView.setQuery(_query, false);
        searchView.setIconified(false);
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

        RecyclerView recyclerView = view.findViewById(R.id.recyclerViewSearchResults);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false));

        List<Integer> images = new ArrayList<>();
        for (int i = 0; i < 7; i++) {
            images.add(R.drawable.ic_game_test_icon);
        }
        list = AppActivity.getDatabase().listingDAO().getAllListings();
        ImageAdapter adapter = new ImageAdapter(getContext(), images);

//        recyclerView.setAdapter(new ListingItemAdapter(list, this));
        if(!list.isEmpty()) {
            var listingItemAdapter = new ListingItemAdapter(list, (MainActivity)getActivity(), ListingItemAdapter.ListingMode.ADDABLE);
            recyclerView.setAdapter(listingItemAdapter);
        }

        shakeDetector = new ShakeDetector(requireContext(), () -> {
            reversed = !reversed;
            Collections.reverse(list);
            adapter.notifyDataSetChanged();

            Toast.makeText(requireContext(), reversed ? R.string.order_reversed : R.string.order_restored, Toast.LENGTH_SHORT).show();
        });

        return view;
    }

    @Override
    public void onPause() {
        super.onPause();
        shakeDetector.stop();
    }

    @Override
    public void onResume() {
        super.onResume();
        shakeDetector.start();
    }
}
