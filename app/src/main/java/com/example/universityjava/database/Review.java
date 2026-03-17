package com.example.universityjava.database;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.Index;
import androidx.room.PrimaryKey;

import com.example.universityjava.User;

@Entity(foreignKeys = {@ForeignKey(
                entity = Listing.class,
                parentColumns = "id",
                childColumns = "fk_listingid"),
        @ForeignKey(
                entity = User.class,
                parentColumns = "id",
                childColumns = "fk_buyerid")
}, indices = {@Index(value = "fk_listingid", unique = true),
        @Index(value = "fk_buyerid", unique = true)})
public class Review {
    @PrimaryKey
    @ColumnInfo(name="fk_listingid")
    private long fk_listingid;
    @ColumnInfo(name="fk_buyerid")
    private long fk_buyerid;
    @ColumnInfo(name="rating")
    private int rating;
    @ColumnInfo(name="comment")
    private String comment;

    public void setFk_listingid(long fk_listingid) { this.fk_listingid = fk_listingid; }
    public long getFk_listingid() { return this.fk_listingid; }
    public void setFk_buyerid(long fk_buyerid) { this.fk_buyerid = fk_buyerid; }
    public long getFk_buyerid() { return this.fk_buyerid; }
    public void setRating(int rating) { this.rating = rating; }
    public int getRating() { return this.rating; }
    public void setComment(String comment) { this.comment = comment; }
    public String getComment() { return this.comment; }


}
