package dev.guruprasath.feeledger.ui.student

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
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
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import dev.guruprasath.feeledger.domain.StudentField
import dev.guruprasath.feeledger.ui.appViewModel
import dev.guruprasath.feeledger.ui.components.MonthStepper

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StudentEditScreen(studentId: Long?, onBack: () -> Unit, onSaved: (Long) -> Unit) {
    val viewModel = appViewModel(key = "edit-$studentId") { StudentEditViewModel(it.repository, it.clock, studentId) }
    val form = viewModel.form

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(if (viewModel.isNew) "Add student" else "Edit student") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
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
            OutlinedTextField(
                value = form.name,
                onValueChange = { v -> viewModel.update { it.copy(name = v) } },
                label = { Text("Student name") },
                isError = StudentField.NAME in form.errors,
                supportingText = form.errors[StudentField.NAME]?.let { msg -> @Composable { Text(msg) } },
                keyboardOptions = KeyboardOptions(capitalization = KeyboardCapitalization.Words),
                singleLine = true,
                modifier = Modifier.fillMaxWidth(),
            )
            OutlinedTextField(
                value = form.batch,
                onValueChange = { v -> viewModel.update { it.copy(batch = v) } },
                label = { Text("Batch / class (optional)") },
                placeholder = { Text("e.g. Class 8 Maths, Sat 5 PM") },
                singleLine = true,
                modifier = Modifier.fillMaxWidth(),
            )
            OutlinedTextField(
                value = form.phone,
                onValueChange = { v -> viewModel.update { it.copy(phone = v) } },
                label = { Text("Parent's mobile (optional)") },
                isError = StudentField.PHONE in form.errors,
                supportingText = {
                    Text(form.errors[StudentField.PHONE] ?: "Stored encrypted on this phone only")
                },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                singleLine = true,
                modifier = Modifier.fillMaxWidth(),
            )
            OutlinedTextField(
                value = form.fee,
                onValueChange = { v -> viewModel.update { it.copy(fee = v) } },
                label = { Text("Monthly fee (₹)") },
                isError = StudentField.FEE in form.errors,
                supportingText = form.errors[StudentField.FEE]?.let { msg -> @Composable { Text(msg) } },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                singleLine = true,
                modifier = Modifier.fillMaxWidth(),
            )
            OutlinedTextField(
                value = form.dueDay,
                onValueChange = { v -> viewModel.update { it.copy(dueDay = v.filter(Char::isDigit).take(2)) } },
                label = { Text("Fee due on day of month") },
                isError = StudentField.DUE_DAY in form.errors,
                supportingText = {
                    Text(form.errors[StudentField.DUE_DAY] ?: "Day 31 falls on the last day of shorter months")
                },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                singleLine = true,
                modifier = Modifier.fillMaxWidth(),
            )
            Text("Billing starts from", style = MaterialTheme.typography.labelLarge)
            MonthStepper(form.startMonth, onChange = { m -> viewModel.update { it.copy(startMonth = m) } })
            Text(
                "Every month from here to today is billed, so past unpaid months show up as arrears.",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
            Button(
                onClick = { viewModel.save(onSaved) },
                enabled = !form.saving,
                modifier = Modifier.fillMaxWidth(),
            ) {
                Text(if (viewModel.isNew) "Save student" else "Save changes")
            }
        }
    }
}
