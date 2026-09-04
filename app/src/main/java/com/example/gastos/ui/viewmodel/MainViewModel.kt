package com.example.gastos.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.gastos.data.backup.BackupData
import com.example.gastos.data.local.entity.CategoriaEntity
import com.example.gastos.data.local.entity.CuentaEntity
import com.example.gastos.data.local.entity.MovimientoEntity
import com.example.gastos.data.local.entity.TipoMovimiento
import com.example.gastos.data.local.model.CategoriaResumen
import com.example.gastos.data.repository.GastoRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.util.Collections.emptyList

data class FechaFiltro(
    val mes: String,  // Formato "01" - "12"
    val anio: String  // Formato "YYYY"
)

data class UiState(
    val fechaFiltro: FechaFiltro = FechaFiltro(
        mes = String.format("%02d", LocalDate.now().monthValue),
        anio = LocalDate.now().year.toString()
    ),
    val totalIngresos: Double = 0.0,
    val totalGastos: Double = 0.0,
    val saldoTotal: Double = 0.0,
    val movimientos: List<MovimientoEntity> = emptyList(),
    val gastosPorCategoria: List<CategoriaResumen> = emptyList(),

)

@OptIn(ExperimentalCoroutinesApi::class)
class MainViewModel(
    private val repository: GastoRepository) : ViewModel() {

    val categoriasGastos: StateFlow<List<CategoriaEntity>> = repository
        .obtenerCategoriasPorTipo(TipoMovimiento.GASTO)
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )
    val categoriasIngresos: StateFlow<List<CategoriaEntity>> = repository
        .obtenerCategoriasPorTipo(TipoMovimiento.INGRESO)
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )
    private val _fechaFiltro = MutableStateFlow(
        FechaFiltro(
            mes = String.format("%02d", LocalDate.now().monthValue),
            anio = LocalDate.now().year.toString()
        )
    )
    val fechaFiltro: StateFlow<FechaFiltro> = _fechaFiltro.asStateFlow()

    val uiState: StateFlow<UiState> = _fechaFiltro.flatMapLatest { filtro ->
        combine(
            repository.obtenerMovimientosPorMes(filtro.mes, filtro.anio),
            repository.obtenerTotalPorTipoYMes(TipoMovimiento.INGRESO, filtro.mes, filtro.anio),
            repository.obtenerTotalPorTipoYMes(TipoMovimiento.GASTO, filtro.mes, filtro.anio),
            repository.obtenerGastosPorCategoriaYMes(filtro.mes, filtro.anio)
        ) { movimientos, ingresos, gastos, categorias ->
            val totalIngresos = ingresos ?: 0.0
            val totalGastos = gastos ?: 0.0
            UiState(
                fechaFiltro = filtro,
                totalIngresos = totalIngresos,
                totalGastos = totalGastos,
                saldoTotal = totalIngresos - totalGastos,
                movimientos = movimientos,
                gastosPorCategoria = categorias
            )
        }
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = UiState()
    )

    fun cambiarMes(deltaMeses: Long) {
        val actual = LocalDate.of(_fechaFiltro.value.anio.toInt(), _fechaFiltro.value.mes.toInt(), 1)
        val nuevaFecha = actual.plusMonths(deltaMeses)
        _fechaFiltro.value = FechaFiltro(
            mes = String.format("%02d", nuevaFecha.monthValue),
            anio = nuevaFecha.year.toString()
        )
    }

    fun agregarMovimiento(movimiento: MovimientoEntity) {
        viewModelScope.launch {
            repository.insertarMovimiento(movimiento)
        }
    }

    fun eliminarMovimiento(movimiento: MovimientoEntity) {
        viewModelScope.launch {
            repository.borrarMovimiento(movimiento)
        }
    }

    // Función para guardar una nueva categoría
    fun guardarCategoria(categoria: CategoriaEntity) {
        viewModelScope.launch {
            repository.insertarCategoria(categoria)
        }
    }

    fun borrarCategoria(categoria: CategoriaEntity) {
        viewModelScope.launch {
            repository.borrarCategoria(categoria)
        }
    }
    fun generarBackupJson(): String {
        val datos = BackupData(
            categorias = categoriasGastos.value + categoriasIngresos.value,
            movimientos = uiState.value.movimientos
        )
        return com.google.gson.Gson().toJson(datos)
    }

    fun restaurarBackupJson(jsonString: String) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val backup = com.google.gson.Gson().fromJson(jsonString, BackupData::class.java)

                // Cambia 'repository' por el nombre real de tu variable
                backup.categorias.forEach { categoria ->
                    repository.insertarCategoria(categoria) // O el nombre de tu función en el DAO
                }

                backup.movimientos.forEach { movimiento ->
                    repository.insertarMovimiento(movimiento) // O el nombre de tu función en el DAO
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }
    val cuentas: StateFlow<List<CuentaEntity>> = repository
        .obtenerCuentas() // 👈 Esto es lo que hace que se use la función del repositorio
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    fun agregarCuenta(
        nombre: String,
        saldoInicial: Double,
        esInversion: Boolean,
        capitalInvertido: Double,
        esPrincipal: Boolean = false // 👈 Añade este parámetro aquí
    ) {
        viewModelScope.launch(Dispatchers.IO) {
            val cuenta = CuentaEntity(
                nombre = nombre,
                saldoActual = saldoInicial,
                esInversion = esInversion,
                capitalInicialInvertido = if (esInversion) capitalInvertido else 0.0,
                esPrincipal = esPrincipal // 👈 Asígnaselo a la entidad
            )

            // Si marcas esta como principal, opcionalmente puedes desmarcar las demás antes:
            if (esPrincipal) {
                repository.quitarTodasPrincipales()
            }

            repository.insertarCuenta(cuenta)
        }
    }
    fun actualizarSaldoInversion(cuentaId: Long, nuevoSaldo: Double) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.actualizarSaldoCuenta(cuentaId, nuevoSaldo) // 👈 Usamos 'repository'
        }
    }

    fun agregarAportacionInversion(cuentaId: Long, importe: Double) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.sumarAportacion(cuentaId, importe)
        }
    }

    fun borrarCuenta(cuentaId: Long) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.borrarCuentaPorId(cuentaId)
        }
    }

    fun registrarMovimiento(
        importe: Double,
        tipo: TipoMovimiento,
        descripcion: String?,
        categoriaId: Long,
        cuentaId: Long
    ) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                repository.insertarMovimiento(
                    MovimientoEntity(
                        importe = importe,
                        tipo = tipo,
                        descripcion = descripcion,
                        fechaTimestamp = System.currentTimeMillis(),
                        categoriaId = categoriaId,
                        cuentaId = cuentaId
                    )
                )

                when (tipo) {
                    TipoMovimiento.GASTO -> repository.restarSaldoCuenta(cuentaId, importe)
                    TipoMovimiento.INGRESO -> repository.sumarSaldoCuenta(cuentaId, importe)
                }
            } catch (e: Exception) {
                // Loguea el error real en tu consola de depuración para identificar la causa exacta
                e.printStackTrace()
            }
        }
    }
    fun establecerCuentaPrincipal(cuentaId: Long) {
        viewModelScope.launch(Dispatchers.IO) {
            // Asegúrate de que pasas por el repositorio si así está diseñada tu arquitectura:
            repository.quitarTodasPrincipales()
            repository.marcarComoPrincipal(cuentaId)
        }
    }
    fun realizarTransferencia(origenId: Long, destinoId: Long, importe: Double) {
        if (origenId == destinoId || importe <= 0) return
        viewModelScope.launch(Dispatchers.IO) {
            try {
                repository.transferirEntreCuentas(origenId, destinoId, importe)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

}