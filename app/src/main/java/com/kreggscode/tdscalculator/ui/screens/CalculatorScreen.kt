package com.kreggscode.tdscalculator.ui.screens

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.kreggscode.tdscalculator.data.models.TDSCalculation
import com.kreggscode.tdscalculator.ui.components.*
import com.kreggscode.tdscalculator.ui.theme.*
import com.kreggscode.tdscalculator.ui.viewmodels.TDSViewModel
import java.text.NumberFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CalculatorScreen(
    viewModel: TDSViewModel,
    modifier: Modifier = Modifier
) {
    val grossIncome by viewModel.grossIncome.collectAsState()
    val deductions80C by viewModel.deductions80C.collectAsState()
    val otherDeductions by viewModel.otherDeductions.collectAsState()
    val calculationResult by viewModel.calculationResult.collectAsState()
    val isCalculating by viewModel.isCalculating.collectAsState()
    
    val scrollState = rememberScrollState()
    
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
                        text = "TDS Calculator",
                        style = MaterialTheme.typography.headlineMedium.copy(
                            fontWeight = FontWeight.Bold
                        ),
                        color = MaterialTheme.colorScheme.onBackground
                    )
                    Text(
                        text = "Calculate your tax liability",
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
                        icon = Icons.Default.Calculate,
                        tint = Color.White,
                        size = 24.dp
                    )
                }
            }
            
            Spacer(modifier = Modifier.height(24.dp))
            
            // Input Fields
            PremiumCard(
                gradient = Brush.linearGradient(
                    colors = listOf(Indigo.copy(alpha = 0.5f), Purple.copy(alpha = 0.5f))
                )
            ) {
                Text(
                    text = "Income Details",
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.SemiBold
                    ),
                    color = MaterialTheme.colorScheme.onSurface
                )
                
                Spacer(modifier = Modifier.height(16.dp))
                
                // Gross Income
                OutlinedTextField(
                    value = grossIncome,
                    onValueChange = { viewModel.updateGrossIncome(it) },
                    label = { Text("Annual Gross Income") },
                    leadingIcon = {
                        Icon(Icons.Default.AccountBalance, contentDescription = null)
                    },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = Indigo,
                        unfocusedBorderColor = MaterialTheme.colorScheme.outline
                    )
                )
                
                Spacer(modifier = Modifier.height(12.dp))
                
                // 80C Deductions
                OutlinedTextField(
                    value = deductions80C,
                    onValueChange = { viewModel.updateDeductions80C(it) },
                    label = { Text("Deductions (80C)") },
                    leadingIcon = {
                        Icon(Icons.Default.Savings, contentDescription = null)
                    },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = Indigo,
                        unfocusedBorderColor = MaterialTheme.colorScheme.outline
                    )
                )
                
                Spacer(modifier = Modifier.height(12.dp))
                
                // Other Deductions
                OutlinedTextField(
                    value = otherDeductions,
                    onValueChange = { viewModel.updateOtherDeductions(it) },
                    label = { Text("Other Deductions") },
                    leadingIcon = {
                        Icon(Icons.Default.MoreHoriz, contentDescription = null)
                    },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = Indigo,
                        unfocusedBorderColor = MaterialTheme.colorScheme.outline
                    )
                )
            }
            
            Spacer(modifier = Modifier.height(20.dp))
            
            // Action Buttons
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                AnimatedGradientButton(
                    text = "Calculate",
                    onClick = { viewModel.calculateTDS() },
                    modifier = Modifier.weight(1f),
                    gradient = Brush.linearGradient(
                        colors = listOf(Indigo, Purple, Pink)
                    ),
                    enabled = grossIncome.isNotEmpty() && !isCalculating
                )
                
                OutlinedButton(
                    onClick = { viewModel.resetCalculator() },
                    modifier = Modifier.height(56.dp),
                    shape = RoundedCornerShape(16.dp),
                    colors = ButtonDefaults.outlinedButtonColors(
                        contentColor = MaterialTheme.colorScheme.primary
                    )
                ) {
                    Icon(Icons.Default.Refresh, contentDescription = null)
                }
            }
            
            // Results
            AnimatedVisibility(
                visible = calculationResult != null,
                enter = slideInVertically() + fadeIn(),
                exit = slideOutVertically() + fadeOut()
            ) {
                calculationResult?.let { result ->
                    Column {
                        Spacer(modifier = Modifier.height(24.dp))
                        
                        CalculationResultCard(result)
                    }
                }
            }
        }
    }
}

