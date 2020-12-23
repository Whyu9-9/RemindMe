package com.example.praktikumprogmob.Dao;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;

import com.example.praktikumprogmob.Models.Trash;

import java.util.List;

@Dao
public interface TrashDao {
    @Insert
    void insertTrash(Trash trash);

    @Query("DELETE FROM trashs")
    void deleteAllTrash();

    @Query("SELECT * FROM trashs")
    List<Trash> getAll();
}
