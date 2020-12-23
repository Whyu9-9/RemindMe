package com.example.praktikumprogmob;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.Base64;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.praktikumprogmob.Fragments.HomeFragment;
import com.example.praktikumprogmob.Models.Pengobatan;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class PengobatanEdit extends AppCompatActivity {
    TextInputEditText jenis_penyakit, nama_obat, frekuensi_minum, qty, deskripsi;
    TextInputLayout layoutjenis_penyakit, layoutnama_obat, layoutfrekuensi_minum, layoutqty,layoutdeskripsi;
    TextView editPhoto;
    Button btnSaveEdit;
    int position;
    private Bitmap bitmap = null;
    ProgressDialog dialog;
    String  idObat, tokenLogin;
    private static final int GALLERY_ADD_IMAGE = 1;
    ImageView imageView;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pengobatan_edit);
        SharedPreferences userPref = getApplicationContext().getSharedPreferences("user", getApplicationContext().MODE_PRIVATE);
        tokenLogin = userPref.getString("token", null);
        init();
    }

    private void init() {
        imageView = findViewById(R.id.imgObatEdit);

        layoutjenis_penyakit = findViewById(R.id.txtLayoutEditJenisPenyakit);
        layoutnama_obat = findViewById(R.id.txtLayoutEditNamaObat);
        layoutfrekuensi_minum = findViewById(R.id.txtLayoutEditFrekuensi);
        layoutqty = findViewById(R.id.txtLayoutEditQty);
        layoutdeskripsi = findViewById(R.id.txtLayoutEditDeskripsi);
        
        jenis_penyakit = findViewById(R.id.txtEditJenisPenyakit);
        nama_obat = findViewById(R.id.txtEditNamaObat);
        frekuensi_minum = findViewById(R.id.txtEditFrekuensi);
        qty = findViewById(R.id.txtEditQty);
        deskripsi = findViewById(R.id.txtEditDeskripsi);

        btnSaveEdit = findViewById(R.id.btnEditMed);
        editPhoto = findViewById(R.id.editPhoto);
        
        dialog = new ProgressDialog(PengobatanEdit.this);
        dialog.setCancelable(false);

        getIncomingExtra();
        setData();
        editPhoto.setOnClickListener(view -> {
            Intent i = new Intent(Intent.ACTION_PICK);
            i.setType("image/*");
            startActivityForResult(i,GALLERY_ADD_IMAGE);
        });
        btnSaveEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(validate()){
                    edit();
                }
            }
        });
    }

    private void edit() {
        dialog.setMessage("Updating Data");
        dialog.show();
        StringRequest request = new StringRequest(Request.Method.POST, Constant.UPDATE_PENGOBATANS, response -> {
            try {
                JSONObject object1 = new JSONObject(response);
                if (object1.getBoolean("success")){
                    Pengobatan pengobatan = HomeFragment.arrayList.get(position);
                    pengobatan.setJenis_penyakit(jenis_penyakit.getText().toString());
                    pengobatan.setNama_obat(nama_obat.getText().toString());
                    pengobatan.setFrekuensi_minum(frekuensi_minum.getText().toString());
                    pengobatan.setDeskripsi(deskripsi.getText().toString());
                    pengobatan.setQty(qty.getText().toString());
                    HomeFragment.arrayList.set(position, pengobatan);
                    HomeFragment.recyclerView.getAdapter().notifyItemChanged(position);
                    HomeFragment.recyclerView.getAdapter().notifyDataSetChanged();

                    Intent intent = new Intent(this, HomeActivity.class);
                    intent.putExtra("position", position);
                    startActivity(intent);
                    finish();
                    Toast.makeText(getApplicationContext(), "Update Data Success", Toast.LENGTH_SHORT).show();
                }
            } catch (JSONException e) {
                e.printStackTrace();
                Toast.makeText(getApplicationContext(), "Update Data Failed", Toast.LENGTH_SHORT).show();
            }
            dialog.dismiss();
        },error -> {
            error.printStackTrace();
            dialog.dismiss();
        }){
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> headers = new HashMap<>();
                headers.put("Authorization", "Bearer " + tokenLogin);
                return headers;
            }

            protected Map<String, String> getParams() throws AuthFailureError {
                HashMap<String, String> map = new HashMap<>();
                map.put("id",idObat);
                map.put("jenis_penyakit",jenis_penyakit.getText().toString());
                map.put("nama_obat", nama_obat.getText().toString());
                map.put("frekuensi_minum",frekuensi_minum.getText().toString());
                map.put("qty", qty.getText().toString());
                map.put("deskripsi", deskripsi.getText().toString());
                map.put("img", bitmapToString(bitmap));
                return map;
            }
        };
        RequestQueue queue = Volley.newRequestQueue(getApplicationContext());
        queue.add(request);

    }
    private boolean validate() {
        if (jenis_penyakit.getText().toString().isEmpty()){
            layoutjenis_penyakit.setErrorEnabled(true);
            layoutjenis_penyakit.setError("Penyakit Tidak Boleh Kosong");
            return false;
        }

        if (nama_obat.getText().toString().isEmpty()){
            layoutnama_obat.setErrorEnabled(true);
            layoutnama_obat.setError("Obat Tidak Boleh Kosong");
            return false;
        }

        if (deskripsi.getText().toString().isEmpty()){
            layoutdeskripsi.setErrorEnabled(true);
            layoutdeskripsi.setError("Deskripsi Tidak Boleh Kosong");
            return false;
        }

        if (frekuensi_minum.getText().toString().isEmpty()){
            layoutfrekuensi_minum.setErrorEnabled(true);
            layoutfrekuensi_minum.setError("Frekuensi Tidak Boleh Kosong");
            return false;
        }

        if (!TextUtils.isDigitsOnly(frekuensi_minum.getText())){
            layoutfrekuensi_minum.setErrorEnabled(true);
            layoutfrekuensi_minum.setError("Frekuensi Tidak Boleh Kosong");
            return false;
        }

        if (qty.getText().toString().isEmpty()){
            layoutqty.setErrorEnabled(true);
            layoutqty.setError("Dosis Tidak Boleh Kosong");
            return false;
        }

        if (!TextUtils.isDigitsOnly(qty.getText())){
            layoutqty.setErrorEnabled(true);
            layoutqty.setError("Dosis Tidak Boleh Kosong");
            return false;
        }
        return true;
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode==GALLERY_ADD_IMAGE && resultCode==RESULT_OK){
            Uri imgUri = data.getData();
            imageView.setImageURI(imgUri);
            try {
                bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(),imgUri);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
    private String bitmapToString(Bitmap bitmap) {
        if(bitmap!=null){
            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, byteArrayOutputStream);
            byte [] array = byteArrayOutputStream.toByteArray();
            return Base64.encodeToString(array, Base64.DEFAULT);
        }
        return "";
    }

    private void setData() {
        Pengobatan pengobatan = HomeFragment.arrayList.get(position);
        jenis_penyakit.setText(pengobatan.getJenis_penyakit());
        nama_obat.setText(pengobatan.getNama_obat());
        frekuensi_minum.setText(pengobatan.getFrekuensi_minum());
        qty.setText(pengobatan.getQty());
        deskripsi.setText(pengobatan.getDeskripsi());
        idObat = pengobatan.getId();
        Picasso.get().load(Constant.URL+"image/"+pengobatan.getImg()).into(imageView);
    }

    private void getIncomingExtra() {
        if (getIntent().hasExtra("position")) {
            position = getIntent().getIntExtra("position", 0);
        }
    }

    public void cancelEditMed(View view) {
        super.onBackPressed();
    }
}