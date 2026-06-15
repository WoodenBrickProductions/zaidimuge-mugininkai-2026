package com.example.universityjava;

import android.animation.Animator;
import android.animation.AnimatorInflater;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.universityjava.database.Game;
import com.example.universityjava.database.WishlistGame;

import java.io.File;
import java.util.List;

public class GameItemAdapter extends RecyclerView.Adapter<GameItemAdapter.GameViewHolder> {
    public enum ListingMode {
        EDITABLE,
        ADDABLE
    }
    private List<Game> games;
    private static ItemRecyclerViewEvent listener;
    public ListingMode listingMode;
    public boolean showDelete = true;
    public boolean showEdit = true;
    public boolean showWishlist = true;
    public boolean showCart = true;
    private final AppDatabase db = AppActivity.getDatabase();
    /// Listing list or Game list to work
    public GameItemAdapter(List<Game> list, ItemRecyclerViewEvent listener, ListingMode mode){
        GameItemAdapter.listener = listener;
        this.listingMode = mode;
        if (list != null && !list.isEmpty()){
            this.games = list;
        }
    }
    public static class GameViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private LinearLayout itemHolder;
        private Game game;
        private final TextView title;
        private final TextView price;
        private final ImageView image;
        private final TextView listingsFrom;
        private final ImageButton editButton;
        private final ImageButton deleteButton;
        private final ImageButton wishlistButton;
        private final ImageButton cartButton;
        private final Animator wishlistBounce;
        private final Animator deleteBounce;
        private final Animator fadeOutGame;
        public GameViewHolder(View view) {
            super(view);

            title = (TextView) view.findViewById(R.id.listing_name);
            price = (TextView) view.findViewById(R.id.listing_price);
            image = (ImageView) view.findViewById(R.id.listing_image);
            listingsFrom = (TextView) view.findViewById(R.id.listings_from);
            editButton = view.findViewById(R.id.buttonFirst);
            deleteButton = view.findViewById(R.id.buttonSecond);
            cartButton = view.findViewById(R.id.buttonCart);
            wishlistButton = view.findViewById(R.id.buttonWishlist);
            itemHolder = view.findViewById(R.id.item_holder);
            view.setOnClickListener(this);
            fadeOutGame = AnimatorInflater.loadAnimator(view.getContext(),R.animator.listing_fade_out);
            fadeOutGame.setTarget(itemHolder);
            wishlistBounce = AnimatorInflater.loadAnimator(view.getContext(), R.animator.overshoot_bounce);
            deleteBounce = AnimatorInflater.loadAnimator(view.getContext(), R.animator.overshoot_bounce);
            wishlistBounce.setTarget(wishlistButton);
            deleteBounce.setTarget(deleteButton);
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
        public LinearLayout getItemHolder() {
            return itemHolder;
        }
        public ImageButton getWishlistButton(){
            return wishlistButton;
        }
        public ImageButton getEditButton(){
            return editButton;
        }
        public ImageButton getDeleteButton(){
            return deleteButton;
        }
        public ImageButton getCartButton(){
            return cartButton;
        }
        public void SetItem(Game game){
            this.game = game;
        }
        @Override
        public void onClick(View v) {
            int position = getAbsoluteAdapterPosition();
            if(position != RecyclerView.NO_POSITION){
                listener.onItemClick(game);
            }
        }
    }
    @NonNull
    @Override
    public GameViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        switch (listingMode) {
            case EDITABLE -> {
                View view = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.listing_item_editable, parent, false);
                return new GameViewHolder(view);
            }
            default -> { // ADDABLE
                View view = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.listing_item_addable, parent, false);
                return new GameViewHolder(view);
            }
        }
    }

    @Override
    public void onBindViewHolder(@NonNull GameViewHolder holder, int position) {
        Game game = games.get(position);
        long userid = AppActivity.getCurrentUserID();
        holder.SetItem(game);
        holder.getTitle().setText(game.getTitle());
        setImage(holder.getImage(), db.gameDAO().getGameByID(game.getId()).getImage());
        double price = db.gameDAO().getGameMinPriceById(game.getId());
        if(price != 0) {
            holder.getListingsFrom().setText("Listings from:");
            holder.getPrice().setText(price+" €");
        }
        else{
            holder.getListingsFrom().setText(R.string.NoListings);
            holder.getPrice().setText("");}

        if(listingMode == ListingMode.EDITABLE)
        {
            holder.editButton.setVisibility(View.GONE);
            holder.deleteButton.setVisibility(showDelete ? ViewGroup.VISIBLE : View.GONE);
            holder.deleteButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Toast.makeText(view.getContext(), "Deleting!", Toast.LENGTH_SHORT).show();
                    holder.deleteBounce.start();
                    db.wishlistGameDAO().removeWGameByGameAndUserID(game.getId(),userid);
                    holder.fadeOutGame.start();
                    games.remove(game);
                    notifyItemRemoved(holder.getBindingAdapterPosition());
                    notifyItemRangeChanged(holder.getBindingAdapterPosition(), games.size());
                }
            });
        }
        else
        {
            holder.wishlistButton.setVisibility(showWishlist ? ViewGroup.VISIBLE : View.GONE);
            holder.cartButton.setVisibility(View.GONE);
            if(!db.wishlistGameDAO().getWGameByGameAndUserID(game.getId(), userid).isEmpty()) {
                holder.wishlistButton.setSelected(true);
            }else holder.wishlistButton.setSelected(false);
            holder.wishlistButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    //Toast.makeText(view.getContext(), "Wishlist!", Toast.LENGTH_SHORT).show();
                    var wGame = new WishlistGame();
                    wGame.setFk_gameid(game.getId());
                    wGame.setFk_userid(AppActivity.getCurrentUserID());
                    holder.wishlistBounce.start();
                    if(db.wishlistGameDAO().getWGameByGameAndUserID(game.getId(), userid).isEmpty()) {
                        db.wishlistGameDAO().insert(wGame);
                        holder.wishlistButton.setSelected(true);
                    }else {
                        db.wishlistGameDAO().removeWGameByGameAndUserID(game.getId(),userid);
                        holder.wishlistButton.setSelected(false);
                    }
                }
            });
        }
    }

    @Override
    public int getItemCount() {
        if(games != null)
            return games.size();
        return -1;
    }

    private void setImage(ImageView imageView, String imageName) {
        Bitmap bitmap;
        File imageFile = AppActivity.getCachedImageFile(
                imageView.getContext(), imageName);

        if(imageFile != null) {
            bitmap = BitmapFactory.decodeFile(imageFile.getAbsolutePath());
            imageView.setImageBitmap(bitmap);
        } else {
            imageView.setImageResource(R.drawable.ic_launcher_background);
        }
    }
}
