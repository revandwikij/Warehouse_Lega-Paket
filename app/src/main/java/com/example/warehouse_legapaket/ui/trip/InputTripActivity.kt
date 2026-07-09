package com.example.warehouse_legapaket.ui.trip

import android.os.Bundle
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.warehouse_legapaket.LocalDbHelper
import com.example.warehouse_legapaket.databinding.ActivityInputTripBinding
import com.example.warehouse_legapaket.databinding.ItemSelectBarangBinding
import com.example.warehouse_legapaket.model.BarangModel
import com.example.warehouse_legapaket.model.TripModel

class InputTripActivity : AppCompatActivity() {

    private lateinit var binding: ActivityInputTripBinding
    private lateinit var dbHelper: LocalDbHelper
    private var allBarangSortir = mutableListOf<BarangModel>()
    private var filteredBarangSortir = mutableListOf<BarangModel>()
    private val selectedResiList = mutableListOf<String>()

    private val driverArmadaMap = mapOf(
        "Budi Santoso" to "B 9123 XA (Truk Engkel)",
        "Ahmad Hidayat" to "D 4452 TQ (Mobil Box)",
        "Siti Nurhaliza" to "L 8810 OP (Truk Fuso)",
        "Eko Prasetyo" to "B 3321 Kk (Truk CDD)",
        "Rian Wijaya" to "F 7741 AA (Mobil Blindvan)"
    )

    // Daftar pilihan Hub Tujuan
    private val listHubTujuan = listOf("Jakarta Hub", "Surabaya Hub", "Bandung Hub", "Semarang Hub", "Medan Hub")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Mengikat layout activity_input_trip.xml ke objek binding
        binding = ActivityInputTripBinding.inflate(layoutInflater)
        setContentView(binding.root)

        dbHelper = LocalDbHelper(this)

        binding.btnBack.setOnClickListener { finish() }

        // 1. Ambil seluruh data master barang berstatus "Sortir"
        allBarangSortir = dbHelper.getAllBarang()
            .filter { it.status.equals("Sortir", ignoreCase = true) }
            .toMutableList()

        // 2. Setup Dropdown Hub Tujuan
        setupHubTujuanDropdown()

        // 3. Setup Dropdown Nama Driver & Plat Otomatis
        setupDriverDropdown()

        // 4. Setup RecyclerView (Awalnya kosong sebelum Hub dipilih)
        setupRecyclerView()

