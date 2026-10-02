package dev.guruprasath.feeledger.security

import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import javax.crypto.KeyGenerator

class AesGcmFieldCipherTest {
    private val key = KeyGenerator.getInstance("AES").apply { init(256) }.generateKey()
    private val cipher = AesGcmFieldCipher { key }

    @Test
    fun `round trips unicode text`() {
        val plain = "9876543210 · ₹ · மீனா"
        assertEquals(plain, cipher.decrypt(cipher.encrypt(plain)))
    }

    @Test
    fun `same input encrypts differently each time`() {
        assertNotEquals(cipher.encrypt("9876543210"), cipher.encrypt("9876543210"))
    }

    @Test
    fun `ciphertext does not contain the plaintext`() {
        val token = cipher.encrypt("9876543210")
        assertTrue(!java.util.Base64.getDecoder().decode(token).toString(Charsets.ISO_8859_1).contains("9876543210"))
    }

    @Test(expected = Exception::class)
    fun `tampered token fails authentication`() {
        val bytes = java.util.Base64.getDecoder().decode(cipher.encrypt("9876543210"))
        bytes[bytes.size - 1] = (bytes[bytes.size - 1].toInt() xor 1).toByte()
        cipher.decrypt(java.util.Base64.getEncoder().encodeToString(bytes))
    }

    @Test(expected = Exception::class)
    fun `a different key cannot decrypt`() {
        val other = AesGcmFieldCipher { KeyGenerator.getInstance("AES").apply { init(256) }.generateKey() }
        other.decrypt(cipher.encrypt("secret"))
    }
}
