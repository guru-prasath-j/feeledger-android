package dev.guruprasath.feeledger.ui.onboarding

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import dev.guruprasath.feeledger.FeeLedgerApp
import dev.guruprasath.feeledger.domain.TutorProfile
import dev.guruprasath.feeledger.domain.Validators

@Composable
fun OnboardingScreen(onDone: () -> Unit) {
    val store = (LocalContext.current.applicationContext as FeeLedgerApp).container.profileStore
    var name by rememberSaveable { mutableStateOf("") }
    var vpa by rememberSaveable { mutableStateOf("") }
    var error by rememberSaveable { mutableStateOf<String?>(null) }

    Column(
        Modifier
            .fillMaxSize()
            .safeDrawingPadding()
            .imePadding()
            .verticalScroll(rememberScrollState())
            .padding(24.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        Text("FeeLedger", style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.Bold)
        Text(
            "Track monthly tuition fees and collect them over UPI with zero gateway charges. " +
                "Parents scan a QR with any UPI app; the money lands straight in your bank account.",
            style = MaterialTheme.typography.bodyLarge,
        )
        OutlinedTextField(
            value = name,
            onValueChange = { name = it; error = null },
            label = { Text("Your name (shown to parents)") },
            keyboardOptions = KeyboardOptions(capitalization = KeyboardCapitalization.Words),
            singleLine = true,
            modifier = Modifier.fillMaxWidth(),
        )
        OutlinedTextField(
            value = vpa,
            onValueChange = { vpa = it.trim(); error = null },
            label = { Text("Your UPI ID") },
            placeholder = { Text("yourname@okicici") },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
            isError = error != null,
            supportingText = { Text(error ?: "Find it in GPay, PhonePe or Paytm under your profile") },
            singleLine = true,
            modifier = Modifier.fillMaxWidth(),
        )
        Text(
            "Everything stays on this phone. Your UPI ID and parents' numbers are encrypted with a key held in the Android Keystore.",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
        Button(
            onClick = {
                val problem = Validators.validateProfile(name, vpa)
                if (problem != null) {
                    error = problem
                } else {
                    store.save(TutorProfile(name, vpa))
                    onDone()
                }
            },
            modifier = Modifier.fillMaxWidth(),
        ) { Text("Get started") }
    }
}
