package com.adit.penjualannya_adit.kategori

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.adit.penjualannya_adit.Model.ModelKategori
import com.adit.penjualannya_adit.R
import com.adit.penjualannya_adit.adapter.KategoriAdapter
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.firebase.database.*

class DataKategoriActivity : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var btnTambah: FloatingActionButton
    private lateinit var btnBack: ImageView
    private lateinit var etSearch: EditText

    private lateinit var adapter: KategoriAdapter
    private val listKategori    = mutableListOf<ModelKategori>()
    private val listOriginal    = mutableListOf<ModelKategori>()

    // Firebase
    private val database     = FirebaseDatabase.getInstance()
    private val kategoriRef  = database.getReference("kategori")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_data_kategori)

        initView()
        setupRecyclerView()
        setupSearch()
        setupClickListener()
        loadDataFromFirebase()
    }

    private fun initView() {
        recyclerView = findViewById(R.id.recyclerView)
        btnTambah    = findViewById(R.id.btnTambah)
        btnBack      = findViewById(R.id.btnBack)
        etSearch     = findViewById(R.id.etSearch)
    }

    private fun setupRecyclerView() {
        adapter = KategoriAdapter(
            listKategori  = listKategori,
            onItemClick   = { kategori -> bukaEditKategori(kategori) },
            onItemLongClick = { kategori -> showDeleteDialog(kategori) }
        )
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter       = adapter
    }

    private fun setupSearch() {
        etSearch.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                adapter.filter(s.toString(), listOriginal)
            }
            override fun afterTextChanged(s: Editable?) {}
        })
    }

    private fun setupClickListener() {
        btnBack.setOnClickListener { finish() }

        btnTambah.setOnClickListener {
            startActivity(Intent(this, ModKategoriActivity::class.java))
        }
    }

    private fun loadDataFromFirebase() {
        kategoriRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                listKategori.clear()
                listOriginal.clear()

                for (data in snapshot.children) {
                    val kategori = data.getValue(ModelKategori::class.java)
                    if (kategori != null) {
                        listKategori.add(kategori)
                        listOriginal.add(kategori)
                    }
                }
                adapter.updateData(listOriginal)
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(
                    this@DataKategoriActivity,
                    "Gagal memuat data: ${error.message}",
                    Toast.LENGTH_SHORT
                ).show()
            }
        })
    }

    private fun bukaEditKategori(kategori: ModelKategori) {
        val intent = Intent(this, ModKategoriActivity::class.java).apply {
            putExtra("id",            kategori.id)
            putExtra("namaKategori",  kategori.namaKategori)
            putExtra("status",        kategori.status)
        }
        startActivity(intent)
    }

    private fun showDeleteDialog(kategori: ModelKategori) {
        AlertDialog.Builder(this)
            .setTitle("Hapus Kategori")
            .setMessage("Apakah kamu yakin ingin menghapus '${kategori.namaKategori}'?")
            .setPositiveButton("Hapus") { _, _ ->
                hapusKategori(kategori)
            }
            .setNegativeButton("Batal", null)
            .show()
    }

    private fun hapusKategori(kategori: ModelKategori) {
        kategoriRef.child(kategori.id).removeValue()
            .addOnSuccessListener {
                Toast.makeText(this, "Kategori berhasil dihapus", Toast.LENGTH_SHORT).show()
            }
            .addOnFailureListener { e ->
                Toast.makeText(this, "Gagal menghapus: ${e.message}", Toast.LENGTH_SHORT).show()
            }
    }
}