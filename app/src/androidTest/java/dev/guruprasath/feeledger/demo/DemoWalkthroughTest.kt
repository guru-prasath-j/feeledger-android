package dev.guruprasath.feeledger.demo

import android.graphics.Bitmap
import androidx.compose.ui.test.hasScrollAction
import androidx.compose.ui.test.hasText
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onAllNodesWithText
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performScrollTo
import androidx.compose.ui.test.performScrollToNode
import androidx.compose.ui.test.performTextInput
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import androidx.test.uiautomator.By
import androidx.test.uiautomator.UiDevice
import androidx.test.uiautomator.Until
import dev.guruprasath.feeledger.FeeLedgerApp
import dev.guruprasath.feeledger.MainActivity
import dev.guruprasath.feeledger.data.StudentDraft
import dev.guruprasath.feeledger.domain.FeeCalculator
import dev.guruprasath.feeledger.domain.PaymentMethod
import dev.guruprasath.feeledger.work.Notifications
import kotlinx.coroutines.runBlocking
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import java.io.File
import java.time.LocalDate
import java.time.YearMonth

/**
 * Scripted end-to-end walkthrough of the app on a real emulator.
 * CI runs it to capture the screenshots in docs/screenshots; it also fails if any step breaks.
 */
@RunWith(AndroidJUnit4::class)
class DemoWalkthroughTest {

    @get:Rule
    val compose = createAndroidComposeRule<MainActivity>()

    private val instrumentation = InstrumentationRegistry.getInstrumentation()
    private val app: FeeLedgerApp
        get() = instrumentation.targetContext.applicationContext as FeeLedgerApp

    private fun waitForText(text: String, timeoutMs: Long = 15_000) {
        compose.waitUntil(timeoutMs) {
            compose.onAllNodesWithText(text, substring = true).fetchSemanticsNodes().isNotEmpty()
        }
    }

    private fun waitForCount(text: String, count: Int, timeoutMs: Long = 15_000) {
        compose.waitUntil(timeoutMs) {
            compose.onAllNodesWithText(text, useUnmergedTree = true).fetchSemanticsNodes().size == count
        }
    }

    private fun tap(label: String, index: Int = 0) {
        compose.onAllNodesWithText(label, useUnmergedTree = true)[index].performClick()
    }

    private fun shot(name: String) {
        compose.waitForIdle()
        Thread.sleep(1_500)
        val bitmap = instrumentation.uiAutomation.takeScreenshot()
        val dir = File(app.filesDir, "screenshots").apply { mkdirs() }
        File(dir, "$name.png").outputStream().use { bitmap.compress(Bitmap.CompressFormat.PNG, 100, it) }
    }

    @Test
    fun walkthrough() {
        // 1. First launch: the tutor enters the name and UPI ID parents will pay.
        waitForText("Get started")
        compose.onNodeWithText("Your name (shown to parents)").performTextInput("Meena Iyer")
        compose.onNodeWithText("Your UPI ID").performTextInput("meena.tuition@okaxis")
        shot("01-onboarding")
        tap("Get started")

        // 2. Empty roster.
        waitForText("No students yet")
        shot("02-home-empty")

        // 3. Add a student whose billing started two months ago.
        tap("Add student")
        waitForText("Student name")
        compose.onNodeWithText("Student name").performTextInput("Ravi Kumar")
        compose.onNodeWithText("Batch / class (optional)").performTextInput("Class 8 Maths, Mon-Wed 5 PM")
        compose.onNodeWithText("Parent's mobile (optional)").performTextInput("98765 43210")
        compose.onNodeWithText("Monthly fee (₹)").performTextInput("1500")
        compose.onNodeWithContentDescription("Previous month").performClick()
        compose.onNodeWithContentDescription("Previous month").performClick()
        shot("03-add-student")
        compose.onNodeWithText("Save student", useUnmergedTree = true).performScrollTo()
        tap("Save student")

        // 4. The student page bills every month since the start month.
        waitForText("Pending fees")
        waitForCount("Mark paid", 3)
        shot("04-student-detail")

        // 5. Request the oldest month over UPI: QR + shareable message.
        tap("Request via UPI")
        waitForText("Share QR")
        shot("05-upi-request")
        tap("Close")

        // 6. The parent paid: record it with the UTR from their receipt.
        tap("Mark paid")
        waitForText("UTR / transaction ID")
        compose.onNodeWithText("UTR / transaction ID (optional)").performTextInput("427812345678")
        shot("06-record-payment")
        tap("Save")
        waitForCount("Mark paid", 2)
        compose.onNode(hasScrollAction()).performScrollToNode(hasText("Payment history"))
        shot("07-after-payment")
        compose.onNodeWithContentDescription("Back").performClick()

        // 7. A realistic roster: due today, part paid, paid up and two months overdue.
        seedRoster()
        waitForText("Kabir Mehta")
        shot("08-home-roster")

        tap("Overdue")
        shot("09-overdue-filter")
        tap("All")

        // 8. Settings: payment details, app lock, CSV export, privacy note.
        compose.onNodeWithContentDescription("Settings").performClick()
        waitForText("Payment details")
        shot("10-settings")
        compose.onNodeWithContentDescription("Back").performClick()
        waitForText("Kabir Mehta")

        // 9. The daily reminder the WorkManager job posts at 9 AM.
        runBlocking {
            val (students, payments) = app.container.repository.snapshot()
            Notifications.showDueSummary(app, FeeCalculator.summarize(students, payments, LocalDate.now()))
        }
        val device = UiDevice.getInstance(instrumentation)
        device.openNotification()
        device.wait(Until.hasObject(By.textContains("outstanding")), 10_000)
        shot("11-reminder-notification")
        device.pressBack()
    }

    private fun seedRoster() = runBlocking {
        val repo = app.container.repository
        val today = LocalDate.now()
        val month = YearMonth.now()

        repo.saveStudent(
            StudentDraft("Ananya Sharma", "Class 10 Science", "9123456780", 2_000_00, today.dayOfMonth, month),
            null,
        )

        val kabir = repo.saveStudent(StudentDraft("Kabir Mehta", "Class 6 Maths", "", 1_200_00, 5, month.minusMonths(1)), null)
        repo.recordPayment(kabir, month.minusMonths(1), 1_200_00, PaymentMethod.CASH, null)
        repo.recordPayment(kabir, month, 1_200_00, PaymentMethod.UPI, "427899001122")

        val diyaDue = minOf(today.lengthOfMonth(), today.dayOfMonth + 7)
        val diya = repo.saveStudent(StudentDraft("Diya Nair", "Carnatic vocals, Sat 10 AM", "9988776655", 1_800_00, diyaDue, month), null)
        repo.recordPayment(diya, month, 600_00, PaymentMethod.UPI, "427855443322")

        repo.saveStudent(StudentDraft("Arjun Das", "Class 9 Physics", "", 1_500_00, 1, month.minusMonths(1)), null)
    }
}
