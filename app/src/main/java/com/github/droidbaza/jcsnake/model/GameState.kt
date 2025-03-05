package com.github.droidbaza.jcsnake.model

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