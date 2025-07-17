package com.arcane.ui.components

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.BorderStroke
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.arcane.model.ArchiveInfo
import com.arcane.model.ArchiveEntry
import com.arcane.model.ExtractionProgress
import com.arcane.ui.components.archive.ArchiveInfoBar
import com.arcane.ui.components.archive.DarkFileBrowser
import com.arcane.ui.components.archive.DarkFileDetails
import com.arcane.ui.components.archive.DarkActionBar
import com.arcane.ui.components.shared.ArchiveColors
import com.arcane.ui.components.shared.ViewMode
import com.arcane.ui.components.ModernFileChooser
import com.arcane.ui.components.FileChooserMode
import java.io.File

@OptIn(ExperimentalMaterial3Api::class, ExperimentalAnimationApi::class)
@Composable
fun ModernArchivePanel(
    archiveInfo: ArchiveInfo?,
    extractionProgress: ExtractionProgress?,
    @Suppress("UNUSED_PARAMETER") selectedEntries: List<ArchiveEntry>,
    onExtract: (String) -> Unit,
    onClear: () -> Unit,
    onFileDropped: (File) -> Unit,
    @Suppress("UNUSED_PARAMETER") isDarkTheme: Boolean,
    modifier: Modifier = Modifier
) {
    // 动画状态
    val animatedVisibility = remember { Animatable(0f) }
    
    LaunchedEffect(archiveInfo) {
        animatedVisibility.animateTo(1f, animationSpec = tween(800))
    }

    if (archiveInfo != null) {
        // 暗黑模式无padding界面设计
        DarkModeLayout(
            archiveInfo = archiveInfo,
            extractionProgress = extractionProgress,
            selectedEntries = selectedEntries,
            onExtract = onExtract,
            onClear = onClear,
            animatedVisibility = animatedVisibility.value,
            modifier = modifier
        )
    } else {
        // 重新设计的拖拽区域 - 无padding
        DarkDropZoneLayout(
            onFileDropped = onFileDropped,
            modifier = modifier
        )
    }
}

@Composable
private fun DarkDropZoneLayout(
    onFileDropped: (File) -> Unit,
    modifier: Modifier = Modifier
) {
    var showFileChooser by remember { mutableStateOf(false) }
    
    Box(
        modifier = modifier
            .fillMaxSize()
            .background(ArchiveColors.DarkestBackground),
        contentAlignment = Alignment.Center
    ) {
        // 霓虹拖拽区域
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(20.dp)
        ) {
            // 霓虹图标
            Surface(
                modifier = Modifier.size(120.dp),
                shape = RoundedCornerShape(20.dp),
                color = ArchiveColors.DarkSurface,
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(
                        imageVector = Icons.Rounded.UploadFile,
                        contentDescription = null,
                        tint = ArchiveColors.PrimaryNeon,
                        modifier = Modifier.size(60.dp)
                    )
                }
            }
            
            // 霓虹文本
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text(
                    text = "拖拽压缩文件到此处",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = ArchiveColors.TextBright
                )
                Text(
                    text = "或点击按钮选择文件",
                    fontSize = 16.sp,
                    color = ArchiveColors.SecondaryNeon,
                    textAlign = TextAlign.Center
                )
                Text(
                    text = "支持 ZIP、RAR、7Z、TAR.GZ 格式",
                    fontSize = 14.sp,
                    color = ArchiveColors.TextDim,
                    textAlign = TextAlign.Center
                )
            }
            
            // 文件选择按钮
            Button(
                onClick = { showFileChooser = true },
                colors = ButtonDefaults.buttonColors(
                    containerColor = ArchiveColors.PrimaryNeon.copy(alpha = 0.2f),
                    contentColor = ArchiveColors.PrimaryNeon
                ),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier
                    .height(50.dp)
                    .width(180.dp),
                border = BorderStroke(1.dp, ArchiveColors.PrimaryNeon.copy(alpha = 0.8f))
            ) {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Rounded.FolderOpen,
                        contentDescription = null,
                        modifier = Modifier.size(20.dp)
                    )
                    Text(
                        text = "选择文件",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
            
            // 支持格式标签
            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                listOf("ZIP", "RAR", "7Z", "TAR.GZ").forEach { format ->
                    Surface(
                        color = ArchiveColors.DarkSurface,
                        shape = RoundedCornerShape(6.dp),
                        border = BorderStroke(1.dp, ArchiveColors.BorderGlow.copy(alpha = 0.3f))
                    ) {
                        Text(
                            text = format,
                            color = ArchiveColors.SecondaryNeon,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Medium,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                        )
                    }
                }
            }
        }
    }
    
    // 文件选择对话框
    if (showFileChooser) {
        ModernFileChooser(
            title = "选择压缩文件",
            mode = FileChooserMode.FILES_ONLY,
            onFileSelected = { filePath ->
                onFileDropped(File(filePath))
                showFileChooser = false
            },
            onDismiss = { showFileChooser = false },
            fileFilter = { file ->
                val fileName = file.name.lowercase()
                fileName.endsWith(".zip") || 
                fileName.endsWith(".7z") || 
                fileName.endsWith(".rar") || 
                fileName.endsWith(".tar.gz") || 
                fileName.endsWith(".tgz") ||
                fileName.endsWith(".tar")
            },
            isDarkTheme = true,
            initialDirectory = System.getProperty("user.home")
        )
    }
}

