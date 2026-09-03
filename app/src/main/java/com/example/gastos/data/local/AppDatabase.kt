package com.example.gastos.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import com.example.gastos.data.local.dao.CategoriaDao
import com.example.gastos.data.local.dao.MovimientoDao
import com.example.gastos.data.local.entity.CategoriaEntity
import com.example.gastos.data.local.entity.MovimientoEntity
import com.example.gastos.data.local.entity.TipoMovimiento
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Database(
    entities = [CategoriaEntity::class, MovimientoEntity::class],
    version = 1,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {

    abstract fun categoriaDao(): CategoriaDao
    abstract fun movimientoDao(): MovimientoDao

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
                    .addCallback(AppDatabaseCallback()) // Precarga categorías por defecto al instalar
                    .build()
                INSTANCE = instance
                instance
            }
        }

        // Inserta categorías iniciales por defecto la primera vez que se crea la base de datos
        private class AppDatabaseCallback : RoomDatabase.Callback() {
            override fun onCreate(db: SupportSQLiteDatabase) {
                super.onCreate(db)
                INSTANCE?.let { database ->
                    CoroutineScope(Dispatchers.IO).launch {
                        val categoriaDao = database.categoriaDao()
                        val categoriasIniciales = listOf(
                            // Categorías de Gastos
                            CategoriaEntity(nombre = "Alimentación", tipo = TipoMovimiento.GASTO, colorHex = "#4CAF50"),
                            CategoriaEntity(nombre = "Transporte", tipo = TipoMovimiento.GASTO, colorHex = "#2196F3"),
                            CategoriaEntity(nombre = "Vivienda", tipo = TipoMovimiento.GASTO, colorHex = "#FF9800"),
                            CategoriaEntity(nombre = "Ocio", tipo = TipoMovimiento.GASTO, colorHex = "#E91E63"),

                            // Categorías de Ingresos
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