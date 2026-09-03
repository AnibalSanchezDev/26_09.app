package com.example.gastos.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.example.gastos.data.local.entity.CategoriaEntity
import com.example.gastos.data.local.entity.TipoMovimiento
import com.example.gastos.utils.TextUtils

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GestorCategoriasDialog(
    categoriasGastos: List<CategoriaEntity>,
    categoriasIngresos: List<CategoriaEntity>,
    onDismiss: () -> Unit,
    onGuardar: (categoria: CategoriaEntity) -> Unit,
    onBorrar: (categoria: CategoriaEntity) -> Unit
) {
    var tipoSeleccionado by remember { mutableStateOf(TipoMovimiento.GASTO) }
    val categoriasActuales = if (tipoSeleccionado == TipoMovimiento.GASTO) categoriasGastos else categoriasIngresos

    var categoriaEnEdicion by remember { mutableStateOf<CategoriaEntity?>(null) }
    var categoriaABorrar by remember { mutableStateOf<CategoriaEntity?>(null) }

    var nombreText by remember { mutableStateOf("") }
    var colorSeleccionado by remember { mutableStateOf("#4CAF50") }
    var mensajeError by remember { mutableStateOf<String?>(null) }

    val coloresDisponibles = listOf(
        "#4CAF50", "#2196F3", "#FF9800", "#E91E63",
        "#9C27B0", "#00BCD4", "#FF5722", "#607D8B"
    )

    fun resetForm() {
        categoriaEnEdicion = null
        nombreText = ""
        colorSeleccionado = "#4CAF50"
        mensajeError = null
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(if (categoriaEnEdicion == null) "Gestionar Categorías" else "Editar Categoría") },
        text = {
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                // Selector Tipo Movimiento
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    FilterChip(
                        selected = tipoSeleccionado == TipoMovimiento.GASTO,
                        onClick = {
                            tipoSeleccionado = TipoMovimiento.GASTO
                            resetForm()
                        },
                        label = { Text("Gastos") }
                    )
                    FilterChip(
                        selected = tipoSeleccionado == TipoMovimiento.INGRESO,
                        onClick = {
                            tipoSeleccionado = TipoMovimiento.INGRESO
                            resetForm()
                        },
                        label = { Text("Ingresos") }
                    )
                }

                // Campo Nombre
                OutlinedTextField(
                    value = nombreText,
                    onValueChange = {
                        nombreText = it
                        mensajeError = null
                    },
                    label = { Text(if (categoriaEnEdicion == null) "Nueva categoría" else "Editar nombre") },
                    isError = mensajeError != null,
                    supportingText = {
                        mensajeError?.let { Text(it, color = MaterialTheme.colorScheme.error) }
                    },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )

                // Selector de Color
                Text("Color:", style = MaterialTheme.typography.labelMedium)
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    coloresDisponibles.forEach { colorHex ->
                        val parsedColor = try {
                            Color(android.graphics.Color.parseColor(colorHex))
                        } catch (e: Exception) {
                            Color.Gray
                        }

                        Box(
                            modifier = Modifier
                                .size(32.dp)
                                .background(parsedColor, CircleShape)
                                .border(
                                    width = if (colorSeleccionado == colorHex) 3.dp else 0.dp,
                                    color = if (colorSeleccionado == colorHex) MaterialTheme.colorScheme.primary else Color.Transparent,
                                    shape = CircleShape
                                )
                                .clickable { colorSeleccionado = colorHex }
                        )
                    }
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Button(
                        onClick = {
                            val nombreLimpio = nombreText.trim()
                            if (nombreLimpio.isBlank()) {
                                mensajeError = "El nombre no puede estar vacío"
                                return@Button
                            }

                            val listaExistente = categoriasActuales.map { it.id to it.nombre }
                            val yaExiste = TextUtils.esNombreDuplicado(
                                nuevoNombre = nombreLimpio,
                                categoriasExistentes = listaExistente,
                                idCategoriaEnEdicion = categoriaEnEdicion?.id
                            )

                            if (yaExiste) {
                                mensajeError = "Ya existe esa categoría"
                            } else {
                                onGuardar(
                                    CategoriaEntity(
                                        id = categoriaEnEdicion?.id ?: 0L,
                                        nombre = nombreLimpio,
                                        tipo = tipoSeleccionado,
                                        colorHex = colorSeleccionado
                                    )
                                )
                                resetForm()
                            }
                        }
                    ) {
                        Text(if (categoriaEnEdicion == null) "Agregar" else "Actualizar")
                    }

                    if (categoriaEnEdicion != null) {
                        TextButton(onClick = { resetForm() }) {
                            Text("Cancelar edición")
                        }
                    }
                }

                HorizontalDivider(modifier = Modifier.padding(vertical = 4.dp))

                // Lista de categorías creadas
                Text("Categorías registradas:", style = MaterialTheme.typography.labelLarge)

                if (categoriasActuales.isEmpty()) {
                    Text(
                        text = "No hay categorías guardadas.",
                        style = MaterialTheme.typography.bodySmall,
                        color = Color.Gray
                    )
                } else {
                    LazyColumn(
                        modifier = Modifier
                            .fillMaxWidth()
                            .heightIn(max = 180.dp),
                        verticalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        items(categoriasActuales, key = { it.id }) { cat ->
                            val colorCat = try {
                                Color(android.graphics.Color.parseColor(cat.colorHex))
                            } catch (e: Exception) {
                                MaterialTheme.colorScheme.primary
                            }

                            Card(
                                modifier = Modifier.fillMaxWidth(),
                                colors = CardDefaults.cardColors(
                                    containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
                                )
                            ) {
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(horizontal = 12.dp, vertical = 8.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                                    ) {
                                        Box(
                                            modifier = Modifier
                                                .size(16.dp)
                                                .background(colorCat, CircleShape)
                                        )
                                        Text(cat.nombre, style = MaterialTheme.typography.bodyMedium)
                                    }

                                    Row {
                                        IconButton(
                                            onClick = {
                                                categoriaEnEdicion = cat
                                                nombreText = cat.nombre
                                                colorSeleccionado = cat.colorHex
                                                mensajeError = null
                                            }
                                        ) {
                                            Icon(
                                                imageVector = Icons.Default.Edit,
                                                contentDescription = "Editar categoría",
                                                modifier = Modifier.size(20.dp)
                                            )
                                        }

                                        IconButton(
                                            onClick = { categoriaABorrar = cat }
                                        ) {
                                            Icon(
                                                imageVector = Icons.Default.Delete,
                                                contentDescription = "Borrar categoría",
                                                tint = MaterialTheme.colorScheme.error,
                                                modifier = Modifier.size(20.dp)
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        },
        confirmButton = {},
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cerrar")
            }
        }
    )

    // Diálogo de confirmación para eliminar
    categoriaABorrar?.let { cat ->
        AlertDialog(
            onDismissRequest = { categoriaABorrar = null },
            title = { Text("Eliminar categoría") },
            text = { Text("¿Deseas eliminar la categoría \"${cat.nombre}\"?") },
            confirmButton = {
                TextButton(
                    onClick = {
                        onBorrar(cat)
                        if (categoriaEnEdicion?.id == cat.id) resetForm()
                        categoriaABorrar = null
                    }
                ) {
                    Text("Eliminar", color = MaterialTheme.colorScheme.error)
                }
            },
            dismissButton = {
                TextButton(onClick = { categoriaABorrar = null }) {
                    Text("Cancelar")
                }
            }
        )
    }
}