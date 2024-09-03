package source

import org.jetbrains.annotations.Range

@JvmInline
value class TimerHandValue(
    private val value: UInt
) {

    companion object {

        fun zero() = TimerHandValue(0u)

        fun fromSecond(value: @Range(from = 0, to = 362439) UInt) = run {
            val s = value.mod(60u)
            val m = value.div(60u).mod(60u)
            val h = value.div(60u).div(60u)
            var wrappedValue = 0u
            wrappedValue = wrappedValue
                .or(h / 100u).shl(4)
                .or(h / 10u % 10u).shl(4)
                .or(h % 10u).shl(4)
                .or(m / 10u).shl(4)
                .or(m % 10u).shl(4)
                .or(s / 10u).shl(4)
                .or(s % 10u)
            TimerHandValue(wrappedValue)
        }
    }

    fun push(value: @Range(from = 0, to = 9) UInt) = TimerHandValue(this.value.shl(4) + value)

    fun pop() = TimerHandValue(value.shr(4))

    val second get() = value.mod(0x10u) + value.shr(4).mod(0x10u) * 10u
    val minute get() = value.shr(8).mod(0x10u) + value.shr(12).mod(0x10u) * 10u
    val hour
        get() = value.shr(16).mod(0x10u) + value.shr(20).mod(0x10u) * 10u + value.shr(24) * 100u

    fun toArray() = run {
        arrayOf(
            value.shr(20).mod(0x10u) + value.shr(24) * 10u,
            value.shr(16).mod(0x10u),
            value.shr(12).mod(0x10u),
            value.shr(8).mod(0x10u),
            value.shr(4).mod(0x10u),
            value.mod(0x10u)
        )
    }

    fun toSecond() = hour * 60u * 60u + minute * 60u + second
}