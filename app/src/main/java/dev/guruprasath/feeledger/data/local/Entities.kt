package dev.guruprasath.feeledger.data.local

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(tableName = "students")
data class StudentEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val name: String,
    val batch: String,
    /** Guardian's mobile, AES-GCM encrypted with the Keystore key. Empty when not given. */
    val guardianPhoneEnc: String,
    val monthlyFeePaise: Long,
    val dueDay: Int,
    /** yyyymm, see MonthKey. */
    val startMonth: Int,
    val active: Boolean = true,
    val createdAt: Long,
)

@Entity(
    tableName = "payments",
    foreignKeys = [
        ForeignKey(
            entity = StudentEntity::class,
            parentColumns = ["id"],
            childColumns = ["studentId"],
            onDelete = ForeignKey.CASCADE,
        ),
    ],
    indices = [Index("studentId"), Index("monthKey")],
)
data class PaymentEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val studentId: Long,
    val monthKey: Int,
    val amountPaise: Long,
    val method: String,
    /** UPI transaction reference (UTR), encrypted. Null for cash. */
    val utrEnc: String?,
    val paidAt: Long,
)
