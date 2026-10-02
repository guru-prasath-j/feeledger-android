package dev.guruprasath.feeledger.domain

import org.junit.Assert.assertEquals
import org.junit.Test
import java.time.YearMonth

class LedgerCsvTest {
    @Test
    fun `exports one row per payment sorted by month`() {
        val students = listOf(student(id = 1, name = "Ravi, Jr."), student(id = 2, name = "Asha"))
        val payments = listOf(
            payment(studentId = 1, month = YearMonth.of(2026, 10), amountPaise = 1_500_00),
            payment(studentId = 2, month = YearMonth.of(2026, 9), amountPaise = 1_200_50),
        )
        val lines = LedgerCsv.build(students, payments).trim().lines()
        assertEquals(LedgerCsv.HEADER, lines[0])
        assertEquals("Asha,Class 8 Maths,2026-09,1200.50,UPI,412345678901,2026-09-01T10:00:00Z", lines[1])
        assertEquals("\"Ravi, Jr.\",Class 8 Maths,2026-10,1500.00,UPI,412345678901,2026-09-01T10:00:00Z", lines[2])
    }

    @Test
    fun `neutralises spreadsheet formulas and escapes quotes`() {
        assertEquals("'=HYPERLINK(1)", LedgerCsv.escape("=HYPERLINK(1)"))
        assertEquals("\"say \"\"hi\"\"\"", LedgerCsv.escape("say \"hi\""))
        assertEquals("plain", LedgerCsv.escape("plain"))
    }
}
