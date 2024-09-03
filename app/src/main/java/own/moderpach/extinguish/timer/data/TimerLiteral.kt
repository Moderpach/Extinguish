package own.moderpach.extinguish.timer.data

import org.jetbrains.annotations.Range


/**
 * TimerLiteral is a memory friendly timer literal in the form HH:MM:SS.
 * In TimerLiteral, the individual numbers in the timer are stored in 1 Int in 8421BCD code.
 * ```
 * 0000 Over Hhhh Hhhh Mmmm Mmmm Ssss Ssss
 *   28   24   20   16   12    8    4    0
 * ```
 * The TimerLiteral has a scale of 0 to 362439 seconds, i.e.,
 * 99:99:99 (non-standard) or
 * 100:40:39 (standard).
 *
 * When editing a timer using the keyboard, because the value entered is a digit,
 * there will be cases where the value of minutes and seconds is greater than or equal to
 * 60, such as 32:80:75.
 * The timer literal for such a case is called non-standard.
 *
 * If a non-standard type like 99:99:99 is converted to a standard type,
 * there will be a situation where the number of hours is greater than 99.
 * Bits 24 through 27 of the TimerLiteral are used to store exactly the number
 * that overflows in this case.
 *
 * */
@JvmInline
value class TimerLiteral private constructor(
    private val value: Int
) {

    val hours inline get() = get(6) * 100 + get(5) * 10 + get(4)

    val minutes inline get() = get(3) * 10 + get(2)

    val seconds inline get() = get(1) * 10 + get(0)

    /**
     * Whether TimerLiteral has not zero 24~27 bit
     * */
    val isOverflow get() = value.shr(6 * 4) >= 1

    /**
     * Whether TimerLiteral has not zero 24~27 bit after converted to standard
     * */
    val isOverflowInStandard get() = inSeconds() > 99 * 60 * 60 + 59 * 60 + 59

    val isStandard get() = minutes < 60 && seconds < 60

    val isFilled get() = get(5) != 0 || get(6) != 0

    val isZeroed get() = value == 0

    fun convertToStandard() = fromSeconds(inSeconds())

    /**
     * Push digit to last and return new TimerLiteral
     * @exception IllegalArgumentException if digit is out of 0 to 9.
     * @exception IllegalArgumentException if TimerLiteral could not push more.
     * */
    fun push(digit: @Range(from = 0, to = 9) Int): TimerLiteral {
        require(digit in 0..9) {
            "Digit should be in 0..9."
        }
        require(!isFilled) {
            "TimerLiteral is filled so could not push more."
        }
        return TimerLiteral(value.shl(4) or digit)
    }

    /**
     * Pop digit from last and return new TimerLiteral
     * @exception IllegalArgumentException if TimerLiteral has zeroed now.
     * */
    fun pop(): TimerLiteral {
        require(!isZeroed) {
            "TimerLiteral is zeroed so could not pop more."
        }
        return TimerLiteral(value.shr(4))
    }

    /**
     * Get each digit from TimerLiteral
     * @exception IllegalArgumentException if index is out of 0 to 6.
     * */
    operator fun get(index: Int): Int {
        require(index in 0..6) {
            "TimerLiteral only has 7 numbers."
        }
        return value.shr(index * 4) and 0xF
    }

    fun inSeconds() = hours * 60 * 60 + minutes * 60 + seconds

    override fun toString() = "$hours:$minutes:$seconds"

    companion object {

        fun zero() = TimerLiteral(0)

        /**
         * Create standard TimerLiteral from seconds.
         * @exception IllegalArgumentException if seconds is out of scale 0 to 362439.
         * */
        fun fromSeconds(seconds: @Range(from = 0, to = 362439) Int): TimerLiteral {
            require(seconds in Scale) {
                "seconds should in 0..362439"
            }

            //Convert seconds value to BCD
            var temp = seconds
            var result = 0
            //Divide
            val s = temp % 60
            temp /= 60
            val m = temp % 60
            temp /= 60
            val h = temp

            result += s % 10 shl 0
            result += s / 10 shl 4
            result += m % 10 shl 8
            result += m / 10 shl 12
            result += h % 10 shl 16
            result += h / 10 % 10 shl 20
            result += h / 100 shl 24
            return TimerLiteral(result)
        }

        val Scale = 0..362439
    }
}
