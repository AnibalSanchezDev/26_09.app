package com.example.gastos.data.local.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "movimientos",
    foreignKeys = [
        ForeignKey(
            entity = CategoriaEntity::class,
            parentColumns = ["id"],
            childColumns = ["categoriaId"],
            onDelete = ForeignKey.RESTRICT
        )
    ],
    indices = [Index(value = ["categoriaId"])]
)
data class MovimientoEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val importe: Double, // Permite decimales como 17.54
    val tipo: TipoMovimiento, // GASTO o INGRESO
    val descripcion: String? = null, // Descripción opcional
    val fechaTimestamp: Long = System.currentTimeMillis(), // Fecha guardada en milisegundos UTC
    val categoriaId: Long,
    val usuarioId: String = "default_user" // Preparado para múltiples usuarios
)