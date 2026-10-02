package dev.guruprasath.feeledger.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowLeft
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import dev.guruprasath.feeledger.domain.FeeRequests
import dev.guruprasath.feeledger.domain.FeeStatus
import dev.guruprasath.feeledger.ui.theme.Amber
import dev.guruprasath.feeledger.ui.theme.Green
import dev.guruprasath.feeledger.ui.theme.Navy
import dev.guruprasath.feeledger.ui.theme.Red
import java.time.YearMonth

fun statusColor(status: FeeStatus): Color = when (status) {
    FeeStatus.PAID -> Green
    FeeStatus.OVERDUE -> Red
    FeeStatus.DUE_TODAY, FeeStatus.PARTIAL -> Amber
    FeeStatus.UPCOMING -> Navy
}

@Composable
fun StatusPill(status: FeeStatus, modifier: Modifier = Modifier) {
    val color = statusColor(status)
    Text(
        text = status.label,
        color = color,
        style = MaterialTheme.typography.labelMedium,
        modifier = modifier
            .background(color.copy(alpha = 0.12f), RoundedCornerShape(50))
            .padding(horizontal = 10.dp, vertical = 4.dp),
    )
}

@Composable
fun MonthStepper(
    month: YearMonth,
    onChange: (YearMonth) -> Unit,
    modifier: Modifier = Modifier,
    max: YearMonth? = null,
) {
    Row(modifier, verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(4.dp)) {
        IconButton(onClick = { onChange(month.minusMonths(1)) }) {
            Icon(Icons.AutoMirrored.Filled.KeyboardArrowLeft, contentDescription = "Previous month")
        }
        Text(FeeRequests.monthLabel(month), style = MaterialTheme.typography.titleMedium)
        IconButton(
            onClick = { onChange(month.plusMonths(1)) },
            enabled = max == null || month.isBefore(max),
        ) {
            Icon(Icons.AutoMirrored.Filled.KeyboardArrowRight, contentDescription = "Next month")
        }
    }
}
