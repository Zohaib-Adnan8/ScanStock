package com.example.scanstock.database;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

@Dao
public interface ItemDao {

    @Insert
    void insert(Item item);

    @Update
    void update(Item item);

    @Delete
    void delete(Item item);

    @Query("SELECT * FROM items ORDER BY id DESC")
    List<Item> getAllItems();

    @Query("SELECT COUNT(*) FROM items")
    int getTotalCount();

    @Query("SELECT COUNT(*) FROM items WHERE quantity <= 2")
    int getLowStockCount();
}