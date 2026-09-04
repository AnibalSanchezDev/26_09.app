package com.example.gastos.ui.screen

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.List
import androidx.compose.material.icons.filled.Star
import androidx.compose.ui.graphics.vector.ImageVector

enum class SeccionApp(val titulo: String, val icono: ImageVector) {
    INICIO("Inicio", Icons.Default.Home),
    GESTION("Gestión", Icons.Default.List),
    RESUMENES("Resúmenes", Icons.Default.DateRange),
    
    CUENTAS("Cuentas", Icons.Default.Star)
}