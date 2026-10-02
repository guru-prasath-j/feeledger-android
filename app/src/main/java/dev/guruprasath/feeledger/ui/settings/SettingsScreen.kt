package dev.guruprasath.feeledger.ui.settings

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import dev.guruprasath.feeledger.FeeLedgerApp
import dev.guruprasath.feeledger.domain.LedgerCsv
import dev.guruprasath.feeledger.domain.TutorProfile
import dev.guruprasath.feeledger.domain.Validators
import dev.guruprasath.feeledger.security.AppLock
import dev.guruprasath.feeledger.ui.util.Share
import kotlinx.coroutines.launch
import java.time.LocalDate

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(onBack: () -> Unit) {
    val context = LocalContext.current
    val container = (context.applicationContext as FeeLedgerApp).container
    val store = container.profileStore
    val profile by store.profile.collectAsStateWithLifecycle()
    val lockEnabled by store.appLockEnabled.collectAsStateWithLifecycle()
    val lockAvailable = remember { AppLock.isAvailable(context) }
    val scope = rememberCoroutineScope()

    var name by rememberSaveable { mutableStateOf(profile?.name.orEmpty()) }
    var vpa by rememberSaveable { mutableStateOf(profile?.vpa.orEmpty()) }
    var message by rememberSaveable { mutableStateOf<String?>(null) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Settings") },
                navigationIcon = {
                    IconButton(onClick = onBack) { Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back") }
                },
            )
        },
    ) { padding ->
        Column(
            Modifier
                .fillMaxSize()
                .padding(padding)
                .imePadding()
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            Text("Payment details", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold)
            OutlinedTextField(
                value = name,
                onValueChange = { name = it; message = null },
                label = { Text("Name shown to parents") },
                singleLine = true,
                modifier = Modifier.fillMaxWidth(),
            )
            OutlinedTextField(
                value = vpa,
                onValueChange = { vpa = it.trim(); message = null },
                label = { Text("UPI ID") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                singleLine = true,
                modifier = Modifier.fillMaxWidth(),
            )
            message?.let { Text(it, style = MaterialTheme.typography.bodySmall) }
            Button(onClick = {
                val problem = Validators.validateProfile(name, vpa)
                message = if (problem == null) {
                    store.save(TutorProfile(name, vpa))
                    "Saved. New fee requests will use these details."
                } else {
                    problem
                }
            }) { Text("Save") }

            HorizontalDivider()

            Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                Column(Modifier.weight(1f)) {
                    Text("App lock", style = MaterialTheme.typography.titleMedium)
                    Text(
                        if (lockAvailable) "Ask for fingerprint, face or screen lock when opening FeeLedger"
                        else "Set up a screen lock on this phone to use app lock",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                }
                Switch(
                    checked = lockEnabled && lockAvailable,
                    onCheckedChange = { store.setAppLock(it) },
                    enabled = lockAvailable,
                )
            }

            HorizontalDivider()

            Text("Export", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold)
            Text(
                "Every recorded payment as a CSV file, for your accountant or income-tax filing.",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
            OutlinedButton(onClick = {
                scope.launch {
                    val (students, payments) = container.repository.snapshot()
                    val csv = LedgerCsv.build(students, payments)
                    Share.csv(context, csv, "feeledger-${LocalDate.now(container.clock)}.csv")
                }
            }) { Text("Export payments (CSV)") }

            HorizontalDivider()
            Text(
                "Privacy: FeeLedger has no account, no server and no internet permission. " +
                    "Data lives only on this phone; parents' numbers, UTRs and your UPI ID are encrypted " +
                    "with AES-256-GCM using a key kept in the Android Keystore. Backups are disabled so " +
                    "encrypted data is never copied off the device.",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }
    }
}
