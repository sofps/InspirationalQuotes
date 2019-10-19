package com.sofps.inspirationalquotes.asynctask

import android.content.Context
import android.graphics.Bitmap
import android.os.AsyncTask
import com.sofps.inspirationalquotes.util.ScreenshotUtils
import java.io.File
import java.util.UUID

class ScreenshotLoader(
        private val context: Context,
        private val listener: ScreenshotLoaderTaskListener
) : AsyncTask<Bitmap, Void, File>() {

    interface ScreenshotLoaderTaskListener {

        fun onScreenshotLoaderTaskComplete(file: File)

        fun onScreenshotLoaderTaskInProgress()
    }

    override fun doInBackground(vararg bitmaps: Bitmap): File {
        // Get the path to save screenshot
        val saveFile = ScreenshotUtils.getMainDirectoryName(context)
        val filename = "IQ_" + UUID.randomUUID() + ".jpg"
        // Save the screenshot to selected path
        return ScreenshotUtils.store(bitmaps[0], filename, saveFile)
    }

    override fun onPreExecute() {
        listener.onScreenshotLoaderTaskInProgress()
    }

    override fun onPostExecute(result: File) {
        listener.onScreenshotLoaderTaskComplete(result)
    }
}
