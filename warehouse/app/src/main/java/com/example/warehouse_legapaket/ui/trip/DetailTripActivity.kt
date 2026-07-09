package com.example.warehouse_legapaket.ui.trip

import android.os.Bundle
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.warehouse_legapaket.LocalDbHelper
import com.example.warehouse_legapaket.databinding.ActivityDetailTripBinding
import com.example.warehouse_legapaket.databinding.ItemSelectBarangBinding
import com.example.warehouse_legapaket.model.BarangModel
import com.example.warehouse_legapaket.model.TripModel

class DetailTripActivity : AppCompatActivity() {

    private lateinit var binding: ActivityDetailTripBinding
    private lateinit var dbHelper: LocalDbHelper
    private var idTrip: String? = null
    private var listBarangMuatan = mutableListOf<BarangModel>()

    // Perbaikan utama: Pindahkan currentTrip ke global agar bisa diakses oleh RecyclerView
    private var currentTrip: TripModel? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDetailTripBinding.inflate(layoutInflater)
        setContentView(binding.root)

        dbHelper = LocalDbHelper(this)

        // 1. Tangkap parameter ID Trip dari halaman sebelumnya
        idTrip = intent.getStringExtra("EXTRA_ID_TRIP")

        binding.btnBack.setOnClickListener { finish() }

