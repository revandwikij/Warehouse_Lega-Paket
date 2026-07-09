package com.example.warehouse_legapaket.ui.barang

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.warehouse_legapaket.databinding.ItemBarangBinding
import com.example.warehouse_legapaket.model.BarangModel

class BarangAdapter(
    private val listBarang: MutableList<BarangModel>,
    private val onItemClick: (String) -> Unit
) : RecyclerView.Adapter<BarangAdapter.BarangViewHolder>() {

    // ViewHolder
    class BarangViewHolder(val binding: ItemBarangBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BarangViewHolder {
        val binding = ItemBarangBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return BarangViewHolder(binding)
    }

    override fun onBindViewHolder(holder: BarangViewHolder, position: Int) {
        val barang = listBarang[position]

        // Pakai with(holder.binding) agar tidak perlu membuat variabel TextView manual lagi
        with(holder.binding) {
            tvResi.text = barang.resi
            tvStatus.text = barang.status
            tvAgent.text = "Agent : ${barang.agent}"
            tvDestination.text = "Destination : ${barang.destination}"
            tvWeight.text = "Weight : ${barang.weight}"
            tvDate.text = barang.date

            // Aksi klik langsung dipasang ke root layout item
            root.setOnClickListener { onItemClick(barang.resi) }
        }
    }

    override fun getItemCount(): Int = listBarang.size

    fun updateData(newData: List<BarangModel>) {
        listBarang.clear()
        listBarang.addAll(newData)
        notifyDataSetChanged()
    }
}