package dev.guruprasath.feeledger.domain

import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test

class MoneyTest {
    @Test
    fun `formats with indian digit grouping`() {
        assertEquals("₹0", Money.format(0))
        assertEquals("₹999", Money.format(999_00))
        assertEquals("₹1,500", Money.format(1_500_00))
        assertEquals("₹1,23,456", Money.format(1_23_456_00))
        assertEquals("₹12,34,56,789.05", Money.format(12_34_56_789_05))
        assertEquals("-₹50", Money.format(-50_00))
    }

    @Test
    fun `parses user input to paise`() {
        assertEquals(150000L, Money.parseToPaise("1500"))
        assertEquals(150050L, Money.parseToPaise("1,500.5"))
        assertEquals(150050L, Money.parseToPaise("₹ 1500.50"))
        assertEquals(150000L, Money.parseToPaise("1500.000"))
        assertNull(Money.parseToPaise(""))
        assertNull(Money.parseToPaise("abc"))
        assertNull(Money.parseToPaise("-10"))
        assertNull(Money.parseToPaise("10.005"))
    }

    @Test
    fun `input round trip`() {
        assertEquals("1500", Money.toInput(1_500_00))
        assertEquals("1500.50", Money.toInput(1_500_50))
    }
}
