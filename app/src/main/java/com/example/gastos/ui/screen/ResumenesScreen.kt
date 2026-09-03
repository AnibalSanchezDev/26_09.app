package com.example.gastos.ui.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.gastos.data.local.entity.CategoriaEntity
import com.example.gastos.data.local.entity.TipoMovimiento
import com.example.gastos.ui.viewmodel.MainViewModel
import com.example.gastos.utils.FormatUtils

data class CategoriaGastoResumen(
    val categoria: CategoriaEntity,
    val total: Double,
    val porcentaje: Float
)

@Composable
fun ResumenesScreen(viewModel: MainViewModel) {
    val state by viewModel.uiState.collectAsState()
    val categoriasGastos by viewModel.categoriasGastos.collectAsState()

    // Filtramos solo movimientos de tipo GASTO del mes actual
    val gastosDelMes = remember(state.movimientos) {
        state.movimientos.filter { it.tipo == TipoMovimiento.GASTO }
    }

    val totalGastos = remember(gastosDelMes) {
        gastosDelMes.sumOf { it.importe }
    }

    // Agrupamos el gasto por cada categoría
    val resumenCategorias = remember(gastosDelMes, categoriasGastos) {
        categoriasGastos.mapNotNull { cat ->
            val totalCat = gastosDelMes
                .filter { it.categoriaId == cat.id }
                .sumOf { it.importe }

            if (totalCat > 0) {
                val pct = if (totalGastos > 0) (totalCat / totalGastos).toFloat() else 0f
                CategoriaGastoResumen(cat, totalCat, pct)
            } else null
        }.sortedByDescending { it.total }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text(
            text = "Desglose de Gastos del Mes",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold
        )

        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("Total Gastado", fontSize = 14.sp)
                    Text(
                        text = FormatUtils.formatearEuros(totalGastos),
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFFC62828)
                    )
                }

                if (resumenCategorias.isNotEmpty()) {
                    // Barra Proporcional Segmentada (Gráfico de Distribución)
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(18.dp)
                            .clip(RoundedCornerShape(9.dp))
                            .background(Color.LightGray.copy(alpha = 0.3f))
                    ) {
                        resumenCategorias.forEach { item ->
                            val colorCat = try {
                                Color(android.graphics.Color.parseColor(item.categoria.colorHex))
                            } catch (e: Exception) {
                                MaterialTheme.colorScheme.primary
                            }
                            Box(
                                modifier = Modifier
                                    .fillMaxHeight()
                                    .weight(item.porcentaje.coerceAtLeast(0.01f))
                                    .background(colorCat)
                            )
                        }
                    }
                }
            }
        }

        if (resumenCategorias.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .weight(1f),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "No hay gastos registrados en este período.",
                    color = Color.Gray
                )
            }
        } else {
            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(resumenCategorias, key = { it.categoria.id }) { item ->
                    val colorCat = try {
                        Color(android.graphics.Color.parseColor(item.categoria.colorHex))
                    } catch (e: Exception) {
                        MaterialTheme.colorScheme.primary
                    }

                    Card(modifier = Modifier.fillMaxWidth()) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(12.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(10.dp)
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(14.dp)
                                        .background(colorCat, CircleShape)
                                )
                                Column {
                                    Text(
                                        text = item.categoria.nombre,
                                        fontWeight = FontWeight.Bold
                                    )
                                    Text(
                                        text = "%.1f%% del total".format(item.porcentaje * 100),
                                        fontSize = 12.sp,
                                        color = Color.Gray
                                    )
                                }
                            }

                            Text(
                                text = FormatUtils.formatearEuros(item.total),
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFFC62828)
                            )
                        }
                    }
                }
            }
        }
    }
}