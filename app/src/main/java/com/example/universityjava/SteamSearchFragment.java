package com.example.universityjava;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class SteamSearchFragment extends Fragment {

    public static final String REQUEST_KEY = "steam_search_result";
    public static final String RESULT_NAME = "name";
    public static final String RESULT_DESCRIPTION = "description";
    public static final String RESULT_HEADER_URL = "header_url";

    private static final String ARG_QUERY = "query";

    public static class SteamGame {
        public final int appId;
        public final String name;
        public final String tinyImageUrl;
        public String shortDescription;
        public String headerImageUrl;

        SteamGame(int appId, String name, String tinyImageUrl) {
            this.appId = appId;
            this.name = name;
            this.tinyImageUrl = tinyImageUrl;
        }
    }

    private SteamSearchAdapter adapter;
    private ProgressBar progressBar;
    private TextView noResultsText;
    private EditText searchField;

    private final ExecutorService executor = Executors.newFixedThreadPool(5);
    private final Handler mainHandler = new Handler(Looper.getMainLooper());

    public static SteamSearchFragment newInstance(String query) {
        SteamSearchFragment f = new SteamSearchFragment();
        Bundle args = new Bundle();
        args.putString(ARG_QUERY, query != null ? query : "");
        f.setArguments(args);
        return f;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_steam_search, container, false);

        view.findViewById(R.id.buttonBackSteam).setOnClickListener(v ->
                requireActivity().getSupportFragmentManager().popBackStack());

        progressBar   = view.findViewById(R.id.steamProgressBar);
        noResultsText = view.findViewById(R.id.steamNoResults);
        searchField   = view.findViewById(R.id.steamSearchField);

        RecyclerView recyclerView = view.findViewById(R.id.steamRecyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));

        adapter = new SteamSearchAdapter(game -> {
            if (game.headerImageUrl != null) {
                sendResult(game);
            } else {
                // Details not yet fetched — fetch them now before returning
                progressBar.setVisibility(View.VISIBLE);
                executor.submit(() -> {
                    doFetchDetails(game);
                    mainHandler.post(() -> {
                        if (!isAdded()) return;
                        progressBar.setVisibility(View.GONE);
                        sendResult(game);
                    });
                });
            }
        });
        recyclerView.setAdapter(adapter);

        view.findViewById(R.id.buttonSteamSearch).setOnClickListener(v -> {
            String query = searchField.getText().toString().trim();
            if (!query.isEmpty()) performSearch(query);
        });

        String initial = getArguments() != null ? getArguments().getString(ARG_QUERY, "") : "";
        searchField.setText(initial);
        if (!initial.isEmpty()) performSearch(initial);

        return view;
    }

    private void sendResult(SteamGame game) {
        Bundle result = new Bundle();
        result.putString(RESULT_NAME, game.name);
        result.putString(RESULT_DESCRIPTION, game.shortDescription != null ? game.shortDescription : "");
        result.putString(RESULT_HEADER_URL, game.headerImageUrl != null ? game.headerImageUrl : "");
        getParentFragmentManager().setFragmentResult(REQUEST_KEY, result);
        requireActivity().getSupportFragmentManager().popBackStack();
    }

    private void performSearch(String query) {
        progressBar.setVisibility(View.VISIBLE);
        noResultsText.setVisibility(View.GONE);
        adapter.clear();

        executor.submit(() -> {
            List<SteamGame> results = fetchSearchResults(query);
            mainHandler.post(() -> {
                if (!isAdded()) return;
                progressBar.setVisibility(View.GONE);
                if (results.isEmpty()) {
                    noResultsText.setVisibility(View.VISIBLE);
                    return;
                }
                adapter.setItems(results);
                for (SteamGame game : results) {
                    executor.submit(() -> {
                        doFetchDetails(game);
                        mainHandler.post(() -> {
                            if (!isAdded()) return;
                            if (game.headerImageUrl == null) {
                                // Adult content — remove from list
                                adapter.removeItem(game);
                            } else {
                                adapter.updateItem(game);
                            }
                        });
                    });
                }
            });
        });
    }

    private List<SteamGame> fetchSearchResults(String query) {
        try {
            String encoded = URLEncoder.encode(query, "UTF-8");
            String urlStr = "https://store.steampowered.com/api/storesearch/?term="
                    + encoded + "&l=english&cc=US&filter=games";
            HttpURLConnection conn = (HttpURLConnection) new URL(urlStr).openConnection();
            conn.setConnectTimeout(8000);
            conn.setReadTimeout(12000);

            StringBuilder sb = new StringBuilder();
            try (BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream()))) {
                String line;
                while ((line = br.readLine()) != null) sb.append(line);
            }
            conn.disconnect();

            JSONObject json = new JSONObject(sb.toString());
            JSONArray items = json.optJSONArray("items");
            if (items == null) return Collections.emptyList();

            List<SteamGame> games = new ArrayList<>();
            for (int i = 0; i < items.length(); i++) {
                JSONObject item = items.getJSONObject(i);
                int appId     = item.optInt("id");
                String name   = item.optString("name", "");
                String thumb  = item.optString("tiny_image", "");
                games.add(new SteamGame(appId, name, thumb));
            }
            return games;
        } catch (Exception e) {
            e.printStackTrace();
            return Collections.emptyList();
        }
    }

    // Blocking — must be called from a background thread.
    // On return, game.shortDescription and game.headerImageUrl are set,
    // or left null if the game is for adults or the call failed.
    private void doFetchDetails(SteamGame game) {
        try {
            String urlStr = "https://store.steampowered.com/api/appdetails?appids=" + game.appId;
            HttpURLConnection conn = (HttpURLConnection) new URL(urlStr).openConnection();
            conn.setConnectTimeout(8000);
            conn.setReadTimeout(12000);

            StringBuilder sb = new StringBuilder();
            try (BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream()))) {
                String line;
                while ((line = br.readLine()) != null) sb.append(line);
            }
            conn.disconnect();

            JSONObject json     = new JSONObject(sb.toString());
            JSONObject appEntry = json.optJSONObject(String.valueOf(game.appId));
            if (appEntry == null || !appEntry.optBoolean("success", false)) return;

            JSONObject data = appEntry.optJSONObject("data");
            if (data == null) return;

            int requiredAge = 0;
            try {
                requiredAge = Integer.parseInt(data.optString("required_age", "0").trim());
            } catch (NumberFormatException ignored) {}

            if (requiredAge >= 18) return; // leave fields null to signal adult content

            game.shortDescription = data.optString("short_description", "");
            game.headerImageUrl   = data.optString("header_image", "");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        executor.shutdownNow();
    }
}
