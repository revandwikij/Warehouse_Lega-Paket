package com.example.warehouse_legapaket

import android.content.Context
import com.example.warehouse_legapaket.model.BarangModel
import com.example.warehouse_legapaket.model.TripModel
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class LocalDbHelper(context: Context) {

    // SharedPreferences sebagai wadah penyimpanan file lokal
    private val sharedPref = context.getSharedPreferences("WarehousePrefs", Context.MODE_PRIVATE)
    private val gson = Gson()

    // =================================================================
    // LOGIKA UNTUK DATA BARANG
    // =================================================================

    // 1. Ambil semua data barang dari JSON
    fun getAllBarang(): MutableList<BarangModel> {
        val jsonString = sharedPref.getString("KEY_BARANG", null) ?: return mutableListOf()
        val type = object : TypeToken<MutableList<BarangModel>>() {}.type
        return gson.fromJson(jsonString, type)
    }

    // 2. Simpan atau Tambah Barang Baru
    fun saveBarang(barangBaru: BarangModel) {
        val listBarang = getAllBarang()

        // Jika resi sudah ada, hapus yang lama (biar tidak duplikat saat edit/update)
        listBarang.removeAll { it.resi == barangBaru.resi }

        // Tambahkan data yang baru
        listBarang.add(barangBaru)

        // Ubah list menjadi String JSON lalu gembok di SharedPreferences
        val jsonString = gson.toJson(listBarang)
        sharedPref.edit().putString("KEY_BARANG", jsonString).apply()
    }


    // =================================================================
    // LOGIKA UNTUK DATA TRIP / DRIVER
    // =================================================================

    // 1. Ambil semua data trip
    fun getAllTrip(): List<TripModel> {
        val json = sharedPref.getString("KEY_TRIP_LIST", null) ?: return emptyList()
        val type = object : TypeToken<List<TripModel>>() {}.type
        return gson.fromJson(json, type)
    }

    fun saveTrip(trip: TripModel) {
        val currentList = getAllTrip().toMutableList()
        // Jika ID trip sudah ada, timpa yang lama (update), jika belum ada, masukkan baru
        val index = currentList.indexOfFirst { it.idTrip == trip.idTrip }
        if (index != -1) {
            currentList[index] = trip
        } else {
            currentList.add(trip)
        }
        sharedPref.edit().putString("KEY_TRIP_LIST", gson.toJson(currentList)).apply()
    }
}