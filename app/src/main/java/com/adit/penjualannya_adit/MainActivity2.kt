package com.adit.penjualannya_adit

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
import com.adit.penjualannya_adit.kategori.DataKategoriActivity

class MainActivity2 : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main2)

        findViewById<CardView>(R.id.cardKategori).setOnClickListener {
            startActivity(Intent(this, DataKategoriActivity::class.java))
        }
    }
}