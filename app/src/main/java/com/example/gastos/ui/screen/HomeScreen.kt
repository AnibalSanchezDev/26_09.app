package com.example.gastos.ui.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.List
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.gastos.data.local.entity.CategoriaEntity
import com.example.gastos.data.local.entity.CuentaEntity
import com.example.gastos.data.local.entity.MovimientoEntity
import com.example.gastos.data.local.entity.TipoMovimiento
import com.example.gastos.ui.components.GestorCategoriasDialog
import com.example.gastos.ui.viewmodel.MainViewModel
import com.example.gastos.ui.viewmodel.UiState

@Composable
fun HomeScreen(viewModel: MainViewModel) {
    val state by viewModel.uiState.collectAsState()

    val categoriasGastos by viewModel.categoriasGastos.collectAsState()
    val categoriasIngresos by viewModel.categoriasIngresos.collectAsState()
    val cuentas by viewModel.cuentas.collectAsState()

    val todasLasCategorias = remember(categoriasGastos, categoriasIngresos) {
        categoriasGastos + categoriasIngresos
    }

    var mostrarDialogoMovimiento by remember { mutableStateOf(false) }
    var mostrarDialogoCategoria by remember { mutableStateOf(false) }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Column(
            modifier = Modifier.fillMaxSize()
        ) {
            SelectorMesHeader(
                mes = state.fechaFiltro.mes,
                anio = state.fechaFiltro.anio,
                onMesAnterior = { viewModel.cambiarMes(-1) },
                onMesSiguiente = { viewModel.cambiarMes(1) }
            )

            Spacer(modifier = Modifier.height(12.dp))

            ResumenCard(state)

            Spacer(modifier = Modifier.height(16.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Button(
                    onClick = { mostrarDialogoMovimiento = true },
                    modifier = Modifier.weight(1f)
                ) {
                    Icon(Icons.Default.Add, contentDescription = null)
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Nuevo Registro")
                }

                OutlinedButton(
                    onClick = { mostrarDialogoCategoria = true }
                ) {
                    Icon(Icons.Default.List, contentDescription = null)
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Categorías")
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "Historial de Movimientos",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(8.dp))

            if (state.movimientos.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f),
                    contentAlignment = Alignment.Center
                ) {
                    Text("No hay movimientos en este mes.", color = Color.Gray)
                }
            } else {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(state.movimientos, key = { it.id }) { movimiento ->
                        MovimientoItem(
                            movimiento = movimiento,
                            categorias = todasLasCategorias,
                            onDelete = { viewModel.eliminarMovimiento(movimiento) }
                        )
                    }
                }
            }
        }
    }

    if (mostrarDialogoMovimiento) {
        NuevoMovimientoDialog(
            categoriasGastos = categoriasGastos,
            categoriasIngresos = categoriasIngresos,
            cuentas = cuentas,
            onDismiss = { mostrarDialogoMovimiento = false },
            onConfirm = { importe, tipo, descripcion, categoriaId, cuentaId ->
                viewModel.registrarMovimiento(
                    importe = importe,
                    tipo = tipo,
                    descripcion = descripcion.ifBlank { null },
                    categoriaId = categoriaId,
                    cuentaId = cuentaId
                )
                mostrarDialogoMovimiento = false
            }
        )
    }

    if (mostrarDialogoCategoria) {
        GestorCategoriasDialog(
            categoriasGastos = categoriasGastos,
            categoriasIngresos = categoriasIngresos,
            onDismiss = { mostrarDialogoCategoria = false },
            onGuardar = { categoria ->
                viewModel.guardarCategoria(categoria)
            },
            onBorrar = { categoria ->
                viewModel.borrarCategoria(categoria)
            }
        )
    }
}

