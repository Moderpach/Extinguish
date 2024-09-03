package own.moderpach.extinguish.service

import android.content.Intent
import android.os.Build
import android.service.quicksettings.Tile
import android.service.quicksettings.TileService
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import own.moderpach.extinguish.R

class ScreenControlTile : TileService() {

    private val coroutineScope = CoroutineScope(SupervisorJob() + Dispatchers.Main.immediate)

    override fun onDestroy() {
        super.onDestroy()
        coroutineScope.cancel()
    }

    override fun onStartListening() {
        super.onStartListening()
        coroutineScope.launch {
            ExtinguishService.state.collectLatest {
                when (it) {
                    ExtinguishService.State.Prepared -> {
                        when (ExtinguishService.screenState.value) {
                            ExtinguishService.ScreenState.On -> setQsStateOff()
                            ExtinguishService.ScreenState.Off -> setQsStateOn()
                        }
                    }

                    else -> setQsStateUnavailable()
                }
            }
        }
        coroutineScope.launch {
            ExtinguishService.screenState.collectLatest {
                when (it) {
                    ExtinguishService.ScreenState.On -> if (ExtinguishService.state.value == ExtinguishService.State.Prepared) setQsStateOff()
                    ExtinguishService.ScreenState.Off -> if (ExtinguishService.state.value == ExtinguishService.State.Prepared) setQsStateOn()
                }
            }
        }
    }

    override fun onClick() {
        super.onClick()
        if (ExtinguishService.state.value != ExtinguishService.State.Prepared) return
        when (ExtinguishService.screenState.value) {
            ExtinguishService.ScreenState.On -> turnScreenOff()
            ExtinguishService.ScreenState.Off -> turnScreenOn()
        }
    }

    private fun turnScreenOn() {
        Intent(this, ExtinguishService::class.java).apply {
            putExtra(ExtinguishService.EXTRA_SCREEN, ExtinguishService.EXTRA_VALUE_SCREEN_ON)
            startService(this)
        }
    }

    private fun turnScreenOff() {
        Intent(this, ExtinguishService::class.java).apply {
            putExtra(ExtinguishService.EXTRA_SCREEN, ExtinguishService.EXTRA_VALUE_SCREEN_OFF)
            startService(this)
        }
    }

    private fun setQsStateOff() {
        qsTile.state = Tile.STATE_INACTIVE
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            qsTile.stateDescription = getText(R.string.Off)
        }
        qsTile.updateTile()
    }

    private fun setQsStateOn() {
        qsTile.state = Tile.STATE_ACTIVE
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            qsTile.stateDescription = getText(R.string.On)
        }
        qsTile.updateTile()
    }

    private fun setQsStateUnavailable() {
        qsTile.state = Tile.STATE_UNAVAILABLE
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            qsTile.stateDescription = getText(R.string.Unavailable)
        }
        qsTile.updateTile()
    }
}