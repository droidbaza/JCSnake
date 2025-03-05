package com.github.droidbaza.jcsnake

import com.github.droidbaza.jcsnake.engine.GameEngine
import com.github.droidbaza.jcsnake.model.Direction
import com.github.droidbaza.jcsnake.model.GameState
import com.github.droidbaza.jcsnake.model.GameStatus
import com.github.droidbaza.jcsnake.ui.SnakeIntent
import com.github.droidbaza.jcsnake.ui.SnakeViewModel
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test
import org.mockito.Mock
import org.mockito.Mockito.mock
import org.mockito.Mockito.verify
import org.mockito.Mockito.`when`
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class SnakeViewModelTest {

    private lateinit var viewModel: SnakeViewModel

    @Mock
    private lateinit var gameEngine: GameEngine

    @Before
    fun setup() {
        gameEngine = mock(GameEngine::class.java)
        `when`(gameEngine.state).thenReturn(GameState())
        viewModel = SnakeViewModel(gameEngine = gameEngine)
    }

    @Test
    fun testSendIntentRotate() {
        val direction = Direction.UP
        viewModel.sendIntent(SnakeIntent.Rotate(direction))
        verify(gameEngine).rotate(direction)
    }

    @Test
    fun testSendIntentRun() {
        viewModel.sendIntent(SnakeIntent.Run)
        verify(gameEngine).run()
    }

    @Test
    fun testSendIntentPauseOrResume() {
        viewModel.sendIntent(SnakeIntent.PauseOrResume)
        verify(gameEngine).pauseOrResume()
    }

    @Test
    fun testSendIntentRestart() {
        viewModel.sendIntent(SnakeIntent.Restart)
        verify(gameEngine).restart()
    }

    @Test
    fun testStateUpdateOnIntent() {
        val initialState = GameState()
        `when`(gameEngine.state).thenReturn(initialState.copy(status = GameStatus.Running))
        viewModel.sendIntent(SnakeIntent.Run)
        assertEquals(GameStatus.Running, viewModel.state.value.status)
    }

    @Test
    fun testStateFlowUpdates() = runTest {
        val gameState = GameState(status = GameStatus.Running, speed = 100L)
        `when`(gameEngine.state).thenReturn(gameState)
        val job = launch {
            val state = viewModel.state.first()
            assertTrue(state.status is GameStatus.Running)
        }
        viewModel.sendIntent(SnakeIntent.Run)
        job.join()
    }
}
