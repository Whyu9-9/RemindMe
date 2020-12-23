package com.example.praktikumprogmob.Database;

import android.content.Context;


import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

import com.example.praktikumprogmob.Dao.PengobatanDao;
import com.example.praktikumprogmob.Dao.TrashDao;
import com.example.praktikumprogmob.Models.Pengobatan;
import com.example.praktikumprogmob.Models.Trash;

@Database(entities = {Pengobatan.class,Trash.class},version = 1,exportSchema = false)
public abstract class RoomDB extends RoomDatabase {
    private static RoomDB database;
    private static String DATABASE_NAME = "database";

    public synchronized static RoomDB getInstance(Context context) {
        if (database == null) {
            database = Room.databaseBuilder(context.getApplicationContext(), RoomDB.class, DATABASE_NAME).allowMainThreadQueries().fallbackToDestructiveMigration().build();
        }

        return database;
    }
    public abstract PengobatanDao pengobatanDao();
    public abstract TrashDao trashDao();
}