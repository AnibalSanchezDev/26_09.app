package com.example.gastos.ui.screen

import androidx.compose.animation.Crossfade
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import com.example.gastos.ui.viewmodel.MainViewModel

// ❌ NO pongas aquí "enum class SeccionApp"
// Simplemente usa el enum que ya existe en SeccionApp.kt

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainContainerScreen(viewModel: MainViewModel) {
    var seccionActual by remember { mutableStateOf(SeccionApp.GESTION) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = seccionActual.titulo,
                        style = MaterialTheme.typography.titleLarge
                    )
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    titleContentColor = MaterialTheme.colorScheme.onPrimaryContainer
                )
            )
        },
        bottomBar = {
            NavigationBar(
                containerColor = MaterialTheme.colorScheme.surfaceContainer
            ) {
                SeccionApp.values().forEach { seccion ->
                    NavigationBarItem(
                        selected = seccionActual == seccion,
                        onClick = { seccionActual = seccion },
                        icon = {
                            Icon(
                                imageVector = seccion.icono,
                                contentDescription = seccion.titulo
                            )
                        },
                        label = { Text(seccion.titulo) }
                    )
                }
            }
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues) // 👈 Relleno automático para no tapar contenido con las barras
        ) {
            Crossfade(targetState = seccionActual, label = "SeccionAnimation") { targetSeccion ->
                when (targetSeccion) {
                    SeccionApp.INICIO -> InicioScreen(viewModel = viewModel)
                    SeccionApp.GESTION -> HomeScreen(viewModel = viewModel)
                    SeccionApp.RESUMENES -> ResumenesScreen(viewModel = viewModel)
                }
            }
        }
    }
}