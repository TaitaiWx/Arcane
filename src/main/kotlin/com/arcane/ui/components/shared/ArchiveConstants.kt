package com.arcane.ui.components.shared

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.*
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import com.arcane.model.ArchiveEntry

// 暗黑模式颜色系统
object ArchiveColors {
    val DarkestBackground = Color(0xFF000000) // 最深黑色
    val DarkBackground = Color(0xFF0A0A0A) // 极深灰
    val DarkSurface = Color(0xFF111111) // 深灰表面
    val DarkCard = Color(0xFF1A1A1A) // 深色卡片
    val BorderGlow = Color(0xFF333333) // 边框发光
    val PrimaryNeon = Color(0xFF00FF88) // 霓虹绿
    val SecondaryNeon = Color(0xFF0088FF) // 霓虹蓝
    val AccentNeon = Color(0xFFFF0088) // 霓虹粉
    val TextBright = Color(0xFFFFFFFF) // 亮白文字
    val TextDim = Color(0xFF888888) // 暗灰文字
}

// 文件类型工具函数
object FileTypeUtils {
    fun getFileTypeIcon(entry: ArchiveEntry): ImageVector {
        return if (entry.isDirectory) {
            Icons.Rounded.Folder
        } else {
            when (entry.name.substringAfterLast('.', "").lowercase()) {
                "txt", "md", "rtf" -> Icons.Rounded.Description
                "jpg", "jpeg", "png", "gif", "bmp" -> Icons.Rounded.Image
                "mp4", "avi", "mkv", "mov" -> Icons.Rounded.VideoFile
                "mp3", "wav", "flac", "aac" -> Icons.Rounded.AudioFile
                "pdf" -> Icons.Rounded.PictureAsPdf
                "zip", "rar", "7z", "tar" -> Icons.Rounded.Archive
                "exe", "msi", "app" -> Icons.Rounded.Apps
                else -> Icons.Rounded.InsertDriveFile
            }
        }
    }

    fun getFileTypeColor(entry: ArchiveEntry): Color {
        return if (entry.isDirectory) {
            ArchiveColors.PrimaryNeon // 霓虹绿
        } else {
            when (entry.name.substringAfterLast('.', "").lowercase()) {
                "txt", "md", "rtf" -> ArchiveColors.SecondaryNeon // 霓虹蓝
                "jpg", "jpeg", "png", "gif", "bmp" -> ArchiveColors.AccentNeon // 霓虹粉
                "mp4", "avi", "mkv", "mov" -> Color(0xFF8800FF) // 霓虹紫
                "mp3", "wav", "flac", "aac" -> ArchiveColors.PrimaryNeon // 霓虹绿
                "pdf" -> Color(0xFFFF4400) // 霓虹橙
                "zip", "rar", "7z", "tar" -> Color(0xFFFFFF00) // 霓虹黄
                "exe", "msi", "app" -> Color(0xFF4400FF) // 霓虹蓝紫
                else -> ArchiveColors.TextDim
            }
        }
    }

    fun formatFileSize(bytes: Long): String {
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
}

// 视图模式枚举
enum class ViewMode {
    GRID, LIST
} 