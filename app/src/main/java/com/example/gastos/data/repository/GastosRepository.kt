package com.example.gastos.data.repository

import com.example.gastos.data.local.dao.CategoriaDao
import com.example.gastos.data.local.dao.MovimientoDao
import com.example.gastos.data.local.entity.CategoriaEntity
import com.example.gastos.data.local.entity.MovimientoEntity
import com.example.gastos.data.local.entity.TipoMovimiento
import kotlinx.coroutines.flow.Flow

class GastosRepository(
    private val movimientoDao: MovimientoDao,
    private val categoriaDao: CategoriaDao
) {
    // --- MOVIMIENTOS ---
    val todosLosMovimientos: Flow<List<MovimientoEntity>> =
        movimientoDao.obtenerTodosLosMovimientos()

    val totalGastos: Flow<Double?> =
        movimientoDao.obtenerTotalPorTipo(TipoMovimiento.GASTO)

    val totalIngresos: Flow<Double?> =
        movimientoDao.obtenerTotalPorTipo(TipoMovimiento.INGRESO)

    suspend fun insertarMovimiento(movimiento: MovimientoEntity) {
        movimientoDao.insertarMovimiento(movimiento)
    }

    suspend fun borrarMovimiento(movimiento: MovimientoEntity) {
        movimientoDao.borrarMovimiento(movimiento)
    }

    // --- CATEGORÍAS ---
    fun obtenerCategoriasPorTipo(tipo: TipoMovimiento): Flow<List<CategoriaEntity>> {
        return categoriaDao.obtenerCategoriasPorTipo(tipo)
    }

    suspend fun insertarCategoria(categoria: CategoriaEntity) {
        categoriaDao.insertarCategoria(categoria)
    }
}