package com.arcane.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Folder
import androidx.compose.material.icons.filled.FolderOpen
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import java.io.File

enum class FileChooserMode {
    FILES_ONLY,
    DIRECTORIES_ONLY,
    FILES_AND_DIRECTORIES
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ModernFileChooser(
    title: String = "选择文件",
    mode: FileChooserMode = FileChooserMode.FILES_ONLY,
    onFileSelected: (String) -> Unit,
    onDismiss: () -> Unit,
    fileFilter: (File) -> Boolean = { true },
    isDarkTheme: Boolean = true,
    initialDirectory: String = System.getProperty("user.dir")
) {
    var currentDirectory by remember { 
        mutableStateOf(File(initialDirectory)) 
    }
    var selectedFile by remember { mutableStateOf<File?>(null) }
    
    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(
            dismissOnClickOutside = true,
            dismissOnBackPress = true
        )
    ) {
        Surface(
            modifier = Modifier
                .width(700.dp)
                .height(500.dp),
            shape = RoundedCornerShape(16.dp),
            color = if (isDarkTheme) Color(0xFF2D3748) else Color(0xFFFFFFFF),
            shadowElevation = 8.dp
        ) {
            Column(
                modifier = Modifier.fillMaxSize()
            ) {
                // 标题栏
                Surface(
                    modifier = Modifier.fillMaxWidth(),
                    color = if (isDarkTheme) Color(0xFF1A202C) else Color(0xFFF7FAFC),
                    shape = RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = title,
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            color = if (isDarkTheme) Color.White else Color.Black
                        )
                        
                        IconButton(
                            onClick = onDismiss,
                            modifier = Modifier.size(32.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Close,
                                contentDescription = "关闭",
                                tint = if (isDarkTheme) Color.White else Color.Black
                            )
                        }
                    }
                }
                
                // 路径导航
                Surface(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 8.dp),
                    color = if (isDarkTheme) Color(0xFF4A5568) else Color(0xFFE2E8F0),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        IconButton(
                            onClick = {
                                currentDirectory = File(System.getProperty("user.home"))
                                selectedFile = null
                            },
                            modifier = Modifier.size(32.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Home,
                                contentDescription = "主目录",
                                tint = if (isDarkTheme) Color.White else Color.Black
                            )
                        }
                        
                        Text(
                            text = currentDirectory.absolutePath,
                            fontSize = 14.sp,
                            color = if (isDarkTheme) Color.White else Color.Black,
                            modifier = Modifier.weight(1f)
                        )
                        
                        if (currentDirectory.parent != null) {
                            IconButton(
                                onClick = {
                                    currentDirectory = currentDirectory.parentFile
                                    selectedFile = null
                                },
                                modifier = Modifier.size(32.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.ArrowBack,
                                    contentDescription = "上级目录",
                                    tint = if (isDarkTheme) Color.White else Color.Black
                                )
                            }
                        }
                    }
                }
                
                // 文件列表
                LazyColumn(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f)
                        .padding(horizontal = 16.dp),
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    val files = currentDirectory.listFiles()?.sortedWith(
                        compareBy<File> { !it.isDirectory() }.thenBy { it.name.lowercase() }
                    ) ?: emptyList()
                    
                    items(files) { file ->
                        val isSelectable = when (mode) {
                            FileChooserMode.FILES_ONLY -> !file.isDirectory && fileFilter(file)
                            FileChooserMode.DIRECTORIES_ONLY -> file.isDirectory
                            FileChooserMode.FILES_AND_DIRECTORIES -> fileFilter(file)
                        }
                        
                        FileItem(
                            file = file,
                            onClick = {
                                if (file.isDirectory && mode != FileChooserMode.DIRECTORIES_ONLY) {
                                    currentDirectory = file
                                    selectedFile = null
                                } else if (isSelectable) {
                                    if (mode == FileChooserMode.FILES_ONLY && !file.isDirectory) {
                                        onFileSelected(file.absolutePath)
                                    } else if (mode == FileChooserMode.DIRECTORIES_ONLY && file.isDirectory) {
                                        selectedFile = file
                                    } else {
                                        selectedFile = file
                                    }
                                }
                            },
                            isSelected = selectedFile == file,
                            isSelectable = isSelectable,
                            isDarkTheme = isDarkTheme
                        )
                    }
                }
                
                // 底部按钮
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    horizontalArrangement = Arrangement.spacedBy(12.dp, Alignment.End)
                ) {
                    OutlinedButton(
                        onClick = onDismiss,
                        colors = ButtonDefaults.outlinedButtonColors(
                            contentColor = if (isDarkTheme) Color.White else Color.Black
                        )
                    ) {
                        Text("取消")
                    }
                    
                    if (mode == FileChooserMode.DIRECTORIES_ONLY) {
                        Button(
                            onClick = {
                                val directoryToSelect = selectedFile ?: currentDirectory
                                onFileSelected(directoryToSelect.absolutePath)
                            },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = Color(0xFF4FC3F7)
                            )
                        ) {
                            Text(
                                text = if (selectedFile != null) "选择此文件夹" else "选择当前文件夹",
                                color = Color.White
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun FileItem(
    file: File,
    onClick: () -> Unit,
    isSelected: Boolean = false,
    isSelectable: Boolean,
    isDarkTheme: Boolean
) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        color = if (isSelected) {
            if (isDarkTheme) Color(0xFF4FC3F7).copy(alpha = 0.3f) else Color(0xFF1976D2).copy(alpha = 0.3f)
        } else if (isDarkTheme) {
            Color(0xFF4A5568)
        } else {
            Color(0xFFF7FAFC)
        },
        shape = RoundedCornerShape(8.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Icon(
                imageVector = if (file.isDirectory) Icons.Default.Folder else Icons.Default.Description,
                contentDescription = if (file.isDirectory) "文件夹" else "文件",
                tint = if (file.isDirectory) {
                    if (isDarkTheme) Color(0xFF4FC3F7) else Color(0xFF1976D2)
                } else {
                    if (isSelectable) {
                        if (isDarkTheme) Color(0xFF48BB78) else Color(0xFF38A169)
                    } else {
                        if (isDarkTheme) Color(0xFF718096) else Color(0xFF4A5568)
                    }
                },
                modifier = Modifier.size(20.dp)
            )
            
            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = file.name,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Medium,
                    color = if (isDarkTheme) Color.White else Color.Black
                )
                
                if (!file.isDirectory) {
                    Text(
                        text = formatFileSize(file.length()),
                        fontSize = 12.sp,
                        color = if (isDarkTheme) Color(0xFFB0B0B0) else Color(0xFF666666)
                    )
                }
            }
        }
    }
}

private fun formatFileSize(bytes: Long): String {
    val kb = bytes / 1024.0
    val mb = kb / 1024.0
    val gb = mb / 1024.0
    
    return when {
        gb >= 1 -> String.format("%.2f GB", gb)
        mb >= 1 -> String.format("%.2f MB", mb)
        kb >= 1 -> String.format("%.2f KB", kb)
        else -> "$bytes B"
    }
}
