package dev.guruprasath.feeledger.work

import android.Manifest
import android.annotation.SuppressLint
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import androidx.core.app.NotificationChannelCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat
import dev.guruprasath.feeledger.MainActivity
import dev.guruprasath.feeledger.R
import dev.guruprasath.feeledger.domain.LedgerSummary
import dev.guruprasath.feeledger.domain.Money

object Notifications {
    private const val CHANNEL_DUES = "fee_dues"
    private const val ID_DAILY_SUMMARY = 1001

    fun createChannel(context: Context) {
        val channel = NotificationChannelCompat.Builder(CHANNEL_DUES, NotificationManagerCompat.IMPORTANCE_DEFAULT)
            .setName("Fee reminders")
            .setDescription("Daily summary of fees due today and overdue")
            .build()
        NotificationManagerCompat.from(context).createNotificationChannel(channel)
    }

    @SuppressLint("MissingPermission") // checked just below
    fun showDueSummary(context: Context, summary: LedgerSummary) {
        if (Build.VERSION.SDK_INT >= 33 &&
            ContextCompat.checkSelfPermission(context, Manifest.permission.POST_NOTIFICATIONS) !=
            PackageManager.PERMISSION_GRANTED
        ) {
            return
        }
        val parts = buildList {
            if (summary.overdueCount > 0) add("${summary.overdueCount} overdue")
            if (summary.dueTodayCount > 0) add("${summary.dueTodayCount} due today")
        }
        val open = PendingIntent.getActivity(
            context,
            0,
            Intent(context, MainActivity::class.java).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP),
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT,
        )
        val notification = NotificationCompat.Builder(context, CHANNEL_DUES)
            .setSmallIcon(R.drawable.ic_stat_fee)
            .setContentTitle("Fees: " + parts.joinToString(" · "))
            .setContentText("${Money.format(summary.outstandingPaise)} outstanding. Tap to send reminders.")
            .setContentIntent(open)
            .setAutoCancel(true)
            .build()
        NotificationManagerCompat.from(context).notify(ID_DAILY_SUMMARY, notification)
    }
}
