package com.church.injilkeselamatan.audiorenungan.feature_music.domain.use_case

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.widget.Toast

class CopyToClipboard(private val context: Context) {
    private val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager

    operator fun invoke() {
        val clip = ClipData.newPlainText("No Rekening", "2520929994")
        clipboard.setPrimaryClip(clip)
        Toast.makeText(context, "Nomor rekening berhasil disalin", Toast.LENGTH_SHORT).show()
    }
}