package dev.guruprasath.feeledger.ui.home

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import dev.guruprasath.feeledger.domain.FeeRequests
import dev.guruprasath.feeledger.domain.LedgerSummary
import dev.guruprasath.feeledger.domain.Money
import dev.guruprasath.feeledger.ui.appViewModel
import dev.guruprasath.feeledger.ui.components.StatusPill
import dev.guruprasath.feeledger.ui.theme.Red
import java.time.YearMonth

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(onAddStudent: () -> Unit, onOpenStudent: (Long) -> Unit, onSettings: () -> Unit) {
    val viewModel = appViewModel { HomeViewModel(it.repository, it.clock) }
    val state by viewModel.state.collectAsStateWithLifecycle()

    RequestNotificationPermission()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("FeeLedger", fontWeight = FontWeight.SemiBold) },
                actions = {
                    IconButton(onClick = onSettings) { Icon(Icons.Filled.Settings, contentDescription = "Settings") }
                },
            )
        },
        floatingActionButton = {
            ExtendedFloatingActionButton(
                onClick = onAddStudent,
                icon = { Icon(Icons.Filled.Add, contentDescription = null) },
                text = { Text("Add student") },
            )
        },
    ) { padding ->
        LazyColumn(
            modifier = Modifier.fillMaxSize().padding(padding),
            contentPadding = PaddingValues(start = 16.dp, top = 8.dp, end = 16.dp, bottom = 96.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            item { SummaryCard(state.month, state.summary) }
            item {
                Row(
                    Modifier.horizontalScroll(rememberScrollState()),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                ) {
                    HomeFilter.entries.forEach { option ->
                        FilterChip(
                            selected = option == state.filter,
                            onClick = { viewModel.setFilter(option) },
                            label = { Text(option.label) },
                        )
                    }
                }
            }
            if (!state.loading && state.rows.isEmpty()) {
                item { EmptyState(state.filter, state.totalStudents) }
            }
            items(state.rows, key = { it.student.id }) { row ->
                StudentCard(row, onClick = { onOpenStudent(row.student.id) })
            }
        }
    }
}

@Composable
private fun SummaryCard(month: YearMonth, summary: LedgerSummary) {
    Card(
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer),
        modifier = Modifier.fillMaxWidth(),
    ) {
        Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
            Text(FeeRequests.monthLabel(month), style = MaterialTheme.typography.labelLarge)
            Text(
                "${Money.format(summary.collectedThisMonthPaise)} of ${Money.format(summary.expectedThisMonthPaise)} collected",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.SemiBold,
            )
            val ratio = if (summary.expectedThisMonthPaise == 0L) 0f
            else (summary.collectedThisMonthPaise.toFloat() / summary.expectedThisMonthPaise).coerceIn(0f, 1f)
            LinearProgressIndicator(progress = { ratio }, modifier = Modifier.fillMaxWidth())
            Spacer(Modifier.height(2.dp))
            Text(
                "Outstanding (all months): ${Money.format(summary.outstandingPaise)}",
                style = MaterialTheme.typography.bodyMedium,
            )
            if (summary.overdueCount + summary.dueTodayCount > 0) {
                Text(
                    "${summary.overdueCount} overdue · ${summary.dueTodayCount} due today",
                    color = if (summary.overdueCount > 0) Red else MaterialTheme.colorScheme.onPrimaryContainer,
                    style = MaterialTheme.typography.bodyMedium,
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun StudentCard(row: StudentRow, onClick: () -> Unit) {
    Card(onClick = onClick, modifier = Modifier.fillMaxWidth()) {
        Row(
            Modifier.padding(16.dp).fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween,
        ) {
            Column(Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(2.dp)) {
                Text(row.student.name, style = MaterialTheme.typography.titleMedium)
                val detail = buildList {
                    if (row.student.batch.isNotBlank()) add(row.student.batch)
                    add("${Money.format(row.student.monthlyFeePaise)}/month")
                    if (row.pendingMonths > 1) add("${row.pendingMonths} months pending")
                }.joinToString(" · ")
                Text(detail, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
            Column(horizontalAlignment = Alignment.End, verticalArrangement = Arrangement.spacedBy(4.dp)) {
                StatusPill(row.status)
                if (row.balancePaise > 0) {
                    Text(Money.format(row.balancePaise), style = MaterialTheme.typography.titleSmall)
                }
            }
        }
    }
}

@Composable
private fun EmptyState(filter: HomeFilter, totalStudents: Int) {
    val text = when {
        totalStudents == 0 && filter == HomeFilter.ALL ->
            "No students yet. Add your first student to start tracking monthly fees."
        filter == HomeFilter.OVERDUE -> "Nothing overdue. Nice."
        filter == HomeFilter.PENDING -> "Every fee is paid up."
        filter == HomeFilter.ARCHIVED -> "No archived students."
        else -> "No students match this filter."
    }
    Text(
        text,
        style = MaterialTheme.typography.bodyLarge,
        color = MaterialTheme.colorScheme.onSurfaceVariant,
        modifier = Modifier.padding(vertical = 32.dp),
    )
}

@Composable
private fun RequestNotificationPermission() {
    if (Build.VERSION.SDK_INT < 33) return
    val context = LocalContext.current
    val launcher = rememberLauncherForActivityResult(ActivityResultContracts.RequestPermission()) { }
    LaunchedEffect(Unit) {
        val granted = ContextCompat.checkSelfPermission(context, Manifest.permission.POST_NOTIFICATIONS) ==
            PackageManager.PERMISSION_GRANTED
        if (!granted) launcher.launch(Manifest.permission.POST_NOTIFICATIONS)
    }
}
