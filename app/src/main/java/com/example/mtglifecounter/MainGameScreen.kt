package com.example.mtglifecounter

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Casino
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel

@Composable
fun MainGameScreen(viewModel: MTGViewModel = viewModel()) {
    val gameState by viewModel.gameState.collectAsState()
    var showMenu by remember { mutableStateOf(false) }

    Box(modifier = Modifier.fillMaxSize().background(Color.Black)) {
        // Grid Horizontal
        Row(modifier = Modifier.fillMaxSize()) {
            val playerCount = gameState.playerCount
            val cols = when (playerCount) {
                2 -> 1
                4 -> 2
                6 -> 3
                else -> 2
            }
            
            for (c in 0 until cols) {
                Column(modifier = Modifier.weight(1f)) {
                    for (r in 0 until 2) {
                        val index = if (playerCount == 2) r else (r * cols + c)
                        if (index < gameState.players.size) {
                            val player = gameState.players[index]
                            PlayerSection(
                                player = player,
                                isRotated = r == 0,
                                onUpdateLife = { delta -> viewModel.updateLife(player.id, delta) },
                                modifier = Modifier.weight(1f)
                            )
                        }
                    }
                }
            }
        }

        // Botón Ajustes Único y Minimalista
        IconButton(
            onClick = { showMenu = true },
            modifier = Modifier
                .align(Alignment.Center)
                .size(44.dp)
                .background(Color.Black.copy(alpha = 0.5f), CircleShape)
        ) {
            Icon(Icons.Default.Settings, "Ajustes", tint = Color.White.copy(alpha = 0.8f))
        }

        if (showMenu) {
            SettingsDialog(
                gameState = gameState,
                onDismiss = { showMenu = false },
                onSetupGame = { count, life -> viewModel.setupGame(count, life) },
                onRollDice = { viewModel.rollAllDice(); showMenu = false },
                onReset = { viewModel.resetGame(); showMenu = false }
            )
        }
    }
}

@Composable
fun SettingsDialog(
    gameState: GameState,
    onDismiss: () -> Unit,
    onSetupGame: (Int, Int) -> Unit,
    onRollDice: () -> Unit,
    onReset: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = Color(0xFF1A1A1A),
        shape = RoundedCornerShape(12.dp),
        title = { Text("Mesa de Juego", color = Color.White, fontWeight = FontWeight.Bold) },
        text = {
            Column(modifier = Modifier.widthIn(max = 400.dp)) { // Optimizado para horizontal
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                    // Columna Jugadores
                    Column(modifier = Modifier.weight(1f)) {
                        Text("JUGADORES", color = Color.Gray, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                        Spacer(Modifier.height(8.dp))
                        Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                            listOf(2, 4, 6).forEach { count ->
                                val isSel = gameState.playerCount == count
                                Surface(
                                    modifier = Modifier.weight(1f).height(36.dp),
                                    shape = RoundedCornerShape(6.dp),
                                    color = if (isSel) Color.White.copy(0.1f) else Color.Transparent,
                                    border = androidx.compose.foundation.BorderStroke(1.dp, if (isSel) Color.White else Color.White.copy(0.1f)),
                                    onClick = { onSetupGame(count, gameState.initialLife) }
                                ) {
                                    Box(contentAlignment = Alignment.Center) { Text(count.toString(), color = Color.White, fontSize = 14.sp) }
                                }
                            }
                        }
                    }
                    // Columna Vida
                    Column(modifier = Modifier.weight(1f)) {
                        Text("VIDA INICIAL", color = Color.Gray, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                        Spacer(Modifier.height(8.dp))
                        Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                            listOf(20, 40).forEach { life ->
                                val isSel = gameState.initialLife == life
                                Surface(
                                    modifier = Modifier.weight(1f).height(36.dp),
                                    shape = RoundedCornerShape(6.dp),
                                    color = if (isSel) Color.White.copy(0.1f) else Color.Transparent,
                                    border = androidx.compose.foundation.BorderStroke(1.dp, if (isSel) Color.White else Color.White.copy(0.1f)),
                                    onClick = { onSetupGame(gameState.playerCount, life) }
                                ) {
                                    Box(contentAlignment = Alignment.Center) { Text(life.toString(), color = Color.White, fontSize = 14.sp) }
                                }
                            }
                        }
                    }
                }
                
                Spacer(modifier = Modifier.height(24.dp))
                
                // Botón TIRAR DADOS (Ahora aquí dentro)
                Button(
                    onClick = onRollDice,
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(8.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF4CAF50), contentColor = Color.White)
                ) {
                    Icon(Icons.Default.Casino, null, modifier = Modifier.size(20.dp))
                    Spacer(Modifier.width(8.dp))
                    Text("TIRAR DADOS PARA TODOS", fontWeight = FontWeight.Bold)
                }
            }
        },
        confirmButton = {
            Button(
                onClick = onReset,
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(8.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color.White, contentColor = Color.Black)
            ) {
                Icon(Icons.Default.Refresh, null, modifier = Modifier.size(18.dp))
                Spacer(Modifier.width(8.dp))
                Text("REINICIAR PARTIDA")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss, modifier = Modifier.fillMaxWidth()) {
                Text("CERRAR", color = Color.Gray)
            }
        }
    )
}
