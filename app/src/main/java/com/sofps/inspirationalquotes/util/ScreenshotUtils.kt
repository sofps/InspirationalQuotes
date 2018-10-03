package com.sofps.inspirationalquotes.util

import android.content.Context
import android.graphics.Bitmap
import android.os.Environment
import android.util.Log
import android.view.View
import java.io.File
import java.io.FileOutputStream

object ScreenshotUtils {

    /**
     * Create directory to save screenshot
     */
    @JvmStatic
    fun getMainDirectoryName(context: Context): File {
        // The folder will be created in the directory returned by getExternalFilesDir
        // The benefit of using getExternalFilesDir is that the images will be deleted automatically when the app is uninstalled
        return File(context.getExternalFilesDir(Environment.DIRECTORY_PICTURES), "IQ_screenshots").apply {
            // If File is not present create directory
            if (!exists()) {
                if (mkdir()) {
                    Log.e("Create Directory", "Main Directory Created : $this")
                }
            }
        }
    }

    @JvmStatic
    fun getScreenshot(view: View): Bitmap? {
        view.isDrawingCacheEnabled = true
        val bitmap = Bitmap.createBitmap(view.drawingCache)
        view.isDrawingCacheEnabled = false
        return bitmap
    }

    @JvmStatic
    fun store(bitmap: Bitmap, fileName: String, saveFilePath: File): File {
        File(saveFilePath.absolutePath).apply {
            if (!exists()) {
                mkdirs()
            }
        }
        return File(saveFilePath.absolutePath, fileName).apply {
            try {
                FileOutputStream(this).apply {
                    bitmap.compress(Bitmap.CompressFormat.JPEG, 85, this)
                    flush()
                    close()
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }
}
