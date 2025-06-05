package com.eynnzerr.bandoristation

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.text.AnnotatedString
import com.eynnzerr.bandoristation.ui.crash.CrashReportPage
import com.eynnzerr.bandoristation.ui.theme.BandoriTheme

class CrashActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        val message = intent.getStringExtra("msg") ?: "unknown error!"
        setContent {
            val clipboardManager = LocalClipboardManager.current

            BandoriTheme {
                CrashReportPage(
                    message = message,
                    onConfirm = {
                        clipboardManager.setText(AnnotatedString(message))
                        this.finishAffinity()
                    }
                )
            }
        }
    }
}