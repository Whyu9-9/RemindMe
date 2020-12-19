package com.example.praktikumprogmob.Fragments;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

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
import com.example.praktikumprogmob.Constant;
import com.example.praktikumprogmob.Database.RoomDB;
import com.example.praktikumprogmob.HomeActivity;
import com.example.praktikumprogmob.Models.Pengobatan;
import com.example.praktikumprogmob.R;
import com.google.android.material.appbar.MaterialToolbar;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class HomeFragment extends Fragment {
    private View view;
    public static RecyclerView recyclerView;
    public static ArrayList<Pengobatan> arrayList;
    public static ArrayList<Pengobatan> arrayListBackup = new ArrayList<>();
    private SwipeRefreshLayout refreshLayout;
    private PengobatansAdapter pengobatansAdapter;
    private MaterialToolbar toolbar;
    private SharedPreferences sharedPreferences;
    RoomDB database;
    public HomeFragment(){}

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.layout_home,container,false);
        init();
        return view;
    }

    private void init() {
        sharedPreferences = getContext().getApplicationContext().getSharedPreferences("user", Context.MODE_PRIVATE);
        recyclerView = view.findViewById(R.id.recyclerHome);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        refreshLayout = view.findViewById(R.id.swipe_home);
        toolbar = view.findViewById(R.id.toolbarHome);
        ((HomeActivity)getContext()).setSupportActionBar(toolbar);
        getPengobatans();
        refreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                getPengobatans();
            }
        });
    }

    private void getPengobatans() {
        arrayList = new ArrayList<>();
        database = RoomDB.getInstance(getContext());
        refreshLayout.setRefreshing(true);
        StringRequest request = new StringRequest(Request.Method.GET, Constant.PENGOBATANS,response -> {
            try {
                JSONObject object = new JSONObject(response);
                if (object.getBoolean("success")){
                    database.pengobatanDao().deleteAll();
                    JSONArray array = new JSONArray(object.getString("data"));
                    for(int i=0;i<array.length();i++){
                        JSONObject pengobatanObject = array.getJSONObject(i);

                        Pengobatan pengobatan = new Pengobatan();
                        pengobatan.setId(pengobatanObject.getString("id"));
                        pengobatan.setId_user(pengobatanObject.getString("id_user"));
                        pengobatan.setJenis_penyakit(pengobatanObject.getString("jenis_penyakit"));
                        pengobatan.setImg(pengobatanObject.getString("img"));
                        pengobatan.setNama_obat(pengobatanObject.getString("nama_obat"));
                        pengobatan.setFrekuensi_minum(pengobatanObject.getString("frekuensi_minum"));
                        pengobatan.setQty(pengobatanObject.getString("qty"));
                        arrayList.add(pengobatan);
                        database.pengobatanDao().insertPengobatan(pengobatan);
                    }
                    pengobatansAdapter = new PengobatansAdapter(getContext(),arrayList);
                    recyclerView.setAdapter(pengobatansAdapter);
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
                    arrayListBackup = (ArrayList<Pengobatan>) database.pengobatanDao().getAll();
                    pengobatansAdapter = new PengobatansAdapter(getContext(),arrayListBackup);
                    recyclerView.setAdapter(pengobatansAdapter);
                }
                Toast.makeText(getContext(),"No Connection",Toast.LENGTH_SHORT).show();
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
        RequestQueue queue = Volley.newRequestQueue(getContext());
        queue.add(request);
    }
}
