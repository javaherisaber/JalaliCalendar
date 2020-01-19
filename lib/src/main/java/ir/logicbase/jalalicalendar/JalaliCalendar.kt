package ir.logicbase.jalalicalendar

import java.util.*
import kotlin.math.abs
import kotlin.math.floor
import kotlin.math.roundToInt

class JalaliCalendar : Calendar {

    constructor(year: Int, month: Int, day: Int) : super(
        TimeZone.getDefault(),
        Locale.getDefault()
    ) {
        set(year, month, day)
    }

    constructor(year: Int, month: MonthPersian, day: Int) : this(year, month.ordinal, day)

    constructor(
        year: Int, month: Int, day: Int, hour: Int, minute: Int
    ) : super(TimeZone.getDefault(), Locale.getDefault()) {
        set(year, month, day, hour, minute)
    }

    constructor(year: Int, month: MonthPersian, day: Int, hour: Int, minute: Int) : this(
        year, month.ordinal, day, hour, minute
    )

    constructor(year: Int, month: Int, day: Int, hour: Int, minute: Int, second: Int) : super(
        TimeZone.getDefault(), Locale.getDefault()
    ) {
        set(year, month, day, hour, minute, second)
    }

    constructor(
        year: Int, month: MonthPersian, day: Int, hour: Int, minute: Int, second: Int
    ) : this(
        year, month.ordinal, day, hour, minute, second
    )

    constructor(locale: Locale) : this(TimeZone.getDefault(), locale)

    @JvmOverloads
    constructor(
        timezone: TimeZone = TimeZone.getDefault(),
        locale: Locale = Locale.getDefault()
    ) : super(timezone, locale) {
        timeInMillis = System.currentTimeMillis()
    }

    constructor(calendar: Calendar) : this() {
        timeInMillis = calendar.timeInMillis
    }

    fun set(month: MonthPersian) = super.set(MONTH, month.ordinal)

    fun set(year: Int, month: MonthPersian, date: Int) = super.set(year, month.ordinal, date)

    override fun add(field: Int, value: Int) {
        var valueCopy = value
        if (valueCopy == 0) {
            return
        }
        require(!(field < 0 || field >= ZONE_OFFSET))
        if (field == ERA) {
            complete()
            if (fields[ERA] == AH) {
                if (valueCopy >= 0) {
                    return
                }
                set(ERA, BH)
            } else {
                if (valueCopy <= 0) {
                    return
                }
                set(ERA, AH)
            }
            complete()
            return
        }
        if (field == YEAR || field == MONTH) {
            complete()
            if (field == MONTH) {
                var month = fields[MONTH] + valueCopy
                if (month < 0) {
                    valueCopy = (month - 11) / 12
                    month = 12 + month % 12
                } else {
                    valueCopy = month / 12
                }
                set(MONTH, month % 12)
            }
            set(YEAR, fields[YEAR] + valueCopy)
            val days = daysInMonth
            if (fields[DATE] > days) {
                set(DATE, days)
            }
            complete()
            return
        }
        var multiplier: Long = 0
        timeInMillis // Update the time
        when (field) {
            MILLISECOND -> time += valueCopy.toLong()
            SECOND -> time += valueCopy * 1000L
            MINUTE -> time += valueCopy * 60000L
            HOUR, HOUR_OF_DAY -> time += valueCopy * 3600000L
            AM_PM -> multiplier = 43200000L
            DAY_OF_MONTH, DAY_OF_YEAR, DAY_OF_WEEK -> multiplier = 86400000L
            WEEK_OF_YEAR, WEEK_OF_MONTH, DAY_OF_WEEK_IN_MONTH -> multiplier = 604800000L
        }
        if (multiplier == 0L) {
            areFieldsSet = false
            complete()
            return
        }
        var delta = valueCopy * multiplier
        /*
         * Attempt to keep the hour and minute constant when we've crossed a DST
         * boundary and the user's units are AM_PM or larger. The typical
         * consequence is that calls to add(DATE, 1) will add 23, 24 or 25 hours
         * depending on whether the DST goes forward, constant, or backward.
         *
         * We know we've crossed a DST boundary if the new time will have a
         * different timezone offset. Adjust by adding the difference of the two
         * offsets. We don't adjust when doing so prevents the change from
         * crossing the boundary.
         */
        val zoneOffset = timeZone.rawOffset
        val offsetBefore = getOffset(time + zoneOffset)
        val offsetAfter = getOffset(time + zoneOffset + delta)
        val dstDelta = offsetBefore - offsetAfter
        if (getOffset(time + zoneOffset + delta + dstDelta) == offsetAfter) {
            delta += dstDelta.toLong()
        }
        time += delta
        areFieldsSet = false
        complete()
    }

