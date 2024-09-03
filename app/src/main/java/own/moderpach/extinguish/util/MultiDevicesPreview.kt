package own.moderpach.extinguish.util

import androidx.compose.ui.tooling.preview.Devices
import androidx.compose.ui.tooling.preview.Preview

/**
 * 屏幕设计规范
 * Small -> 0..420.dp
 * Medium -> 420..1000.dp
 * Large -> 1000.dp..
 * 通常分为Small和非Small两档即可。
 * */

@Preview(name = "PhoneVertical", device = Devices.PHONE)
@Preview(name = "PhoneHorizon", device = Devices.PHONE, widthDp = 891, heightDp = 411)
//@Preview(name = "FoldableVertical", device = Devices.FOLDABLE)
//@Preview(name = "FoldableHorizon", device = Devices.FOLDABLE, widthDp = 841, heightDp = 637)
@Preview(name = "TabletHorizon", device = Devices.TABLET)
@Preview(name = "TabletVertical", device = Devices.TABLET, widthDp = 1080, heightDp = 1920)
annotation class MultiDevicesPreview
