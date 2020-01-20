package ir.logicbase.jalalicalendar

/**
 * Iterator class being used in Kotlin rangeTo function
 */
class JalaliCalendarIterator(
    start: JalaliCalendar, private val endInclusive: JalaliCalendar
) : Iterator<JalaliCalendar> {

    private var initialValue = JalaliCalendar(start)

    override fun hasNext(): Boolean = initialValue <= endInclusive

    override fun next(): JalaliCalendar {
        val current = JalaliCalendar(initialValue)
        initialValue++
        return current
    }
}