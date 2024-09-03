package own.moderpach.extinguish.ui

import org.junit.Assert.assertThrows
import com.google.common.truth.Truth.assertThat
import org.junit.Test

class InputtedTimeTest {

    @Test
    fun `test create and to UInt`() {
        val r1 = InputtedTime.zero()
        assertThat(r1.toUInt()).isEqualTo(0u)

        val r2 = InputtedTime.createByInt(10800)
        assertThat((r2.toUInt())).isEqualTo(10800u)

        assertThrows(InputtedTime.OUT_OF_SCOPE_EXCEPTION.javaClass) { InputtedTime.createByInt(-23) }
        assertThrows(InputtedTime.OUT_OF_SCOPE_EXCEPTION.javaClass) {
            InputtedTime.createByInt(
                217000
            )
        }
    }

    @Test
    fun `test pushLast`() {
        var r = InputtedTime.zero()
        assertThrows(InputtedTime.OUT_OF_SCOPE_EXCEPTION.javaClass) {
            r = r.pushLast(3u)
            r = r.pushLast(3u)
            r = r.pushLast(3u)
            r = r.pushLast(3u)
            r = r.pushLast(3u)
            r = r.pushLast(3u)
            r = r.pushLast(3u)
        }
    }

    @Test
    fun `test popLast`() {
        var r1 = InputtedTime(
            DecimalUInt(2u, 3u),
            DecimalUInt(2u, 3u),
            DecimalUInt(2u, 3u),
        )
        r1 = r1.popLast().popLast()
        assertThat(r1.hour).isEqualTo(DecimalUInt(0u, 0u))
        assertThat(r1.minute).isEqualTo(DecimalUInt(2u, 3u))
        assertThat(r1.second).isEqualTo(DecimalUInt(2u, 3u))
    }

    @Test
    fun `test DecimalUInt function`() {
        val r1 = InputtedTime(
            DecimalUInt(0u, 3u),
            DecimalUInt(2u, 3u),
            DecimalUInt(2u, 3u),
        )
        assertThat(r1.hour.toRawString()).isEqualTo("03")
        assertThat(r1.hour.toCompressString()).isEqualTo("3")
        assertThat(r1.hour.toUInt()).isEqualTo(3u)
    }
}