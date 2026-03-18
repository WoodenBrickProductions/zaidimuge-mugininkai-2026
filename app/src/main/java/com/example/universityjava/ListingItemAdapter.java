package com.example.universityjava;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.universityjava.database.Game;
import com.example.universityjava.database.Listing;

import java.util.List;

public class ListingItemAdapter extends RecyclerView.Adapter<ListingItemAdapter.ListingViewHolder> {

    private boolean isCategory;
    private List<Listing> listings;
    private List<Game> games;
    private final AppDatabase db = AppActivity.getDatabase();
    /// Listing list or Game list to work
    public ListingItemAdapter(List<?> list){
        if (list != null && !list.isEmpty()){
            if(list.get(0) instanceof Listing){
                this.listings = (List<Listing>) list;
                isCategory = false;
            }else if(list.get(0) instanceof Game){
                this.games = (List<Game>) list;
                isCategory = true;
            }
        }
    }
    public static class ListingViewHolder extends RecyclerView.ViewHolder{
        private final TextView title;
        private final TextView price;
        private final ImageView image;
        private final TextView listingsFrom;
        private final Button editButton;
        public ListingViewHolder(View view){
            super(view);

            title = (TextView) view.findViewById(R.id.listing_name);
            price = (TextView) view.findViewById(R.id.listing_price);
            image = (ImageView) view.findViewById(R.id.listing_image);
            listingsFrom = (TextView) view.findViewById(R.id.listings_from);
            editButton = (Button) view.findViewById(R.id.buttonEdit);
        }

        public TextView getTitle(){
            return title;
        }
        public TextView getPrice(){
            return price;
        }
        public ImageView getImage(){
            return image;
        }
        public TextView getListingsFrom(){
            return listingsFrom;
        }
    }
    @NonNull
    @Override
    public ListingItemAdapter.ListingViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.listing_item_editable, parent, false);
        return new ListingViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ListingItemAdapter.ListingViewHolder holder, int position) {
        if(isCategory){
            Game item = games.get(position);
            holder.getTitle().setText(item.getTitle());
            holder.getImage().setImageResource(R.drawable.ic_launcher_background);
            double price = db.gameDAO().getGameMinPriceById(item.getId());
            if(price != 0)
                holder.getPrice().setText(price+" €");
            else{
                holder.getListingsFrom().setText(R.string.NoListings);
                holder.getPrice().setText("");}
        }else{
            Listing item = listings.get(position);
        holder.getTitle().setText(db.listingDAO().getGameNameByListingId(item.getId()));
        holder.getListingsFrom().setVisibility(View.GONE);
        holder.getImage().setImageResource(R.drawable.ic_launcher_background);
        holder.getPrice().setText(item.getPrice()+" €");
        }
        holder.editButton.setVisibility(View.GONE);
    }

    @Override
    public int getItemCount() {
        if(isCategory)
            return games.size();
        else
            return listings.size();
    }
}
