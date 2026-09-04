package com.example.gastos.data.repository

import com.example.gastos.data.local.dao.CategoriaDao
import com.example.gastos.data.local.dao.CuentaDao
import com.example.gastos.data.local.dao.MovimientoDao
import com.example.gastos.data.local.entity.CategoriaEntity
import com.example.gastos.data.local.entity.CuentaEntity
import com.example.gastos.data.local.entity.MovimientoEntity
import com.example.gastos.data.local.entity.TipoMovimiento
import com.example.gastos.data.local.model.CategoriaResumen
import kotlinx.coroutines.flow.Flow

class GastoRepository(
    private val categoriaDao: CategoriaDao,
    private val movimientoDao: MovimientoDao,
    private val cuentaDao: CuentaDao // 👈 Ya lo pasas correctamente desde el MainActivity
) {

    // --- CATEGORÍAS ---
    fun obtenerCategoriasPorTipo(tipo: TipoMovimiento): Flow<List<CategoriaEntity>> =
        categoriaDao.obtenerCategoriasPorTipo(tipo)

    suspend fun insertarCategoria(categoria: CategoriaEntity) =
        categoriaDao.insertarCategoria(categoria)

    suspend fun borrarCategoria(categoria: CategoriaEntity) =
        categoriaDao.borrarCategoria(categoria)

    // --- MOVIMIENTOS ---
    fun obtenerMovimientosPorMes(mes: String, anio: String): Flow<List<MovimientoEntity>> =
        movimientoDao.obtenerMovimientosPorMes(mes, anio)

    fun obtenerTotalPorTipoYMes(tipo: TipoMovimiento, mes: String, anio: String): Flow<Double?> =
        movimientoDao.obtenerTotalPorTipoYMes(tipo, mes, anio)

    fun obtenerGastosPorCategoriaYMes(mes: String, anio: String): Flow<List<CategoriaResumen>> =
        movimientoDao.obtenerGastosPorCategoriaYMes(mes, anio)

    suspend fun insertarMovimiento(movimiento: MovimientoEntity) =
        movimientoDao.insertarMovimiento(movimiento)

    suspend fun borrarMovimiento(movimiento: MovimientoEntity) =
        movimientoDao.borrarMovimiento(movimiento)

    // --- CUENTAS (¡Asegúrate de tener esto aquí!) ---
    fun obtenerCuentas(): Flow<List<CuentaEntity>> =
        cuentaDao.obtenerCuentas() // 👈 Esto elimina el aviso de que no se usa

    suspend fun insertarCuenta(cuenta: CuentaEntity) =
        cuentaDao.insertarCuenta(cuenta)

    suspend fun actualizarSaldoCuenta(cuentaId: Long, nuevoSaldo: Double) =
        cuentaDao.actualizarSaldoCuenta(cuentaId, nuevoSaldo)

    suspend fun sumarAportacion(cuentaId: Long, importe: Double) =
        cuentaDao.sumarAportacion(cuentaId, importe)

    suspend fun borrarCuentaPorId(cuentaId: Long) =
        cuentaDao.borrarCuentaPorId(cuentaId)

    suspend fun restarSaldoCuenta(cuentaId: Long, importe: Double) =
        cuentaDao.restarSaldoCuenta(cuentaId, importe)

    suspend fun sumarSaldoCuenta(cuentaId: Long, importe: Double) =
        cuentaDao.sumarSaldoCuenta(cuentaId, importe)

    suspend fun establecerCuentaPrincipal(cuentaId: Long) {
        cuentaDao.quitarTodasPrincipales()
        cuentaDao.marcarComoPrincipal(cuentaId)
    }
    suspend fun quitarTodasPrincipales() {
        cuentaDao.quitarTodasPrincipales() // Asegúrate de que tu variable DAO interna se llame cuentaDao
    }
    suspend fun marcarComoPrincipal(cuentaId: Long) {
        cuentaDao.marcarComoPrincipal(cuentaId)
    }
    suspend fun transferirEntreCuentas(origenId: Long, destinoId: Long, importe: Double) {
        cuentaDao.transferirEntreCuentas(origenId, destinoId, importe)
    }

}