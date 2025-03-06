package com.github.droidbaza.jcsnake


import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createComposeRule
import com.github.droidbaza.jcsnake.model.Direction
import com.github.droidbaza.jcsnake.model.GameState
import com.github.droidbaza.jcsnake.model.GameStatus
import com.github.droidbaza.jcsnake.ui.SnakeGameRoute
import com.github.droidbaza.jcsnake.ui.SnakeIntent
import com.github.droidbaza.jcsnake.ui.SnakeViewModel
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import kotlinx.coroutines.flow.MutableStateFlow
import org.junit.Before
import org.junit.Rule
import org.junit.Test

class SnakeGameTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    private lateinit var viewModel: SnakeViewModel

    @Before
    fun setUp() {
        viewModel = mockk(relaxed = true)
    }

    @Test
    fun testPauseOrResume() {
        composeTestRule.setContent {
            SnakeGameRoute(viewModel)
        }
        val gameCanvas = composeTestRule.onNodeWithContentDescription("GameCanvas")
        gameCanvas.performClick()
        verify { viewModel.sendIntent(SnakeIntent.PauseOrResume) }
    }

    @Test
    fun testRestartGame() {
        val gameState = GameState(status = GameStatus.GameOver, speed = 1000)
        every { viewModel.state } returns MutableStateFlow(gameState)

        composeTestRule.setContent {
            SnakeGameRoute(viewModel = viewModel)
        }

        val gameOverText = composeTestRule.onNodeWithText("GAME OVER")
        gameOverText.performClick()
        verify { viewModel.sendIntent(SnakeIntent.Restart) }
    }

    @Test
    fun testRotateDirection() {
        composeTestRule.setContent {
            SnakeGameRoute(viewModel = viewModel)
        }

        val rotateRightButton = composeTestRule.onNodeWithContentDescription("btn right")
        rotateRightButton.performClick()

        verify { viewModel.sendIntent(SnakeIntent.Rotate(Direction.RIGHT)) }
    }

    @Test
    fun testGameStatusDisplay() {
        val runningState = GameState(status = GameStatus.Running, speed = 1000)
        every { viewModel.state } returns MutableStateFlow(runningState)

        composeTestRule.setContent {
            SnakeGameRoute(viewModel = viewModel)
        }

        // Проверяем, что отображается правильный текст
        composeTestRule.onNodeWithText("GAME OVER").assertDoesNotExist()

        // Устанавливаем состояние игры как "GameOver"
        val gameOverState = GameState(status = GameStatus.GameOver, speed = 1000)
        every { viewModel.state } returns MutableStateFlow(gameOverState)

        composeTestRule.setContent {
            SnakeGameRoute(viewModel = viewModel)
        }

        // Проверяем, что отображается "GAME OVER"
        composeTestRule.onNodeWithText("GAME OVER").assertExists()
    }
}
