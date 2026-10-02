package dev.guruprasath.feeledger.ui.student

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import dev.guruprasath.feeledger.domain.FeeRequest
import dev.guruprasath.feeledger.domain.FeeRequests
import dev.guruprasath.feeledger.domain.Money
import dev.guruprasath.feeledger.domain.MonthlyDue
import dev.guruprasath.feeledger.domain.Payment
import dev.guruprasath.feeledger.domain.PaymentMethod
import dev.guruprasath.feeledger.domain.Student
import dev.guruprasath.feeledger.ui.appViewModel
import dev.guruprasath.feeledger.ui.components.StatusPill
import dev.guruprasath.feeledger.ui.util.QrCode
import dev.guruprasath.feeledger.ui.util.Share
import java.time.YearMonth
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.Locale

private val PAID_AT = DateTimeFormatter.ofPattern("d MMM yyyy, h:mm a", Locale.ENGLISH)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StudentDetailScreen(studentId: Long, onBack: () -> Unit, onEdit: () -> Unit) {
    val viewModel = appViewModel(key = "detail-$studentId") {
        StudentDetailViewModel(it.repository, it.profileStore, it.clock, studentId)
    }
    val state by viewModel.state.collectAsStateWithLifecycle()
    val student = state.student

    var request by remember { mutableStateOf<FeeRequest?>(null) }
    var payingMonth by remember { mutableStateOf<MonthlyDue?>(null) }
    var recordAnyMonth by rememberSaveable { mutableStateOf(false) }
    var deleting by remember { mutableStateOf<Payment?>(null) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(student?.name ?: "") },
                navigationIcon = {
                    IconButton(onClick = onBack) { Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back") }
                },
                actions = {
                    if (student != null) {
                        IconButton(onClick = onEdit) { Icon(Icons.Filled.Edit, contentDescription = "Edit student") }
                    }
                },
            )
        },
    ) { padding ->
        if (student == null) {
            if (state.loaded) {
                Text("This student no longer exists.", Modifier.padding(padding).padding(16.dp))
            }
            return@Scaffold
        }
        LazyColumn(
            Modifier.fillMaxSize().padding(padding),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            item { ProfileCard(student, onToggleActive = { viewModel.setActive(!student.active) }) }

            item {
                Text("Pending fees", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold)
            }
            if (state.outstanding.isEmpty()) {
                item { Text("All paid up. 🎉", color = MaterialTheme.colorScheme.onSurfaceVariant) }
            }
            items(state.outstanding, key = { "due-${it.month}" }) { due ->
                DueCard(
                    due = due,
                    canRequest = state.profile != null,
                    onRequest = { request = viewModel.feeRequest(due) },
                    onMarkPaid = { payingMonth = due },
                )
            }
            item {
                TextButton(onClick = { recordAnyMonth = true }) { Text("Record an advance or other payment") }
            }

            item {
                Text("Payment history", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold)
            }
            if (state.payments.isEmpty()) {
                item { Text("No payments recorded yet.", color = MaterialTheme.colorScheme.onSurfaceVariant) }
            }
            items(state.payments, key = { "pay-${it.id}" }) { payment ->
                PaymentRow(payment, onDelete = { deleting = payment })
                HorizontalDivider()
            }
        }
    }

    request?.let { req -> RequestDialog(req, phone = student?.guardianPhone.orEmpty(), onDismiss = { request = null }) }

    payingMonth?.let { due ->
        RecordPaymentDialog(
            initialMonth = due.month,
            initialAmountPaise = due.balancePaise,
            maxMonth = state.currentMonth.plusMonths(12),
            onDismiss = { payingMonth = null },
            onConfirm = { month, amount, method, utr ->
                viewModel.recordPayment(month, amount, method, utr)
                payingMonth = null
            },
        )
    }
    if (recordAnyMonth && student != null) {
        RecordPaymentDialog(
            initialMonth = state.currentMonth.plusMonths(1),
            initialAmountPaise = student.monthlyFeePaise,
            maxMonth = state.currentMonth.plusMonths(12),
            onDismiss = { recordAnyMonth = false },
            onConfirm = { month, amount, method, utr ->
                viewModel.recordPayment(month, amount, method, utr)
                recordAnyMonth = false
            },
        )
    }
    deleting?.let { payment ->
        AlertDialog(
            onDismissRequest = { deleting = null },
            title = { Text("Delete this payment?") },
            text = { Text("${Money.format(payment.amountPaise)} for ${FeeRequests.monthLabel(payment.month)} will be removed and the month will show as unpaid again.") },
            confirmButton = {
                TextButton(onClick = {
                    viewModel.deletePayment(payment.id)
                    deleting = null
                }) { Text("Delete") }
            },
            dismissButton = { TextButton(onClick = { deleting = null }) { Text("Cancel") } },
        )
    }
}

@Composable
private fun ProfileCard(student: Student, onToggleActive: () -> Unit) {
    Card(Modifier.fillMaxWidth()) {
        Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
            if (student.batch.isNotBlank()) Text(student.batch, style = MaterialTheme.typography.bodyMedium)
            Text(
                "${Money.format(student.monthlyFeePaise)} per month, due on day ${student.dueDay}",
                style = MaterialTheme.typography.bodyLarge,
            )
            Text(
                "Billed since ${FeeRequests.monthLabel(student.startMonth)}",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
            if (student.guardianPhone.isNotBlank()) {
                Text("Parent: +91 ${student.guardianPhone}", style = MaterialTheme.typography.bodySmall)
            }
            TextButton(onClick = onToggleActive, contentPadding = PaddingValues(0.dp)) {
                Text(if (student.active) "Archive student (stops billing reminders)" else "Restore student")
            }
        }
    }
}

