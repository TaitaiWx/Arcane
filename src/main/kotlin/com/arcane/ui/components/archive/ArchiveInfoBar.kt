package com.arcane.ui.components.archive

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.arcane.model.ArchiveInfo
import com.arcane.ui.components.shared.ArchiveColors
import com.arcane.ui.components.shared.ViewMode

@Composable
fun ArchiveInfoBar(
    archiveInfo: ArchiveInfo,
    viewMode: ViewMode,
    onViewModeChanged: (ViewMode) -> Unit,
    borderAlpha: Float,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier,
        color = ArchiveColors.DarkBackground,
        shape = RoundedCornerShape(8.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(60.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // 档案信息 - 左侧紧贴
            Row(
                modifier = Modifier.padding(start = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // 霓虹档案图标
                Surface(
                    modifier = Modifier.size(40.dp),
                    shape = RoundedCornerShape(8.dp),
                    color = ArchiveColors.DarkSurface,
                    border = BorderStroke(2.dp, ArchiveColors.PrimaryNeon.copy(alpha = borderAlpha))
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Icon(
                            imageVector = Icons.Rounded.Archive,
                            contentDescription = null,
                            tint = ArchiveColors.PrimaryNeon,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }
                
                Column {
                    Text(
                        text = archiveInfo.name,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = ArchiveColors.TextBright
                    )
                    Text(
                        text = "${archiveInfo.entries.size} 个文件",
                        fontSize = 12.sp,
                        color = ArchiveColors.TextDim
                    )
                }
            }
            
            // 视图模式切换 - 右侧紧贴
            Row(
                modifier = Modifier.padding(end = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                ViewModeButton(
                    icon = Icons.Rounded.GridView,
                    isSelected = viewMode == ViewMode.GRID,
                    onClick = { onViewModeChanged(ViewMode.GRID) },
                    borderAlpha = borderAlpha
                )
                ViewModeButton(
                    icon = Icons.Rounded.ViewList,
                    isSelected = viewMode == ViewMode.LIST,
                    onClick = { onViewModeChanged(ViewMode.LIST) },
                    borderAlpha = borderAlpha
                )
            }
        }
    }
}

@Composable
private fun ViewModeButton(
    icon: ImageVector,
    isSelected: Boolean,
    onClick: () -> Unit,
    borderAlpha: Float
) {
    Surface(
        onClick = onClick,
        modifier = Modifier.size(32.dp),
        shape = RoundedCornerShape(8.dp),
        color = if (isSelected) ArchiveColors.PrimaryNeon.copy(alpha = 0.2f) else ArchiveColors.DarkSurface,
        border = BorderStroke(
            width = 1.dp,
            color = if (isSelected) ArchiveColors.PrimaryNeon.copy(alpha = borderAlpha) else ArchiveColors.BorderGlow.copy(alpha = 0.3f)
        )
    ) {
        Box(contentAlignment = Alignment.Center) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = if (isSelected) ArchiveColors.PrimaryNeon else ArchiveColors.TextDim,
                modifier = Modifier.size(16.dp)
            )
        }
    }
} 