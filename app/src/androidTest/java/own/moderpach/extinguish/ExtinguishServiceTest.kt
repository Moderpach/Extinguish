package own.moderpach.extinguish

import android.Manifest
import android.app.Instrumentation
import android.content.Intent
import android.os.IBinder
import android.util.Log
import android.view.KeyEvent
import androidx.compose.ui.geometry.Offset
import androidx.lifecycle.lifecycleScope
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import androidx.test.rule.GrantPermissionRule
import androidx.test.rule.ServiceTestRule
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.Assert
import org.junit.runner.RunWith
import own.moderpach.extinguish.service.ExtinguishService
import own.moderpach.extinguish.service.hosts.FloatingButtonHost
import own.moderpach.extinguish.service.hosts.VolumeKeyEventWindowHost
import own.moderpach.extinguish.service.hosts.windows.FloatingButtonWindow
import own.moderpach.extinguish.service.hosts.windows.TimerSetterPopupWindow
import own.moderpach.extinguish.timer.test.FakeTimersRepository

private const val TAG = "ExtinguishServiceTest"

@RunWith(AndroidJUnit4::class)
class ExtinguishServiceTest {

    @get:Rule
    val serviceRule = ServiceTestRule()

    @get:Rule
    val displayOnAppsPermissionRule: GrantPermissionRule = GrantPermissionRule.grant(
        Manifest.permission.SYSTEM_ALERT_WINDOW
    )

    private lateinit var service: ExtinguishService
    private lateinit var instrumentation: Instrumentation

    @Before
    fun setUp() {
        instrumentation = InstrumentationRegistry.getInstrumentation()
        val serviceIntent = Intent(
            ApplicationProvider.getApplicationContext(),
            ExtinguishService::class.java
        )
        val binder: IBinder = serviceRule.bindService(serviceIntent)
        service = (binder as ExtinguishService.LocalBinder).getService()
    }

    @Test
    fun testFloatingButtonWindow() {
        runBlockingOnMainCoroutine {
            val floatingButtonWindow = FloatingButtonWindow(service, true, {})
            floatingButtonWindow.create()
            delay(5000)
            floatingButtonWindow.destroy()
            delay(500)
        }
    }

    @Test
    fun testTimerSetterPopupWindow() {
        runBlockingOnMainCoroutine {
            val timerSetterPopupWindow = TimerSetterPopupWindow(
                service,
                FakeTimersRepository(),
                onDismiss = {

                },
                {}
            )
            timerSetterPopupWindow.popupBias = Offset(0.5f, 0.5f)
            timerSetterPopupWindow.create()
            delay(5000)
            timerSetterPopupWindow.destroy()
            delay(500)
        }
    }

    @Test
    fun testFloatingButtonHost() {
        runBlockingOnMainCoroutine {
            val floatingButtonHost = FloatingButtonHost(
                service,
                isScreenOn = true
            ) { action ->

            }
            floatingButtonHost.create()
            delay(5000)
            floatingButtonHost.destroy()
            delay(500)
        }
    }

    @Test
    fun testVolumeKeyEventWindowHost() {
        runBlockingOnMainCoroutine {
            val volumeKeyEventWindowHost = VolumeKeyEventWindowHost(service) {

            }.also { it.create() }
            delay(500)
            volumeKeyEventWindowHost.sleep()
            delay(500)
            volumeKeyEventWindowHost.wake()
            delay(500)
            volumeKeyEventWindowHost.destroy()
            delay(500)
        }
    }

    private fun <T> runBlockingOnMainCoroutine(
        block: suspend CoroutineScope.() -> T
    ) = runBlocking {
        runOnMainCoroutine(block)
    }

    private suspend fun <T> runOnMainCoroutine(
        block: suspend CoroutineScope.() -> T
    ) = withContext(service.lifecycleScope.coroutineContext, block)
}