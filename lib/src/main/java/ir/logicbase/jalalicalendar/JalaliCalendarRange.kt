package ir.logicbase.jalalicalendar

/**
 * Define a class to be used in Kotlin rangeTo function
 */
class JalaliCalendarRange(
    override val start: JalaliCalendar,
    override val endInclusive: JalaliCalendar
) : ClosedRange<JalaliCalendar>, Iterable<JalaliCalendar> {

    override fun iterator(): Iterator<JalaliCalendar> = JalaliCalendarIterator(start, endInclusive)
}