    override fun computeFields() {
        val timeInZone = time + getOffset(time)
        val fixedDate = floor(timeInZone * 1.0 / ONE_DAY_IN_MILLIS).toInt() + EPOCH_OFFSET
        fields[YEAR] = getYearFromFixedDate(fixedDate)
        if (fields[YEAR] <= 0) {
            fields[YEAR] = -fields[YEAR] + 1
            fields[ERA] = BH
        } else {
            fields[ERA] = AH
        }
        val far1 = getFixedDateFar1(fields[YEAR], fields[ERA] == AH)
        fields[DAY_OF_YEAR] = fixedDate - far1 + 1
        if (fields[DAY_OF_YEAR] < ACCUMULATED_DAYS_IN_MONTH[6]) {
            fields[MONTH] = floor((fields[DAY_OF_YEAR] - 1) / 31.0).toInt() // month range is 0-11
        } else {
            fields[MONTH] = floor(
                (fields[DAY_OF_YEAR] - 1 - ACCUMULATED_DAYS_IN_MONTH[6]) / 30.0
            ).toInt() + 6
        }
        fields[DAY_OF_MONTH] = fields[DAY_OF_YEAR] - ACCUMULATED_DAYS_IN_MONTH[fields[MONTH]]
        var extra = timeInZone - (fixedDate - EPOCH_OFFSET) * ONE_DAY_IN_MILLIS
        fields[HOUR_OF_DAY] = floor(extra * 1.0 / ONE_HOUR_IN_MILLIS).toInt()
        if (fields[HOUR_OF_DAY] >= 12) {
            fields[HOUR] = fields[HOUR_OF_DAY] - 12
            fields[AM_PM] = PM
        } else {
            fields[HOUR] = fields[HOUR_OF_DAY] - 12
            fields[AM_PM] = AM
        }
        extra -= fields[HOUR_OF_DAY] * ONE_HOUR_IN_MILLIS
        fields[MINUTE] = floor(extra * 1.0 / ONE_MINUTE_IN_MILLIS).toInt()
        extra -= fields[MINUTE] * ONE_MINUTE_IN_MILLIS
        fields[SECOND] = floor(extra * 1.0 / ONE_SECOND_IN_MILLIS).toInt()
        extra -= fields[SECOND] * ONE_SECOND_IN_MILLIS
        fields[MILLISECOND] = extra.toInt()
        fields[DAY_OF_WEEK] =
            if (fixedDate >= 0) (fixedDate + 4) % 7 + 1 else (fixedDate - 1) % 7 + 7
    }

