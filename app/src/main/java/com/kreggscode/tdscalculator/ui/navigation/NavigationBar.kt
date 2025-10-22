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
    CALCULATOR("calculator", Icons.Default.Calculate, "Calculator"),
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
    val glassColor = if (isDark) GlassDark else GlassLight
    val borderColor = if (isDark) BorderDark else BorderLight
    
    Box(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp, vertical = 16.dp),
        contentAlignment = Alignment.Center
    ) {
        Surface(
            modifier = Modifier
                .height(72.dp)
                .border(
                    width = 1.dp,
                    color = borderColor,
                    shape = RoundedCornerShape(36.dp)
                ),
            shape = RoundedCornerShape(36.dp),
            color = glassColor,
            shadowElevation = 12.dp
        ) {
            Row(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 8.dp),
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
                        style = MaterialTheme.typography.labelSmall,
                        color = Color.White,
                        maxLines = 1
                    )
                }
            }
        }
    }
}
