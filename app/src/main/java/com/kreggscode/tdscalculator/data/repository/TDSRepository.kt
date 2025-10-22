package com.kreggscode.tdscalculator.data.repository

import com.kreggscode.tdscalculator.data.models.*
import javax.inject.Inject
import javax.inject.Singleton
import kotlin.math.max

@Singleton
class TDSRepository @Inject constructor() {
    
    fun calculateTDS(
        grossIncome: Double,
        deductions80C: Double,
        otherDeductions: Double
    ): TDSCalculation {
        val totalDeductions = deductions80C + otherDeductions
        val taxableIncome = max(0.0, grossIncome - totalDeductions)
        
        val taxBreakdown = calculateTaxBreakdown(taxableIncome)
        val totalTax = taxBreakdown.sumOf { it.tax }
        val cess = totalTax * 0.04 // 4% cess
        val netTax = totalTax + cess
        val monthlyTDS = netTax / 12
        
        return TDSCalculation(
            grossIncome = grossIncome,
            deductions80C = deductions80C,
            otherDeductions = otherDeductions,
            taxableIncome = taxableIncome,
            totalTax = totalTax,
            cess = cess,
            netTax = netTax,
            monthlyTDS = monthlyTDS,
            taxBreakdown = taxBreakdown
        )
    }
    
    private fun calculateTaxBreakdown(taxableIncome: Double): List<TaxSlabBreakdown> {
        val breakdown = mutableListOf<TaxSlabBreakdown>()
        var remainingIncome = taxableIncome
        
        // New Tax Regime Slabs (FY 2023-24)
        val slabs = listOf(
            Triple("Up to ₹3,00,000", 300000.0, 0.0),
            Triple("₹3,00,001 - ₹6,00,000", 300000.0, 0.05),
            Triple("₹6,00,001 - ₹9,00,000", 300000.0, 0.10),
            Triple("₹9,00,001 - ₹12,00,000", 300000.0, 0.15),
            Triple("₹12,00,001 - ₹15,00,000", 300000.0, 0.20),
            Triple("Above ₹15,00,000", Double.MAX_VALUE, 0.30)
        )
        
        for ((slabName, slabLimit, rate) in slabs) {
            if (remainingIncome <= 0) break
            
            val incomeInSlab = minOf(remainingIncome, slabLimit)
            val tax = incomeInSlab * rate
            
            if (incomeInSlab > 0) {
                breakdown.add(
                    TaxSlabBreakdown(
                        slabName = slabName,
                        income = incomeInSlab,
                        rate = rate,
                        tax = tax
                    )
                )
            }
            
            remainingIncome -= incomeInSlab
        }
        
        return breakdown
    }
    
    fun getTDSInformation(): List<TDSInfo> {
        return listOf(
            TDSInfo(
                title = "What is TDS?",
                description = "Tax Deducted at Source - A mechanism to collect income tax",
                details = listOf(
                    "TDS is tax collected by the government at the source of income",
                    "Employers deduct TDS from salary before paying employees",
                    "Helps in regular tax collection throughout the year",
                    "Reduces tax evasion and ensures timely revenue for government"
                )
            ),
            TDSInfo(
                title = "Income Tax Slabs (New Regime)",
                description = "Current tax rates for FY 2023-24",
                details = listOf(
                    "Up to ₹3,00,000: Nil",
                    "₹3,00,001 - ₹6,00,000: 5%",
                    "₹6,00,001 - ₹9,00,000: 10%",
                    "₹9,00,001 - ₹12,00,000: 15%",
                    "₹12,00,001 - ₹15,00,000: 20%",
                    "Above ₹15,00,000: 30%",
                    "Plus 4% Health & Education Cess on total tax"
                )
            ),
            TDSInfo(
                title = "Section 80C Deductions",
                description = "Tax-saving investments up to ₹1.5 lakh",
                details = listOf(
                    "Public Provident Fund (PPF)",
                    "Employee Provident Fund (EPF)",
                    "Equity Linked Savings Scheme (ELSS)",
                    "National Savings Certificate (NSC)",
                    "Life Insurance Premium",
                    "5-year Fixed Deposits",
                    "Principal repayment of Home Loan",
                    "Tuition fees for children"
                )
            ),
            TDSInfo(
                title = "Other Deductions",
                description = "Additional tax-saving options",
                details = listOf(
                    "80D: Health Insurance Premium (up to ₹25,000)",
                    "80E: Education Loan Interest",
                    "80G: Donations to Charitable Institutions",
                    "80TTA: Interest on Savings Account (up to ₹10,000)",
                    "24(b): Home Loan Interest (up to ₹2,00,000)",
                    "80CCD(1B): NPS contribution (up to ₹50,000)"
                )
            ),
            TDSInfo(
                title = "How to Save Tax?",
                description = "Smart tax planning strategies",
                details = listOf(
                    "Maximize 80C deductions (₹1.5 lakh)",
                    "Invest in NPS for additional ₹50,000 deduction",
                    "Take health insurance for family members",
                    "Claim HRA if you're paying rent",
                    "Keep medical bills and receipts",
                    "Plan investments at the start of financial year",
                    "Consider tax-saving fixed deposits",
                    "Donate to eligible charitable institutions"
                )
            )
        )
    }
    
