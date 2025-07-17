package com.arcane

import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.*
import com.arcane.ui.theme.ArcaneTheme
import com.arcane.ui.screens.MainScreen

fun main() = application {
    val windowState = rememberWindowState(
        width = 1400.dp, 
        height = 900.dp,
        placement = WindowPlacement.Floating
    )
    
    Window(
        onCloseRequest = ::exitApplication,
        title = "Arcane",
        state = windowState,
        undecorated = true,
        transparent = false,
        resizable = true
    ) {
        ArcaneTheme {
            MainScreen(
                onCloseRequest = ::exitApplication,
                windowState = windowState
            )
        }
    }
}

@Preview
@Composable
fun AppPreview() {
    ArcaneTheme {
        MainScreen(
            onCloseRequest = {},
            windowState = null
        )
    }
}
