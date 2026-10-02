package dev.guruprasath.feeledger.domain

import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import java.time.LocalDate
import java.time.YearMonth

class FeeCalculatorTest {
    private val aug = YearMonth.of(2026, 8)
    private val sep = YearMonth.of(2026, 9)
    private val oct = YearMonth.of(2026, 10)

    @Test
    fun `due day 31 falls on the last day of shorter months`() {
        assertEquals(LocalDate.of(2026, 2, 28), FeeCalculator.dueDate(YearMonth.of(2026, 2), 31))
        assertEquals(LocalDate.of(2028, 2, 29), FeeCalculator.dueDate(YearMonth.of(2028, 2), 31))
        assertEquals(LocalDate.of(2026, 9, 30), FeeCalculator.dueDate(sep, 31))
    }

    @Test
    fun `status moves from upcoming to due today to overdue`() {
        val due = LocalDate.of(2026, 10, 5)
        assertEquals(FeeStatus.UPCOMING, FeeCalculator.status(100, 0, due, LocalDate.of(2026, 10, 4)))
        assertEquals(FeeStatus.DUE_TODAY, FeeCalculator.status(100, 0, due, due))
        assertEquals(FeeStatus.OVERDUE, FeeCalculator.status(100, 0, due, LocalDate.of(2026, 10, 6)))
        assertEquals(FeeStatus.PARTIAL, FeeCalculator.status(100, 40, due, LocalDate.of(2026, 10, 1)))
        assertEquals(FeeStatus.OVERDUE, FeeCalculator.status(100, 40, due, LocalDate.of(2026, 10, 9)))
        assertEquals(FeeStatus.PAID, FeeCalculator.status(100, 100, due, LocalDate.of(2026, 10, 9)))
    }

    @Test
    fun `bills every month from start to the current month`() {
        val months = FeeCalculator.billableMonths(student(start = aug), LocalDate.of(2026, 10, 2))
        assertEquals(listOf(aug, sep, oct), months)
    }

    @Test
    fun `student starting next month is not billed yet`() {
        val months = FeeCalculator.billableMonths(student(start = YearMonth.of(2026, 11)), LocalDate.of(2026, 10, 2))
        assertTrue(months.isEmpty())
        assertEquals(FeeStatus.UPCOMING, FeeCalculator.rowStatus(student(start = YearMonth.of(2026, 11)), emptyList(), LocalDate.of(2026, 10, 2)))
    }

    @Test
    fun `outstanding lists arrears oldest first and respects part payments`() {
        val s = student(start = aug)
        val payments = listOf(payment(month = aug), payment(month = sep, amountPaise = 500_00))
        val open = FeeCalculator.outstanding(s, payments, LocalDate.of(2026, 10, 2))
        assertEquals(listOf(sep, oct), open.map { it.month })
        assertEquals(1_000_00L, open[0].balancePaise)
        assertEquals(FeeStatus.OVERDUE, open[0].status)
        assertEquals(FeeStatus.UPCOMING, open[1].status)
    }

    @Test
    fun `payments for other students are ignored`() {
        val s = student(id = 1, start = oct)
        val open = FeeCalculator.outstanding(s, listOf(payment(studentId = 2, month = oct)), LocalDate.of(2026, 10, 2))
        assertEquals(1, open.size)
    }

    @Test
    fun `row status is overdue when any earlier month is unpaid`() {
        val s = student(start = sep)
        val status = FeeCalculator.rowStatus(s, listOf(payment(month = oct)), LocalDate.of(2026, 10, 2))
        assertEquals(FeeStatus.OVERDUE, status)
    }

    @Test
    fun `summary counts expected, collected, outstanding and late students`() {
        val today = LocalDate.of(2026, 10, 5)
        val students = listOf(
            student(id = 1, start = oct, dueDay = 5),               // due today, unpaid
            student(id = 2, start = sep, dueDay = 10),              // September overdue
            student(id = 3, start = oct, dueDay = 1, feePaise = 2_000_00), // paid
            student(id = 4, start = oct, active = false),           // archived, ignored
        )
        val payments = listOf(
            payment(studentId = 3, month = oct, amountPaise = 2_000_00),
            payment(studentId = 4, month = oct),
        )
        val summary = FeeCalculator.summarize(students, payments, today)
        assertEquals(5_000_00L, summary.expectedThisMonthPaise)
        assertEquals(2_000_00L, summary.collectedThisMonthPaise)
        assertEquals(1_500_00L + 3_000_00L, summary.outstandingPaise)
        assertEquals(1, summary.dueTodayCount)
        assertEquals(1, summary.overdueCount)
    }
}
