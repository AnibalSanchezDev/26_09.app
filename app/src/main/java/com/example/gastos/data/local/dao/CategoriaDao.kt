package com.example.gastos.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.gastos.data.local.entity.CategoriaEntity
import com.example.gastos.data.local.entity.TipoMovimiento
import kotlinx.coroutines.flow.Flow

@Dao
interface CategoriaDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertarCategoria(categoria: CategoriaEntity): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertarCategorias(categorias: List<CategoriaEntity>)

    @Update
    suspend fun actualizarCategoria(categoria: CategoriaEntity)

    @Delete
    suspend fun borrarCategoria(categoria: CategoriaEntity)

    // Obtener categorías filtradas por tipo (GASTO o INGRESO) para un usuario
    @Query("SELECT * FROM categorias WHERE tipo = :tipo AND usuarioId = :usuarioId ORDER BY nombre ASC")
    fun obtenerCategoriasPorTipo(tipo: TipoMovimiento, usuarioId: String = "default_user"): Flow<List<CategoriaEntity>>

    @Query("SELECT * FROM categorias WHERE usuarioId = :usuarioId ORDER BY nombre ASC")
    fun obtenerTodasLasCategorias(usuarioId: String = "default_user"): Flow<List<CategoriaEntity>>

    @Query("SELECT * FROM categorias WHERE tipo = :tipo")
    fun obtenerPorTipo(tipo: TipoMovimiento): Flow<List<CategoriaEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertar(categoria: CategoriaEntity)

    @Delete
    suspend fun borrar(categoria: CategoriaEntity)
}