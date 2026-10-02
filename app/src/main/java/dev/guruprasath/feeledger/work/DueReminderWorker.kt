package dev.guruprasath.feeledger.work

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.WorkerParameters
import dev.guruprasath.feeledger.FeeLedgerApp
import dev.guruprasath.feeledger.domain.FeeCalculator
import java.time.Clock
import java.time.Duration
import java.time.LocalDate
import java.time.ZonedDateTime
import java.util.concurrent.TimeUnit

/** Runs once a day around 9 AM and posts one summary notification if anything is due. */
class DueReminderWorker(context: Context, params: WorkerParameters) : CoroutineWorker(context, params) {

    override suspend fun doWork(): Result {
        val container = (applicationContext as FeeLedgerApp).container
        val (students, payments) = container.repository.snapshot()
        val summary = FeeCalculator.summarize(students, payments, LocalDate.now(container.clock))
        if (summary.overdueCount + summary.dueTodayCount > 0) {
            Notifications.showDueSummary(applicationContext, summary)
        }
        return Result.success()
    }

    companion object {
        private const val UNIQUE_NAME = "daily-fee-reminder"
        private const val HOUR = 9

        fun schedule(context: Context, clock: Clock) {
            val request = PeriodicWorkRequestBuilder<DueReminderWorker>(1, TimeUnit.DAYS)
                .setInitialDelay(delayUntilNextRun(clock).toMillis(), TimeUnit.MILLISECONDS)
                .build()
            WorkManager.getInstance(context)
                .enqueueUniquePeriodicWork(UNIQUE_NAME, ExistingPeriodicWorkPolicy.KEEP, request)
        }

        fun delayUntilNextRun(clock: Clock): Duration {
            val now = ZonedDateTime.now(clock)
            var next = now.toLocalDate().atTime(HOUR, 0).atZone(now.zone)
            if (!next.isAfter(now)) next = next.plusDays(1)
            return Duration.between(now, next)
        }
    }
}
