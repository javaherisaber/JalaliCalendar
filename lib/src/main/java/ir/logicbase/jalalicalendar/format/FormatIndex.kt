package ir.logicbase.jalalicalendar.format

/**
 * Define index to iterate through a pattern
 *
 * @property pos position of index
 */
internal class FormatIndex(var pos: Int) {

    /**
     * Peak into next letters in pattern
     *
     * @param pattern to be peaked string
     * @param step how much to peak
     * @return sub string of [pattern]
     */
    fun peak(pattern: String, step: Int): String {
        return if (pos + step >= pattern.length) {
            // end of pattern reached, return current position
            pattern[pos].toString()
        } else {
            pattern.substring(pos..(pos + step))
        }
    }
}