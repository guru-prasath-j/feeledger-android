package dev.guruprasath.feeledger.domain

import java.time.LocalDate
import java.time.YearMonth

/**
 * Pure fee arithmetic: which months are billable, what is still owed, and how late it is.
 * Kept free of Android types so it is fully unit-tested on the JVM.
 */
object FeeCalculator {

    /** Due date inside [month]; a due day of 31 falls on the last day of shorter months. */
    fun dueDate(month: YearMonth, dueDay: Int): LocalDate =
        month.atDay(dueDay.coerceIn(1, month.lengthOfMonth()))

    fun status(feePaise: Long, paidPaise: Long, dueDate: LocalDate, today: LocalDate): FeeStatus = when {
        paidPaise >= feePaise -> FeeStatus.PAID
        today.isAfter(dueDate) -> FeeStatus.OVERDUE
        paidPaise > 0 -> FeeStatus.PARTIAL
        today.isEqual(dueDate) -> FeeStatus.DUE_TODAY
        else -> FeeStatus.UPCOMING
    }

    /** Every month from the student's start month up to and including the current month. */
    fun billableMonths(student: Student, today: LocalDate): List<YearMonth> {
        val current = YearMonth.from(today)
        if (student.startMonth.isAfter(current)) return emptyList()
        return generateSequence(student.startMonth) { it.plusMonths(1) }
            .takeWhile { !it.isAfter(current) }
            .toList()
    }

    fun paidByMonth(studentId: Long, payments: List<Payment>): Map<YearMonth, Long> =
        payments.asSequence()
            .filter { it.studentId == studentId }
            .groupBy { it.month }
            .mapValues { (_, list) -> list.sumOf { it.amountPaise } }

    fun dues(student: Student, payments: List<Payment>, today: LocalDate): List<MonthlyDue> {
        val paid = paidByMonth(student.id, payments)
        return billableMonths(student, today).map { month ->
            val paidPaise = paid[month] ?: 0L
            val due = dueDate(month, student.dueDay)
            MonthlyDue(
                studentId = student.id,
                month = month,
                feePaise = student.monthlyFeePaise,
                paidPaise = paidPaise,
                dueDate = due,
                status = status(student.monthlyFeePaise, paidPaise, due, today),
            )
        }
    }

    /** Unpaid or part-paid months, oldest first. */
    fun outstanding(student: Student, payments: List<Payment>, today: LocalDate): List<MonthlyDue> =
        dues(student, payments, today).filter { it.balancePaise > 0 }

    /** One status per student for the list: any overdue month wins, else the current month's status. */
    fun rowStatus(student: Student, payments: List<Payment>, today: LocalDate): FeeStatus {
        val all = dues(student, payments, today)
        if (all.isEmpty()) return FeeStatus.UPCOMING
        if (all.any { it.status == FeeStatus.OVERDUE }) return FeeStatus.OVERDUE
        return all.last().status
    }

    fun summarize(students: List<Student>, payments: List<Payment>, today: LocalDate): LedgerSummary {
        val current = YearMonth.from(today)
        val active = students.filter { it.active }
        val activeIds = active.mapTo(HashSet()) { it.id }

        val expected = active.filter { !it.startMonth.isAfter(current) }.sumOf { it.monthlyFeePaise }
        val collected = payments.filter { it.month == current && it.studentId in activeIds }
            .sumOf { it.amountPaise }

        var outstanding = 0L
        var dueToday = 0
        var overdue = 0
        for (student in active) {
            val open = outstanding(student, payments, today)
            outstanding += open.sumOf { it.balancePaise }
            when {
                open.any { it.status == FeeStatus.OVERDUE } -> overdue++
                open.any { it.status == FeeStatus.DUE_TODAY } -> dueToday++
            }
        }
        return LedgerSummary(expected, collected, outstanding, dueToday, overdue)
    }
}
