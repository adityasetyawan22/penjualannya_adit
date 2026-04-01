package com.adit.penjualannya_adit.kategori

import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.adit.penjualannya_adit.Model.ModelKategori
import com.adit.penjualannya_adit.R
import com.google.android.material.button.MaterialButton
import com.google.android.material.textfield.TextInputEditText
import com.google.firebase.database.FirebaseDatabase

class ModKategoriActivity : AppCompatActivity() {

    private lateinit var etNamaKategori: TextInputEditText
    private lateinit var spinnerJenis: AutoCompleteTextView
    private lateinit var btnSimpan: MaterialButton
    private lateinit var btnBack: ImageView

    // Firebase
    private val database = FirebaseDatabase.getInstance()
    private val kategoriRef = database.getReference("kategori")

    // Untuk mode edit
    private var idKategori: String? = null
    private var isEditMode = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_mod_kategori)

        initView()
        setupDropdown()
        checkEditMode()
        setupClickListener()
    }

    private fun initView() {
        etNamaKategori = findViewById(R.id.etNamaKategori)
        spinnerJenis    = findViewById(R.id.spinnerJenis)
        btnSimpan       = findViewById(R.id.btnSimpan)
        btnBack         = findViewById(R.id.btnBack)
    }

    private fun setupDropdown() {
        val statusList = listOf("Aktif", "Nonaktif")
        val adapter = ArrayAdapter(this, android.R.layout.simple_dropdown_item_1line, statusList)
        spinnerJenis.setAdapter(adapter)
    }

    private fun checkEditMode() {
        // Cek apakah ada data dari intent (mode edit)
        idKategori = intent.getStringExtra("id")
        if (idKategori != null) {
            isEditMode = true
            etNamaKategori.setText(intent.getStringExtra("namaKategori"))
            spinnerJenis.setText(intent.getStringExtra("status"), false)
            btnSimpan.text = "Update"
        }
    }

    private fun setupClickListener() {
        btnBack.setOnClickListener { finish() }

        btnSimpan.setOnClickListener {
            val nama   = etNamaKategori.text.toString().trim()
            val status = spinnerJenis.text.toString().trim()

            // Validasi
            if (nama.isEmpty()) {
                etNamaKategori.error = "Nama kategori tidak boleh kosong"
                return@setOnClickListener
            }
            if (status.isEmpty() || status == "Pilih") {
                Toast.makeText(this, "Pilih status kategori", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if (isEditMode) {
                updateKategori(nama, status)
            } else {
                simpanKategori(nama, status)
            }
        }
    }

    private fun simpanKategori(nama: String, status: String) {
        // Generate ID otomatis dari Firebase
        val id = kategoriRef.push().key ?: return

        val data = ModelKategori(
            id            = id,
            namaKategori  = nama,
            status        = status
        )

        kategoriRef.child(id).setValue(data)
            .addOnSuccessListener {
                Toast.makeText(this, "Kategori berhasil disimpan", Toast.LENGTH_SHORT).show()
                finish()
            }
            .addOnFailureListener { e ->
                Toast.makeText(this, "Gagal menyimpan: ${e.message}", Toast.LENGTH_SHORT).show()
            }
    }

    private fun updateKategori(nama: String, status: String) {
        val updates = mapOf(
            "namaKategori" to nama,
            "status"       to status
        )

        kategoriRef.child(idKategori!!).updateChildren(updates)
            .addOnSuccessListener {
                Toast.makeText(this, "Kategori berhasil diupdate", Toast.LENGTH_SHORT).show()
                finish()
            }
            .addOnFailureListener { e ->
                Toast.makeText(this, "Gagal mengupdate: ${e.message}", Toast.LENGTH_SHORT).show()
            }
    }
}