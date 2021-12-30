package ru.debajo.reader.rss.data.dump

import android.content.Context
import android.net.Uri
import androidx.annotation.WorkerThread
import org.joda.time.DateTime

class FileSaver(
    private val context: Context
) {

    fun createFileName(title: String, extension: String = "txt"): String {
        val nowTime = DateTime.now().toString("dd-MM-yyyy-HH-mm-ss")
        return "Sreeeder_${title}_$nowTime.${extension}"
    }

    @WorkerThread
    fun writeFile(uri: Uri, data: String) {
        val stream = context.contentResolver.openOutputStream(uri) ?: throw IllegalStateException()
        stream.bufferedWriter().use { it.write(data) }
    }

    @WorkerThread
    fun readFileRaw(uri: Uri): String? {
        return context.contentResolver.openInputStream(uri)
            ?.bufferedReader()
            ?.use { it.readText() }
    }
}
