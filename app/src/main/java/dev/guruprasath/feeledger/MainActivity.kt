package dev.guruprasath.feeledger

import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import dev.guruprasath.feeledger.security.AppLock
import dev.guruprasath.feeledger.ui.FeeLedgerNavHost
import dev.guruprasath.feeledger.ui.theme.FeeLedgerTheme

/** FragmentActivity because BiometricPrompt needs one. */
class MainActivity : FragmentActivity() {

    private var unlocked by mutableStateOf(false)

    override fun onCreate(savedInstanceState: Bundle?) {
        enableEdgeToEdge()
        super.onCreate(savedInstanceState)
        unlocked = savedInstanceState?.getBoolean(KEY_UNLOCKED) ?: false
        val container = (application as FeeLedgerApp).container

        setContent {
            FeeLedgerTheme {
                Surface(Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background) {
                    val lockEnabled by container.profileStore.appLockEnabled.collectAsStateWithLifecycle()
                    if (lockEnabled && !unlocked && AppLock.isAvailable(this)) {
                        LockedScreen(onUnlock = { authenticate() })
                        LaunchedEffect(Unit) { authenticate() }
                    } else {
                        FeeLedgerNavHost(container)
                    }
                }
            }
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putBoolean(KEY_UNLOCKED, unlocked)
    }

    private fun authenticate() {
        AppLock.prompt(this) { unlocked = true }
    }

    private companion object {
        const val KEY_UNLOCKED = "unlocked"
    }
}

@Composable
private fun LockedScreen(onUnlock: () -> Unit) {
    Column(
        Modifier.fillMaxSize().padding(32.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp, Alignment.CenterVertically),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Text("FeeLedger is locked", style = MaterialTheme.typography.headlineSmall)
        Button(onClick = onUnlock) { Text("Unlock") }
    }
}