@Composable
fun SelectorMesHeader(
    mes: String,
    anio: String,
    onMesAnterior: () -> Unit,
    onMesSiguiente: () -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        IconButton(onClick = onMesAnterior) {
            Icon(Icons.Default.ArrowBack, contentDescription = "Mes anterior")
        }
        Text(
            text = "$mes / $anio",
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold
        )
        IconButton(onClick = onMesSiguiente) {
            Icon(Icons.Default.ArrowForward, contentDescription = "Mes siguiente")
        }
    }
}

@Composable
fun ResumenCard(state: UiState) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text("Saldo del Mes", fontSize = 14.sp)
            Text(
                text = "%.2f €".format(state.saldoTotal),
                fontSize = 28.sp,
                fontWeight = FontWeight.Bold,
                color = if (state.saldoTotal >= 0) Color(0xFF2E7D32) else Color(0xFFC62828)
            )
            Spacer(modifier = Modifier.height(12.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column {
                    Text("Ingresos", fontSize = 12.sp)
                    Text("+%.2f €".format(state.totalIngresos), color = Color(0xFF2E7D32), fontWeight = FontWeight.SemiBold)
                }
                Column {
                    Text("Gastos", fontSize = 12.sp)
                    Text("-%.2f €".format(state.totalGastos), color = Color(0xFFC62828), fontWeight = FontWeight.SemiBold)
                }
            }
        }
    }
}

