package com.example.universityjava.database;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.Index;
import androidx.room.PrimaryKey;

@Entity
public class Game {
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id")
    private long id;
    @ColumnInfo(name = "steamdbid")
    private long steamdbid = 0;
    @ColumnInfo(name = "title")
    private String title = "empty_title";
    @ColumnInfo(name = "description")
    private String description = "empty_description";
    @ColumnInfo(name = "image")
    private String image = "image.png";
    @ColumnInfo(name = "is_for_adults")
    private boolean is_for_adults = false;

    public void setId(long id) { this.id = id; }
    public long getId() { return this.id; }
    public void setSteamdbid(long steamdbid) { this.steamdbid = steamdbid; }
    public long getSteamdbid() { return this.steamdbid; }
    public void setTitle(String title) { this.title = title; }
    public String getTitle() { return this.title; }
    public void setDescription(String description) { this.description = description; }
    public String getDescription() { return this.description; }
    public void setImage(String image) { this.image = image; }
    public String getImage() { return this.image; }
    public void setIs_for_adults(boolean is_for_adults) { this.is_for_adults = is_for_adults; }
    public boolean getIs_for_adults() { return this.is_for_adults; }
}
