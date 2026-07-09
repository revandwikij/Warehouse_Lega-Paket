package com.example.warehouse_legapaket.ui.barang

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.TextView
import android.widget.EditText
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.warehouse_legapaket.LocalDbHelper
import com.example.warehouse_legapaket.R
import com.example.warehouse_legapaket.ui.profile.ProfileFragment

class BarangFragment : Fragment(R.layout.fragment_barang) {

    private lateinit var btnSemua: TextView
    private lateinit var btnMasuk: TextView
    private lateinit var btnSortir: TextView
    private lateinit var btnTransit: TextView
    private lateinit var btnSelesai: TextView
    private lateinit var etSearch: EditText

    private lateinit var rvBarang: RecyclerView
    private lateinit var barangAdapter: BarangAdapter
    private lateinit var dbHelper: LocalDbHelper
    private var currentFilter: String = "Semua"

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        dbHelper = LocalDbHelper(requireContext())
        etSearch = view.findViewById(R.id.etSearch)

        // fungsi setup RecyclerView
        rvBarang = view.findViewById(R.id.rvBarang)
        setupRecyclerView()

        view.findViewById<View>(R.id.btnProfile).setOnClickListener {
            parentFragmentManager.beginTransaction()
                .replace(R.id.fragmentContainer, ProfileFragment())
                .addToBackStack(null)
                .commit()
        }

        btnSemua = view.findViewById(R.id.btnSemua)
        btnMasuk = view.findViewById(R.id.btnMasuk)
        btnSortir = view.findViewById(R.id.btnSortir)
        btnTransit = view.findViewById(R.id.btnTransit)
        btnSelesai = view.findViewById(R.id.btnSelesai)

        btnSemua.setOnClickListener { currentFilter = "Semua"; selectFilter(btnSemua); filterData() }
        btnMasuk.setOnClickListener { currentFilter = "Masuk"; selectFilter(btnMasuk); filterData() }
        btnSortir.setOnClickListener { currentFilter = "Sortir"; selectFilter(btnSortir); filterData() }
        btnTransit.setOnClickListener { currentFilter = "Transit"; selectFilter(btnTransit); filterData() }
        btnSelesai.setOnClickListener { currentFilter = "Selesai"; selectFilter(btnSelesai); filterData() }

        btnSemua.performClick()

        // Fitur Live Search ketika user mengetik nomor resi
        etSearch.addTextChangedListener { text ->
            filterData(text.toString().trim())
        }

        view.findViewById<View>(R.id.fabAdd).setOnClickListener {
            // Membuka InputResiActivity dari context fragment secara aman
            val intent = Intent(requireContext(), InputResiActivity::class.java)
            startActivity(intent)
        }
    }

    private fun setupRecyclerView() {
        barangAdapter = BarangAdapter(mutableListOf()) { nomorResi ->
            // Ketika item barang diklik, pindah ke DetailBarangActivity membawa nomor resi
            val intent = Intent(requireContext(), DetailBarangActivity::class.java)
            intent.putExtra("EXTRA_RESI", nomorResi)
            startActivity(intent)
        }
        rvBarang.layoutManager = LinearLayoutManager(requireContext())
        rvBarang.adapter = barangAdapter
    }

    // Fungsi filter data JSON terintegrasi dengan Search Bar
    private fun filterData(query: String = "") {
        val masterList = dbHelper.getAllBarang()

        // Filter Berdasarkan Tab Status terlebih dahulu
        var filteredList = if (currentFilter == "Semua") {
            masterList
        } else {
            masterList.filter { it.status.equals(currentFilter, ignoreCase = true) }
        }

        // Jika user sedang mengetik di etSearch, saring lagi berdasarkan nomor resi
        if (query.isNotEmpty()) {
            filteredList = filteredList.filter { it.resi.contains(query, ignoreCase = true) }
        }

        barangAdapter.updateData(filteredList)
    }

    private fun selectFilter(selectedButton: TextView) {
        resetFilter()
        selectedButton.setBackgroundResource(R.drawable.bg_chip_selected)
        selectedButton.setTextColor(resources.getColor(android.R.color.white, null))
    }

    private fun resetFilter() {
        val normalBg = R.drawable.bg_chip
        btnSemua.setBackgroundResource(normalBg)
        btnMasuk.setBackgroundResource(normalBg)
        btnSortir.setBackgroundResource(normalBg)
        btnTransit.setBackgroundResource(normalBg)
        btnSelesai.setBackgroundResource(normalBg)

        val textPrimaryColor = resources.getColor(R.color.text_primary, null)
        btnSemua.setTextColor(textPrimaryColor)
        btnMasuk.setTextColor(textPrimaryColor)
        btnSortir.setTextColor(textPrimaryColor)
        btnTransit.setTextColor(textPrimaryColor)
        btnSelesai.setTextColor(textPrimaryColor)
    }

    override fun onResume() {
        super.onResume()
        filterData(etSearch.text.toString().trim())
    }
}