package com.example.warehouse_legapaket

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.warehouse_legapaket.databinding.ActivityMainBinding
import com.example.warehouse_legapaket.model.BarangModel
import com.example.warehouse_legapaket.ui.barang.BarangFragment
import com.example.warehouse_legapaket.ui.dashboard.DashboardFragment
import com.example.warehouse_legapaket.ui.laporan.LaporanFragment
import com.example.warehouse_legapaket.ui.trip.TripFragment
import com.example.warehouse_legapaket.ui.login.LoginActivity

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // 1. GERBANG UTAMA: Cek status login (Mencegah NullPointer pada binding jika dilempar ke login)
        val sharedPref = getSharedPreferences("SesiLegaPaket", Context.MODE_PRIVATE)
        val isLogin = sharedPref.getBoolean("IS_LOGIN", false)

        if (!isLogin) {
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
            finish()
            return
        }

        // 2. Inisialisasi View Binding dipastikan aman setelah status terkonfirmasi masuk
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Inisialisasi Dummy Data Gudang Lokal
        val db = LocalDbHelper(this)
        if (db.getAllBarang().isEmpty()) {
            db.saveBarang(BarangModel("LP-2026-00101", "Surabaya", "Agent Leuwi Panjang", "8.5 Kg", "Masuk", "2026-07-07", "Sparepart Motor", "Budi Sentosa", "Roni Wijaya"))
            db.saveBarang(BarangModel("LP-2026-00102", "Semarang", "Agent Leuwi Panjang", "12.0 Kg", "Masuk", "2026-07-07", "Pakaian Garmen", "Zaskia Store", "Siti Aminah"))
            db.saveBarang(BarangModel("LP-2026-00103", "Yogyakarta", "Agent Soreang", "4.2 Kg", "Masuk", "2026-07-07", "Kosmetik / Skincare", "Cantik Bersolek", "Dewi Lestari"))
            db.saveBarang(BarangModel("LP-2026-00104", "Solo", "Agent Soreang", "15.5 Kg", "Masuk", "2026-07-07", "Elektronik / HP", "Maju Jaya Selular", "Hendra Setiawan"))
            db.saveBarang(BarangModel("LP-2026-00105", "Denpasar", "Agent Margaasih", "9.1 Kg", "Masuk", "2026-07-07", "Makanan Kering", "Oleh-Oleh Sanur", "I Gede Sukarta"))
            db.saveBarang(BarangModel("LP-2026-00107", "Medan", "Agent Soreang", "18.3 Kg", "Masuk", "2026-07-07", "Alat Olahraga", "Fit Fitness", "Andi Wijaya"))
            db.saveBarang(BarangModel("LP-2026-00108", "Palembang", "Agent Margaasih", "5.0 Kg", "Masuk", "2026-07-07", "Buku / Dokumen", "Pustaka Ilmu", "Eko Prasetyo"))
            db.saveBarang(BarangModel("LP-2026-00109", "Balikpapan", "Agent Leuwi Panjang", "30.5 Kg", "Masuk", "2026-07-07", "Besi Baja Utama", "Rian Hidayat", "Haji Muhidin"))
            db.saveBarang(BarangModel("LP-2026-00110", "Malang", "Agent Margaasih", "6.7 Kg", "Masuk", "2026-07-07", "Sepatu Cargo", "Grosir Cibaduyut", "Anwar Sadat"))

            //kendala
            db.saveBarang(BarangModel("LP-2026-00106", "Makassar", "Agent Margaasih", "22.1 Kg", "Kendala", "2026-07-07", "Cairan Kimia / Oli", "PT. Sumber Oli", "Daeng Udin"))
        }

        replaceFragment(DashboardFragment())

        binding.bottomNavigation.setOnItemSelectedListener {
            when (it.itemId) {
                R.id.nav_dashboard -> replaceFragment(DashboardFragment())
                R.id.nav_barang -> replaceFragment(BarangFragment())
                R.id.nav_trip -> replaceFragment(TripFragment())
                R.id.nav_laporan -> replaceFragment(LaporanFragment())
            }
            true
        }
    }

    fun hideBottomNav() {
        if (::binding.isInitialized) binding.bottomNavigation.visibility = android.view.View.GONE
    }

    fun showBottomNav() {
        if (::binding.isInitialized) binding.bottomNavigation.visibility = android.view.View.VISIBLE
    }

    private fun replaceFragment(fragment: androidx.fragment.app.Fragment) {
        // Otomatisasi pendeteksian kontainer layout agar adaptif terhadap nama ID XML Anda
        val containerId = resources.getIdentifier("nav_host_fragment_activity_main", "id", packageName)
        val finalId = if (containerId != 0) containerId else resources.getIdentifier("fragment_container", "id", packageName)

        supportFragmentManager.beginTransaction()
            .replace(R.id.fragmentContainer, fragment)
            .commit()
    }
}