        // 5. Logika Tombol / CheckBox Pilih Semua (Select All)
        binding.cbSelectAll.setOnCheckedChangeListener { _, isChecked ->
            selectedResiList.clear()
            if (isChecked) {
                // Masukkan semua resi yang sedang disaring (filtered)
                filteredBarangSortir.forEach { selectedResiList.add(it.resi) }
            }
            // Refresh tampilan RecyclerView agar semua checkbox tercentang / tidak
            binding.rvSelectBarang.adapter?.notifyDataSetChanged()
        }
        // 6. Logika Simpan / Rilis Manifes Trip
        binding.btnBuatTrip.setOnClickListener {
            val destination = binding.actvDestination.text.toString().trim()
            val driver = binding.actvDriver.text.toString().trim()
            val armada = binding.etArmada.text.toString().trim()

            if (destination.isEmpty() || driver.isEmpty() || armada.isEmpty()) {
                Toast.makeText(this, "Mohon lengkapi seluruh data manifes!", Toast.LENGTH_SHORT)
                    .show()
                return@setOnClickListener
            }

            if (selectedResiList.isEmpty()) {
                Toast.makeText(
                    this,
                    "Pilih minimal 1 barang untuk dimasukkan ke Trip!",
                    Toast.LENGTH_SHORT
                ).show()
                return@setOnClickListener
            }

            val idTripBaru = "TRP-${System.currentTimeMillis().toString().takeLast(6)}"

            // Simpan Trip Baru
            val tripBaru =
                TripModel(idTripBaru, driver, armada, destination, "In Transit", selectedResiList)
            dbHelper.saveTrip(tripBaru)

            // Update status barang terpilih menjadi Transit
            val masterBarang = dbHelper.getAllBarang()
            masterBarang.forEach { barang ->
                if (selectedResiList.contains(barang.resi)) {
                    barang.status = "Transit"
                    barang.idTrip = idTripBaru
                    dbHelper.saveBarang(barang)
                }
            }

            Toast.makeText(
                this,
                "Trip $idTripBaru menuju $destination Berhasil Dirilis!",
                Toast.LENGTH_SHORT
            ).show()
            finish()
        }
    }

    private fun setupHubTujuanDropdown() {
        val adapter = ArrayAdapter(this, android.R.layout.simple_list_item_1, listHubTujuan)
        binding.actvDestination.setAdapter(adapter)

        binding.actvDestination.setOnClickListener {
            binding.actvDestination.showDropDown()
        }

        binding.actvDestination.setOnItemClickListener { parent, _, position, _ ->
            val selectedHub = parent.getItemAtPosition(position) as String

            // Ambil kata pertama saja (misal "Surabaya Hub" diambil "Surabaya")
            // untuk mengantisipasi ketidakcocokan nama di database barang
            val namaKotaKeyword = selectedHub.split(" ")[0]

            // Saring data barang berdasarkan kata kunci kota tujuan
            filteredBarangSortir = allBarangSortir.filter { barang ->
                barang.destination.contains(namaKotaKeyword, ignoreCase = true) ||
                        barang.destination.contains(selectedHub, ignoreCase = true)
            }.toMutableList()

            // Reset pilihan centang
            selectedResiList.clear()
            binding.cbSelectAll.isChecked = false

            // PASANG ADAPTER BARU ATAU REFRESH ADAPTER SECARA TOTAL
            setupRecyclerView()

            if (filteredBarangSortir.isEmpty()) {
                Toast.makeText(this, "Tidak ada barang 'Sortir' untuk tujuan $selectedHub", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(this, "Berhasil memuat ${filteredBarangSortir.size} paket tujuan $selectedHub", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun setupDriverDropdown() {
        val drivers = driverArmadaMap.keys.toList()
        val adapter = ArrayAdapter(this, android.R.layout.simple_list_item_1, drivers)
        binding.actvDriver.setAdapter(adapter)

        // MEMAKSA DROPDOWN MUNCUL SAAT DIKLIK/DISENTUH
        binding.actvDriver.setOnClickListener {
            binding.actvDriver.showDropDown()
        }

        // Ketika nama Driver dipilih, isikan plat armada secara otomatis
        binding.actvDriver.setOnItemClickListener { parent, _, position, _ ->
            val selectedDriver = parent.getItemAtPosition(position) as String
            val platArmada = driverArmadaMap[selectedDriver]

            // Set otomatis ke edit text armada
            binding.etArmada.setText(platArmada)
        }
    }

    private fun setupRecyclerView() {
        binding.rvSelectBarang.layoutManager = LinearLayoutManager(this)
        binding.rvSelectBarang.adapter = object : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

            override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
                val itemBinding = ItemSelectBarangBinding.inflate(LayoutInflater.from(parent.context), parent, false)
                return object : RecyclerView.ViewHolder(itemBinding.root) {}
            }

            override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
                val barang = filteredBarangSortir[position]
                val itemBinding = ItemSelectBarangBinding.bind(holder.itemView)

                itemBinding.cbBarang.text = " 📦 ${barang.resi}  •  ${barang.destination}"

                // Mencegah bug recycling checkbox tercentang sendiri saat di-scroll
                itemBinding.cbBarang.setOnCheckedChangeListener(null)
                itemBinding.cbBarang.isChecked = selectedResiList.contains(barang.resi)

                itemBinding.cbBarang.setOnCheckedChangeListener { _, isChecked ->
                    if (isChecked) {
                        selectedResiList.add(barang.resi)
                    } else {
                        selectedResiList.remove(barang.resi)
                        // Jika ada 1 saja yang tidak dicentang, matikan centang Pilih Semua
                        binding.cbSelectAll.setOnCheckedChangeListener(null)
                        binding.cbSelectAll.isChecked = false
                        binding.cbSelectAll.setOnCheckedChangeListener { _, isCh ->
                            selectedResiList.clear()
                            if (isCh) filteredBarangSortir.forEach { selectedResiList.add(it.resi) }
                            binding.rvSelectBarang.adapter?.notifyDataSetChanged()
                        }
                    }
                }
            }

            override fun getItemCount(): Int = filteredBarangSortir.size
        }
    }
}