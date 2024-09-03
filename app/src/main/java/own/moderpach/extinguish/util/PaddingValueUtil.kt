package own.moderpach.extinguish.util

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.calculateEndPadding
import androidx.compose.foundation.layout.calculateStartPadding
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.isUnspecified

fun PaddingValues.copy(
    layoutDirection: LayoutDirection = LayoutDirection.Ltr,
    start: Dp = this.calculateLeftPadding(layoutDirection),
    top: Dp = this.calculateTopPadding(),
    end: Dp = this.calculateRightPadding(layoutDirection),
    bottom: Dp = this.calculateBottomPadding()
) = PaddingValues(
    start, top, end, bottom
)

fun PaddingValues.add(
    layoutDirection: LayoutDirection = LayoutDirection.Ltr,
    paddingValues: PaddingValues
) = this.add(
    layoutDirection,
    paddingValues.calculateStartPadding(layoutDirection),
    paddingValues.calculateTopPadding(),
    paddingValues.calculateEndPadding(layoutDirection),
    paddingValues.calculateBottomPadding()
)

fun PaddingValues.add(
    layoutDirection: LayoutDirection = LayoutDirection.Ltr,
    start: Dp = 0.dp,
    top: Dp = 0.dp,
    end: Dp = 0.dp,
    bottom: Dp = 0.dp
) = PaddingValues(
    this.calculateStartPadding(layoutDirection) + start,
    this.calculateTopPadding() + top,
    this.calculateEndPadding(layoutDirection) + end,
    this.calculateBottomPadding() + bottom
)

fun PaddingValues.add(
    layoutDirection: LayoutDirection = LayoutDirection.Ltr,
    dp: Dp = 0.dp
) = PaddingValues(
    this.calculateStartPadding(layoutDirection) + dp,
    this.calculateTopPadding() + dp,
    this.calculateEndPadding(layoutDirection) + dp,
    this.calculateBottomPadding() + dp
)

fun PaddingValues.add(
    layoutDirection: LayoutDirection = LayoutDirection.Ltr,
    vertical: Dp = 0.dp,
    horizontal: Dp = 0.dp
) = PaddingValues(
    this.calculateStartPadding(layoutDirection) + horizontal,
    this.calculateTopPadding() + vertical,
    this.calculateEndPadding(layoutDirection) + horizontal,
    this.calculateBottomPadding() + vertical
)

fun PaddingValues.hasZeroPadding(
    layoutDirection: LayoutDirection = LayoutDirection.Ltr
) = this.hasZeroVerticalPadding() && this.hasZeroHorizontalPadding(layoutDirection)

fun PaddingValues.hasZeroVerticalPadding() = this.hasZeroTopPadding() && this.hasZeroBottomPadding()

fun PaddingValues.hasZeroHorizontalPadding(
    layoutDirection: LayoutDirection = LayoutDirection.Ltr
) = this.hasZeroStartPadding(layoutDirection) && this.hasZeroEndPadding(layoutDirection)

fun PaddingValues.hasZeroStartPadding(
    layoutDirection: LayoutDirection = LayoutDirection.Ltr
) = this.calculateStartPadding(layoutDirection) == 0.dp

fun PaddingValues.hasZeroEndPadding(
    layoutDirection: LayoutDirection = LayoutDirection.Ltr
) = this.calculateEndPadding(layoutDirection) == 0.dp

fun PaddingValues.hasZeroTopPadding() = this.calculateTopPadding() == 0.dp

fun PaddingValues.hasZeroBottomPadding() = this.calculateBottomPadding() == 0.dp