package com.example.warehouse_legapaket.ui.dashboard

import android.os.Bundle
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.ProgressBar
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.example.warehouse_legapaket.LocalDbHelper
import com.example.warehouse_legapaket.R
import com.example.warehouse_legapaket.databinding.ItemSelectBarangBinding // Menggunakan binding item bawaan Anda jika sesuai, atau inflater manual
import com.example.warehouse_legapaket.ui.barang.BarangFragment
import com.example.warehouse_legapaket.ui.laporan.LaporanFragment
import com.example.warehouse_legapaket.ui.profile.ProfileFragment
import com.example.warehouse_legapaket.ui.trip.TripFragment

class DashboardFragment : Fragment(R.layout.fragment_dashboard) {

    private lateinit var dbHelper: LocalDbHelper

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        dbHelper = LocalDbHelper(requireContext())

        // 1. Ambil Data Real-time dari Database JSON Anda
        val allBarang = dbHelper.getAllBarang()
        val allTrip = dbHelper.getAllTrip()

        // 2. Kalkulasi Angka Statistik Operasional Gudang
        val totalBarang = allBarang.size
        val tripAktif = allTrip.filter { it.status.equals("In Transit", ignoreCase = true) }.size
        val readyToShip = allBarang.filter { it.status.equals("Transit", ignoreCase = true) }.size
        val inSorting = allBarang.filter { it.status.equals("Sorting", ignoreCase = true) || it.status.equals("Income", ignoreCase = true) }.size

        // 3. Suntikkan Nilai Kalkulasi ke ID Widget XML Dashboard
        view.findViewById<TextView>(R.id.tvTotalBarangMasuk).text = totalBarang.toString()
        view.findViewById<TextView>(R.id.tvTripAktifCount).text = tripAktif.toString()
        view.findViewById<TextView>(R.id.tvReadyToShipCount).text = readyToShip.toString()
        view.findViewById<TextView>(R.id.tvInSortingCount).text = inSorting.toString()

        // 4. Logika Grafik Dinamis (Merubah tinggi batang 'Hari Ini' sesuai jumlah barang masuk)
        val chartHariIni = view.findViewById<View>(R.id.viewChartHariIni)
        chartHariIni.post {
            val params = chartHariIni.layoutParams as LinearLayout.LayoutParams

            // Atur batas tinggi maksimal grafik batang (misal: maks 90dp jika total paket mencapai 100+)
            val basisTinggiDp = if (totalBarang == 0) 10 else Math.min(10 + (totalBarang * 3), 90)

            // Konversi nilai DP ke Pixel sistem Android
            val tinggiPixel = TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_DIP,
                basisTinggiDp.toFloat(),
                resources.displayMetrics
            ).toInt()

            params.height = tinggiPixel
            chartHariIni.layoutParams = params
        }

        // 5. Hitung Persentase Kapasitas Maksimal Sortir Gudang (Batas tampung internal misal 200 paket)
        val batasMaksSortir = 200
        val persentaseSortir = if (totalBarang == 0) 0 else Math.min((inSorting * 100) / batasMaksSortir, 100)

        view.findViewById<ProgressBar>(R.id.progressKapasitasSortir).progress = persentaseSortir
        view.findViewById<TextView>(R.id.tvLabelKapasitasSortir).text = "$persentaseSortir% Kapasitas Sortir Terpakai"

        // 6. Logika Dinamis Render "Daftar Paket Terakhir" (Menampilkan 3 paket terbaru)
        val containerPaket = view.findViewById<LinearLayout>(R.id.containerPaketTerakhir)
        containerPaket.removeAllViews() // Bersihkan sisa data dummy bawaan XML

        // Ambil maksimal 3 data paket paling terakhir dimasukkan ke database
        val paketTerakhir = allBarang.takeLast(3).reversed()

        val inflater = LayoutInflater.from(requireContext())
        for (barang in paketTerakhir) {
            // Inflate menggunakan layout baru yang strukturnya persis sesuai keinginan Anda
            val itemView = inflater.inflate(R.layout.item_dashboard_paket, containerPaket, false)

            val tvIcon = itemView.findViewById<TextView>(R.id.tvItemIcon)
            val tvResi = itemView.findViewById<TextView>(R.id.tvItemResi)
            val tvDetail = itemView.findViewById<TextView>(R.id.tvItemDetail)
            val tvBadge = itemView.findViewById<TextView>(R.id.tvItemStatusBadge)

            // Set Data Tekstual Pokok
            tvResi.text = barang.resi
            // Catatan: Jika di model Barang Anda tidak ada properti .berat, Anda bisa hapus teks beratnya
            tvDetail.text = "Tujuan: ${barang.destination}"

            // Atur Visual Icon & Badge secara presisi berdasarkan Status Asli Barang
            when (barang.status.lowercase()) {
                "berkendala" -> {
                    tvIcon.text = "⚠️"
                    tvBadge.text = "Kendala"
                    tvBadge.setTextColor(android.graphics.Color.parseColor("#D32F2F"))
                    tvBadge.setBackgroundResource(R.drawable.bg_status_chip) // Sesuaikan jika ada bg khusus merah
                }
                "diterima" -> {
                    tvIcon.text = "✅"
                    tvBadge.text = "Diterima"
                    tvBadge.setTextColor(android.graphics.Color.parseColor("#2E7D32"))
                }
                "transit" -> {
                    tvIcon.text = "🚚"
                    tvBadge.text = "Transit"
                    tvBadge.setTextColor(android.graphics.Color.parseColor("#0288D1"))
                }
                else -> { // Status default: "Sorting" atau "Income"
                    tvIcon.text = "📦"
                    tvBadge.text = barang.status
                    tvBadge.setTextColor(android.graphics.Color.parseColor("#757575"))
                }
            }

            // Masukkan item view yang sudah jadi ke dalam container di Dashboard
            containerPaket.addView(itemView)
        }

        // 7. Navigasi Aksi Klik Pindah Fragment (Sesuai kode bawaan asli Anda)
        view.findViewById<View>(R.id.btnProfile).setOnClickListener {
            parentFragmentManager.beginTransaction()
                .replace(R.id.fragmentContainer, ProfileFragment())
                .addToBackStack(null)
                .commit()
        }

        view.findViewById<View>(R.id.menuInputBarang).setOnClickListener {
            parentFragmentManager.beginTransaction()
                .replace(R.id.fragmentContainer, BarangFragment())
                .commit()
        }

        // Sesuai instruksi Anda: Menu Scan Resi dibiarkan dulu untuk persiapan Barcode Scanner di kemudian hari
        view.findViewById<View>(R.id.menuScanResi).setOnClickListener {
            parentFragmentManager.beginTransaction()
                .replace(R.id.fragmentContainer, BarangFragment())
                .commit()
        }

        view.findViewById<View>(R.id.menuTrip).setOnClickListener {
            parentFragmentManager.beginTransaction()
                .replace(R.id.fragmentContainer, TripFragment())
                .commit()
        }

        view.findViewById<View>(R.id.menuRiwayat).setOnClickListener {
            parentFragmentManager.beginTransaction()
                .replace(R.id.fragmentContainer, LaporanFragment())
                .commit()
        }
    }
}