package com.example.universityjava.database;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;

import java.util.List;

@Dao
public interface OrderDAO {
    @Insert
    void insert(Order order);

    @Query("DELETE FROM 'Order'")
    void deleteAll();

    @Query("DELETE FROM 'Order' WHERE id = :id")
    void removeOrderByID(long id);
    @Query("SELECT * FROM 'Order' WHERE id = :id")
    Order getOrderByID(long id);
    @Query("DELETE FROM 'Order' WHERE fk_userid = :userid AND" +
            " fk_listingid = :listid")
    void removeOrderByListingAndUserID(long listid, long userid);
    @Query("SELECT * FROM 'Order'")
    List<Order> getAllOrders();

    @Query("SELECT * FROM 'Order' WHERE fk_userid LIKE :userid")
    List<Order> getOrdersByUserId(long userid);

    @Query("SELECT * FROM 'Order' WHERE fk_listingid = :id")
    List<Order> getOrdersByListingID(long id);

    @Query("SELECT * FROM 'Order' WHERE fk_listingid = :listid AND fk_userid = :userid")
    List<Order> getOrderByListingAndUserID(long listid, long userid);
}
