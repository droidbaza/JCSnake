package com.github.droidbaza.jcsnake.engine

import com.github.droidbaza.jcsnake.model.Direction
import com.github.droidbaza.jcsnake.model.GameState

interface GameEngine {
    val state: GameState
    fun run()
    fun restart()
    fun pauseOrResume()
    fun rotate(direction: Direction)
}