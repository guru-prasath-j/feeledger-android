package dev.guruprasath.feeledger.ui.student

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dev.guruprasath.feeledger.data.FeeRepository
import dev.guruprasath.feeledger.data.StudentDraft
import dev.guruprasath.feeledger.domain.Money
import dev.guruprasath.feeledger.domain.StudentField
import dev.guruprasath.feeledger.domain.Validators
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import java.time.Clock
import java.time.YearMonth

data class StudentForm(
    val name: String = "",
    val batch: String = "",
    val phone: String = "",
    val fee: String = "",
    val dueDay: String = "5",
    val startMonth: YearMonth,
    val errors: Map<StudentField, String> = emptyMap(),
    val saving: Boolean = false,
)

class StudentEditViewModel(
    private val repository: FeeRepository,
    clock: Clock,
    private val studentId: Long?,
) : ViewModel() {

    var form by mutableStateOf(StudentForm(startMonth = YearMonth.now(clock)))
        private set

    val isNew: Boolean get() = studentId == null

    init {
        if (studentId != null) {
            viewModelScope.launch {
                repository.student(studentId).first()?.let { s ->
                    form = form.copy(
                        name = s.name,
                        batch = s.batch,
                        phone = s.guardianPhone,
                        fee = Money.toInput(s.monthlyFeePaise),
                        dueDay = s.dueDay.toString(),
                        startMonth = s.startMonth,
                    )
                }
            }
        }
    }

    fun update(transform: (StudentForm) -> StudentForm) {
        form = transform(form).let { it.copy(errors = emptyMap()) }
    }

    fun save(onSaved: (Long) -> Unit) {
        val current = form
        val errors = Validators.validateStudent(current.name, current.phone, current.fee, current.dueDay)
        if (errors.isNotEmpty()) {
            form = current.copy(errors = errors)
            return
        }
        form = current.copy(saving = true)
        viewModelScope.launch {
            val id = repository.saveStudent(
                StudentDraft(
                    name = current.name,
                    batch = current.batch,
                    guardianPhone = current.phone,
                    monthlyFeePaise = requireNotNull(Money.parseToPaise(current.fee)),
                    dueDay = current.dueDay.trim().toInt(),
                    startMonth = current.startMonth,
                ),
                studentId,
            )
            form = form.copy(saving = false)
            onSaved(id)
        }
    }
}
