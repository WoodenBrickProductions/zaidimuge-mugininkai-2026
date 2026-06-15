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

public class GameImageAdapter extends RecyclerView.Adapter<GameImageAdapter.GameImageViewHolder> {
    private List<Game> games;
    private static ItemRecyclerViewEvent listener;
    private final AppDatabase db = AppActivity.getDatabase();
    /// Listing list or Game list to work
    public GameImageAdapter(List<Game> list, ItemRecyclerViewEvent listener){
        GameImageAdapter.listener = listener;
        if (list != null && !list.isEmpty()){
            this.games = list;
        }
    }
    public static class GameImageViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private Game game;
        private final ImageView image;
        public GameImageViewHolder(View view) {
            super(view);
            image = (ImageView) view.findViewById(R.id.imageView);
            view.setOnClickListener(this);
        }

        public ImageView getImage(){
            return image;
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
    public GameImageViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_image, parent, false);
        return new GameImageViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull GameImageViewHolder holder, int position) {
        Game game = games.get(position);
        holder.SetItem(game);
        setImage(holder.getImage() ,db.gameDAO().getGameByID(game.getId()).getImage());
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
