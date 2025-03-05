package com.github.droidbaza.jcsnake

import com.github.droidbaza.jcsnake.engine.GameEngineImpl
import com.github.droidbaza.jcsnake.model.Direction
import com.github.droidbaza.jcsnake.model.GameStatus
import com.github.droidbaza.jcsnake.model.Point
import org.hamcrest.MatcherAssert.assertThat
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test


class GameEngineTest {
    private val engine = GameEngineImpl(gridSize = 10)

    @Test
    fun testRunGame() {
        engine.run()
        assertEquals(GameStatus.Pause, engine.state.status)
    }

    @Test
    fun testPauseOrResume() {
        assertEquals(GameStatus.Pause, engine.state.status)

        engine.pauseOrResume()
        assertEquals(GameStatus.Running, engine.state.status)

        engine.pauseOrResume()
        assertEquals(GameStatus.Pause, engine.state.status)
    }

    @Test
    fun testRotate() {
        engine.rotate(Direction.RIGHT)
        assertEquals(Direction.RIGHT, engine.state.direction)

        engine.rotate(Direction.LEFT)
        assertEquals(Direction.RIGHT, engine.state.direction)

        engine.rotate(Direction.DOWN)
        assertEquals(Direction.DOWN, engine.state.direction)
    }

    @Test
    fun testNextPoint() {
        val point = Point(5, 5)

        assertEquals(Point(5, 4), engine.nextPoint(point, Direction.UP))
        assertEquals(Point(5, 6), engine.nextPoint(point, Direction.DOWN))
        assertEquals(Point(4, 5), engine.nextPoint(point, Direction.LEFT))
        assertEquals(Point(6, 5), engine.nextPoint(point, Direction.RIGHT))
    }

    @Test
    fun testGenerateNewFood() {
        (0..10).forEach {x->
            (0..10).forEach {y->
                val snake = listOf(Point(x, y))
                val food = engine.generateNewFood(snake)
                assertTrue(food != null)
                assertTrue(food !in snake)
            }
        }
    }

    @Test
    fun `rotate should not change direction when opposite`() {
        engine.rotate(Direction.LEFT)
        engine.rotate(Direction.RIGHT)
        assertEquals((engine.state.direction),Direction.LEFT)
    }

    @Test
    fun testRestartGame() {
        engine.run()
        val previousState = engine.state

        engine.restart()

        assertEquals(GameStatus.Running, engine.state.status)
        assertEquals(0, engine.state.score)
        assertEquals(previousState.maxScore, engine.state.maxScore)
        assertEquals(previousState.gridSize, engine.state.gridSize)
    }
}




