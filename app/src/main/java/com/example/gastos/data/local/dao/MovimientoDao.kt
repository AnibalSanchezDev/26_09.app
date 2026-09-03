package com.example.gastos.data.local.dao

import androidx.room.*
import com.example.gastos.data.local.entity.MovimientoEntity
import com.example.gastos.data.local.entity.TipoMovimiento
import com.example.gastos.data.local.model.CategoriaResumen
import kotlinx.coroutines.flow.Flow

@Dao
interface MovimientoDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertarMovimiento(movimiento: MovimientoEntity)

    @Delete
    suspend fun borrarMovimiento(movimiento: MovimientoEntity)

    // Obtener todos los movimientos ordenados por fecha
    @Query("SELECT * FROM movimientos ORDER BY fechaTimestamp DESC")
    fun obtenerTodosLosMovimientos(): Flow<List<MovimientoEntity>>

    // Filtrar movimientos por mes (01-12) y año (YYYY)
    @Query("""
        SELECT * FROM movimientos 
        WHERE strftime('%m', datetime(fechaTimestamp / 1000, 'unixepoch')) = :mes 
          AND strftime('%Y', datetime(fechaTimestamp / 1000, 'unixepoch')) = :anio
        ORDER BY fechaTimestamp DESC
    """)
    fun obtenerMovimientosPorMes(mes: String, anio: String): Flow<List<MovimientoEntity>>

    // Total acumulado por tipo (GASTO o INGRESO) para un mes y año
    @Query("""
        SELECT SUM(importe) FROM movimientos 
        WHERE tipo = :tipo 
          AND strftime('%m', datetime(fechaTimestamp / 1000, 'unixepoch')) = :mes 
          AND strftime('%Y', datetime(fechaTimestamp / 1000, 'unixepoch')) = :anio
    """)
    fun obtenerTotalPorTipoYMes(tipo: TipoMovimiento, mes: String, anio: String): Flow<Double?>

    // Suma de gastos agrupados por categoría para el gráfico del mes
    @Query("""
        SELECT c.id AS categoriaId, c.nombre AS nombreCategoria, c.colorHex AS colorHex, SUM(m.importe) AS totalGastado
        FROM movimientos m
        INNER JOIN categorias c ON m.categoriaId = c.id
        WHERE m.tipo = 'GASTO' 
          AND strftime('%m', datetime(m.fechaTimestamp / 1000, 'unixepoch')) = :mes 
          AND strftime('%Y', datetime(m.fechaTimestamp / 1000, 'unixepoch')) = :anio
        GROUP BY c.id
    """)
    fun obtenerGastosPorCategoriaYMes(mes: String, anio: String): Flow<List<CategoriaResumen>>
}