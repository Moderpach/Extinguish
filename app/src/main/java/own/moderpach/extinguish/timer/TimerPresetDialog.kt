package own.moderpach.extinguish.timer

import android.app.Activity
import android.content.res.Configuration
import android.widget.Toast
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.displayCutoutPadding
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.windowsizeclass.ExperimentalMaterial3WindowSizeClassApi
import androidx.compose.material3.windowsizeclass.calculateWindowSizeClass
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavGraphBuilder
import own.moderpach.extinguish.ExtinguishNavGraph
import own.moderpach.extinguish.ExtinguishNavRoute
import own.moderpach.extinguish.R
import own.moderpach.extinguish.timer.components.BackSpaceIcon
import own.moderpach.extinguish.timer.components.NumberKeyBoard
import own.moderpach.extinguish.timer.components.NumberKeyboardEvent
import own.moderpach.extinguish.timer.components.NumberKeyboardEvent.BackSpace
import own.moderpach.extinguish.timer.components.NumberKeyboardEvent.Number
import own.moderpach.extinguish.timer.components.TimerDisplay
import own.moderpach.extinguish.timer.components.TimerDisplayDefault
import own.moderpach.extinguish.timer.data.ITimersRepository
import own.moderpach.extinguish.timer.data.TimerLiteral
import own.moderpach.extinguish.timer.test.FakeTimersRepository
import own.moderpach.extinguish.ui.components.ExtinguishOutlinedButton
import own.moderpach.extinguish.ui.components.ExtinguishOutlinedIconButton
import own.moderpach.extinguish.ui.navigation.extinguishComposable
import own.moderpach.extinguish.ui.theme.ExtinguishTheme

val ExtinguishNavGraph.TimerPresetDialog: ExtinguishNavRoute get() = "TimerSetterDialog"

@OptIn(ExperimentalMaterial3WindowSizeClassApi::class)
fun NavGraphBuilder.timerPresetDialog(
    onBack: () -> Unit,
    timersRepository: ITimersRepository,
    onNavigateTo: (ExtinguishNavRoute) -> Unit
) = extinguishComposable(
    ExtinguishNavGraph.TimerPresetDialog,
) {
    val context = LocalContext.current
    val configuration = LocalConfiguration.current
    val windowSizeClass = calculateWindowSizeClass(context as Activity)
    val type = if (
        configuration.orientation == Configuration.ORIENTATION_LANDSCAPE
    ) TimerPresetDialogType.Horizontal
    else TimerPresetDialogType.Vertical

    TimerPresetDialog(
        onBack,
        timersRepository,
        type
    )
}

enum class TimerPresetDialogType {
    Vertical, Horizontal
}

@Composable
fun TimerPresetDialog(
    onBack: () -> Unit,
    timersRepository: ITimersRepository,
    type: TimerPresetDialogType
) {
    val context = LocalContext.current
    var timer by remember {
        mutableStateOf(TimerLiteral.zero())
    }

    val onKeyBoardEvent: (NumberKeyboardEvent) -> Unit = {
        when (it) {
            BackSpace -> try {
                timer = timer.pop()
            } catch (_: Exception) {
            }

            is Number -> try {
                timer = timer.push(it.num)
            } catch (_: Exception) {
            }

            NumberKeyboardEvent.Clear -> timer = TimerLiteral.zero()
        }
    }

    val onSave: () -> Unit = {
        if (timer.inSeconds() >= 1) {
            timersRepository.insert(timer)
            onBack()
        } else Toast.makeText(
            context,
            context.getString(R.string.str_timer_limit), Toast.LENGTH_SHORT
        ).show()
    }

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.surfaceContainer,
        contentColor = MaterialTheme.colorScheme.onSurface
    ) {
        when (type) {
            TimerPresetDialogType.Vertical -> Column(
                modifier = Modifier
                    .systemBarsPadding()
                    .displayCutoutPadding(),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                TimerDisplay(
                    modifier = Modifier
                        .width(300.dp)
                        .padding(vertical = 36.dp),
                    timer = timer,
                    style = TimerDisplayDefault.Large
                )
                NumberKeyBoard(
                    modifier = Modifier
                        .heightIn(max = 350.dp)
                        .padding(horizontal = 36.dp)
                        .aspectRatio(3f / 4),
                    onEvent = onKeyBoardEvent,
                    backSpaceIcon = BackSpaceIcon.Medium
                )
                CloseAndSaveButtonGroup(
                    modifier = Modifier.padding(top = 36.dp),
                    onSave = onSave,
                    onClose = onBack
                )
            }

            TimerPresetDialogType.Horizontal -> Row(
                modifier = Modifier
                    .systemBarsPadding()
                    .displayCutoutPadding(),
                horizontalArrangement = Arrangement.SpaceEvenly,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    TimerDisplay(
                        modifier = Modifier
                            .width(300.dp)
                            .padding(bottom = 16.dp),
                        timer = timer,
                        style = TimerDisplayDefault.Large
                    )
                    CloseAndSaveButtonGroup(
                        modifier = Modifier.padding(top = 16.dp),
                        onSave = onSave,
                        onClose = onBack
                    )
                }

                NumberKeyBoard(
                    modifier = Modifier
                        .widthIn(max = 300.dp)
                        .padding(vertical = 16.dp)
                        .aspectRatio(3f / 4),
                    onEvent = onKeyBoardEvent,
                    backSpaceIcon = BackSpaceIcon.Medium
                )
            }
        }
        Box {
            CloseButton(
                modifier = Modifier
                    .systemBarsPadding()
                    .displayCutoutPadding()
                    .padding(4.dp),
                onBack
            )
        }
    }
}

@Composable
private fun CloseButton(
    modifier: Modifier = Modifier,
    onClose: () -> Unit,
) {
    ExtinguishOutlinedIconButton(
        modifier = modifier,
        onClick = onClose,
        border = null,
        containerColor = MaterialTheme.colorScheme.surfaceContainerLowest,
        contentColor = MaterialTheme.colorScheme.onSurface
    ) {
        Icon(
            painterResource(R.drawable.close_24px), stringResource(R.string.Close)
        )
    }
}

@Composable
private fun CloseAndSaveButtonGroup(
    modifier: Modifier = Modifier,
    onClose: () -> Unit,
    onSave: () -> Unit
) {
    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        ExtinguishOutlinedButton(
            onClick = onClose,
            border = null,
            containerColor = MaterialTheme.colorScheme.surfaceContainerLowest,
            contentColor = MaterialTheme.colorScheme.onSurface
        ) {
            Icon(
                painterResource(R.drawable.close_20px), stringResource(R.string.Close)
            )
            Text(stringResource(R.string.Close))
        }
        ExtinguishOutlinedButton(
            onClick = onSave,
            border = null,
            containerColor = MaterialTheme.colorScheme.primary,
            contentColor = MaterialTheme.colorScheme.onPrimary
        ) {
            Icon(
                painterResource(R.drawable.save_20px), stringResource(R.string.Save)
            )
            Text(stringResource(R.string.Save))
        }
    }

}

@Preview(device = "spec:parent=pixel_5,orientation=landscape")
@Composable
private fun TimerPresetDialogHorizontalPreview() = ExtinguishTheme {
    TimerPresetDialog(
        {}, FakeTimersRepository(),
        TimerPresetDialogType.Horizontal
    )
}

@Preview(device = "spec:parent=pixel_5")
@Composable
private fun TimerPresetDialogVerticalPreview() = ExtinguishTheme {
    TimerPresetDialog(
        {}, FakeTimersRepository(),
        TimerPresetDialogType.Vertical
    )
}