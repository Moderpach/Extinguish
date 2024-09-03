package own.moderpach.extinguish.home

import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.BasicAlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import own.moderpach.extinguish.home.cards.externalControl
import own.moderpach.extinguish.home.cards.floatingButton
import own.moderpach.extinguish.home.cards.moreSettings
import own.moderpach.extinguish.home.cards.notificationControl
import own.moderpach.extinguish.home.cards.screenEventControl
import own.moderpach.extinguish.home.cards.solution
import own.moderpach.extinguish.home.cards.tileControl
import own.moderpach.extinguish.home.cards.volumeKeyControl
import own.moderpach.extinguish.ui.components.ExtinguishListItem

@Composable
@OptIn(ExperimentalMaterial3Api::class)
fun HomeScreenQuickScrollDialog(
    cardList: List<HomeScreenCardKey>,
    scrollTo: (HomeScreenCardKey) -> Unit,
    onDismissRequest: () -> Unit,
) {
    BasicAlertDialog(
        onDismissRequest = onDismissRequest
    ) {
        Card {
            Text(
                text = "快速滚动到",
                style = MaterialTheme.typography.titleMedium,
                modifier = Modifier
                    .align(Alignment.CenterHorizontally)
                    .padding(top = 24.dp, bottom = 16.dp)
            )
            LazyColumn {
                items(cardList) {
                    val onClick = {
                        onDismissRequest()
                        scrollTo(it)
                    }
                    when (it) {
                        HomeScreenCardKeys.solution -> QuickScrollItem(
                            "解决方案",
                            onClick = onClick
                        )

                        HomeScreenCardKeys.floatingButton -> QuickScrollItem(
                            "悬浮按钮",
                            onClick = onClick
                        )

                        HomeScreenCardKeys.volumeKeyControl -> QuickScrollItem(
                            "音量键控制",
                            onClick = onClick
                        )

                        HomeScreenCardKeys.screenEventControl -> QuickScrollItem(
                            "触碰屏幕亮屏",
                            onClick = onClick
                        )

                        HomeScreenCardKeys.notificationControl -> QuickScrollItem(
                            "通知控制",
                            onClick = onClick
                        )

                        HomeScreenCardKeys.tileControl -> QuickScrollItem(
                            "快捷设置控制",
                            onClick = onClick
                        )

                        HomeScreenCardKeys.externalControl -> QuickScrollItem(
                            "外部控制",
                            onClick = onClick
                        )

                        HomeScreenCardKeys.moreSettings -> QuickScrollItem(
                            "更多设置",
                            onClick = onClick
                        )
                    }
                }
            }
            Spacer(Modifier.height(24.dp))
        }
    }
}

@Composable
private fun QuickScrollItem(
    text: String,
    onClick: () -> Unit
) {
    ExtinguishListItem(
        headlineContent = {
            Text(text)
        },
        headlineTextStyle = MaterialTheme.typography.bodyLarge,
        onClick = onClick
    )
}
