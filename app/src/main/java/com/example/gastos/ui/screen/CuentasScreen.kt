package com.example.gastos.ui.screen

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.outlined.Star
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.gastos.data.local.entity.CuentaEntity
import com.example.gastos.ui.viewmodel.MainViewModel

@Composable
fun CuentasScreen(viewModel: MainViewModel) {
    val cuentas by viewModel.cuentas.collectAsState()
    var mostrarDialogoNuevaCuenta by remember { mutableStateOf(false) }
    var mostrarDialogoTransferencia by remember { mutableStateOf(false) }

    var nombreCuenta by remember { mutableStateOf("") }
    var saldoInicialText by remember { mutableStateOf("") }
    var esInversion by remember { mutableStateOf(false) }
    var capitalInvertidoText by remember { mutableStateOf("") }
    var esPrincipalNueva by remember { mutableStateOf(cuentas.isEmpty()) }

    // Cálculos de los totales
    val totalGlobal = cuentas.sumOf { it.saldoActual }
    val totalCorrientes = cuentas.filter { !it.esInversion }.sumOf { it.saldoActual }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(text = "Mis Cuentas", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedButton(
                    onClick = { mostrarDialogoTransferencia = true },
                    enabled = cuentas.size >= 2
                ) {
                    Text("Transferir")
                }
                Button(onClick = { mostrarDialogoNuevaCuenta = true }) {
                    Icon(Icons.Default.Add, contentDescription = null)
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Nueva")
                }
            }
        }

        // Tarjeta de Resumen de Totales
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text("Patrimonio Total (Inversiones + Demás):", fontSize = 13.sp, color = Color.Gray)
                    Text("%.2f €".format(totalGlobal), fontSize = 14.sp, fontWeight = FontWeight.Bold)
                }
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text("Solo Cuentas Corrientes / Demás:", fontSize = 13.sp, color = Color.Gray)
                    Text("%.2f €".format(totalCorrientes), fontSize = 14.sp, fontWeight = FontWeight.Bold, color = Color(0xFF2E7D32))
                }
            }
        }

        if (cuentas.isEmpty()) {
            Box(modifier = Modifier.fillMaxWidth().weight(1f), contentAlignment = Alignment.Center) {
                Text("No hay cuentas registradas.", color = Color.Gray)
            }
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxWidth().weight(1f),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(cuentas, key = { it.id }) { cuenta ->
                    CuentaItem(
                        cuenta = cuenta,
                        onActualizarInversion = { nuevoSaldo ->
                            viewModel.actualizarSaldoInversion(cuenta.id, nuevoSaldo)
                        },
                        onAgregarAportacion = { importe ->
                            viewModel.agregarAportacionInversion(cuenta.id, importe)
                        },
                        onEliminarCuenta = {
                            viewModel.borrarCuenta(cuenta.id)
                        },
                        onMarcarPrincipal = {
                            viewModel.establecerCuentaPrincipal(cuenta.id)
                        }
                    )
                }
            }
        }
    }

    if (mostrarDialogoNuevaCuenta) {
        AlertDialog(
            onDismissRequest = { mostrarDialogoNuevaCuenta = false },
            title = { Text("Añadir nueva cuenta") },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedTextField(
                        value = nombreCuenta,
                        onValueChange = { nombreCuenta = it },
                        label = { Text("Nombre (ej. BBVA, Efectivo)") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )
                    OutlinedTextField(
                        value = saldoInicialText,
                        onValueChange = { saldoInicialText = it.replace(',', '.') },
                        label = { Text("Saldo inicial / actual (€)") },
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                        modifier = Modifier.fillMaxWidth()
                    )
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text("¿Es una cuenta de inversión?")
                        Checkbox(
                            checked = esInversion,
                            onCheckedChange = { esInversion = it }
                        )
                    }
                    if (esInversion) {
                        OutlinedTextField(
                            value = capitalInvertidoText,
                            onValueChange = { capitalInvertidoText = it.replace(',', '.') },
                            label = { Text("Capital inicial aportado (€)") },
                            singleLine = true,
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text("Marcar como principal")
                        Checkbox(
                            checked = esPrincipalNueva,
                            onCheckedChange = { esPrincipalNueva = it }
                        )
                    }
                }
            },
            confirmButton = {
                Button(onClick = {
                    val saldo = saldoInicialText.toDoubleOrNull() ?: 0.0
                    val capital = capitalInvertidoText.toDoubleOrNull() ?: 0.0
                    if (nombreCuenta.isNotBlank()) {
                        viewModel.agregarCuenta(
                            nombre = nombreCuenta,
                            saldoInicial = saldo,
                            esInversion = esInversion,
                            capitalInvertido = capital,
                            esPrincipal = esPrincipalNueva
                        )
                        nombreCuenta = ""
                        saldoInicialText = ""
                        capitalInvertidoText = ""
                        esInversion = false
                        esPrincipalNueva = false
                        mostrarDialogoNuevaCuenta = false
                    }
                }) {
                    Text("Crear")
                }
            },
            dismissButton = {
                TextButton(onClick = { mostrarDialogoNuevaCuenta = false }) {
                    Text("Cancelar")
                }
            }
        )
    }

    if (mostrarDialogoTransferencia) {
        TransferenciaDialog(
            cuentas = cuentas,
            onDismiss = { mostrarDialogoTransferencia = false },
            onConfirmarTransferencia = { origenId, destinoId, importe ->
                viewModel.realizarTransferencia(origenId, destinoId, importe)
            }
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TransferenciaDialog(
    cuentas: List<CuentaEntity>,
    onDismiss: () -> Unit,
    onConfirmarTransferencia: (Long, Long, Double) -> Unit
) {
    // Por defecto seleccionamos la primera cuenta como origen y la segunda (si existe) como destino
    var origenId by remember { mutableStateOf(cuentas.firstOrNull()?.id ?: 0L) }
    var destinoId by remember { mutableStateOf(cuentas.getOrNull(1)?.id ?: cuentas.firstOrNull()?.id ?: 0L) }
    var importeText by remember { mutableStateOf("") }

    // Estados para controlar la apertura de los desplegables
    var expandedOrigen by remember { mutableStateOf(false) }
    var expandedDestino by remember { mutableStateOf(false) }

    val cuentaOrigenNombre = cuentas.find { it.id == origenId }?.nombre ?: "Seleccionar cuenta"
    val cuentaDestinoNombre = cuentas.find { it.id == destinoId }?.nombre ?: "Seleccionar cuenta"

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Transferir entre cuentas") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {

                // 1. Selector de Cuenta Origen
                ExposedDropdownMenuBox(
                    expanded = expandedOrigen,
                    onExpandedChange = { expandedOrigen = !expandedOrigen }
                ) {
                    OutlinedTextField(
                        value = cuentaOrigenNombre,
                        onValueChange = {},
                        readOnly = true,
                        label = { Text("Cuenta Origen (Sale el dinero)") },
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expandedOrigen) },
                        modifier = Modifier.menuAnchor().fillMaxWidth()
                    )
                    ExposedDropdownMenu(
                        expanded = expandedOrigen,
                        onDismissRequest = { expandedOrigen = false }
                    ) {
                        cuentas.forEach { cuenta ->
                            DropdownMenuItem(
                                text = { Text("${cuenta.nombre} (%.2f €)".format(cuenta.saldoActual)) },
                                onClick = {
                                    origenId = cuenta.id
                                    expandedOrigen = false
                                }
                            )
                        }
                    }
                }

                // 2. Selector de Cuenta Destino
                ExposedDropdownMenuBox(
                    expanded = expandedDestino,
                    onExpandedChange = { expandedDestino = !expandedDestino }
                ) {
                    OutlinedTextField(
                        value = cuentaDestinoNombre,
                        onValueChange = {},
                        readOnly = true,
                        label = { Text("Cuenta Destino (Entra el dinero)") },
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expandedDestino) },
                        modifier = Modifier.menuAnchor().fillMaxWidth()
                    )
                    ExposedDropdownMenu(
                        expanded = expandedDestino,
                        onDismissRequest = { expandedDestino = false }
                    ) {
                        cuentas.forEach { cuenta ->
                            DropdownMenuItem(
                                text = { Text("${cuenta.nombre} (%.2f €)".format(cuenta.saldoActual)) },
                                onClick = {
                                    destinoId = cuenta.id
                                    expandedDestino = false
                                }
                            )
                        }
                    }
                }

                // 3. Campo de Importe
                OutlinedTextField(
                    value = importeText,
                    onValueChange = { importeText = it.replace(',', '.') },
                    label = { Text("Importe a transferir (€)") },
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                    modifier = Modifier.fillMaxWidth()
                )

                if (origenId == destinoId && cuentas.size > 1) {
                    Text(
                        text = "⚠️ La cuenta de origen y destino no pueden ser la misma.",
                        fontSize = 12.sp,
                        color = Color(0xFFC62828)
                    )
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    val importe = importeText.toDoubleOrNull() ?: 0.0
                    if (importe > 0 && origenId != destinoId) {
                        onConfirmarTransferencia(origenId, destinoId, importe)
                        onDismiss()
                    }
                },
                enabled = origenId != destinoId && (importeText.toDoubleOrNull() ?: 0.0) > 0
            ) {
                Text("Transferir")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancelar")
            }
        }
    )
}
@Composable
fun CuentaItem(
    cuenta: CuentaEntity,
    onActualizarInversion: (Double) -> Unit,
    onAgregarAportacion: (Double) -> Unit,
    onEliminarCuenta: () -> Unit,
    onMarcarPrincipal: () -> Unit
) {
    var mostrarDialogoActualizar by remember { mutableStateOf(false) }
    var mostrarDialogoAportacion by remember { mutableStateOf(false) }
    var mostrarDialogoBorrar by remember { mutableStateOf(false) }

    var nuevoSaldoText by remember { mutableStateOf(cuenta.saldoActual.toString()) }
    var importeAportacionText by remember { mutableStateOf("") }
    var textoConfirmacionBorrar by remember { mutableStateOf("") }

    val beneficio = cuenta.saldoActual - cuenta.capitalInicialInvertido
    val rentabilidad = if (cuenta.esInversion && cuenta.capitalInicialInvertido > 0) {
        (beneficio / cuenta.capitalInicialInvertido) * 100
    } else {
        0.0
    }

    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = if (cuenta.esPrincipal) CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.25f)) else CardDefaults.cardColors()
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Text(text = cuenta.nombre, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                        if (cuenta.esPrincipal) {
                            Badge(containerColor = MaterialTheme.colorScheme.primary) {
                                Text("Principal", modifier = Modifier.padding(horizontal = 4.dp), fontSize = 10.sp)
                            }
                        }
                    }
                    Text(
                        text = if (cuenta.esInversion) "Cuenta de Inversión" else "Cuenta Corriente",
                        fontSize = 12.sp,
                        color = Color.Gray
                    )
                }
                Row(verticalAlignment = Alignment.CenterVertically) {
                    IconButton(onClick = onMarcarPrincipal) {
                        Icon(
                            imageVector = if (cuenta.esPrincipal) Icons.Filled.Star else Icons.Outlined.Star,
                            contentDescription = "Marcar como principal",
                            tint = if (cuenta.esPrincipal) Color(0xFFFFC107) else Color.Gray
                        )
                    }
                    Text(
                        text = "%.2f €".format(cuenta.saldoActual),
                        fontWeight = FontWeight.Bold,
                        fontSize = 18.sp,
                        color = if (cuenta.saldoActual >= 0) Color(0xFF2E7D32) else Color(0xFFC62828)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    IconButton(onClick = { mostrarDialogoBorrar = true }) {
                        Icon(
                            imageVector = Icons.Default.Delete,
                            contentDescription = "Borrar cuenta",
                            tint = Color.Gray
                        )
                    }
                }
            }

            if (cuenta.esInversion) {
                Spacer(modifier = Modifier.height(8.dp))
                HorizontalDivider(color = Color.LightGray.copy(alpha = 0.5f))
                Spacer(modifier = Modifier.height(8.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text("Total aportado:", fontSize = 13.sp, color = Color.Gray)
                    Text("%.2f €".format(cuenta.capitalInicialInvertido), fontSize = 13.sp, fontWeight = FontWeight.Medium)
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text("Beneficio / Pérdida:", fontSize = 13.sp, color = Color.Gray)
                    Text(
                        text = "${if (beneficio >= 0) "+" else ""}%.2f € (${if (rentabilidad >= 0) "+" else ""}%.2f%%)".format(beneficio, rentabilidad),
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                        color = if (beneficio >= 0) Color(0xFF2E7D32) else Color(0xFFC62828)
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End
                ) {
                    OutlinedButton(onClick = { mostrarDialogoAportacion = true }) {
                        Text("Aportar Dinero")
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    Button(onClick = { mostrarDialogoActualizar = true }) {
                        Text("Actualizar Valor")
                    }
                }
            }
        }
    }

    if (mostrarDialogoActualizar) {
        AlertDialog(
            onDismissRequest = { mostrarDialogoActualizar = false },
            title = { Text("Actualizar valor de mercado") },
            text = {
                OutlinedTextField(
                    value = nuevoSaldoText,
                    onValueChange = { nuevoSaldoText = it.replace(',', '.') },
                    label = { Text("Nuevo valor actual total (€)") },
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                    modifier = Modifier.fillMaxWidth()
                )
            },
            confirmButton = {
                Button(onClick = {
                    val nuevoSaldo = nuevoSaldoText.toDoubleOrNull()
                    if (nuevoSaldo != null) {
                        onActualizarInversion(nuevoSaldo)
                        mostrarDialogoActualizar = false
                    }
                }) {
                    Text("Guardar")
                }
            },
            dismissButton = {
                TextButton(onClick = { mostrarDialogoActualizar = false }) {
                    Text("Cancelar")
                }
            }
        )
    }

    if (mostrarDialogoAportacion) {
        AlertDialog(
            onDismissRequest = { mostrarDialogoAportacion = false },
            title = { Text("Añadir nueva aportación") },
            text = {
                OutlinedTextField(
                    value = importeAportacionText,
                    onValueChange = { importeAportacionText = it.replace(',', '.') },
                    label = { Text("Cantidad aportada (€)") },
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                    modifier = Modifier.fillMaxWidth()
                )
            },
            confirmButton = {
                Button(onClick = {
                    val importe = importeAportacionText.toDoubleOrNull()
                    if (importe != null && importe > 0) {
                        onAgregarAportacion(importe)
                        importeAportacionText = ""
                        mostrarDialogoAportacion = false
                    }
                }) {
                    Text("Añadir")
                }
            },
            dismissButton = {
                TextButton(onClick = { mostrarDialogoAportacion = false }) {
                    Text("Cancelar")
                }
            }
        )
    }

    if (mostrarDialogoBorrar) {
        AlertDialog(
            onDismissRequest = {
                mostrarDialogoBorrar = false
                textoConfirmacionBorrar = ""
            },
            title = { Text("Eliminar cuenta") },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text("Estás a punto de eliminar '${cuenta.nombre}'. Para confirmar, escribe la palabra **ACEPTAR** en mayúsculas:")
                    OutlinedTextField(
                        value = textoConfirmacionBorrar,
                        onValueChange = { textoConfirmacionBorrar = it },
                        label = { Text("Escribe ACEPTAR") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        if (textoConfirmacionBorrar == "ACEPTAR") {
                            onEliminarCuenta()
                            mostrarDialogoBorrar = false
                            textoConfirmacionBorrar = ""
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFC62828)),
                    enabled = textoConfirmacionBorrar == "ACEPTAR"
                ) {
                    Text("Eliminar")
                }
            },
            dismissButton = {
                TextButton(onClick = {
                    mostrarDialogoBorrar = false
                    textoConfirmacionBorrar = ""
                }) {
                    Text("Cancelar")
                }
            }
        )
    }
}