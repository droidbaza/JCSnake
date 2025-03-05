package com.github.droidbaza.jcsnake.ui

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.sp
import com.github.droidbaza.jcsnake.ui.theme.JCSnakeTheme
import kotlinx.coroutines.delay

@Composable
fun Countdown(target: Int = 3, onFinish: () -> Unit) {
    var count by remember { mutableIntStateOf(target) }
    LaunchedEffect(Unit) {
        while (count >= 0) {
            delay(1000L)
            count--
        }
        delay(1000L)
        onFinish()
    }
    if (count >= 0) {
        Text(
            text = count.toString(),
            fontSize = 64.sp,
        )
    }
}


@Preview(showBackground = true)
@Composable
fun CountdownPreview() {
    JCSnakeTheme {
        Box(contentAlignment = Alignment.Center, modifier = Modifier.fillMaxSize()) {
            Countdown {

            }
        }
    }
}

