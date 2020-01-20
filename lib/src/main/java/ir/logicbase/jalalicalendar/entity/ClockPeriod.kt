package ir.logicbase.jalalicalendar.entity

import ir.logicbase.jalalicalendar.Clock

data class ClockPeriod(var start: Clock, var end: Clock) {

    companion object {

        /**
         * Check whether [first] and [second] overlap on each other
         */
        @JvmStatic
        fun isOverlap(first: ClockPeriod, second: ClockPeriod): Boolean {
            return first.start < second.end && first.end > second.start
        }
    }
}