package com.example.praktikumprogmob.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.praktikumprogmob.Models.Trash;
import com.example.praktikumprogmob.R;

import java.util.ArrayList;

public class TrashsAdapter extends RecyclerView.Adapter<TrashsAdapter.TrashsHolder> {

    private ArrayList<Trash> listTrash;
    private Context context;

    public TrashsAdapter(Context context,ArrayList<Trash> listTrash) {
        this.context = context;
        this.listTrash = listTrash;
    }

    @NonNull
    @Override
    public TrashsHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View view = layoutInflater.inflate(R.layout.layout_trash, parent, false);
        TrashsHolder viewHolder = new TrashsHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull TrashsHolder holder, int position) {
        Trash trash = listTrash.get(position);
        holder.txtPenyakitTrash.setText(trash.getJenis_penyakit());
        holder.txtObatTrash.setText(trash.getNama_obat());
        holder.txtDeleted.setText("Deleted at: "+trash.getDeleted_at());
    }

    @Override
    public int getItemCount() {
        return listTrash.size();
    }

    public class TrashsHolder extends RecyclerView.ViewHolder{
        TextView txtPenyakitTrash, txtObatTrash, txtDeleted;

        public TrashsHolder(@NonNull View itemView) {
            super(itemView);
            txtPenyakitTrash = itemView.findViewById(R.id.namaPenyakitTrash);
            txtObatTrash = itemView.findViewById(R.id.namaObatTrash);
            txtDeleted = itemView.findViewById(R.id.deletedAtTrash);
        }
    }
}
