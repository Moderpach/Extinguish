package source

import org.jetbrains.annotations.Range


/**
 * Store and process mechanical timer value with BCD, which
 * contains hour, minutes and second.
 *
 * The range of minutes and second is 0..59.
 * The range of hour is 0..100.
 *
 * Memory struct is like:
 * ```
 *   28   24   20   16   12    8    4    0
 * 0000 HHHH HHHH HHHH MMMM MMMM SSSS SSSS
 * ```
 *
 * */
@JvmInline
value class MechanicalTimerValue private constructor(
    private val value: UInt
) {

    companion object {

        fun zero() = MechanicalTimerValue(0u)

        /**
         * Convert seconds to MechanicalTimerValue.
         * hours, minutes and seconds part never larger than 59.
         * */
        fun from(seconds: @Range(from = 0, to = 362439) UInt): MechanicalTimerValue {
            require(seconds.isInScaleRange()) {
                "seconds should in 0u..362439u"
            }

            //Convert seconds value to BCD
            var temp = seconds
            var result = 0u
            //Divide
            val s = temp % 60u
            temp /= 60u
            val m = temp % 60u
            temp /= 60u
            val h = temp

            result += s % 10u shl 0
            result += s / 10u shl 4
            result += m % 10u shl 8
            result += m / 10u shl 12
            result += h % 10u shl 16
            result += h / 10u % 10u shl 20
            result += h / 100u shl 24

            return MechanicalTimerValue(result)
        }

        private fun UInt.isInScaleRange() = this in 0u..362439u
    }

    val hours get() = get(6) * 100u + get(5) * 10u + get(4)

    val minutes get() = get(3) * 10u + get(2)

    val seconds get() = get(1) * 10u + get(0)

    fun toDigitalTimerValue() = DigitalTimerValue.from(toUInt())

    /**
     * Convert MechanicalTimerValue to UInt in seconds.
     * */
    fun toUInt() = hours * 60u * 60u + minutes * 60u + seconds

    operator fun get(index: Int): UInt {
        require(index in 0..6) {
            "MechanicalTimerValue only has 7 numbers."
        }
        return value.shr(index * 4) and 0xFu
    }
}

/**
 * Store and process mechanical timer value with BCD, which
 * contains hour, minutes and second.
 *
 * The range of hour, minutes and second is 0..99.
 *
 * Memory struct is like:
 * ```
 *   28   24   20   16   12    8    4    0
 * 0000 0000 HHHH HHHH MMMM MMMM SSSS SSSS
 * ```
 *
 * */
@JvmInline
value class DigitalTimerValue private constructor(
    private val value: UInt
) {

    companion object {

        fun zero() = DigitalTimerValue(0u)

        /**
         * Convert seconds to MechanicalTimerValue.
         * hours, minutes and seconds part never larger than 99.
         * Always trying to make lower part larger.
         * */
        fun from(seconds: @Range(from = 0, to = 362439) UInt): DigitalTimerValue {
            require(seconds.isInScaleRange()) {
                "seconds should in 0u..362439u"
            }

            //Convert seconds value to BCD
            var temp = seconds
            var result = 0u
            //Divide
            var s = temp % 60u
            temp /= 60u
            var m = temp % 60u
            temp /= 60u
            var h = temp
            //Make lower bit full
            if (m >= 1u && s <= 99u - 60u) {
                s += 60u
                m -= 1u
            }
            if (h >= 1u && m <= 99u - 60u) {
                m += 60u
                h -= 1u
            }

            result += s % 10u shl 0
            result += s / 10u shl 4
            result += m % 10u shl 8
            result += m / 10u shl 12
            result += h % 10u shl 16
            result += h / 10u shl 20

            return DigitalTimerValue(result)
        }

        private fun UInt.isInScaleRange() = this in 0u..362439u
    }

    val hours get() = get(5) * 10u + get(4)

    val minutes get() = get(3) * 10u + get(2)

    val seconds get() = get(1) * 10u + get(0)

    fun toMechanicalTimerValue() = MechanicalTimerValue.from(toUInt())

    /**
     * Convert DigitalTimerValue to UInt in seconds.
     * */
    fun toUInt() = hours * 60u * 60u + minutes * 60u + seconds

    operator fun get(index: Int): UInt {
        /*require(index in 0..5) {
            "DigitalTimerValue only has 6 numbers."
        }*/
        return value.shr(index * 4) and 0xFu
    }

    fun push(digit: @Range(from = 0, to = 9) UInt): DigitalTimerValue {
        if (couldNotPushMore()) return this
        return DigitalTimerValue(
            value.shl(4) or digit
        )
    }

    fun couldNotPushMore() = (get(5) != 0u)

    fun pop(): DigitalTimerValue {
        return DigitalTimerValue(
            value.shr(4)
        )
    }

    fun couldNotPopMore() = value == 0u
}