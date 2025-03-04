package com.github.droidbaza.jcsnake

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowLeft
import androidx.compose.material.icons.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.github.droidbaza.jcsnake.ui.theme.Primary
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

enum class Direction {
    UP, DOWN, LEFT, RIGHT;

    fun isOpposite(other: Direction): Boolean {
        return when (this) {
            UP -> other == DOWN
            DOWN -> other == UP
            LEFT -> other == RIGHT
            RIGHT -> other == LEFT
        }
    }
}

data class Point(val x: Int, val y: Int)

data class GameState(
    val snake: List<Point> = listOf(Point(5, 5)),
    val food: Point = Point(7, 7),
    val direction: Direction = Direction.RIGHT,
    val score: Int = 0,
    val maxScore: Int = 0,
    val speed: Long = 300L,
    val gridSize: Int = 20,
    val status: GameStatus = GameStatus.Pause,
)

sealed class GameStatus {
    object Pause : GameStatus()
    object Running : GameStatus()
    object GameOver : GameStatus()
}

sealed class SnakeIntent {
    object Run : SnakeIntent()
    object PauseOrResume : SnakeIntent()
    object Restart : SnakeIntent()
    data class Rotate(val direction: Direction) : SnakeIntent()
}

interface GameEngine {
    val state: GameState
    fun run()
    fun restart()
    fun pauseOrResume()
    fun rotate(direction: Direction)
}

class DefaultGameEngine(val gridSize: Int = 10) : GameEngine {

    private val grid = List(gridSize * gridSize) {
        Point(it % gridSize, it / gridSize)
    }
    private var _state = GameState(gridSize = gridSize)
    override val state: GameState
        get() = _state

    fun nextPoint(p: Point, direction: Direction) = when (direction) {
        Direction.UP -> p.copy(y = (p.y - 1).mod(gridSize))
        Direction.DOWN -> p.copy(y = (p.y + 1).mod(gridSize))
        Direction.LEFT -> p.copy(x = (p.x - 1).mod(gridSize))
        Direction.RIGHT -> p.copy(x = (p.x + 1).mod(gridSize))
    }

    override fun pauseOrResume() {
        val targetStatus = if (state.status == GameStatus.Pause) {
            GameStatus.Running
        } else GameStatus.Pause
        _state = state.copy(status = targetStatus)
    }

    override fun run() {
        if (state.status == GameStatus.Pause) {
            return
        }
        val snake = state.snake
        val head = snake.first()
        val newHead = nextPoint(head, state.direction)

        val gameOver = newHead in snake
        if (gameOver) {
            _state = state.copy(status = GameStatus.GameOver)
            return
        }

        val newSnake = snake.toMutableList()
        newSnake.add(0, newHead)
        var currentFood = state.food
        var newScore = state.score
        var newSpeed = state.speed
        if (newHead == currentFood) {
            newScore += 1
            newSpeed = maxOf(100L, state.speed - 10L)

            val newFood = generateNewFood(newSnake)
            if (newFood != null) {
                currentFood = newFood
            }
        } else {
            newSnake.removeAt(newSnake.size - 1)
        }

        _state = state.copy(
            snake = newSnake,
            food = currentFood,
            score = newScore,
            speed = newSpeed,
            status = GameStatus.Running
        )
    }

    override fun rotate(direction: Direction) {
        if (!direction.isOpposite(state.direction)) {
            _state = state.copy(direction = direction)
        }
    }

    override fun restart() {
        _state = GameState(
            maxScore = state.maxScore,
            gridSize = state.gridSize,
            status = GameStatus.Running
        )
    }

    private fun generateNewFood(snake: List<Point>): Point? {
        val items = grid.filter { it !in snake }
        return items.random()
    }
}


class SnakeViewModel(private val gameEngine: GameEngine = DefaultGameEngine()) : ViewModel() {

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
fun SnakeGame(viewModel: SnakeViewModel) {
    val state = viewModel.state.collectAsState()
    val status = state.value.status
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
                viewModel.sendIntent(SnakeIntent.Restart)
            }
            GameCanvas(
                Modifier
                    .alpha(alpha)
                    .clickable(!gameOver) {
                        viewModel.sendIntent(SnakeIntent.PauseOrResume)
                    },
                state = state.value
            )
            if (gameOver) {
                Text(
                    "GAME OVER",
                    Modifier
                        .clickable {
                            viewModel.sendIntent(SnakeIntent.Restart)
                        },
                    fontSize = 32.sp
                )
            }
        }
        InfoPanel(state.value)
        ControlPanel(!gameOver) {
            viewModel.sendIntent(SnakeIntent.Rotate(it))
        }
    }
}

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


@Composable
fun ControlPanel(enabled: Boolean, rotate: (Direction) -> Unit) {
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