@Composable
private fun DarkModeLayout(
    archiveInfo: ArchiveInfo,
    extractionProgress: ExtractionProgress?,
    @Suppress("UNUSED_PARAMETER") selectedEntries: List<ArchiveEntry>,
    onExtract: (String) -> Unit,
    onClear: () -> Unit,
    animatedVisibility: Float,
    modifier: Modifier = Modifier
) {
    // 视图模式状态
    var viewMode by remember { mutableStateOf(ViewMode.LIST) }
    
    // 选中文件状态
    var selectedEntry by remember(archiveInfo) { mutableStateOf<ArchiveEntry?>(null) }
    
    // 动画状态
    val borderAlpha by animateFloatAsState(
        targetValue = animatedVisibility,
        animationSpec = tween(1000)
    )
    
    // 提取进度状态
    val isExtracting = extractionProgress != null
    
    Column(
        modifier = modifier
            .fillMaxSize()
            .background(ArchiveColors.DarkestBackground)
            .alpha(animatedVisibility)
            .scale(0.95f + 0.05f * animatedVisibility),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        // 档案信息栏
        ArchiveInfoBar(
            archiveInfo = archiveInfo,
            viewMode = viewMode,
            onViewModeChanged = { viewMode = it },
            borderAlpha = borderAlpha,
            modifier = Modifier.fillMaxWidth()
        )
        
        // 主要内容区域
        Row(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // 文件浏览器
            DarkFileBrowser(
                entries = archiveInfo.entries,
                selectedEntry = selectedEntry,
                onEntrySelected = { selectedEntry = it },
                viewMode = viewMode,
                borderAlpha = borderAlpha,
                modifier = Modifier.weight(2f)
            )
            
            // 文件详情
            DarkFileDetails(
                selectedEntry = selectedEntry,
                archiveInfo = archiveInfo,
                borderAlpha = borderAlpha,
                modifier = Modifier.weight(1f)
            )
        }
        
        // 操作栏
        DarkActionBar(
            onExtract = { onExtract(System.getProperty("user.dir")) },
            onClear = onClear,
            isExtracting = isExtracting,
            borderAlpha = borderAlpha,
            modifier = Modifier.fillMaxWidth()
        )
        
        // 提取进度
        extractionProgress?.let { progress ->
            DarkExtractionProgress(
                progress = progress,
                borderAlpha = borderAlpha,
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}

@Composable
private fun DarkExtractionProgress(
    progress: ExtractionProgress,
    borderAlpha: Float,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier,
        color = ArchiveColors.DarkCard,
        shape = RoundedCornerShape(8.dp),
        shadowElevation = (4 * borderAlpha).dp
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "解压进度",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    color = ArchiveColors.TextBright
                )
                Text(
                    text = "${(progress.progress * 100).toInt()}%",
                    fontSize = 12.sp,
                    color = ArchiveColors.PrimaryNeon,
                    fontWeight = FontWeight.Bold
                )
            }
            
            // 进度条
            LinearProgressIndicator(
                progress = progress.progress,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(6.dp),
                color = ArchiveColors.PrimaryNeon,
                trackColor = ArchiveColors.DarkSurface,
                strokeCap = androidx.compose.ui.graphics.StrokeCap.Round
            )
            
            Text(
                text = progress.currentFile,
                fontSize = 10.sp,
                color = ArchiveColors.TextDim,
                maxLines = 1
            )
        }
    }
}
