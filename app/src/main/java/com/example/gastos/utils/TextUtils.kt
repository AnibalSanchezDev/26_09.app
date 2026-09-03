package com.example.gastos.utils

import java.text.Normalizer

object TextUtils {

    /**
     * Normaliza un texto eliminando tildes/acentos, caracteres especiales
     * y convirtiéndolo a minúsculas sin espacios innecesarios.
     * Ejemplo: "Alimentación " -> "alimentacion"
     */
    fun normalizar(texto: String): String {
        if (texto.isBlank()) return ""
        val regex = "\\p{InCombiningDiacriticalMarks}+".toRegex()
        val temp = Normalizer.normalize(texto, Normalizer.Form.NFD)
        return regex.replace(temp, "").lowercase().trim()
    }

    /**
     * Comprueba si un nombre de categoría ya existe en una lista dada,
     * ignorando mayúsculas, minúsculas y tildes.
     *
     * @param nuevoNombre El nombre ingresado por el usuario.
     * @param categoriasExistentes Lista de nombres de categorías a comparar.
     * @param idCategoriaEnEdicion ID de la categoría que se está editando (para no colisionar consigo misma).
     */
    fun esNombreDuplicado(
        nuevoNombre: String,
        categoriasExistentes: List<Pair<Long, String>>, // Lista de pares (id, nombre)
        idCategoriaEnEdicion: Long? = null
    ): Boolean {
        val nombreNormalizado = normalizar(nuevoNombre)
        return categoriasExistentes.any { (id, nombre) ->
            val esOtraCategoria = idCategoriaEnEdicion == null || id != idCategoriaEnEdicion
            esOtraCategoria && normalizar(nombre) == nombreNormalizado
        }
    }
}

/**
 * Extensión opcional para llamar a la normalización de forma más fluida:
 * "Alimentación".normalizar()
 */
fun String.normalizar(): String = TextUtils.normalizar(this)