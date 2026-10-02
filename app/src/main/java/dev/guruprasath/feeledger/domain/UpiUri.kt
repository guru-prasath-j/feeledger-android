package dev.guruprasath.feeledger.domain

import java.math.BigDecimal
import java.net.URLEncoder

/** UPI virtual payment address, e.g. "asha.tutor@okicici". */
object Vpa {
    private val PATTERN = Regex("^[a-z0-9._-]{2,256}@[a-z][a-z0-9.-]{1,63}$")

    fun normalize(vpa: String): String = vpa.trim().lowercase()

    fun isValid(vpa: String): Boolean = PATTERN.matches(normalize(vpa))
}

data class UpiPaymentRequest(
    val payeeVpa: String,
    val payeeName: String,
    val amountPaise: Long,
    val note: String,
    val transactionRef: String,
)

/**
 * Builds a payee-initiated `upi://pay` link following the NPCI UPI deep-linking spec.
 * Any UPI app (GPay, PhonePe, Paytm, BHIM...) can scan it as a QR with the amount pre-filled.
 */
object UpiUri {
    /** Several UPI apps truncate or reject longer transaction notes. */
    const val MAX_NOTE_LENGTH = 50
    private const val MAX_NAME_LENGTH = 50

    fun build(request: UpiPaymentRequest): String {
        require(Vpa.isValid(request.payeeVpa)) { "Invalid UPI ID: ${request.payeeVpa}" }
        require(request.amountPaise > 0) { "Amount must be positive" }
        require(request.transactionRef.matches(Regex("^[A-Za-z0-9]{1,35}$"))) { "Reference must be 1-35 alphanumerics" }

        val params = linkedMapOf(
            "pa" to Vpa.normalize(request.payeeVpa),
            "pn" to request.payeeName.trim().take(MAX_NAME_LENGTH),
            "am" to formatAmount(request.amountPaise),
            "cu" to "INR",
            "tn" to request.note.trim().take(MAX_NOTE_LENGTH),
            "tr" to request.transactionRef,
        )
        return "upi://pay?" + params.entries.joinToString("&") { (key, value) -> "$key=${encode(value)}" }
    }

    /** 150050 paise -> "1500.50". UPI expects a plain decimal with two places. */
    fun formatAmount(paise: Long): String = BigDecimal.valueOf(paise, 2).toPlainString()

    private fun encode(value: String): String =
        URLEncoder.encode(value, "UTF-8")
            .replace("+", "%20")
            .replace("%40", "@")
}
