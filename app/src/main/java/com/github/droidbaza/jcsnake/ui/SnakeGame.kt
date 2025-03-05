package com.github.droidbaza.jcsnake.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.github.droidbaza.jcsnake.engine.GameEngine
import com.github.droidbaza.jcsnake.engine.GameEngineImpl
import com.github.droidbaza.jcsnake.model.Direction
import com.github.droidbaza.jcsnake.model.GameState
import com.github.droidbaza.jcsnake.model.GameStatus
import com.github.droidbaza.jcsnake.ui.theme.Primary
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

sealed class SnakeIntent {
    object Run : SnakeIntent()
    object PauseOrResume : SnakeIntent()
    object Restart : SnakeIntent()
    data class Rotate(val direction: Direction) : SnakeIntent()
}

class SnakeViewModel(private val gameEngine: GameEngine = GameEngineImpl()) : ViewModel() {
    private val _state = MutableStateFlow(gameEngine.state)
    val state = _state.asStateFlow()
    fun sendIntent(intent: SnakeIntent) {
        when (intent) {
            is SnakeIntent.Rotate -> gameEngine.rotate(intent.direction)
            SnakeIntent.Run -> gameEngine.run()
            SnakeIntent.PauseOrResume -> gameEngine.pauseOrResume()
            SnakeIntent.Restart -> {
                gameEngine.restart()
                startGameLoop()
            }
        }
        _state.value = gameEngine.state
    }

    private fun startGameLoop() {
        viewModelScope.launch(Dispatchers.IO) {
            while (state.value.status !is GameStatus.GameOver) {
                delay(state.value.speed)
                sendIntent(SnakeIntent.Run)
            }
        }
    }
}

@Composable
fun SnakeGameRoute(viewModel: SnakeViewModel) {
    val state = viewModel.state.collectAsState()
    SnakeGameScreen(state.value, viewModel::sendIntent)
}

@Composable
fun SnakeGameScreen(state: GameState, sendIntent: (SnakeIntent) -> Unit = {}) {
    val status = state.status
    val gameOver = status == GameStatus.GameOver
    val alpha = if (status == GameStatus.Running) 1f else 0.3f

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Primary),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(contentAlignment = Alignment.Center, modifier = Modifier.padding(16.dp)) {
            Countdown {
                sendIntent(SnakeIntent.Restart)
            }
            GameCanvas(
                Modifier
                    .alpha(alpha)
                    .clickable(!gameOver) {
                        sendIntent(SnakeIntent.PauseOrResume)
                    },
                state = state
            )
            if (gameOver) {
                Text(
                    "GAME OVER",
                    Modifier
                        .clickable {
                            sendIntent(SnakeIntent.Restart)
                        },
                    fontSize = 32.sp
                )
            }
        }
        InfoPanel(state)
        ControlPanel(!gameOver) {
            sendIntent(SnakeIntent.Rotate(it))
        }
    }
}

@Preview(showBackground = true)
@Composable
fun SnakeGamePreview() {
    SnakeGameScreen(GameState())
}