@Composable
fun CalculationResultCard(result: TDSCalculation) {
    val currencyFormat = NumberFormat.getCurrencyInstance(Locale("en", "IN"))
    
    Column(
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Main Result Card
        GlassmorphicCard {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = "Monthly TDS",
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                
                Spacer(modifier = Modifier.height(8.dp))
                
                // Circular Progress
                Box(
                    modifier = Modifier
                        .size(160.dp)
                        .clip(CircleShape)
                        .background(
                            Brush.radialGradient(
                                colors = listOf(
                                    Indigo.copy(alpha = 0.2f),
                                    Purple.copy(alpha = 0.1f)
                                )
                            )
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = currencyFormat.format(result.monthlyTDS),
                            style = MaterialTheme.typography.headlineMedium.copy(
                                fontWeight = FontWeight.Bold,
                                fontSize = 28.sp
                            ),
                            color = Indigo
                        )
                        Text(
                            text = "per month",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
                
                Spacer(modifier = Modifier.height(16.dp))
                
                Divider(color = MaterialTheme.colorScheme.outline.copy(alpha = 0.3f))
                
                Spacer(modifier = Modifier.height(16.dp))
                
                // Tax Breakdown
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column {
                        Text(
                            text = "Taxable Income",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Text(
                            text = currencyFormat.format(result.taxableIncome),
                            style = MaterialTheme.typography.titleMedium.copy(
                                fontWeight = FontWeight.SemiBold
                            ),
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    }
                    
                    Column(horizontalAlignment = Alignment.End) {
                        Text(
                            text = "Annual Tax",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Text(
                            text = currencyFormat.format(result.netTax),
                            style = MaterialTheme.typography.titleMedium.copy(
                                fontWeight = FontWeight.SemiBold
                            ),
                            color = Indigo
                        )
                    }
                }
            }
        }
        
        // Stat Cards
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            StatCard(
                title = "Total Tax",
                value = currencyFormat.format(result.totalTax),
                icon = Icons.Default.Receipt,
                modifier = Modifier.weight(1f),
                gradient = Brush.linearGradient(
                    colors = listOf(Indigo, Purple)
                )
            )
            
            StatCard(
                title = "Cess (4%)",
                value = currencyFormat.format(result.cess),
                icon = Icons.Default.Add,
                modifier = Modifier.weight(1f),
                gradient = Brush.linearGradient(
                    colors = listOf(Purple, Pink)
                )
            )
        }
        
        // Tax Slab Breakdown
        if (result.taxBreakdown.isNotEmpty()) {
            PremiumCard(
                gradient = Brush.linearGradient(
                    colors = listOf(Emerald.copy(alpha = 0.5f), SuccessTeal.copy(alpha = 0.5f))
                )
            ) {
                Text(
                    text = "Tax Slab Breakdown",
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.SemiBold
                    ),
                    color = MaterialTheme.colorScheme.onSurface
                )
                
                Spacer(modifier = Modifier.height(12.dp))
                
                result.taxBreakdown.forEach { slab ->
                    if (slab.income > 0) {
                        Column {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text(
                                    text = slab.slabName,
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                                Text(
                                    text = "${(slab.rate * 100).toInt()}%",
                                    style = MaterialTheme.typography.bodyMedium.copy(
                                        fontWeight = FontWeight.SemiBold
                                    ),
                                    color = Emerald
                                )
                            }
                            
                            Spacer(modifier = Modifier.height(4.dp))
                            
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text(
                                    text = currencyFormat.format(slab.income),
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                                Text(
                                    text = "Tax: ${currencyFormat.format(slab.tax)}",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                            
                            Spacer(modifier = Modifier.height(8.dp))
                            
                            AnimatedProgressBar(
                                progress = (slab.income / result.taxableIncome).toFloat(),
                                gradient = Brush.linearGradient(
                                    colors = listOf(Emerald, SuccessTeal)
                                )
                            )
                            
                            Spacer(modifier = Modifier.height(12.dp))
                        }
                    }
                }
            }
        }
    }
}
