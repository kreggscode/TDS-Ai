package com.kreggscode.tdscalculator.data.repository

import com.kreggscode.tdscalculator.data.models.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONArray
import org.json.JSONObject
import java.net.HttpURLConnection
import java.net.URL
import java.net.URLEncoder
import javax.inject.Inject
import javax.inject.Singleton
import kotlin.math.max

@Singleton
class TDSRepository @Inject constructor() {
    
    // Water Quality TDS Calculation
    // TDS = Total Dissolved Solids in water (measured in ppm or mg/L)
    fun calculateTDS(
        conductivity: Double,  // Electrical conductivity in µS/cm
        temperature: Double,   // Water temperature in °C
        conversionFactor: Double = 0.64  // Standard conversion factor
    ): TDSCalculation {
        // TDS (ppm) = Conductivity (µS/cm) × Conversion Factor
        val tdsValue = conductivity * conversionFactor
        
        // Temperature compensation (standard reference is 25°C)
        val tempCompensation = 1 + 0.02 * (temperature - 25)
        val compensatedTDS = tdsValue / tempCompensation
        
        val waterQuality = getWaterQualityRating(compensatedTDS)
        val breakdown = getTDSBreakdown(compensatedTDS)
        
        return TDSCalculation(
            grossIncome = conductivity,  // Reusing field for conductivity
            deductions80C = temperature,  // Reusing field for temperature
            otherDeductions = conversionFactor,  // Reusing field for conversion factor
            taxableIncome = conductivity,  // Conductivity value for display
            totalTax = compensatedTDS,  // Compensated TDS for display
            cess = waterQuality.toDouble(),  // Reusing field for quality rating (1-5)
            netTax = temperature,  // Temperature for display
            monthlyTDS = compensatedTDS,  // Main TDS value to display in PPM
            taxBreakdown = breakdown
        )
    }
    
    private fun getWaterQualityRating(tds: Double): Int {
        return when {
            tds < 50 -> 1  // Too pure (may lack minerals)
            tds < 150 -> 5  // Excellent
            tds < 300 -> 4  // Good
            tds < 500 -> 3  // Fair
            tds < 900 -> 2  // Poor
            else -> 1  // Unacceptable
        }
    }
    
    private fun getTDSBreakdown(tds: Double): List<TaxSlabBreakdown> {
        val breakdown = mutableListOf<TaxSlabBreakdown>()
        
        // Water quality categories based on TDS levels
        val categories = listOf(
            Pair("Excellent (50-150 ppm)", if (tds in 50.0..150.0) tds else 0.0),
            Pair("Good (150-300 ppm)", if (tds in 150.0..300.0) tds else 0.0),
            Pair("Fair (300-500 ppm)", if (tds in 300.0..500.0) tds else 0.0),
            Pair("Poor (500-900 ppm)", if (tds in 500.0..900.0) tds else 0.0),
            Pair("Unacceptable (>900 ppm)", if (tds > 900.0) tds else 0.0)
        )
        
        for ((category, value) in categories) {
            if (value > 0) {
                breakdown.add(
                    TaxSlabBreakdown(
                        slabName = category,
                        income = value,
                        rate = getWaterQualityRating(value).toDouble() / 5.0,
                        tax = value
                    )
                )
            }
        }
        
        return breakdown
    }
    
