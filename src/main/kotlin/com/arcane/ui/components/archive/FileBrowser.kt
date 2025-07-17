package com.arcane.ui.components.archive

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.BorderStroke
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.arcane.model.ArchiveEntry
import com.arcane.ui.components.shared.ArchiveColors
import com.arcane.ui.components.shared.FileTypeUtils
import com.arcane.ui.components.shared.ViewMode

@Composable
fun DarkFileBrowser(
    entries: List<ArchiveEntry>,
    selectedEntry: ArchiveEntry?,
    onEntrySelected: (ArchiveEntry) -> Unit,
    viewMode: ViewMode,
    borderAlpha: Float,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier,
        color = ArchiveColors.DarkCard,
        shape = RoundedCornerShape(8.dp)
    ) {
        Column(
            modifier = Modifier.fillMaxSize()
        ) {
            // 标题栏
            Surface(
                modifier = Modifier.fillMaxWidth(),
                color = ArchiveColors.DarkSurface,
                shape = RoundedCornerShape(0.dp)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(40.dp)
                        .padding(horizontal = 12.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "文件浏览器",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = ArchiveColors.TextBright
                    )
                    
                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = ArchiveColors.SecondaryNeon.copy(alpha = 0.2f),
                    ) {
                        Text(
                            text = "${entries.size}",
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Medium,
                            color = ArchiveColors.SecondaryNeon,
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                        )
                    }
                }
            }
            
            // 文件列表/网格
            when (viewMode) {
                ViewMode.GRID -> {
                    LazyVerticalGrid(
                        columns = GridCells.Adaptive(minSize = 100.dp),
                        verticalArrangement = Arrangement.spacedBy(1.dp),
                        horizontalArrangement = Arrangement.spacedBy(1.dp),
                        modifier = Modifier.fillMaxSize()
                    ) {
                        items(entries) { entry ->
                            DarkFileGridItem(
                                entry = entry,
                                isSelected = entry == selectedEntry,
                                onClick = { onEntrySelected(entry) },
                                borderAlpha = borderAlpha
                            )
                        }
                    }
                }
                ViewMode.LIST -> {
                    LazyColumn(
                        verticalArrangement = Arrangement.spacedBy(1.dp),
                        modifier = Modifier.fillMaxSize()
                    ) {
                        items(entries) { entry ->
                            DarkFileListItem(
                                entry = entry,
                                isSelected = entry == selectedEntry,
                                onClick = { onEntrySelected(entry) },
                                borderAlpha = borderAlpha
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun DarkFileGridItem(
    entry: ArchiveEntry,
    isSelected: Boolean,
    onClick: () -> Unit,
    borderAlpha: Float
) {
    Surface(
        onClick = onClick,
        modifier = Modifier
            .fillMaxWidth()
            .height(90.dp),
        shape = RoundedCornerShape(0.dp),
        color = if (isSelected) ArchiveColors.PrimaryNeon.copy(alpha = 0.1f) else ArchiveColors.DarkCard,
        border = if (isSelected) BorderStroke(1.dp, ArchiveColors.PrimaryNeon.copy(alpha = borderAlpha)) else null
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(8.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            // 文件图标
            Surface(
                modifier = Modifier.size(32.dp),
                shape = RoundedCornerShape(8.dp),
                color = FileTypeUtils.getFileTypeColor(entry).copy(alpha = 0.2f),
                border = BorderStroke(1.dp, FileTypeUtils.getFileTypeColor(entry).copy(alpha = borderAlpha))
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(
                        imageVector = FileTypeUtils.getFileTypeIcon(entry),
                        contentDescription = null,
                        tint = FileTypeUtils.getFileTypeColor(entry),
                        modifier = Modifier.size(16.dp)
                    )
                }
            }
            
            Spacer(modifier = Modifier.height(4.dp))
            
            // 文件名
            Text(
                text = entry.name,
                fontSize = 10.sp,
                color = if (isSelected) ArchiveColors.PrimaryNeon else ArchiveColors.TextBright,
                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis,
                textAlign = TextAlign.Center
            )
        }
    }
}

@Composable
private fun DarkFileListItem(
    entry: ArchiveEntry,
    isSelected: Boolean,
    onClick: () -> Unit,
    borderAlpha: Float
) {
    Surface(
        onClick = onClick,
        modifier = Modifier
            .fillMaxWidth()
            .height(48.dp),
        shape = RoundedCornerShape(0.dp),
        color = if (isSelected) ArchiveColors.PrimaryNeon.copy(alpha = 0.1f) else ArchiveColors.DarkCard,
        border = if (isSelected) BorderStroke(1.dp, ArchiveColors.PrimaryNeon.copy(alpha = borderAlpha)) else null
    ) {
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 12.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // 文件图标
            Surface(
                modifier = Modifier.size(32.dp),
                shape = RoundedCornerShape(6.dp),
                color = FileTypeUtils.getFileTypeColor(entry).copy(alpha = 0.2f),
                border = BorderStroke(1.dp, FileTypeUtils.getFileTypeColor(entry).copy(alpha = borderAlpha))
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(
                        imageVector = FileTypeUtils.getFileTypeIcon(entry),
                        contentDescription = null,
                        tint = FileTypeUtils.getFileTypeColor(entry),
                        modifier = Modifier.size(16.dp)
                    )
                }
            }
            
            // 文件信息
            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.Center
            ) {
                Text(
                    text = entry.name,
                    fontSize = 12.sp,
                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                    color = if (isSelected) ArchiveColors.PrimaryNeon else ArchiveColors.TextBright,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                
                Text(
                    text = if (entry.isDirectory) "文件夹" else FileTypeUtils.formatFileSize(entry.size),
                    fontSize = 10.sp,
                    color = ArchiveColors.TextDim,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
            
            // 选择指示器
            if (isSelected) {
                Icon(
                    imageVector = Icons.Rounded.Check,
                    contentDescription = null,
                    tint = ArchiveColors.PrimaryNeon,
                    modifier = Modifier.size(16.dp)
                )
            }
        }
    }
} 