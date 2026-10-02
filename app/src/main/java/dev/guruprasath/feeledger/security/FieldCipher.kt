package dev.guruprasath.feeledger.security

import java.nio.ByteBuffer
import java.util.Base64
import javax.crypto.Cipher
import javax.crypto.SecretKey
import javax.crypto.spec.GCMParameterSpec

/** Encrypts individual sensitive fields (phone numbers, UPI ID, UTRs) before they touch disk. */
interface FieldCipher {
    fun encrypt(plain: String): String
    fun decrypt(token: String): String
}

/**
 * AES-256-GCM with a fresh random IV per value.
 *
 * Token layout (Base64): [version:1][ivLength:1][iv][ciphertext + 16-byte tag].
 * The key comes from [keyProvider]; in the app it is a non-exportable Android Keystore key,
 * in unit tests a plain JCE key.
 */
class AesGcmFieldCipher(private val keyProvider: () -> SecretKey) : FieldCipher {

    override fun encrypt(plain: String): String {
        val cipher = Cipher.getInstance(TRANSFORMATION)
        cipher.init(Cipher.ENCRYPT_MODE, keyProvider())
        val iv = cipher.iv
        val sealed = cipher.doFinal(plain.toByteArray(Charsets.UTF_8))
        val packed = ByteBuffer.allocate(2 + iv.size + sealed.size)
            .put(VERSION)
            .put(iv.size.toByte())
            .put(iv)
            .put(sealed)
            .array()
        return Base64.getEncoder().encodeToString(packed)
    }

    override fun decrypt(token: String): String {
        val buffer = ByteBuffer.wrap(Base64.getDecoder().decode(token))
        val version = buffer.get()
        require(version == VERSION) { "Unsupported token version $version" }
        val ivLength = buffer.get().toInt()
        require(ivLength in 12..16) { "Corrupt token" }
        val iv = ByteArray(ivLength).also { buffer.get(it) }
        val sealed = ByteArray(buffer.remaining()).also { buffer.get(it) }
        val cipher = Cipher.getInstance(TRANSFORMATION)
        cipher.init(Cipher.DECRYPT_MODE, keyProvider(), GCMParameterSpec(TAG_BITS, iv))
        return String(cipher.doFinal(sealed), Charsets.UTF_8)
    }

    private companion object {
        const val TRANSFORMATION = "AES/GCM/NoPadding"
        const val VERSION: Byte = 1
        const val TAG_BITS = 128
    }
}
