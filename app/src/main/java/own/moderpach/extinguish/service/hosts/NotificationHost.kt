package own.moderpach.extinguish.service.hosts

import android.Manifest
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.lifecycle.LifecycleOwner
import own.moderpach.extinguish.ExtinguishNotificationChannels
import own.moderpach.extinguish.R
import own.moderpach.extinguish.service.ExtinguishService

private const val TAG = "NotificationHost"

class NotificationHost<T>(
    private val owner: T,
) : LifecycleOwner by owner
        where T : Context, T : LifecycleOwner {

    companion object {
        const val NOTIFICATION_ID = 1
    }

    private val manager = NotificationManagerCompat.from(owner)

    private val builder =
        NotificationCompat.Builder(owner, ExtinguishNotificationChannels.Service.id)
            .setSmallIcon(R.drawable.extinguish_24px)
            .setContentTitle("Extinguish 服务正在运行")
            .setOnlyAlertOnce(true)
            .setOngoing(true)

    private val basicIntent
        get() = Intent(
            owner, ExtinguishService::class.java
        )

    private fun Intent.toPendingIntent(requestCode: Int = 0) = PendingIntent.getService(
        owner,
        requestCode,
        this,
        PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
    )

    private fun NotificationCompat.Builder.turnScreenOnAction(): NotificationCompat.Builder {
        val intent = basicIntent.apply {
            val value = ExtinguishService.EXTRA_VALUE_SCREEN_ON
            putExtra(ExtinguishService.EXTRA_SCREEN, value)
        }
        val text = "打开屏幕"
        val action = NotificationCompat.Action.Builder(
            null, text, intent.toPendingIntent(0)
        ).build()
        return addAction(action)
    }

    private fun NotificationCompat.Builder.turnScreenOffAction(): NotificationCompat.Builder {
        val intent = basicIntent.apply {
            val value = ExtinguishService.EXTRA_VALUE_SCREEN_OFF
            putExtra(ExtinguishService.EXTRA_SCREEN, value)
        }
        val text = "关闭屏幕"
        val action = NotificationCompat.Action.Builder(
            null, text, intent.toPendingIntent(1)
        ).build()
        return addAction(action)
    }

    private fun NotificationCompat.Builder.showFloatingButtonStateAction(): NotificationCompat.Builder {
        val intent = basicIntent.apply {
            val value = ExtinguishService.EXTRA_VALUE_FLOATING_BUTTON_SHOW
            putExtra(ExtinguishService.EXTRA_FLOATING_BUTTON, value)
        }
        val text = "显示悬浮按钮"
        val action = NotificationCompat.Action.Builder(
            null, text, intent.toPendingIntent(3)
        ).build()
        return addAction(action)
    }

    private fun NotificationCompat.Builder.hideFloatingButtonStateAction(): NotificationCompat.Builder {
        val intent = basicIntent.apply {
            val value = ExtinguishService.EXTRA_VALUE_FLOATING_BUTTON_HIDE
            putExtra(ExtinguishService.EXTRA_FLOATING_BUTTON, value)
        }
        val text = "隐藏悬浮按钮"
        val action = NotificationCompat.Action.Builder(
            null, text, intent.toPendingIntent(4)
        ).build()
        return addAction(action)
    }

    private fun NotificationCompat.Builder.stopServiceAction(): NotificationCompat.Builder {
        val intent = basicIntent.apply {
            val value = ExtinguishService.EXTRA_VALUE_STOP
            putExtra(ExtinguishService.EXTRA_STOP, value)
        }
        val text = "停止服务"
        val action = NotificationCompat.Action.Builder(
            null, text, intent.toPendingIntent(5)
        ).build()
        return addAction(action)
    }

    fun notify(
        isScreenOn: Boolean,
        isFloatingButtonShowing: Boolean,
        isFloatingButtonEnabled: Boolean
    ) = builder
        .setContentText("预期当前屏幕状态为 ${getCurrentStateText(isScreenOn)}")
        .clearActions()
        .let {
            if (isScreenOn) it.turnScreenOffAction()
            else it.turnScreenOnAction()
        }
        .let {
            if (isFloatingButtonEnabled) {
                if (isFloatingButtonShowing) it.hideFloatingButtonStateAction()
                else it.showFloatingButtonStateAction()
            } else it
        }
        .stopServiceAction()
        .build().also {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                if (
                    owner.checkSelfPermission(Manifest.permission.POST_NOTIFICATIONS) ==
                    PackageManager.PERMISSION_GRANTED
                ) {
                    manager.notify(NOTIFICATION_ID, it)
                }
            } else {
                manager.notify(NOTIFICATION_ID, it)
            }
        }

    private fun getCurrentStateText(isScreenOn: Boolean): String {
        return when (isScreenOn) {
            true -> "打开"
            false -> "关闭"
        }
    }

}