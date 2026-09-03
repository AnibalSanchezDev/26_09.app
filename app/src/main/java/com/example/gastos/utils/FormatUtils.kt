package com.example.gastos.utils

import java.text.NumberFormat
import java.util.Locale

object FormatUtils {

    private val euroFormat = NumberFormat.getCurrencyInstance(Locale("es", "ES"))

    /**
     * Convierte un valor numérico a formato moneda en Euros (€).
     * Ejemplo: 1250.5 -> "1.250,50 €"
     */
    fun formatearEuros(importe: Double): String {
        return euroFormat.format(importe)
    }
}