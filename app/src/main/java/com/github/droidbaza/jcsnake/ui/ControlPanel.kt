package com.github.droidbaza.jcsnake.ui

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowLeft
import androidx.compose.material.icons.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.github.droidbaza.jcsnake.model.Direction
import com.github.droidbaza.jcsnake.ui.theme.JCSnakeTheme

@Composable
fun ControlPanel(enabled: Boolean = true, rotate: (Direction) -> Unit = {}) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Icon(
            imageVector = Icons.Default.KeyboardArrowUp,
            "btn top",
            Modifier
                .padding(top = 40.dp)
                .size(40.dp)
                .clickable(enabled) { rotate(Direction.UP) }
        )
        Row(
            modifier = Modifier
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.Center
        ) {
            Icon(
                imageVector = Icons.Default.KeyboardArrowLeft,
                "btn left",
                Modifier
                    .size(40.dp)
                    .clickable(enabled) { rotate(Direction.LEFT) }
            )
            Spacer(Modifier.size(40.dp))
            Icon(
                imageVector = Icons.Default.KeyboardArrowRight,
                "btn right",
                Modifier
                    .size(40.dp)
                    .clickable(enabled) { rotate(Direction.RIGHT) }
            )
        }
        Icon(
            imageVector = Icons.Default.KeyboardArrowDown,
            "btn up",
            Modifier
                .size(40.dp)
                .clickable(enabled) { rotate(Direction.DOWN) }
        )
    }
}

@Preview(showBackground = true)
@Composable
fun ControlPanelPreview() {
    ControlPanel()
}