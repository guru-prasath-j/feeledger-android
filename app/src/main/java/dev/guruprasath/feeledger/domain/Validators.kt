package dev.guruprasath.feeledger.domain

enum class StudentField { NAME, PHONE, FEE, DUE_DAY }

object Validators {
    private val INDIAN_MOBILE = Regex("^[6-9][0-9]{9}$")
    const val MAX_FEE_PAISE = 10_00_000_00L // ₹10,00,000 per month is far beyond any tuition fee

    /** "+91 98765-43210" -> "9876543210". */
    fun normalizePhone(raw: String): String {
        var digits = raw.filter { it.isDigit() }
        if (digits.length == 12 && digits.startsWith("91")) digits = digits.drop(2)
        if (digits.length == 11 && digits.startsWith("0")) digits = digits.drop(1)
        return digits
    }

    fun isValidIndianMobile(raw: String): Boolean = INDIAN_MOBILE.matches(normalizePhone(raw))

    fun validateStudent(name: String, phone: String, fee: String, dueDay: String): Map<StudentField, String> {
        val errors = mutableMapOf<StudentField, String>()
        val trimmed = name.trim()
        if (trimmed.isEmpty()) errors[StudentField.NAME] = "Enter the student's name"
        else if (trimmed.length > 60) errors[StudentField.NAME] = "Keep the name under 60 characters"

        if (phone.isNotBlank() && !isValidIndianMobile(phone)) {
            errors[StudentField.PHONE] = "Enter a 10-digit Indian mobile number"
        }

        val feePaise = Money.parseToPaise(fee)
        when {
            feePaise == null -> errors[StudentField.FEE] = "Enter the monthly fee, e.g. 1500"
            feePaise <= 0 -> errors[StudentField.FEE] = "Fee must be more than zero"
            feePaise > MAX_FEE_PAISE -> errors[StudentField.FEE] = "That fee looks too large"
        }

        val day = dueDay.trim().toIntOrNull()
        if (day == null || day !in 1..31) errors[StudentField.DUE_DAY] = "Pick a day between 1 and 31"
        return errors
    }

    /** Returns an error message, or null when the tutor profile is valid. */
    fun validateProfile(name: String, vpa: String): String? = when {
        name.isBlank() -> "Enter the name parents will see on the payment screen"
        name.trim().length > 50 -> "Keep the name under 50 characters"
        !Vpa.isValid(vpa) -> "Enter a valid UPI ID, e.g. yourname@okicici"
        else -> null
    }
}
