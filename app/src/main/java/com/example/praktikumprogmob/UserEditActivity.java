package com.example.praktikumprogmob;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.provider.MediaStore;
import android.util.Base64;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;

public class UserEditActivity extends AppCompatActivity {
    private Button btnSaveEdit;
    private TextInputEditText name, email,age;
    private TextView photo;
    private TextInputLayout layoutName, layoutEmail, layoutAge;
    String idLogin, tokenLogin;
    ProgressDialog dialog;
    CircleImageView circleImageView;
    private static final int GALLERY_ADD_PROFILE = 1;
    private Bitmap bitmap = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_edit);
        SharedPreferences userPref = getApplicationContext().getSharedPreferences("user", getApplicationContext().MODE_PRIVATE);
        idLogin = userPref.getString("id",null);
        tokenLogin = userPref.getString("token", null);
        init();
    }

    private void init() {
        circleImageView = findViewById(R.id.imgUserAccountEdit);

        layoutName = findViewById(R.id.txtLayoutNameEdit);
        layoutAge = findViewById(R.id.txtLayoutAgeEdit);
        layoutEmail = findViewById(R.id.txtLayoutEmailEdit);

        photo = findViewById(R.id.txtSelectEditPhoto);
        btnSaveEdit = findViewById(R.id.btnEditSave);

        name = findViewById(R.id.txtNameEdit);
        age = findViewById(R.id.txtAgeEdit);
        email = findViewById(R.id.txtEmailEdit);

        setUserInfo();
        getUserInfo();

        dialog = new ProgressDialog(UserEditActivity.this);
        dialog.setCancelable(false);
        photo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Intent.ACTION_PICK);
                intent.setType("image/*");
                startActivityForResult(intent, GALLERY_ADD_PROFILE);
            }
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

    public void openActivity() {
        Intent intent1 = new Intent(this, HomeActivity.class);
        startActivity(intent1);
        finish();
    }
    private void getUserInfo() {
        StringRequest request = new StringRequest(Request.Method.POST, Constant.USER_INFO, response -> {
            try {
                JSONObject object1 = new JSONObject(response);
                if (object1.getBoolean("success")){
                    JSONObject user = object1.getJSONObject("user");
                    SharedPreferences userPref = getApplicationContext().getSharedPreferences("user", getApplicationContext().MODE_PRIVATE);
                    SharedPreferences.Editor editor = userPref.edit();
                    editor.putString("id",user.getString("id"));
                    editor.putString("email",user.getString("email"));
                    editor.putString("name",user.getString("name"));
                    editor.putString("age",user.getString("age"));
                    editor.putString("photo",user.getString("photo"));
                    editor.apply();
                }
            } catch (JSONException e) {
                e.printStackTrace();
                Toast.makeText(getApplicationContext(), "Get Data Failed", Toast.LENGTH_SHORT).show();
            }
        },error -> {
            error.printStackTrace();
        }){
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> headers = new HashMap<>();
                headers.put("Authorization", "Bearer " + tokenLogin);
                return headers;
            }

            protected Map<String, String> getParams() throws AuthFailureError {
                HashMap<String,String> map = new HashMap<>();
                map.put("id", idLogin);
                return map;
            }
        };
        RequestQueue queue = Volley.newRequestQueue(getApplicationContext());
        queue.add(request);
    }

    private void setUserInfo() {
        SharedPreferences userPref = getApplicationContext().getSharedPreferences("user", getApplicationContext().MODE_PRIVATE);
        String nameLogin = userPref.getString("name",null);
        String ageLogin = userPref.getString("age",null);
        String emailLogin = userPref.getString("email",null);

        name.setText(nameLogin);
        age.setText(ageLogin);
        email.setText(emailLogin);
        Picasso.get().load(Constant.URL+"/profiles/"+userPref.getString("photo", null)).into(circleImageView);
    }

    private void edit() {
        dialog.setMessage("Updating Data");
        dialog.show();
        StringRequest request = new StringRequest(Request.Method.POST, Constant.EDIT_USER_INFO, response -> {
            try {
                JSONObject object1 = new JSONObject(response);
                if (object1.getBoolean("success")){
                    JSONObject user = object1.getJSONObject("user");
                    SharedPreferences userPref = getApplicationContext().getSharedPreferences("user", getApplicationContext().MODE_PRIVATE);
                    SharedPreferences.Editor editor = userPref.edit();
                    editor.putString("id",user.getString("id"));
                    editor.putString("email",user.getString("email"));
                    editor.putString("name",user.getString("name"));
                    editor.putString("age",user.getString("age"));
                    editor.putString("photo",user.getString("photo"));
                    editor.apply();
                    Toast.makeText(getApplicationContext(), "Edit Success", Toast.LENGTH_SHORT).show();
                }
            } catch (JSONException e) {
                e.printStackTrace();
                Toast.makeText(getApplicationContext(), "Edit Data Failed", Toast.LENGTH_SHORT).show();
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
                HashMap<String,String> map = new HashMap<>();
                map.put("id", idLogin);
                map.put("name", name.getText().toString());
                map.put("email", email.getText().toString());
                map.put("age", age.getText().toString());
                map.put("photo", bitmapToString(bitmap));
                return map;
            }
        };
        RequestQueue queue = Volley.newRequestQueue(getApplicationContext());
        queue.add(request);
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode==GALLERY_ADD_PROFILE && resultCode==RESULT_OK){
            Uri imgUri = data.getData();
            circleImageView.setImageURI(imgUri);
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

    private boolean validate() {
        if(name.getText().toString().isEmpty()){
            layoutName.setErrorEnabled(true);
            layoutName.setError("Name is Required");
            return false;
        }
        if(email.getText().toString().isEmpty()){
            layoutEmail.setErrorEnabled(true);
            layoutEmail.setError("Email is Required");
            return false;
        }
        if(age.getText().toString().isEmpty()){
            layoutAge.setErrorEnabled(true);
            layoutAge.setError("Age is Required");
            return false;
        }
        return true;
    }

    public void cancelEditProfile(View view) {
        super.onBackPressed();
    }
}