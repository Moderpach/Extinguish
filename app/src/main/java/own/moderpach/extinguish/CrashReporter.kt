package own.moderpach.extinguish

import android.Manifest
import android.app.ActivityManager
import android.app.ApplicationExitInfo
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import java.text.DateFormat
import java.util.Date

private const val TAG = "CrashReporter"

/** Notifies on crashes immediately, and on other unexpected deaths at next start. */
object CrashReporter {

    private const val PREFS = "crash_reporter"
    private const val KEY_LAST_CHECKED_EXIT = "last_checked_exit_timestamp"

    private const val CRASH_NOTIFICATION_ID = 0x0C4A54
    private const val KILLED_NOTIFICATION_ID = 0x0C4A55

    fun install(context: Context) {
        val appContext = context.applicationContext
        val previous = Thread.getDefaultUncaughtExceptionHandler()
        Thread.setDefaultUncaughtExceptionHandler { thread, throwable ->
            try {
                notifyCrash(appContext, throwable)
            } catch (t: Throwable) {
                Log.e(TAG, "failed to post crash notification", t)
            }
            previous?.uncaughtException(thread, throwable)
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            try {
                reportPreviousExits(appContext)
            } catch (t: Throwable) {
                Log.e(TAG, "failed to read process exit history", t)
            }
        }
    }

    private fun notifyCrash(context: Context, throwable: Throwable) {
        val summary = "${throwable.javaClass.simpleName}: ${throwable.message.orEmpty()}".take(200)
        notify(
            context,
            CRASH_NOTIFICATION_ID,
            context.getString(R.string.str_crash_title),
            context.getString(R.string.str_crash_text, summary),
            throwable.stackTraceToString()
        )
    }

    @RequiresApi(Build.VERSION_CODES.R)
    private fun reportPreviousExits(context: Context) {
        val prefs = context.getSharedPreferences(PREFS, Context.MODE_PRIVATE)
        val activityManager = context.getSystemService(ActivityManager::class.java) ?: return
        val exits = activityManager.getHistoricalProcessExitReasons(context.packageName, 0, 10)
            .filter { it.processName == context.packageName }
        val newest = exits.maxOfOrNull { it.timestamp } ?: return

        val lastChecked = prefs.getLong(KEY_LAST_CHECKED_EXIT, -1L)
        prefs.edit().putLong(KEY_LAST_CHECKED_EXIT, maxOf(newest, lastChecked)).apply()
        // first run
        if (lastChecked < 0) return

        val unexpected = exits.filter { it.timestamp > lastChecked && it.isUnexpectedServiceDeath() }
        val latest = unexpected.maxByOrNull { it.timestamp } ?: return

        val time = DateFormat.getDateTimeInstance(DateFormat.SHORT, DateFormat.SHORT)
            .format(Date(latest.timestamp))
        val reason = buildString {
            append(latest.reasonName())
            latest.description?.takeIf { it.isNotBlank() }?.let { append(" - ").append(it) }
        }
        val details = unexpected.sortedByDescending { it.timestamp }.joinToString("\n\n") {
            "${Date(it.timestamp)}\nreason=${it.reasonName()} status=${it.status} " +
                    "importance=${it.importance}\n${it.description.orEmpty()}"
        }
        notify(
            context,
            KILLED_NOTIFICATION_ID,
            context.getString(R.string.str_killed_title),
            context.getString(R.string.str_killed_text, time, reason),
            details
        )
    }

    @RequiresApi(Build.VERSION_CODES.R)
    private fun ApplicationExitInfo.isUnexpectedServiceDeath(): Boolean {
        // already reported by the uncaught exception handler
        if (reason == ApplicationExitInfo.REASON_CRASH) return false
        if (
            reason in setOf(
                ApplicationExitInfo.REASON_EXIT_SELF,
                ApplicationExitInfo.REASON_USER_REQUESTED,
                ApplicationExitInfo.REASON_USER_STOPPED,
                ApplicationExitInfo.REASON_PERMISSION_CHANGE,
                ApplicationExitInfo.REASON_PACKAGE_UPDATED,
                ApplicationExitInfo.REASON_PACKAGE_STATE_CHANGE,
            )
        ) return false
        return reason == ApplicationExitInfo.REASON_CRASH_NATIVE ||
                reason == ApplicationExitInfo.REASON_ANR ||
                importance <= ActivityManager.RunningAppProcessInfo.IMPORTANCE_FOREGROUND_SERVICE
    }

    @RequiresApi(Build.VERSION_CODES.R)
    private fun ApplicationExitInfo.reasonName() = when (reason) {
        ApplicationExitInfo.REASON_ANR -> "app not responding"
        ApplicationExitInfo.REASON_CRASH_NATIVE -> "native crash"
        ApplicationExitInfo.REASON_LOW_MEMORY -> "low memory"
        ApplicationExitInfo.REASON_EXCESSIVE_RESOURCE_USAGE -> "excessive resource usage"
        ApplicationExitInfo.REASON_SIGNALED -> "killed by signal $status"
        ApplicationExitInfo.REASON_INITIALIZATION_FAILURE -> "initialization failure"
        ApplicationExitInfo.REASON_DEPENDENCY_DIED -> "dependency died"
        ApplicationExitInfo.REASON_FREEZER -> "frozen"
        ApplicationExitInfo.REASON_OTHER -> "killed by system"
        else -> "reason $reason"
    }

    private fun notify(
        context: Context,
        id: Int,
        title: String,
        text: String,
        logs: String,
    ) {
        if (
            Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU &&
            context.checkSelfPermission(Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED
        ) {
            Log.w(TAG, "can't post \"$title\": notification permission not granted")
            return
        }
        context.createNotificationChannels()

        val openApp = PendingIntent.getActivity(
            context,
            id,
            Intent(context, MainActivity::class.java).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK),
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
        )
        val copyLogs = PendingIntent.getBroadcast(
            context,
            id,
            Intent(context, ExceptionNotifier::class.java).apply {
                action = ExceptionNotifier.ACTION_COPY
                putExtra(ExceptionNotifier.EXTRA_SCENE_NAME, title)
                putExtra(ExceptionNotifier.EXTRA_EXCEPTION, logs)
            },
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
        )
        val notification = NotificationCompat.Builder(context, ExtinguishNotificationChannels.Exception.id)
            .setSmallIcon(R.drawable.bug_report_24px)
            .setContentTitle(title)
            .setContentText(text)
            .setStyle(NotificationCompat.BigTextStyle().bigText(text))
            .setContentIntent(openApp)
            .setAutoCancel(true)
            .addAction(
                NotificationCompat.Action.Builder(null, context.getString(R.string.Copy_logs), copyLogs).build()
            )
            .build()
        NotificationManagerCompat.from(context).notify(id, notification)
    }
}
