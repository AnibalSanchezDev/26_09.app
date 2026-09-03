package com.example.gastos.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.gastos.data.local.entity.MovimientoEntity
import com.example.gastos.data.local.entity.TipoMovimiento
import kotlinx.coroutines.flow.Flow

@Dao
interface MovimientoDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertarMovimiento(movimiento: MovimientoEntity): Long

    @Update
    suspend fun actualizarMovimiento(movimiento: MovimientoEntity)

    @Delete
    suspend fun borrarMovimiento(movimiento: MovimientoEntity)

    // Lista de movimientos ordenados por fecha descendente (los más recientes primero)
    @Query("SELECT * FROM movimientos WHERE usuarioId = :usuarioId ORDER BY fechaTimestamp DESC")
    fun obtenerTodosLosMovimientos(usuarioId: String = "default_user"): Flow<List<MovimientoEntity>>

    // Obtener el total acumulado de ingresos o gastos
    @Query("SELECT SUM(importe) FROM movimientos WHERE tipo = :tipo AND usuarioId = :usuarioId")
    fun obtenerTotalPorTipo(tipo: TipoMovimiento, usuarioId: String = "default_user"): Flow<Double?>
}