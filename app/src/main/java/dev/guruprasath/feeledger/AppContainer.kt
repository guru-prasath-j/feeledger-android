package dev.guruprasath.feeledger

import android.content.Context
import dev.guruprasath.feeledger.data.FeeRepository
import dev.guruprasath.feeledger.data.local.AppDatabase
import dev.guruprasath.feeledger.security.AesGcmFieldCipher
import dev.guruprasath.feeledger.security.FieldCipher
import dev.guruprasath.feeledger.security.KeystoreKeyProvider
import dev.guruprasath.feeledger.security.TutorProfileStore
import java.time.Clock

/** Manual dependency wiring; small enough that a DI framework would be overhead. */
class AppContainer(context: Context) {
    private val appContext = context.applicationContext

    val clock: Clock = Clock.systemDefaultZone()
    private val cipher: FieldCipher = AesGcmFieldCipher(KeystoreKeyProvider("feeledger.fields.v1"))
    private val database: AppDatabase = AppDatabase.build(appContext)

    val repository = FeeRepository(database, cipher, clock)
    val profileStore = TutorProfileStore(appContext, cipher)
}
