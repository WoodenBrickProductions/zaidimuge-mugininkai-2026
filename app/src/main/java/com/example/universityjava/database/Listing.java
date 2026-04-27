package com.example.universityjava.database;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.Index;
import androidx.room.PrimaryKey;

import com.example.universityjava.User;

@Entity(foreignKeys = {@ForeignKey(
        entity = User.class,
        parentColumns = "id",
        childColumns = "fk_seller"),
        @ForeignKey(
                entity = Platform.class,
                parentColumns = "id",
                childColumns = "fk_platform"),
        @ForeignKey(
                entity = Game.class,
                parentColumns = "id",
                childColumns = "fk_gameid")
}, indices = {@Index(value = "fk_seller"),
              @Index(value = "fk_platform"),
              @Index(value = "fk_gameid")})
public class Listing {
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name="id")
    private long id;
    @NonNull
    @ColumnInfo(name = "fk_seller")
    private long fk_seller;
    @NonNull
    @ColumnInfo(name = "price")
    private double price;
    @NonNull
    @ColumnInfo(name = "isdigital")
    private boolean isdigital;
    @NonNull
    @ColumnInfo(name = "fk_platform")
    private int fk_platform;
    @NonNull
    @ColumnInfo(name = "fk_gameid")
    private long fk_gameid;
    @ColumnInfo(name = "issold")
    private boolean issold;

    public void setId(long id) { this.id = id; }
    public long getId() { return this.id; }
    public void setFk_seller(long fk_seller) { this.fk_seller = fk_seller; }
    public long getFk_seller() { return this.fk_seller; }
    public void setPrice(double price) {this.price = price;}
    public double getPrice() {return this.price;}
    public void setIsdigital(boolean isdigital) {this.isdigital = isdigital;}
    public boolean getIsdigital() {return this.isdigital;}
    public void setFk_platform(int fk_platform) {this.fk_platform = fk_platform;}
    public int getFk_platform() {return this.fk_platform;}
    public void setFk_gameid(long fk_gameid) { this.fk_gameid = fk_gameid; }
    public long getFk_gameid() { return this.fk_gameid; }
    public void setIssold(boolean issold) {this.issold = issold;}
    public boolean getIssold() {return this.issold;}

    public void generateTestData(long id, long fk_gameid, long fk_seller, int fk_platform) {
        this.id = id;
        this.fk_gameid = fk_gameid;
        this.fk_seller = fk_seller;
        this.fk_platform = fk_platform;
        this.issold = false;
        this.isdigital = true;
        this.price = id / 7.2d;
    }
}


