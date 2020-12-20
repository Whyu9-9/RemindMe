package com.example.praktikumprogmob.Fragments;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.praktikumprogmob.AuthActivity;
import com.example.praktikumprogmob.Constant;
import com.example.praktikumprogmob.HomeActivity;
import com.example.praktikumprogmob.R;
import com.example.praktikumprogmob.UserEditActivity;
import com.example.praktikumprogmob.UserEditPassActivity;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.textfield.TextInputEditText;
import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;

public class AccountFragment extends Fragment {
    private View view;
    private MaterialToolbar toolbar;
    private SharedPreferences sharedPreferences;
    private Button buttonedit;
    private TextView txteditpass;
    private TextInputEditText name, age, email;
    private String idLogin, tokenLogin;
    CircleImageView circleImageView;
    public AccountFragment(){}

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.layout_account,container,false);
        sharedPreferences = getContext().getApplicationContext().getSharedPreferences("user", Context.MODE_PRIVATE);
        SharedPreferences userPref = getContext().getSharedPreferences("user", getContext().MODE_PRIVATE);
        idLogin = userPref.getString("id",null);
        tokenLogin = userPref.getString("token", null);
        toolbar = view.findViewById(R.id.toolbarAccount);
        ((HomeActivity)getContext()).setSupportActionBar(toolbar);
        setHasOptionsMenu(true);
        init();
        return view;
    }

    private void init() {
        buttonedit = view.findViewById(R.id.btnEditProfile);
        txteditpass = view.findViewById(R.id.txtChangePassword);
        name = view.findViewById(R.id.txtNameAccount);
        age = view.findViewById(R.id.txtAgeAccount);
        email = view.findViewById(R.id.txtEmailAccount);
        circleImageView = view.findViewById(R.id.imgUserAccount);
        setAccountInfo();
        buttonedit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getContext(), UserEditActivity.class);
                startActivity(intent);
            }
        });
        txteditpass.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getContext(), UserEditPassActivity.class);
                startActivity(intent);
            }
        });
    }

    private void setAccountInfo() {
        SharedPreferences userPref = getContext().getSharedPreferences("user", getContext().MODE_PRIVATE);
        String nameLogin = userPref.getString("name",null);
        String ageLogin = userPref.getString("age",null);
        String emailLogin = userPref.getString("email",null);

        name.setText(nameLogin);
        age.setText(ageLogin);
        email.setText(emailLogin);
        Picasso.get().load(Constant.URL+"/profiles/"+userPref.getString("photo", null)).into(circleImageView);
    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        inflater.inflate(R.menu.menu_account,menu);
        super.onCreateOptionsMenu(menu,inflater);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()){
            case R.id.item_logout:{
                AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                builder.setMessage("Apakah anda ingin Log Out?");
                builder.setPositiveButton("Log Out", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        logout();
                    }
                });
                builder.setNegativeButton("Batal", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                    }
                });
                builder.show();
            }
        }
        return super.onOptionsItemSelected(item);
    }

    private void logout(){
        StringRequest request = new StringRequest(Request.Method.GET, Constant.LOGOUT,response -> {
            try {
                JSONObject object = new JSONObject(response);
                if (object.getBoolean("success")){
                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    editor.clear();
                    editor.apply();
                    startActivity(new Intent(((HomeActivity)getContext()), AuthActivity.class));
                    ((HomeActivity)getContext()).finish();
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        },error -> {
            error.printStackTrace();
        }){
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                String token = sharedPreferences.getString("token","");
                HashMap<String,String> map = new HashMap<>();
                map.put("Authorization","Bearer "+token);
                return map;
            }
        };
        RequestQueue queue = Volley.newRequestQueue(getContext());
        queue.add(request);
    }
    @Override
    public void onResume() {
        super.onResume();
        setAccountInfo();
    }
}
