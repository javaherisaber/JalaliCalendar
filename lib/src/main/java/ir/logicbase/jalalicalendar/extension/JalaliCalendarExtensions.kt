package ir.logicbase.jalalicalendar.extension

import ir.logicbase.jalalicalendar.JalaliCalendar
import ir.logicbase.jalalicalendar.format.JalaliDateFormat
import java.text.SimpleDateFormat
import java.util.*

/**
 * @return JalaliCalendar with only date part
 */
fun JalaliCalendar.cloneDatePart(): JalaliCalendar = JalaliCalendar(year, month, dayOfMonth)

/**
 * @return check if equals to [other] date parts
 */
fun JalaliCalendar.datePartEquals(other: JalaliCalendar): Boolean {
    return (year == other.year) && (month == other.month) && (dayOfMonth == other.dayOfMonth)
}

/**
 * @return true if current calendar is before today
 */
fun JalaliCalendar.isInPast() = JalaliCalendar() > this

/**
 * Convert current jalali calendar to gregorian datetime string
 *
 * @param pattern date format pattern eg. yyyy-MM-dd
 * @param locale date format locale eg. per or en
 * @return date time format of this calendar eg. 2020-01-14 16:45:00
 */
fun JalaliCalendar.toGregorianDateTime(
    pattern: String = JalaliCalendar.GREGORIAN_DATE_TIME_FORMAT,
    locale: Locale = JalaliCalendar.DEFAULT_LOCALE
): String {
    val dateFormat = SimpleDateFormat(pattern, locale)
    val date = Date(timeInMillis)
    return dateFormat.format(date)
}

/**
 * Convert current jalali calendar to jalali datetime string
 *
 * @param pattern date format pattern eg. yyyy/mm/dd
 * @return date time format of this calendar eg. 1398/10/30
 */
fun JalaliCalendar.toJalaliDateTime(
    pattern: String = JalaliCalendar.JALALI_DATE_TIME_FORMAT
): String {
    val dateFormat = JalaliDateFormat(pattern)
    return dateFormat.format(this)
}

/**
 * Epoch time in seconds
 */
val JalaliCalendar.timeInSeconds: Long
    get() = timeInMillis / 1000