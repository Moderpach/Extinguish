package own.moderpach.extinguish

import android.Manifest
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.util.Log
import android.widget.Toast
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat

private const val TAG = "ExceptionNotifier"

object ExceptionMassages {
    val MethodNotFound = R.string.str_exc_methodNotFound
    val ReadBrightnessSettingFailed = R.string.str_exc_readBrightnessSettingFailed
    val ShizukuFailed = R.string.str_exc_shizukuFailed
}

fun Context.notifyException(
    massage: Int,
    supporting: String?
) = notifyException(getString(massage), supporting)

fun Context.notifyException(
    massage: String,
    supporting: String?
) {
    Intent(this, ExceptionNotifier::class.java).apply {
        setAction(ExceptionNotifier.ACTION_NOTIFY)
        putExtra(ExceptionNotifier.EXTRA_MASSAGE, massage)
        putExtra(ExceptionNotifier.EXTRA_EXCEPTION, supporting)
        sendBroadcast(this)
    }
}

class ExceptionNotifier : BroadcastReceiver() {

    companion object {
        const val ACTION_NOTIFY = "extinguish.exception.notify"
        const val ACTION_COPY = "extinguish.exception.copy"

        const val EXTRA_EXCEPTION = "exception"
        const val EXTRA_MASSAGE = "massage"

        private var NOTIFICATION_ID = 1
    }

    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action == ACTION_NOTIFY) {
            Log.d(TAG, "onReceive: ACTION_NOTIFY")
            Log.d(TAG, "onReceive: EXTRA_MASSAGE = ${intent.getStringExtra(EXTRA_MASSAGE)}")
            Log.d(TAG, "onReceive: EXTRA_EXCEPTION = ${intent.getStringExtra(EXTRA_EXCEPTION)}")
            val copyAction = with(
                PendingIntent.getBroadcast(
                    context, NOTIFICATION_ID,
                    Intent(context, ExceptionNotifier::class.java).apply {
                        setAction(ACTION_COPY)
                        putExtra(EXTRA_MASSAGE, intent.getStringExtra(EXTRA_MASSAGE))
                        putExtra(EXTRA_EXCEPTION, intent.getStringExtra(EXTRA_EXCEPTION))
                    },
                    PendingIntent.FLAG_IMMUTABLE + PendingIntent.FLAG_UPDATE_CURRENT
                )
            ) {
                NotificationCompat.Action.Builder(
                    null, context.getString(R.string.Copy_logs), this
                ).build()
            }
            val builder =
                NotificationCompat.Builder(context, ExtinguishNotificationChannels.Exception.id)
                    .setSmallIcon(R.drawable.bug_report_24px)
                    .setContentTitle(context.getString(R.string.Runtime_exception_detected))
                    .setContentText(intent.getStringExtra(EXTRA_MASSAGE))
                    .clearActions()
                    .addAction(copyAction)
            with(NotificationManagerCompat.from(context)) {
                if (
                    Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU &&
                    context.checkSelfPermission(
                        Manifest.permission.POST_NOTIFICATIONS
                    ) != PackageManager.PERMISSION_GRANTED
                ) {
                    return@with
                }
                notify(NOTIFICATION_ID, builder.build())
            }
        }

        if (intent.action == ACTION_COPY) {
            val clipboardManager =
                context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
            val clip: ClipData = ClipData.newPlainText(
                intent.getStringExtra(EXTRA_MASSAGE),
                intent.getStringExtra(EXTRA_EXCEPTION) ?: ""
            )
            clipboardManager.setPrimaryClip(clip)
            if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.S_V2)
                Toast.makeText(
                    context,
                    context.getString(R.string.Copied), Toast.LENGTH_SHORT
                ).show()
        }

    }
}