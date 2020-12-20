package com.example.praktikumprogmob;

import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.praktikumprogmob.Fragments.AccountFragment;
import com.google.android.material.textfield.TextInputLayout;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class UserEditPassActivity extends AppCompatActivity {
    private Button savepass;
    private TextInputLayout currentLayout, newLayout, confirmLayout;
    private EditText currentPass, newPass, confirmPass;
    String idLogin, tokenLogin;
    ProgressDialog dialog;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_edit_pass);
        SharedPreferences userPref = getApplicationContext().getSharedPreferences("user", getApplicationContext().MODE_PRIVATE);
        idLogin = userPref.getString("id",null);
        tokenLogin = userPref.getString("token", null);
        init();
    }

    private void init() {
        currentLayout = (TextInputLayout)findViewById(R.id.txtLayoutCurrentPass);
        newLayout = (TextInputLayout)findViewById(R.id.txtLayoutNewPass);
        confirmLayout = (TextInputLayout)findViewById(R.id.txtLayoutConfirmPass);

        currentPass = (EditText)findViewById(R.id.txtCurrentPass);
        newPass = (EditText)findViewById(R.id.txtNewPass);
        confirmPass = (EditText)findViewById(R.id.txtConfirmPass);

        savepass = (Button) findViewById(R.id.btnChangePass);
        savepass.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(validate()){
                    editPass();
                }
            }
        });

        dialog = new ProgressDialog(UserEditPassActivity.this);
        dialog.setCancelable(false);
    }
    public void openActivity() {
        Intent intent = new Intent(this, HomeActivity.class);
        startActivity(intent);
    }
    private void editPass() {
        dialog.setMessage("Updating Password");
        dialog.show();
        StringRequest request = new StringRequest(Request.Method.POST, Constant.USER_EDIT_PASS, response -> {
            try {
                JSONObject object1 = new JSONObject(response);
                if (object1.getBoolean("success")) {
                    Toast.makeText(getApplicationContext(), "Password Has Been Changed", Toast.LENGTH_SHORT).show();
                    openActivity();
                }else if(!object1.getBoolean("success")){
                    Toast.makeText(getApplicationContext(), "Current Password is Invalid", Toast.LENGTH_SHORT).show();
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
                map.put("password", currentPass.getText().toString());
                map.put("newPass", newPass.getText().toString());
                return map;
            }
        };
        RequestQueue queue = Volley.newRequestQueue(getApplicationContext());
        queue.add(request);
    }

    private boolean validate() {
        if(currentPass.getText().toString().length()<8){
            currentLayout.setErrorEnabled(true);
            currentLayout.setError("Current Password must be at least 8 characters!");
            return false;
        }
        if(newPass.getText().toString().length()<8){
            newLayout.setErrorEnabled(true);
            newLayout.setError("New Password must be at least 8 characters!");
            return false;
        }
        if(!newPass.getText().toString().equals(confirmPass.getText().toString())){
            confirmLayout.setErrorEnabled(true);
            confirmLayout.setError("Password did not match!");
            return false;
        }
        return true;
    }

    public void cancelEdit(View view) {
        super.onBackPressed();
    }
}