package com.sofps.inspirationalquotes.util;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import java.io.File;
import java.io.FileOutputStream;

public class ScreenshotUtils {

    // Create directory to save screenshot
    public static File getMainDirectoryName(Context context) {
        // The folder will be created in the directory returned by getExternalFilesDir
        // The benefit of using getExternalFilesDir is that the images will be deleted automatically when the app is uninstalled
        File mainDir = new File(context.getExternalFilesDir(Environment.DIRECTORY_PICTURES), "IQ_screenshots");

        //If File is not present create directory
        if (!mainDir.exists()) {
            if (mainDir.mkdir()) {
                Log.e("Create Directory", "Main Directory Created : " + mainDir);
            }
        }
        return mainDir;
    }

    public static Bitmap getScreenshot(View view) {
        View screenView = view.getRootView();
        screenView.setDrawingCacheEnabled(true);
        Bitmap bitmap = Bitmap.createBitmap(screenView.getDrawingCache());
        screenView.setDrawingCacheEnabled(false);
        return bitmap;
    }

    public static File store(Bitmap bm, String fileName, File saveFilePath) {
        File dir = new File(saveFilePath.getAbsolutePath());
        if (!dir.exists()) {
            dir.mkdirs();
        }
        File file = new File(saveFilePath.getAbsolutePath(), fileName);
        try {
            FileOutputStream fOut = new FileOutputStream(file);
            bm.compress(Bitmap.CompressFormat.JPEG, 85, fOut);
            fOut.flush();
            fOut.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return file;
    }
}
