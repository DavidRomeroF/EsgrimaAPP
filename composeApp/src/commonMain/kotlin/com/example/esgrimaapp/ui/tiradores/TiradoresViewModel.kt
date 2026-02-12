package com.example.esgrimaapp.ui.tiradores

import cafe.adriel.voyager.core.model.ScreenModel
import com.example.esgrimaapp.ui.competicion.CompeticionUIState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

class TiradoresViewModel: ScreenModel {
    private val _uiState = MutableStateFlow(TiradoresUIState())
    val uiState = _uiState.asStateFlow()

}