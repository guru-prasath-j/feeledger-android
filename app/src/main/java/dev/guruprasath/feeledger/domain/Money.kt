package dev.guruprasath.feeledger.domain

import java.math.BigDecimal
import kotlin.math.abs

/** Money is kept as Long paise everywhere; this formats and parses it the Indian way. */
object Money {

    /** 12345600 -> "₹1,23,456"; 150050 -> "₹1,500.50". */
    fun format(paise: Long): String {
        val sign = if (paise < 0) "-" else ""
        val absolute = abs(paise)
        val rupees = absolute / 100
        val fraction = absolute % 100
        val decimals = if (fraction == 0L) "" else "." + fraction.toString().padStart(2, '0')
        return "$sign₹${groupIndian(rupees.toString())}$decimals"
    }

    /** Indian digit grouping: last three digits, then pairs (1,23,45,678). */
    fun groupIndian(digits: String): String {
        if (digits.length <= 3) return digits
        val lastThree = digits.takeLast(3)
        var rest = digits.dropLast(3)
        val parts = ArrayDeque<String>()
        while (rest.length > 2) {
            parts.addFirst(rest.takeLast(2))
            rest = rest.dropLast(2)
        }
        if (rest.isNotEmpty()) parts.addFirst(rest)
        return parts.joinToString(",") + "," + lastThree
    }

    /** "1,500.5" -> 150050. Returns null for blanks, negatives, or more than two decimals. */
    fun parseToPaise(input: String): Long? {
        val cleaned = input.trim().removePrefix("₹").replace(",", "").trim()
        if (cleaned.isEmpty()) return null
        val value = cleaned.toBigDecimalOrNull() ?: return null
        if (value.signum() < 0 || value.stripTrailingZeros().scale() > 2) return null
        return runCatching { value.movePointRight(2).setScale(0).longValueExact() }.getOrNull()
    }

    /** Value to pre-fill an amount field with. */
    fun toInput(paise: Long): String =
        if (paise % 100 == 0L) (paise / 100).toString() else BigDecimal.valueOf(paise, 2).toPlainString()
}
