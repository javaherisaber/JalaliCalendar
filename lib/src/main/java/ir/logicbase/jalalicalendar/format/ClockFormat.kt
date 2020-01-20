package ir.logicbase.jalalicalendar.format

import ir.logicbase.jalalicalendar.Clock
import ir.logicbase.jalalicalendar.extension.toLeadingZero

/**
 * [Clock] formatter
 *
 * @property pattern to be parsed or formatted by this class is as following
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
 *
 * If you want to omit zero values from resulting format just put two number after each pattern
 * these numbers define how many char's to be omitted before and after value
 * for example i want to omit minute if zero would be => H:tt10 for Clock(13, 45, 0) would be 13:45
 * and for Clock(13, 0, 0) would be 13
 */
class ClockFormat(private val pattern: String) {

    fun format(clock: Clock): String = buildString {
        val index = FormatIndex(0)
        while (index.pos < pattern.length) {
            format(clock, index, this)
            index.pos++
        }
    }

    internal fun format(clock: Clock, index: FormatIndex, sb: StringBuilder): Unit = with(sb) {
        fun process(value: Int, toLeadingZero: Boolean, offset: Int = 0) {
            val afterPos1 = index.pos + 1 + offset
            val afterPos2 = index.pos + 2 + offset
            if (afterPos1 < pattern.length && afterPos2 < pattern.length &&
                pattern[afterPos1].isDigit() && pattern[afterPos2].isDigit()
            ) {
                // we must omit before or after chars if value is zero because pattern contains number
                // eg. HH:tt10
                val beforeCount = pattern[afterPos1].toString().toInt()
                val afterCount = pattern[afterPos2].toString().toInt()
                if (value == 0) {
                    val result = this.dropLast(beforeCount)
                    this.clear()
                    append(result)
                    index.pos += beforeCount + afterCount // move index to pass omitted chars
                } else {
                    append(if (toLeadingZero) value.toLeadingZero() else value)
                }
                index.pos += 2 // move index to pass 2 digits
            } else {
                // no need to omit chars
                append(if (toLeadingZero) value.toLeadingZero() else value)
            }
        }
        when (pattern[index.pos]) {
            PATTERN_HOUR_NUMBER_24_1 -> {
                if (index.peak(pattern, 1) == PATTERN_HOUR_NUMBER_24_0) {
                    // HH
                    process(clock.hour, true, 1)
                    index.pos++
                } else {
                    // H
                    process(clock.hour, false)
                }
            }
            PATTERN_HOUR_NUMBER_12_1 -> {
                if (index.peak(pattern, 1) == PATTERN_HOUR_NUMBER_12_0) {
                    // hh
                    process(clock.hour % 12, true, 1)
                    index.pos++
                } else {
                    // h
                    process(clock.hour % 12, false)
                }
            }
            PATTERN_MINUTE_NUMBER_1 -> {
                if (index.peak(pattern, 1) == PATTERN_MINUTE_NUMBER_0) {
                    // tt
                    process(clock.minute, true, 1)
                    index.pos++
                } else {
                    // t
                    process(clock.minute, false)
                }
            }
            PATTERN_SECOND_NUMBER_1 -> {
                if (index.peak(pattern, 1) == PATTERN_SECOND_NUMBER_0) {
                    // ss
                    process(clock.second, true, 1)
                    index.pos++
                } else {
                    // s
                    process(clock.second, false)
                }
            }
            PATTERN_AM_PM_LETTER -> {
                if (isHourPM(clock.hour)) {
                    append(PM_LETTER)
                } else {
                    append(AM_LETTER)
                }
            }
            PATTERN_AM_PM_WORD -> {
                if (isHourPM(clock.hour)) {
                    append(PM_WORD)
                } else {
                    append(AM_WORD)
                }
            }
            else -> append(pattern[index.pos])
        }
    }

    /**
     * Parse [source] date time to [Clock] instance
     *
     * Only support this pattern: [PARSE_PATTERN_HOUR], [PARSE_PATTERN_HOUR_MINUTE]
     * and [PARSE_PATTERN_HOUR_MINUTE_SECOND]
     */
    fun parse(source: String): Clock {
        require(
            pattern == PARSE_PATTERN_HOUR || pattern == PARSE_PATTERN_HOUR_MINUTE
                    || pattern == PARSE_PATTERN_HOUR_MINUTE_SECOND
        ) {
            "Only $PARSE_PATTERN_HOUR, $PARSE_PATTERN_HOUR_MINUTE " +
                    "and $PARSE_PATTERN_HOUR_MINUTE_SECOND patterns are supported"
        }
        val parts = source.split(PARSE_DELIMITER_TIME)
        return Clock(
            if (parts.isNotEmpty()) parts[0].toInt() else 0,
            if (parts.size >= 2) parts[1].toInt() else 0,
            if (parts.size == 3) parts[2].toInt() else 0
        )
    }

    private fun isHourPM(hour: Int): Boolean = (hour / 12) >= 1.0

    companion object {
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

        const val PARSE_DELIMITER_TIME = ':'
        const val PARSE_PATTERN_HOUR = "HH"
        const val PARSE_PATTERN_HOUR_MINUTE = "HH:tt"
        const val PARSE_PATTERN_HOUR_MINUTE_SECOND = "HH:tt:ss"
    }
}