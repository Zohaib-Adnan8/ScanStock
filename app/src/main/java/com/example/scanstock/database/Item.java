package com.example.scanstock.database;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "items")
public class Item {

    @PrimaryKey(autoGenerate = true)
    public int id;

    public String barcode;
    public String name;
    public int quantity;
    public String category;
    public String notes;
    public String dateAdded;
}