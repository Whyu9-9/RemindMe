package com.example.praktikumprogmob.Models;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "trashs")
public class Trash {
    @PrimaryKey(autoGenerate = false)
    @NonNull
    private String id;

    @ColumnInfo(name = "id_user")
    private String id_user;

    @ColumnInfo(name = "jenis_penyakit")
    private String jenis_penyakit;

    @ColumnInfo(name = "img")
    private String img;

    @ColumnInfo(name = "nama_obat")
    private String nama_obat;

    @ColumnInfo(name = "frekuensi_minum")
    private String frekuensi_minum;

    @ColumnInfo(name = "qty")
    private String qty;

    @ColumnInfo(name = "deskripsi")
    private String deskripsi;

    @ColumnInfo(name = "deleted_at")
    private String deleted_at;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getId_user() {
        return id_user;
    }

    public void setId_user(String id_user) {
        this.id_user = id_user;
    }

    public String getJenis_penyakit() {
        return jenis_penyakit;
    }

    public void setJenis_penyakit(String jenis_penyakit) {
        this.jenis_penyakit = jenis_penyakit;
    }

    public String getImg() {
        return img;
    }

    public void setImg(String img) {
        this.img = img;
    }

    public String getNama_obat() {
        return nama_obat;
    }

    public void setNama_obat(String nama_obat) {
        this.nama_obat = nama_obat;
    }

    public String getFrekuensi_minum() {
        return frekuensi_minum;
    }

    public void setFrekuensi_minum(String frekuensi_minum) {
        this.frekuensi_minum = frekuensi_minum;
    }

    public String getQty() {
        return qty;
    }

    public void setQty(String qty) {
        this.qty = qty;
    }

    public String getDeskripsi() {
        return deskripsi;
    }

    public void setDeskripsi(String deskripsi) {
        this.deskripsi = deskripsi;
    }

    public String getDeleted_at() {
        return deleted_at;
    }

    public void setDeleted_at(String deleted_at) {
        this.deleted_at = deleted_at;
    }
}
