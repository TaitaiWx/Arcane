package com.arcane.ui.components.archive

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.BorderStroke
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.arcane.model.ArchiveEntry
import com.arcane.model.ArchiveInfo
import com.arcane.ui.components.shared.ArchiveColors
import com.arcane.ui.components.shared.FileTypeUtils

@Composable
fun DarkFileDetails(
    selectedEntry: ArchiveEntry?,
    archiveInfo: ArchiveInfo,
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
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "文件详情",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = ArchiveColors.TextBright
                    )
                }
            }
            
            // 详情内容
            if (selectedEntry != null) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(12.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    // 文件预览
                    DarkFilePreview(
                        entry = selectedEntry,
                        borderAlpha = borderAlpha
                    )
                    
                    // 文件信息
                    DarkFileInfo(
                        entry = selectedEntry,
                        archiveInfo = archiveInfo,
                        borderAlpha = borderAlpha
                    )
                }
            } else {
                // 空状态
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Surface(
                            modifier = Modifier.size(48.dp),
                            shape = RoundedCornerShape(12.dp),
                            color = ArchiveColors.DarkSurface,
                            border = BorderStroke(1.dp, ArchiveColors.BorderGlow.copy(alpha = borderAlpha))
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Icon(
                                    imageVector = Icons.Rounded.Description,
                                    contentDescription = null,
                                    tint = ArchiveColors.TextDim,
                                    modifier = Modifier.size(24.dp)
                                )
                            }
                        }
                        Text(
                            text = "选择文件查看详情",
                            fontSize = 12.sp,
                            color = ArchiveColors.TextDim,
                            textAlign = TextAlign.Center
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun DarkFilePreview(
    entry: ArchiveEntry,
    borderAlpha: Float
) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(4.dp),
        color = ArchiveColors.DarkSurface,
        border = BorderStroke(1.dp, ArchiveColors.BorderGlow.copy(alpha = borderAlpha))
    ) {
        Row(
            modifier = Modifier.padding(8.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Surface(
                modifier = Modifier.size(32.dp),
                shape = RoundedCornerShape(4.dp),
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
            
            Column {
                Text(
                    text = entry.name,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = ArchiveColors.TextBright,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Text(
                    text = if (entry.isDirectory) "文件夹" else "文件",
                    fontSize = 10.sp,
                    color = ArchiveColors.TextDim
                )
            }
        }
    }
}

@Composable
private fun DarkFileInfo(
    entry: ArchiveEntry,
    archiveInfo: ArchiveInfo,
    borderAlpha: Float
) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(4.dp),
        color = ArchiveColors.DarkSurface,
        border = BorderStroke(1.dp, ArchiveColors.BorderGlow.copy(alpha = borderAlpha))
    ) {
        Column(
            modifier = Modifier.padding(8.dp),
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            DarkInfoItem("大小", FileTypeUtils.formatFileSize(entry.size))
            DarkInfoItem("路径", entry.path)
            DarkInfoItem("类型", if (entry.isDirectory) "文件夹" else "文件")
            DarkInfoItem("压缩包", archiveInfo.name)
        }
    }
}

@Composable
private fun DarkInfoItem(
    label: String,
    value: String
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = label,
            fontSize = 10.sp,
            color = ArchiveColors.TextDim,
            modifier = Modifier.weight(1f)
        )
        Text(
            text = value,
            fontSize = 10.sp,
            color = ArchiveColors.TextBright,
            fontWeight = FontWeight.Medium,
            modifier = Modifier.weight(2f),
            textAlign = TextAlign.End,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
    }
} 