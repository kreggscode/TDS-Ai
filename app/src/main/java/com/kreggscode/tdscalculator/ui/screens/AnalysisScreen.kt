package com.kreggscode.tdscalculator.ui.screens

import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.kreggscode.tdscalculator.ui.components.*
import com.kreggscode.tdscalculator.ui.theme.*
import com.kreggscode.tdscalculator.ui.viewmodels.TDSViewModel
import java.text.NumberFormat
import java.util.*

@Composable
fun AnalysisScreen(
    viewModel: TDSViewModel,
    modifier: Modifier = Modifier
) {
    val calculationResult by viewModel.calculationResult.collectAsState()
    val scrollState = rememberScrollState()
    val currencyFormat = NumberFormat.getCurrencyInstance(Locale("en", "IN"))
    
    Box(modifier = modifier.fillMaxSize()) {
        GradientBackground()
        
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(scrollState)
                .padding(horizontal = 20.dp)
                .padding(top = 20.dp, bottom = 100.dp)
        ) {
            // Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "AI Analysis",
                        style = MaterialTheme.typography.headlineMedium.copy(
                            fontWeight = FontWeight.Bold
                        ),
                        color = MaterialTheme.colorScheme.onBackground
                    )
                    Text(
                        text = "Smart tax optimization insights",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                
                Box(
                    modifier = Modifier
                        .size(48.dp)
                        .background(
                            Brush.linearGradient(
                                colors = listOf(AITeal, AIIndigo, AIPurple)
                            ),
                            CircleShape
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    PulsingIcon(
                        icon = Icons.Default.Analytics,
                        tint = Color.White,
                        size = 24.dp
                    )
                }
            }
            
            Spacer(modifier = Modifier.height(24.dp))
            
            if (calculationResult != null) {
                val result = calculationResult!!
                
                // Tax Efficiency Score
                val taxEfficiency = calculateTaxEfficiency(result.grossIncome, result.netTax)
                
                GlassmorphicCard {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(
                            text = "Tax Efficiency Score",
                            style = MaterialTheme.typography.titleMedium.copy(
                                fontWeight = FontWeight.SemiBold
                            ),
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        
                        Spacer(modifier = Modifier.height(16.dp))
                        
                        Box(
                            modifier = Modifier.size(120.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            CircularProgressIndicator(
                                progress = taxEfficiency / 100f,
                                modifier = Modifier.fillMaxSize(),
                                strokeWidth = 12.dp,
                                color = when {
                                    taxEfficiency >= 80 -> Emerald
                                    taxEfficiency >= 60 -> Amber
                                    else -> WarningRed
                                },
                                trackColor = MaterialTheme.colorScheme.surfaceVariant
                            )
                            
                            Text(
                                text = "${taxEfficiency.toInt()}%",
                                style = MaterialTheme.typography.headlineMedium.copy(
                                    fontWeight = FontWeight.Bold
                                ),
                                color = MaterialTheme.colorScheme.onSurface
                            )
                        }
                        
                        Spacer(modifier = Modifier.height(12.dp))
                        
                        Text(
                            text = when {
                                taxEfficiency >= 80 -> "Excellent tax planning!"
                                taxEfficiency >= 60 -> "Good, but can improve"
                                else -> "Needs optimization"
                            },
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
                
                Spacer(modifier = Modifier.height(16.dp))
                
                // Key Metrics
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    StatCard(
                        title = "Effective Rate",
                        value = "${String.format("%.2f", (result.netTax / result.grossIncome) * 100)}%",
                        icon = Icons.Default.Percent,
                        modifier = Modifier.weight(1f),
                        gradient = Brush.linearGradient(
                            colors = listOf(Indigo, Purple)
                        )
                    )
                    
                    StatCard(
                        title = "Take Home",
                        value = currencyFormat.format(result.grossIncome - result.netTax),
                        icon = Icons.Default.AccountBalanceWallet,
                        modifier = Modifier.weight(1f),
                        gradient = Brush.linearGradient(
                            colors = listOf(Emerald, SuccessTeal)
                        )
                    )
                }
                
                Spacer(modifier = Modifier.height(16.dp))
                
                // AI Recommendations
                PremiumCard(
                    gradient = Brush.linearGradient(
                        colors = listOf(AITeal.copy(alpha = 0.3f), AIIndigo.copy(alpha = 0.3f))
                    )
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Lightbulb,
                            contentDescription = null,
                            tint = Amber,
                            modifier = Modifier.size(32.dp)
                        )
                        
                        Text(
                            text = "AI Recommendations",
                            style = MaterialTheme.typography.titleMedium.copy(
                                fontWeight = FontWeight.SemiBold
                            ),
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    }
                    
                    Spacer(modifier = Modifier.height(16.dp))
                    
                    val recommendations = generateRecommendations(result.grossIncome, result.deductions80C, result.otherDeductions)
                    
                    recommendations.forEach { recommendation ->
                        RecommendationItem(recommendation)
                        Spacer(modifier = Modifier.height(12.dp))
                    }
                }
                
                Spacer(modifier = Modifier.height(16.dp))
                
                // Tax Breakdown Chart
                PremiumCard(
                    gradient = Brush.linearGradient(
                        colors = listOf(Purple.copy(alpha = 0.3f), Pink.copy(alpha = 0.3f))
                    )
                ) {
                    Text(
                        text = "Income Distribution",
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.SemiBold
                        ),
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    
                    Spacer(modifier = Modifier.height(16.dp))
                    
                    // Gross Income
                    DistributionItem(
                        label = "Gross Income",
                        amount = currencyFormat.format(result.grossIncome),
                        progress = 1f,
                        color = Indigo
                    )
                    
                    Spacer(modifier = Modifier.height(12.dp))
                    
                    // Deductions
                    val totalDeductions = result.deductions80C + result.otherDeductions
                    DistributionItem(
                        label = "Total Deductions",
                        amount = currencyFormat.format(totalDeductions),
                        progress = (totalDeductions / result.grossIncome).toFloat(),
                        color = Emerald
                    )
                    
                    Spacer(modifier = Modifier.height(12.dp))
                    
                    // Tax
                    DistributionItem(
                        label = "Total Tax",
                        amount = currencyFormat.format(result.netTax),
                        progress = (result.netTax / result.grossIncome).toFloat(),
                        color = WarningRed
                    )
                    
                    Spacer(modifier = Modifier.height(12.dp))
                    
                    // Take Home
                    val takeHome = result.grossIncome - result.netTax
                    DistributionItem(
                        label = "Take Home",
                        amount = currencyFormat.format(takeHome),
                        progress = (takeHome / result.grossIncome).toFloat(),
                        color = SuccessTeal
                    )
                }
                
            } else {
                // Empty State
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 60.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Analytics,
                            contentDescription = null,
                            modifier = Modifier.size(80.dp),
                            tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.3f)
                        )
                        
                        Text(
                            text = "No Analysis Available",
                            style = MaterialTheme.typography.titleLarge,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        
                        Text(
                            text = "Calculate your TDS first to see AI-powered insights",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f)
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun RecommendationItem(text: String) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Icon(
            imageVector = Icons.Default.CheckCircle,
            contentDescription = null,
            tint = Emerald,
            modifier = Modifier.size(20.dp)
        )
        
        Text(
            text = text,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurface,
            modifier = Modifier.weight(1f)
        )
    }
}

@Composable
fun DistributionItem(
    label: String,
    amount: String,
    progress: Float,
    color: Color
) {
    Column {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = label,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurface
            )
            Text(
                text = amount,
                style = MaterialTheme.typography.bodyMedium.copy(
                    fontWeight = FontWeight.SemiBold
                ),
                color = color
            )
        }
        
        Spacer(modifier = Modifier.height(8.dp))
        
        AnimatedProgressBar(
            progress = progress,
            gradient = Brush.linearGradient(
                colors = listOf(color, color.copy(alpha = 0.7f))
            )
        )
    }
}

fun calculateTaxEfficiency(grossIncome: Double, netTax: Double): Float {
    if (grossIncome == 0.0) return 0f
    val effectiveRate = (netTax / grossIncome) * 100
    return (100 - effectiveRate).toFloat().coerceIn(0f, 100f)
}

fun generateRecommendations(grossIncome: Double, deductions80C: Double, otherDeductions: Double): List<String> {
    val recommendations = mutableListOf<String>()
    
    if (deductions80C < 150000) {
        recommendations.add("Maximize 80C deductions up to ₹1.5 lakh by investing in PPF, ELSS, or EPF")
    }
    
    if (otherDeductions < 50000) {
        recommendations.add("Consider NPS investment for additional ₹50,000 deduction under 80CCD(1B)")
    }
    
    if (grossIncome > 500000) {
        recommendations.add("Explore health insurance premiums under 80D for up to ₹25,000 deduction")
    }
    
    if (grossIncome > 1000000) {
        recommendations.add("Consider home loan for interest deduction up to ₹2 lakh under Section 24(b)")
    }
    
    recommendations.add("Keep medical bills and receipts for reimbursement claims")
    recommendations.add("Plan investments at the start of financial year for better returns")
    
    return recommendations
}
