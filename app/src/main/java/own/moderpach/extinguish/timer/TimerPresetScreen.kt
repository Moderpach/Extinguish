package own.moderpach.extinguish.timer

import android.app.Activity
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.displayCutout
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.union
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.staggeredgrid.LazyVerticalStaggeredGrid
import androidx.compose.foundation.lazy.staggeredgrid.StaggeredGridCells
import androidx.compose.foundation.lazy.staggeredgrid.items
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.ScaffoldDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.windowsizeclass.ExperimentalMaterial3WindowSizeClassApi
import androidx.compose.material3.windowsizeclass.WindowHeightSizeClass
import androidx.compose.material3.windowsizeclass.WindowWidthSizeClass
import androidx.compose.material3.windowsizeclass.calculateWindowSizeClass
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavGraphBuilder
import own.moderpach.extinguish.ExtinguishNavGraph
import own.moderpach.extinguish.ExtinguishNavRoute
import own.moderpach.extinguish.R
import own.moderpach.extinguish.settings.data.ISettingsRepository
import own.moderpach.extinguish.settings.test.FakeSettingsRepository
import own.moderpach.extinguish.timer.components.TimerCard
import own.moderpach.extinguish.timer.components.TimerListItem
import own.moderpach.extinguish.timer.data.ITimersRepository
import own.moderpach.extinguish.timer.test.FakeTimersRepository
import own.moderpach.extinguish.ui.components.ExtinguishTopAppBarWithNavigationBack
import own.moderpach.extinguish.ui.navigation.extinguishComposable
import own.moderpach.extinguish.ui.theme.ExtinguishTheme
import own.moderpach.extinguish.util.add

val ExtinguishNavGraph.TimerPreset: ExtinguishNavRoute get() = "TimerPreset"

@OptIn(ExperimentalMaterial3WindowSizeClassApi::class)
fun NavGraphBuilder.timerPreset(
    onBack: () -> Unit,
    settingsRepository: ISettingsRepository,
    timersRepository: ITimersRepository,
    onNavigateTo: (ExtinguishNavRoute) -> Unit
) = extinguishComposable(
    ExtinguishNavGraph.TimerPreset,
) {
    val context = LocalContext.current
    val windowSizeClass = calculateWindowSizeClass(context as Activity)
    val type = if (
        windowSizeClass.heightSizeClass >= WindowHeightSizeClass.Medium &&
        windowSizeClass.widthSizeClass >= WindowWidthSizeClass.Medium
    ) TimerPresetScreenType.Grid
    else TimerPresetScreenType.List

    TimerPresetScreen(
        onBack,
        settingsRepository,
        timersRepository,
        onNavigateTo,
        type
    )
}

enum class TimerPresetScreenType {
    List, Grid
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TimerPresetScreen(
    onBack: () -> Unit,
    settingsRepository: ISettingsRepository,
    timersRepository: ITimersRepository,
    onNavigateTo: (ExtinguishNavRoute) -> Unit,
    type: TimerPresetScreenType
) {
    val scrollBehavior = TopAppBarDefaults.exitUntilCollapsedScrollBehavior()

    val timers = timersRepository.readAll().collectAsState(emptyList())

    Scaffold(
        modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
        contentWindowInsets = ScaffoldDefaults.contentWindowInsets.union(WindowInsets.displayCutout),
        topBar = {
            ExtinguishTopAppBarWithNavigationBack(
                onBack = onBack,
                titleString = stringResource(R.string.Preset_timers),
                scrollBehavior = scrollBehavior
            )
        },
        floatingActionButton = {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                ExtendedFloatingActionButton(
                    onClick = { onNavigateTo(ExtinguishNavGraph.TimerPresetDialog) },
                    containerColor = MaterialTheme.colorScheme.primary,
                    contentColor = MaterialTheme.colorScheme.onPrimary,
                    text = {
                        Text(stringResource(R.string.Add_timer))
                    },
                    icon = {
                        Icon(painterResource(R.drawable.add_24px), null)
                    }
                )
            }
        }
    ) { innerPadding ->
        when (type) {
            TimerPresetScreenType.List -> LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = innerPadding
            ) {
                items(
                    items = timers.value,
                    key = { it.hashCode() }
                ) {
                    TimerListItem(
                        timer = it,
                        onDelete = { timer ->
                            timersRepository.delete(timer)
                        }
                    )
                }
            }

            TimerPresetScreenType.Grid -> LazyVerticalStaggeredGrid(
                modifier = Modifier.fillMaxSize(),
                columns = StaggeredGridCells.Adaptive(300.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalItemSpacing = 12.dp,
                contentPadding = innerPadding.add(horizontal = 16.dp)
                    .add(bottom = (56 + 16 * 3).dp),
                content = {
                    items(
                        items = timers.value,
                        key = { it.hashCode() }
                    ) {
                        TimerCard(
                            timer = it,
                            onDelete = { timer ->
                                timersRepository.delete(timer)
                            }
                        )
                    }
                }
            )
        }


    }
}

@Preview
@Composable
private fun TimerPresetScreenListPreview() = ExtinguishTheme {
    Box(modifier = Modifier.background(MaterialTheme.colorScheme.background)) {
        TimerPresetScreen(
            onBack = {},
            settingsRepository = FakeSettingsRepository(),
            timersRepository = FakeTimersRepository(),
            onNavigateTo = {},
            type = TimerPresetScreenType.List
        )
    }
}

@Preview
@Composable
private fun TimerPresetScreenGridPreview() = ExtinguishTheme {
    Box(modifier = Modifier.background(MaterialTheme.colorScheme.background)) {
        TimerPresetScreen(
            onBack = {},
            settingsRepository = FakeSettingsRepository(),
            timersRepository = FakeTimersRepository(),
            onNavigateTo = {},
            type = TimerPresetScreenType.Grid
        )
    }
}
