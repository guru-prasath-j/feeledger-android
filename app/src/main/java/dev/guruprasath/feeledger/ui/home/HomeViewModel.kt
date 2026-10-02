package dev.guruprasath.feeledger.ui.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dev.guruprasath.feeledger.data.FeeRepository
import dev.guruprasath.feeledger.domain.FeeCalculator
import dev.guruprasath.feeledger.domain.FeeStatus
import dev.guruprasath.feeledger.domain.LedgerSummary
import dev.guruprasath.feeledger.domain.Student
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import java.time.Clock
import java.time.LocalDate
import java.time.YearMonth

enum class HomeFilter(val label: String) {
    ALL("All"),
    PENDING("Pending"),
    OVERDUE("Overdue"),
    PAID("Paid up"),
    ARCHIVED("Archived"),
}

data class StudentRow(
    val student: Student,
    val status: FeeStatus,
    val balancePaise: Long,
    val pendingMonths: Int,
)

data class HomeUiState(
    val month: YearMonth,
    val summary: LedgerSummary,
    val rows: List<StudentRow>,
    val filter: HomeFilter,
    val totalStudents: Int,
    val loading: Boolean,
)

class HomeViewModel(repository: FeeRepository, private val clock: Clock) : ViewModel() {

    private val filter = MutableStateFlow(HomeFilter.ALL)

    val state: StateFlow<HomeUiState> =
        combine(repository.students, repository.payments, filter) { students, payments, selected ->
            val today = LocalDate.now(clock)
            val rows = students.map { student ->
                val open = FeeCalculator.outstanding(student, payments, today)
                StudentRow(
                    student = student,
                    status = FeeCalculator.rowStatus(student, payments, today),
                    balancePaise = open.sumOf { it.balancePaise },
                    pendingMonths = open.size,
                )
            }
            val visible = rows.filter { row ->
                when (selected) {
                    HomeFilter.ARCHIVED -> !row.student.active
                    HomeFilter.ALL -> row.student.active
                    HomeFilter.PENDING -> row.student.active && row.balancePaise > 0
                    HomeFilter.OVERDUE -> row.student.active && row.status == FeeStatus.OVERDUE
                    HomeFilter.PAID -> row.student.active && row.balancePaise == 0L
                }
            }.sortedWith(compareBy<StudentRow>({ it.status.priority }, { it.student.name.lowercase() }))

            HomeUiState(
                month = YearMonth.from(today),
                summary = FeeCalculator.summarize(students, payments, today),
                rows = visible,
                filter = selected,
                totalStudents = students.count { it.active },
                loading = false,
            )
        }.stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(5_000),
            HomeUiState(YearMonth.now(clock), LedgerSummary.EMPTY, emptyList(), HomeFilter.ALL, 0, loading = true),
        )

    fun setFilter(value: HomeFilter) {
        filter.value = value
    }
}
