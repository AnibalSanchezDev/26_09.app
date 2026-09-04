package com.example.gastos.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Update
import com.example.gastos.data.local.entity.CategoriaEntity
import com.example.gastos.data.local.entity.CuentaEntity
import com.example.gastos.data.local.entity.TipoMovimiento
import kotlinx.coroutines.flow.Flow
@Dao
interface CuentaDao {

    @Query("SELECT * FROM cuentas")
    fun obtenerCuentas(): Flow<List<CuentaEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertarCuenta(cuenta: CuentaEntity)

    @Query("UPDATE cuentas SET saldoActual = :nuevoSaldo WHERE id = :cuentaId")
    suspend fun actualizarSaldoCuenta(cuentaId: Long, nuevoSaldo: Double)

    @Query("UPDATE cuentas SET saldoActual = saldoActual + :importe WHERE id = :cuentaId")
    suspend fun sumarSaldo(cuentaId: Long, importe: Double)

    @Query("UPDATE cuentas SET saldoActual = saldoActual - :importe WHERE id = :cuentaId")
    suspend fun restarSaldo(cuentaId: Long, importe: Double)

    @Query("UPDATE cuentas SET capitalInicialInvertido = capitalInicialInvertido + :importe, saldoActual = saldoActual + :importe WHERE id = :cuentaId")
    suspend fun sumarAportacion(cuentaId: Long, importe: Double)

    @Query("DELETE FROM cuentas WHERE id = :cuentaId")
    suspend fun borrarCuentaPorId(cuentaId: Long)

    @Query("UPDATE cuentas SET esPrincipal = 0")
    suspend fun desmarcarTodasPrincipales()

    @Query("UPDATE cuentas SET esPrincipal = 1 WHERE id = :cuentaId")
    suspend fun marcarComoPrincipal(cuentaId: Long)

    @Query("UPDATE cuentas SET saldoActual = saldoActual - :importe WHERE id = :cuentaId")
    suspend fun restarSaldoCuenta(cuentaId: Long, importe: Double)

    @Query("UPDATE cuentas SET saldoActual = saldoActual + :importe WHERE id = :cuentaId")
    suspend fun sumarSaldoCuenta(cuentaId: Long, importe: Double)

    @Query("UPDATE cuentas SET esPrincipal = 0")
    suspend fun quitarTodasPrincipales()

    @Transaction
    suspend fun transferirEntreCuentas(origenId: Long, destinoId: Long, importe: Double) {
        restarSaldoCuenta(origenId, importe)
        sumarSaldoCuenta(destinoId, importe)
    }

}