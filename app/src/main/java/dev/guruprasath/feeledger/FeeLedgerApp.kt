package dev.guruprasath.feeledger

import android.app.Application
import dev.guruprasath.feeledger.work.DueReminderWorker
import dev.guruprasath.feeledger.work.Notifications

class FeeLedgerApp : Application() {
    lateinit var container: AppContainer
        private set

    override fun onCreate() {
        super.onCreate()
        container = AppContainer(this)
        Notifications.createChannel(this)
        DueReminderWorker.schedule(this, container.clock)
    }
}
