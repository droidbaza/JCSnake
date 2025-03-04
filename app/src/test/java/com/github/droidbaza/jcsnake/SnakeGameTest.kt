package com.github.droidbaza.jcsnake

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test


class DirectionTest {
    @Test
    fun `isOpposite should return true for opposite directions`() {
        assertTrue(Direction.UP.isOpposite(Direction.DOWN))
        assertTrue(Direction.DOWN.isOpposite(Direction.UP))
        assertTrue(Direction.LEFT.isOpposite(Direction.RIGHT))
        assertTrue(Direction.RIGHT.isOpposite(Direction.LEFT))
    }

    @Test
    fun `isOpposite should return false for non-opposite directions`() {
        assertFalse(Direction.UP.isOpposite(Direction.LEFT))
        assertFalse(Direction.DOWN.isOpposite(Direction.RIGHT))
    }
}

class GameEngineTest {
    private lateinit var gameEngine: DefaultGameEngine

    @Before
    fun setup() {
        gameEngine = DefaultGameEngine(gridSize = 10)
    }

    @Test
    fun `snake moves in the correct direction`() {
        gameEngine.rotate(Direction.RIGHT)
        gameEngine.run()
        assertEquals(Point(6, 5), gameEngine.state.snake.first())
    }

    @Test
    fun `snake eats food and grows`() {
        TODO()
    }

    @Test
    fun `game over when snake collides with itself`() {
        TODO()
    }
}

class SnakeViewModelTest {
    private lateinit var viewModel: SnakeViewModel
    private lateinit var gameEngine: DefaultGameEngine

    @Before
    fun setup() {
        gameEngine = DefaultGameEngine(gridSize = 10)
        viewModel = SnakeViewModel(gameEngine)
    }

    @Test
    fun `viewModel updates state correctly on move`() {
        val initialHead = viewModel.state.value.snake.first()
        viewModel.sendIntent(SnakeIntent.Run)
        val newHead = viewModel.state.value.snake.first()
        assertNotEquals(initialHead, newHead)
    }
}



