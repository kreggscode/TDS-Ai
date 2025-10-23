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
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.kreggscode.tdscalculator.data.models.TDSCalculation
import com.kreggscode.tdscalculator.ui.components.*
import com.kreggscode.tdscalculator.ui.theme.*
import com.kreggscode.tdscalculator.ui.viewmodels.TDSViewModel
import kotlinx.coroutines.launch
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
    
    var aiAnalysis by remember { mutableStateOf<String?>(null) }
    var isAnalyzing by remember { mutableStateOf(false) }
    val coroutineScope = rememberCoroutineScope()
    
    // Water type selection
    var expanded by remember { mutableStateOf(false) }
    val waterTypes = listOf(
        "Natural Freshwater" to 0.64,
        "Drinking Water" to 0.67,
        "Brackish Water" to 0.8,
        "Seawater" to 0.9
    )
    var selectedWaterType by remember { mutableStateOf(waterTypes[0]) }
    var applyTempCorrection by remember { mutableStateOf(true) }
    
    // Additional water quality parameters
    var phValue by remember { mutableStateOf("") }
    var turbidity by remember { mutableStateOf("") }
    var salinity by remember { mutableStateOf("") }
    var showAdvancedParams by remember { mutableStateOf(false) }
    
    val scrollState = rememberScrollState()
    val focusManager = LocalFocusManager.current
    
    Box(modifier = modifier.fillMaxSize()) {
        GradientBackground()
        
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(scrollState)
                .padding(horizontal = 20.dp)
                .padding(top = 48.dp, bottom = 250.dp)
                .imePadding()
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
                        text = "Measure water quality",
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
                    colors = listOf(AITeal.copy(alpha = 0.5f), AIIndigo.copy(alpha = 0.5f))
                )
            ) {
                Text(
                    text = "Water Quality Parameters",
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.SemiBold
                    ),
                    color = MaterialTheme.colorScheme.onSurface
                )
                
                Spacer(modifier = Modifier.height(16.dp))
                
                // Conductivity
                OutlinedTextField(
                    value = grossIncome,
                    onValueChange = { viewModel.updateGrossIncome(it) },
                    label = { Text("Electrical Conductivity (µS/cm)") },
                    leadingIcon = {
                        Icon(Icons.Default.WaterDrop, contentDescription = null)
                    },
                    supportingText = { Text("From your TDS meter reading") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = AITeal,
                        unfocusedBorderColor = MaterialTheme.colorScheme.outline
                    )
                )
                
                Spacer(modifier = Modifier.height(12.dp))
                
                // Temperature
                OutlinedTextField(
                    value = deductions80C,
                    onValueChange = { viewModel.updateDeductions80C(it) },
                    label = { Text("Water Temperature (°C)") },
                    leadingIcon = {
                        Icon(Icons.Default.Thermostat, contentDescription = null)
                    },
                    supportingText = { Text("Default: 25°C for room temperature") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = AITeal,
                        unfocusedBorderColor = MaterialTheme.colorScheme.outline
                    )
                )
                
                Spacer(modifier = Modifier.height(12.dp))
                
                // Water Type Dropdown
                ExposedDropdownMenuBox(
                    expanded = expanded,
                    onExpandedChange = { expanded = !expanded },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    OutlinedTextField(
                        value = selectedWaterType.first,
                        onValueChange = {},
                        readOnly = true,
                        label = { Text("Water Type") },
                        leadingIcon = {
                            Icon(Icons.Default.WaterDrop, contentDescription = null)
                        },
                        trailingIcon = {
                            ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded)
                        },
                        supportingText = { Text("K = ${selectedWaterType.second}") },
                        modifier = Modifier
                            .fillMaxWidth()
                            .menuAnchor(),
                        shape = RoundedCornerShape(12.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = AITeal,
                            unfocusedBorderColor = MaterialTheme.colorScheme.outline
                        )
                    )
                    
                    ExposedDropdownMenu(
                        expanded = expanded,
                        onDismissRequest = { expanded = false }
                    ) {
                        waterTypes.forEach { waterType ->
                            DropdownMenuItem(
                                text = { 
                                    Column {
                                        Text(waterType.first)
                                        Text(
                                            "K = ${waterType.second}",
                                            style = MaterialTheme.typography.bodySmall,
                                            color = MaterialTheme.colorScheme.onSurfaceVariant
                                        )
                                    }
                                },
                                onClick = {
                                    selectedWaterType = waterType
                                    viewModel.updateOtherDeductions(waterType.second.toString())
                                    expanded = false
                                }
                            )
                        }
                    }
                }
                
                Spacer(modifier = Modifier.height(12.dp))
                
                // Temperature Correction Toggle
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Thermostat,
                            contentDescription = null,
                            tint = AITeal
                        )
                        Column {
                            Text(
                                text = "Temperature Correction",
                                style = MaterialTheme.typography.bodyMedium.copy(
                                    fontWeight = FontWeight.Medium
                                ),
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            Text(
                                text = "Normalize to 25°C",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                    Switch(
                        checked = applyTempCorrection,
                        onCheckedChange = { applyTempCorrection = it },
                        colors = SwitchDefaults.colors(
                            checkedThumbColor = Color.White,
                            checkedTrackColor = AITeal
                        )
                    )
                }
            }
            
            Spacer(modifier = Modifier.height(16.dp))
            
            // Advanced Parameters Toggle
            TextButton(
                onClick = { showAdvancedParams = !showAdvancedParams },
                modifier = Modifier.fillMaxWidth()
            ) {
                Icon(
                    imageVector = if (showAdvancedParams) Icons.Default.ExpandLess else Icons.Default.ExpandMore,
                    contentDescription = null,
                    tint = AITeal
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = if (showAdvancedParams) "Hide Advanced Parameters" else "Show Advanced Parameters",
                    color = AITeal,
                    style = MaterialTheme.typography.bodyMedium.copy(
                        fontWeight = FontWeight.Medium
                    )
                )
            }
            
            // Advanced Parameters Card
            AnimatedVisibility(
                visible = showAdvancedParams,
                enter = expandVertically() + fadeIn(),
                exit = shrinkVertically() + fadeOut()
            ) {
                PremiumCard(
                    gradient = Brush.linearGradient(
                        colors = listOf(Purple.copy(alpha = 0.5f), Pink.copy(alpha = 0.5f))
                    )
                ) {
                    Text(
                        text = "Advanced Water Quality Parameters",
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.SemiBold
                        ),
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    
                    Spacer(modifier = Modifier.height(16.dp))
                    
                    // pH Value
                    OutlinedTextField(
                        value = phValue,
                        onValueChange = { phValue = it.filter { c -> c.isDigit() || c == '.' } },
                        label = { Text("pH Level") },
                        leadingIcon = {
                            Icon(Icons.Default.Science, contentDescription = null)
                        },
                        supportingText = { Text("Normal range: 6.5-8.5") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = Purple,
                            unfocusedBorderColor = MaterialTheme.colorScheme.outline
                        )
                    )
                    
                    Spacer(modifier = Modifier.height(12.dp))
                    
                    // Turbidity
                    OutlinedTextField(
                        value = turbidity,
                        onValueChange = { turbidity = it.filter { c -> c.isDigit() || c == '.' } },
                        label = { Text("Turbidity (NTU)") },
                        leadingIcon = {
                            Icon(Icons.Default.Visibility, contentDescription = null)
                        },
                        supportingText = { Text("Clarity measure (lower is clearer)") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = Purple,
                            unfocusedBorderColor = MaterialTheme.colorScheme.outline
                        )
                    )
                    
                    Spacer(modifier = Modifier.height(12.dp))
                    
                    // Salinity
                    OutlinedTextField(
                        value = salinity,
                        onValueChange = { salinity = it.filter { c -> c.isDigit() || c == '.' } },
                        label = { Text("Salinity (ppt)") },
                        leadingIcon = {
                            Icon(Icons.Default.Grain, contentDescription = null)
                        },
                        supportingText = { Text("Salt content in parts per thousand") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = Purple,
                            unfocusedBorderColor = MaterialTheme.colorScheme.outline
                        )
                    )
                }
            }
            
            Spacer(modifier = Modifier.height(20.dp))
            
            // Action Buttons
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                AnimatedGradientButton(
                    text = "Calculate",
                    onClick = { 
                        focusManager.clearFocus()
                        viewModel.calculateTDS()
                    },
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
                        
                        CalculationResultCard(
                            result = result,
                            aiAnalysis = aiAnalysis,
                            isAnalyzing = isAnalyzing,
                            phValue = phValue,
                            turbidity = turbidity,
                            salinity = salinity,
                            onAnalyzeClick = {
                                isAnalyzing = true
                                coroutineScope.launch {
                                    aiAnalysis = viewModel.getAIAnalysis(
                                        tdsValue = result.monthlyTDS,
                                        conductivity = result.taxableIncome,
                                        temperature = result.netTax
                                    )
                                    isAnalyzing = false
                                }
                            }
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun CalculationResultCard(
    result: TDSCalculation,
    aiAnalysis: String? = null,
    isAnalyzing: Boolean = false,
    phValue: String = "",
    turbidity: String = "",
    salinity: String = "",
    onAnalyzeClick: () -> Unit = {}
) {
    // Get quality rating based on WHO and international standards
    val qualityRating = when {
        result.monthlyTDS < 50 -> "Too Pure"
        result.monthlyTDS <= 150 -> "Excellent"
        result.monthlyTDS <= 300 -> "Good"
        result.monthlyTDS <= 500 -> "Fair"
        result.monthlyTDS <= 600 -> "Acceptable"
        result.monthlyTDS <= 900 -> "Poor"
        result.monthlyTDS <= 1200 -> "Very Poor"
        else -> "Unacceptable"
    }
    
    val qualityDescription = when {
        result.monthlyTDS < 50 -> "May lack essential minerals"
        result.monthlyTDS <= 150 -> "Ideal for drinking water"
        result.monthlyTDS <= 300 -> "Acceptable for drinking"
        result.monthlyTDS <= 500 -> "May affect taste"
        result.monthlyTDS <= 600 -> "WHO limit for drinking"
        result.monthlyTDS <= 900 -> "Not ideal for drinking"
        result.monthlyTDS <= 1200 -> "Not recommended"
        else -> "Requires treatment"
    }
    
    val qualityColor = when {
        result.monthlyTDS < 50 -> Amber
        result.monthlyTDS <= 150 -> Emerald
        result.monthlyTDS <= 300 -> SuccessTeal
        result.monthlyTDS <= 500 -> AITeal
        result.monthlyTDS <= 600 -> Amber
        result.monthlyTDS <= 900 -> WarningOrange
        else -> WarningRed
    }
    
    val qualityProgress = when {
        result.monthlyTDS < 50 -> 0.15f
        result.monthlyTDS <= 150 -> 0.3f
        result.monthlyTDS <= 300 -> 0.5f
        result.monthlyTDS <= 500 -> 0.65f
        result.monthlyTDS <= 600 -> 0.75f
        result.monthlyTDS <= 900 -> 0.85f
        result.monthlyTDS <= 1200 -> 0.95f
        else -> 1f
    }
    
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
                    text = "Water Quality Result",
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
                                    qualityColor.copy(alpha = 0.2f),
                                    qualityColor.copy(alpha = 0.1f)
                                )
                            )
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = "${result.monthlyTDS.toInt()}",
                            style = MaterialTheme.typography.headlineMedium.copy(
                                fontWeight = FontWeight.Bold,
                                fontSize = 36.sp
                            ),
                            color = qualityColor
                        )
                        Text(
                            text = "ppm",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = qualityRating,
                            style = MaterialTheme.typography.titleSmall.copy(
                                fontWeight = FontWeight.Bold
                            ),
                            color = qualityColor
                        )
                        Text(
                            text = qualityDescription,
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
                
                Spacer(modifier = Modifier.height(16.dp))
                
                Divider(color = MaterialTheme.colorScheme.outline.copy(alpha = 0.3f))
                
                Spacer(modifier = Modifier.height(16.dp))
                
                // Water Quality Info
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column {
                        Text(
                            text = "Conductivity",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Text(
                            text = "${result.taxableIncome.toInt()} µS/cm",
                            style = MaterialTheme.typography.titleMedium.copy(
                                fontWeight = FontWeight.SemiBold
                            ),
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    }
                    
                    Column(horizontalAlignment = Alignment.End) {
                        Text(
                            text = "Temperature",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Text(
                            text = "${result.netTax.toInt()}°C",
                            style = MaterialTheme.typography.titleMedium.copy(
                                fontWeight = FontWeight.SemiBold
                            ),
                            color = AITeal
                        )
                    }
                }
            }
        }
        
        // Quality Progress Bar
        PremiumCard(
            gradient = Brush.linearGradient(
                colors = listOf(
                    qualityColor.copy(alpha = 0.3f),
                    qualityColor.copy(alpha = 0.2f)
                )
            )
        ) {
            Column {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Water Quality Scale",
                        style = MaterialTheme.typography.titleSmall.copy(
                            fontWeight = FontWeight.SemiBold
                        ),
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        text = "WHO Standard: <600 ppm",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                
                Spacer(modifier = Modifier.height(12.dp))
                
                AnimatedProgressBar(
                    progress = qualityProgress,
                    gradient = Brush.linearGradient(
                        colors = listOf(qualityColor, qualityColor.copy(alpha = 0.7f))
                    )
                )
                
                Spacer(modifier = Modifier.height(8.dp))
                
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = "0",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Text(
                        text = "1200+ ppm",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }
        
        // Stat Cards
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            StatCard(
                title = "TDS Level",
                value = "${result.totalTax.toInt()} ppm",
                icon = Icons.Default.WaterDrop,
                modifier = Modifier.weight(1f),
                gradient = Brush.linearGradient(
                    colors = listOf(AITeal, AIIndigo)
                )
            )
            
            StatCard(
                title = "Quality",
                value = qualityRating,
                icon = Icons.Default.CheckCircle,
                modifier = Modifier.weight(1f),
                gradient = Brush.linearGradient(
                    colors = listOf(qualityColor, qualityColor.copy(alpha = 0.7f))
                )
            )
        }
        
        // Additional Parameters Display (if provided)
        if (phValue.isNotEmpty() || turbidity.isNotEmpty() || salinity.isNotEmpty()) {
            Spacer(modifier = Modifier.height(12.dp))
            
            PremiumCard(
                gradient = Brush.linearGradient(
                    colors = listOf(Purple.copy(alpha = 0.4f), Pink.copy(alpha = 0.3f))
                )
            ) {
                Text(
                    text = "Additional Parameters",
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.SemiBold
                    ),
                    color = MaterialTheme.colorScheme.onSurface
                )
                
                Spacer(modifier = Modifier.height(12.dp))
                
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    if (phValue.isNotEmpty()) {
                        Column(modifier = Modifier.weight(1f)) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(4.dp)
                            ) {
                                Icon(
                                    Icons.Default.Science,
                                    contentDescription = null,
                                    tint = Purple,
                                    modifier = Modifier.size(16.dp)
                                )
                                Text(
                                    text = "pH Level",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                            Text(
                                text = phValue,
                                style = MaterialTheme.typography.titleMedium.copy(
                                    fontWeight = FontWeight.SemiBold
                                ),
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            val phStatus = phValue.toDoubleOrNull()?.let { ph ->
                                when {
                                    ph < 6.5 -> "Acidic"
                                    ph > 8.5 -> "Alkaline"
                                    else -> "Normal"
                                }
                            } ?: ""
                            if (phStatus.isNotEmpty()) {
                                Text(
                                    text = phStatus,
                                    style = MaterialTheme.typography.bodySmall,
                                    color = when (phStatus) {
                                        "Normal" -> Emerald
                                        else -> Amber
                                    }
                                )
                            }
                        }
                    }
                    
                    if (turbidity.isNotEmpty()) {
                        Column(modifier = Modifier.weight(1f)) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(4.dp)
                            ) {
                                Icon(
                                    Icons.Default.Visibility,
                                    contentDescription = null,
                                    tint = Purple,
                                    modifier = Modifier.size(16.dp)
                                )
                                Text(
                                    text = "Turbidity",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                            Text(
                                text = "$turbidity NTU",
                                style = MaterialTheme.typography.titleMedium.copy(
                                    fontWeight = FontWeight.SemiBold
                                ),
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            val turbStatus = turbidity.toDoubleOrNull()?.let { turb ->
                                when {
                                    turb < 1 -> "Clear"
                                    turb < 5 -> "Good"
                                    else -> "Cloudy"
                                }
                            } ?: ""
                            if (turbStatus.isNotEmpty()) {
                                Text(
                                    text = turbStatus,
                                    style = MaterialTheme.typography.bodySmall,
                                    color = when (turbStatus) {
                                        "Clear" -> Emerald
                                        "Good" -> SuccessTeal
                                        else -> Amber
                                    }
                                )
                            }
                        }
                    }
                    
                    if (salinity.isNotEmpty()) {
                        Column(modifier = Modifier.weight(1f)) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(4.dp)
                            ) {
                                Icon(
                                    Icons.Default.Grain,
                                    contentDescription = null,
                                    tint = Purple,
                                    modifier = Modifier.size(16.dp)
                                )
                                Text(
                                    text = "Salinity",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                            Text(
                                text = "$salinity ppt",
                                style = MaterialTheme.typography.titleMedium.copy(
                                    fontWeight = FontWeight.SemiBold
                                ),
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            val salStatus = salinity.toDoubleOrNull()?.let { sal ->
                                when {
                                    sal < 0.5 -> "Fresh"
                                    sal < 30 -> "Brackish"
                                    else -> "Saline"
                                }
                            } ?: ""
                            if (salStatus.isNotEmpty()) {
                                Text(
                                    text = salStatus,
                                    style = MaterialTheme.typography.bodySmall,
                                    color = when (salStatus) {
                                        "Fresh" -> Emerald
                                        "Brackish" -> AITeal
                                        else -> Amber
                                    }
                                )
                            }
                        }
                    }
                }
            }
        }
        
        // Water Quality Breakdown
        if (result.taxBreakdown.isNotEmpty()) {
            PremiumCard(
                gradient = Brush.linearGradient(
                    colors = listOf(qualityColor.copy(alpha = 0.5f), qualityColor.copy(alpha = 0.3f))
                )
            ) {
                Text(
                    text = "Water Quality Assessment",
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
                                Icon(
                                    imageVector = if (slab.income == result.monthlyTDS) 
                                        Icons.Default.CheckCircle else Icons.Default.Circle,
                                    contentDescription = null,
                                    tint = if (slab.income == result.monthlyTDS) 
                                        qualityColor else MaterialTheme.colorScheme.outline,
                                    modifier = Modifier.size(20.dp)
                                )
                            }
                            
                            Spacer(modifier = Modifier.height(4.dp))
                            
                            Text(
                                text = "${slab.income.toInt()} ppm",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            
                            Spacer(modifier = Modifier.height(8.dp))
                            
                            AnimatedProgressBar(
                                progress = if (slab.income == result.monthlyTDS) 1f else 0.3f,
                                gradient = Brush.linearGradient(
                                    colors = if (slab.income == result.monthlyTDS)
                                        listOf(qualityColor, qualityColor.copy(alpha = 0.7f))
                                    else
                                        listOf(MaterialTheme.colorScheme.outline, MaterialTheme.colorScheme.outline.copy(alpha = 0.5f))
                                )
                            )
                            
                            Spacer(modifier = Modifier.height(12.dp))
                        }
                    }
                }
            }
        }
        
        // AI Analysis Section
        PremiumCard(
            gradient = Brush.linearGradient(
                colors = listOf(AITeal.copy(alpha = 0.5f), AIIndigo.copy(alpha = 0.5f), AIPurple.copy(alpha = 0.3f))
            )
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(40.dp)
                            .background(
                                Brush.linearGradient(
                                    colors = listOf(AITeal, AIIndigo)
                                ),
                                CircleShape
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        if (isAnalyzing) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(20.dp),
                                color = Color.White,
                                strokeWidth = 2.dp
                            )
                        } else {
                            PulsingIcon(
                                icon = Icons.Default.SmartToy,
                                tint = Color.White,
                                size = 20.dp
                            )
                        }
                    }
                    
                    Text(
                        text = "AI Analysis",
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.SemiBold
                        ),
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }
                
                if (aiAnalysis == null) {
                    AnimatedGradientButton(
                        text = "Analyze",
                        onClick = onAnalyzeClick,
                        modifier = Modifier.height(40.dp),
                        gradient = Brush.linearGradient(
                            colors = listOf(AITeal, AIIndigo, AIPurple)
                        ),
                        enabled = !isAnalyzing
                    )
                }
            }
            
            if (aiAnalysis != null) {
                Spacer(modifier = Modifier.height(12.dp))
                
                Divider(color = MaterialTheme.colorScheme.outline.copy(alpha = 0.3f))
                
                Spacer(modifier = Modifier.height(12.dp))
                
                Text(
                    text = aiAnalysis,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurface,
                    lineHeight = MaterialTheme.typography.bodyMedium.lineHeight * 1.4f
                )
            }
        }
    }
}
