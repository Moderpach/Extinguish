package own.moderpach.extinguish.home

import androidx.compose.foundation.lazy.staggeredgrid.LazyStaggeredGridScope
import own.moderpach.extinguish.ExtinguishNavRoute
import own.moderpach.extinguish.home.cards.externalControl
import own.moderpach.extinguish.home.cards.externalControlCard
import own.moderpach.extinguish.home.cards.floatingButton
import own.moderpach.extinguish.home.cards.floatingButtonCard
import own.moderpach.extinguish.home.cards.moreSettings
import own.moderpach.extinguish.home.cards.moreSettingsCard
import own.moderpach.extinguish.home.cards.notificationControl
import own.moderpach.extinguish.home.cards.notificationControlCard
import own.moderpach.extinguish.home.cards.screenEventControl
import own.moderpach.extinguish.home.cards.screenEventControlCard
import own.moderpach.extinguish.home.cards.solution
import own.moderpach.extinguish.home.cards.solutionCard
import own.moderpach.extinguish.home.cards.tileControl
import own.moderpach.extinguish.home.cards.tileControlCard
import own.moderpach.extinguish.home.cards.volumeKeyControl
import own.moderpach.extinguish.home.cards.volumeKeyControlCard
import own.moderpach.extinguish.service.ExtinguishService
import own.moderpach.extinguish.settings.data.ISettingsRepository

fun LazyStaggeredGridScope.homeScreenCards(
    cardList: List<HomeScreenCardKey>,
    extinguishServiceState: ExtinguishService.State,
    settingsRepository: ISettingsRepository,
    onNavigateTo: (ExtinguishNavRoute) -> Unit,
    onRequestService: () -> Unit
) {
    cardList.forEach {
        when (it) {
            HomeScreenCardKeys.solution -> solutionCard(
                extinguishServiceState,
                settingsRepository,
                onNavigateTo,
                onRequestService
            )

            HomeScreenCardKeys.floatingButton -> floatingButtonCard(
                extinguishServiceState,
                settingsRepository,
                onNavigateTo
            )

            HomeScreenCardKeys.volumeKeyControl -> volumeKeyControlCard(
                extinguishServiceState,
                settingsRepository,
                onNavigateTo
            )

            HomeScreenCardKeys.screenEventControl -> screenEventControlCard(
                extinguishServiceState,
                settingsRepository,
                onNavigateTo
            )

            HomeScreenCardKeys.notificationControl -> notificationControlCard(
                extinguishServiceState,
                settingsRepository,
                onNavigateTo
            )

            HomeScreenCardKeys.tileControl -> tileControlCard(
                extinguishServiceState,
                settingsRepository,
                onNavigateTo
            )

            HomeScreenCardKeys.externalControl -> externalControlCard(
                extinguishServiceState,
                settingsRepository,
                onNavigateTo
            )

            HomeScreenCardKeys.moreSettings -> moreSettingsCard(
                extinguishServiceState,
                settingsRepository,
                onNavigateTo
            )
        }
    }
}

object HomeScreenCardKeys
typealias HomeScreenCardKey = String