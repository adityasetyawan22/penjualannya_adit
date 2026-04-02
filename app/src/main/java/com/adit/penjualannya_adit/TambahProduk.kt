package com.adit.penjualannya_adit.produk

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.text.Editable
import android.text.TextWatcher
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.adit.penjualannya_adit.R
import com.google.android.material.button.MaterialButton
import com.google.android.material.textfield.TextInputEditText
import com.google.firebase.database.FirebaseDatabase

class TambahProduk : AppCompatActivity() {

    private lateinit var imgProduk: ImageView
    private lateinit var layoutFotoPlaceholder: LinearLayout
    private lateinit var btnKamera: Button
    private lateinit var btnGaleri: Button
    private lateinit var btnBack: ImageView
    private lateinit var tvTitle: TextView
    private lateinit var etNamaProduk: TextInputEditText
    private lateinit var etSku: TextInputEditText
    private lateinit var etBarcode: TextInputEditText
    private lateinit var btnPilihKategori: MaterialButton
    private lateinit var btnPilihCabang: MaterialButton
    private lateinit var etHargaBeli: TextInputEditText
    private lateinit var spinnerTipeKeuntungan: AutoCompleteTextView
    private lateinit var etNilaiProfit: TextInputEditText
    private lateinit var etHargaJual: TextInputEditText
    private lateinit var etStok: TextInputEditText
    private lateinit var cbStokTakTerbatas: CheckBox
    private lateinit var btnSimpan: MaterialButton

    private var selectedImageUri: Uri? = null
    private var selectedKategoriId: String = ""
    private var selectedKategoriNama: String = ""
    private var selectedCabangId: String = ""
    private var selectedCabangNama: String = ""
    private var isEditMode = false
    private var idProduk: String = ""

    private val database = FirebaseDatabase.getInstance()
    private val produkRef = database.getReference("produk")

    companion object {
        const val REQUEST_KAMERA = 101
        const val REQUEST_GALERI = 102
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_tambah_produk)

