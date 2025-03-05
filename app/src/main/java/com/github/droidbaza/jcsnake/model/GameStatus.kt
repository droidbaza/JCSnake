package com.github.droidbaza.jcsnake.model

sealed class GameStatus {
    object Pause : GameStatus()
    object Running : GameStatus()
    object GameOver : GameStatus()
}