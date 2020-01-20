package ir.logicbase.jalalicalendar.extension

import ir.logicbase.jalalicalendar.Clock
import ir.logicbase.jalalicalendar.format.ClockFormat

/**
 * Format [Clock] instance with given [pattern]
 */
fun Clock.format(
    pattern: String = ClockFormat.PARSE_PATTERN_HOUR_MINUTE_SECOND
): String = ClockFormat(pattern).format(this)