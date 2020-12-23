package com.example.praktikumprogmob;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkError;
import com.android.volley.NoConnectionError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.TimeoutError;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.praktikumprogmob.Adapters.PengobatansAdapter;
import com.example.praktikumprogmob.Adapters.TrashsAdapter;
import com.example.praktikumprogmob.Database.RoomDB;
import com.example.praktikumprogmob.Models.Pengobatan;
import com.example.praktikumprogmob.Models.Trash;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class PengobatanTrash extends AppCompatActivity {
    public  static RecyclerView recyclerView;
    private SwipeRefreshLayout refreshLayout;
    private TrashsAdapter trashsAdapter;
    public static ArrayList<Trash> trashList;
    private SharedPreferences sharedPreferences;
    LinearLayoutManager linearLayoutManager;
    public static ArrayList<Trash> trashListBackup = new ArrayList<>();

    RoomDB database;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        sharedPreferences = getApplicationContext().getApplicationContext().getSharedPreferences("user", Context.MODE_PRIVATE);
        setContentView(R.layout.activity_pengobatan_trash);
        recyclerView = findViewById(R.id.recyclerTrash);
        recyclerView.setHasFixedSize(true);
        linearLayoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerView.setHasFixedSize(true);
        refreshLayout = findViewById(R.id.swipe_trash);
        getTrash();
        refreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                getTrash();
            }
        });
    }

    private void getTrash() {
        trashList = new ArrayList<>();
        database = RoomDB.getInstance(getApplicationContext());
        refreshLayout.setRefreshing(true);
        StringRequest request = new StringRequest(Request.Method.GET, Constant.TRASH, response -> {
            try {
                JSONObject object = new JSONObject(response);
                if (object.getBoolean("success")){
                    database.trashDao().deleteAllTrash();
                    JSONArray array = new JSONArray(object.getString("data"));
                    for(int i=0;i<array.length();i++){
                        JSONObject trashObject = array.getJSONObject(i);

                        Trash trash = new Trash();
                        trash.setId(trashObject.getString("id"));
                        trash.setId_user(trashObject.getString("id_user"));
                        trash.setJenis_penyakit(trashObject.getString("jenis_penyakit"));
                        trash.setImg(trashObject.getString("img"));
                        trash.setNama_obat(trashObject.getString("nama_obat"));
                        trash.setFrekuensi_minum(trashObject.getString("frekuensi_minum"));
                        trash.setQty(trashObject.getString("qty"));
                        trash.setDeskripsi(trashObject.getString("deskripsi"));
                        trash.setDeleted_at(trashObject.getString("deleted_at"));
                        trashList.add(trash);
                        database.trashDao().insertTrash(trash);
                    }
                    trashsAdapter = new TrashsAdapter(getApplicationContext(),trashList);
                    recyclerView.setAdapter(trashsAdapter);
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
            refreshLayout.setRefreshing(false);

        },new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                refreshLayout.setRefreshing(false);
                if (error instanceof NetworkError || error instanceof AuthFailureError || error instanceof NoConnectionError || error instanceof TimeoutError) {
                    trashListBackup = (ArrayList<Trash>) database.trashDao().getAll();
                    trashsAdapter = new TrashsAdapter(getApplicationContext(),trashListBackup);
                    recyclerView.setAdapter(trashsAdapter);
                }
                Toast.makeText(getApplicationContext(),"No Connection",Toast.LENGTH_SHORT).show();
            }
        }){
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                String token = sharedPreferences.getString("token","");
                HashMap<String,String> map = new HashMap<>();
                map.put("Authorization","Bearer "+token);
                return map;
            }
        };
        RequestQueue queue = Volley.newRequestQueue(getApplicationContext());
        queue.add(request);
    }

    public void backTrash(View view) {
        super.onBackPressed();
    }
}