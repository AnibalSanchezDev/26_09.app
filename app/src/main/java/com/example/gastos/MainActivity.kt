package com.example.gastos

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.gastos.data.local.AppDatabase
import com.example.gastos.data.repository.GastoRepository
import com.example.gastos.ui.screen.MainContainerScreen
import com.example.gastos.ui.viewmodel.MainViewModel

class MainActivity : ComponentActivity() {

    private val viewModel: MainViewModel by viewModels {
        object : ViewModelProvider.Factory {
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                // 1. Obtén la instancia de AppDatabase
                val database = AppDatabase.getDatabase(applicationContext)

                // 2. Crea el repositorio pasándole movimientoDao
                val repository = GastoRepository(
                    categoriaDao = database.categoriaDao(),
                    movimientoDao = database.movimientoDao(),
                    cuentaDao = database.cuentaDao() // 👈 Añade esto aquí
                )

                // 3. Retornamos la instancia de MainViewModel
                return MainViewModel(repository) as T
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MainContainerScreen(viewModel = viewModel)
        }
    }
}