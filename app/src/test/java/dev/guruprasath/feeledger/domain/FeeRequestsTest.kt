package dev.guruprasath.feeledger.domain

import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import java.time.LocalDate
import java.time.YearMonth

class FeeRequestsTest {
    private val profile = TutorProfile("Meena Iyer", "meena.tuition@okaxis")

    @Test
    fun `request carries balance, month note and stable reference`() {
        val oct = YearMonth.of(2026, 10)
        val due = MonthlyDue(7, oct, 1_500_00, 500_00, LocalDate.of(2026, 10, 5), FeeStatus.PARTIAL)
        val req = FeeRequests.build(profile, student(id = 7, name = "Ravi Kumar"), due)

        assertEquals(1_000_00L, req.amountPaise)
        assertEquals("FL7M202610", req.reference)
        assertTrue(req.uri.contains("am=1000.00"))
        assertTrue(req.uri.contains("tn=Fee%20Oct%202026%20Ravi"))
        assertTrue(req.message.contains("₹1,000"))
        assertTrue(req.message.contains("meena.tuition@okaxis"))
        assertTrue(req.message.contains("balance after ₹500 received"))
    }
}