    fun getAIResponse(userMessage: String): String {
        // Simple rule-based AI responses
        val lowerMessage = userMessage.lowercase()
        
        return when {
            lowerMessage.contains("tds") && lowerMessage.contains("what") -> {
                "TDS (Tax Deducted at Source) is a mechanism where tax is collected at the source of income. Your employer deducts TDS from your salary based on your income and investments before paying you."
            }
            lowerMessage.contains("save") && lowerMessage.contains("tax") -> {
                "You can save tax by:\n1. Investing in 80C options (up to ₹1.5L)\n2. Additional NPS investment (₹50K)\n3. Health insurance premiums\n4. Home loan interest\n5. Education loan interest\n\nWould you like details on any specific option?"
            }
            lowerMessage.contains("80c") -> {
                "Section 80C allows deductions up to ₹1.5 lakh. Popular options include:\n• PPF (Public Provident Fund)\n• ELSS Mutual Funds\n• EPF contributions\n• Life Insurance premiums\n• 5-year Fixed Deposits\n• NSC (National Savings Certificate)"
            }
            lowerMessage.contains("slab") || lowerMessage.contains("rate") -> {
                "Current tax slabs (New Regime):\n• Up to ₹3L: 0%\n• ₹3L-₹6L: 5%\n• ₹6L-₹9L: 10%\n• ₹9L-₹12L: 15%\n• ₹12L-₹15L: 20%\n• Above ₹15L: 30%\n\nPlus 4% cess on total tax."
            }
            lowerMessage.contains("calculate") -> {
                "I can help you calculate your TDS! Please go to the Calculator tab and enter:\n1. Your annual gross income\n2. Deductions under 80C\n3. Other deductions\n\nI'll show you the exact tax breakdown and monthly TDS amount."
            }
            lowerMessage.contains("nps") -> {
                "NPS (National Pension System) offers:\n• Additional ₹50,000 deduction under 80CCD(1B)\n• This is over and above the ₹1.5L limit of 80C\n• Long-term retirement savings\n• Market-linked returns\n• Tax benefits on maturity"
            }
            lowerMessage.contains("hra") -> {
                "HRA (House Rent Allowance) exemption is the minimum of:\n1. Actual HRA received\n2. 50% of salary (metro) or 40% (non-metro)\n3. Rent paid minus 10% of salary\n\nYou need rent receipts to claim this exemption."
            }
            lowerMessage.contains("hello") || lowerMessage.contains("hi") -> {
                "Hello! I'm your AI TDS assistant. I can help you with:\n• Understanding TDS and tax slabs\n• Tax-saving investment options\n• Calculating your TDS\n• Optimizing your tax liability\n\nWhat would you like to know?"
            }
            lowerMessage.contains("thank") -> {
                "You're welcome! Feel free to ask if you have more questions about TDS or tax planning. I'm here to help! 😊"
            }
            else -> {
                "I understand you're asking about: \"$userMessage\"\n\nI can help you with:\n• TDS calculations and tax slabs\n• Section 80C and other deductions\n• Tax-saving investment options\n• HRA and other exemptions\n\nCould you please be more specific about what you'd like to know?"
            }
        }
    }
}