@Composable
fun MovimientoItem(
    movimiento: MovimientoEntity,
    categorias: List<CategoriaEntity>,
    onDelete: () -> Unit
) {
    val esIngreso = movimiento.tipo == TipoMovimiento.INGRESO

    val categoriaAsociada = remember(movimiento.categoriaId, categorias) {
        categorias.find { it.id == movimiento.categoriaId }
    }

    val colorCategoria = remember(categoriaAsociada) {
        try {
            Color(android.graphics.Color.parseColor(categoriaAsociada?.colorHex ?: "#808080"))
        } catch (e: Exception) {
            Color.Gray
        }
    }

    Card(modifier = Modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = movimiento.descripcion.orEmpty().ifEmpty { if (esIngreso) "Ingreso" else "Gasto" },
                    fontWeight = FontWeight.Bold,
                    style = MaterialTheme.typography.bodyLarge
                )

                Spacer(modifier = Modifier.height(4.dp))

                Surface(
                    shape = CircleShape,
                    color = colorCategoria.copy(alpha = 0.15f),
                    contentColor = colorCategoria
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(8.dp)
                                .background(colorCategoria, CircleShape)
                        )
                        Text(
                            text = categoriaAsociada?.nombre ?: "Sin categoría",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Medium
                        )
                    }
                }
            }

            Text(
                text = "${if (esIngreso) "+" else "-"}%.2f €".format(movimiento.importe),
                color = if (esIngreso) Color(0xFF2E7D32) else Color(0xFFC62828),
                fontWeight = FontWeight.Bold,
                fontSize = 16.sp,
                modifier = Modifier.padding(end = 8.dp)
            )

            IconButton(onClick = onDelete) {
                Icon(Icons.Default.Delete, contentDescription = "Eliminar", tint = Color.Gray)
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NuevoMovimientoDialog(
    categoriasGastos: List<CategoriaEntity>,
    categoriasIngresos: List<CategoriaEntity>,
    cuentas: List<CuentaEntity>,
    onDismiss: () -> Unit,
    onConfirm: (Double, TipoMovimiento, String, Long, Long) -> Unit
) {
    var importeText by remember { mutableStateOf("") }
    var descripcion by remember { mutableStateOf("") }
    var tipoSeleccionado by remember { mutableStateOf(TipoMovimiento.GASTO) }

    val categoriasDisponibles = if (tipoSeleccionado == TipoMovimiento.GASTO) categoriasGastos else categoriasIngresos
    var categoriaSeleccionadaId by remember(tipoSeleccionado, categoriasDisponibles) {
        mutableStateOf(categoriasDisponibles.firstOrNull()?.id ?: 1L)
    }

    // Cuenta principal por defecto o la primera disponible
    val cuentaPorDefecto = remember(cuentas) {
        cuentas.firstOrNull { it.esPrincipal } ?: cuentas.firstOrNull()
    }
    var cuentaSeleccionadaId by remember(cuentaPorDefecto) {
        mutableStateOf(cuentaPorDefecto?.id ?: 1L)
    }

    var menuCategoriaExpandido by remember { mutableStateOf(false) }
    var menuCuentaExpandido by remember { mutableStateOf(false) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Nuevo Registro") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceEvenly) {
                    FilterChip(
                        selected = tipoSeleccionado == TipoMovimiento.GASTO,
                        onClick = { tipoSeleccionado = TipoMovimiento.GASTO },
                        label = { Text("Gasto") }
                    )
                    FilterChip(
                        selected = tipoSeleccionado == TipoMovimiento.INGRESO,
                        onClick = { tipoSeleccionado = TipoMovimiento.INGRESO },
                        label = { Text("Ingreso") }
                    )
                }

                OutlinedTextField(
                    value = importeText,
                    onValueChange = { importeText = it.replace(',', '.') },
                    label = { Text("Importe (€)") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )

                // Selector de Cuenta
                ExposedDropdownMenuBox(
                    expanded = menuCuentaExpandido,
                    onExpandedChange = { menuCuentaExpandido = !menuCuentaExpandido }
                ) {
                    val cuentaActualNombre = cuentas.find { it.id == cuentaSeleccionadaId }?.nombre ?: "Seleccionar Cuenta"
                    OutlinedTextField(
                        value = cuentaActualNombre,
                        onValueChange = {},
                        readOnly = true,
                        label = { Text("Cuenta asociada") },
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = menuCuentaExpandido) },
                        modifier = Modifier
                            .menuAnchor()
                            .fillMaxWidth()
                    )
                    ExposedDropdownMenu(
                        expanded = menuCuentaExpandido,
                        onDismissRequest = { menuCuentaExpandido = false }
                    ) {
                        cuentas.forEach { cuenta ->
                            DropdownMenuItem(
                                text = { Text("${cuenta.nombre} ${if (cuenta.esPrincipal) "★" else ""}") },
                                onClick = {
                                    cuentaSeleccionadaId = cuenta.id
                                    menuCuentaExpandido = false
                                }
                            )
                        }
                    }
                }

                // Selector de Categoría
                ExposedDropdownMenuBox(
                    expanded = menuCategoriaExpandido,
                    onExpandedChange = { menuCategoriaExpandido = !menuCategoriaExpandido }
                ) {
                    val categoriaActualNombre = categoriasDisponibles.find { it.id == categoriaSeleccionadaId }?.nombre ?: "Seleccionar Categoría"
                    OutlinedTextField(
                        value = categoriaActualNombre,
                        onValueChange = {},
                        readOnly = true,
                        label = { Text("Categoría") },
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = menuCategoriaExpandido) },
                        modifier = Modifier
                            .menuAnchor()
                            .fillMaxWidth()
                    )
                    ExposedDropdownMenu(
                        expanded = menuCategoriaExpandido,
                        onDismissRequest = { menuCategoriaExpandido = false }
                    ) {
                        categoriasDisponibles.forEach { cat ->
                            DropdownMenuItem(
                                text = { Text(cat.nombre) },
                                onClick = {
                                    categoriaSeleccionadaId = cat.id
                                    menuCategoriaExpandido = false
                                }
                            )
                        }
                    }
                }

                OutlinedTextField(
                    value = descripcion,
                    onValueChange = { descripcion = it },
                    label = { Text("Descripción (opcional)") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    val importe = importeText.toDoubleOrNull()
                    if (importe != null && importe > 0) {
                        onConfirm(importe, tipoSeleccionado, descripcion, categoriaSeleccionadaId, cuentaSeleccionadaId)
                    }
                }
            ) {
                Text("Guardar")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancelar")
            }
        }
    )
}