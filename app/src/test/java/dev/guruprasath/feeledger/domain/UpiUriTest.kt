package dev.guruprasath.feeledger.domain

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test
import java.net.URLDecoder

class UpiUriTest {

    private fun params(uri: String): Map<String, String> =
        uri.substringAfter("?").split("&").associate { pair ->
            val (k, v) = pair.split("=", limit = 2)
            k to URLDecoder.decode(v, "UTF-8")
        }

    @Test
    fun `builds a spec-compliant upi pay link`() {
        val uri = UpiUri.build(
            UpiPaymentRequest("Asha.Tutor@OKICICI", "Asha Rao", 1_500_50, "Fee Oct 2026 Ravi", "FL7M202610"),
        )
        assertTrue(uri.startsWith("upi://pay?pa=asha.tutor@okicici&"))
        val p = params(uri)
        assertEquals("asha.tutor@okicici", p["pa"])
        assertEquals("Asha Rao", p["pn"])
        assertEquals("1500.50", p["am"])
        assertEquals("INR", p["cu"])
        assertEquals("Fee Oct 2026 Ravi", p["tn"])
        assertEquals("FL7M202610", p["tr"])
    }

    @Test
    fun `spaces are percent-encoded, not plus signs`() {
        val uri = UpiUri.build(UpiPaymentRequest("a.b@ybl", "Asha Rao", 100, "Fee", "R1"))
        assertTrue(uri.contains("pn=Asha%20Rao"))
        assertFalse(uri.contains("+"))
    }

    @Test
    fun `long notes are trimmed to what UPI apps accept`() {
        val uri = UpiUri.build(UpiPaymentRequest("a.b@ybl", "A", 100, "x".repeat(80), "R1"))
        assertEquals(UpiUri.MAX_NOTE_LENGTH, params(uri).getValue("tn").length)
    }

    @Test(expected = IllegalArgumentException::class)
    fun `rejects zero amount`() {
        UpiUri.build(UpiPaymentRequest("a.b@ybl", "A", 0, "n", "R1"))
    }

    @Test(expected = IllegalArgumentException::class)
    fun `rejects invalid vpa`() {
        UpiUri.build(UpiPaymentRequest("not-a-vpa", "A", 100, "n", "R1"))
    }

    @Test
    fun `vpa validation`() {
        assertTrue(Vpa.isValid("9876543210@paytm"))
        assertTrue(Vpa.isValid("asha.rao-1@okhdfcbank"))
        assertTrue(Vpa.isValid("  Asha@YBL "))
        assertFalse(Vpa.isValid("asha"))
        assertFalse(Vpa.isValid("@ybl"))
        assertFalse(Vpa.isValid("asha@"))
        assertFalse(Vpa.isValid("asha rao@ybl"))
        assertFalse(Vpa.isValid("asha@1bank"))
    }

    @Test
    fun `amount formatting always has two decimals`() {
        assertEquals("1500.00", UpiUri.formatAmount(1_500_00))
        assertEquals("0.01", UpiUri.formatAmount(1))
    }
}
