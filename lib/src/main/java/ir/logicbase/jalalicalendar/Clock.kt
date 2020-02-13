package ir.logicbase.jalalicalendar

import ir.logicbase.jalalicalendar.extension.format
import ir.logicbase.jalalicalendar.format.ClockFormat
import java.util.*

/**
 * Data holder to represent a 24hour based clock
 */
data class Clock(
    var hour: Int = HOUR_MIN_VALUE,
    var minute: Int = MINUTE_MIN_VALUE,
    var second: Int = SECOND_MIN_VALUE
) : Comparable<Clock> {

    constructor(clock: Clock) : this(clock.hour, clock.minute, clock.second)

    constructor(seconds: Int) : this() {
        set(seconds)
    }

    fun set(seconds: Int) {
        hour = seconds.div(HOUR_DURATION_SECONDS)
        minute = seconds.rem(HOUR_DURATION_SECONDS).div(MINUTE_DURATION_SECONDS)
        second = seconds.rem(MINUTE_DURATION_SECONDS)
    }

    init {
        validateArguments(hour, minute, second)
    }

    /**
     * Validate primary constructor inputs
     */
    @Throws(IllegalArgumentException::class)
    private fun validateArguments(hour: Int, minute: Int, second: Int) {
        require(hour in HOUR_MIN_VALUE..HOUR_MAX_VALUE) {
            "hour = $hour is wrong, it must be from $HOUR_MIN_VALUE until $HOUR_MAX_VALUE"
        }
        require(minute in MINUTE_MIN_VALUE..MINUTE_MAX_VALUE) {
            "minute = $minute is wrong, it must be from $MINUTE_MIN_VALUE until $MINUTE_MAX_VALUE"
        }
        require(second in SECOND_MIN_VALUE..SECOND_MAX_VALUE) {
            "second = $second is wrong, it must be from $SECOND_MIN_VALUE until $SECOND_MAX_VALUE"
        }
    }

    /**
     * @return true if clock is 00:00:00, false otherwise
     */
    fun isMin(): Boolean = (second == SECOND_MIN_VALUE && minute == MINUTE_MIN_VALUE && hour == HOUR_MIN_VALUE)

    /**
     * Set minimum possible values
     */
    fun setMin() {
        second = SECOND_MIN_VALUE
        minute = MINUTE_MIN_VALUE
        hour = HOUR_MIN_VALUE
    }

    /**
     * @return true if clock is 23:59:59, false otherwise
     */
    fun isMax(): Boolean = (second == SECOND_MAX_VALUE && minute == MINUTE_MAX_VALUE && hour == HOUR_MAX_VALUE)

    /**
     * Set maximum possible values
     */
    fun setMax() {
        second = SECOND_MAX_VALUE
        minute = MINUTE_MAX_VALUE
        hour = HOUR_MAX_VALUE
    }

    fun toSeconds() = hour * HOUR_DURATION_SECONDS + minute * MINUTE_DURATION_SECONDS + second

    fun toMinutes() = hour * 60 + minute

    override fun toString(): String = this.format()

    override fun compareTo(other: Clock): Int = when {
        hour != other.hour -> hour - other.hour
        minute != other.minute -> minute - other.minute
        else -> second - other.second
    }

    operator fun minus(other: Clock) = Clock(this.toSeconds() - other.toSeconds())

    operator fun plus(other: Clock) = Clock(this.toSeconds() + other.toSeconds())

    operator fun inc(): Clock {
        set(this.toSeconds() + 1)
        return this
    }

    operator fun dec(): Clock {
        set(this.toSeconds() - 1)
        return this
    }

    operator fun times(value: Float): Clock {
        set((this.toSeconds() * value).toInt())
        return this
    }

    companion object {
        const val MINUTE_DURATION_SECONDS = 60
        const val HOUR_DURATION_SECONDS = 3600

        const val SECOND_MAX_VALUE = 59
        const val MINUTE_MAX_VALUE = 59
        const val HOUR_MAX_VALUE = 23

        const val SECOND_MIN_VALUE = 0
        const val MINUTE_MIN_VALUE = 0
        const val HOUR_MIN_VALUE = 0

        /**
         * Parse [source] containing clock int [Clock] instance
         *
         * @param source string to be parsed eg. 17:30:00
         * @param pattern format of clock in [source] eg. HH:tt:ss
         * @return [Clock] parsed from given [source]
         */
        @JvmStatic
        fun of(source: String, pattern: String = ClockFormat.PARSE_PATTERN_HOUR_MINUTE_SECOND): Clock {
            return ClockFormat(pattern).parse(source)
        }

        @JvmStatic
        fun current(timeZone: TimeZone = TimeZone.getDefault()): Clock = Clock(JalaliCalendar(timeZone).clock)
    }
}