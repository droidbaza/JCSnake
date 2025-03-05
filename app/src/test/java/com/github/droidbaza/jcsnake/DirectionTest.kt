package com.github.droidbaza.jcsnake

import com.github.droidbaza.jcsnake.model.Direction
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class DirectionTest {

    @Test
    fun isOppositeReturnsTrueForOppositeDirections() {
        assertTrue(Direction.UP.isOpposite(Direction.DOWN))
        assertTrue(Direction.DOWN.isOpposite(Direction.UP))
        assertTrue(Direction.LEFT.isOpposite(Direction.RIGHT))
        assertTrue(Direction.RIGHT.isOpposite(Direction.LEFT))
    }

    @Test
    fun isOppositeReturnsFalseForNonOppositeDirections() {
        assertFalse(Direction.UP.isOpposite(Direction.LEFT))
        assertFalse(Direction.DOWN.isOpposite(Direction.RIGHT))
    }
}