    fun getTDSInformation(): List<TDSInfo> {
        return listOf(
            TDSInfo(
                title = "What is TDS?",
                description = "Total Dissolved Solids - A measure of water quality",
                details = listOf(
                    "TDS measures the total concentration of dissolved substances in water",
                    "Measured in parts per million (ppm) or milligrams per liter (mg/L)",
                    "Includes minerals, salts, metals, and other dissolved particles",
                    "Higher TDS doesn't always mean unsafe, but affects taste and quality",
                    "Essential for assessing drinking water, aquariums, and hydroponics"
                )
            ),
            TDSInfo(
                title = "TDS Water Quality Levels",
                description = "Understanding TDS measurements",
                details = listOf(
                    "Less than 50 ppm: Too pure, may lack essential minerals",
                    "50-150 ppm: Excellent - Ideal for drinking water",
                    "150-300 ppm: Good - Acceptable for drinking",
                    "300-500 ppm: Fair - Acceptable but may affect taste",
                    "500-900 ppm: Poor - Not ideal for drinking",
                    "900-1200 ppm: Very Poor - Not recommended",
                    "Above 1200 ppm: Unacceptable - Requires treatment",
                    "WHO recommends less than 600 ppm for drinking water"
                )
            ),
            TDSInfo(
                title = "How to Measure TDS",
                description = "Methods for testing water quality",
                details = listOf(
                    "Use a TDS meter (digital probe) - Most accurate",
                    "Measure electrical conductivity (EC) in µS/cm",
                    "TDS = EC × Conversion Factor (typically 0.5-0.7)",
                    "Test at room temperature (25°C) for accuracy",
                    "Calibrate meter regularly with calibration solution",
                    "Test multiple samples from different sources",
                    "Record measurements over time to track changes",
                    "Clean probe after each use for longevity"
                )
            ),
            TDSInfo(
                title = "Common TDS Sources",
                description = "What contributes to TDS in water",
                details = listOf(
                    "Natural minerals: Calcium, Magnesium, Potassium",
                    "Salts: Sodium chloride, Carbonates, Bicarbonates",
                    "Metals: Iron, Copper, Zinc, Lead, Manganese",
                    "Industrial pollutants and chemical runoff",
                    "Agricultural fertilizers and pesticides",
                    "Sewage and wastewater contamination",
                    "Pipe corrosion and plumbing materials",
                    "Geological formations and soil composition"
                )
            ),
            TDSInfo(
                title = "Water Treatment Methods",
                description = "How to reduce TDS effectively",
                details = listOf(
                    "Reverse Osmosis (RO) - Removes 90-95% of TDS",
                    "Distillation - Removes almost all dissolved solids",
                    "Deionization - Uses ion exchange resins",
                    "Activated Carbon Filters - Removes chlorine and organics",
                    "UV Purification - Kills bacteria but doesn't reduce TDS",
                    "Boiling - Kills pathogens but concentrates TDS",
                    "Regular filter maintenance is essential",
                    "Consider remineralization after RO treatment"
                )
            ),
            TDSInfo(
                title = "Health Effects of TDS",
                description = "Impact on human health",
                details = listOf(
                    "Moderate TDS (50-300 ppm) provides essential minerals",
                    "Very low TDS may lack beneficial minerals",
                    "High TDS can cause digestive issues and kidney problems",
                    "Some minerals like calcium and magnesium are beneficial",
                    "Heavy metals in high TDS water are harmful",
                    "Taste becomes unpleasant above 500 ppm",
                    "Long-term consumption of high TDS water not recommended",
                    "Always test for specific contaminants, not just TDS"
                )
            ),
            TDSInfo(
                title = "TDS vs Water Hardness",
                description = "Understanding the difference",
                details = listOf(
                    "TDS measures all dissolved solids in water",
                    "Hardness specifically measures calcium and magnesium",
                    "Hard water has high calcium/magnesium content",
                    "Hard water can have moderate TDS levels",
                    "Soft water typically has lower TDS",
                    "Water softeners exchange calcium for sodium",
                    "Softened water may increase sodium TDS",
                    "Both TDS and hardness affect water quality differently"
                )
            ),
            TDSInfo(
                title = "Reverse Osmosis (RO) Systems",
                description = "Most effective TDS reduction method",
                details = listOf(
                    "Uses semi-permeable membrane to filter water",
                    "Removes 90-99% of dissolved solids",
                    "Requires regular membrane replacement (2-3 years)",
                    "Pre-filters protect RO membrane",
                    "Post-filters improve taste",
                    "Produces wastewater (3-4 gallons per gallon filtered)",
                    "Remineralization cartridge adds back minerals",
                    "Regular maintenance ensures optimal performance"
                )
            ),
            TDSInfo(
                title = "TDS in Different Water Sources",
                description = "Typical TDS levels by source",
                details = listOf(
                    "Distilled water: <10 ppm",
                    "RO water: 30-100 ppm",
                    "Rainwater: 5-30 ppm",
                    "Tap water: 100-500 ppm (varies by location)",
                    "Groundwater/Well water: 200-1000 ppm",
                    "Brackish water: 1000-5000 ppm",
                    "Seawater: 30,000-50,000 ppm",
                    "Mineral water: 250-800 ppm"
                )
            ),
            TDSInfo(
                title = "Conductivity and TDS Relationship",
                description = "Understanding EC to TDS conversion",
                details = listOf(
                    "Electrical Conductivity (EC) measured in µS/cm",
                    "TDS = EC × Conversion Factor",
                    "Conversion factor typically 0.5 to 0.7",
                    "0.64 is standard for most drinking water",
                    "0.5 for pure water with minimal ions",
                    "0.7 for water with high salt content",
                    "Temperature affects conductivity readings",
                    "Most meters auto-compensate for temperature"
                )
            ),
            TDSInfo(
                title = "Water Quality Standards",
                description = "International guidelines",
                details = listOf(
                    "WHO: <600 ppm for drinking water",
                    "EPA (USA): <500 ppm recommended",
                    "BIS (India): <500 ppm acceptable, <200 ppm desirable",
                    "EU: <1500 ppm maximum for drinking water",
                    "Bottled water: Typically 50-250 ppm",
                    "Aquariums: 50-150 ppm for freshwater fish",
                    "Hydroponics: 800-1500 ppm depending on plants",
                    "Swimming pools: 200-400 ppm acceptable"
                )
            ),
            TDSInfo(
                title = "Maintaining Your TDS Meter",
                description = "Proper care and calibration",
                details = listOf(
                    "Calibrate monthly with calibration solution",
                    "Use 1413 µS/cm or 1000 ppm calibration standard",
                    "Clean probe with distilled water after each use",
                    "Store probe in storage solution, not water",
                    "Replace probe every 1-2 years",
                    "Check battery regularly",
                    "Avoid extreme temperatures",
                    "Keep meter dry when not in use"
                )
            ),
            TDSInfo(
                title = "TDS in Cooking and Beverages",
                description = "Impact on taste and quality",
                details = listOf(
                    "Coffee: 50-150 ppm water enhances flavor",
                    "Tea: 50-100 ppm ideal for brewing",
                    "Pasta/Rice: Higher TDS can affect cooking time",
                    "Baking: Low TDS water preferred",
                    "Ice cubes: Low TDS prevents cloudiness",
                    "Aquariums: Species-specific TDS requirements",
                    "Plants: 50-150 ppm for most houseplants",
                    "High TDS affects taste of all beverages"
                )
            ),
            TDSInfo(
                title = "Seasonal TDS Variations",
                description = "How TDS changes throughout the year",
                details = listOf(
                    "Monsoon/Rain season: TDS typically decreases",
                    "Summer: TDS often increases due to evaporation",
                    "Winter: Groundwater TDS may be more stable",
                    "Agricultural runoff increases TDS in spring",
                    "Drought conditions concentrate dissolved solids",
                    "Flooding can dilute or contaminate water",
                    "Regular testing recommended quarterly",
                    "Keep log of TDS readings over time"
                )
            ),
            TDSInfo(
                title = "Emergency Water Purification",
                description = "Quick methods to reduce TDS",
                details = listOf(
                    "Boiling kills pathogens but doesn't reduce TDS",
                    "Portable RO filters for camping/travel",
                    "Distillation using solar stills",
                    "Activated carbon removes chlorine and taste",
                    "UV pens kill bacteria in clear water",
                    "Water purification tablets for emergencies",
                    "Ceramic filters remove particles but not TDS",
                    "Always test water after treatment"
                )
            )
        )
    }
    
