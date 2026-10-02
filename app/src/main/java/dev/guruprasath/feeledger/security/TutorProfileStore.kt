package dev.guruprasath.feeledger.security

import android.content.Context
import dev.guruprasath.feeledger.domain.TutorProfile
import dev.guruprasath.feeledger.domain.Vpa
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

/** The tutor's own name and UPI ID, stored encrypted; plus the app-lock preference. */
class TutorProfileStore(context: Context, private val cipher: FieldCipher) {

    private val prefs = context.getSharedPreferences(PREFS, Context.MODE_PRIVATE)

    private val _profile = MutableStateFlow(read())
    val profile: StateFlow<TutorProfile?> = _profile.asStateFlow()

    private val _appLock = MutableStateFlow(prefs.getBoolean(KEY_LOCK, false))
    val appLockEnabled: StateFlow<Boolean> = _appLock.asStateFlow()

    fun save(profile: TutorProfile) {
        val clean = TutorProfile(profile.name.trim(), Vpa.normalize(profile.vpa))
        prefs.edit()
            .putString(KEY_NAME, cipher.encrypt(clean.name))
            .putString(KEY_VPA, cipher.encrypt(clean.vpa))
            .apply()
        _profile.value = clean
    }

    fun setAppLock(enabled: Boolean) {
        prefs.edit().putBoolean(KEY_LOCK, enabled).apply()
        _appLock.value = enabled
    }

    private fun read(): TutorProfile? {
        val name = prefs.getString(KEY_NAME, null) ?: return null
        val vpa = prefs.getString(KEY_VPA, null) ?: return null
        return runCatching { TutorProfile(cipher.decrypt(name), cipher.decrypt(vpa)) }.getOrNull()
    }

    private companion object {
        const val PREFS = "tutor_profile"
        const val KEY_NAME = "name_enc"
        const val KEY_VPA = "vpa_enc"
        const val KEY_LOCK = "app_lock"
    }
}
