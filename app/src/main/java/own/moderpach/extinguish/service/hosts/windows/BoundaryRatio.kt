package own.moderpach.extinguish.service.hosts.windows

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.IntSize
import kotlin.math.roundToInt

typealias BoundaryRatio = Offset

fun Boundary.calculateBoundaryRatio(
    position: IntOffset
) = this.calculateBoundaryRatio(
    x = position.x,
    y = position.y
)

fun Boundary.calculateBoundaryRatio(
    x: Int,
    y: Int
) = Offset(
    x = (x - this.left).toFloat() / this.width(),
    y = (y - this.top).toFloat() / this.height()
)

fun Boundary.calculatePosition(
    boundaryRatio: BoundaryRatio
) = IntOffset(
    x = (boundaryRatio.x * this.width()).roundToInt() + this.left,
    y = (boundaryRatio.y * this.height()).roundToInt() + this.top
)