    // Pollinations.AI API integration
    suspend fun getAIResponse(userMessage: String): String {
        return withContext(Dispatchers.IO) {
            try {
                // System prompt for water quality TDS context
                val systemPrompt = "You are a helpful AI assistant specializing in water quality and TDS (Total Dissolved Solids) measurements. " +
                        "Provide accurate, concise information about water quality, TDS levels, measurement methods, and water treatment. " +
                        "Keep responses friendly and easy to understand."
                
                // Build the request payload
                val payload = JSONObject().apply {
                    put("model", "openai")
                    put("temperature", 1.0)
                    put("max_tokens", 500)
                    put("messages", JSONArray().apply {
                        put(JSONObject().apply {
                            put("role", "system")
                            put("content", systemPrompt)
                        })
                        put(JSONObject().apply {
                            put("role", "user")
                            put("content", userMessage)
                        })
                    })
                }
                
                // Make API request
                val url = URL("https://text.pollinations.ai/openai")
                val connection = url.openConnection() as HttpURLConnection
                connection.apply {
                    requestMethod = "POST"
                    setRequestProperty("Content-Type", "application/json")
                    doOutput = true
                    connectTimeout = 30000
                    readTimeout = 30000
                }
                
                // Send request
                connection.outputStream.use { os ->
                    val input = payload.toString().toByteArray(Charsets.UTF_8)
                    os.write(input, 0, input.size)
                }
                
                // Read response
                val responseCode = connection.responseCode
                if (responseCode == HttpURLConnection.HTTP_OK) {
                    val response = connection.inputStream.bufferedReader().use { it.readText() }
                    val jsonResponse = JSONObject(response)
                    val choices = jsonResponse.getJSONArray("choices")
                    if (choices.length() > 0) {
                        val message = choices.getJSONObject(0).getJSONObject("message")
                        message.getString("content").trim()
                    } else {
                        getFallbackResponse(userMessage)
                    }
                } else {
                    getFallbackResponse(userMessage)
                }
            } catch (e: Exception) {
                e.printStackTrace()
                getFallbackResponse(userMessage)
            }
        }
    }
    
