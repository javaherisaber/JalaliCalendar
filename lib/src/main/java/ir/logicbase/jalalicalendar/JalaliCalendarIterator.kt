package ir.logicbase.jalalicalendar

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