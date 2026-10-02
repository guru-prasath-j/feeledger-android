package dev.guruprasath.feeledger.ui.util

import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import androidx.core.content.FileProvider
import java.io.File

/** Shares files through the app's FileProvider; nothing is written to shared storage. */
object Share {
    private fun sharedDir(context: Context) = File(context.cacheDir, "shared").apply { mkdirs() }

    private fun uriFor(context: Context, file: File) =
        FileProvider.getUriForFile(context, "${context.packageName}.files", file)

    fun feeRequest(context: Context, qr: Bitmap, message: String) {
        val file = File(sharedDir(context), "fee-request.png")
        file.outputStream().use { qr.compress(Bitmap.CompressFormat.PNG, 100, it) }
        val intent = Intent(Intent.ACTION_SEND).apply {
            type = "image/png"
            putExtra(Intent.EXTRA_STREAM, uriFor(context, file))
            putExtra(Intent.EXTRA_TEXT, message)
            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        }
        context.startActivity(Intent.createChooser(intent, "Send fee request"))
    }

    fun csv(context: Context, csv: String, fileName: String) {
        val file = File(sharedDir(context), fileName)
        file.writeText(csv)
        val intent = Intent(Intent.ACTION_SEND).apply {
            type = "text/csv"
            putExtra(Intent.EXTRA_STREAM, uriFor(context, file))
            putExtra(Intent.EXTRA_SUBJECT, fileName)
            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        }
        context.startActivity(Intent.createChooser(intent, "Export fee ledger"))
    }
}
