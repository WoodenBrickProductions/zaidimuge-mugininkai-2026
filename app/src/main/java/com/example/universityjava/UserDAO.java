package com.example.universityjava;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;

import java.util.List;

@Dao
public interface UserDAO {
    @Insert
    void insert(User user);

    @Query("DELETE FROM User")
    void deleteAll();

    @Query("SELECT * FROM User ORDER BY user_name")
    List<User> getAllUsers();

    @Query("SELECT * FROM User WHERE user_name LIKE :name")
    List<User> getUserByName(String name);

    @Query("SELECT * FROM User WHERE id = :id")
    User getUserByID(long id);
}
