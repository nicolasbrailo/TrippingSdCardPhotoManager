package com.nico.trippingsdcardphotomanager.Services;

import android.os.AsyncTask;
import android.os.SystemClock;
import android.util.Log;
import android.view.View;

import com.nico.trippingsdcardphotomanager.Model.Album;
import com.nico.trippingsdcardphotomanager.Model.Picture;
import com.nico.trippingsdcardphotomanager.PendingOpsApplierActivity;
import com.nico.trippingsdcardphotomanager.PhotoView;
import com.nico.trippingsdcardphotomanager.PictureMogrifier.PictureMogrifier;

import java.io.File;

public class PendingOpsApplier extends AsyncTask<Void, Integer, Void> {

    public interface Callback {
        public void onComplete();
        public void onProgressReport(int pct);
    }

    private final Callback cb;
    private final Album album;

    public PendingOpsApplier(final Callback cb, final Album album) {
        this.cb = cb;
        this.album = album;
    }

    @Override
    protected Void doInBackground(Void... dummy) {
        Log.i(PendingOpsApplier.class.getName(), "Processing pending operations.");

        long processedCount = 0;
        for (Picture pic : album) {
            if (isCancelled()) break;
            publishProgress((int) ((100.0*processedCount)/album.getSize()));

            if (pic.isMarkedForDeletion()) {
                doDelete(pic);
            } else if (pic.isMarkedForCompression()) {
                doCompress(pic);
            }

            ++processedCount;
        }

        return null;
    }

    @Override
    protected void onProgressUpdate(Integer... values) {
        cb.onProgressReport(values[0]);
    }

    @Override
    protected void onPostExecute(Void v) {
        cb.onComplete();
    }

    private void doDelete(Picture pic) {
        Log.i(PendingOpsApplier.class.getName(), "Removing " + pic.getFullPath());
        // TODO: Add a log to the screen with this op

        File fp = new File(pic.getFullPath());
        fp.delete();
    }

    private void doCompress(Picture pic) {
        Log.i(PendingOpsApplier.class.getName(), "Compressing " + pic.getFullPath() +
                " to " + pic.getCompressionLevel());
        // TODO: Add a log to the screen with this op

        String argv[] = {"-quality", String.valueOf(pic.getCompressionLevel()),
                         pic.getFullPath()};
        int ret = PictureMogrifier.mogrify(argv);
        Log.i(PendingOpsApplier.class.getName(), "\tCompressed " + pic.getFullPath() + " = " + ret);
    }

}