        // 2. Jalankan pemuatan data manifes
        displayDetailTrip()
    }

    private fun displayDetailTrip() {
        if (idTrip == null) {
            Toast.makeText(this, "ID Trip bermasalah!", Toast.LENGTH_SHORT).show()
            finish()
            return
        }

        // Cari manifest trip yang cocok dari Local Database JSON
        currentTrip = dbHelper.getAllTrip().find { it.idTrip == idTrip }

        val trip = currentTrip
        if (trip != null) {
            // Pasang data tekstual manifes ke ID XML
            binding.tvIdTrip.text = trip.idTrip
            binding.tvStatusTrip.text = trip.status
            binding.tvDriver.text = trip.driver
            binding.tvArmada.text = trip.armada
            binding.tvDestination.text = "Tujuan: ${trip.destination}"
            binding.tvPackageTitle.text = "Daftar Paket Muatan (${trip.listResi.size})"

            // Sembunyikan tombol jika status trip memang sudah diselesaikan sebelumnya
            if (trip.status.equals("Completed", ignoreCase = true)) {
                binding.btnSelesaiTrip.visibility = android.view.View.GONE
                binding.tvStatusTrip.text = "Diterima"
                binding.tvStatusTrip.setTextColor(android.graphics.Color.WHITE)
                binding.tvStatusTrip.setBackgroundColor(android.graphics.Color.parseColor("#2E7D32"))
            }

            val masterBarang = dbHelper.getAllBarang()
            listBarangMuatan = masterBarang.filter { barang ->
                trip.listResi.contains(barang.resi)
            }.toMutableList()

            setupRecyclerView()

            // LOGIKA TOMBOL SELESAIKAN PERJALANAN
            binding.btnSelesaiTrip.setOnClickListener {
                val updatedTrip = TripModel(
                    idTrip = trip.idTrip,
                    driver = trip.driver,
                    armada = trip.armada,
                    destination = trip.destination,
                    status = "Completed",
                    listResi = trip.listResi
                )
                dbHelper.saveTrip(updatedTrip)

                // Update status barang yang dibawa
                masterBarang.forEach { barang ->
                    if (trip.listResi.contains(barang.resi)) {
                        // FILTER: Hanya ubah ke "Diterima" jika paket TIDAK berkendala!
                        if (!barang.status.equals("Berkendala", ignoreCase = true)) {
                            barang.status = "Diterima"
                            dbHelper.saveBarang(barang)
                        }
                    }
                }

                Toast.makeText(this, "Manifes ${trip.idTrip} Berhasil Diproses ke Hub!", Toast.LENGTH_LONG).show()
                finish()
            }

        } else {
            Toast.makeText(this, "Data Trip tidak ditemukan di database!", Toast.LENGTH_SHORT).show()
            finish()
        }
    }

    private fun setupRecyclerView() {
        binding.rvBarangTrip.layoutManager = LinearLayoutManager(this)
        binding.rvBarangTrip.adapter = object : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

            override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
                val itemBinding = ItemSelectBarangBinding.inflate(LayoutInflater.from(parent.context), parent, false)
                return object : RecyclerView.ViewHolder(itemBinding.root) {}
            }

            override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
                val barang = listBarangMuatan[position]
                val itemBinding = ItemSelectBarangBinding.bind(holder.itemView)

                // 1. Cek status paket saat ini untuk menentukan tampilan teks & warna secara dinamis
                if (barang.status.equals("Berkendala", ignoreCase = true)) {
                    itemBinding.cbBarang.text = " ⚠️ [BERKENDALA] ${barang.resi} • ${barang.destination}"
                    itemBinding.cbBarang.setTextColor(android.graphics.Color.parseColor("#D32F2F"))
                    itemBinding.cbBarang.isChecked = false
                } else if (barang.status.equals("Diterima", ignoreCase = true)) {
                    itemBinding.cbBarang.text = " ✅ [DITERIMA HUB] ${barang.resi} • ${barang.destination}"
                    itemBinding.cbBarang.setTextColor(android.graphics.Color.parseColor("#2E7D32"))
                    itemBinding.cbBarang.isChecked = true
                } else {
                    itemBinding.cbBarang.text = " 📦 ${barang.resi}  •  ${barang.destination}"
                    itemBinding.cbBarang.setTextColor(android.graphics.Color.parseColor("#333333"))
                    itemBinding.cbBarang.isChecked = true
                }

                itemBinding.cbBarang.setOnCheckedChangeListener(null)
                itemBinding.cbBarang.isEnabled = true

                itemBinding.cbBarang.setOnClickListener {
                    itemBinding.cbBarang.isChecked = !barang.status.equals("Berkendala", ignoreCase = true)
                    Toast.makeText(this@DetailTripActivity, "Tekan lama (tahan) untuk mengubah status paket", Toast.LENGTH_SHORT).show()
                }

                // 2. Akses Klik Tahan (Long Click) untuk memicu menu klaim Paket Berkendala / Pulihkan ke Transit
                itemBinding.cbBarang.setOnLongClickListener {
                    // Jika barang sudah telanjur "Diterima" di hub tujuan, kunci total
                    if (barang.status.equals("Diterima", ignoreCase = true)) {
                        return@setOnLongClickListener true
                    }

                    val dialogBuilder = androidx.appcompat.app.AlertDialog.Builder(this@DetailTripActivity)

                    if (barang.status.equals("Berkendala", ignoreCase = true)) {
                        // Sesuai request: Dikembalikan murni ke status Transit
                        dialogBuilder.setTitle("Pulihkan Status Paket")
                            .setMessage("Apakah paket dengan resi ${barang.resi} ini sudah aman dan ingin dikembalikan ke status Normal (Transit)?")
                            .setPositiveButton("Ya, Normalkan") { _, _ ->
                                val masterBarang = dbHelper.getAllBarang()
                                val targetBarang = masterBarang.find { it.resi == barang.resi }

                                if (targetBarang != null) {
                                    targetBarang.status = "Transit"
                                    dbHelper.saveBarang(targetBarang)

                                    Toast.makeText(this@DetailTripActivity, "Status paket ${barang.resi} kembali Normal (Transit)!", Toast.LENGTH_SHORT).show()
                                    displayDetailTrip()
                                }
                            }
                    } else {
                        dialogBuilder.setTitle("Laporkan Masalah Paket")
                            .setMessage("Apakah Anda ingin menandai resi ${barang.resi} ini sebagai Paket Berkendala?")
                            .setPositiveButton("Ya, Berkendala") { _, _ ->
                                val masterBarang = dbHelper.getAllBarang()
                                val targetBarang = masterBarang.find { it.resi == barang.resi }

                                if (targetBarang != null) {
                                    targetBarang.status = "Berkendala"
                                    dbHelper.saveBarang(targetBarang)

                                    Toast.makeText(this@DetailTripActivity, "Status paket ${barang.resi} diubah menjadi Berkendala!", Toast.LENGTH_SHORT).show()
                                    displayDetailTrip()
                                }
                            }
                    }

                    dialogBuilder.setNegativeButton("Batal", null).show()
                    true
                }
            }

            override fun getItemCount(): Int = listBarangMuatan.size
        }
    }
}