        initView()
        setupDropdown()
        setupHargaOtomatis()
        checkEditMode()
        setupClickListener()
    }

    private fun initView() {
        imgProduk             = findViewById(R.id.imgProduk)
        layoutFotoPlaceholder = findViewById(R.id.layoutFotoPlaceholder)
        btnKamera             = findViewById(R.id.btnKamera)
        btnGaleri             = findViewById(R.id.btnGaleri)
        btnBack               = findViewById(R.id.btnBack)
        tvTitle               = findViewById(R.id.tvTitle)
        etNamaProduk          = findViewById(R.id.etNamaProduk)
        etSku                 = findViewById(R.id.etSku)
        etBarcode             = findViewById(R.id.etBarcode)
        btnPilihKategori      = findViewById(R.id.btnPilihKategori)
        btnPilihCabang        = findViewById(R.id.btnPilihCabang)
        etHargaBeli           = findViewById(R.id.etHargaBeli)
        spinnerTipeKeuntungan = findViewById(R.id.spinnerTipeKeuntungan)
        etNilaiProfit         = findViewById(R.id.etNilaiProfit)
        etHargaJual           = findViewById(R.id.etHargaJual)
        etStok                = findViewById(R.id.etStok)
        cbStokTakTerbatas     = findViewById(R.id.cbStokTakTerbatas)
        btnSimpan             = findViewById(R.id.btnSimpan)
    }

    private fun setupDropdown() {
        val tipeList = listOf("Persentase (%)", "Nominal (Rp)")
        val adapter = ArrayAdapter(this, android.R.layout.simple_dropdown_item_1line, tipeList)
        spinnerTipeKeuntungan.setAdapter(adapter)
        spinnerTipeKeuntungan.setText("Persentase (%)", false)
    }

    private fun setupHargaOtomatis() {
        val watcher = object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun afterTextChanged(s: Editable?) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                hitungHargaJual()
            }
        }
        etHargaBeli.addTextChangedListener(watcher)
        etNilaiProfit.addTextChangedListener(watcher)
        spinnerTipeKeuntungan.setOnItemClickListener { _, _, _, _ -> hitungHargaJual() }
    }

    private fun hitungHargaJual() {
        val hargaBeli = etHargaBeli.text.toString().toDoubleOrNull() ?: return
        val nilaiProfit = etNilaiProfit.text.toString().toDoubleOrNull() ?: return
        val tipe = spinnerTipeKeuntungan.text.toString()

        val hargaJual = if (tipe.contains("%")) {
            hargaBeli + (hargaBeli * nilaiProfit / 100)
        } else {
            hargaBeli + nilaiProfit
        }
        etHargaJual.setText(hargaJual.toInt().toString())
    }

    private fun checkEditMode() {
        idProduk = intent.getStringExtra("id") ?: ""
        if (idProduk.isNotEmpty()) {
            isEditMode = true
            tvTitle.text = "Edit Produk"
            btnSimpan.text = "Update"
            etNamaProduk.setText(intent.getStringExtra("namaProduk"))
            etSku.setText(intent.getStringExtra("sku"))
            etBarcode.setText(intent.getStringExtra("barcode"))
            etHargaBeli.setText(intent.getStringExtra("hargaBeli"))
            etNilaiProfit.setText(intent.getStringExtra("nilaiProfit"))
            etHargaJual.setText(intent.getStringExtra("hargaJual"))
            etStok.setText(intent.getStringExtra("stok"))
            selectedKategoriNama = intent.getStringExtra("kategoriNama") ?: ""
            selectedCabangNama = intent.getStringExtra("cabangNama") ?: ""
            if (selectedKategoriNama.isNotEmpty()) btnPilihKategori.text = selectedKategoriNama
            if (selectedCabangNama.isNotEmpty()) btnPilihCabang.text = selectedCabangNama
        }
    }

    private fun setupClickListener() {
        btnBack.setOnClickListener { finish() }

        btnKamera.setOnClickListener {
            val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
            startActivityForResult(intent, REQUEST_KAMERA)
        }

        btnGaleri.setOnClickListener {
            val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
            startActivityForResult(intent, REQUEST_GALERI)
        }

        cbStokTakTerbatas.setOnCheckedChangeListener { _, isChecked ->
            etStok.isEnabled = !isChecked
            if (isChecked) etStok.setText("")
        }

        btnSimpan.setOnClickListener {
            simpanProduk()
        }
    }

    private fun simpanProduk() {
        val nama = etNamaProduk.text.toString().trim()
        if (nama.isEmpty()) {
            etNamaProduk.error = "Nama produk tidak boleh kosong"
            return
        }

        val id = if (isEditMode) idProduk else produkRef.push().key ?: return

        val data = mapOf(
            "id"           to id,
            "namaProduk"   to nama,
            "sku"          to etSku.text.toString().trim(),
            "barcode"      to etBarcode.text.toString().trim(),
            "kategoriId"   to selectedKategoriId,
            "kategoriNama" to selectedKategoriNama,
            "cabangId"     to selectedCabangId,
            "cabangNama"   to selectedCabangNama,
            "hargaBeli"    to (etHargaBeli.text.toString().toDoubleOrNull() ?: 0.0),
            "tipeKeuntungan" to spinnerTipeKeuntungan.text.toString(),
            "nilaiProfit"  to (etNilaiProfit.text.toString().toDoubleOrNull() ?: 0.0),
            "hargaJual"    to (etHargaJual.text.toString().toDoubleOrNull() ?: 0.0),
            "stok"         to if (cbStokTakTerbatas.isChecked) -1 else (etStok.text.toString().toIntOrNull() ?: 0),
            "stokTakTerbatas" to cbStokTakTerbatas.isChecked
        )

        produkRef.child(id).setValue(data)
            .addOnSuccessListener {
                Toast.makeText(this,
                    if (isEditMode) "Produk berhasil diupdate" else "Produk berhasil disimpan",
                    Toast.LENGTH_SHORT).show()
                finish()
            }
            .addOnFailureListener { e ->
                Toast.makeText(this, "Gagal: ${e.message}", Toast.LENGTH_SHORT).show()
            }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK) {
            when (requestCode) {
                REQUEST_GALERI -> {
                    selectedImageUri = data?.data
                    imgProduk.setImageURI(selectedImageUri)
                    layoutFotoPlaceholder.visibility = android.view.View.GONE
                }
                REQUEST_KAMERA -> {
                    val bitmap = data?.extras?.get("data") as? android.graphics.Bitmap
                    imgProduk.setImageBitmap(bitmap)
                    layoutFotoPlaceholder.visibility = android.view.View.GONE
                }
            }
        }
    }
}