package com.example.gastos.ui.screen

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.Crossfade
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import com.example.gastos.ui.viewmodel.MainViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainContainerScreen(viewModel: MainViewModel) {
    var seccionActual by remember { mutableStateOf(SeccionApp.GESTION) }
    var menuOpcionesExpandido by remember { mutableStateOf(false) }
    val context = LocalContext.current

    // 1. Lanzador para crear/guardar el archivo de exportación JSON
    val exportarLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.CreateDocument("application/json")
    ) { uri ->
        uri?.let { archivoUri ->
            val jsonContent = viewModel.generarBackupJson()
            context.contentResolver.openOutputStream(archivoUri)?.use { outputStream ->
                outputStream.write(jsonContent.toByteArray())
            }
        }
    }

    // 2. Lanzador para seleccionar el archivo JSON que deseas importar
    val importarLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri ->
        uri?.let { archivoUri ->
            try {
                context.contentResolver.openInputStream(archivoUri)?.use { inputStream ->
                    val jsonContent = inputStream.bufferedReader().use { it.readText() }
                    viewModel.restaurarBackupJson(jsonContent)
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = seccionActual.titulo,
                        style = MaterialTheme.typography.titleLarge
                    )
                },
                actions = {
                    Box {
                        IconButton(onClick = { menuOpcionesExpandido = true }) {
                            Icon(Icons.Default.MoreVert, contentDescription = "Opciones de datos")
                        }
                        DropdownMenu(
                            expanded = menuOpcionesExpandido,
                            onDismissRequest = { menuOpcionesExpandido = false }
                        ) {
                            DropdownMenuItem(
                                text = { Text("Exportar datos") },
                                onClick = {
                                    menuOpcionesExpandido = false
                                    exportarLauncher.launch("gastos_backup.json")
                                }
                            )
                            DropdownMenuItem(
                                text = { Text("Importar datos") },
                                onClick = {
                                    menuOpcionesExpandido = false
                                    importarLauncher.launch("application/json")
                                }
                            )
                        }
                    }
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
                        icon = { Icon(seccion.icono, contentDescription = seccion.titulo) },
                        label = { Text(seccion.titulo) }
                    )
                }
            }
        }
    ) { paddingValues ->
        Box(modifier = Modifier.padding(paddingValues)) {
            Crossfade(targetState = seccionActual, label = "SeccionAnimation") { targetSeccion ->
                when (targetSeccion) {
                    SeccionApp.INICIO -> InicioScreen(viewModel = viewModel)
                    SeccionApp.GESTION -> HomeScreen(viewModel = viewModel)
                    SeccionApp.RESUMENES -> ResumenesScreen(viewModel = viewModel)
                    SeccionApp.CUENTAS -> CuentasScreen(viewModel = viewModel)
                }
            }
        }
    }
}