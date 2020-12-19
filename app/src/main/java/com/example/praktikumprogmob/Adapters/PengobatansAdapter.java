package com.example.praktikumprogmob.Adapters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.praktikumprogmob.Constant;
import com.example.praktikumprogmob.HomeActivity;
import com.example.praktikumprogmob.Models.Pengobatan;
import com.example.praktikumprogmob.PengobatanDetail;
import com.example.praktikumprogmob.R;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

public class PengobatansAdapter extends RecyclerView.Adapter<PengobatansAdapter.PengobatansHolder> {
    Context context;
    ArrayList<Pengobatan> list;

    public PengobatansAdapter(Context context, ArrayList<Pengobatan> list) {
        this.list = list;
        this.context = context;
    }

    @NonNull
    @Override
    public PengobatansHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View view = layoutInflater.inflate(R.layout.layout_pengobatan, parent, false);
        PengobatansHolder viewHolder = new PengobatansHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull PengobatansHolder holder, int position) {
        Pengobatan pengobatan = list.get(position);
        Picasso.get().load(Constant.URL+"image/"+pengobatan.getImg()).into(holder.imgObat);
        holder.txtobat.setText(pengobatan.getNama_obat());
        holder.txtpenyakit.setText(pengobatan.getJenis_penyakit());
        holder.txtfrekuensi.setText("Dosis Minum: "+pengobatan.getFrekuensi_minum()+"x sehari");
        holder.detail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent =  new Intent(context, PengobatanDetail.class);
                intent.putExtra("position", position);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                context.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class PengobatansHolder extends RecyclerView.ViewHolder{
        ImageButton detail;
        TextView txtobat,txtpenyakit, txtfrekuensi;
        CircleImageView imgObat;

        public PengobatansHolder(@NonNull View itemView) {
            super(itemView);
            detail = itemView.findViewById(R.id.detail);
            txtobat = itemView.findViewById(R.id.namaObat);
            txtpenyakit = itemView.findViewById(R.id.namaPenyakit);
            txtfrekuensi = itemView.findViewById(R.id.frekuensi);
            imgObat = itemView.findViewById(R.id.imgObat);
        }
    }
}
