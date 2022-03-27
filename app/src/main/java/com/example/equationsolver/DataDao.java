package com.example.equationsolver;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;

import java.util.List;

@Dao
public abstract class DataDao {
    @Query("SELECT * FROM data")
    List<Data> getAll() {
        return null;
    }

    @Query("SELECT * FROM data WHERE Did IN (:userIds)")
    List<Data> loadAllByIds(int[] userIds) {
        return null;
    }

    @Insert
    void insertAll(Data... data) {

    }

    @Delete
    void delete(Data data) {

    }
}
