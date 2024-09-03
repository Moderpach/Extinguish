package own.moderpach.extinguish.home.cards

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.SizeTransform
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.staggeredgrid.LazyStaggeredGridScope
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.delay
import own.moderpach.extinguish.ExtinguishNavGraph
import own.moderpach.extinguish.ExtinguishNavRoute
import own.moderpach.extinguish.ISolutionsStateManager
import own.moderpach.extinguish.ISystemPermissionsManager
import own.moderpach.extinguish.R
import own.moderpach.extinguish.home.HomeScreenCardKey
import own.moderpach.extinguish.home.HomeScreenCardKeys
import own.moderpach.extinguish.service.ExtinguishService
import own.moderpach.extinguish.settings.Solution
import own.moderpach.extinguish.settings.data.ISettingsRepository
import own.moderpach.extinguish.settings.data.SettingsTokens
import own.moderpach.extinguish.ui.components.ExtinguishButtonToken
import own.moderpach.extinguish.ui.components.ExtinguishCard
import own.moderpach.extinguish.ui.components.ExtinguishOutlinedButton
import own.moderpach.extinguish.ui.components.ExtinguishOutlinedIconButton
import own.moderpach.extinguish.ui.theme.ExtinguishTheme

val HomeScreenCardKeys.solution: HomeScreenCardKey get() = "Solution"

fun LazyStaggeredGridScope.solutionCard(
    extinguishServiceState: ExtinguishService.State,
    solutionsStateManager: ISolutionsStateManager,
    systemPermissionsManager: ISystemPermissionsManager,
    settingsRepository: ISettingsRepository,
    onNavigateTo: (ExtinguishNavRoute) -> Unit,
    onRequestService: () -> Unit
) = item(
    key = HomeScreenCardKeys.solution
) {
    SolutionCard(
        extinguishServiceState,
        solutionsStateManager,
        systemPermissionsManager,
        settingsRepository,
        onNavigateTo,
        onRequestService
    )
}

@Composable
fun SolutionCard(
    extinguishServiceState: ExtinguishService.State,
    solutionsStateManager: ISolutionsStateManager,
    systemPermissionsManager: ISystemPermissionsManager,
    settingsRepository: ISettingsRepository,
    onNavigateTo: (ExtinguishNavRoute) -> Unit,
    onRequestService: () -> Unit
) {
    val context = LocalContext.current

    val border = when (extinguishServiceState) {
        ExtinguishService.State.Destroyed -> BorderStroke(
            1.dp,
            MaterialTheme.colorScheme.outlineVariant
        )

        ExtinguishService.State.Created -> BorderStroke(2.dp, MaterialTheme.colorScheme.secondary)
        ExtinguishService.State.Prepared -> BorderStroke(2.dp, MaterialTheme.colorScheme.primary)
        ExtinguishService.State.Error -> BorderStroke(2.dp, MaterialTheme.colorScheme.error)
    }
    ExtinguishCard(
        border = border,
        contentPadding = PaddingValues(start = 16.dp, top = 16.dp, end = 16.dp, bottom = 12.dp),
        verticalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        val solutionText = when (settingsRepository.solution) {
            SettingsTokens.SolutionValue.ShizukuPowerOffScreen -> stringResource(R.string.str_ShizukuPowerOffScreen)
            SettingsTokens.SolutionValue.ShizukuScreenBrightnessNeg1 -> stringResource(R.string.str_ShizukuScreenBrightnessNeg1)
        }
        Text(
            stringResource(R.string.Current_solution),
            style = MaterialTheme.typography.labelMedium
        )
        Text(solutionText, style = MaterialTheme.typography.titleMedium)
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                ExtinguishOutlinedButton(
                    onClick = { onNavigateTo(ExtinguishNavGraph.Solution) },
                    icon = painterResource(R.drawable.swap_horiz_20px),
                    text = stringResource(R.string.Switch)
                )
            }
            ServiceButton(
                state = extinguishServiceState,
                onClick = onRequestService
            )
        }
    }
}

@Composable
private fun ServiceButton(
    modifier: Modifier = Modifier,
    state: ExtinguishService.State,
    onClick: () -> Unit
) = ExtinguishOutlinedButton(
    modifier = modifier,
    onClick = onClick,
    containerColor = MaterialTheme.colorScheme.surfaceContainerLow,
    shadowElevation = 6.dp,
) {
    val contentColor: Color = LocalContentColor.current
    AnimatedContent(
        state,
        transitionSpec = {
            fadeIn() + slideInVertically { it } togetherWith
                    fadeOut() + slideOutVertically { -it } using
                    SizeTransform()
        },
        label = "service button in solution card"
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(ExtinguishButtonToken.spacing)
        ) {
            when (it) {
                ExtinguishService.State.Destroyed -> {
                    Icon(painterResource(R.drawable.play_arrow_20px), null)
                    Text(stringResource(R.string.Start))
                }

                ExtinguishService.State.Created -> {
                    CircularProgressIndicator(
                        modifier = Modifier
                            .padding(horizontal = 4.dp)
                            .size(12.dp),
                        color = contentColor,
                        strokeWidth = 1.5.dp
                    )
                    Text(stringResource(R.string.Starting))
                }

                else -> {
                    Icon(painterResource(R.drawable.stop_20px), null)
                    Text(stringResource(R.string.Stop))
                }
            }
        }
    }
}

@Preview
@Composable
private fun ServiceButtonPreview() = ExtinguishTheme {
    var currentState by remember {
        mutableStateOf(ExtinguishService.State.Destroyed)
    }
    Surface {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.SpaceAround
        ) {
            ServiceButton(state = ExtinguishService.State.Destroyed) {}
            ServiceButton(state = ExtinguishService.State.Created) {}
            ServiceButton(state = ExtinguishService.State.Prepared) {}
            ServiceButton(state = ExtinguishService.State.Error) {}
            ServiceButton(state = currentState) {}
        }
    }
    LaunchedEffect(Unit) {
        while (true) {
            currentState = ExtinguishService.State.Destroyed
            delay(2000)
            currentState = ExtinguishService.State.Created
            delay(2000)
            currentState = ExtinguishService.State.Prepared
            delay(2000)
            currentState = ExtinguishService.State.Error
            delay(2000)
        }
    }
}