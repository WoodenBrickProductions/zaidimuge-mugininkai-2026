package com.example.universityjava;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

public class SteamSearchAdapter extends RecyclerView.Adapter<SteamSearchAdapter.VH> {

    interface OnGameClickListener {
        void onGameClick(SteamSearchFragment.SteamGame game);
    }

    private final List<SteamSearchFragment.SteamGame> items = new ArrayList<>();
    private final OnGameClickListener listener;

    SteamSearchAdapter(OnGameClickListener listener) {
        this.listener = listener;
    }

    void setItems(List<SteamSearchFragment.SteamGame> newItems) {
        items.clear();
        items.addAll(newItems);
        notifyDataSetChanged();
    }

    void clear() {
        items.clear();
        notifyDataSetChanged();
    }

    void updateItem(SteamSearchFragment.SteamGame game) {
        int idx = indexOf(game.appId);
        if (idx >= 0) notifyItemChanged(idx);
    }

    void removeItem(SteamSearchFragment.SteamGame game) {
        int idx = indexOf(game.appId);
        if (idx >= 0) {
            items.remove(idx);
            notifyItemRemoved(idx);
        }
    }

    private int indexOf(int appId) {
        for (int i = 0; i < items.size(); i++) {
            if (items.get(i).appId == appId) return i;
        }
        return -1;
    }

    @NonNull
    @Override
    public VH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_steam_game, parent, false);
        return new VH(v);
    }

    @Override
    public void onBindViewHolder(@NonNull VH holder, int position) {
        SteamSearchFragment.SteamGame game = items.get(position);
        holder.name.setText(game.name);
        holder.description.setText(game.shortDescription != null ? game.shortDescription : "");
        ImageManager.loadFromUrl(game.tinyImageUrl, holder.thumbnail);
        holder.itemView.setOnClickListener(v -> listener.onGameClick(game));
    }

    @Override
    public int getItemCount() { return items.size(); }

    static class VH extends RecyclerView.ViewHolder {
        final ImageView thumbnail;
        final TextView name;
        final TextView description;

        VH(View v) {
            super(v);
            thumbnail = v.findViewById(R.id.steamGameThumbnail);
            name = v.findViewById(R.id.steamGameName);
            description = v.findViewById(R.id.steamGameDescription);
        }
    }
}
