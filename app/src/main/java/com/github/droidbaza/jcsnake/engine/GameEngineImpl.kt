package com.github.droidbaza.jcsnake.engine

import androidx.annotation.VisibleForTesting
import com.github.droidbaza.jcsnake.model.Direction
import com.github.droidbaza.jcsnake.model.GameState
import com.github.droidbaza.jcsnake.model.GameStatus
import com.github.droidbaza.jcsnake.model.Point

class GameEngineImpl(val gridSize: Int = 10) : GameEngine {

    private val grid = List(gridSize * gridSize) {
        Point(it % gridSize, it / gridSize)
    }
    @VisibleForTesting
    var _state = GameState(gridSize = gridSize)
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

    @VisibleForTesting
    fun generateNewFood(snake: List<Point>): Point? {
        val items = grid.filter { it !in snake }
        return items.random()
    }
}