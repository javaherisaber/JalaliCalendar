package ir.logicbase.jalalicalendar

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
        var index = 0
        while (index < pattern.length) {
            when (pattern[index]) {
                PATTERN_DAY_OF_WEEK_NUMBER_1 -> {
                    val dayOfWeek = DayOfWeekPersian.valuesOrderedInPersian
                        .indexOf(calendar.dayOfWeek) + 1
                    if (pattern.substring(index..index + 1) == PATTERN_DAY_OF_WEEK_NUMBER_0) {
                        // ww
                        append(dayOfWeek.toLeadingZero())
                        index++
                    } else {
                        // w
                        append(dayOfWeek)
                    }
                }
                PATTERN_DAY_OF_WEEK_LETTER -> {
                    val dayOfWeekText = calendar.dayOfWeek.persianText
                    if (pattern.substring(index..index + 1) == PATTERN_DAY_OF_WEEK_WORD) {
                        // WW
                        append(dayOfWeekText)
                        index++
                    } else {
                        // W
                        append(dayOfWeekText[0])
                    }
                }
                PATTERN_DAY_OF_MONTH_NUMBER_1 -> {
                    val dayOfMonth = calendar.get(Calendar.DAY_OF_MONTH)
                    if (pattern.substring(index..index + 1) == PATTERN_DAY_OF_MONTH_NUMBER_0) {
                        // dd
                        append(dayOfMonth.toLeadingZero())
                        index++
                    } else {
                        // d
                        append(dayOfMonth)
                    }
                }
                PATTERN_MONTH_NUMBER_1 -> {
                    val month = calendar.get(Calendar.MONTH) + 1
                    if (pattern.substring(index..index + 1) == PATTERN_MONTH_NUMBER_0) {
                        // mm
                        append(month.toLeadingZero())
                        index++
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
                    if (pattern.substring(index..index + 3) == PATTERN_YEAR_NUMBER_4) {
                        // yyyy
                        append(year)
                        index += 3
                    } else if (pattern.substring(index..index + 1) == PATTERN_YEAR_NUMBER_2) {
                        // yy
                        append(year % 100)
                        index++
                    }
                }
                PATTERN_HOUR_NUMBER_24_1 -> {
                    val hour = calendar.get(Calendar.HOUR_OF_DAY)
                    if (pattern.substring(index..index + 1) == PATTERN_HOUR_NUMBER_24_0) {
                        // HH
                        append(hour.toLeadingZero())
                        index++
                    } else {
                        // H
                        append(hour)
                    }
                }
                PATTERN_HOUR_NUMBER_12_1 -> {
                    val hour = calendar.get(Calendar.HOUR_OF_DAY)
                    if (pattern.substring(index..index + 1) == PATTERN_HOUR_NUMBER_12_0) {
                        // hh
                        append((hour % 12).toLeadingZero())
                        index++
                    } else {
                        // h
                        append(hour % 12)
                    }
                }
                PATTERN_MINUTE_NUMBER_1 -> {
                    val minute = calendar.get(Calendar.MINUTE)
                    if (pattern.substring(index..index + 1) == PATTERN_MINUTE_NUMBER_0) {
                        // tt
                        append(minute.toLeadingZero())
                        index++
                    } else {
                        // t
                        append(minute)
                    }
                }
                PATTERN_SECOND_NUMBER_1 -> {
                    val second = calendar.get(Calendar.SECOND)
                    if (pattern.substring(index..index + 1) == PATTERN_SECOND_NUMBER_0) {
                        // ss
                        append(second.toLeadingZero())
                        index++
                    } else {
                        // s
                        append(second)
                    }
                }
                PATTERN_AM_PM_LETTER -> {
                    if (isHourPM(calendar)) {
                        append(PM_LETTER)
                    } else {
                        append(AM_LETTER)
                    }
                }
                PATTERN_AM_PM_WORD -> {
                    if (isHourPM(calendar)) {
                        append(PM_WORD)
                    } else {
                        append(AM_WORD)
                    }
                }
                else -> append(pattern[index])
            }
            index++
        }
    }

    private fun isHourPM(calendar: JalaliCalendar): Boolean {
        val hour = calendar.get(Calendar.HOUR_OF_DAY)
        return (hour / 12) >= 1.0
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

    /**
     * @return number with leading zero eg. 07
     */
    private fun Int.toLeadingZero() = if (this < 10) "0$this" else this.toString()

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
        const val PATTERN_HOUR_NUMBER_24_1 = 'H'
        const val PATTERN_HOUR_NUMBER_24_0 = "HH"
        const val PATTERN_HOUR_NUMBER_12_1 = 'h'
        const val PATTERN_HOUR_NUMBER_12_0 = "hh"
        const val PATTERN_MINUTE_NUMBER_1 = 't'
        const val PATTERN_MINUTE_NUMBER_0 = "tt"
        const val PATTERN_SECOND_NUMBER_1 = 's'
        const val PATTERN_SECOND_NUMBER_0 = "ss"
        const val PATTERN_AM_PM_LETTER = 'a'
        const val PATTERN_AM_PM_WORD = 'A'

        const val AM_LETTER = "ص"
        const val AM_WORD = "صبح"
        const val PM_LETTER = "ع"
        const val PM_WORD = "عصر"

        const val PARSE_PATTERN_DATE = "yyyy/mm/dd"
        const val PARSE_DELIMITER_DATE_TIME = ' '
        const val PARSE_DELIMITER_DATE = '/'
        const val PARSE_DELIMITER_TIME = ':'
        const val PARSE_PATTERN_DATE_TIME = "yyyy/mm/dd HH:tt:ss"
    }
}