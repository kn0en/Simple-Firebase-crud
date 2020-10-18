package com.example.firebasecrud;

import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.firebasecrud.adapter.RecyclerViewAdapter;
import com.example.firebasecrud.model.data_mahasiswa;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Objects;

public class MyListData extends AppCompatActivity implements RecyclerViewAdapter.dataListener {

    //Deklarasi Variable untuk RecyclerView
    private RecyclerView recyclerView;
    private RecyclerView.Adapter adapter;

    //Deklarasi Variable Database Reference dan ArrayList dengan Parameter Class Model kita.
    private DatabaseReference reference;
    private ArrayList<data_mahasiswa> dataMahasiswa;

    private FirebaseAuth auth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R .layout.activity_my_list_data);
        recyclerView = findViewById(R.id.datalist);
        Objects.requireNonNull(getSupportActionBar()).setTitle("Data Mahasiswa");
        auth = FirebaseAuth.getInstance();
        MyRecyclerView();
        GetData();
    }

    //Berisi baris kode untuk mengambil data dari Database dan menampilkannya kedalam Adapter
    private void GetData(){
        //Mendapatkan Referensi Database
        reference = FirebaseDatabase.getInstance().getReference();
        reference.child("Admin").child(Objects.requireNonNull(auth.getUid())).child("Mahasiswa")
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        //Inisialisasi ArrayList
                        dataMahasiswa = new ArrayList<>();

                        for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                            //Mapping data pada DataSnapshot ke dalam objek mahasiswa
                            data_mahasiswa mahasiswa = snapshot.getValue(data_mahasiswa.class);

                            //Mengambil Primary Key, digunakan untuk proses Update dan Delete
                            assert mahasiswa != null;
                            mahasiswa.setKey(snapshot.getKey());
                            dataMahasiswa.add(mahasiswa);
                        }

                        //Inisialisasi Adapter dan data Mahasiswa dalam bentuk Array
                        adapter = new RecyclerViewAdapter(dataMahasiswa, MyListData.this);

                        //Memasang Adapter pada RecyclerView
                        recyclerView.setAdapter(adapter);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
              /*
                Kode ini akan dijalankan ketika ada error dan
                pengambilan data error tersebut lalu memprint error nya
                ke LogCat
               */
                        Toast.makeText(getApplicationContext(), "Data Gagal Dimuat", Toast.LENGTH_LONG).show();
                        Log.e("MyListActivity", databaseError.getDetails() + " " + databaseError.getMessage());
                    }
                });
    }

    //Methode yang berisi kumpulan baris kode untuk mengatur RecyclerView
    private void MyRecyclerView(){
        //Menggunakan Layout Manager, Dan Membuat List Secara Vertical
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setHasFixedSize(true);

        //Membuat Underline pada Setiap Item Didalam List
        DividerItemDecoration itemDecoration = new DividerItemDecoration(getApplicationContext(), DividerItemDecoration.VERTICAL);
        itemDecoration.setDrawable(Objects.requireNonNull(ContextCompat.getDrawable(getApplicationContext(), R.drawable.line)));
        recyclerView.addItemDecoration(itemDecoration);
    }

    @Override
    public void onDeleteData(data_mahasiswa data, int position) {
        /*
         * Kode ini akan dipanggil ketika method onDeleteData
         * dipanggil dari adapter pada RecyclerView melalui interface.
         * kemudian akan menghapus data berdasarkan primary key dari data tersebut
         * Jika berhasil, maka akan memunculkan Toast
         */
        String userID = auth.getUid();
        if(reference != null){
            assert userID != null;
            reference.child("Admin")
                    .child(userID)
                    .child("Mahasiswa")
                    .child(data.getKey())
                    .removeValue()
                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            Toast.makeText(MyListData.this, "Data Berhasil Dihapus", Toast.LENGTH_SHORT).show();
                        }
                    });
        }
    }
}
