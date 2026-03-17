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
        childColumns = "fk_listingid")
}, indices = {@Index(value = "fk_listingid", unique = true)})
public class PhysicalListingAttributes {
    @PrimaryKey
    @ColumnInfo(name="fk_listingid")
    private long fk_listingid;
    @ColumnInfo(name="fk_condition")
    private Condition fk_condition;
    @ColumnInfo(name="condition_description")
    private String condition_description;
    @ColumnInfo(name="image")
    private String image;

    public void setFk_listingid(long fk_listingid) { this.fk_listingid = fk_listingid; }
    public long getFk_listingid() { return this.fk_listingid; }
    public void setFk_condition(Condition fk_condition) { this.fk_condition = fk_condition; }
    public Condition getFk_condition() { return this.fk_condition; }
    public void setCondition_description(String condition_description) { this.condition_description = condition_description; }
    public String getCondition_description() { return this.condition_description; }
    public void setImage(String image) { this.image = image; }
    public String getImage() { return this.image; }
}
