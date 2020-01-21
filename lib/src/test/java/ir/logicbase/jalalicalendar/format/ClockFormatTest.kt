package ir.logicbase.jalalicalendar.format

import ir.logicbase.jalalicalendar.Clock
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test

class ClockFormatTest {

    private lateinit var clock: Clock

    @Before
    fun before() {
        clock = Clock(13, 45, 0)
    }

    @Test
    fun format() {
        assertEquals(ClockFormat(PATTERN1).format(clock), TIME1)
        assertEquals(ClockFormat(PATTERN2).format(clock), TIME2)
        assertEquals(ClockFormat(PATTERN3).format(clock), TIME3)
        assertEquals(ClockFormat(PATTERN4).format(clock), TIME4)
        assertEquals(ClockFormat(PATTERN5).format(clock), TIME5)

        assertEquals(ClockFormat(PATTERN6).format(Clock(15, 0, 0)), "15")
        assertEquals(ClockFormat(PATTERN6).format(Clock(15, 45, 0)), "15:45")
    }

    @Test
    fun parse() {
        assertEquals(ClockFormat(PATTERN1).parse(TIME1), clock)
    }

    companion object {
        const val PATTERN1 = "HH:ii:ss"
        const val PATTERN2 = "hh:ii:ss A"
        const val PATTERN3 = "hh:ii:ss a"
        const val PATTERN4 = "h:ii A"
        const val PATTERN5 = "HH:ii:ss10"
        const val PATTERN6 = "HH:ii10"

        const val TIME1 = "13:45:00"
        const val TIME2 = "01:45:00 عصر"
        const val TIME3 = "01:45:00 ع"
        const val TIME4 = "1:45 عصر"
        const val TIME5 = "13:45"
    }
}