package ir.logicbase.jalalicalendar.extension

/**
 * @return number with leading zero eg. 07
 */
internal fun Int.toLeadingZero() = if (this < 10) "0$this" else this.toString()