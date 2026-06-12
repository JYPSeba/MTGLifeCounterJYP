package com.example.mtglifecounter

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.tooling.preview.Preview

@Composable
fun PlayerSection(
    player: PlayerData,
    isRotated: Boolean,
    onUpdateLife: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    val contentColor = if (player.color == Color(0xFFF9FAF4)) Color(0xFF1A1A1A) else Color.White
    val interactionSource = remember { MutableInteractionSource() }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(
                brush = Brush.verticalGradient(
                    colors = listOf(player.color, player.secondaryColor)
                )
            )
            .rotate(if (isRotated) 180f else 0f)
    ) {
        // Main Life Interaction Layer
        Row(modifier = Modifier.fillMaxSize()) {
            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxHeight()
                    .clickable(interactionSource = interactionSource, indication = null) { onUpdateLife(-1) },
                contentAlignment = Alignment.Center
            ) {
                Text("-", fontSize = 60.sp, color = contentColor.copy(alpha = 0.4f))
            }
            
            Box(
                modifier = Modifier.weight(1.5f).fillMaxHeight(),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    AnimatedVisibility(
                        visible = player.recentChange != 0,
                        enter = fadeIn(),
                        exit = fadeOut()
                    ) {
                        val sign = if (player.recentChange > 0) "+" else ""
                        Text(
                            text = "$sign${player.recentChange}",
                            fontSize = 32.sp,
                            color = contentColor.copy(alpha = 0.7f),
                            fontWeight = FontWeight.Bold
                        )
                    }
                    Text(
                        text = player.life.toString(),
                        fontSize = 130.sp,
                        fontWeight = FontWeight.Black,
                        color = contentColor,
                        letterSpacing = (-2).sp
                    )
                }
            }

            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxHeight()
                    .clickable(interactionSource = interactionSource, indication = null) { onUpdateLife(1) },
                contentAlignment = Alignment.Center
            ) {
                Text("+", fontSize = 60.sp, color = contentColor.copy(alpha = 0.4f))
            }
        }

        // Die Result and Winner Label
        AnimatedVisibility(
            visible = player.dieRoll != null,
            enter = fadeIn(),
            exit = fadeOut(),
            modifier = Modifier.align(Alignment.BottomCenter).padding(bottom = 12.dp)
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                if (player.isWinner) {
                    Text(
                        text = "WINNER",
                        color = contentColor,
                        fontWeight = FontWeight.ExtraBold,
                        fontSize = 12.sp,
                        letterSpacing = 2.sp
                    )
                }
                Box(
                    modifier = Modifier
                        .size(44.dp)
                        .background(contentColor.copy(alpha = 0.2f), androidx.compose.foundation.shape.RoundedCornerShape(8.dp)),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = player.dieRoll.toString(),
                        color = contentColor,
                        fontWeight = FontWeight.Bold,
                        fontSize = 22.sp
                    )
                }
            }
        }

        Text(
            text = "P${player.id}",
            modifier = Modifier.align(Alignment.TopStart).padding(8.dp),
            color = contentColor.copy(alpha = 0.3f),
            fontWeight = FontWeight.Bold,
            fontSize = 10.sp
        )
    }
}

@Preview(showBackground = true, widthDp = 400, heightDp = 200)
@Composable
fun PlayerSectionPreview() {
    PlayerSection(
        player = PlayerData(
            id = 1,
            life = 40,
            recentChange = -3,
            color = Color(0xFFD3202A),
            secondaryColor = Color(0xFF4A0E0E)
        ),
        isRotated = false,
        onUpdateLife = {}
    )
}