    override fun computeTime() {
        require(fields[YEAR] != 0) { "Year cannot be zero" }
        if (!isSet(ERA)) {
            fields[ERA] = AH
        }
        val extraYear = floor(fields[MONTH] / 12.0).toInt()
        if (extraYear != 0) {
            if ((fields[ERA] == AH) xor (extraYear > 0)) {
                if (fields[ERA] == AH && fields[YEAR] <= abs(extraYear)
                ) {
                    fields[YEAR] =
                        abs(extraYear) - fields[YEAR] + 1
                    set(ERA, BH)
                } else if (fields[ERA] == BH && fields[YEAR] <= abs(extraYear)
                ) {
                    fields[YEAR] =
                        abs(extraYear) - fields[YEAR] + 1
                    set(ERA, AH)
                } else if (fields[ERA] == AH) {
                    fields[YEAR] += extraYear // the same as -= Math.abs(extraYear)
                } else {
                    fields[YEAR] -= extraYear // the same as += Math.abs(extraYear)
                }
            } else {
                fields[YEAR] += abs(extraYear)
            }
        }
        fields[MONTH] %= 12 // months of a year is a fixed number (12)
        if (fields[MONTH] < 0) {
            fields[MONTH] += 12 // month range is 0-11
        }
        val fixedDate = getFixedDateFar1(fields[YEAR], fields[ERA] == AH) +
                ACCUMULATED_DAYS_IN_MONTH[fields[MONTH]] +
                if (isSet(DAY_OF_MONTH)) fields[DAY_OF_MONTH] - 1 else 0
        val timezoneOffset = -getOffset(fixedDate * ONE_DAY_IN_MILLIS)
        val timeOfHour = when {
            isSet(HOUR_OF_DAY) -> fields[HOUR_OF_DAY]
            isSet(HOUR) && isSet(AM_PM) -> fields[HOUR] + if (fields[AM_PM] == AM) 0 else 12
            else -> 0
        }
        val timeOfMinute = if (isSet(MINUTE)) fields[MINUTE] else 0
        val timeOfSecond = if (isSet(SECOND)) fields[SECOND] else 0
        val timeOfMillisecond = if (isSet(MILLISECOND)) fields[MILLISECOND] else 0
        time = (fixedDate - EPOCH_OFFSET) * ONE_DAY_IN_MILLIS +
                (timeOfHour * ONE_HOUR_IN_MILLIS) + (timeOfMinute * ONE_MINUTE_IN_MILLIS) +
                (timeOfSecond * ONE_SECOND_IN_MILLIS) +
                timeOfMillisecond + timezoneOffset
        areFieldsSet = false
    }

    override fun getActualMinimum(field: Int): Int = getMinimum(field)

    /**
     * Returns the maximum value of the given field for the current date.
     * For example, the maximum number of days in the current month.
     */
    override fun getActualMaximum(field: Int): Int {
        if (field == DAY_OF_MONTH) {
            daysInMonth
        }
        return getMaximum(field)
    }

    override fun getGreatestMinimum(field: Int): Int = MINIMUMS[field]

    override fun getLeastMaximum(field: Int): Int = LEAST_MAXIMUMS[field]

    override fun getMaximum(field: Int): Int = MAXIMUMS[field]

    override fun getMinimum(field: Int): Int = MINIMUMS[field]

    @Throws(IllegalStateException::class)
    override fun roll(field: Int, up: Boolean) =
        throw IllegalStateException("Not supported")

    private fun getOffset(localTime: Long): Int = timeZone.getOffset(localTime)

    /* To find the year that associated with fixedDat. */
    private fun getYearFromFixedDate(fd: Int): Int {
        var testYear: Int
        var testAfterH = fd > 0
        testYear = if (testAfterH) {
            floor(((fd - 1) / 365.24219).roundToInt().toDouble()).toInt() + 1
        } else {
            floor((fd / 365.24219).roundToInt().toDouble()).toInt()
        }
        if (testYear == 0) {
            testYear = 1
            testAfterH = true
        }
        val far1 =
            getFixedDateFar1(abs(testYear), testAfterH)
        return if (far1 <= fd) {
            if (testYear <= 0) {
                testYear + 1
            } else {
                testYear
            }
        } else { // last year of testYear and try to convert it to include zero
            if (testYear <= -1) {
                testYear
            } else {
                testYear - 1
            }
        }
    }

