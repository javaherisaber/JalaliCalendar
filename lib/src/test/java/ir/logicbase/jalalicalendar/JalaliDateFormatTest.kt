package ir.logicbase.jalalicalendar

import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test

class JalaliDateFormatTest {

    private lateinit var calendar: JalaliCalendar

    @Before
    fun before() {
        calendar = JalaliCalendar(1398, MonthPersian.Dey, 30, 13, 45, 30)
    }

    @Test
    fun format() {
        assertEquals(JalaliDateFormat(PATTERN1).format(calendar), DATE_TIME1)
        assertEquals(JalaliDateFormat(PATTERN2).format(calendar), DATE_TIME2)
        assertEquals(JalaliDateFormat(PATTERN3).format(calendar), DATE_TIME3)
        assertEquals(JalaliDateFormat(PATTERN4).format(calendar), DATE_TIME4)
        assertEquals(JalaliDateFormat(PATTERN5).format(calendar), DATE_TIME5)
        assertEquals(JalaliDateFormat(PATTERN6).format(calendar), DATE_TIME6)
        assertEquals(JalaliDateFormat(PATTERN7).format(calendar), DATE_TIME7)
    }

    @Test
    fun parse() {
        val calendar2 = JalaliCalendar(1398, MonthPersian.Dey, 30)
        assertEquals(JalaliDateFormat(PATTERN2).parse(DATE_TIME2), calendar2)
        assertEquals(JalaliDateFormat(PATTERN7).parse(DATE_TIME7), calendar)
    }

    companion object {
        const val PATTERN1 = "WW dd M yyyy ساعت HH:tt:ss"
        const val PATTERN2 = "yyyy/mm/dd"
        const val PATTERN3 = "WW dd M yyyy"
        const val PATTERN4 = "dd M yyyy"
        const val PATTERN5 = "ww dd M yyyy ساعت HH:tt a"
        const val PATTERN6 = "W dd M yy ساعت HH:tt A"
        const val PATTERN7 = "yyyy/mm/dd HH:tt:ss"

        const val DATE_TIME1 = "دوشنبه 30 دی 1398 ساعت 13:45:30"
        const val DATE_TIME2 = "1398/10/30"
        const val DATE_TIME3 = "دوشنبه 30 دی 1398"
        const val DATE_TIME4 = "30 دی 1398"
        const val DATE_TIME5 = "03 30 دی 1398 ساعت 13:45 ع"
        const val DATE_TIME6 = "د 30 دی 98 ساعت 13:45 عصر"
        const val DATE_TIME7 = "1398/10/30 13:45:30"
    }
}