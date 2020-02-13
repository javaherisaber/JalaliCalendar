package ir.logicbase.jalalicalendar.entity

import ir.logicbase.jalalicalendar.Clock

data class ClockPeriod(var start: Clock, var end: Clock) {

    /**
     * Check whether current period overlap on [other]
     */
    fun isOverlap(other: ClockPeriod) = this.start < other.end && this.end > other.start
}