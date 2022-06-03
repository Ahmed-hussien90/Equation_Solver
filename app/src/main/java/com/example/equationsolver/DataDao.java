package com.example.equationsolver;

import android.database.Observable;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;

import java.util.List;

import io.reactivex.Completable;
import io.reactivex.Single;

@Dao
interface DataDao {
    @Query("SELECT * FROM data")
    Single<List<Data>> getAll();

    @Query("SELECT * FROM data WHERE Did IN (:userIds)")
    Single<List<Data>> loadAllByIds(int[] userIds);

    @Insert
    Completable insertAll(Data... data);

    @Query("DELETE FROM data WHERE Did = :id")
    void delete(int id);
}
