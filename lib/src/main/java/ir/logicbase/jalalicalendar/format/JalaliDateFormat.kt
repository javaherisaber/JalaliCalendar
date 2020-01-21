package ir.logicbase.jalalicalendar.format

import ir.logicbase.jalalicalendar.DayOfWeekPersian
import ir.logicbase.jalalicalendar.JalaliCalendar
import ir.logicbase.jalalicalendar.MonthPersian
import ir.logicbase.jalalicalendar.extension.toLeadingZero
import ir.logicbase.jalalicalendar.format.ClockFormat.Companion.PARSE_DELIMITER_TIME
import java.util.*

/**
 * [JalaliCalendar] formatter
 *
 * @property pattern to be parsed or formatted by this class is as following
 * w for day of week number no leading zero eg. 1
 * ww for day of week number with leading zero eg. 01
 * W for first letter of week eg. چهارشنبه would be چ
 * WW for full name of week e.g چهارشنبه
 * d for day of month number no leading zero eg. 8
 * dd for day of month number with leading zero eg. 08
 * m for month number no leading zero eg. 1
 * mm for month number with leading zero eg. 01
 * M for month name eg. شهریور
 * yy for year number first two places from right eg. 1398 would be 98
 * yyyy for year number eg. 1398
 * H for hour of day number (24 hour-based) no leading zero eg. 7
 * HH for hour of day number (24 hour-based) with leading zero eg. 07
 * h for hour of day number (12 hour-based) no leading zero eg. 7
 * hh for hour of day number (12 hour-based) with leading zero eg. 07
 * t for minute number no leading zero eg. 5
 * tt for minute number with leading zero eg. 05
 * s for second number no leading zero eg. 5
 * ss for second number with leading zero eg. 05
 * a for Am/Pm first letter eg. ص
 * A for Am/Pm full name eg. صبح
 */
class JalaliDateFormat(private val pattern: String) {

    fun format(calendar: JalaliCalendar): String = buildString {
        val index = FormatIndex(0)
        while (index.pos < pattern.length) {
            when (pattern[index.pos]) {
                PATTERN_DAY_OF_WEEK_NUMBER_1 -> {
                    val dayOfWeek = DayOfWeekPersian.valuesOrderedInPersian.indexOf(calendar.dayOfWeek) + 1
                    if (index.peak(pattern, 1) == PATTERN_DAY_OF_WEEK_NUMBER_0) {
                        // ww
                        append(dayOfWeek.toLeadingZero())
                        index.pos++
                    } else {
                        // w
                        append(dayOfWeek)
                    }
                }
                PATTERN_DAY_OF_WEEK_LETTER -> {
                    val dayOfWeekText = calendar.dayOfWeek.persianText
                    if (index.peak(pattern, 1) == PATTERN_DAY_OF_WEEK_WORD) {
                        // WW
                        append(dayOfWeekText)
                        index.pos++
                    } else {
                        // W
                        append(dayOfWeekText[0])
                    }
                }
                PATTERN_DAY_OF_MONTH_NUMBER_1 -> {
                    val dayOfMonth = calendar.get(Calendar.DAY_OF_MONTH)
                    if (index.peak(pattern, 1) == PATTERN_DAY_OF_MONTH_NUMBER_0) {
                        // dd
                        append(dayOfMonth.toLeadingZero())
                        index.pos++
                    } else {
                        // d
                        append(dayOfMonth)
                    }
                }
                PATTERN_MONTH_NUMBER_1 -> {
                    val month = calendar.get(Calendar.MONTH) + 1
                    if (index.peak(pattern, 1) == PATTERN_MONTH_NUMBER_0) {
                        // mm
                        append(month.toLeadingZero())
                        index.pos++
                    } else {
                        // m
                        append(month)
                    }
                }
                PATTERN_MONTH_WORD -> {
                    append(calendar.month.persianText)
                }
                PATTERN_YEAR_NUMBER_1 -> {
                    val year = calendar.get(Calendar.YEAR)
                    if (index.peak(pattern, 3) == PATTERN_YEAR_NUMBER_4) {
                        // yyyy
                        append(year)
                        index.pos += 3
                    } else if (index.peak(pattern, 1) == PATTERN_YEAR_NUMBER_2) {
                        // yy
                        append(year % 100)
                        index.pos++
                    }
                }
                else -> ClockFormat(pattern).format(calendar.clock, index, this)
            }
            index.pos++
        }
    }

    /**
     * Parse [source] date time to [JalaliCalendar] instance
     *
     * Only support this pattern: [PARSE_PATTERN_DATE_TIME]
     * and this one: [PARSE_PATTERN_DATE]
     */
    fun parse(source: String): JalaliCalendar {
        require(pattern == PARSE_PATTERN_DATE || pattern == PARSE_PATTERN_DATE_TIME) {
            "Only $PARSE_PATTERN_DATE_TIME and $PARSE_PATTERN_DATE patterns are supported"
        }
        val parts = source.split(PARSE_DELIMITER_DATE_TIME)
        return if (parts.size == 1) {
            // pattern is date
            val dateParts = source.split(PARSE_DELIMITER_DATE)
            JalaliCalendar(
                dateParts[0].toInt(), MonthPersian.of(dateParts[1].toInt() - 1), dateParts[2].toInt()
            )
        } else {
            // pattern is date time
            val dateParts = parts[0].split(PARSE_DELIMITER_DATE)
            val timeParts = parts[1].split(PARSE_DELIMITER_TIME)
            JalaliCalendar(
                dateParts[0].toInt(), MonthPersian.of(dateParts[1].toInt() - 1), dateParts[2].toInt(),
                timeParts[0].toInt(), timeParts[1].toInt(), timeParts[2].toInt()
            )
        }
    }

    companion object {
        const val PATTERN_DAY_OF_WEEK_NUMBER_1 = 'w'
        const val PATTERN_DAY_OF_WEEK_NUMBER_0 = "ww"
        const val PATTERN_DAY_OF_WEEK_LETTER = 'W'
        const val PATTERN_DAY_OF_WEEK_WORD = "WW"
        const val PATTERN_DAY_OF_MONTH_NUMBER_1 = 'd'
        const val PATTERN_DAY_OF_MONTH_NUMBER_0 = "dd"
        const val PATTERN_MONTH_NUMBER_1 = 'm'
        const val PATTERN_MONTH_NUMBER_0 = "mm"
        const val PATTERN_MONTH_WORD = 'M'
        const val PATTERN_YEAR_NUMBER_1 = 'y'
        const val PATTERN_YEAR_NUMBER_2 = "yy"
        const val PATTERN_YEAR_NUMBER_4 = "yyyy"

        const val PARSE_PATTERN_DATE = "yyyy/mm/dd"
        const val PARSE_DELIMITER_DATE_TIME = ' '
        const val PARSE_DELIMITER_DATE = '/'
        const val PARSE_PATTERN_DATE_TIME = "yyyy/mm/dd HH:tt:ss"
    }
}