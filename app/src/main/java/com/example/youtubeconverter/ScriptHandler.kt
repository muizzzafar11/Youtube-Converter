package com.example.youtubeconverter

import android.content.Context
import android.os.Environment
import android.widget.Toast
import com.chaquo.python.Python
import com.chaquo.python.android.AndroidPlatform

object ScriptHandler {

    fun sendDataToScript(url: String, context: Context) {
        Toast.makeText(context, "Starting download", Toast.LENGTH_LONG).show()
        Toast.makeText(context, getStringFromScript(url, context), Toast.LENGTH_LONG).show()
        NotificationHelper.showNotification(context)
    }

    private fun getStringFromScript(url: String, context: Context): String {
        val python = Python.getInstance()
        val pyFileName = python.getModule("script")
        val path = Environment.getExternalStorageDirectory().absolutePath + "/Music"
        return pyFileName.callAttr("converter", url, path).toString()
    }

    fun initPy (context: Context) {
        if(!Python.isStarted()) {
            Python.start(AndroidPlatform(context))
        }
    }

}