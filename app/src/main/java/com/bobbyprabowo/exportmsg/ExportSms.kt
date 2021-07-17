package com.bobbyprabowo.exportmsg

import android.content.Context
import android.net.Uri
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okio.buffer
import okio.sink
import java.io.File
import java.io.IOException

class ExportSms(private val context: Context) {

    companion object {
        const val COLUMN_BODY = "body"
    }

    @Throws(IOException::class)
    suspend fun execute(file: File) {
        withContext(Dispatchers.Default) {
            file.sink().buffer().use { sink ->
                val cursor = context.contentResolver.query(Uri.parse("content://sms/inbox"), null, null, null, null)
                cursor?.moveToFirst()
                do {
                    cursor?.getString(cursor.getColumnIndexOrThrow(COLUMN_BODY))?.let { messageBody ->
                        sink.writeUtf8(messageBody)
                        sink.writeUtf8("\n")
                        sink.writeUtf8("\n")
                        sink.writeUtf8(">>>>>>>>>>>>>>>>>>>>>>")
                        sink.writeUtf8("\n")
                    }
                } while (cursor?.moveToNext() == true)
                cursor?.close()
                sink.close()
            }

        }

    }
}
