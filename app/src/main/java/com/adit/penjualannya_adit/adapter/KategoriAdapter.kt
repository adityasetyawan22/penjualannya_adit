package com.adit.penjualannya_adit.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.adit.penjualannya_adit.Model.ModelKategori
import com.adit.penjualannya_adit.R

class KategoriAdapter(
    private var listKategori: MutableList<ModelKategori>,
    private val onItemClick: (ModelKategori) -> Unit,
    private val onItemLongClick: (ModelKategori) -> Unit
) : RecyclerView.Adapter<KategoriAdapter.ViewHolder>() {

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tvKategori: TextView  = itemView.findViewById(R.id.tvKategori)
        val tvAktif: TextView     = itemView.findViewById(R.id.tvAktif)
        val imgStatus: ImageView  = itemView.findViewById(R.id.imgStatusAktif)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_data_kategori, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = listKategori[position]

        holder.tvKategori.text = item.namaKategori
        holder.tvAktif.text    = item.status

        // Ubah warna/icon berdasarkan status
        if (item.status == "Aktif") {
            holder.tvAktif.setTextColor(
                holder.itemView.context.getColor(android.R.color.holo_green_dark)
            )
            holder.imgStatus.setImageResource(R.drawable.ceklist)
        } else {
            holder.tvAktif.setTextColor(
                holder.itemView.context.getColor(android.R.color.holo_red_dark)
            )
            holder.imgStatus.setImageResource(R.drawable.ceklist) // ganti dengan icon nonaktif jika ada
        }

        holder.itemView.setOnClickListener       { onItemClick(item) }
        holder.itemView.setOnLongClickListener   { onItemLongClick(item); true }
    }

    override fun getItemCount() = listKategori.size

    // Filter untuk search
    fun filter(query: String, originalList: List<ModelKategori>) {
        listKategori = if (query.isEmpty()) {
            originalList.toMutableList()
        } else {
            originalList.filter {
                it.namaKategori.contains(query, ignoreCase = true)
            }.toMutableList()
        }
        notifyDataSetChanged()
    }

    // Update data
    fun updateData(newList: List<ModelKategori>) {
        listKategori = newList.toMutableList()
        notifyDataSetChanged()
    }
}