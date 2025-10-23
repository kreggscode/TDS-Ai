package com.kreggscode.tdscalculator.ui.screens

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.kreggscode.tdscalculator.ui.viewmodels.TDSViewModel

/**
 * Analysis Screen - Redirects to TDS Tracker Screen
 * Displays water quality trends, graphs, and historical measurements
 */
@Composable
fun AnalysisScreen(
    viewModel: TDSViewModel,
    modifier: Modifier = Modifier
) {
    TDSTrackerScreen(viewModel = viewModel, modifier = modifier)
}
