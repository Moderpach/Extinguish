package own.moderpach.extinguish.test

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.os.Bundle
import android.os.IBinder
import android.se.omapi.SEService.OnConnectedListener
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.runBlocking
import own.moderpach.extinguish.service.ExtinguishService
import own.moderpach.extinguish.service.hosts.FloatingButtonHost
import own.moderpach.extinguish.ui.components.ExtinguishOutlinedButton
import own.moderpach.extinguish.ui.theme.ExtinguishTheme

private const val TAG = "TestActivity"

class TestActivity : ComponentActivity() {

    lateinit var extinguishService: ExtinguishService
    val serviceConnection = object : ServiceConnection {
        override fun onServiceConnected(name: ComponentName?, service: IBinder?) {
            val binder = service as ExtinguishService.LocalBinder
            extinguishService = binder.getService()
        }

        override fun onServiceDisconnected(name: ComponentName?) {

        }

    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        Intent(
            this@TestActivity,
            ExtinguishService::class.java
        ).also {
            bindService(it, serviceConnection, Context.BIND_AUTO_CREATE)
        }
        setContent {
            ExtinguishTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    Column(
                        modifier = Modifier.padding(innerPadding),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        var floatingButtonHost: FloatingButtonHost<ExtinguishService>? = null
                        ExtinguishOutlinedButton(
                            onClick = {
                                floatingButtonHost = FloatingButtonHost(
                                    extinguishService,
                                    isScreenOn = extinguishService.isScreenOn
                                ) { action ->
                                    Log.d(TAG, "FloatingButtonHostAction: $action")
                                }
                                floatingButtonHost?.create()
                            },
                            text = "create floating button host"
                        )
                        ExtinguishOutlinedButton(
                            onClick = {
                                floatingButtonHost?.destroy()
                            },
                            text = "destroy floating button host"
                        )
                    }
                }
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        unbindService(serviceConnection)
    }
}
