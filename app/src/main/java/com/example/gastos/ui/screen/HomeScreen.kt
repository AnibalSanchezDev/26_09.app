package com.example.gastos.ui.screen

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.gastos.data.local.entity.MovimientoEntity
import com.example.gastos.data.local.entity.TipoMovimiento
import com.example.gastos.ui.viewmodel.MainViewModel
import com.example.gastos.ui.viewmodel.UiState

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(viewModel: MainViewModel) {
    val state by viewModel.uiState.collectAsState()
    var mostrarDialogo by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Mi Control de Gastos") },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer
                )
            )
        },
        floatingActionButton = {
            FloatingActionButton(onClick = { mostrarDialogo = true }) {
                Icon(Icons.Default.Add, contentDescription = "Añadir movimiento")
            }
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp)
        ) {
            // Tarjeta con resumen de Saldo
            ResumenCard(state)

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "Historial de Movimientos",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(8.dp))

            if (state.movimientos.isEmpty()) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Text("No hay movimientos registrados. Toca '+' para añadir.")
                }
            } else {
                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(state.movimientos, key = { it.id }) { movimiento ->
                        MovimientoItem(
                            movimiento = movimiento,
                            onDelete = { viewModel.eliminarMovimiento(movimiento) }
                        )
                    }
                }
            }
        }
    }

    if (mostrarDialogo) {
        NuevoMovimientoDialog(
            state = state,
            onDismiss = { mostrarDialogo = false },
            onConfirm = { importe, tipo, descripcion, categoriaId ->
                viewModel.agregarMovimiento(importe, tipo, descripcion, categoriaId)
                mostrarDialogo = false
            }
        )
    }
}

@Composable
fun ResumenCard(state: UiState) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text("Saldo Total", fontSize = 14.sp)
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
fun MovimientoItem(movimiento: MovimientoEntity, onDelete: () -> Unit) {
    val esIngreso = movimiento.tipo == TipoMovimiento.INGRESO
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
                    text = movimiento.descripcion ?: if (esIngreso) "Ingreso" else "Gasto",
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = if (esIngreso) "Ingreso" else "Gasto",
                    fontSize = 12.sp,
                    color = Color.Gray
                )
            }
            Text(
                text = "${if (esIngreso) "+" else "-"}%.2f €".format(movimiento.importe),
                color = if (esIngreso) Color(0xFF2E7D32) else Color(0xFFC62828),
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(end = 8.dp)
            )
            IconButton(onClick = onDelete) {
                Icon(Icons.Default.Delete, contentDescription = "Eliminar", tint = Color.Gray)
            }
        }
    }
}

@Composable
fun NuevoMovimientoDialog(
    state: UiState,
    onDismiss: () -> Unit,
    onConfirm: (Double, TipoMovimiento, String, Long) -> Unit
) {
    var importeText by remember { mutableStateOf("") }
    var descripcion by remember { mutableStateOf("") }
    var tipoSeleccionado by remember { mutableStateOf(TipoMovimiento.GASTO) }

    val categoriasActuales = if (tipoSeleccionado == TipoMovimiento.GASTO) state.categoriasGasto else state.categoriasIngreso
    var categoriaSeleccionadaId by remember(tipoSeleccionado) {
        mutableStateOf(categoriasActuales.firstOrNull()?.id ?: 1L)
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Nuevo Registro") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                // Selector de Tipo: Gasto o Ingreso
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
                        onConfirm(importe, tipoSeleccionado, descripcion, categoriaSeleccionadaId)
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