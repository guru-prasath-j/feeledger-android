package dev.guruprasath.feeledger.domain

import java.time.Instant
import java.time.YearMonth

fun student(
    id: Long = 1,
    name: String = "Asha Rao",
    feePaise: Long = 1_500_00,
    dueDay: Int = 5,
    start: YearMonth = YearMonth.of(2026, 8),
    active: Boolean = true,
) = Student(id, name, "Class 8 Maths", "9876543210", feePaise, dueDay, start, active)

fun payment(
    studentId: Long = 1,
    month: YearMonth,
    amountPaise: Long = 1_500_00,
    id: Long = 0,
    method: PaymentMethod = PaymentMethod.UPI,
) = Payment(id, studentId, month, amountPaise, method, "412345678901", Instant.parse("2026-09-01T10:00:00Z"))
