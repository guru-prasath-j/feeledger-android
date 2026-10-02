package dev.guruprasath.feeledger.data

import dev.guruprasath.feeledger.data.local.AppDatabase
import dev.guruprasath.feeledger.data.local.PaymentEntity
import dev.guruprasath.feeledger.data.local.StudentEntity
import dev.guruprasath.feeledger.domain.MonthKey
import dev.guruprasath.feeledger.domain.Payment
import dev.guruprasath.feeledger.domain.PaymentMethod
import dev.guruprasath.feeledger.domain.Student
import dev.guruprasath.feeledger.domain.Validators
import dev.guruprasath.feeledger.security.FieldCipher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import java.time.Clock
import java.time.Instant
import java.time.YearMonth

data class StudentDraft(
    val name: String,
    val batch: String,
    val guardianPhone: String,
    val monthlyFeePaise: Long,
    val dueDay: Int,
    val startMonth: YearMonth,
)

/** Single source of truth. Encrypts sensitive columns on the way in and decrypts on the way out. */
class FeeRepository(
    db: AppDatabase,
    private val cipher: FieldCipher,
    private val clock: Clock,
) {
    private val studentDao = db.students()
    private val paymentDao = db.payments()

    val students: Flow<List<Student>> =
        studentDao.observeAll().map { list -> list.map { studentOf(it) } }.flowOn(Dispatchers.Default)

    val payments: Flow<List<Payment>> =
        paymentDao.observeAll().map { list -> list.map { paymentOf(it) } }.flowOn(Dispatchers.Default)

    fun student(id: Long): Flow<Student?> =
        studentDao.observe(id).map { it?.let { s -> studentOf(s) } }.flowOn(Dispatchers.Default)

    fun paymentsFor(studentId: Long): Flow<List<Payment>> =
        paymentDao.observeForStudent(studentId).map { list -> list.map { paymentOf(it) } }.flowOn(Dispatchers.Default)

    suspend fun snapshot(): Pair<List<Student>, List<Payment>> = withContext(Dispatchers.Default) {
        studentDao.all().map { studentOf(it) } to paymentDao.all().map { paymentOf(it) }
    }

    /** Inserts when [id] is null, otherwise updates. Returns the student id. */
    suspend fun saveStudent(draft: StudentDraft, id: Long?): Long = withContext(Dispatchers.Default) {
        val phone = Validators.normalizePhone(draft.guardianPhone)
        val phoneEnc = if (phone.isEmpty()) "" else cipher.encrypt(phone)
        if (id == null) {
            studentDao.insert(
                StudentEntity(
                    name = draft.name.trim(),
                    batch = draft.batch.trim(),
                    guardianPhoneEnc = phoneEnc,
                    monthlyFeePaise = draft.monthlyFeePaise,
                    dueDay = draft.dueDay,
                    startMonth = MonthKey.of(draft.startMonth),
                    createdAt = clock.millis(),
                ),
            )
        } else {
            val existing = requireNotNull(studentDao.get(id)) { "Student $id not found" }
            studentDao.update(
                existing.copy(
                    name = draft.name.trim(),
                    batch = draft.batch.trim(),
                    guardianPhoneEnc = phoneEnc,
                    monthlyFeePaise = draft.monthlyFeePaise,
                    dueDay = draft.dueDay,
                    startMonth = MonthKey.of(draft.startMonth),
                ),
            )
            id
        }
    }

    suspend fun recordPayment(
        studentId: Long,
        month: YearMonth,
        amountPaise: Long,
        method: PaymentMethod,
        utr: String?,
    ): Long = withContext(Dispatchers.Default) {
        require(amountPaise > 0) { "Amount must be positive" }
        val cleanUtr = utr?.trim()?.takeIf { it.isNotEmpty() }
        paymentDao.insert(
            PaymentEntity(
                studentId = studentId,
                monthKey = MonthKey.of(month),
                amountPaise = amountPaise,
                method = method.name,
                utrEnc = cleanUtr?.let(cipher::encrypt),
                paidAt = clock.millis(),
            ),
        )
    }

    suspend fun deletePayment(id: Long) = paymentDao.delete(id)

    suspend fun setActive(id: Long, active: Boolean) = studentDao.setActive(id, active)

    private fun studentOf(e: StudentEntity) = Student(
        id = e.id,
        name = e.name,
        batch = e.batch,
        guardianPhone = decryptOrEmpty(e.guardianPhoneEnc),
        monthlyFeePaise = e.monthlyFeePaise,
        dueDay = e.dueDay,
        startMonth = MonthKey.toYearMonth(e.startMonth),
        active = e.active,
    )

    private fun paymentOf(e: PaymentEntity) = Payment(
        id = e.id,
        studentId = e.studentId,
        month = MonthKey.toYearMonth(e.monthKey),
        amountPaise = e.amountPaise,
        method = runCatching { PaymentMethod.valueOf(e.method) }.getOrDefault(PaymentMethod.UPI),
        utr = e.utrEnc?.let { decryptOrEmpty(it) }?.takeIf { it.isNotEmpty() },
        paidAt = Instant.ofEpochMilli(e.paidAt),
    )

    private fun decryptOrEmpty(token: String): String =
        if (token.isEmpty()) "" else runCatching { cipher.decrypt(token) }.getOrDefault("")
}
