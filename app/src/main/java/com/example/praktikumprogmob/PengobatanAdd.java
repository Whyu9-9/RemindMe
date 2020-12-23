package com.example.praktikumprogmob;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Context;
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
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.material.textfield.TextInputLayout;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class PengobatanAdd extends AppCompatActivity {
    private Button btnAdd;
    ImageView imgObat;
    TextView txtaddimg;
    EditText penyakit, obat, frekuensi, dosis, desc;
    private static final int GALLERY_ADD_IMAGE = 1;
    TextInputLayout layoutPenyakit, layoutObat, layoutFrekuensi, layoutDosis,layoutDesc;
    private Bitmap bitmap = null;
    private ProgressDialog dialog;
    private SharedPreferences preferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pengobatan_add);
        init();
    }

    private void init() {
        btnAdd = findViewById(R.id.btnAddMed);
        txtaddimg = findViewById(R.id.addPhoto);

        dialog = new ProgressDialog(this);
        dialog.setCancelable(false);

        preferences = getApplicationContext().getSharedPreferences("user", Context.MODE_PRIVATE);
        penyakit = findViewById(R.id.txtJenisPenyakit);
        obat = findViewById(R.id.txtNamaObat);
        frekuensi = findViewById(R.id.txtFrekuensi);
        dosis = findViewById(R.id.txtQty);
        desc = findViewById(R.id.txtDeskripsi);

        layoutPenyakit = findViewById(R.id.txtLayoutJenisPenyakit);
        layoutObat = findViewById(R.id.txtLayoutNamaObat);
        layoutFrekuensi = findViewById(R.id.txtLayoutFrekuensi);
        layoutDosis = findViewById(R.id.txtLayoutQty);
        layoutDesc = findViewById(R.id.txtLayoutDeskripsi);
        imgObat = findViewById(R.id.imgObat);
        txtaddimg.setOnClickListener(view -> {
            Intent i = new Intent(Intent.ACTION_PICK);
            i.setType("image/*");
            startActivityForResult(i,GALLERY_ADD_IMAGE);
        });
        btnAdd.setOnClickListener(view -> {
            if(validate()){
                savePengobatan();
            }
        });
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode==GALLERY_ADD_IMAGE && resultCode==RESULT_OK){
            Uri imgUri = data.getData();
            imgObat.setImageURI(imgUri);
            try {
                bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(),imgUri);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
    private void savePengobatan() {
        dialog.setMessage("Menyimpan Data Pengobatan");
        dialog.show();
        String jenis_penyakit = penyakit.getText().toString().trim();
        String nama_obat = obat.getText().toString().trim();
        String frekuensi_minum = frekuensi.getText().toString().trim();
        String qty = dosis.getText().toString().trim();
        String deskripsi = desc.getText().toString().trim();

        StringRequest request = new StringRequest(Request.Method.POST, Constant.ADD_PENGOBATANS,response -> {
            try {
                JSONObject object = new JSONObject(response);
                if(object.getBoolean("success")){
                    Intent intent = new Intent(this, HomeActivity.class);
                    startActivity(intent);
                    Toast.makeText(getApplicationContext(), "Add Data Success", Toast.LENGTH_SHORT).show();
                }
            } catch (JSONException e) {
                e.printStackTrace();
                Toast.makeText(getApplicationContext(), "Add Data Success", Toast.LENGTH_SHORT).show();
            }
            dialog.dismiss();
        },error -> {
            error.printStackTrace();
            dialog.dismiss();
        }){
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                String token = preferences.getString("token", "");
                HashMap<String, String> map = new HashMap<>();
                map.put("Authorization","Bearer "+token);
                return map;
            }

            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                HashMap<String, String> map = new HashMap<>();
                map.put("jenis_penyakit",jenis_penyakit);
                map.put("nama_obat", nama_obat);
                map.put("frekuensi_minum",frekuensi_minum);
                map.put("qty", qty);
                map.put("deskripsi", deskripsi);
                map.put("img", bitmapToString(bitmap));
                return map;
            }
        };
        RequestQueue queue = Volley.newRequestQueue(PengobatanAdd.this);
        queue.add(request);
    }

    private boolean validate() {
        if (penyakit.getText().toString().isEmpty()){
            layoutPenyakit.setErrorEnabled(true);
            layoutPenyakit.setError("Penyakit Tidak Boleh Kosong");
            return false;
        }

        if (obat.getText().toString().isEmpty()){
            layoutObat.setErrorEnabled(true);
            layoutObat.setError("Obat Tidak Boleh Kosong");
            return false;
        }

        if (desc.getText().toString().isEmpty()){
            layoutDesc.setErrorEnabled(true);
            layoutDesc.setError("Deskripsi Tidak Boleh Kosong");
            return false;
        }

        if (frekuensi.getText().toString().isEmpty()){
            layoutFrekuensi.setErrorEnabled(true);
            layoutFrekuensi.setError("Frekuensi Tidak Boleh Kosong");
            return false;
        }

        if (!TextUtils.isDigitsOnly(frekuensi.getText())){
            layoutFrekuensi.setErrorEnabled(true);
            layoutFrekuensi.setError("Input Number!");
            return false;
        }

        if (dosis.getText().toString().isEmpty()){
            layoutDosis.setErrorEnabled(true);
            layoutDosis.setError("Dosis Tidak Boleh Kosong");
            return false;
        }

        if (!TextUtils.isDigitsOnly(dosis.getText())){
            layoutDosis.setErrorEnabled(true);
            layoutDosis.setError("Input Number!");
            return false;
        }


        return true;
    }

    public void cancelAdd(View view) {
        super.onBackPressed();
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

}