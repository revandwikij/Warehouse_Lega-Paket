package com.example.warehouse_legapaket.ui.laporan

import android.content.ContentValues
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.example.warehouse_legapaket.LocalDbHelper
import com.example.warehouse_legapaket.R
import com.example.warehouse_legapaket.databinding.FragmentLaporanBinding
import com.example.warehouse_legapaket.ui.profile.ProfileFragment
import java.io.File
import java.io.FileWriter
import java.io.OutputStream

class LaporanFragment : Fragment() {

    private var _binding: FragmentLaporanBinding? = null
    private val binding get() = _binding!!

    // Inisialisasi Database Lokal JSON Anda
    private lateinit var dbHelper: LocalDbHelper

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentLaporanBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        dbHelper = LocalDbHelper(requireContext())

        setupActionListeners()
        kalkulasiDataLaporan()
    }

    private fun setupActionListeners() {
        binding.btnProfile.setOnClickListener {
            val profileFragment = ProfileFragment()
            parentFragmentManager.beginTransaction()
                .replace(R.id.fragmentContainer, profileFragment)
                .addToBackStack(null)
                .commit()
        }

        binding.btnExport.setOnClickListener {
            jalankanDownloadLaporanLangsung()
        }
    }

    /**
     * Hitung ringkasan statistik yang muncul di layar UI Laporan
     */
    private fun kalkulasiDataLaporan() {
        val allBarang = dbHelper.getAllBarang()

        val totalBarangMasuk = allBarang.size
        val totalBarangKeluar = allBarang.filter { it.status.equals("Diterima", ignoreCase = true) }.size
        val totalDalamGudang = allBarang.filter {
            it.status.equals("Sorting", ignoreCase = true) || it.status.equals("Income", ignoreCase = true)
        }.size
        val totalBerkendala = allBarang.filter { it.status.equals("Berkendala", ignoreCase = true) }.size

        binding.tvLaporanBarangMasuk.text = totalBarangMasuk.toString()
        binding.tvLaporanBarangKeluar.text = totalBarangKeluar.toString()
        binding.tvLaporanDalamGudang.text = totalDalamGudang.toString()
        binding.tvJumlahInsidenBadge.text = "$totalBerkendala Insiden"
    }

    /**
     * PROSES DOWNLOAD OTOMATIS: Mengambil data riil dari DB dan disimpan ke folder Download HP
     */
    private fun jalankanDownloadLaporanLangsung() {
        val allBarang = dbHelper.getAllBarang()

        if (allBarang.isEmpty()) {
            Toast.makeText(requireContext(), "Gagal: Tidak ada data barang untuk di-export!", Toast.LENGTH_SHORT).show()
            return
        }

        // Susun nama file unik menggunakan timestamp waktu saat ini
        val namaFile = "Laporan_Aktivitas_Gudang_${System.currentTimeMillis()}.csv"

        // Buat struktur isi file Excel (.csv) secara dinamis lewat StringBuilder
        val isiKontenCSV = StringBuilder()

        // 1. Tulis Header Kolom
        isiKontenCSV.append("No;Nomor Resi;Kota Tujuan;Status Terakhir\n")

        // 2. Looping menyuntikkan seluruh baris data dari database lokal Anda
        for ((index, barang) in allBarang.withIndex()) {
            val nomor = index + 1
            isiKontenCSV.append("$nomor;${barang.resi};${barang.destination};${barang.status}\n")
        }

        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                // Android 10 ke atas menggunakan MediaStore API (Sangat aman, langsung tembus folder Download)
                val resolver = requireContext().contentResolver
                val contentValues = ContentValues().apply {
                    put(MediaStore.MediaColumns.DISPLAY_NAME, namaFile)
                    put(MediaStore.MediaColumns.MIME_TYPE, "text/comma-separated-values")
                    put(MediaStore.MediaColumns.RELATIVE_PATH, Environment.DIRECTORY_DOWNLOADS)
                }

                val uri = resolver.insert(MediaStore.Downloads.EXTERNAL_CONTENT_URI, contentValues)
                if (uri != null) {
                    val outputStream: OutputStream? = resolver.openOutputStream(uri)
                    outputStream?.use {
                        it.write(isiKontenCSV.toString().toByteArray())
                    }
                    Toast.makeText(requireContext(), "Berhasil diunduh ke folder Download!", Toast.LENGTH_LONG).show()
                } else {
                    throw Exception("Gagal mengalokasikan URI penyimpanan.")
                }
            } else {
                // Android 9 ke bawah (Metode File Legacy)
                val downloadDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)
                if (!downloadDir.exists()) downloadDir.mkdirs()

                val fileTarget = File(downloadDir, namaFile)
                val writer = FileWriter(fileTarget)
                writer.append(isiKontenCSV.toString())
                writer.flush()
                writer.close()
                Toast.makeText(requireContext(), "Berhasil diunduh ke: ${fileTarget.absolutePath}", Toast.LENGTH_LONG).show()
            }
        } catch (e: Exception) {
            e.printStackTrace()
            Toast.makeText(requireContext(), "Gagal mengunduh file: ${e.message}", Toast.LENGTH_LONG).show()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}