package com.kreggscode.tdscalculator.data.models

data class TDSCalculation(
    val grossIncome: Double = 0.0,
    val deductions80C: Double = 0.0,
    val otherDeductions: Double = 0.0,
    val taxableIncome: Double = 0.0,
    val totalTax: Double = 0.0,
    val cess: Double = 0.0,
    val netTax: Double = 0.0,
    val monthlyTDS: Double = 0.0,
    val taxBreakdown: List<TaxSlabBreakdown> = emptyList()
)

data class TaxSlabBreakdown(
    val slabName: String,
    val income: Double,
    val rate: Double,
    val tax: Double
)

data class ChatMessage(
    val id: String = java.util.UUID.randomUUID().toString(),
    val text: String,
    val isUser: Boolean,
    val timestamp: Long = System.currentTimeMillis()
)

data class TDSInfo(
    val title: String,
    val description: String,
    val details: List<String>,
    val isExpanded: Boolean = false
)

enum class ThemeMode {
    LIGHT, DARK, SYSTEM
}

data class UserPreferences(
    val themeMode: ThemeMode = ThemeMode.SYSTEM,
    val notificationsEnabled: Boolean = true
)
