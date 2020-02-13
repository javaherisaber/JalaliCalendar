package ir.logicbase.jalalicalendar

import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class ClockTest {

    @Test
    fun increaseDecrease() {
        var clock = Clock(10, 45, 0)
        assertEquals(++clock, Clock(10, 45, 1))
        assertEquals(--clock, Clock(10, 45, 0))
    }

    @Test
    fun compare() {
        val first = Clock(10, 0, 0)
        val second = Clock(23, 0, 0)
        assertTrue(first < second)
        assertTrue(second == second)
        assertTrue(second > first)
    }

    @Test
    fun set() {
        val seconds = 3 * Clock.HOUR_DURATION_SECONDS + 45 * Clock.MINUTE_DURATION_SECONDS
        val clock = Clock(seconds)
        assertEquals(clock, Clock(3, 45, 0))
    }

    @Test
    fun of() {
        val clock = Clock.of("14:37:00")
        assertEquals(clock, Clock(14, 37, 0))
    }

    @Test
    fun timesFloat() {
        var clock = Clock(1, 0, 0)
        clock *= 1.2f
        assertEquals(clock, Clock(1, 12,0))
    }
}