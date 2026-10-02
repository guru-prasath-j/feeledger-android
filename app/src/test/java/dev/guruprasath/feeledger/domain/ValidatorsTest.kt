package dev.guruprasath.feeledger.domain

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

class ValidatorsTest {
    @Test
    fun `normalizes indian mobile numbers`() {
        assertEquals("9876543210", Validators.normalizePhone("+91 98765-43210"))
        assertEquals("9876543210", Validators.normalizePhone("09876543210"))
        assertTrue(Validators.isValidIndianMobile("98765 43210"))
        assertFalse(Validators.isValidIndianMobile("1234567890"))
        assertFalse(Validators.isValidIndianMobile("98765"))
    }

    @Test
    fun `valid student has no errors`() {
        assertTrue(Validators.validateStudent("Ravi", "", "1500", "5").isEmpty())
    }

    @Test
    fun `reports each invalid field`() {
        val errors = Validators.validateStudent(" ", "12345", "0", "32")
        assertEquals(setOf(StudentField.NAME, StudentField.PHONE, StudentField.FEE, StudentField.DUE_DAY), errors.keys)
    }

    @Test
    fun `profile validation`() {
        assertNull(Validators.validateProfile("Meena", "meena@okaxis"))
        assertTrue(Validators.validateProfile("", "meena@okaxis") != null)
        assertTrue(Validators.validateProfile("Meena", "meena") != null)
    }
}