    /* To find the fixedDate of first day of year. Farvardin 1, 1 must have fixedDate of one. */
    private fun getFixedDateFar1(year: Int, afterH: Boolean): Int {
        require(year > 0) { "Year cannot be negative or zero. Year: $year" }
        if (afterH && year >= BASE_YEAR && year < BASE_YEAR + FIXED_DATES.size - 1) {
            return FIXED_DATES[year - BASE_YEAR]
        }
        // The detail can be found in [https://en.wikibooks.org/wiki/Persian_Calendar]
        val realYear: Int = if (afterH) {
            year - 1
        } else {
            -year
        }
        var days = 1029983 * floor((realYear + 38) / 2820.0).toInt()
        var cycle = (realYear + 38) % 2820
        if (cycle < 0) {
            cycle += 2820
        }
        days += (floor((cycle - 38) * 365.24219)).toInt() + 1
        val extra = cycle * 0.24219
        val frac = getIntegerPart((extra - floor(extra)) * 1000)
        var lastYear = year - 1
        if (afterH && year == 1) {
            lastYear = 1
        } else if (!afterH) {
            lastYear = year + 1
        }
        if (isLeapYear(lastYear) && frac <= 202) {
            days++
        }
        return days
    }

    /* To get integer part of a double */
    private fun getIntegerPart(d: Double): Int = if (d >= 0) {
        floor(d).toInt()
    } else {
        floor(d).toInt() + 1
    }

    val isLeapYear: Boolean
        get() {
            if (isSet(YEAR)) {
                return isLeapYear(get(YEAR))
            }
            throw IllegalArgumentException("Year must be set")
        }

    val daysInMonth: Int
        get() = daysInMonth(isLeapYear(get(YEAR)), get(MONTH))

    val dayOfWeek: DayOfWeekPersian
        get() = DayOfWeekPersian.of(get(DAY_OF_WEEK))

    val month: MonthPersian
        get() = MonthPersian.of(get(MONTH))

    val timeInSeconds: Long
        get() = timeInMillis / 1000

    override fun equals(other: Any?): Boolean {
        if (this === other) {
            return true
        }
        if (other !is Calendar) {
            return false
        }
        return timeInMillis == other.timeInMillis && get(YEAR) == other[YEAR]
                && get(MONTH) == other[MONTH] && get(DAY_OF_MONTH) == other[DAY_OF_MONTH]
                && get(HOUR) == other[HOUR] && get(MINUTE) == other[MINUTE]
                && get(SECOND) == other[SECOND] && get(MILLISECOND) == other[MILLISECOND]
    }

    override fun hashCode(): Int {
        var result = super.hashCode()
        result = 31 * result + timeInMillis.toInt()
        result = 31 * result + get(YEAR)
        result = 31 * result + get(MONTH)
        result = 31 * result + get(DAY_OF_MONTH)
        result = 31 * result + get(HOUR)
        result = 31 * result + get(MINUTE)
        result = 31 * result + get(SECOND)
        result = 31 * result + get(MILLISECOND)
        return result
    }

    /**
     * @return tomorrow calendar
     */
    operator fun inc(): JalaliCalendar {
        add(DAY_OF_MONTH, 1)
        return this
    }

    /**
     * Generate range from now to [that]
     */
    operator fun rangeTo(that: JalaliCalendar) = JalaliCalendarRange(this, that)

