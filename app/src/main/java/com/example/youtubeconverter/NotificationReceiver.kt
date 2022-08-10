package com.example.youtubeconverter

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Environment
import android.widget.Toast
import androidx.core.app.RemoteInput
import com.chaquo.python.Python
import com.chaquo.python.android.AndroidPlatform


class NotificationReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        val remoteInput = RemoteInput.getResultsFromIntent(intent)
        initPy(context)

        if (remoteInput != null) {
            val url = remoteInput.getCharSequence(
                NotificationHelper.KEY_TEXT_REPLY
            ).toString()
            Toast.makeText(context, "Starting download", Toast.LENGTH_LONG).show()
            Toast.makeText(context, getStringFromScript(url, context), Toast.LENGTH_LONG).show()
            NotificationHelper.showNotification(context)
        }
    }

    private fun getStringFromScript(url: String, context: Context): String {
        val python = Python.getInstance()
        val pyFileName = python.getModule("script")
        val path = Environment.getExternalStorageDirectory().absolutePath + "/Music"
        return pyFileName.callAttr("converter", url, path).toString()
    }

    private fun initPy (context: Context) {
        if(!Python.isStarted()) {
            Python.start(AndroidPlatform(context))
        }
    }
}