package com.example.gastos.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "cuentas")
data class CuentaEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0L,
    val nombre: String,
    val saldoActual: Double,
    val esInversion: Boolean = false,
    val capitalInicialInvertido: Double = 0.0,
    val esPrincipal: Boolean = false // 👈 Asegúrate de que esta línea exista
)