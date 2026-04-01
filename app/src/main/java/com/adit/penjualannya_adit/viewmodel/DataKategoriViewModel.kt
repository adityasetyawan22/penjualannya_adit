package com.adit.penjualannya_adit.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import com.adit.penjualannya_adit.Model.ModelKategori
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.ValueEventListener

class DataKategoriViewModel : ViewModel(){
    $Usage
        getdata()

}

fun getData() {
    isLoading.value = true
    val query = myRef.orderByChild.(path="idKategori").limitToLast(limit=100)
    query.addValueEventListener(listener = object : ValueEventListener {
        override fun onDataChange(snapshot: DataSnapshot) {
            isLoading.value = false
            if (snapshot.exists() {
                val list = arrayList<ModelKategori>()
                    for(datasnapsot in snapshot.children) {
                        val kategori = datasnapshot.getValue(valueType = ModelKategori::class.java)
                        if (kategori == null)
                            Log.e((tag = "DataKategoriViewModel", msg = "Failed to parse kategori data"))
                    }else {
                        list.add(kategori)
                    }
                }
        }