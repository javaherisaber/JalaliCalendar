package ir.logicbase.jalalicalendar

class JalaliCalendarIterator(
    start: JalaliCalendar, private val endInclusive: JalaliCalendar
) : Iterator<JalaliCalendar> {

    var initValue = JalaliCalendar(start)

    override fun hasNext(): Boolean = initValue <= endInclusive

    override fun next(): JalaliCalendar {
        val current = JalaliCalendar(initValue)
        initValue++
        return current
    }
}