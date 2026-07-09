package com.example.warehouse_legapaket.model

data class TripModel(
    val idTrip: String,
    val driver: String,
    val armada: String,
    val destination: String,
    val status: String,
    val listResi: List<String>
)