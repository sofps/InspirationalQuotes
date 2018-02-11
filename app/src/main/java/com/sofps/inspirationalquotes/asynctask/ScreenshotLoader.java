package com.sofps.inspirationalquotes.asynctask;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import com.sofps.inspirationalquotes.util.ScreenshotUtils;
import java.io.File;
import java.util.UUID;

public class ScreenshotLoader extends AsyncTask<Bitmap, Void, File> {

    public interface ScreenshotLoaderTaskListener {

        void onScreenshotLoaderTaskComplete(File file);

        void onScreenshotLoaderTaskInProgress();
    }

    private final Context mContext;
    private final ScreenshotLoaderTaskListener mListener;

    public ScreenshotLoader(Context context, ScreenshotLoaderTaskListener listener) {
        mContext = context;
        mListener = listener;
    }

    @Override
    protected File doInBackground(Bitmap... bitmaps) {
        File saveFile = ScreenshotUtils.getMainDirectoryName(mContext); // Get the path to save screenshot
        String filename = "IQ_" + UUID.randomUUID() + ".jpg";
        return ScreenshotUtils.store(bitmaps[0], filename, saveFile); // Save the screenshot to selected path
    }

    @Override
    protected void onPreExecute() {
        mListener.onScreenshotLoaderTaskInProgress();
    }

    @Override
    protected void onPostExecute(File result) {
        mListener.onScreenshotLoaderTaskComplete(result);
    }
}
