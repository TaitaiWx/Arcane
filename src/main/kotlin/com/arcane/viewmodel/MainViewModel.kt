package com.arcane.viewmodel

import androidx.compose.runtime.*
import com.arcane.model.*
import com.arcane.service.ArchiveServiceManager
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.io.File

class MainViewModel {
    private val archiveServiceManager = ArchiveServiceManager()
    private val coroutineScope = CoroutineScope(Dispatchers.Main + SupervisorJob())
    
    // UI State
    private val _uiState = MutableStateFlow(MainUiState())
    val uiState: StateFlow<MainUiState> = _uiState.asStateFlow()
    
    // Archive State
    private val _archiveInfo = MutableStateFlow<ArchiveInfo?>(null)
    val archiveInfo: StateFlow<ArchiveInfo?> = _archiveInfo.asStateFlow()
    
    // Extraction State
    private val _extractionProgress = MutableStateFlow(ExtractionProgress(ExtractionStatus.IDLE, 0f))
    val extractionProgress: StateFlow<ExtractionProgress> = _extractionProgress.asStateFlow()
    
    fun onFileDropped(file: File) {
        if (!file.exists()) {
            _uiState.value = _uiState.value.copy(
                errorMessage = "File does not exist: ${file.name}"
            )
            return
        }
        
        _uiState.value = _uiState.value.copy(
            isLoading = true,
            errorMessage = null
        )
        
        coroutineScope.launch {
            try {
                val info = archiveServiceManager.analyzeArchive(file)
                if (info != null) {
                    _archiveInfo.value = info
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        selectedFile = file
                    )
                } else {
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        errorMessage = "Unsupported archive format: ${file.name}"
                    )
                }
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    errorMessage = "Error analyzing archive: ${e.message}"
                )
            }
        }
    }
    
    fun extractArchive(outputDir: File, selectedEntries: List<String>? = null) {
        val currentArchive = _archiveInfo.value
        if (currentArchive == null) {
            _uiState.value = _uiState.value.copy(
                errorMessage = "No archive selected"
            )
            return
        }
        
        val service = archiveServiceManager.getService(currentArchive.type)
        if (service == null) {
            _uiState.value = _uiState.value.copy(
                errorMessage = "No service available for ${currentArchive.type.displayName}"
            )
            return
        }
        
        coroutineScope.launch {
            service.extractArchive(currentArchive.file, outputDir, selectedEntries)
                .collect { progress ->
                    _extractionProgress.value = progress
                }
        }
    }
    
    fun clearError() {
        _uiState.value = _uiState.value.copy(errorMessage = null)
    }
    
    fun resetExtraction() {
        _extractionProgress.value = ExtractionProgress(ExtractionStatus.IDLE, 0f)
    }
    
    fun clearArchive() {
        _archiveInfo.value = null
        _uiState.value = _uiState.value.copy(selectedFile = null)
        resetExtraction()
    }
    
    fun onDispose() {
        coroutineScope.cancel()
    }
}

data class MainUiState(
    val isLoading: Boolean = false,
    val selectedFile: File? = null,
    val errorMessage: String? = null,
    val isDarkTheme: Boolean = false
)
