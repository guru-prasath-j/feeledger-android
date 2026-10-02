package dev.guruprasath.feeledger.domain

import java.time.YearMonth
import java.time.format.DateTimeFormatter
import java.util.Locale

data class FeeRequest(
    val uri: String,
    val message: String,
    val amountPaise: Long,
    val month: YearMonth,
    val reference: String,
)

/**
 * Turns an unpaid month into something a parent can act on.
 *
 * WhatsApp and SMS do not make `upi://` links tappable, so the request is sent as a QR image
 * plus a text message that repeats the UPI ID and amount for parents paying from the same phone.
 */
object FeeRequests {
    private val MONTH = DateTimeFormatter.ofPattern("MMM yyyy", Locale.ENGLISH)

    fun monthLabel(month: YearMonth): String = month.format(MONTH)

    fun reference(studentId: Long, month: YearMonth): String = "FL${studentId}M${MonthKey.of(month)}"

    fun build(profile: TutorProfile, student: Student, due: MonthlyDue): FeeRequest {
        val label = monthLabel(due.month)
        val firstName = student.name.trim().substringBefore(' ')
        val ref = reference(student.id, due.month)
        val uri = UpiUri.build(
            UpiPaymentRequest(
                payeeVpa = profile.vpa,
                payeeName = profile.name,
                amountPaise = due.balancePaise,
                note = "Fee $label $firstName",
                transactionRef = ref,
            ),
        )
        val partNote = if (due.paidPaise > 0) " (balance after ${Money.format(due.paidPaise)} received)" else ""
        val message = buildString {
            append("Hello! ${student.name}'s fee for $label is ${Money.format(due.balancePaise)}$partNote.\n\n")
            append("Scan the attached QR with any UPI app, or pay to UPI ID ${profile.vpa} (${profile.name}).\n")
            append("Reference: $ref\n\n")
            append("Please reply with the UTR / transaction ID once paid. Thank you!")
        }
        return FeeRequest(uri, message, due.balancePaise, due.month, ref)
    }
}
