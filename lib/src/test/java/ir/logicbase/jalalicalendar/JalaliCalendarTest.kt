package ir.logicbase.jalalicalendar

import org.junit.Assert.*
import org.junit.Test
import java.util.*

class JalaliCalendarTest {

    @Test
    fun setFields() {
        val calendar = JalaliCalendar(1398, MonthPersian.Dey, 28, 12, 45, 30)
        assertEquals(calendar[Calendar.YEAR].toLong(), 1398)
        assertEquals(calendar[Calendar.MONTH].toLong(), 9)
        assertEquals(calendar[Calendar.DAY_OF_MONTH].toLong(), 28)
        assertEquals(calendar[Calendar.HOUR_OF_DAY].toLong(), 12)
        assertEquals(calendar[Calendar.MINUTE].toLong(), 45)
        assertEquals(calendar[Calendar.SECOND].toLong(), 30)
    }

    @Test
    fun addFields() {
        val calendar = JalaliCalendar(1398, MonthPersian.Dey, 28, 12, 45, 30)
        assertEquals(calendar.dayOfWeek, DayOfWeekPersian.Shanbeh)
        calendar.add(Calendar.DAY_OF_MONTH, 1)
        assertEquals(calendar.dayOfWeek, DayOfWeekPersian.Yekshanbeh)
    }

    @Test
    fun isLeapYear() {
        assertTrue(JalaliCalendar.isLeapYear(1399))
        assertTrue(JalaliCalendar.isLeapYear(1473))
        assertTrue(JalaliCalendar.isLeapYear(1395))
        assertTrue(JalaliCalendar.isLeapYear(1370))
        assertTrue(JalaliCalendar.isLeapYear(1403))
        assertTrue(JalaliCalendar.isLeapYear(1391))
        assertTrue(JalaliCalendar.isLeapYear(1379))
        assertTrue(JalaliCalendar.isLeapYear(1387))
        assertFalse(JalaliCalendar.isLeapYear(1398))
    }

    @Test
    fun compare() {
        val first = JalaliCalendar(1398, MonthPersian.Dey, 28)
        val second = JalaliCalendar(1398, MonthPersian.Dey, 20)
        assertTrue(first > second)
        second.set(Calendar.YEAR, 1399)
        assertTrue(first < second)
        second.set(Calendar.YEAR, 1398)
        first.set(MonthPersian.Esfand)
        assertTrue(first > second)
        first.set(MonthPersian.Dey)
        first.set(Calendar.DAY_OF_MONTH, 20)
        assertEquals(first, second)
    }

    @Test
    fun rangeTo() {
        val start = JalaliCalendar(1398, MonthPersian.Dey, 1)
        val end = JalaliCalendar(1398, MonthPersian.Dey, 30)
        var index = 1
        for (date in start..end) {
            assertEquals(date.get(Calendar.DAY_OF_MONTH), index)
            index++
        }
    }
}