package com.example.gastos.ui.screen

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import com.example.gastos.ui.viewmodel.MainViewModel

enum class SeccionApp(val titulo: String) {
    INICIO("Inicio"),
    GESTION("Gestión"),
    RESUMENES("Resúmenes")
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainContainerScreen(viewModel: MainViewModel) {
    var seccionActual by remember { mutableStateOf(SeccionApp.GESTION) }
    var menuExpandido by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Box {
                        TextButton(onClick = { menuExpandido = true }) {
                            Text(
                                text = "Sección: ${seccionActual.titulo}",
                                style = MaterialTheme.typography.titleLarge
                            )
                            Icon(Icons.Default.ArrowDropDown, contentDescription = "Seleccionar sección")
                        }

                        DropdownMenu(
                            expanded = menuExpandido,
                            onDismissRequest = { menuExpandido = false }
                        ) {
                            SeccionApp.values().forEach { seccion ->
                                DropdownMenuItem(
                                    text = { Text(seccion.titulo) },
                                    onClick = {
                                        seccionActual = seccion
                                        menuExpandido = false
                                    }
                                )
                            }
                        }
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer
                )
            )
        }
    ) { paddingValues ->
        Box(modifier = Modifier.padding(paddingValues)) {
            when (seccionActual) {
                SeccionApp.INICIO -> InicioScreen(viewModel = viewModel)
                SeccionApp.GESTION -> HomeScreen(viewModel = viewModel)
                SeccionApp.RESUMENES -> ResumenesScreen(viewModel = viewModel)
            }
        }
    }
}