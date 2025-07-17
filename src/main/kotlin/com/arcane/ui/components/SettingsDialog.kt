package com.arcane.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Folder
import androidx.compose.material.icons.filled.FolderOpen
import androidx.compose.material.icons.filled.Palette
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Speed
import androidx.compose.material.icons.filled.Security
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

data class SettingsData(
    val isDarkTheme: Boolean = true,
    val defaultOutputPath: String = System.getProperty("user.home") + "/Desktop",
    val overwriteExisting: Boolean = false,
    val createSubfolder: Boolean = true,
    val preserveStructure: Boolean = true,
    val enableProgressSound: Boolean = false,
    val maxConcurrentExtractions: Int = 1
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsDialog(
    isDarkTheme: Boolean,
    onThemeToggle: () -> Unit,
    onDismiss: () -> Unit,
    defaultOutputPath: String = System.getProperty("user.home") + "/Desktop",
    onOutputPathChange: (String) -> Unit = {},
    onSettingsChange: (SettingsData) -> Unit = {}
) {
    var settings by remember {
        mutableStateOf(
            SettingsData(
                isDarkTheme = isDarkTheme,
                defaultOutputPath = defaultOutputPath
            )
        )
    }
    var showDirectoryChooser by remember { mutableStateOf(false) }
    
    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(
            dismissOnClickOutside = true,
            dismissOnBackPress = true
        )
    ) {
        Surface(
            modifier = Modifier
                .width(500.dp)
                .heightIn(max = 700.dp),
            shape = RoundedCornerShape(16.dp),
            color = if (isDarkTheme) Color(0xFF2D3748) else Color(0xFFFFFFFF),
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
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Settings,
                                contentDescription = "设置",
                                tint = if (isDarkTheme) Color(0xFF4FC3F7) else Color(0xFF1976D2),
                                modifier = Modifier.size(24.dp)
                            )
                            Text(
                                text = "应用设置",
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Bold,
                                color = if (isDarkTheme) Color.White else Color.Black
                            )
                        }
                        
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
                
                // 设置内容
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .verticalScroll(rememberScrollState())
                        .padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    // 外观设置
                    SettingsSection(
                        title = "外观设置",
                        icon = Icons.Default.Palette,
                        isDarkTheme = isDarkTheme
                    ) {
                        SettingsRow(
                            title = "深色主题",
                            subtitle = "切换应用的亮色/暗色主题",
                            isDarkTheme = isDarkTheme
                        ) {
                            Switch(
                                checked = isDarkTheme,
                                onCheckedChange = { 
                                    onThemeToggle()
                                    settings = settings.copy(isDarkTheme = !isDarkTheme)
                                },
                                colors = SwitchDefaults.colors(
                                    checkedThumbColor = Color(0xFF4FC3F7),
                                    checkedTrackColor = Color(0xFF4FC3F7).copy(alpha = 0.5f)
                                )
                            )
                        }
                    }
                    
                    // 文件夹设置
                    SettingsSection(
                        title = "文件夹设置",
                        icon = Icons.Default.Folder,
                        isDarkTheme = isDarkTheme
                    ) {
                        SettingsRow(
                            title = "默认输出目录",
                            subtitle = settings.defaultOutputPath,
                            isDarkTheme = isDarkTheme
                        ) {
                            IconButton(
                                onClick = { showDirectoryChooser = true }
                            ) {
                                Icon(
                                    imageVector = Icons.Default.FolderOpen,
                                    contentDescription = "选择目录",
                                    tint = if (isDarkTheme) Color(0xFF4FC3F7) else Color(0xFF1976D2)
                                )
                            }
                        }
                        
                        SettingsRow(
                            title = "创建子文件夹",
                            subtitle = "为每个压缩包创建单独的文件夹",
                            isDarkTheme = isDarkTheme
                        ) {
                            Switch(
                                checked = settings.createSubfolder,
                                onCheckedChange = { 
                                    settings = settings.copy(createSubfolder = it)
                                },
                                colors = SwitchDefaults.colors(
                                    checkedThumbColor = Color(0xFF4FC3F7),
                                    checkedTrackColor = Color(0xFF4FC3F7).copy(alpha = 0.5f)
                                )
                            )
                        }
                        
                        SettingsRow(
                            title = "保持目录结构",
                            subtitle = "解压时保持原有的文件夹结构",
                            isDarkTheme = isDarkTheme
                        ) {
                            Switch(
                                checked = settings.preserveStructure,
                                onCheckedChange = { 
                                    settings = settings.copy(preserveStructure = it)
                                },
                                colors = SwitchDefaults.colors(
                                    checkedThumbColor = Color(0xFF4FC3F7),
                                    checkedTrackColor = Color(0xFF4FC3F7).copy(alpha = 0.5f)
                                )
                            )
                        }
                    }
                    
                    // 解压设置
                    SettingsSection(
                        title = "解压设置",
                        icon = Icons.Default.Speed,
                        isDarkTheme = isDarkTheme
                    ) {
                        SettingsRow(
                            title = "覆盖已存在文件",
                            subtitle = "如果目标文件已存在，自动覆盖",
                            isDarkTheme = isDarkTheme
                        ) {
                            Switch(
                                checked = settings.overwriteExisting,
                                onCheckedChange = { 
                                    settings = settings.copy(overwriteExisting = it)
                                },
                                colors = SwitchDefaults.colors(
                                    checkedThumbColor = Color(0xFF4FC3F7),
                                    checkedTrackColor = Color(0xFF4FC3F7).copy(alpha = 0.5f)
                                )
                            )
                        }
                        
                        SettingsRow(
                            title = "完成提示音",
                            subtitle = "解压完成时播放提示音",
                            isDarkTheme = isDarkTheme
                        ) {
                            Switch(
                                checked = settings.enableProgressSound,
                                onCheckedChange = { 
                                    settings = settings.copy(enableProgressSound = it)
                                },
                                colors = SwitchDefaults.colors(
                                    checkedThumbColor = Color(0xFF4FC3F7),
                                    checkedTrackColor = Color(0xFF4FC3F7).copy(alpha = 0.5f)
                                )
                            )
                        }
                    }
                    
                    // 关于信息
                    SettingsSection(
                        title = "关于",
                        icon = Icons.Default.Info,
                        isDarkTheme = isDarkTheme
                    ) {
                        Column(
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Text(
                                text = "Arcane v1.0.0",
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold,
                                color = if (isDarkTheme) Color.White else Color.Black
                            )
                            Text(
                                text = "现代化的跨平台压缩包解压工具",
                                fontSize = 14.sp,
                                color = if (isDarkTheme) Color(0xFFB0B0B0) else Color(0xFF666666)
                            )
                            Text(
                                text = "基于 Jetpack Compose for Desktop 构建",
                                fontSize = 12.sp,
                                color = if (isDarkTheme) Color(0xFF808080) else Color(0xFF999999)
                            )
                        }
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
                    
                    Button(
                        onClick = {
                            onSettingsChange(settings)
                            onOutputPathChange(settings.defaultOutputPath)
                            onDismiss()
                        },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color(0xFF4FC3F7)
                        )
                    ) {
                        Text("保存", color = Color.White)
                    }
                }
            }
        }
    }
    
    // 目录选择对话框
    if (showDirectoryChooser) {
        ModernFileChooser(
            title = "选择默认输出目录",
            mode = FileChooserMode.DIRECTORIES_ONLY,
            onFileSelected = { directoryPath ->
                settings = settings.copy(defaultOutputPath = directoryPath)
                showDirectoryChooser = false
            },
            onDismiss = { showDirectoryChooser = false },
            isDarkTheme = isDarkTheme,
            initialDirectory = settings.defaultOutputPath
        )
    }
}

@Composable
private fun SettingsSection(
    title: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    isDarkTheme: Boolean,
    content: @Composable ColumnScope.() -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = if (isDarkTheme) Color(0xFF4A5568) else Color(0xFFF7FAFC)
        ),
        shape = RoundedCornerShape(12.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = title,
                    tint = if (isDarkTheme) Color(0xFF4FC3F7) else Color(0xFF1976D2),
                    modifier = Modifier.size(20.dp)
                )
                Text(
                    text = title,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = if (isDarkTheme) Color.White else Color.Black
                )
            }
            
            Spacer(modifier = Modifier.height(12.dp))
            
            content()
        }
    }
}

@Composable
private fun SettingsRow(
    title: String,
    subtitle: String,
    isDarkTheme: Boolean,
    action: @Composable () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(
            modifier = Modifier.weight(1f)
        ) {
            Text(
                text = title,
                fontSize = 14.sp,
                fontWeight = FontWeight.Medium,
                color = if (isDarkTheme) Color.White else Color.Black
            )
            Text(
                text = subtitle,
                fontSize = 12.sp,
                color = if (isDarkTheme) Color(0xFFB0B0B0) else Color(0xFF666666)
            )
        }
        
        action()
    }
}
