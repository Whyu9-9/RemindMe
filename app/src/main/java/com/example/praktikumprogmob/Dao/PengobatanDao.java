package com.example.praktikumprogmob.Dao;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Transaction;

import com.example.praktikumprogmob.Models.Pengobatan;

import java.util.List;

@Dao
public interface PengobatanDao {
    @Insert
    void insertPengobatan(Pengobatan pengobatan);

    @Query("DELETE FROM obats")
    void deleteAll();

    @Query("SELECT * FROM obats")
    List<Pengobatan> getAll();
}
