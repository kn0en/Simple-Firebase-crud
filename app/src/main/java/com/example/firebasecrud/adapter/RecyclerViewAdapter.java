package com.example.firebasecrud.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.RecyclerView;

import com.example.firebasecrud.MyListData;
import com.example.firebasecrud.R;
import com.example.firebasecrud.model.data_mahasiswa;
import com.example.firebasecrud.update_Data;

import java.util.ArrayList;

//Class Adapter ini Digunakan Untuk Mengatur Bagaimana Data akan Ditampilkan
public class RecyclerViewAdapter extends RecyclerView.Adapter<RecyclerViewAdapter.ViewHolder> {

    //Deklarasi Variable
    private ArrayList<data_mahasiswa> listMahasiswa;
    private Context context;
    private ViewHolder holder;
    private int position;

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        //Membuat View untuk Menyiapkan dan Memasang Layout yang Akan digunakan pada RecyclerView
        View V = LayoutInflater.from(parent.getContext()).inflate(R.layout.view_design, parent, false);
        return new ViewHolder(V);
    }

    //Deklarasi objek dari Interfece
    dataListener listener;

    //Membuat Konstruktor, untuk menerima input dari Database
    public RecyclerViewAdapter(ArrayList<data_mahasiswa> listMahasiswa, Context context) {
        this.listMahasiswa = listMahasiswa;
        this.context = context;
        listener = (MyListData) context;
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(ViewHolder holder, final int i) {
        //Mengambil Nilai/Value yenag terdapat pada RecyclerView berdasarkan Posisi Tertentu
        final String NIM = listMahasiswa.get(holder.getAdapterPosition()).getNim();
        final String Nama = listMahasiswa.get(holder.getAdapterPosition()).getNama();
        final String Jurusan = listMahasiswa.get(holder.getAdapterPosition()).getJurusan();

        //Memasukan Nilai/Value kedalam View (TextView: NIM, Nama, Jurusan)
        holder.NIM.setText("NIM: " + NIM);
        holder.Nama.setText("Nama: " + Nama);
        holder.Jurusan.setText("Jurusan: " + Jurusan);

        //Menampilkan Menu Update dan Delete saat user melakukan long klik pada salah satu item
        holder.ListItem.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(final View view) {
                final String[] action = {"Update", "Delete"};
                AlertDialog.Builder alert = new AlertDialog.Builder(view.getContext());
                alert.setItems(action, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int i) {
                        switch (i){
                            case 0:
                                /*
                                  Berpindah Activity pada halaman layout updateData
                                  dan mengambil data pada listMahasiswa, berdasarkan posisinya
                                  untuk dikirim pada activity updateData
                                 */
                                Bundle bundle = new Bundle();
                                bundle.putString("dataNIM", listMahasiswa.get(position).getNim());
                                bundle.putString("dataNama", listMahasiswa.get(position).getNama());
                                bundle.putString("dataJurusan", listMahasiswa.get(position).getJurusan());
                                bundle.putString("getPrimaryKey", listMahasiswa.get(position).getKey());
                                Intent intent = new Intent(view.getContext(), update_Data.class);
                                intent.putExtras(bundle);
                                context.startActivity(intent);
                                break;
                            case 1:
                                //Menggunakan interface untuk mengirim data mahasiswa, yang akan dihapus
                                listener.onDeleteData(listMahasiswa.get(position), position);
                                break;
                        }
                    }
                });
                alert.create();
                alert.show();
                return true;
            }
        });
    }

    //Membuat Interfece
    public interface dataListener {
        void onDeleteData(data_mahasiswa data, int position);
    }

    //ViewHolder Digunakan Untuk Menyimpan Referensi Dari View-View
    static class ViewHolder extends RecyclerView.ViewHolder {

        private TextView NIM, Nama, Jurusan;
        private LinearLayout ListItem;

        ViewHolder(View itemView) {
            super(itemView);
            //Menginisialisasi View-View yang terpasang pada layout RecyclerView kita
            NIM = itemView.findViewById(R.id.nim);
            Nama = itemView.findViewById(R.id.nama);
            Jurusan = itemView.findViewById(R.id.jurusan);
            ListItem = itemView.findViewById(R.id.list_item);
        }
    }

    @Override
    public int getItemCount() {
        //Menghitung Ukuran/Jumlah Data Yang Akan Ditampilkan Pada RecyclerView
        return listMahasiswa.size();
    }

}