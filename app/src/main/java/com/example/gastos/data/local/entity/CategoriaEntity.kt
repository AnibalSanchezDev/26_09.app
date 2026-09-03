package com.example.gastos.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

enum class TipoMovimiento {
    GASTO,
    INGRESO
}

@Entity(tableName = "categorias")
data class CategoriaEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val nombre: String,
    val tipo: TipoMovimiento, // GASTO o INGRESO
    val icono: String = "default",
    val colorHex: String = "#FF0000",
    val usuarioId: String = "default_user" // Preparado para múltiples usuarios
)