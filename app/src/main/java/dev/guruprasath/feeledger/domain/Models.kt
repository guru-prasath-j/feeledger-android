package dev.guruprasath.feeledger.domain

import java.time.Instant
import java.time.LocalDate
import java.time.YearMonth

enum class PaymentMethod { UPI, CASH }

/** A student billed a fixed fee every month, due on [dueDay]. Amounts are in paise. */
data class Student(
    val id: Long,
    val name: String,
    val batch: String,
    val guardianPhone: String,
    val monthlyFeePaise: Long,
    val dueDay: Int,
    val startMonth: YearMonth,
    val active: Boolean,
)

/** A payment received for one billing month. A month can be paid in several parts. */
data class Payment(
    val id: Long,
    val studentId: Long,
    val month: YearMonth,
    val amountPaise: Long,
    val method: PaymentMethod,
    val utr: String?,
    val paidAt: Instant,
)

data class TutorProfile(val name: String, val vpa: String)

enum class FeeStatus(val priority: Int, val label: String) {
    OVERDUE(0, "Overdue"),
    DUE_TODAY(1, "Due today"),
    PARTIAL(2, "Part paid"),
    UPCOMING(3, "Upcoming"),
    PAID(4, "Paid"),
}

data class MonthlyDue(
    val studentId: Long,
    val month: YearMonth,
    val feePaise: Long,
    val paidPaise: Long,
    val dueDate: LocalDate,
    val status: FeeStatus,
) {
    val balancePaise: Long get() = (feePaise - paidPaise).coerceAtLeast(0)
}

data class LedgerSummary(
    val expectedThisMonthPaise: Long,
    val collectedThisMonthPaise: Long,
    val outstandingPaise: Long,
    val dueTodayCount: Int,
    val overdueCount: Int,
) {
    companion object {
        val EMPTY = LedgerSummary(0, 0, 0, 0, 0)
    }
}

/** Billing months are stored as yyyymm integers (e.g. 202610) so they sort and index cheaply. */
object MonthKey {
    fun of(month: YearMonth): Int = month.year * 100 + month.monthValue
    fun toYearMonth(key: Int): YearMonth = YearMonth.of(key / 100, key % 100)
}