    // Fallback responses when API is unavailable
    private fun getFallbackResponse(userMessage: String): String {
        val lowerMessage = userMessage.lowercase()
        
        return when {
            lowerMessage.contains("tds") && lowerMessage.contains("what") -> {
                "TDS (Total Dissolved Solids) measures the concentration of dissolved substances in water, expressed in ppm or mg/L. It includes minerals, salts, and other particles that affect water quality and taste."
            }
            lowerMessage.contains("measure") || lowerMessage.contains("test") -> {
                "To measure TDS:\n1. Use a TDS meter (digital probe)\n2. Measure electrical conductivity\n3. TDS = Conductivity × 0.64 (conversion factor)\n4. Test at 25°C for accuracy\n5. Calibrate your meter regularly"
            }
            lowerMessage.contains("safe") || lowerMessage.contains("good") -> {
                "TDS levels for drinking water:\n• 50-150 ppm: Excellent\n• 150-300 ppm: Good\n• 300-500 ppm: Fair\n• 500-900 ppm: Poor\n• Above 900 ppm: Not recommended\n\nIdeal range is 50-300 ppm for drinking water."
            }
            lowerMessage.contains("reduce") || lowerMessage.contains("filter") -> {
                "Methods to reduce TDS:\n• Reverse Osmosis (RO) - Most effective\n• Distillation\n• Deionization\n• Activated Carbon Filters\n\nRO systems can reduce TDS by 90-95%."
            }
            lowerMessage.contains("high") -> {
                "High TDS can indicate:\n• Excess minerals or salts\n• Hard water\n• Contamination\n• Poor taste\n\nConsider using an RO filter if TDS is above 500 ppm."
            }
            lowerMessage.contains("low") -> {
                "Very low TDS (below 50 ppm) means water lacks essential minerals. While safe, it may:\n• Taste flat\n• Lack beneficial minerals\n• Be slightly acidic\n\nConsider remineralization for optimal health."
            }
            lowerMessage.contains("hello") || lowerMessage.contains("hi") -> {
                "Hello! I'm your AI water quality assistant. I can help you with:\n• Understanding TDS measurements\n• Water quality assessment\n• Testing methods\n• Water treatment options\n\nWhat would you like to know?"
            }
            lowerMessage.contains("thank") -> {
                "You're welcome! Feel free to ask if you have more questions about water quality or TDS. I'm here to help! 💧"
            }
            else -> {
                "I can help you with water quality and TDS information:\n• What is TDS and how to measure it\n• Safe TDS levels for drinking water\n• How to reduce high TDS\n• Water treatment methods\n\nWhat specific information do you need?"
            }
        }
    }
}
