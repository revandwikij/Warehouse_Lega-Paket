package com.example.warehouse_legapaket.ui.trip

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.warehouse_legapaket.LocalDbHelper
import com.example.warehouse_legapaket.R
import com.example.warehouse_legapaket.model.TripModel
import com.example.warehouse_legapaket.ui.profile.ProfileFragment

class TripFragment : Fragment(
    R.layout.fragment_trip
) {

    private lateinit var dbHelper: LocalDbHelper
    private lateinit var rvTrip: RecyclerView
    private lateinit var tvCountTotalTrip: TextView
    private lateinit var tvCountInTransit: TextView
    private var listTrip = mutableListOf<TripModel>()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        dbHelper = LocalDbHelper(requireContext())

        // Inisialisasi komponen tampilan
        rvTrip = view.findViewById(R.id.rvTrip)
        tvCountTotalTrip = view.findViewById(R.id.tvCountTotalTrip)
        tvCountInTransit = view.findViewById(R.id.tvCountInTransit)

        rvTrip.layoutManager = LinearLayoutManager(requireContext())

        // Navigasi ke menu profile
        view.findViewById<View>(R.id.btnProfile).setOnClickListener {
            parentFragmentManager.beginTransaction()
                .replace(R.id.fragmentContainer, ProfileFragment())
                .addToBackStack(null)
                .commit()
        }

        // Tombol FAB (+) Menuju Buat Trip Baru
        view.findViewById<View>(R.id.fabAddTrip).setOnClickListener {
            startActivity(Intent(requireContext(), InputTripActivity::class.java))
        }
    }

    override fun onResume() {
        super.onResume()
        // Muat ulang data real-time setiap kali user kembali ke fragment ini
        loadDataTrip()
    }

    private fun loadDataTrip() {
        listTrip = dbHelper.getAllTrip().toMutableList()

        // 1. HITUNG DATA ASLI UNTUK KARTU STATISTIK ATAS
        val totalTripAktif = listTrip.size
        val totalPaketTransit = dbHelper.getAllBarang().count { it.status.equals("Transit", ignoreCase = true) }

        // Set teks angkanya secara dinamis
        tvCountTotalTrip.text = totalTripAktif.toString()
        tvCountInTransit.text = totalPaketTransit.toString()

        // 2. PASANG DATA KE LIST RECYCLERVIEW
        rvTrip.adapter = object : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

            override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
                // Pastikan Anda sudah membuat file item_trip.xml sebelumnya
                val v = LayoutInflater.from(parent.context).inflate(R.layout.item_trip, parent, false)
                return object : RecyclerView.ViewHolder(v) {}
            }

            override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
                val trip = listTrip[position]

                holder.itemView.findViewById<TextView>(R.id.tvIdTrip).text = trip.idTrip
                holder.itemView.findViewById<TextView>(R.id.tvStatusTrip).text = trip.status
                holder.itemView.findViewById<TextView>(R.id.tvDriverTrip).text = "Driver: ${trip.driver}"
                holder.itemView.findViewById<TextView>(R.id.tvArmadaTrip).text = "Armada: ${trip.armada}"
                holder.itemView.findViewById<TextView>(R.id.tvDestinationTrip).text = "Tujuan Hub: ${trip.destination}"
                holder.itemView.findViewById<TextView>(R.id.tvTotalBarang).text = "Muatan: ${trip.listResi.size} Paket"

                // Aksi buka manifes membawa ID Trip ke DetailTripActivity
                holder.itemView.findViewById<Button>(R.id.btnDetailTrip).setOnClickListener {
                    val intent = Intent(requireContext(), DetailTripActivity::class.java)
                    intent.putExtra("EXTRA_ID_TRIP", trip.idTrip)
                    startActivity(intent)
                }
            }

            override fun getItemCount(): Int = listTrip.size
        }
    }
}