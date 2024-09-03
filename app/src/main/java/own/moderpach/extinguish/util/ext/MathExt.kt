package own.moderpach.extinguish.util.ext

import kotlin.math.max

fun max(a: Int, b: Int, c: Int): Int {
    return max(max(a, b), c)
}