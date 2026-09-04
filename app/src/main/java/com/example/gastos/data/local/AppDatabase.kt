package com.example.gastos.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import com.example.gastos.data.local.dao.CategoriaDao
import com.example.gastos.data.local.dao.CuentaDao
import com.example.gastos.data.local.dao.MovimientoDao
import com.example.gastos.data.local.entity.CategoriaEntity
import com.example.gastos.data.local.entity.CuentaEntity
import com.example.gastos.data.local.entity.MovimientoEntity
import com.example.gastos.data.local.entity.TipoMovimiento
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Database(
    entities = [CategoriaEntity::class, MovimientoEntity::class, CuentaEntity::class],
    version = 3, // 👈 Incrementado a 2 para reflejar los cambios en las tablas
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {

    abstract fun categoriaDao(): CategoriaDao
    abstract fun movimientoDao(): MovimientoDao
    abstract fun cuentaDao(): CuentaDao // 👈 Añadido el DAO de cuentas

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "gastos_database"
                )
                    .fallbackToDestructiveMigration() // 👈 Borra y recrea la BD si cambia la versión (ideal para desarrollo)
                    .addCallback(AppDatabaseCallback())
                    .build()
                INSTANCE = instance
                instance
            }
        }

        private class AppDatabaseCallback : RoomDatabase.Callback() {
            override fun onCreate(db: SupportSQLiteDatabase) {
                super.onCreate(db)
                INSTANCE?.let { database ->
                    CoroutineScope(Dispatchers.IO).launch {
                        val categoriaDao = database.categoriaDao()
                        val cuentaDao = database.cuentaDao()

                        // 1. Crear una cuenta por defecto para que los movimientos antiguos tengan ID 1
                        cuentaDao.insertarCuenta(
                            CuentaEntity(nombre = "Cuenta Principal", saldoActual = 0.0, esInversion = false)
                        )

                        // 2. Categorías iniciales
                        val categoriasIniciales = listOf(
                            CategoriaEntity(nombre = "Alimentación", tipo = TipoMovimiento.GASTO, colorHex = "#4CAF50"),
                            CategoriaEntity(nombre = "Transporte", tipo = TipoMovimiento.GASTO, colorHex = "#2196F3"),
                            CategoriaEntity(nombre = "Vivienda", tipo = TipoMovimiento.GASTO, colorHex = "#FF9800"),
                            CategoriaEntity(nombre = "Ocio", tipo = TipoMovimiento.GASTO, colorHex = "#E91E63"),
                            CategoriaEntity(nombre = "Nómina", tipo = TipoMovimiento.INGRESO, colorHex = "#8BC34A"),
                            CategoriaEntity(nombre = "Ventas", tipo = TipoMovimiento.INGRESO, colorHex = "#00BCD4"),
                            CategoriaEntity(nombre = "Regalos", tipo = TipoMovimiento.INGRESO, colorHex = "#9C27B0")
                        )
                        categoriaDao.insertarCategorias(categoriasIniciales)
                    }
                }
            }
        }
    }
}