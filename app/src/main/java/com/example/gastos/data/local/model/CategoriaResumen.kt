package com.example.gastos.data.local.model

data class CategoriaResumen(
    val categoriaId: Long,
    val nombreCategoria: String,
    val colorHex: String,
    val totalGastado: Double
)