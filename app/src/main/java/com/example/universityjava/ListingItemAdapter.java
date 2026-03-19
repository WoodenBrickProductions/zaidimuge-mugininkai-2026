package com.example.universityjava;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.universityjava.database.CartListing;
import com.example.universityjava.database.Game;
import com.example.universityjava.database.Listing;
import com.example.universityjava.database.WishlistListing;

import java.util.List;

public class ListingItemAdapter extends RecyclerView.Adapter<ListingItemAdapter.ListingViewHolder> {

    public enum ListingMode {
        EDITABLE,
        ADDABLE
    }
    private boolean isCategory;
    private List<Listing> listings;
    private List<Game> games;
    private static RecyclerViewEvent listener;
    public ListingMode listingMode;
    public boolean showDelete = true;
    public boolean showEdit = true;
    public boolean showWishlist = true;
    public boolean showCart = true;
    private final AppDatabase db = AppActivity.getDatabase();
    /// Listing list or Game list to work
    public ListingItemAdapter(List<?> list, RecyclerViewEvent listener, ListingMode mode){
        ListingItemAdapter.listener = listener;
        this.listingMode = mode;
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
    public static class ListingViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private Listing listing;
        private final TextView title;
        private final TextView price;
        private final ImageView image;
        private final TextView listingsFrom;
        private final Button editButton;
        private final Button deleteButton;
        private final Button wishlistButton;
        private final Button cartButton;
        public ListingViewHolder(View view) {
            super(view);

            title = (TextView) view.findViewById(R.id.listing_name);
            price = (TextView) view.findViewById(R.id.listing_price);
            image = (ImageView) view.findViewById(R.id.listing_image);
            listingsFrom = (TextView) view.findViewById(R.id.listings_from);
            editButton = (Button) view.findViewById(R.id.buttonEdit);
            deleteButton = view.findViewById(R.id.buttonDelete);
            cartButton = view.findViewById(R.id.buttonCart);
            wishlistButton = view.findViewById(R.id.buttonWishlist);
            view.setOnClickListener(this);
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
        @Override
        public void onClick(View v) {
            int position = getAbsoluteAdapterPosition();
            if(position != RecyclerView.NO_POSITION){
                listener.onItemClick(position);
            }
        }
    }
    @NonNull
    @Override
    public ListingItemAdapter.ListingViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        switch (listingMode) {
            case EDITABLE -> {
                View view = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.listing_item_editable, parent, false);
                return new ListingViewHolder(view);
            }
            default -> { // ADDABLE
                View view = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.listing_item_addable, parent, false);
                return new ListingViewHolder(view);
            }
        }
    }

    @Override
    public void onBindViewHolder(@NonNull ListingItemAdapter.ListingViewHolder holder, int position) {
        var listing = listings.get(position);
        if(isCategory){
            Game item = games.get(position);
            holder.getTitle().setText(item.getTitle());
            holder.getImage().setImageResource(R.drawable.ic_launcher_background);
            double price = db.gameDAO().getGameMinPriceById(item.getId());
            if(price != 0) {
                holder.getPrice().setText(price+" €");
            }
            else{
                holder.getListingsFrom().setText(R.string.NoListings);
                holder.getPrice().setText("");}
        }else {
            holder.getTitle().setText(db.listingDAO().getGameNameByListingId(listing.getId()));
            holder.getListingsFrom().setVisibility(View.GONE);
            holder.getImage().setImageResource(R.drawable.ic_launcher_background);
            holder.getPrice().setText(listing.getPrice() + " €");
        }
        if(listingMode == ListingMode.EDITABLE)
        {
            holder.editButton.setVisibility(showEdit ? ViewGroup.VISIBLE : View.GONE);
            holder.deleteButton.setVisibility(showDelete ? ViewGroup.VISIBLE : View.GONE);
            holder.editButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Toast.makeText(view.getContext(), "Editing!", Toast.LENGTH_SHORT).show();
                    // TODO: Add listing edit
                }
            });
            holder.deleteButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Toast.makeText(view.getContext(), "Deleting!", Toast.LENGTH_SHORT).show();
                    // TODO: Add listing delete
                }
            });
        }
        else
        {
            holder.wishlistButton.setVisibility(showWishlist ? ViewGroup.VISIBLE : View.GONE);
            holder.cartButton.setVisibility(showCart ? ViewGroup.VISIBLE : View.GONE);
            holder.wishlistButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Toast.makeText(view.getContext(), "Wishlist!", Toast.LENGTH_SHORT).show();
                    var wListing = new WishlistListing();
                    wListing.setFk_listingid(listing.getId());
                    wListing.setFk_userid(AppActivity.getCurrentUserID());
                    if(AppActivity.getDatabase().wishlistListingDAO().getListingByListingID(listing.getId()).size() == 0) {
                        AppActivity.getDatabase().wishlistListingDAO().insert(wListing);
                    }
                }
            });
            holder.cartButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    var wListing = new CartListing();
                    wListing.setFk_listingid(listing.getId());
                    wListing.setFk_userid(AppActivity.getCurrentUserID());
                    if(AppActivity.getDatabase().cartListingDAO().getListingByListingID(listing.getId()).size() == 0) {
                        AppActivity.getDatabase().cartListingDAO().insert(wListing);
                    }
                    Toast.makeText(view.getContext(), "Cart!", Toast.LENGTH_SHORT).show();
                    // TODO: Add listing delete
                }
            });
        }
    }

    @Override
    public int getItemCount() {
        if(isCategory)
            return games.size();
        else
            return listings.size();
    }
}
