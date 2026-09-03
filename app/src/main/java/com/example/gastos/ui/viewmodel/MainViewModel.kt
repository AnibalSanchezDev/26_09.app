package com.example.gastos.ui.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.gastos.data.local.AppDatabase
import com.example.gastos.data.local.entity.CategoriaEntity
import com.example.gastos.data.local.entity.MovimientoEntity
import com.example.gastos.data.local.entity.TipoMovimiento
import com.example.gastos.data.repository.GastosRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

data class UiState(
    val movimientos: List<MovimientoEntity> = emptyList(),
    val totalIngresos: Double = 0.0,
    val totalGastos: Double = 0.0,
    val saldoTotal: Double = 0.0,
    val categoriasGasto: List<CategoriaEntity> = emptyList(),
    val categoriasIngreso: List<CategoriaEntity> = emptyList()
)

class MainViewModel(application: Application) : AndroidViewModel(application) {

    private val repository: GastosRepository

    val uiState: StateFlow<UiState>

    init {
        val db = AppDatabase.getDatabase(application)
        repository = GastosRepository(db.movimientoDao(), db.categoriaDao())

        val movimientosFlow = repository.todosLosMovimientos
        val ingresosFlow = repository.totalIngresos
        val gastosFlow = repository.totalGastos
        val catGastosFlow = repository.obtenerCategoriasPorTipo(TipoMovimiento.GASTO)
        val catIngresosFlow = repository.obtenerCategoriasPorTipo(TipoMovimiento.INGRESO)

        uiState = combine(
            movimientosFlow,
            ingresosFlow,
            gastosFlow,
            catGastosFlow,
            catIngresosFlow
        ) { movs, ing, gast, cGastos, cIngresos ->
            val totalIng = ing ?: 0.0
            val totalGas = gast ?: 0.0
            UiState(
                movimientos = movs,
                totalIngresos = totalIng,
                totalGastos = totalGas,
                saldoTotal = totalIng - totalGas,
                categoriasGasto = cGastos,
                categoriasIngreso = cIngresos
            )
        }.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = UiState()
        )
    }

    fun agregarMovimiento(
        importe: Double,
        tipo: TipoMovimiento,
        descripcion: String?,
        categoriaId: Long
    ) {
        viewModelScope.launch {
            val nuevoMovimiento = MovimientoEntity(
                importe = importe,
                tipo = tipo,
                descripcion = descripcion?.ifBlank { null },
                categoriaId = categoriaId
            )
            repository.insertarMovimiento(nuevoMovimiento)
        }
    }

    fun eliminarMovimiento(movimiento: MovimientoEntity) {
        viewModelScope.launch {
            repository.borrarMovimiento(movimiento)
        }
    }
}