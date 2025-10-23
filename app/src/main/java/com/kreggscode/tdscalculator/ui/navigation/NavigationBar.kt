package com.kreggscode.tdscalculator.ui.navigation

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import com.kreggscode.tdscalculator.ui.theme.*

enum class NavDestination(
    val route: String,
    val icon: ImageVector,
    val label: String
) {
    CALCULATOR("calculator", Icons.Default.Calculate, "Calc"),
    LEARN("learn", Icons.Default.School, "Learn"),
    CHAT("chat", Icons.Default.Chat, "AI Chat"),
    ANALYSIS("analysis", Icons.Default.Analytics, "Analysis"),
    SETTINGS("settings", Icons.Default.Settings, "Settings")
}

@Composable
fun FloatingGlassmorphicNavBar(
    currentDestination: NavDestination,
    onNavigate: (NavDestination) -> Unit,
    modifier: Modifier = Modifier
) {
    val isDark = MaterialTheme.colorScheme.background == DarkBackground
    
    // True glassmorphic effect with transparency and blur
    val glassColor = if (isDark) {
        Color(0xFF1E1E2E).copy(alpha = 0.4f)  // More transparent
    } else {
        Color.White.copy(alpha = 0.5f)  // More transparent
    }
    
    val borderColor = if (isDark) {
        Color.White.copy(alpha = 0.15f)
    } else {
        Color.Black.copy(alpha = 0.08f)
    }
    
    Box(
        modifier = modifier
            .fillMaxWidth()
            .windowInsetsPadding(WindowInsets.navigationBars)
            .padding(horizontal = 16.dp, vertical = 12.dp),
        contentAlignment = Alignment.Center
    ) {
        // Background blur layer
        Box(
            modifier = Modifier
                .height(72.dp)
                .widthIn(max = 400.dp)
                .clip(RoundedCornerShape(36.dp))
                .background(
                    brush = Brush.verticalGradient(
                        colors = listOf(
                            glassColor.copy(alpha = 0.6f),
                            glassColor.copy(alpha = 0.4f)
                        )
                    )
                )
                .border(
                    width = 1.dp,
                    brush = Brush.verticalGradient(
                        colors = listOf(
                            borderColor.copy(alpha = 0.3f),
                            borderColor.copy(alpha = 0.1f)
                        )
                    ),
                    shape = RoundedCornerShape(36.dp)
                )
        ) {
            Row(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 8.dp, vertical = 4.dp),
                horizontalArrangement = Arrangement.SpaceEvenly,
                verticalAlignment = Alignment.CenterVertically
            ) {
                NavDestination.values().forEach { destination ->
                    NavBarItem(
                        destination = destination,
                        selected = currentDestination == destination,
                        onClick = { onNavigate(destination) }
                    )
                }
            }
        }
    }
}

@Composable
fun NavBarItem(
    destination: NavDestination,
    selected: Boolean,
    onClick: () -> Unit
) {
    val scale by animateFloatAsState(
        targetValue = if (selected) 1.1f else 1f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessMedium
        ),
        label = "scale"
    )
    
    val iconColor by animateColorAsState(
        targetValue = if (selected) Color.White else MaterialTheme.colorScheme.onSurfaceVariant,
        animationSpec = tween(300),
        label = "icon_color"
    )
    
    Box(
        modifier = Modifier
            .size(56.dp)
            .scale(scale),
        contentAlignment = Alignment.Center
    ) {
        // Background gradient for selected item
        if (selected) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .clip(CircleShape)
                    .background(
                        Brush.linearGradient(
                            colors = when (destination) {
                                NavDestination.CALCULATOR -> listOf(Indigo, Purple)
                                NavDestination.LEARN -> listOf(Emerald, SuccessTeal)
                                NavDestination.CHAT -> listOf(AITeal, AIIndigo, AIPurple)
                                NavDestination.ANALYSIS -> listOf(Purple, Pink)
                                NavDestination.SETTINGS -> listOf(Amber, WarningRed)
                            }
                        )
                    )
            )
        }
        
        IconButton(
            onClick = onClick,
            modifier = Modifier.fillMaxSize()
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Icon(
                    imageVector = destination.icon,
                    contentDescription = destination.label,
                    tint = iconColor,
                    modifier = Modifier.size(if (selected) 26.dp else 24.dp)
                )
                
                AnimatedVisibility(
                    visible = selected,
                    enter = fadeIn() + expandVertically(),
                    exit = fadeOut() + shrinkVertically()
                ) {
                    Text(
                        text = destination.label,
                        style = MaterialTheme.typography.labelSmall.copy(
                            fontSize = MaterialTheme.typography.labelSmall.fontSize * 0.85f
                        ),
                        color = Color.White,
                        maxLines = 1
                    )
                }
            }
        }
    }
}
