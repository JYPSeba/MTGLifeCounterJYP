package com.example.mtglifecounter

import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview

@Preview
@Composable
fun BasicTestPreview() {
    PlayerSection(
        player = PlayerData(
            id = 1,
            life = 40,
            recentChange = 5,
            color = Color.Red,
            secondaryColor = Color.Black
        ),
        isRotated = false,
        onUpdateLife = {}
    )
}
