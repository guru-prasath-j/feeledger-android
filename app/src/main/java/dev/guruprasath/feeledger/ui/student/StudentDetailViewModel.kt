package dev.guruprasath.feeledger.ui.student

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dev.guruprasath.feeledger.data.FeeRepository
import dev.guruprasath.feeledger.domain.FeeCalculator
import dev.guruprasath.feeledger.domain.FeeRequest
import dev.guruprasath.feeledger.domain.FeeRequests
import dev.guruprasath.feeledger.domain.MonthlyDue
import dev.guruprasath.feeledger.domain.Payment
import dev.guruprasath.feeledger.domain.PaymentMethod
import dev.guruprasath.feeledger.domain.Student
import dev.guruprasath.feeledger.domain.TutorProfile
import dev.guruprasath.feeledger.security.TutorProfileStore
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.time.Clock
import java.time.LocalDate
import java.time.YearMonth

data class StudentDetailState(
    val student: Student? = null,
    val outstanding: List<MonthlyDue> = emptyList(),
    val payments: List<Payment> = emptyList(),
    val profile: TutorProfile? = null,
    val currentMonth: YearMonth,
    val loaded: Boolean = false,
)

class StudentDetailViewModel(
    private val repository: FeeRepository,
    profileStore: TutorProfileStore,
    private val clock: Clock,
    private val studentId: Long,
) : ViewModel() {

    val state: StateFlow<StudentDetailState> =
        combine(repository.student(studentId), repository.paymentsFor(studentId), profileStore.profile) { student, payments, profile ->
            val today = LocalDate.now(clock)
            StudentDetailState(
                student = student,
                outstanding = student?.let { FeeCalculator.outstanding(it, payments, today) }.orEmpty(),
                payments = payments,
                profile = profile,
                currentMonth = YearMonth.from(today),
                loaded = true,
            )
        }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), StudentDetailState(currentMonth = YearMonth.now(clock)))

    fun feeRequest(due: MonthlyDue): FeeRequest? {
        val current = state.value
        val student = current.student ?: return null
        val profile = current.profile ?: return null
        return FeeRequests.build(profile, student, due)
    }

    fun recordPayment(month: YearMonth, amountPaise: Long, method: PaymentMethod, utr: String?) {
        viewModelScope.launch { repository.recordPayment(studentId, month, amountPaise, method, utr) }
    }

    fun deletePayment(paymentId: Long) {
        viewModelScope.launch { repository.deletePayment(paymentId) }
    }

    fun setActive(active: Boolean) {
        viewModelScope.launch { repository.setActive(studentId, active) }
    }
}
