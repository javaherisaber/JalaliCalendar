package ir.logicbase.jalalicalendar

/**
 * Represent day of week in persian calendar (jalali)
 */
enum class DayOfWeekPersian {
    Yekshanbeh,
    Doshanbeh,
    Seshhanbeh,
    Chaharshanbeh,
    Panjshanbeh,
    Jomeh,
    Shanbeh;

    /**
     * Gets the days-of-week value.
     *
     * The values are numbered following the ISO-8601 standard,
     * from 1 [Yekshanbeh] to 7 [Shanbeh]
     */
    val value: Int
        get() = ordinal + 1

    val persianText: String
        get() = WEEKDAYS_FA[ordinal]

    val persianLetter: String
        get() = WEEKDAYS_LETTER_FA[ordinal]

    val persianShortText: String
        get() = WEEKDAYS_SHORT_FA[ordinal]

    companion object {

        private val WEEKDAYS_FA = arrayOf(
            "یکشنبه",
            "دوشنبه",
            "سه شنبه",
            "چهارشنبه",
            "پنج شنبه",
            "جمعه",
            "شنبه"
        )

        private val WEEKDAYS_LETTER_FA = arrayOf(
            "ی",
            "د",
            "س",
            "چ",
            "پ",
            "ج",
            "ش"
        )

        private val WEEKDAYS_SHORT_FA = arrayOf(
            "یک",
            "دو",
            "سه",
            "چهار",
            "پنج",
            "جمعه",
            "شنبه"
        )

        private val CACHED_VALUES = values()

        /**
         * DayOfWeek values in correct persian order
         */
        @JvmStatic
        val valuesOrderedInPersian = arrayOf(
            Shanbeh, Yekshanbeh, Doshanbeh, Seshhanbeh, Chaharshanbeh, Panjshanbeh, Jomeh
        )

        /**
         * get [DayOfWeekPersian] with given index
         * @param index dayOfWeek from 1 [Yekshanbeh] to 7 [Shanbeh]
         */
        @JvmStatic
        fun of(index: Int): DayOfWeekPersian {
            require(index in 1..7) {
                "index must be between 1 and 7"
            }
            return CACHED_VALUES[index - 1]
        }
    }
}