package com.nico.trippingsdcardphotomanager.Services;

import android.os.AsyncTask;
import android.util.Log;

import com.nico.trippingsdcardphotomanager.Model.Album;
import com.nico.trippingsdcardphotomanager.Model.Picture;
import com.nico.trippingsdcardphotomanager.PictureMogrifier.PictureMogrifier;
import com.nico.trippingsdcardphotomanager.R;

import java.io.File;

public class PendingOpsApplier extends AsyncTask<Void, Integer, Void> {

    public interface Callback {
        public void onComplete();
        public void onProgressReport(int pct);
        public void addToLog(String msg);
        public String getString(int resource);
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
        Log.i(PendingOpsApplier.class.getName(), "Removing " + pic.getFileName());

        File fp = new File(pic.getFullPath());
        if (fp.delete()) {
            String msg = String.format(cb.getString(R.string.ops_applier_removed_file),
                    pic.getFileName());
            cb.addToLog(msg);
        } else {
            String msg = String.format(cb.getString(R.string.ops_applier_error_removing_file),
                    pic.getFileName());
            cb.addToLog(msg);
        }
    }

    private void doCompress(Picture pic) {
        String msg = String.format(cb.getString(R.string.ops_applier_start_file_compress),
                pic.getFileName(),
                pic.getFileSizeInMb());
        Log.i(PendingOpsApplier.class.getName(), msg);
        cb.addToLog(msg);

        String argv[] = {"-quality", String.valueOf(pic.getCompressionLevel()),
                         pic.getFullPath()};
        int ret = PictureMogrifier.mogrify(argv);
        Log.i(PendingOpsApplier.class.getName(), "\tCompressed " + pic.getFullPath() + " = " + ret);

        String msg2;
        if (ret == 0) {
            msg2 = String.format(cb.getString(R.string.ops_applier_done_file_compress),
                    pic.getFileName(),
                    pic.getFileSizeInMb());
        } else {
            msg2 = String.format(cb.getString(R.string.ops_applier_error_compressing_file),
                    pic.getFileName());
        }
        Log.i(PendingOpsApplier.class.getName(), msg2);
        cb.addToLog(msg2);
    }

}
