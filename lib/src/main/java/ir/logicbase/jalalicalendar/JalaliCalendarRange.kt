package ir.logicbase.jalalicalendar

class JalaliCalendarRange(
    override val start: JalaliCalendar,
    override val endInclusive: JalaliCalendar
) : ClosedRange<JalaliCalendar>, Iterable<JalaliCalendar> {

    override fun iterator(): Iterator<JalaliCalendar> = JalaliCalendarIterator(start, endInclusive)
}