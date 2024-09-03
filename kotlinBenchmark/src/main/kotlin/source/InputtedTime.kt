package source

data class InputtedTime(
    val hour: DecimalUInt,
    val minute: DecimalUInt,
    val second: DecimalUInt
) {
    companion object {
        fun createByInt(v: Int): InputtedTime {
            if (v < 0) throw OUT_OF_SCOPE_EXCEPTION
            if (v > 99 * 99 * 99) throw OUT_OF_SCOPE_EXCEPTION
            var v = v.toUInt()
            val second = v % 60u
            v /= 60u
            val minute = v % 60u
            v /= 60u
            val hour = v

            return InputtedTime(
                hour = DecimalUInt(hour / 10u, hour % 10u),
                minute = DecimalUInt(minute / 10u, minute % 10u),
                second = DecimalUInt(second / 10u, second % 10u),
            )
        }

        fun zero() = InputtedTime(
            DecimalUInt(0u, 0u),
            DecimalUInt(0u, 0u),
            DecimalUInt(0u, 0u),
        )

        val OUT_OF_SCOPE_EXCEPTION = Exception("Input value is out of scope.")
    }

    fun toUInt(): UInt {
        return (hour.toUInt() * 60u * 60u + minute.toUInt() * 60u + second.toUInt())
    }

    fun toInt() = toUInt().toInt()

    @OptIn(ExperimentalUnsignedTypes::class)
    fun pushLast(num: UInt): InputtedTime {
        if (num >= 10u) throw OUT_OF_SCOPE_EXCEPTION
        if (hour.tens != 0u) throw OUT_OF_SCOPE_EXCEPTION

        val array = uintArrayOf(
            hour.tens, hour.ones,
            minute.tens, minute.ones,
            second.tens, second.ones
        )

        for (i in 1 until array.size) {
            array[i - 1] = array[i]
        }
        array[5] = num

        val hour = DecimalUInt(array[0], array[1])
        val minute = DecimalUInt(array[2], array[3])
        val second = DecimalUInt(array[4], array[5])

        return InputtedTime(
            hour, minute, second
        )
    }

    @OptIn(ExperimentalUnsignedTypes::class)
    fun popLast(): InputtedTime {
        val array = uintArrayOf(
            hour.tens, hour.ones,
            minute.tens, minute.ones,
            second.tens, second.ones
        )

        for (i in array.size - 1 downTo 1) {
            array[i] = array[i - 1]
        }
        array[0] = 0u

        val hour = DecimalUInt(array[0], array[1])
        val minute = DecimalUInt(array[2], array[3])
        val second = DecimalUInt(array[4], array[5])

        return InputtedTime(
            hour, minute, second
        )
    }
}

data class DecimalUInt(
    val tens: UInt,
    val ones: UInt
) {

    companion object {
        val OUT_OF_SCOPE_EXCEPTION = Exception("Input value is out of scope.")
    }

    init {
        //if (ones >= 10u) throw OUT_OF_SCOPE_EXCEPTION
    }

    fun toUInt() = ones + tens * 10u

    fun toRawString() = "$tens$ones"

    fun toCompressString() = toUInt().toString()
}
