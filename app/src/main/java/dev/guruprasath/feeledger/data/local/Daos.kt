package dev.guruprasath.feeledger.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface StudentDao {
    @Query("SELECT * FROM students ORDER BY name COLLATE NOCASE ASC")
    fun observeAll(): Flow<List<StudentEntity>>

    @Query("SELECT * FROM students WHERE id = :id")
    fun observe(id: Long): Flow<StudentEntity?>

    @Query("SELECT * FROM students")
    suspend fun all(): List<StudentEntity>

    @Query("SELECT * FROM students WHERE id = :id")
    suspend fun get(id: Long): StudentEntity?

    @Insert
    suspend fun insert(student: StudentEntity): Long

    @Update
    suspend fun update(student: StudentEntity)

    @Query("UPDATE students SET active = :active WHERE id = :id")
    suspend fun setActive(id: Long, active: Boolean)
}

@Dao
interface PaymentDao {
    @Query("SELECT * FROM payments ORDER BY paidAt DESC")
    fun observeAll(): Flow<List<PaymentEntity>>

    @Query("SELECT * FROM payments WHERE studentId = :studentId ORDER BY monthKey DESC, paidAt DESC")
    fun observeForStudent(studentId: Long): Flow<List<PaymentEntity>>

    @Query("SELECT * FROM payments")
    suspend fun all(): List<PaymentEntity>

    @Insert
    suspend fun insert(payment: PaymentEntity): Long

    @Query("DELETE FROM payments WHERE id = :id")
    suspend fun delete(id: Long)
}
