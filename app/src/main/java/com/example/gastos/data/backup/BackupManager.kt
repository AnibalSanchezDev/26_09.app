package com.example.gastos.data.backup

import com.example.gastos.data.local.entity.CategoriaEntity
import com.example.gastos.data.local.entity.MovimientoEntity

data class BackupData(
    val version: Int = 1,
    val timestamp: Long = System.currentTimeMillis(),
    val categorias: List<CategoriaEntity>,
    val movimientos: List<MovimientoEntity>
)