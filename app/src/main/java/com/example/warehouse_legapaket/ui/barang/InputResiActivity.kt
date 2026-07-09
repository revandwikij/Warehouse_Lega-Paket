package com.example.warehouse_legapaket.ui.barang

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.warehouse_legapaket.LocalDbHelper
import com.example.warehouse_legapaket.model.BarangModel
import com.example.warehouse_legapaket.databinding.ActivityInputResiBinding
import com.journeyapps.barcodescanner.ScanContract
import com.journeyapps.barcodescanner.ScanIntentResult
import com.journeyapps.barcodescanner.ScanOptions

class InputResiActivity : AppCompatActivity() {

    private lateinit var binding: ActivityInputResiBinding
    private lateinit var dbHelper: LocalDbHelper
    private var barangDitemukan: BarangModel? = null

    // 1. Registrasi Kontrak Pihak Ketiga untuk Menangkap Hasil Kamera Scanner
    private val barcodeLauncher = registerForActivityResult(ScanContract()) { result: ScanIntentResult ->
        if (result.contents == null) {
            Toast.makeText(this, "Scan dibatalkan", Toast.LENGTH_SHORT).show()
        } else {
            // Hasil scan teks barcode otomatis dimasukkan ke kolom input resi
            binding.etResi.setText(result.contents)
            // Langsung otomatis eksekusi pencarian ke database lokal
            jalankanPencarianResi()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityInputResiBinding.inflate(layoutInflater)
        setContentView(binding.root)

        dbHelper = LocalDbHelper(this)

        // Sembunyikan layoutDetail bawaan XML saat pertama kali halaman dibuka
        binding.layoutDetail.visibility = View.GONE

        setupListeners()
    }

    private fun setupListeners() {
        // Tombol Kembali di Header
        binding.btnBack.setOnClickListener {
            finish()
        }

        // PEMICU SCANNER: Ketika kotak area scanner di-klik, buka kamera
        binding.cardScannerContainer.setOnClickListener {
            bukaKameraScanner()
        }

        // Fitur Pencarian manual saat tombol kaca pembesar diklik
        binding.btnSearchIcon.setOnClickListener {
            jalankanPencarianResi()
        }

        // Fitur Pencarian otomatis saat klik tombol enter di keyboard virtual
        binding.etResi.setOnEditorActionListener { _, _, _ ->
            jalankanPencarianResi()
            true
        }

        // Tombol Validasi Barang Sortir
        binding.btnValidasiBarang.setOnClickListener {
            val barang = barangDitemukan
            if (barang != null) {
                barang.status = "Sorting"
                dbHelper.saveBarang(barang)

                binding.btnValidasiBarang.text = "✓ Sudah Disortir"
                binding.btnValidasiBarang.isEnabled = false

                Toast.makeText(this, "Sukses! Status resi ${barang.resi} diperbarui menjadi Sorting.", Toast.LENGTH_SHORT).show()
            }
        }
    }

    // 2. Fungsi Pengaturan Tampilan Jendela Kamera Scanner
    private fun bukaKameraScanner() {
        val options = ScanOptions().apply {
            setDesiredBarcodeFormats(ScanOptions.ALL_CODE_TYPES) // Mendukung barcode garis & QR Code
            setPrompt("Arahkan kamera ke Barcode Paket Cargo")
            setCameraId(0) // Menggunakan kamera belakang HP
            setBeepEnabled(true) // Bunyi 'Tit' ketika berhasil scan
            setBarcodeImageEnabled(true)
            setOrientationLocked(true) // Menjaga orientasi layar tetap tegak (portrait)
        }
        barcodeLauncher.launch(options)
    }

    private fun jalankanPencarianResi() {
        val keywordResi = binding.etResi.text.toString().trim()

        if (keywordResi.isEmpty()) {
            binding.etResi.error = "Masukkan nomor resi!"
            return
        }

        val dataBarang = dbHelper.getAllBarang().find { it.resi.equals(keywordResi, ignoreCase = true) }

        if (dataBarang != null) {
            barangDitemukan = dataBarang

            binding.tvResi.text = dataBarang.resi
            binding.tvDestination.text = "📍 ${dataBarang.destination}"
            binding.tvBerat.text = "📦 ${dataBarang.weight}"
            binding.tvPenerima.text = "${dataBarang.penerima} (${dataBarang.agent})"

            binding.layoutDetail.visibility = View.VISIBLE

            if (dataBarang.status.equals("Sorting", ignoreCase = true) || dataBarang.status.equals("Sortir", ignoreCase = true)) {
                binding.btnValidasiBarang.text = "✓ Sudah Disortir"
                binding.btnValidasiBarang.isEnabled = false
            } else {
                binding.btnValidasiBarang.text = "✓ Validasi Barang"
                binding.btnValidasiBarang.isEnabled = true
            }

        } else {
            barangDitemukan = null
            binding.layoutDetail.visibility = View.GONE
            Toast.makeText(this, "Resi tidak terdaftar di sistem kargo gudang!", Toast.LENGTH_LONG).show()
        }
    }
}