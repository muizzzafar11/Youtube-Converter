package com.example.youtubeconverter

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import androidx.core.app.RemoteInput
import com.example.youtubeconverter.ScriptHandler.initPy
import com.example.youtubeconverter.ScriptHandler.sendDataToScript


class LinkNotificationReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        val remoteInput = RemoteInput.getResultsFromIntent(intent)
        initPy(context)

        if (remoteInput != null) {
            val url = remoteInput.getCharSequence(
                NotificationHelper.KEY_TEXT_REPLY
            ).toString()
            sendDataToScript(url, context)
        }
    }
}