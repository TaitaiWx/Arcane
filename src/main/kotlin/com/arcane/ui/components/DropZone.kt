package com.arcane.ui.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CloudUpload
import androidx.compose.material.icons.filled.FolderOpen
import androidx.compose.material.icons.outlined.Archive
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import java.io.File

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DropZone(
    modifier: Modifier = Modifier,
    onFileDropped: (File) -> Unit,
    isLoading: Boolean = false
) {
    var isDragOver by remember { mutableStateOf(false) }
    var showFileChooser by remember { mutableStateOf(false) }
    
    // 动画效果
    val animatedScale by animateFloatAsState(
        targetValue = if (isDragOver) 1.05f else 1f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessLow
        )
    )
    
    val backgroundColor by animateColorAsState(
        targetValue = if (isDragOver) {
            Color(0xFF2A2A2A)
        } else {
            Color(0xFF1A1A1A)
        },
        animationSpec = tween(300)
    )
    
    val borderColor by animateColorAsState(
        targetValue = if (isDragOver) {
            Color(0xFF4FC3F7)
        } else {
            Color(0xFF404040)
        },
        animationSpec = tween(300)
    )
    
    Box(
        modifier = modifier
            .fillMaxSize()
            .scale(animatedScale)
            .background(
                brush = Brush.radialGradient(
                    colors = listOf(
                        backgroundColor.copy(alpha = 0.8f),
                        backgroundColor
                    ),
                    radius = 800f
                )
            )
            .border(
                width = if (isDragOver) 3.dp else 2.dp,
                color = borderColor,
                shape = RoundedCornerShape(16.dp)
            )
            .clip(RoundedCornerShape(16.dp)),
        contentAlignment = Alignment.Center
    ) {
        if (isLoading) {
            LoadingContent()
        } else {
            EmptyStateContent(
                onSelectFile = { showFileChooser = true }
            )
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
private fun LoadingContent() {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        CircularProgressIndicator(
            modifier = Modifier.size(48.dp),
            color = Color(0xFF4FC3F7),
            strokeWidth = 4.dp
        )
        
        Text(
            text = "正在分析压缩文件...",
            fontSize = 16.sp,
            fontWeight = FontWeight.Medium,
            color = Color.White,
            textAlign = TextAlign.Center
        )
    }
}

@Composable
private fun EmptyStateContent(
    onSelectFile: () -> Unit
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(24.dp)
    ) {
        // 主图标
        Box(
            modifier = Modifier
                .size(120.dp)
                .background(
                    brush = Brush.radialGradient(
                        colors = listOf(
                            Color(0xFF4FC3F7).copy(alpha = 0.2f),
                            Color.Transparent
                        )
                    ),
                    shape = RoundedCornerShape(60.dp)
                ),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Outlined.Archive,
                contentDescription = "压缩文件",
                tint = Color(0xFF4FC3F7),
                modifier = Modifier.size(64.dp)
            )
        }
        
        // 文字说明
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text(
                text = "选择压缩文件开始解压",
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White,
                textAlign = TextAlign.Center
            )
            
            Text(
                text = "支持 ZIP、7Z、RAR、TAR.GZ 等格式",
                fontSize = 14.sp,
                color = Color(0xFFB0B0B0),
                textAlign = TextAlign.Center
            )
        }
        
        // 选择文件按钮
        Button(
            onClick = onSelectFile,
            colors = ButtonDefaults.buttonColors(
                containerColor = Color(0xFF4FC3F7)
            ),
            shape = RoundedCornerShape(12.dp),
            modifier = Modifier
                .height(48.dp)
                .padding(horizontal = 24.dp)
        ) {
            Icon(
                imageVector = Icons.Default.FolderOpen,
                contentDescription = "选择文件",
                tint = Color.White,
                modifier = Modifier.size(20.dp)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = "选择文件",
                color = Color.White,
                fontSize = 16.sp,
                fontWeight = FontWeight.Medium
            )
        }
        
        // 支持格式提示
        Row(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            listOf("ZIP", "7Z", "RAR", "TAR.GZ").forEach { format ->
                Surface(
                    color = Color(0xFF2A2A2A),
                    shape = RoundedCornerShape(6.dp)
                ) {
                    Text(
                        text = format,
                        color = Color(0xFF4FC3F7),
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Medium,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                    )
                }
            }
        }
    }
}
