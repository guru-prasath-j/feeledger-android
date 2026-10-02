package dev.guruprasath.feeledger.domain

import java.math.BigDecimal

/** CSV export of every payment, for tax filing or sharing with an accountant. */
object LedgerCsv {
    const val HEADER = "student,batch,month,amount_inr,method,utr,recorded_at"

    fun build(students: List<Student>, payments: List<Payment>): String {
        val byId = students.associateBy { it.id }
        val rows = payments.sortedWith(
            compareBy<Payment>({ it.month }, { byId[it.studentId]?.name?.lowercase() ?: "" }, { it.paidAt }),
        )
        return buildString {
            append(HEADER).append('\n')
            for (payment in rows) {
                val student = byId[payment.studentId]
                val cells = listOf(
                    student?.name ?: "Student #${payment.studentId}",
                    student?.batch.orEmpty(),
                    payment.month.toString(),
                    BigDecimal.valueOf(payment.amountPaise, 2).toPlainString(),
                    payment.method.name,
                    payment.utr.orEmpty(),
                    payment.paidAt.toString(),
                )
                append(cells.joinToString(",") { escape(it) }).append('\n')
            }
        }
    }

    /** Quotes cells that need it and neutralises spreadsheet formula injection. */
    fun escape(value: String): String {
        val safe = if (value.isNotEmpty() && value[0] in "=+-@\t\r") "'$value" else value
        val needsQuotes = safe.any { it == ',' || it == '"' || it == '\n' || it == '\r' }
        return if (needsQuotes) "\"" + safe.replace("\"", "\"\"") + "\"" else safe
    }
}