@Composable
private fun DueCard(due: MonthlyDue, canRequest: Boolean, onRequest: () -> Unit, onMarkPaid: () -> Unit) {
    Card(Modifier.fillMaxWidth()) {
        Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Row(
                Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Column {
                    Text(FeeRequests.monthLabel(due.month), style = MaterialTheme.typography.titleMedium)
                    Text(
                        "Due ${due.dueDate.format(DateTimeFormatter.ofPattern("d MMM", Locale.ENGLISH))}" +
                            if (due.paidPaise > 0) " · ${Money.format(due.paidPaise)} received" else "",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                }
                Column(horizontalAlignment = Alignment.End) {
                    StatusPill(due.status)
                    Text(Money.format(due.balancePaise), style = MaterialTheme.typography.titleSmall)
                }
            }
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                Button(onClick = onRequest, enabled = canRequest) { Text("Request via UPI") }
                OutlinedButton(onClick = onMarkPaid) { Text("Mark paid") }
            }
        }
    }
}

@Composable
private fun PaymentRow(payment: Payment, onDelete: () -> Unit) {
    Row(
        Modifier.fillMaxWidth().padding(vertical = 6.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Column(Modifier.weight(1f)) {
            Text(
                "${Money.format(payment.amountPaise)} · ${FeeRequests.monthLabel(payment.month)}",
                style = MaterialTheme.typography.bodyLarge,
            )
            val detail = buildString {
                append(if (payment.method == PaymentMethod.UPI) "UPI" else "Cash")
                payment.utr?.let { append(" · UTR $it") }
                append(" · ")
                append(payment.paidAt.atZone(ZoneId.systemDefault()).format(PAID_AT))
            }
            Text(detail, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
        }
        IconButton(onClick = onDelete) { Icon(Icons.Filled.Delete, contentDescription = "Delete payment") }
    }
}

@Composable
private fun RequestDialog(request: FeeRequest, phone: String, onDismiss: () -> Unit) {
    val context = LocalContext.current
    val clipboard = LocalClipboardManager.current
    val qr = remember(request.uri) { QrCode.bitmap(request.uri) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("${FeeRequests.monthLabel(request.month)} · ${Money.format(request.amountPaise)}") },
        text = {
            Column(horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Image(
                    bitmap = qr.asImageBitmap(),
                    contentDescription = "UPI payment QR code",
                    modifier = Modifier.size(240.dp),
                )
                Text(
                    "Parents can scan this with any UPI app. The amount and reference ${request.reference} are filled in for them.",
                    style = MaterialTheme.typography.bodySmall,
                )
                TextButton(onClick = { clipboard.setText(AnnotatedString(request.message)) }) { Text("Copy message") }
                if (phone.isNotBlank()) {
                    TextButton(onClick = {
                        val sms = Intent(Intent.ACTION_SENDTO, Uri.parse("smsto:+91$phone"))
                            .putExtra("sms_body", request.message)
                        context.startActivity(sms)
                    }) { Text("Send as SMS to parent") }
                }
            }
        },
        confirmButton = {
            Button(onClick = { Share.feeRequest(context, qr, request.message) }) { Text("Share QR") }
        },
        dismissButton = { TextButton(onClick = onDismiss) { Text("Close") } },
    )
}

@Composable
private fun RecordPaymentDialog(
    initialMonth: YearMonth,
    initialAmountPaise: Long,
    maxMonth: YearMonth,
    onDismiss: () -> Unit,
    onConfirm: (YearMonth, Long, PaymentMethod, String?) -> Unit,
) {
    var month by remember { mutableStateOf(initialMonth) }
    var amount by remember { mutableStateOf(Money.toInput(initialAmountPaise)) }
    var method by remember { mutableStateOf(PaymentMethod.UPI) }
    var utr by remember { mutableStateOf("") }
    val parsed = Money.parseToPaise(amount)
    val valid = parsed != null && parsed > 0

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Record payment") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                dev.guruprasath.feeledger.ui.components.MonthStepper(month, onChange = { month = it }, max = maxMonth)
                OutlinedTextField(
                    value = amount,
                    onValueChange = { amount = it },
                    label = { Text("Amount (₹)") },
                    isError = !valid,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                    singleLine = true,
                )
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    PaymentMethod.entries.forEach { option ->
                        FilterChip(
                            selected = method == option,
                            onClick = { method = option },
                            label = { Text(if (option == PaymentMethod.UPI) "UPI" else "Cash") },
                        )
                    }
                }
                if (method == PaymentMethod.UPI) {
                    OutlinedTextField(
                        value = utr,
                        onValueChange = { utr = it.filter(Char::isLetterOrDigit).take(22) },
                        label = { Text("UTR / transaction ID (optional)") },
                        supportingText = { Text("12-digit number from the parent's UPI receipt") },
                        singleLine = true,
                    )
                }
            }
        },
        confirmButton = {
            Button(
                onClick = { onConfirm(month, requireNotNull(parsed), method, utr.takeIf { method == PaymentMethod.UPI }) },
                enabled = valid,
            ) { Text("Save") }
        },
        dismissButton = { TextButton(onClick = onDismiss) { Text("Cancel") } },
    )
}