    companion object {

        private const val AH = 1 // Value for the after hejra era.
        private const val BH = 0 // Value for the before hejra era.
        private const val EPOCH_OFFSET = 492268 // offset between jalali and gregorian
        private const val BASE_YEAR = 1349
        private const val ONE_SECOND_IN_MILLIS = 1000L
        private const val ONE_MINUTE_IN_MILLIS = 60 * ONE_SECOND_IN_MILLIS
        private const val ONE_HOUR_IN_MILLIS = 60 * ONE_MINUTE_IN_MILLIS
        private const val ONE_DAY_IN_MILLIS = 24 * ONE_HOUR_IN_MILLIS

        private val FIXED_DATES = intArrayOf(
            492347,  // False   ,  1349
            492712,  // True   ,  1350
            493078,  // False   ,  1351
            493443,  // False   ,  1352
            493808,  // False   ,  1353
            494173,  // True   ,  1354
            494539,  // False   ,  1355
            494904,  // False   ,  1356
            495269,  // False   ,  1357
            495634,  // True   ,  1358
            496000,  // False   ,  1359
            496365,  // False   ,  1360
            496730,  // False   ,  1361
            497095,  // True   ,  1362
            497461,  // False   ,  1363
            497826,  // False   ,  1364
            498191,  // False   ,  1365
            498556,  // True   ,  1366
            498922,  // False   ,  1367
            499287,  // False   ,  1368
            499652,  // False   ,  1369
            500017,  // True   ,  1370
            500383,  // False   ,  1371
            500748,  // False   ,  1372
            501113,  // False   ,  1373
            501478,  // False   ,  1374
            501843,  // True   ,  1375
            502209,  // False   ,  1376
            502574,  // False   ,  1377
            502939,  // False   ,  1378
            503304,  // True   ,  1379
            503670,  // False   ,  1380
            504035,  // False   ,  1381
            504400,  // False   ,  1382
            504765,  // True   ,  1383
            505131,  // False   ,  1384
            505496,  // False   ,  1385
            505861,  // False   ,  1386
            506226,  // True   ,  1387
            506592,  // False   ,  1388
            506957,  // False   ,  1389
            507322,  // False   ,  1390
            507687,  // True   ,  1391
            508053,  // False   ,  1392
            508418,  // False   ,  1393
            508783,  // False   ,  1394
            509148,  // True   ,  1395
            509514,  // False   ,  1396
            509879,  // False   ,  1397
            510244,  // False   ,  1398
            510609,  // True   ,  1399
            510975,  // False   ,  1400
            511340,  // False   ,  1401
            511705,  // False   ,  1402
            512070,  // False   ,  1403
            512435,  // True   ,  1404
            512801,  // False   ,  1405
            513166,  // False   ,  1406
            513531,  // False   ,  1407
            513896,  // True   ,  1408
            514262,  // False   ,  1409
            514627,  // False   ,  1410
            514992,  // False   ,  1411
            515357,  // True   ,  1412
            515723,  // False   ,  1413
            516088,  // False   ,  1414
            516453,  // False   ,  1415
            516818,  // True   ,  1416
            517184
        )
        private val ACCUMULATED_DAYS_IN_MONTH = intArrayOf(
            0, 31, 62, 93, 124, 155,
            186, 216, 246, 276, 306, 336
        )
        private val MINIMUMS = intArrayOf(
            0, 1, 0, 1, 0, 1, 1, 1, 1, 0,
            0, 0, 0, 0, 0, -13 * 3600 * 1000, 0
        )
        private val MAXIMUMS = intArrayOf(
            1, 292278994, 11, 53, 6, 31,
            366, 7, 6, 1, 11, 23, 59, 59, 999, 14 * 3600 * 1000, 7200000
        )
        private val LEAST_MAXIMUMS = intArrayOf(
            1, 292269054, 11, 50, 3,
            28, 355, 7, 3, 1, 11, 23, 59, 59, 999, 50400000, 1200000
        )

        @JvmStatic
        fun daysInMonth(leapYear: Boolean, month: Int): Int {
            require(month in 0..MonthPersian.Esfand.ordinal)
            return if (month == MonthPersian.Esfand.ordinal) {
                if (leapYear) {
                    30
                } else {
                    29
                }
            } else ACCUMULATED_DAYS_IN_MONTH[month + 1] - ACCUMULATED_DAYS_IN_MONTH[month]
        }

        @JvmStatic
        fun daysInYear(year: Int) = if (isLeapYear(year)) 366 else 365

        @JvmStatic
        fun isLeapYear(year: Int) = CalendarLeapUtils.isJalaliLeapYear(year)
    }
}