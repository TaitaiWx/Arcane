package com.arcane.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material.icons.filled.CropSquare
import androidx.compose.material.icons.filled.Brightness6
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.WindowState
import androidx.compose.ui.window.WindowPlacement
import androidx.compose.ui.window.WindowPosition
import androidx.compose.foundation.ExperimentalFoundationApi
import kotlin.math.roundToInt
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.delay
import kotlinx.coroutines.Job
import kotlinx.coroutines.coroutineScope
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsHoveredAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush

@OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class)
@Composable
fun CustomTitleBar(
    title: String = "Arcane",
    subtitle: String? = null,
    isDarkTheme: Boolean = true,
    onThemeToggle: () -> Unit = {},
    onSettings: () -> Unit = {},
    onCloseRequest: () -> Unit = {},
    windowState: WindowState? = null,
    modifier: Modifier = Modifier
) {
    // 最简化的拖动实现 - 避免任何复杂状态管理
    var isDragging by remember { mutableStateOf(false) }
    
    Surface(
        modifier = modifier
            .fillMaxWidth()
            .height(60.dp),
        color = if (isDarkTheme) Color(0xFF1A1A1A) else Color(0xFFFFFFFF),
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .pointerInput(Unit) {
                    detectDragGestures(
                        onDragStart = { 
                            isDragging = true
                        },
                        onDragEnd = {
                            isDragging = false
                        }
                    ) { change, dragAmount ->
                        // 只消费事件，使用最简单的拖动逻辑
                        change.consume()
                        
                        // 获取当前窗口位置并直接应用偏移
                        windowState?.let { state ->
                            val currentPos = state.position
                            if (currentPos is WindowPosition.Absolute) {
                                val newX = currentPos.x.value + dragAmount.x
                                val newY = currentPos.y.value + dragAmount.y
                                
                                state.position = WindowPosition.Absolute(
                                    x = newX.dp,
                                    y = newY.dp
                                )
                            }
                        }
                    }
                }
        ) {
            // 左侧：只有应用图标
            Row(
                modifier = Modifier
                    .align(Alignment.CenterStart)
                    .padding(start = 16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // 应用图标
                Surface(
                    modifier = Modifier.size(32.dp),
                    shape = RoundedCornerShape(8.dp),
                    color = Color(0xFF4FC3F7)
                ) {
                    Box(
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "A",
                            color = Color.White,
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
            
            // 中间：标题居中显示
            Column(
                modifier = Modifier.align(Alignment.Center),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = title,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = if (isDarkTheme) Color.White else Color.Black
                )
                subtitle?.let { sub ->
                    Text(
                        text = sub,
                        fontSize = 12.sp,
                        color = if (isDarkTheme) Color(0xFFB0B0B0) else Color(0xFF666666)
                    )
                }
            }
            
            // 右侧：控制按钮 - icon变小
            Row(
                modifier = Modifier
                    .align(Alignment.CenterEnd)
                    .padding(end = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(4.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // 主题切换按钮 - 现代化设计
                ModernIconButton(
                    icon = Icons.Default.Brightness6,
                    contentDescription = "切换主题",
                    onClick = onThemeToggle,
                    tint = if (isDarkTheme) Color(0xFF4FC3F7) else Color(0xFF1976D2)
                )
                
                // 设置按钮 - 现代化设计
                ModernIconButton(
                    icon = Icons.Default.Settings,
                    contentDescription = "设置",
                    onClick = onSettings,
                    tint = if (isDarkTheme) Color(0xFFB0B0B0) else Color(0xFF666666)
                )
                
                Spacer(modifier = Modifier.width(4.dp))
                
                // 窗口控制按钮
                OptimizedWindowControlButtons(
                    windowState = windowState,
                    onCloseRequest = onCloseRequest
                )
            }
        }
    }
}

@Composable
private fun OptimizedWindowControlButtons(
    windowState: WindowState?,
    onCloseRequest: () -> Unit
) {
    Row(
        horizontalArrangement = Arrangement.spacedBy(6.dp)
    ) {
        // 最小化按钮
        SquareControlButton(
            onClick = {
                windowState?.let { state ->
                    state.isMinimized = true
                }
            },
            icon = Icons.Default.Remove,
            contentDescription = "最小化",
            hoverColor = Color(0xFFFBBC04)
        )
        
        // 最大化/还原按钮
        SquareControlButton(
            onClick = {
                windowState?.let { state ->
                    state.placement = if (state.placement == WindowPlacement.Maximized) {
                        WindowPlacement.Floating
                    } else {
                        WindowPlacement.Maximized
                    }
                }
            },
            icon = Icons.Default.CropSquare,
            contentDescription = "最大化",
            hoverColor = Color(0xFF34A853)
        )
        
        // 关闭按钮
        SquareControlButton(
            onClick = onCloseRequest,
            icon = Icons.Default.Close,
            contentDescription = "关闭",
            hoverColor = Color(0xFFEA4335)
        )
    }
}

@Composable
private fun ModernIconButton(
    icon: ImageVector,
    contentDescription: String,
    onClick: () -> Unit,
    tint: Color,
    modifier: Modifier = Modifier
) {
    val interactionSource = remember { MutableInteractionSource() }
    val isHovered by interactionSource.collectIsHoveredAsState()
    
    // 辉光动画
    val glowAlpha by animateFloatAsState(
        targetValue = if (isHovered) 0.6f else 0.1f,
        animationSpec = tween(200)
    )
    
    // 缩放动画
    val scale by animateFloatAsState(
        targetValue = if (isHovered) 1.1f else 1.0f,
        animationSpec = tween(200)
    )
    
    Surface(
        onClick = onClick,
        modifier = modifier
            .size(28.dp)
            .scale(scale)
            .shadow(
                elevation = if (isHovered) 8.dp else 2.dp,
                shape = RoundedCornerShape(6.dp)
            ),
        shape = RoundedCornerShape(6.dp),
        color = Color.Transparent,
        interactionSource = interactionSource
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    brush = Brush.radialGradient(
                        colors = listOf(
                            tint.copy(alpha = glowAlpha),
                            Color.Transparent
                        ),
                        radius = 40f
                    ),
                    shape = RoundedCornerShape(6.dp)
                ),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = contentDescription,
                tint = tint,
                modifier = Modifier.size(14.dp)
            )
        }
    }
}

@Composable
private fun SquareControlButton(
    onClick: () -> Unit,
    icon: ImageVector,
    contentDescription: String,
    hoverColor: Color,
    modifier: Modifier = Modifier
) {
    val interactionSource = remember { MutableInteractionSource() }
    val isHovered by interactionSource.collectIsHoveredAsState()
    
    Surface(
        onClick = onClick,
        modifier = modifier.size(24.dp),
        shape = RoundedCornerShape(4.dp), // 方形圆角
        color = if (isHovered) hoverColor else Color.Transparent,
        interactionSource = interactionSource
    ) {
        Box(
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = contentDescription,
                tint = if (isHovered) Color.White else Color(0xFF888888),
                modifier = Modifier.size(12.dp)
            )
        }
    }
}
