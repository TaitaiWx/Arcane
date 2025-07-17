package com.arcane.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Brightness6
import androidx.compose.material.icons.filled.Error
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.WindowState
import com.arcane.ui.components.DropZone
import com.arcane.ui.components.FileTree
import com.arcane.ui.components.CustomTitleBar
import com.arcane.ui.components.ModernArchivePanel
import com.arcane.ui.components.SettingsDialog
import com.arcane.ui.components.SettingsData
import com.arcane.viewmodel.MainViewModel
import com.arcane.model.ExtractionStatus
import com.arcane.model.ArchiveEntry
import kotlinx.coroutines.delay
import java.io.File

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen(
    onCloseRequest: () -> Unit,
    windowState: WindowState? = null
) {
    val viewModel = remember { MainViewModel() }
    
    val uiState by viewModel.uiState.collectAsState()
    val archiveInfo by viewModel.archiveInfo.collectAsState()
    val extractionProgress by viewModel.extractionProgress.collectAsState()
    
    var isDarkTheme by remember { mutableStateOf(true) }
    var selectedEntries by remember { mutableStateOf<List<ArchiveEntry>>(emptyList()) }
    var showSettings by remember { mutableStateOf(false) }
    var currentSettings by remember { 
        mutableStateOf(
            SettingsData(
                isDarkTheme = true,
                defaultOutputPath = System.getProperty("user.home") + "/Desktop"
            )
        ) 
    }
    
    DisposableEffect(Unit) {
        onDispose {
            viewModel.onDispose()
        }
    }
    
    // 主题背景
    val backgroundGradient = Brush.verticalGradient(
        colors = if (isDarkTheme) {
            listOf(Color(0xFF1A1A1A), Color(0xFF2D2D2D))
        } else {
            listOf(Color(0xFFE3F2FD), Color(0xFFBBDEFB))
        }
    )
    
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(
                backgroundGradient,
            )
    ) {
        // 自定义标题栏
        CustomTitleBar(
            title = "Arcane",
            subtitle = "现代化压缩包解压工具",
            isDarkTheme = isDarkTheme,
            onThemeToggle = { 
                isDarkTheme = !isDarkTheme 
                currentSettings = currentSettings.copy(isDarkTheme = isDarkTheme)
            },
            onCloseRequest = onCloseRequest,
            onSettings = { showSettings = true },
            windowState = windowState
        )
        
        // 主内容区域
        ModernArchivePanel(
            archiveInfo = archiveInfo,
            extractionProgress = extractionProgress,
            selectedEntries = selectedEntries,
            onExtract = { outputDir ->
                val archive = archiveInfo
                val actualOutputDir = if (currentSettings.createSubfolder && archive != null) {
                    val archiveName = archive.name.substringBeforeLast('.')
                    File(outputDir, archiveName).absolutePath
                } else {
                    outputDir
                }
                viewModel.extractArchive(
                    outputDir = File(actualOutputDir),
                    selectedEntries = selectedEntries.takeIf { it.isNotEmpty() }?.map { it.path }
                )
            },
            onClear = {
                viewModel.clearArchive()
                selectedEntries = emptyList()
            },
            onFileDropped = { file ->
                viewModel.onFileDropped(file)
            },
            isDarkTheme = isDarkTheme,
            modifier = Modifier.weight(1f)
        )
        
        // 现代化错误提示
        uiState.errorMessage?.let { error ->
            LaunchedEffect(error) {
                delay(4000)
                viewModel.clearError()
            }
            
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                colors = CardDefaults.cardColors(
                    containerColor = Color(0xFFE53E3E).copy(alpha = 0.95f)
                ),
                shape = RoundedCornerShape(12.dp),
                elevation = CardDefaults.cardElevation(
                    defaultElevation = 8.dp
                )
            ) {
                Row(
                    modifier = Modifier.padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        Icons.Default.Error,
                        contentDescription = "错误",
                        tint = Color.White,
                        modifier = Modifier.size(24.dp)
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                    Text(
                        text = error,
                        color = Color.White,
                        fontWeight = FontWeight.Medium,
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
            }
        }
    }
    
    // 设置对话框
    if (showSettings) {
        SettingsDialog(
            isDarkTheme = isDarkTheme,
            onThemeToggle = { 
                isDarkTheme = !isDarkTheme 
                currentSettings = currentSettings.copy(isDarkTheme = isDarkTheme)
            },
            onDismiss = { showSettings = false },
            defaultOutputPath = currentSettings.defaultOutputPath,
            onOutputPathChange = { path ->
                currentSettings = currentSettings.copy(defaultOutputPath = path)
            },
            onSettingsChange = { newSettings ->
                currentSettings = newSettings
                isDarkTheme = newSettings.isDarkTheme
            }
        )
    }
}
