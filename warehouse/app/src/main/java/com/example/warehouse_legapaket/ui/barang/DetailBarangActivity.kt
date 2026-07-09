package com.example.warehouse_legapaket.ui.barang

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.warehouse_legapaket.LocalDbHelper
import com.example.warehouse_legapaket.databinding.ActivityDetailBarangBinding
import com.example.warehouse_legapaket.model.BarangModel
class DetailBarangActivity : AppCompatActivity() {

    private lateinit var binding: ActivityDetailBarangBinding
    private lateinit var dbHelper: LocalDbHelper
    private var nomorResi: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityDetailBarangBinding.inflate(layoutInflater)
        setContentView(binding.root)

        dbHelper = LocalDbHelper(this)

        // 1. Ambil nomor resi yang dikirim dari list barang fragment
        nomorResi = intent.getStringExtra("EXTRA_RESI")

        // Tombol Back/Kembali
        binding.btnBack.setOnClickListener { finish() }

        // Tampilkan data barang dari database JSON
        displayDetailBarang()
    }

    private fun displayDetailBarang() {
        if (nomorResi == null) {
            Toast.makeText(this, "Resi tidak valid!", Toast.LENGTH_SHORT).show()
            finish()
            return
        }

        // 2. Cari data barang spesifik dari database JSON berdasarkan resi
        val listBarang = dbHelper.getAllBarang()
        val barang = listBarang.find { it.resi.equals(nomorResi, ignoreCase = true) }

        if (barang != null) {
            // 3. Pasang data asli ke XML
            binding.tvResi.text = barang.resi
            binding.tvDate.text = "Tanggal Masuk: ${barang.date}"
            binding.tvPengirim.text = "Pengirim: ${barang.pengirim}"
            binding.tvPenerima.text = "Penerima: ${barang.penerima}"
            binding.tvDestination.text = "Kota Tujuan: ${barang.destination}"
            binding.tvAgent.text = "Drop Point/Agen: ${barang.agent}"
            binding.tvNamaBarang.text = "Isi Paket: ${barang.namaBarang}"
            binding.tvBerat.text = "Berat: ${barang.weight}"
            binding.tvStatus.text = barang.status

            // 4. Kelola text dan aksi tombol btnStatus berdasarkan status barang saat ini
            setupTombolStatus(barang)
        } else {
            Toast.makeText(this, "Barang tidak ditemukan!", Toast.LENGTH_SHORT).show()
            finish()
        }
    }

    private fun setupTombolStatus(barang: BarangModel) {
        when (barang.status) {
            "Masuk" -> {
                binding.btnStatus.visibility = View.VISIBLE
                binding.btnStatus.text = "Mulai Sortir"
                binding.btnStatus.isEnabled = true

                binding.btnStatus.setOnClickListener {
                    // UBAH STATUS MENJADI SORTIR
                    barang.status = "Sortir"
                    dbHelper.saveBarang(barang) // Simpan perubahan ke database JSON

                    Toast.makeText(this, "Barang berhasil dipindahkan ke area Sortir!", Toast.LENGTH_SHORT).show()

                    // Refresh tampilan detail agar teks status & tombol berubah otomatis
                    displayDetailBarang()
                }
            }
            "Sortir" -> {
                // Jika sudah disortir, tombol kita ubah teksnya menandakan dia siap masuk ke menu Trip
                binding.btnStatus.visibility = View.VISIBLE
                binding.btnStatus.text = "Siap Masuk Trip"
                binding.btnStatus.isEnabled = false // Disable karena proses muat mobil dilakukan di halaman Buat Trip
            }
            "Transit" -> {
                binding.btnStatus.visibility = View.VISIBLE
                binding.btnStatus.text = "Sedang Dalam Perjalanan"
                binding.btnStatus.isEnabled = false
            }
            "Selesai" -> {
                // Jika barang sudah sampai, sembunyikan tombol aksi
                binding.btnStatus.visibility = View.GONE
            }
        }
    }
}