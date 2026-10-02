package dev.guruprasath.feeledger.ui

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import dev.guruprasath.feeledger.AppContainer
import dev.guruprasath.feeledger.ui.home.HomeScreen
import dev.guruprasath.feeledger.ui.onboarding.OnboardingScreen
import dev.guruprasath.feeledger.ui.settings.SettingsScreen
import dev.guruprasath.feeledger.ui.student.StudentDetailScreen
import dev.guruprasath.feeledger.ui.student.StudentEditScreen

private object Routes {
    const val ONBOARDING = "onboarding"
    const val HOME = "home"
    const val ADD_STUDENT = "add-student"
    const val STUDENT = "student/{id}"
    const val EDIT_STUDENT = "edit-student/{id}"
    const val SETTINGS = "settings"

    fun student(id: Long) = "student/$id"
    fun editStudent(id: Long) = "edit-student/$id"
}

@Composable
fun FeeLedgerNavHost(container: AppContainer) {
    val nav = rememberNavController()
    val start = remember { if (container.profileStore.profile.value == null) Routes.ONBOARDING else Routes.HOME }
    val idArg = listOf(navArgument("id") { type = NavType.LongType })

    NavHost(navController = nav, startDestination = start) {
        composable(Routes.ONBOARDING) {
            OnboardingScreen(onDone = {
                nav.navigate(Routes.HOME) { popUpTo(Routes.ONBOARDING) { inclusive = true } }
            })
        }
        composable(Routes.HOME) {
            HomeScreen(
                onAddStudent = { nav.navigate(Routes.ADD_STUDENT) },
                onOpenStudent = { nav.navigate(Routes.student(it)) },
                onSettings = { nav.navigate(Routes.SETTINGS) },
            )
        }
        composable(Routes.ADD_STUDENT) {
            StudentEditScreen(
                studentId = null,
                onBack = { nav.popBackStack() },
                onSaved = { id -> nav.navigate(Routes.student(id)) { popUpTo(Routes.HOME) } },
            )
        }
        composable(Routes.STUDENT, arguments = idArg) { entry ->
            val id = entry.arguments?.getLong("id") ?: return@composable
            StudentDetailScreen(
                studentId = id,
                onBack = { nav.popBackStack() },
                onEdit = { nav.navigate(Routes.editStudent(id)) },
            )
        }
        composable(Routes.EDIT_STUDENT, arguments = idArg) { entry ->
            val id = entry.arguments?.getLong("id") ?: return@composable
            StudentEditScreen(studentId = id, onBack = { nav.popBackStack() }, onSaved = { nav.popBackStack() })
        }
        composable(Routes.SETTINGS) {
            SettingsScreen(onBack = { nav.popBackStack() })
        }
    }
}
