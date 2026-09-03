package com.example.gastos.data.repository

import com.example.gastos.data.local.dao.CategoriaDao
import com.example.gastos.data.local.dao.MovimientoDao
import com.example.gastos.data.local.entity.CategoriaEntity
import com.example.gastos.data.local.entity.MovimientoEntity
import com.example.gastos.data.local.entity.TipoMovimiento
import com.example.gastos.data.local.model.CategoriaResumen
import kotlinx.coroutines.flow.Flow

class GastoRepository(
    private val movimientoDao: MovimientoDao,
    private val categoriaDao: CategoriaDao
) {

    fun obtenerMovimientosPorMes(mes: String, anio: String): Flow<List<MovimientoEntity>> =
        movimientoDao.obtenerMovimientosPorMes(mes, anio)

    fun obtenerTotalPorTipoYMes(tipo: TipoMovimiento, mes: String, anio: String): Flow<Double?> =
        movimientoDao.obtenerTotalPorTipoYMes(tipo, mes, anio)

    fun obtenerGastosPorCategoriaYMes(mes: String, anio: String): Flow<List<CategoriaResumen>> =
        movimientoDao.obtenerGastosPorCategoriaYMes(mes, anio)

    suspend fun insertarMovimiento(movimiento: MovimientoEntity) {
        movimientoDao.insertarMovimiento(movimiento)
    }

    suspend fun borrarMovimiento(movimiento: MovimientoEntity) {
        movimientoDao.borrarMovimiento(movimiento)
    }

    // Obtener categorías filtradas por tipo (GASTO o INGRESO)
    fun obtenerCategoriasPorTipo(tipo: TipoMovimiento): Flow<List<CategoriaEntity>> {
        return categoriaDao.obtenerPorTipo(tipo)
    }

    // Insertar nueva categoría
    suspend fun insertarCategoria(categoria: CategoriaEntity) {
        categoriaDao.insertar(categoria)
    }

    // Borrar categoría
    suspend fun borrarCategoria(categoria: CategoriaEntity) {
        categoriaDao.borrar(categoria)
    }
}