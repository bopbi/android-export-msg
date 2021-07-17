package com.bobbyprabowo.exportmsg

import android.app.DownloadManager
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Environment
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.lifecycleScope
import com.bobbyprabowo.exportmsg.databinding.ActivityMainBinding
import java.io.File

class MainActivity : AppCompatActivity() {

    companion object {
        private const val EXPORTED_FILE_NAME = "sms-exported.txt"

        private const val READ_SMS_PERMISSION = "android.permission.READ_SMS"
        private const val READ_SMS_REQUEST_CODE = 123

        private const val WRITE_EXTERNAL_PERMISSION = "android.permission.WRITE_EXTERNAL_STORAGE"
        private const val WRITE_EXTERNAL_REQUEST_CODE = 324
    }

    private val binding: ActivityMainBinding by lazy { ActivityMainBinding.inflate(layoutInflater) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        binding.exportSmsToInternalButton.setOnClickListener {
            if(ContextCompat.checkSelfPermission(baseContext, READ_SMS_PERMISSION) == PackageManager.PERMISSION_GRANTED) {
                exportSmsToInternal()
            } else {
                ActivityCompat.requestPermissions(this, arrayOf(READ_SMS_PERMISSION), READ_SMS_REQUEST_CODE)
            }
        }

        binding.copyExportedSmsToDownloadButton.setOnClickListener {
            if(ContextCompat.checkSelfPermission(baseContext, WRITE_EXTERNAL_PERMISSION) == PackageManager.PERMISSION_GRANTED) {
                copyToDownload()
            } else {
                ActivityCompat.requestPermissions(this, arrayOf(WRITE_EXTERNAL_PERMISSION), READ_SMS_REQUEST_CODE)
            }
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == READ_SMS_REQUEST_CODE && permissions.contains(READ_SMS_PERMISSION)) {
            exportSmsToInternal()
        }

        if (requestCode == WRITE_EXTERNAL_REQUEST_CODE && permissions.contains(WRITE_EXTERNAL_PERMISSION)) {
            copyToDownload()
        }
    }

    private fun exportSmsToInternal() {
        val file = File(filesDir, EXPORTED_FILE_NAME)
        val exportSms = ExportSms(this)
        lifecycleScope.launchWhenResumed {
            exportSms.execute(file)
        }

    }

    private fun copyToDownload() {
        val file = File(filesDir, EXPORTED_FILE_NAME)
        val copyToDownload = CopyToDownload(this)
        copyToDownload.execute(file)
    }
}
