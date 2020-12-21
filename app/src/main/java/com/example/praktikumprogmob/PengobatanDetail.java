package com.example.praktikumprogmob;

import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.praktikumprogmob.Fragments.HomeFragment;
import com.example.praktikumprogmob.Models.Pengobatan;
import com.google.android.material.textfield.TextInputEditText;
import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class PengobatanDetail extends AppCompatActivity {
    TextInputEditText jenis_penyakit, nama_obat, frekuensi_minum, qty;
    Button btnEdit, btnDelete;
    int position;
    ProgressDialog dialog1;
    String  idObat, tokenLogin;
    ImageView imageView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pengobatan_detail);
        SharedPreferences userPref = getApplicationContext().getSharedPreferences("user", getApplicationContext().MODE_PRIVATE);
        tokenLogin = userPref.getString("token", null);
        init();
    }

    private void init() {
        imageView = findViewById(R.id.imgObatDetail);
        jenis_penyakit = findViewById(R.id.txtJenisPenyakitDetail);
        nama_obat = findViewById(R.id.txtNamaObatDetail);
        frekuensi_minum = findViewById(R.id.txtFrekuensiDetail);
        qty = findViewById(R.id.txtQtyDetail);

        btnEdit = findViewById(R.id.btnEditMed);
        btnDelete = findViewById(R.id.btnDeleteMed);

        dialog1 = new ProgressDialog(PengobatanDetail.this);
        dialog1.setCancelable(false);

        getIncomingExtra();
        setDetail();

        btnDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder builder = new AlertDialog.Builder(PengobatanDetail.this);
                builder.setTitle("Confirm");
                builder.setMessage("Delete Data?");
                builder.setPositiveButton("Delete", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog1.setMessage("Deleting Data");
                        dialog1.show();
                        StringRequest request = new StringRequest(Request.Method.POST, Constant.DELETE_PENGOBATANS, response -> {
                            try {
                                JSONObject object1 = new JSONObject(response);
                                if (object1.getBoolean("success")){
                                    HomeFragment.recyclerView.getAdapter().notifyItemRemoved(position);
                                    HomeFragment.recyclerView.getAdapter().notifyDataSetChanged();

                                    Intent intent = new Intent(PengobatanDetail.this, HomeActivity.class);
                                    startActivity(intent);
                                    finish();
                                    Toast.makeText(getApplicationContext(), "Delete Data Success", Toast.LENGTH_SHORT).show();
                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                                Toast.makeText(getApplicationContext(), "Delete Data Failed", Toast.LENGTH_SHORT).show();
                            }
                            dialog1.dismiss();
                        },error -> {
                            error.printStackTrace();
                            dialog1.dismiss();
                        }){
                            @Override
                            public Map<String, String> getHeaders() throws AuthFailureError {
                                Map<String, String> headers = new HashMap<>();
                                headers.put("Authorization", "Bearer " + tokenLogin);
                                return headers;
                            }

                            protected Map<String, String> getParams() throws AuthFailureError {
                                HashMap<String,String> map = new HashMap<>();
                                map.put("id",idObat);
                                return map;
                            }
                        };
                        RequestQueue queue = Volley.newRequestQueue(getApplicationContext());
                        queue.add(request);
                    }
                });
                builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                });
                builder.show();
            }
        });
        btnEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(PengobatanDetail.this, PengobatanEdit.class);
                intent.putExtra("id", idObat);
                intent.putExtra("position", getIntent().getIntExtra("position", 0));
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
                finish();
            }
        });
    }

    private void setDetail() {
        Pengobatan pengobatan = HomeFragment.arrayList.get(position);
        jenis_penyakit.setText(pengobatan.getJenis_penyakit());
        nama_obat.setText(pengobatan.getNama_obat());
        frekuensi_minum.setText(pengobatan.getFrekuensi_minum());
        qty.setText(pengobatan.getQty());
        idObat = pengobatan.getId();
        Picasso.get().load(Constant.URL+"image/"+pengobatan.getImg()).into(imageView);
    }

    private void getIncomingExtra() {
        if(getIntent().hasExtra("position")){
            position = getIntent().getIntExtra("position", 0);
        }
    }

    public void backDetail(View view) {
        super.onBackPressed();
    }
}