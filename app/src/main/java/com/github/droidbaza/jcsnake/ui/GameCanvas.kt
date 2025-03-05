package com.github.droidbaza.jcsnake.ui

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.github.droidbaza.jcsnake.model.GameState
import com.github.droidbaza.jcsnake.ui.theme.JCSnakeTheme

@Composable
fun GameCanvas(modifier: Modifier = Modifier, state: GameState) {
    BoxWithConstraints(modifier = modifier.border(2.dp, Color.Black)) {
        val cellSize = with(LocalDensity.current) {
            minOf(
                this@BoxWithConstraints.maxWidth,
                this@BoxWithConstraints.maxHeight
            ).toPx() / state.gridSize
        }
        Canvas(
            modifier = Modifier.aspectRatio(1f)
        ) {
            state.snake.forEach {
                drawRoundRect(
                    color = Color.DarkGray,
                    topLeft = Offset(it.x * cellSize, it.y * cellSize),
                    size = Size(cellSize, cellSize),
                    cornerRadius = CornerRadius(cellSize / 4, cellSize / 4)
                )
            }
            state.food.let {
                drawRoundRect(
                    color = Color.Red,
                    topLeft = Offset(it.x * cellSize, it.y * cellSize),
                    size = Size(cellSize, cellSize),
                    cornerRadius = CornerRadius(cellSize / 4, cellSize / 4)
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun GameCanvasPreview() {
    Column(modifier = Modifier.padding(16.dp)) {
        GameCanvas(state = GameState())
    }
}
