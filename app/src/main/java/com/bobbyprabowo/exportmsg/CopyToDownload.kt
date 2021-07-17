package com.bobbyprabowo.exportmsg

import android.annotation.SuppressLint
import android.content.Context
import android.os.Build
import android.os.ParcelFileDescriptor
import android.provider.MediaStore
import okio.buffer
import okio.sink
import okio.source
import java.io.File
import android.content.ContentValues
import android.os.Environment.DIRECTORY_DOWNLOADS


class CopyToDownload(private val context: Context) {

    @SuppressLint("NewApi")
    fun execute(file: File) {
        when (Build.VERSION.SDK_INT) {
            Build.VERSION_CODES.R, Build.VERSION_CODES.Q -> {
                val destinationUri = MediaStore.Downloads.getContentUri(MediaStore.VOLUME_EXTERNAL_PRIMARY)

                val fileContent = ContentValues()
                fileContent.put(MediaStore.Downloads.DISPLAY_NAME, file.name)
                fileContent.put(MediaStore.Downloads.RELATIVE_PATH, "${DIRECTORY_DOWNLOADS}/export-msg")
                fileContent.put(MediaStore.Downloads.SIZE, file.length())
                fileContent.put(MediaStore.Downloads.IS_PENDING, 1)

                val exportedUri = context.contentResolver.insert(destinationUri, fileContent)
                exportedUri?.let { insertedContentUri ->
                    context.contentResolver.openFileDescriptor(insertedContentUri, "rw").use { parcelFileDescriptor ->
                        ParcelFileDescriptor.AutoCloseOutputStream(parcelFileDescriptor).write(file.readBytes())
                    }
                    fileContent.clear()
                    fileContent.put(MediaStore.Downloads.IS_PENDING, 0)
                    context.contentResolver.update(insertedContentUri, fileContent, null, null)
                }
            }
            else -> {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                    downloadForLowerThanQ(file)
                }
            }
        }
    }

    private fun downloadForLowerThanQ(fileToCopy: File) {
        val fileToSave = File(context.getExternalFilesDir(null), fileToCopy.name)
        fileToSave.delete()
        if (fileToSave.createNewFile()) {
            fileToCopy.source().use { origin ->
                fileToSave.sink().buffer().use { dest -> dest.writeAll(origin) }
            }
        }
    }
}
