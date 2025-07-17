package com.arcane.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Folder
import androidx.compose.material.icons.filled.InsertDriveFile
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.arcane.model.ArchiveEntry
import com.arcane.model.ArchiveInfo
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun FileTree(
    entries: List<ArchiveEntry>,
    @Suppress("UNUSED_PARAMETER") selectedEntries: List<ArchiveEntry>,
    onSelectionChange: (List<ArchiveEntry>) -> Unit,
    isDarkTheme: Boolean,
    modifier: Modifier = Modifier
) {
    var selectedPaths by remember { mutableStateOf<Set<String>>(emptySet()) }
    
    Column(
        modifier = modifier
            .fillMaxHeight()
            .background(if (isDarkTheme) Color(0xFF1E1E1E) else Color(0xFFF5F5F5))
    ) {
        // Header
        Surface(
            modifier = Modifier.fillMaxWidth(),
            color = if (isDarkTheme) Color(0xFF2A2A2A) else Color(0xFFE0E0E0)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "文件列表",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = if (isDarkTheme) Color.White else Color.Black
                    )
                    Text(
                        text = "${entries.size} 项",
                        fontSize = 14.sp,
                        color = if (isDarkTheme) Color(0xFFB0B0B0) else Color(0xFF666666)
                    )
                }
                
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    TextButton(
                        onClick = {
                            selectedPaths = emptySet()
                            onSelectionChange(emptyList())
                        },
                        colors = ButtonDefaults.textButtonColors(
                            contentColor = Color(0xFF6750A4)
                        )
                    ) {
                        Text("清空", fontSize = 14.sp)
                    }
                    
                    TextButton(
                        onClick = {
                            selectedPaths = entries.map { it.path }.toSet()
                            onSelectionChange(entries)
                        },
                        colors = ButtonDefaults.textButtonColors(
                            contentColor = Color(0xFF6750A4)
                        )
                    ) {
                        Text("全选", fontSize = 14.sp)
                    }
                }
            }
        }
        
        // File list
        LazyColumn(
            modifier = Modifier.fillMaxSize()
        ) {
            items(entries) { entry ->
                FileTreeItem(
                    entry = entry,
                    isSelected = selectedPaths.contains(entry.path),
                    onSelectionChanged = { isSelected ->
                        selectedPaths = if (isSelected) {
                            selectedPaths + entry.path
                        } else {
                            selectedPaths - entry.path
                        }
                        onSelectionChange(entries.filter { selectedPaths.contains(it.path) })
                    },
                    isDarkTheme = isDarkTheme
                )
            }
        }
    }
}

@Composable
private fun FileTreeItem(
    entry: ArchiveEntry,
    isSelected: Boolean,
    onSelectionChanged: (Boolean) -> Unit,
    isDarkTheme: Boolean
) {
    val dateFormat = remember { SimpleDateFormat("MMM dd, yyyy HH:mm", Locale.getDefault()) }
    
    Surface(
        modifier = Modifier.fillMaxWidth(),
        color = if (isSelected) {
            if (isDarkTheme) Color(0xFF2A2A2A) else Color(0xFFE3F2FD)
        } else {
            Color.Transparent
        }
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Checkbox(
                checked = isSelected,
                onCheckedChange = onSelectionChanged,
                colors = CheckboxDefaults.colors(
                    checkedColor = Color(0xFF6750A4),
                    uncheckedColor = Color(0xFF606060)
                )
            )
            
            Spacer(modifier = Modifier.width(12.dp))
            
            Icon(
                imageVector = if (entry.isDirectory) Icons.Default.Folder else Icons.Default.InsertDriveFile,
                contentDescription = if (entry.isDirectory) "Folder" else "File",
                tint = if (entry.isDirectory) {
                    Color(0xFF6750A4)
                } else {
                    Color(0xFF909090)
                }
            )
            
            Spacer(modifier = Modifier.width(16.dp))
            
            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = entry.name,
                    fontSize = 15.sp,
                    fontWeight = if (entry.isDirectory) FontWeight.Medium else FontWeight.Normal,
                    color = if (isDarkTheme) Color.White else Color.Black
                )
                
                Row(
                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    if (!entry.isDirectory) {
                        Text(
                            text = formatFileSize(entry.size),
                            fontSize = 12.sp,
                            color = if (isDarkTheme) Color(0xFFB0B0B0) else Color(0xFF666666)
                        )
                    }
                    
                    entry.lastModified?.let { date ->
                        Text(
                            text = dateFormat.format(date),
                            fontSize = 12.sp,
                            color = if (isDarkTheme) Color(0xFFB0B0B0) else Color(0xFF666666)
                        )
                    }
                }
            }
        }
    }
}

private fun formatFileSize(bytes: Long): String {
    if (bytes < 1024) return "$bytes B"
    val kb = bytes / 1024.0
    if (kb < 1024) return String.format("%.1f KB", kb)
    val mb = kb / 1024.0
    if (mb < 1024) return String.format("%.1f MB", mb)
    val gb = mb / 1024.0
    return String.format("%.1f GB", gb)
}
