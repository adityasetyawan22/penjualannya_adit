package com.adit.penjualannya_adit

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide

class ProdukAdapter(
    private var listProduk: MutableList<Produk>,
    private val onItemClick: (Produk) -> Unit
) : RecyclerView.Adapter<ProdukAdapter.ProdukViewHolder>() {

    inner class ProdukViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val imgProduk: ImageView        = itemView.findViewById(R.id.imgProduk)
        val tvNamaProduk: TextView      = itemView.findViewById(R.id.tvNamaProduk)
        val tvHarga: TextView           = itemView.findViewById(R.id.tvHarga)
        val tvKategoriProduk: TextView  = itemView.findViewById(R.id.tvKategoriProduk)
        val tvStok: TextView            = itemView.findViewById(R.id.tvStok)
        val tvCabang: TextView          = itemView.findViewById(R.id.tvCabang)
        val tvStatus: TextView          = itemView.findViewById(R.id.tvStatus)
        val layoutStatus: LinearLayout  = itemView.findViewById(R.id.layoutStatus)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProdukViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_produk, parent, false)
        return ProdukViewHolder(view)
    }

    override fun onBindViewHolder(holder: ProdukViewHolder, position: Int) {
        val produk = listProduk[position]

        holder.tvNamaProduk.text    = produk.namaProduk
        holder.tvHarga.text         = "Rp ${String.format("%,.0f", produk.hargaJual)}"
        holder.tvKategoriProduk.text = produk.kategori.ifEmpty { "-" }
        holder.tvCabang.text        = produk.cabang.ifEmpty { "Utama" }
        holder.tvStatus.text        = produk.status

        // Stok
        holder.tvStok.text = if (produk.stokTakTerbatas) "∞ Tak Terbatas"
        else "${produk.stok} pcs"

        // Warna status
        if (produk.status == "Aktif") {
            holder.tvStatus.setTextColor(0xFF4CAF50.toInt())
        } else {
            holder.tvStatus.setTextColor(0xFFAAAAAA.toInt())
        }

        // Foto via Glide
        if (produk.fotoUrl.isNotEmpty()) {
            Glide.with(holder.itemView.context)
                .load(produk.fotoUrl)
                .placeholder(R.drawable.search)
                .centerCrop()
                .into(holder.imgProduk)
        } else {
            holder.imgProduk.setImageResource(R.drawable.search)
        }

        holder.itemView.setOnClickListener { onItemClick(produk) }
    }

    override fun getItemCount(): Int = listProduk.size

    fun updateData(newList: List<Produk>) {
        listProduk.clear()
        listProduk.addAll(newList)
        notifyDataSetChanged()
    }
}