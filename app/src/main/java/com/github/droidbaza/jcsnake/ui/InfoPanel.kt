package com.github.droidbaza.jcsnake.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.github.droidbaza.jcsnake.model.GameState
import com.github.droidbaza.jcsnake.ui.theme.JCSnakeTheme

@Composable
fun InfoPanel(state: GameState) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text("Score: ${state.score}")
        Text("Max Score: ${state.maxScore}")
    }
}

@Preview(showBackground = true)
@Composable
fun InfoPanelPreview() {
    JCSnakeTheme {
        Box(contentAlignment = Alignment.Center, modifier = Modifier.fillMaxSize()) {
            InfoPanel(GameState())
        }
    }
}


