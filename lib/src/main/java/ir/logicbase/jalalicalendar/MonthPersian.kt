package ir.logicbase.jalalicalendar

/**
 * Represent month in persian calendar (jalali)
 */
enum class MonthPersian {
    Farvardin,
    Ordibehesht,
    Khordad,
    Tir,
    Mordad,
    Shahrivar,
    Mehr,
    Aban,
    Azar,
    Dey,
    Bahman,
    Esfand;

    /**
     * Gets the month-of-year value.
     *
     * The values are numbered following the ISO-8601 standard,
     * from 1 [Farvardin] to 12 [Esfand].
     */
    val value: Int
        get() = ordinal + 1

    val toPersian: String
        get() = PERSIAN_MONTHS_FA[ordinal]

    companion object {

        private val CACHED_VALUES = values()

        private val PERSIAN_MONTHS_FA = arrayOf(
            "فروردین",
            "اردیبهشت",
            "خرداد",
            "تیر",
            "مرداد",
            "شهریور",
            "مهر",
            "آبان",
            "آذر",
            "دی",
            "بهمن",
            "اسفند"
        )

        /**
         * Obtains an instance of `MonthPersian` from an `int` value.
         *
         * @param index the month-of-year to represent, from 0 [Farvardin] to 11 [Esfand]
         */
        fun of(index: Int): MonthPersian {
            require(index in 0..11) {
                "Invalid value for MonthOfYear: $index"
            }
            return CACHED_VALUES[index] // cached values is a zero based array
        }
    }
}
