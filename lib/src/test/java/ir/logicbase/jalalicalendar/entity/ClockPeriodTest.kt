package ir.logicbase.jalalicalendar.entity

import ir.logicbase.jalalicalendar.Clock
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class ClockPeriodTest {

    @Test
    fun isOverlap() {
        val first = ClockPeriod(Clock(10, 0, 0), Clock(12, 0, 0))
        val second = ClockPeriod(Clock(11, 0, 0), Clock(14, 0, 0))
        val third = ClockPeriod(Clock(12, 0, 0), Clock(14, 0, 0))
        assertTrue(first.isOverlap(second))
        assertFalse(first.isOverlap(third))
    }
}