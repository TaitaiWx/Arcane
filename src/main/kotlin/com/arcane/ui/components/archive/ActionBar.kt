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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.arcane.ui.components.shared.ArchiveColors

@Composable
fun DarkActionBar(
    onExtract: () -> Unit,
    onClear: () -> Unit,
    isExtracting: Boolean,
    borderAlpha: Float,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier,
        color = ArchiveColors.DarkCard,
        shape = RoundedCornerShape(8.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(60.dp)
                .padding(horizontal = 16.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp, Alignment.CenterHorizontally),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // 霓虹解压按钮
            DarkNeonButton(
                text = if (isExtracting) "解压中..." else "开始解压",
                icon = Icons.Rounded.Launch,
                color = ArchiveColors.PrimaryNeon,
                onClick = onExtract,
                enabled = !isExtracting,
                borderAlpha = borderAlpha,
                modifier = Modifier.weight(1f)
            )
            
            // 霓虹清除按钮
            DarkNeonButton(
                text = "清除",
                icon = Icons.Rounded.Clear,
                color = ArchiveColors.AccentNeon,
                onClick = onClear,
                borderAlpha = borderAlpha,
                modifier = Modifier.weight(1f)
            )
        }
    }
}

@Composable
private fun DarkNeonButton(
    text: String,
    icon: ImageVector,
    color: Color,
    onClick: () -> Unit,
    borderAlpha: Float,
    modifier: Modifier = Modifier,
    enabled: Boolean = true
) {
    Button(
        onClick = onClick,
        modifier = modifier.height(40.dp),
        enabled = enabled,
        colors = ButtonDefaults.buttonColors(
            containerColor = color.copy(alpha = 0.2f),
            contentColor = color,
            disabledContainerColor = color.copy(alpha = 0.1f)
        ),
        shape = RoundedCornerShape(4.dp),
        border = BorderStroke(1.dp, color.copy(alpha = borderAlpha)),
        elevation = ButtonDefaults.buttonElevation(
            defaultElevation = 0.dp,
            pressedElevation = 0.dp
        )
    ) {
        Row(
            horizontalArrangement = Arrangement.spacedBy(4.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                modifier = Modifier.size(14.dp)
            )
            Text(
                text = text,
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold
            )
        }
    }
} 