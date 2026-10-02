# FeeLedger

[![Android CI](https://github.com/guru-prasath-j/feeledger-android/actions/workflows/android.yml/badge.svg)](https://github.com/guru-prasath-j/feeledger-android/actions/workflows/android.yml)

**UPI fee collection and a private ledger for home tutors in India.** Native Android, Kotlin + Jetpack Compose.

## The problem

India has millions of home tutors, music and dance teachers, and small coaching classes. Almost all of them
collect a fixed fee every month, and almost all of them track it in a notebook or a WhatsApp chat:

- Parents pay on different days, sometimes in parts, sometimes two months late. Arrears get lost.
- Payment gateways charge 2% and need KYC and a business account, which a tutor with 20 students doesn't have.
- A `upi://pay` link sent on WhatsApp **isn't tappable**, so parents end up typing the UPI ID and amount by hand,
  and the tutor can't tell which payment was for which child or month.
- Generic expense apps don't model "₹1,500 due on the 5th of every month, per student".

## What FeeLedger does

- **Student roster with a monthly fee and due day.** Every month from the start month to today is billed, so
  unpaid past months show up as arrears automatically.
- **Status at a glance:** overdue, due today, part paid, upcoming or paid, with this month's collected vs expected.
- **Request via UPI:** generates an NPCI-spec `upi://pay` QR with the exact balance, a month-and-name note and a
  unique reference (`FL<student>M<yyyymm>`), then shares it **as an image plus text** so it works on WhatsApp.
  Parents scan with any UPI app (GPay, PhonePe, Paytm, BHIM); money goes straight to the tutor's bank, at zero cost.
- **Record payments** by UPI (with UTR) or cash, including part payments and advances.
- **Daily 9 AM reminder** (WorkManager) summarising who is due today and who is overdue.
- **CSV export** of every payment for income-tax filing, with spreadsheet-formula injection guarded.

## Security and privacy

FeeLedger holds parents' phone numbers and payment references, so it is built as a no-server, no-account app:

- **No `INTERNET` permission.** Data never leaves the phone.
- **Android Keystore AES-256-GCM field encryption** for parents' numbers, UTRs and the tutor's UPI ID.
  The key is non-exportable, so a copied database or prefs file is unreadable.
- **Biometric / device-credential app lock** (`BiometricPrompt`, `BIOMETRIC_WEAK | DEVICE_CREDENTIAL`).
- **Backups disabled**, so encrypted data is never restored onto a device that lacks the key.
- Shared QR images and CSVs go through a private `FileProvider` cache path, never shared storage.

## Architecture

```
ui (Compose, Navigation, ViewModels + StateFlow)
 └── data/FeeRepository  ── encrypts on write, decrypts on read
      ├── Room (students, payments; FK cascade, indexed by month)
      └── security/FieldCipher (AES-GCM) ← KeystoreKeyProvider
domain (pure Kotlin, no Android): FeeCalculator, UpiUri, FeeRequests, Money, Validators, LedgerCsv
work: DueReminderWorker (daily) → Notifications
```

- Money is `Long` paise end to end; formatting uses Indian digit grouping (₹1,23,456).
- Billing months are stored as `yyyymm` integers for cheap indexing and sorting.
- All fee arithmetic lives in `domain/` and is unit-tested on the JVM, including month-end due dates
  (day 31 in February), part payments, arrears and archived students.

## Build

Requires JDK 17 and the Android SDK (API 35).

```bash
./gradlew testDebugUnitTest   # JVM unit tests
./gradlew assembleDebug       # app/build/outputs/apk/debug/app-debug.apk
```

CI builds the APK and runs the tests on every push; the debug APK is attached to each workflow run.

## Tech

Kotlin 2.0 · Jetpack Compose (Material 3) · Navigation Compose · Room + KSP · WorkManager ·
AndroidX Biometric · Android Keystore · ZXing · JUnit 4 · GitHub Actions

## License

MIT
