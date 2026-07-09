package com.example.warehouse_legapaket.model

data class BarangModel(
    val resi: String,
    val destination: String,
    val agent: String,
    val weight: String,
    var status: String,
    val date: String,
    val namaBarang: String,
    val pengirim: String,
    val penerima: String,
    var idTrip: String? = null,
    var kendalaPesan: String? = null
)