package com.example.universityjava;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.SearchView;
import android.widget.Toast;

import com.example.universityjava.database.Listing;

import java.util.ArrayList;
import java.util.List;

public class SearchResultsFragment extends Fragment{
    String _query;
    Boolean _isDigital;
    Integer _platform;
    private List<Listing> list;
    private RecyclerView recyclerView;
    ShakeDetector shakeDetector;

    public SearchResultsFragment() {
        // Required empty public constructor
    }
    boolean _reversed = false;
    boolean _filtersShown = false;

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
//        list = AppActivity.getDatabase().listingDAO().getAllListings();
        list = AppActivity.getDatabase().listingDAO().searchListings(_query, _isDigital, _platform);
        ImageAdapter adapter = new ImageAdapter(getContext(), images);

//        recyclerView.setAdapter(new ListingItemAdapter(list, this));
        if(!list.isEmpty()) {
            var listingItemAdapter = new ListingItemAdapter(list, (MainActivity)getActivity(), ListingItemAdapter.ListingMode.ADDABLE);
            recyclerView.setAdapter(listingItemAdapter);
        }

        setupDropdowns(view, recyclerView);

        View layout = view.findViewById(R.id.filterView);
        layout.setVisibility(View.GONE);
//        layout.setLayoutParams(new ViewGroup.LayoutParams(layout.getWidth(), 0));
        Button button = view.findViewById(R.id.buttonFilter);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                layout.setVisibility(View.GONE);
                _filtersShown = false;
                list = AppActivity.getDatabase().listingDAO().searchListings(_query, _isDigital, _platform);
                var listingItemAdapter = new ListingItemAdapter(list, (MainActivity)getActivity(), ListingItemAdapter.ListingMode.ADDABLE);
                recyclerView.setAdapter(listingItemAdapter);
            }
        });

        Button buttonFilters = view.findViewById(R.id.buttonFilters);
        buttonFilters.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (_filtersShown) {
                    _filtersShown = false;
                    layout.setVisibility(View.GONE);
                }
                else {
                    _filtersShown = true;
                    layout.setVisibility(View.VISIBLE);
                }
            }
        });

        shakeDetector = new ShakeDetector(requireContext(), () -> {
            reverseList(recyclerView, true);
        });

        return view;
    }

    private void reverseList(RecyclerView recyclerView, boolean notify) {
        _reversed = !_reversed;
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, !_reversed));

        Toast.makeText(requireContext(), _reversed ? R.string.order_reversed : R.string.order_restored, Toast.LENGTH_SHORT).show();
    }

    private void setupDropdowns(View view, RecyclerView recyclerView) {
        CustomDropdown sortingDropdown = view.findViewById(R.id.sortingDropdown);
        String[] sortingLabels = {
                getString(R.string.sort_ascending),
                getString(R.string.sort_descending)};
        String[] sortingValues = {"ASC", "DESC"};

        sortingDropdown.setItems(sortingLabels, sortingValues);
        sortingDropdown.setSelectedValue(sortingValues[0]);

        sortingDropdown.setOnValueChanged(mode -> {
            if ((mode.equals("ASC") && _reversed) || (mode.equals("DESC")  && !_reversed))
                reverseList(recyclerView, false);
        });

        CustomDropdown typeDropdown = view.findViewById(R.id.typeDropdown);
        String[] typeLabels = {
                getString(R.string.any),
                getString(R.string.type_digital),
                getString(R.string.type_physical)};
        String[] typeValues = {"ANY", "DIG", "PHY"};

        typeDropdown.setItems(typeLabels, typeValues);
        typeDropdown.setSelectedValue(typeValues[0]);

        typeDropdown.setOnValueChanged(type -> {
            switch (type) {
                case "ANY":
                    _isDigital = null;
                    break;
                case "DIG":
                    _isDigital = true;
                    break;
                case "PHY":
                    _isDigital = false;
                    break;
            }
        });

        CustomDropdown platformDropdown = view.findViewById(R.id.platformDropdown);
        String[] platformLabels = {
                getString(R.string.any),
                "PC",
                "Xbox",
                "PlayStation"};
        String[] platformValues = {"ANY", "PC", "XBOX", "PS"};

        platformDropdown.setItems(platformLabels, platformValues);
        platformDropdown.setSelectedValue(platformValues[0]);

        platformDropdown.setOnValueChanged(platform -> {
            switch (platform) {
                case "ANY":
                    _platform = null;
                    break;
                case "PC":
                    _platform = 1;
                    break;
                case "XBOX":
                    _platform = 2;
                    break;
                case "PS":
                    _platform = 3;
                    break;
            }
        });
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
