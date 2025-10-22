package com.kreggscode.tdscalculator.ui.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kreggscode.tdscalculator.data.models.*
import com.kreggscode.tdscalculator.data.preferences.PreferencesManager
import com.kreggscode.tdscalculator.data.repository.TDSRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class TDSViewModel @Inject constructor(
    private val repository: TDSRepository,
    private val preferencesManager: PreferencesManager
) : ViewModel() {
    
    // Calculator State
    private val _grossIncome = MutableStateFlow("")
    val grossIncome: StateFlow<String> = _grossIncome.asStateFlow()
    
    private val _deductions80C = MutableStateFlow("")
    val deductions80C: StateFlow<String> = _deductions80C.asStateFlow()
    
    private val _otherDeductions = MutableStateFlow("")
    val otherDeductions: StateFlow<String> = _otherDeductions.asStateFlow()
    
    private val _calculationResult = MutableStateFlow<TDSCalculation?>(null)
    val calculationResult: StateFlow<TDSCalculation?> = _calculationResult.asStateFlow()
    
    private val _isCalculating = MutableStateFlow(false)
    val isCalculating: StateFlow<Boolean> = _isCalculating.asStateFlow()
    
    // Chat State
    private val _chatMessages = MutableStateFlow<List<ChatMessage>>(
        listOf(
            ChatMessage(
                text = "Hello! I'm your AI TDS assistant. How can I help you today?",
                isUser = false
            )
        )
    )
    val chatMessages: StateFlow<List<ChatMessage>> = _chatMessages.asStateFlow()
    
    private val _isTyping = MutableStateFlow(false)
    val isTyping: StateFlow<Boolean> = _isTyping.asStateFlow()
    
    private val _currentMessage = MutableStateFlow("")
    val currentMessage: StateFlow<String> = _currentMessage.asStateFlow()
    
    // Learning State
    private val _tdsInfoList = MutableStateFlow<List<TDSInfo>>(emptyList())
    val tdsInfoList: StateFlow<List<TDSInfo>> = _tdsInfoList.asStateFlow()
    
    // Settings State
    val themeMode: StateFlow<ThemeMode> = preferencesManager.themeMode
        .stateIn(viewModelScope, SharingStarted.Eagerly, ThemeMode.SYSTEM)
    
    val notificationsEnabled: StateFlow<Boolean> = preferencesManager.notificationsEnabled
        .stateIn(viewModelScope, SharingStarted.Eagerly, true)
    
    init {
        loadTDSInformation()
    }
    
    // Calculator Functions
    fun updateGrossIncome(value: String) {
        _grossIncome.value = value.filter { it.isDigit() }
    }
    
    fun updateDeductions80C(value: String) {
        _deductions80C.value = value.filter { it.isDigit() }
    }
    
    fun updateOtherDeductions(value: String) {
        _otherDeductions.value = value.filter { it.isDigit() }
    }
    
    fun calculateTDS() {
        viewModelScope.launch {
            _isCalculating.value = true
            delay(500) // Simulate calculation time for animation
            
            val income = _grossIncome.value.toDoubleOrNull() ?: 0.0
            val deductions80C = _deductions80C.value.toDoubleOrNull() ?: 0.0
            val otherDeductions = _otherDeductions.value.toDoubleOrNull() ?: 0.0
            
            val result = repository.calculateTDS(income, deductions80C, otherDeductions)
            _calculationResult.value = result
            
            _isCalculating.value = false
        }
    }
    
    fun resetCalculator() {
        _grossIncome.value = ""
        _deductions80C.value = ""
        _otherDeductions.value = ""
        _calculationResult.value = null
    }
    
    // Chat Functions
    fun updateCurrentMessage(message: String) {
        _currentMessage.value = message
    }
    
    fun sendMessage() {
        val message = _currentMessage.value.trim()
        if (message.isEmpty()) return
        
        viewModelScope.launch {
            // Add user message
            val userMessage = ChatMessage(text = message, isUser = true)
            _chatMessages.value = _chatMessages.value + userMessage
            _currentMessage.value = ""
            
            // Show typing indicator
            _isTyping.value = true
            delay(1000) // Simulate AI thinking
            
            // Get AI response
            val aiResponse = repository.getAIResponse(message)
            val aiMessage = ChatMessage(text = aiResponse, isUser = false)
            
            _isTyping.value = false
            _chatMessages.value = _chatMessages.value + aiMessage
        }
    }
    
    // Learning Functions
    private fun loadTDSInformation() {
        _tdsInfoList.value = repository.getTDSInformation()
    }
    
    fun toggleInfoExpansion(index: Int) {
        _tdsInfoList.value = _tdsInfoList.value.mapIndexed { i, info ->
            if (i == index) info.copy(isExpanded = !info.isExpanded)
            else info
        }
    }
    
    // Settings Functions
    fun setThemeMode(mode: ThemeMode) {
        viewModelScope.launch {
            preferencesManager.setThemeMode(mode)
        }
    }
    
    fun setNotificationsEnabled(enabled: Boolean) {
        viewModelScope.launch {
            preferencesManager.setNotificationsEnabled(enabled)
        }
    }
}
