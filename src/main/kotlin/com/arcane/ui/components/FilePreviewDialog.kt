package com.arcane.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Image
import androidx.compose.material.icons.filled.TextSnippet
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import java.io.File
import java.io.InputStream

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FilePreviewDialog(
    fileName: String,
    fileContent: ByteArray,
    onDismiss: () -> Unit,
    isDarkTheme: Boolean = true
) {
    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(
            dismissOnBackPress = true,
            dismissOnClickOutside = true
        )
    ) {
        Surface(
            modifier = Modifier
                .fillMaxWidth(0.8f)
                .fillMaxHeight(0.8f),
            color = if (isDarkTheme) Color(0xFF1E1E1E) else Color(0xFFFFFFFF),
            shape = RoundedCornerShape(16.dp),
            shadowElevation = 8.dp
        ) {
            Column {
                // 标题栏
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(
                            if (isDarkTheme) Color(0xFF2A2A2A) else Color(0xFFF5F5F5)
                        )
                        .padding(16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = fileName,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = if (isDarkTheme) Color.White else Color.Black,
                        modifier = Modifier.weight(1f)
                    )
                    
                    IconButton(
                        onClick = onDismiss,
                        modifier = Modifier.size(32.dp)
                    ) {
                        Icon(
                            Icons.Default.Close,
                            contentDescription = "关闭",
                            tint = if (isDarkTheme) Color.White else Color.Black
                        )
                    }
                }
                
                // 内容区域
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(16.dp),
                    contentAlignment = Alignment.Center
                ) {
                    when {
                        isImageFile(fileName) -> {
                            ImagePreview(fileContent, isDarkTheme)
                        }
                        isTextFile(fileName) -> {
                            TextPreview(fileContent, isDarkTheme)
                        }
                        else -> {
                            UnsupportedPreview(fileName, isDarkTheme)
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun ImagePreview(
    fileContent: ByteArray,
    isDarkTheme: Boolean
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Icon(
            Icons.Default.Image,
            contentDescription = "图片预览",
            tint = if (isDarkTheme) Color(0xFF4FC3F7) else Color(0xFF1976D2),
            modifier = Modifier.size(48.dp)
        )
        
        Text(
            text = "图片预览功能",
            fontSize = 16.sp,
            color = if (isDarkTheme) Color.White else Color.Black,
            fontWeight = FontWeight.Medium
        )
        
        Text(
            text = "图片大小: ${formatFileSize(fileContent.size.toLong())}",
            fontSize = 14.sp,
            color = if (isDarkTheme) Color(0xFFB0B0B0) else Color(0xFF666666)
        )
        
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .height(200.dp),
            colors = CardDefaults.cardColors(
                containerColor = if (isDarkTheme) Color(0xFF2A2A2A) else Color(0xFFF8F9FA)
            ),
            shape = RoundedCornerShape(8.dp)
        ) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "图片预览占位符\n(需要实现具体的图片解码逻辑)",
                    fontSize = 12.sp,
                    color = if (isDarkTheme) Color(0xFF999999) else Color(0xFF666666),
                    textAlign = androidx.compose.ui.text.style.TextAlign.Center
                )
            }
        }
    }
}

@Composable
private fun TextPreview(
    fileContent: ByteArray,
    isDarkTheme: Boolean
) {
    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Icon(
                Icons.Default.TextSnippet,
                contentDescription = "文本预览",
                tint = if (isDarkTheme) Color(0xFF4FC3F7) else Color(0xFF1976D2),
                modifier = Modifier.size(24.dp)
            )
            
            Text(
                text = "文本预览",
                fontSize = 16.sp,
                color = if (isDarkTheme) Color.White else Color.Black,
                fontWeight = FontWeight.Medium
            )
        }
        
        Card(
            modifier = Modifier.fillMaxSize(),
            colors = CardDefaults.cardColors(
                containerColor = if (isDarkTheme) Color(0xFF2A2A2A) else Color(0xFFF8F9FA)
            ),
            shape = RoundedCornerShape(8.dp)
        ) {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                try {
                    val text = String(fileContent, Charsets.UTF_8)
                    val lines = text.lines()
                    
                    items(lines.size) { index ->
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Text(
                                text = "${index + 1}",
                                fontSize = 12.sp,
                                color = if (isDarkTheme) Color(0xFF666666) else Color(0xFF999999),
                                fontFamily = FontFamily.Monospace,
                                modifier = Modifier.widthIn(min = 40.dp)
                            )
                            
                            Text(
                                text = lines[index],
                                fontSize = 13.sp,
                                color = if (isDarkTheme) Color.White else Color.Black,
                                fontFamily = FontFamily.Monospace,
                                modifier = Modifier.weight(1f)
                            )
                        }
                    }
                } catch (e: Exception) {
                    item {
                        Text(
                            text = "无法解析文本内容: ${e.message}",
                            fontSize = 14.sp,
                            color = if (isDarkTheme) Color(0xFFFF5252) else Color(0xFFD32F2F)
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun UnsupportedPreview(
    fileName: String,
    isDarkTheme: Boolean
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Icon(
            Icons.Default.TextSnippet,
            contentDescription = "不支持的文件类型",
            tint = if (isDarkTheme) Color(0xFF666666) else Color(0xFF999999),
            modifier = Modifier.size(48.dp)
        )
        
        Text(
            text = "不支持预览此文件类型",
            fontSize = 16.sp,
            color = if (isDarkTheme) Color.White else Color.Black,
            fontWeight = FontWeight.Medium
        )
        
        Text(
            text = "文件: $fileName",
            fontSize = 14.sp,
            color = if (isDarkTheme) Color(0xFFB0B0B0) else Color(0xFF666666)
        )
        
        Text(
            text = "支持的预览格式:\n• 图片: jpg, jpeg, png, gif, bmp\n• 文本: txt, md, json, xml, csv",
            fontSize = 12.sp,
            color = if (isDarkTheme) Color(0xFF999999) else Color(0xFF666666),
            textAlign = androidx.compose.ui.text.style.TextAlign.Center
        )
    }
}

private fun isImageFile(fileName: String): Boolean {
    val imageExtensions = listOf("jpg", "jpeg", "png", "gif", "bmp", "webp")
    return imageExtensions.any { fileName.lowercase().endsWith(".$it") }
}

private fun isTextFile(fileName: String): Boolean {
    val textExtensions = listOf("txt", "md", "json", "xml", "csv", "log", "conf", "config", "properties")
    return textExtensions.any { fileName.lowercase().endsWith(".$it") }
}

private fun formatFileSize(size: Long): String {
    val units = arrayOf("B", "KB", "MB", "GB", "TB")
    var bytes = size.toDouble()
    var unitIndex = 0
    
    while (bytes >= 1024 && unitIndex < units.size - 1) {
        bytes /= 1024
        unitIndex++
    }
    
    return String.format("%.1f %s", bytes, units[unitIndex])
}
